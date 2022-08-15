package org.veupathdb.service.sr.async;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.JobExecutor;
import org.veupathdb.lib.compute.platform.job.JobExecutorContext;
import org.veupathdb.lib.compute.platform.job.JobExecutorFactory;

public class MyExecutorFactory implements JobExecutorFactory {

  @NotNull
  @Override
  public JobExecutor newJobExecutor(@NotNull JobExecutorContext jobExecutorContext) {

    switch(jobExecutorContext.getQueue()) {
      case "string-reverse-queue":
        return new StringReverseJob();
      case "sequence-retrieval-queue":
        return new WriteFeaturesJob();
      default:
        throw new RuntimeException("Unknown queue, context: "+ jobExecutorContext.toString()); 
    }
  }
}
