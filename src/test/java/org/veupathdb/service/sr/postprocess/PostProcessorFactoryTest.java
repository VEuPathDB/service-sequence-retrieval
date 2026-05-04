package org.veupathdb.service.sr.postprocess;

import org.junit.jupiter.api.Test;
import org.veupathdb.service.sr.SrtServiceOptions;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.postprocess.genetree.GeneTreeProcessor;
import org.veupathdb.service.sr.postprocess.msa.MsaProcessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PostProcessorFactoryTest {

  private final SrtServiceOptions mockOptions = mock(SrtServiceOptions.class);

  @Test
  void testCreateMsaProcessor() {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTAL);

    PostProcessor processor = PostProcessorFactory.create(
        PostProcessType.MSA,
        options,
        null,
        mockOptions,
        ProcessingContext.SYNC
    );

    assertNotNull(processor);
    assertInstanceOf(MsaProcessor.class, processor);
  }

  @Test
  void testCreateGeneTreeProcessor() {
    GeneTreeOptions options = new GeneTreeOptionsImpl();
    options.setFormat(GeneTreeFormat.NEWICK);

    PostProcessor processor = PostProcessorFactory.create(
        PostProcessType.GENETREE,
        null,
        options,
        mockOptions,
        ProcessingContext.SYNC
    );

    assertNotNull(processor);
    assertInstanceOf(GeneTreeProcessor.class, processor);
  }

  @Test
  void testCreateWithNullPostProcessType() {
    assertThrows(IllegalArgumentException.class, () ->
        PostProcessorFactory.create(
            null,
            null,
            null,
            mockOptions,
            ProcessingContext.SYNC
        )
    );
  }

  @Test
  void testCreateMsaWithoutOptions() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        PostProcessorFactory.create(
            PostProcessType.MSA,
            null,  // Missing msaOptions
            null,
            mockOptions,
            ProcessingContext.SYNC
        )
    );

    assertTrue(exception.getMessage().contains("msaOptions required"));
  }

  @Test
  void testCreateGeneTreeWithoutOptions() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        PostProcessorFactory.create(
            PostProcessType.GENETREE,
            null,
            null,  // Missing geneTreeOptions
            mockOptions,
            ProcessingContext.SYNC
        )
    );

    assertTrue(exception.getMessage().contains("geneTreeOptions required"));
  }
}
