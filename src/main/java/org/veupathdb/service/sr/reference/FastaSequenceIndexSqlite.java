package org.veupathdb.service.sr.reference;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.lang.UnsupportedOperationException;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.FastaSequenceIndexEntry;

public class FastaSequenceIndexSqlite extends FastaSequenceIndex {

  Connection connection;
  PreparedStatement statement;
  public FastaSequenceIndexSqlite(Path indexSqlitePath){
    super();
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:" + indexSqlitePath.toString());
      /*
       * expecting a file with a table called 'faidx' with SQL columns mirroring text columns in .fai
       * spec at http://www.htslib.org/doc/faidx.html
       */
      statement = connection.prepareStatement("select length, offset, linebases, linewidth from faidx where name = ? limit 1");
    } catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  @Override
  public FastaSequenceIndexEntry getIndexEntry( String contigName ) {
    try {
      statement.setString(1, contigName);
      ResultSet rs = statement.executeQuery();
      if(rs.next()){
        // see htsjdk.samtools.reference.FastaSequenceIndex.parseIndexFile
        var size = rs.getLong("length");
        var location = rs.getLong("offset");
        return new FastaSequenceIndexEntry(
            contigName, location, size,
            rs.getInt("linebases"), rs.getInt("linewidth"), -1);
      } else {
        throw new RuntimeException("Contig not found in the index: " + contigName);
      }
    } catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  /*
   * Gets called by AbstractIndexedFastaSequenceFile on startup
   */
  @Override
  public Iterator<FastaSequenceIndexEntry> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasIndexEntry( String contigName ) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void write(final Path indexFile) throws IOException {
    throw new UnsupportedOperationException();
  }
}

