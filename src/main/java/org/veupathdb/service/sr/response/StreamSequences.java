package org.veupathdb.service.sr.response;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.veupathdb.service.sr.generated.model.DeflineFormat;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import htsjdk.tribble.bed.BEDFeature;

import java.nio.charset.Charset;

public class StreamSequences {
  private static final Logger LOG = LogManager.getLogger(StreamSequences.class);

  private static final Charset CHARSET = StandardCharsets.UTF_8;

  private static final byte[] LINE_SEPARATOR = "\n".getBytes(CHARSET);

  public static void write(OutputStream outputStream,
                           IndexedFastaSequenceFile sequences,
                           List<BEDFeature> features,
                           DeflineFormat deflineFormat,
                           int requestedBasesPerLine) {
    int basesPerLine = requestedBasesPerLine > 0 ? requestedBasesPerLine : Integer.MAX_VALUE;
    try (var buf = new BufferedOutputStream(outputStream)) {
      for (var feature : features) {
        var defline = Deflines.deflineForFeature(feature, deflineFormat, sequences.getIndex().size());
        var bases = Bases.getBasesForBedFeature(sequences, feature);

        LOG.debug("Writing sequence for feature {} to OutputStream.", feature.getName());
        appendSequenceToStream(buf, defline, bases, basesPerLine);
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to stream sequences response", e);
    }
  }

  /*
   * appends sequence to stream
   * customized from htsjdk.samtools.reference.FastaReferenceWriter;
   */
  private static void appendSequenceToStream(OutputStream outputStream, String defline, byte[] bases, int basesPerLine) throws IOException {
    outputStream.write(defline.getBytes(CHARSET));
    outputStream.write(LINE_SEPARATOR);

    final int totalBasesCount = bases.length;
    int writtenBasesCount = 0;
    int currentLineBasesCount = 0;
    while (writtenBasesCount < totalBasesCount) {
      if (currentLineBasesCount == basesPerLine) {
        outputStream.write(LINE_SEPARATOR);
        currentLineBasesCount = 0;
      }
      final int nextLength = Math.min(totalBasesCount - writtenBasesCount, basesPerLine - currentLineBasesCount);
      outputStream.write(bases, writtenBasesCount, nextLength);
      currentLineBasesCount += nextLength;
      writtenBasesCount += nextLength;
    }
    outputStream.write(LINE_SEPARATOR);
  }
}
