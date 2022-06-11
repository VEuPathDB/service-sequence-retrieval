package org.veupathdb.service.demo.async;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.JobContext;
import org.veupathdb.lib.compute.platform.job.JobExecutor;
import org.veupathdb.lib.compute.platform.job.JobResult;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.demo.generated.model.ReverseRequest;

public class StringReverseJob implements JobExecutor {
  @NotNull
  @Override
  public JobResult execute(@NotNull JobContext jobContext) {
    var config = Json.parse(jobContext.getConfig(), ReverseRequest.class);
    var input  = config.getInput();

    var out = new char[input.length()];

    for (int i = 0; i < out.length; i++) {
      out[i] = input.charAt(out.length - i - 1);
    }

    jobContext.getWorkspace().write("output", new String(out));

    return JobResult.success("output");
  }
}
