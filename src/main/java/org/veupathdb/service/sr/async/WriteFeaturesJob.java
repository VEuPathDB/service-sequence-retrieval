package org.veupathdb.service.sr.async;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.JobContext;
import org.veupathdb.lib.compute.platform.job.JobExecutor;
import org.veupathdb.lib.compute.platform.job.JobResult;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.sr.util.Sequences;
import org.veupathdb.service.sr.util.Validate;
import org.veupathdb.service.sr.util.StreamSequences;
import org.veupathdb.service.sr.util.FeatureAdapter;
import org.veupathdb.service.sr.generated.model.SequenceRetrievalSpec;
import org.veupathdb.service.sr.generated.model.SequenceRetrievalSpecImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
import java.net.URL;

import htsjdk.tribble.bed.BEDFeature;

public class WriteFeaturesJob implements JobExecutor {
  @NotNull
  @Override
  public JobResult execute(@NotNull JobContext jobContext) {
    var jobSpec = Json.parse(jobContext.getConfig(), SequenceRetrievalSpecImpl.class);

    var forceStrandedness = jobSpec.getForceStrandedness();
    var sequenceType = jobSpec.getSequenceType();
    var fileFormat = jobSpec.getFileFormat();

    var spec = Sequences.getSequenceSpec(sequenceType);
    var index = Sequences.getSequenceIndex(sequenceType);
    var sequences = Sequences.getSequenceFile(sequenceType);

    List<BEDFeature> requestedFeatures;

    if(jobSpec.getFeatures()!= null){
      requestedFeatures = FeatureAdapter.toBEDFeatures(jobSpec.getFeatures());
    } else {
      try (InputStream fileStream = switch (jobSpec.getUploadMethod()) {
        case FILE -> new ByteArrayInputStream(jobSpec.getFeaturesStr().getBytes(StandardCharsets.UTF_8));
        case URL -> new URL(jobSpec.getFeaturesUrl()).openStream();
      }) {
      requestedFeatures = switch (fileFormat) {
        case BED -> FeatureAdapter.readBed(fileStream, jobSpec.getStartOffset());
        case GFF3 -> FeatureAdapter.readGff3AndConvertToBed(fileStream);
      };
      } catch (IOException e){
        throw new RuntimeException("Unable to complete file processing", e);
      }
    }

    var features = Validate.getValidatedFeatures(sequenceType, index, requestedFeatures, forceStrandedness, spec);

    var stream = StreamSequences.responseStream(sequences, features, jobSpec.getDeflineFormat(), jobSpec.getForceStrandedness(), jobSpec.getBasesPerLine());

    // https://stackoverflow.com/questions/216894/get-an-outputstream-into-a-string
    var os = new OutputStream() {
        private StringBuilder string = new StringBuilder();

        @Override
        public void write(int b) throws IOException {
            this.string.append((char) b );
        }

        public String toString() {
            return this.string.toString();
        }
    };
    stream.accept(os);
    jobContext.getWorkspace().write("output", os.toString());

    return JobResult.success("output");
  }
}
