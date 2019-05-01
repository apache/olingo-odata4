/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.request;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.olingo.client.api.communication.request.ODataStreamer;
import org.apache.olingo.client.core.communication.util.PipedOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Streamer utility object.
 */
public abstract class AbstractODataStreamer implements ODataStreamer {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractODataStreamer.class);
  private static final byte[] CRLF = {13, 10};

  /**
   * OutputStream to be used to write objects to the stream.
   */
  private final PipedOutputStream bodyStreamWriter;

  /**
   * Constructor.
   *
   * @param bodyStreamWriter piped stream to be used to retrieve the payload.
   */
  public AbstractODataStreamer(final PipedOutputStream bodyStreamWriter) {
    this.bodyStreamWriter = bodyStreamWriter;
  }

  /**
   * Writes the gibe byte array onto the output stream provided at instantiation time.
   *
   * @param src byte array to be written.
   */
  protected void stream(final byte[] src) {
    new Writer(src, bodyStreamWriter).run();
  }

  /**
   * Stream CR/LF.
   */
  protected void newLine() {
    stream(CRLF);
  }

  /**
   * Gets the piped stream to be used to stream the payload.
   *
   * @return piped stream.
   */
  @Override
  public PipedOutputStream getBodyStreamWriter() {
    return bodyStreamWriter;
  }

  /**
   * Writer thread.
   */
  private class Writer implements Runnable {

    final OutputStream os;

    final byte[] src;

    public Writer(final byte[] src, final OutputStream os) {
      this.os = os;
      this.src = Arrays.copyOf(src, src.length);
    }

    @Override
    public void run() {
      try {
        os.write(src);
      } catch (IOException e) {
        LOG.error("Error streaming object", e);
      }
    }
  }
}
