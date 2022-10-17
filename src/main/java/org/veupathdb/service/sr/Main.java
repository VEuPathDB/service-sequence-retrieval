package org.veupathdb.service.sr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.compute.platform.config.*;
import org.veupathdb.lib.container.jaxrs.config.Options;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import org.veupathdb.lib.container.jaxrs.server.Server;
import org.veupathdb.service.sr.async.MyExecutorFactory;

public class Main extends Server {

  private static final Logger LOG = LogManager.getLogger(Main.class);

  private final AsyncOptions options = new AsyncOptions();

  public static void main(String[] args) {
    var server = new Main();


    server.start(args);
  }

  @Override
  protected ContainerResources newResourceConfig(Options options) {
    final var out =  new Resources(options);

    // Enabled by default for debugging purposes, this should be removed when
    // production ready.
    out.property("jersey.config.server.tracing.type", "ALL")
       .property("jersey.config.server.tracing.threshold", "VERBOSE");

    return out;
  }

  @Override
  protected Options newOptions() {
    return options;
  }

  @Override
  protected void postCliParse(Options opts) {
    initializeAsyncPlatform();
  }

  private void initializeAsyncPlatform() {
    AsyncPlatform.init(AsyncPlatformConfig.builder()
      .dbConfig(AsyncDBConfig.builder()
        .dbName(options.getQueueDBName())
        .host(options.getQueueDBHost())
        .port(options.getQueueDBPort())
        .username(options.getQueueDBUsername())
        .password(options.getQueueDBPassword())
        .poolSize(options.getQueueDBPoolSize())
        .build())
      .addQueue(AsyncQueueConfig.builder()
        .id("sequence-retrieval-queue")
        .username(options.getJobQueueUsername())
        .password(options.getJobQueuePassword())
        .host(options.getJobQueueHost())
        .port(options.getJobQueuePort())
        .workers(options.getJobQueueWorkers())
        .build())
      .s3Config(AsyncS3Config.builder()
        .host(options.getS3Host())
        .bucket(options.getS3Bucket())
        .https(options.getS3UseHttps())
        .accessToken(options.getS3AccessToken())
        .secretKey(options.getS3SecretKey())
        .port(options.getS3Port())
        .build())
      .jobConfig(AsyncJobConfig.builder()
        .executorFactory(new MyExecutorFactory())
        .expirationDays(options.getJobCacheTimeoutDays())
        .build())
      .build());
  }
}
