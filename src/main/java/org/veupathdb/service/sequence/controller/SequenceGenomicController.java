package org.veupathdb.service.sequence.controller;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.OutputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.sequence.generated.model.SequenceGenomicPostRequest;
import org.veupathdb.service.sequence.generated.resources.SequenceGenomic;

public class SequenceGenomicController implements SequenceGenomic {

  @Context
  private Request req;

  @Override
  @Authenticated
  public PostSequenceGenomicResponse postSequenceGenomic(SequenceGenomicPostRequest entity) {
    var contig = entity.getContig();
    var start = entity.getStart();
    var end = entity.getEnd();

    StreamingOutput responseEntity = new StreamingOutput() {
      @Override
      public void write(OutputStream os) throws IOException{
        Writer writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
        var x = String.format(">%s:%s..%s\nACTG\n", contig, start, end);
        writer.write(x);
        writer.flush();
        try {
          Thread.sleep(4000);
        } catch (InterruptedException e){
        }
        writer.write(x);
        writer.flush();
      }
    };
    return PostSequenceGenomicResponse.respond200WithApplicationOctetStream(responseEntity);

  }
}
