package org.veupathdb.service.sr.postprocess;

import org.junit.jupiter.api.Test;
import org.veupathdb.service.sr.AsyncOptions;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.postprocess.genetree.GeneTreeProcessor;
import org.veupathdb.service.sr.postprocess.isolates.IsolatesMsaProcessor;
import org.veupathdb.service.sr.postprocess.orthomcl.OrthomclMsaProcessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PostProcessorFactoryTest {

  private final AsyncOptions mockOptions = mock(AsyncOptions.class);

  @Test
  void testCreateOrthomclMsaProcessor() {
    OrthomclMsaOptions options = new OrthomclMsaOptionsImpl();
    options.setFormat(OrthomclMsaFormat.CLUSTAL);

    PostProcessor processor = PostProcessorFactory.create(
      PostProcessType.ORTHOMCLMSA,
      options,
      null,
      null,
      mockOptions
    );

    assertNotNull(processor);
    assertInstanceOf(OrthomclMsaProcessor.class, processor);
  }

  @Test
  void testCreateIsolatesMsaProcessor() {
    IsolatesMsaOptions options = new IsolatesMsaOptionsImpl();
    options.setFormat(IsolatesMsaFormat.TBD);

    PostProcessor processor = PostProcessorFactory.create(
      PostProcessType.ISOLATESMSA,
      null,
      options,
      null,
      mockOptions
    );

    assertNotNull(processor);
    assertInstanceOf(IsolatesMsaProcessor.class, processor);
  }

  @Test
  void testCreateGeneTreeProcessor() {
    GeneTreeOptions options = new GeneTreeOptionsImpl();
    options.setFormat(GeneTreeFormat.TBD);

    PostProcessor processor = PostProcessorFactory.create(
      PostProcessType.GENETREE,
      null,
      null,
      options,
      mockOptions
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
        null,
        mockOptions
      )
    );
  }

  @Test
  void testCreateOrthomclMsaWithoutOptions() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      PostProcessorFactory.create(
        PostProcessType.ORTHOMCLMSA,
        null,  // Missing orthomclMsaOptions
        null,
        null,
        mockOptions
      )
    );

    assertTrue(exception.getMessage().contains("orthomclMsaOptions required"));
  }

  @Test
  void testCreateIsolatesMsaWithoutOptions() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      PostProcessorFactory.create(
        PostProcessType.ISOLATESMSA,
        null,
        null,  // Missing isolatesMsaOptions
        null,
        mockOptions
      )
    );

    assertTrue(exception.getMessage().contains("isolatesMsaOptions required"));
  }

  @Test
  void testCreateGeneTreeWithoutOptions() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      PostProcessorFactory.create(
        PostProcessType.GENETREE,
        null,
        null,
        null,  // Missing geneTreeOptions
        mockOptions
      )
    );

    assertTrue(exception.getMessage().contains("geneTreeOptions required"));
  }
}
