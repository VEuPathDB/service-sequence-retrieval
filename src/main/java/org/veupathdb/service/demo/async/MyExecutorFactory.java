package org.veupathdb.service.demo.async;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.veupathdb.lib.compute.platform.JobExecutor;
import org.veupathdb.lib.hash_id.HashID;

public class MyExecutorFactory implements org.veupathdb.lib.compute.platform.JobExecutorFactory {
  @NotNull
  @Override
  public JobExecutor newJobExecutor(@NotNull HashID hashID, @Nullable JsonNode jsonNode) {
    return new StringReverseJob();
  }
}
