package org.veupathdb.service.sequence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.veupathdb.lib.container.jaxrs.config.Options;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import org.veupathdb.lib.container.jaxrs.server.Server;

import org.veupathdb.service.sequence.model.config.ExtOptions;

import org.veupathdb.service.sequence.service.Sequences;

public class Main extends Server {
  private static final Logger LOG = LogManager.getLogger(Main.class);

  public static final ExtOptions options = new ExtOptions();

  public static void main(String[] args) {
    var server = new Main();
    server.enableAccountDB();
    server.start(args);
  }

  @Override
  protected ContainerResources newResourceConfig(Options options) {
    final var out =  new Resources(options);

    // Enabled by default for debugging purposes, this should be removed when
    // production ready.
    out.property("jersey.config.server.tracing.type", "ALL")
      .property("jersey.config.server.tracing.threshold", "VERBOSE");

    return out;
  }

  @Override
  protected void postCliParse(Options opts) {
    Sequences.initialize(options);
  }
}
