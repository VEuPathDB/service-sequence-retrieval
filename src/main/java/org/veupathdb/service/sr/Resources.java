package org.veupathdb.service.sr;

import org.veupathdb.lib.container.jaxrs.config.Options;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import org.veupathdb.service.sr.service.SequenceRetrievalService;
import org.veupathdb.service.sr.config.ReferenceSequenceConfig;
import org.veupathdb.service.sr.util.Sequences;

/**
 * Service Resource Registration.
 *
 * This is where all the individual service specific resources and middleware
 * should be registered.
 */
public class Resources extends ContainerResources {

  public Resources(Options opts) {
    super(opts);

    // read environment to create a configuration and initialize sequence processing
    Sequences.initialize(ReferenceSequenceConfig.getInstance());
  }

  /**
   * Returns an array of JaxRS endpoints, providers, and contexts.
   *
   * Entries in the array can be either classes or instances.
   */
  @Override
  protected Object[] resources() {
    return new Object[] {
      SequenceRetrievalService.class
    };
  }
}
