package org.veupathdb.service.sr.reference;

import htsjdk.tribble.bed.BEDFeature;

import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * Result of {@link ReferenceDAO#validateAndPrepareResponse}.
 *
 * Wraps the deferred FASTA-streaming {@link Consumer} together with the list of features
 * that actually survived {@code percentActg} filtering. {@link #getSurvivedFeatures()} is
 * only populated after {@link #stream()}'s consumer has been invoked, since filtering happens
 * during streaming, not at construction time.
 *
 * Post-processing callers (MSA/GENETREE) must use {@link #getSurvivedFeatures()} instead of
 * the original feature list, so their inputs match the filtered FASTA content.
 */
public class PreparedResponse {

  private final Consumer<OutputStream> streamer;
  private List<BEDFeature> survivedFeatures;

  /**
   * @param streamWriter a function that writes the FASTA output for the given OutputStream and
   *                      returns the list of features that survived filtering and were
   *                      actually written.
   */
  PreparedResponse(java.util.function.Function<OutputStream, List<BEDFeature>> streamWriter) {
    this.streamer = outputStream -> this.survivedFeatures = streamWriter.apply(outputStream);
  }

  /**
   * @return the deferred streaming consumer. Invoking {@code accept} on the returned consumer
   *         writes the (filtered) FASTA output and populates {@link #getSurvivedFeatures()}.
   */
  public Consumer<OutputStream> stream() {
    return streamer;
  }

  /**
   * @return the features that survived percentActg filtering and were actually written to the
   *         FASTA output. Only valid after {@link #stream()}'s consumer has been invoked with
   *         an OutputStream; throws otherwise.
   */
  public List<BEDFeature> getSurvivedFeatures() {
    if (survivedFeatures == null) {
      throw new IllegalStateException(
          "getSurvivedFeatures() called before the stream was written; " +
          "survived features are only known after streaming has occurred.");
    }
    return survivedFeatures;
  }
}
