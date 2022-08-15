package org.veupathdb.service.sr.async;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.JobContext;
import org.veupathdb.lib.compute.platform.job.JobExecutor;
import org.veupathdb.lib.compute.platform.job.JobResult;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.sr.util.Sequences;
import org.veupathdb.service.sr.util.StreamSequences;
import org.veupathdb.service.sr.generated.model.SequenceRetrievalSpec;
import org.veupathdb.service.sr.generated.model.SequenceRetrievalSpecImpl;

import java.io.OutputStream;
import java.io.IOException;
import java.lang.StringBuilder;

public class WriteFeaturesJob implements JobExecutor {
  @NotNull
  @Override
  public JobResult execute(@NotNull JobContext jobContext) {
    var sequenceSpec = Json.parse(jobContext.getConfig(), SequenceRetrievalSpecImpl.class);

    var sequences = Sequences.getSequenceFile(sequenceSpec.getSequenceType());

    var stream = StreamSequences.responseStream(sequences, sequenceSpec.getFeatures(), sequenceSpec.getDeflineFormat(), sequenceSpec.getForceStrandedness(), sequenceSpec.getBasesPerLine());

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
