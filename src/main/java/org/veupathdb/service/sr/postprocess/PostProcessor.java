package org.veupathdb.service.sr.postprocess;

import java.io.File;
import java.io.IOException;

/**
 * Interface for post-processing sequence data.
 *
 * Post-processors take a FASTA file as input and produce transformed output,
 * such as multiple sequence alignments, phylogenetic trees, etc.
 */
public interface PostProcessor {

  /**
   * Process the given FASTA file and return the result.
   *
   * @param fastaInput The input FASTA file containing sequences
   * @return PostProcessResult containing the processed output
   * @throws IOException if processing fails
   */
  PostProcessResult process(File fastaInput) throws IOException;
}
