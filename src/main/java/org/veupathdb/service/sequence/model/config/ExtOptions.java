package org.veupathdb.service.sequence.model.config;

import java.util.Optional;

import org.veupathdb.lib.container.jaxrs.config.Options;
import picocli.CommandLine.Option;

// TODO: doesn't get set
public class ExtOptions extends Options
{
  @Option(
    names = "--sequence-genomic-fasta",
    defaultValue = "${env:SEQUENCE_GENOMIC_FASTA}",
    description = "env: SEQUENCE_GENOMIC_FASTA",
    arity = "1")
  private String sequenceGenomicFasta;

  @Option(
    names = "--sequence-genomic-faidx",
    defaultValue = "${env:SEQUENCE_GENOMIC_FAIDX}",
    description = "env: SEQUENCE_GENOMIC_FAIDX",
    arity = "1")
  private String sequenceGenomicFaidx;

  public Optional < String > getSequenceGenomicFasta() {
    return Optional.ofNullable(System.getenv("SEQUENCE_GENOMIC_FASTA"));
  }

  public Optional < String > getSequenceGenomicFaidx() {
    return Optional.ofNullable(System.getenv("SEQUENCE_GENOMIC_FAIDX"));
  }
}
