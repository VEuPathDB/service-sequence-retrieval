package org.veupathdb.service.sr;

import org.veupathdb.lib.container.jaxrs.config.Options;
import picocli.CommandLine.Option;

/**
 * Service configuration options.
 *
 * Contains both async platform configuration (queues, S3, Postgres database)
 * and general application configuration (MSA post-processing, etc.).
 *
 */
public class SrtServiceOptions extends Options {

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

  @Option(
    names = "--s3-use-https",
    defaultValue = "${env:S3_USE_HTTPS}",
    description = "Whether the platform should use HTTPS when connecting to S3",
    arity = "1"
  )
  private Boolean s3UseHttps;
  private static final boolean DEFAULT_S3_USE_HTTPS = true;

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

  public boolean getS3UseHttps() {
    return s3UseHttps == null ? DEFAULT_S3_USE_HTTPS : s3UseHttps;
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

  // region MSA Configuration

  /*┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓*\
    ┃  MSA Post-Processing Configuration                                   ┃
    ┃                                                                      ┃
    ┃  Options for configuring MSA post-processing using clustal-omega.   ┃
  \*┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛*/

  @Option(
    names = "--clustalo-binary-path",
    defaultValue = "${env:CLUSTALO_BINARY_PATH}",
    description = "Path to the clustalo binary executable",
    arity = "1")
  private String clustaloBinaryPath;
  private static final String DEFAULT_CLUSTALO_BINARY_PATH = "/usr/bin/clustalo";

  @Option(
    names = "--msa-sync-max-sequences",
    defaultValue = "${env:MSA_SYNC_MAX_SEQUENCES}",
    description = "Maximum number of sequences allowed for synchronous MSA requests",
    arity = "1")
  private Integer msaSyncMaxSequences;
  private static final int DEFAULT_MSA_SYNC_MAX_SEQUENCES = 20;

  @Option(
    names = "--msa-async-max-sequences",
    defaultValue = "${env:MSA_ASYNC_MAX_SEQUENCES}",
    description = "Maximum number of sequences allowed for asynchronous MSA requests",
    arity = "1",
    required = true)
  private Integer msaAsyncMaxSequences;

  @Option(
    names = "--clustalo-sync-timeout-seconds",
    defaultValue = "${env:CLUSTALO_SYNC_TIMEOUT_SECONDS}",
    description = "Timeout in seconds for clustalo execution in synchronous requests",
    arity = "1")
  private Integer clustaloSyncTimeoutSeconds;
  private static final int DEFAULT_CLUSTALO_SYNC_TIMEOUT_SECONDS = 30;

  @Option(
    names = "--clustalo-async-timeout-seconds",
    defaultValue = "${env:CLUSTALO_ASYNC_TIMEOUT_SECONDS}",
    description = "Timeout in seconds for clustalo execution in asynchronous jobs",
    arity = "1")
  private Integer clustaloAsyncTimeoutSeconds;
  private static final int DEFAULT_CLUSTALO_ASYNC_TIMEOUT_SECONDS = 1800;

  @Option(
    names = "--itol-base-url",
    defaultValue = "${env:ITOL_BASE_URL}",
    description = "Base URL for iTOL (Interactive Tree of Life) service for phylogenetic tree visualization",
    arity = "1")
  private String itolBaseUrl;
  private static final String DEFAULT_ITOL_BASE_URL = "https://itol.embl.de";

  public String getClustaloBinaryPath() {
    return clustaloBinaryPath == null ? DEFAULT_CLUSTALO_BINARY_PATH : clustaloBinaryPath;
  }

  public int getMsaSyncMaxSequences() {
    return msaSyncMaxSequences == null ? DEFAULT_MSA_SYNC_MAX_SEQUENCES : msaSyncMaxSequences;
  }

  public int getMsaAsyncMaxSequences() {
    return msaAsyncMaxSequences;
  }

  public int getClustaloSyncTimeoutSeconds() {
    return clustaloSyncTimeoutSeconds == null ? DEFAULT_CLUSTALO_SYNC_TIMEOUT_SECONDS : clustaloSyncTimeoutSeconds;
  }

  public int getClustaloAsyncTimeoutSeconds() {
    return clustaloAsyncTimeoutSeconds == null ? DEFAULT_CLUSTALO_ASYNC_TIMEOUT_SECONDS : clustaloAsyncTimeoutSeconds;
  }

  public String getItolBaseUrl() {
    return itolBaseUrl == null ? DEFAULT_ITOL_BASE_URL : itolBaseUrl;
  }

  // endregion MSA Configuration

  // region Gene Tree Configuration

  /*┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓*\
    ┃  Gene Tree Post-Processing Configuration                            ┃
    ┃                                                                      ┃
    ┃  Options for configuring gene tree post-processing using mafft      ┃
    ┃  and fasttree.                                                       ┃
  \*┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛*/

  @Option(
    names = "--mafft-binary-path",
    defaultValue = "${env:MAFFT_BINARY_PATH}",
    description = "Path to the mafft binary executable",
    arity = "1")
  private String mafftBinaryPath;
  private static final String DEFAULT_MAFFT_BINARY_PATH = "/usr/bin/mafft";

  @Option(
    names = "--fasttree-binary-path",
    defaultValue = "${env:FASTTREE_BINARY_PATH}",
    description = "Path to the fasttree binary executable",
    arity = "1")
  private String fastTreeBinaryPath;
  private static final String DEFAULT_FASTTREE_BINARY_PATH = "/usr/bin/fasttree";

  @Option(
    names = "--genetree-sync-max-sequences",
    defaultValue = "${env:GENETREE_SYNC_MAX_SEQUENCES}",
    description = "Maximum number of sequences allowed for synchronous gene tree requests",
    arity = "1")
  private Integer geneTreeSyncMaxSequences;
  private static final int DEFAULT_GENETREE_SYNC_MAX_SEQUENCES = 20;

  @Option(
    names = "--genetree-async-max-sequences",
    defaultValue = "${env:GENETREE_ASYNC_MAX_SEQUENCES}",
    description = "Maximum number of sequences allowed for asynchronous gene tree requests",
    arity = "1",
    required = true)
  private Integer geneTreeAsyncMaxSequences;

  @Option(
    names = "--genetree-sync-timeout-seconds",
    defaultValue = "${env:GENETREE_SYNC_TIMEOUT_SECONDS}",
    description = "Timeout in seconds for gene tree execution in synchronous requests",
    arity = "1")
  private Integer geneTreeSyncTimeoutSeconds;
  private static final int DEFAULT_GENETREE_SYNC_TIMEOUT_SECONDS = 30;

  @Option(
    names = "--genetree-async-timeout-seconds",
    defaultValue = "${env:GENETREE_ASYNC_TIMEOUT_SECONDS}",
    description = "Timeout in seconds for gene tree execution in asynchronous jobs",
    arity = "1")
  private Integer geneTreeAsyncTimeoutSeconds;
  private static final int DEFAULT_GENETREE_ASYNC_TIMEOUT_SECONDS = 1800;

  public String getMafftBinaryPath() {
    return mafftBinaryPath == null ? DEFAULT_MAFFT_BINARY_PATH : mafftBinaryPath;
  }

  public String getFastTreeBinaryPath() {
    return fastTreeBinaryPath == null ? DEFAULT_FASTTREE_BINARY_PATH : fastTreeBinaryPath;
  }

  public int getGeneTreeSyncMaxSequences() {
    return geneTreeSyncMaxSequences == null ? DEFAULT_GENETREE_SYNC_MAX_SEQUENCES : geneTreeSyncMaxSequences;
  }

  public int getGeneTreeAsyncMaxSequences() {
    return geneTreeAsyncMaxSequences;
  }

  public int getGeneTreeSyncTimeoutSeconds() {
    return geneTreeSyncTimeoutSeconds == null ? DEFAULT_GENETREE_SYNC_TIMEOUT_SECONDS : geneTreeSyncTimeoutSeconds;
  }

  public int getGeneTreeAsyncTimeoutSeconds() {
    return geneTreeAsyncTimeoutSeconds == null ? DEFAULT_GENETREE_ASYNC_TIMEOUT_SECONDS : geneTreeAsyncTimeoutSeconds;
  }

  // endregion Gene Tree Configuration
}
