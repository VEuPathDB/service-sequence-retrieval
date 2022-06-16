package org.veupathdb.service.demo;

import org.veupathdb.lib.container.jaxrs.config.Options;
import picocli.CommandLine.Option;

/**
 * Customized options example.
 *
 * Configures details for the queues, s3, and postgres database.
 */
public class MyOptions extends Options {

  // region Postgres

  /*┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓*\
    ┃  Queue PostgreSQL                                                    ┃
    ┃                                                                      ┃
    ┃  Connection details used by the async platform library to connect    ┃
    ┃  to the PostgreSQL instance that it will manage and maintain.        ┃
  \*┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛*/

  @Option(
    names = "--queue-db-name",
    defaultValue = "${env:QUEUE_DB_NAME}",
    description = "Queue database name",
    arity = "1",
    required = true)
  private String queueDBName;

  @Option(
    names = "--queue-db-host",
    defaultValue = "${env:QUEUE_DB_HOST}",
    description = "Queue database hostname",
    arity = "1",
    required = true)
  private String queueDBHost;

  @Option(
    names = "--queue-db-port",
    defaultValue = "${env:QUEUE_DB_PORT}",
    description = "Queue database host port",
    arity = "1")
  private Integer queueDBPort;
  private static final int DEFAULT_QUEUE_DB_PORT = 5432;

  @Option(
    names="--queue-db-username",
    defaultValue = "${env:QUEUE_DB_USERNAME}",
    description = "Queue database username",
    arity = "1",
    required = true)
  private String queueDBUsername;

  @Option(
    names = "--queue-db-password",
    defaultValue = "${env:QUEUE_DB_PASSWORD}",
    description = "Queue database password",
    arity = "1",
    required = true)
  private String queueDBPassword;

  @Option(
    names = "--queue-db-pool-size",
    defaultValue = "${env:QUEUE_DB_POOL_SIZE}",
    description = "Queue database pool size",
    arity = "1")
  private Integer queueDBPoolSize;
  private static final int DEFAULT_QUEUE_DB_POOL_SIZE = 10;

  public String getQueueDBName() {
    return queueDBName;
  }

  public String getQueueDBHost() {
    return queueDBHost;
  }

  public int getQueueDBPort() {
    return queueDBPort == null ? DEFAULT_QUEUE_DB_PORT : queueDBPort;
  }

  public String getQueueDBUsername() {
    return queueDBUsername;
  }

  public String getQueueDBPassword() {
    return queueDBPassword;
  }

  public int getQueueDBPoolSize() {
    return queueDBPoolSize == null ? DEFAULT_QUEUE_DB_POOL_SIZE : queueDBPoolSize;
  }

  // endregion Postgres

  // region RabbitMQ

  /*┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓*\
    ┃  Queue RabbitMQ                                                      ┃
    ┃                                                                      ┃
    ┃  Connection details used by the async platform library to connect    ┃
    ┃  to a single RabbitMQ instance.                                      ┃
    ┃                                                                      ┃
    ┃  The async platform library supports multiple job queues meaning     ┃
    ┃  this configuration will need to be duplicated and/or modified for   ┃
    ┃  services that require more than one job queue.                      ┃
  \*┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛*/

  @Option(
    names = "--job-queue-username",
    defaultValue = "${env:JOB_QUEUE_USERNAME}",
    description = "Username for the RabbitMQ instance",
    arity = "1",
    required = true)
  private String jobQueueUsername;

  @Option(
    names = "--job-queue-password",
    defaultValue = "${env:JOB_QUEUE_PASSWORD}",
    description = "Password for the RabbitMQ instance",
    arity = "1",
    required = true)
  private String jobQueuePassword;

  @Option(
    names = "--job-queue-host",
    defaultValue = "${env:JOB_QUEUE_HOST}",
    description = "Hostname for the RabbitMQ instance.",
    arity = "1",
    required = true)
  private String jobQueueHost;

  @Option(
    names = "--job-queue-port",
    defaultValue = "${env:JOB_QUEUE_PORT}",
    description = "Host port for the RabbitMQ instance.",
    arity = "1")
  private Integer jobQueuePort;
  private static final int DEFAULT_JOB_QUEUE_PORT = 5672;

  @Option(
    names = "--job-queue-workers",
    defaultValue = "${env:JOB_QUEUE_WORKERS}",
    description = "Number or job workers that will consume jobs from the RabbitMQ job queue.",
    arity = "1")
  private Integer jobQueueWorkers;
  private static final int DEFAULT_JOB_QUEUE_WORKERS = 5;

  public String getJobQueueUsername() {
    return jobQueueUsername;
  }

  public String getJobQueuePassword() {
    return jobQueuePassword;
  }

  public String getJobQueueHost() {
    return jobQueueHost;
  }

  public int getJobQueuePort() {
    return jobQueuePort == null ? DEFAULT_JOB_QUEUE_PORT : jobQueuePort;
  }

  public int getJobQueueWorkers() {
    return jobQueueWorkers == null ? DEFAULT_JOB_QUEUE_WORKERS : jobQueueWorkers;
  }

  // endregion RabbitMQ

  // region Minio (S3)

  /*┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓*\
    ┃  Queue S3 Instance                                                   ┃
    ┃                                                                      ┃
    ┃  Connection details used by the async platform library to connect    ┃
    ┃  to a MinIO S3 server instance.                                      ┃
  \*┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛*/

  @Option(
    names = "--s3-host",
    defaultValue = "${env:S3_HOST}",
    description = "S3 instance hostname",
    arity = "1",
    required = true)
  private String s3Host;

  @Option(
    names = "--s3-bucket",
    defaultValue = "${env:S3_BUCKET}",
    description = "S3 bucket name",
    arity = "1",
    required = true)
  private String s3Bucket;

  @Option(
    names = "--s3-access-token",
    defaultValue = "${env:S3_ACCESS_TOKEN}",
    description = "S3 access token",
    arity = "1",
    required = true)
  private String s3AccessToken;

  @Option(
    names = "--s3-secret-key",
    defaultValue = "${env:S3_SECRET_KEY}",
    description = "S3 secret key",
    arity = "1",
    required = true)
  private String s3SecretKey;

  @Option(
    names = "--s3-port",
    defaultValue = "${env:S3_PORT}",
    description = "S3 host port",
    arity = "1")
  private Integer s3Port;
  private static final int DEFAULT_S3_PORT = 9000;

  public String getS3Host() {
    return s3Host;
  }

  public String getS3Bucket() {
    return s3Bucket;
  }

  public String getS3AccessToken() {
    return s3AccessToken;
  }

  public String getS3SecretKey() {
    return s3SecretKey;
  }

  public int getS3Port() {
    return s3Port == null ? DEFAULT_S3_PORT : s3Port;
  }

  // endregion Minio (S3)

  // region Job Configuration

  /*┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓*\
    ┃  Queue Job Configuration                                             ┃
    ┃                                                                      ┃
    ┃  Options and settings configuring the async platform library's job   ┃
    ┃  handling.                                                           ┃
  \*┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛*/

  @Option(
    names = "--job-cache-timeout-days",
    defaultValue = "${env:JOB_CACHE_TIMEOUT_DAYS}",
    description = "Number of days a job will be kept in the cache after it was last accessed.",
    arity = "1")
  private Integer jobCacheTimeoutDays;
  private static final int DEFAULT_JOB_CACHE_TIMEOUT_DAYS = 30;

  public int getJobCacheTimeoutDays() {
    return jobCacheTimeoutDays == null ? DEFAULT_JOB_CACHE_TIMEOUT_DAYS : jobCacheTimeoutDays;
  }

  // endregion Job Configuration
}
