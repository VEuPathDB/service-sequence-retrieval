package org.veupathdb.service.sr.postprocess;

import htsjdk.tribble.bed.BEDFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
   * @param features The list of features (with IDs) that were used to generate the FASTA
   * @return PostProcessResult containing the processed output
   * @throws IOException if processing fails
   */
  PostProcessResult process(File fastaInput, List<BEDFeature> features) throws IOException;
}
