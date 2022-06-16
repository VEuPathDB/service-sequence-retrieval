package org.veupathdb.service.demo.async;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.JobExecutor;
import org.veupathdb.lib.compute.platform.job.JobExecutorContext;
import org.veupathdb.lib.compute.platform.job.JobExecutorFactory;

public class MyExecutorFactory implements JobExecutorFactory {
  @NotNull
  @Override
  public JobExecutor newJobExecutor(@NotNull JobExecutorContext jobExecutorContext) {
    return new StringReverseJob();
  }
}
