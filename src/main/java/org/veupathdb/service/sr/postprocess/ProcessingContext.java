package org.veupathdb.service.sr.postprocess;

/**
 * Execution context for post-processing operations.
 *
 * Determines which timeout and resource limits should be applied.
 */
public enum ProcessingContext {
  /**
   * Synchronous processing within an HTTP request.
   * Uses shorter timeout suitable for web requests.
   */
  SYNC,

  /**
   * Asynchronous processing in a background job.
   * Uses longer timeout for larger, more complex operations.
   */
  ASYNC
}
