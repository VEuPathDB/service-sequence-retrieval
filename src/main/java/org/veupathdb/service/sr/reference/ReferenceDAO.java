package org.veupathdb.service.sr.reference;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.tribble.bed.BEDFeature;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.response.StreamSequences;
import java.util.function.Consumer;
import java.io.OutputStream;
import java.nio.file.Path;
import org.veupathdb.service.sr.reference.ReferenceSequenceSpec;

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

  private final ReferenceSequenceSpec spec;
  private final Path indexPath;
  private final Path sequencesPath;

  public ReferenceDAO(ReferenceSequenceSpec spec, Path indexPath, Path sequencesPath){
    this.spec = spec;
    this.indexPath = indexPath;
    this.sequencesPath = sequencesPath;
  }

  public Consumer<OutputStream> validateAndPrepareResponse(
      List<BEDFeature> features,
      DeflineFormat deflineFormat,
      int requestedBasesPerLine){
    this.spec.validateFeatures(features);
    return outputStream -> {
      var index = new FastaSequenceIndexSqlite(this.indexPath);
      var sequenceFile = new IndexedFastaSequenceFile(this.sequencesPath, index);
      StreamSequences.write(outputStream, sequenceFile, features, deflineFormat, requestedBasesPerLine);
    };
  }

  public void validateRequestedFeatures(List<BEDFeature> features){
    this.spec.validateFeatures(features);
  }

}
