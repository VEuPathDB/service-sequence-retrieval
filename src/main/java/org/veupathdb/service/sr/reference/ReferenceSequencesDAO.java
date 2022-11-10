package org.veupathdb.service.sr.reference;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.tribble.bed.BEDFeature;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.util.StreamSequences;
import java.util.function.Consumer;
import java.io.OutputStream;
import java.nio.file.Path;
import org.veupathdb.service.sr.reference.ReferenceSequenceSpec;

/*
 * Provides data access to sequences 
 *
 * To validate and serve the request:
 * ReferenceSequencesDAOFactory.get(sequenceType).validateAndPrepareResponse(...)
 *
 * To pre-validate, cheaply:
 * ReferenceSequencesDAOFactory.get(sequenceType).validate(...)
 */
public class ReferenceSequencesDAO {

  private final ReferenceSequenceSpec spec;
  private final IndexedFastaSequenceFile sequenceFile;

  public ReferenceSequencesDAO(ReferenceSequenceSpec spec, IndexedFastaSequenceFile sequenceFile){
    this.spec = spec;
    this.sequenceFile = sequenceFile;
  }
  public ReferenceSequencesDAO(ReferenceSequenceSpec spec, Path indexPath, Path sequencesPath){
    this(spec, new IndexedFastaSequenceFile(sequencesPath, new FastaSequenceIndexSqlite(indexPath)));
  }

  public Consumer<OutputStream> validateAndPrepareResponse(
      List<BEDFeature> features,
      DeflineFormat deflineFormat,
      int requestedBasesPerLine){
    this.spec.validateFeatures(features);
    return StreamSequences.responseStream(this.sequenceFile, features, deflineFormat, requestedBasesPerLine);
  }

  public void validateRequestedFeatures(List<BEDFeature> features){
    this.spec.validateFeatures(features);
  }

}
