package org.veupathdb.service.sr.reference;

import java.util.List;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.tribble.bed.BEDFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.response.StreamSequences;

import java.util.function.Consumer;
import java.util.function.Function;
import java.io.OutputStream;
import java.nio.file.Path;

import org.sqlite.SQLiteDataSource;
import org.sqlite.SQLiteConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import htsjdk.samtools.reference.FastaSequenceIndexEntry;

import java.util.Properties;
import java.io.IOException;

/*
 * Provides data access to sequences
 *
 * To validate and serve the request:
 * ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(...)
 *
 * To pre-validate, cheaply:
 * ReferenceDAOFactory.get(sequenceType).validate(...)
 */
public class ReferenceDAO {
  private static final Logger LOG = LogManager.getLogger(ReferenceDAO.class);

  private final ReferenceSequenceSpec spec;
  private final SQLiteDataSource indexDataSource;
  private final Path sequencesPath;

  public ReferenceDAO(ReferenceSequenceSpec spec, Path indexPath, Path sequencesPath) {
    this.spec = spec;
    this.sequencesPath = sequencesPath;
    this.indexDataSource = readIndexDataSource(indexPath);
  }

  private SQLiteDataSource readIndexDataSource(Path indexPath) {
    LOG.info("Indexing data source at indexPath: " + indexPath);
    var properties = new Properties();
    // only allow reading
    properties.setProperty("mode", "ro");
    // additionally, expect a read-only filesystem where journalling won't work
    properties.setProperty("journal_mode", "OFF");
    var result = new SQLiteDataSource(new SQLiteConfig(properties));
    result.setUrl("jdbc:sqlite:" + indexPath.toString());
    return result;
  }

  public Consumer<OutputStream> validateAndPrepareResponse(
      List<BEDFeature> features,
      DeflineFormat deflineFormat,
      int requestedBasesPerLine) {
    this.spec.validateFeatures(features);
    return outputStream -> {
      try (
          var connection = this.indexDataSource.getConnection();
          var statement = connection.prepareStatement("select length, offset, linebases, linewidth from faidx where name = ? limit 1");
          var sequenceFile = new IndexedFastaSequenceFile(this.sequencesPath, transientIndex(statement))
      ) {
        StreamSequences.write(outputStream, sequenceFile, features, deflineFormat, requestedBasesPerLine);
      } catch (SQLException | IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  private FastaSequenceIndexStub transientIndex(PreparedStatement statement) {
    Function<String, FastaSequenceIndexEntry> f = contigName -> {
      try {
        statement.setString(1, contigName);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          // see htsjdk.samtools.reference.FastaSequenceIndex.parseIndexFile
          var size = rs.getLong("length");
          var location = rs.getLong("offset");
          return new FastaSequenceIndexEntry(
              contigName, location, size,
              rs.getInt("linebases"), rs.getInt("linewidth"), -1);
        } else {
          throw new RuntimeException("Contig not found in the index: " + contigName);
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    };
    return new FastaSequenceIndexStub(f);
  }

  public void validateRequestedFeatures(List<BEDFeature> features) {
    this.spec.validateFeatures(features);
  }

}
