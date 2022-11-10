package org.veupathdb.service.sr.reference;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.function.Function;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.FastaSequenceIndexEntry;

/*
 * An adapter converting a contig -> entry function to an index object
 */
public class FakeFastaSequenceIndex extends FastaSequenceIndex {

  private final Function<String, FastaSequenceIndexEntry> f;

  public FakeFastaSequenceIndex(Function<String, FastaSequenceIndexEntry> f){
    this.f = f;
  }

  @Override
  public FastaSequenceIndexEntry getIndexEntry( String contigName ) {
    return this.f.apply(contigName);
  }

  /*
   * Gets called by AbstractIndexedFastaSequenceFile on startup
   */
  @Override
  public Iterator<FastaSequenceIndexEntry> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasIndexEntry( String contigName ) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void write(final Path indexFile) throws IOException {
    throw new UnsupportedOperationException();
  }
}

