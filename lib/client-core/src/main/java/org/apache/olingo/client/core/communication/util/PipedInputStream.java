/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.util;

import org.apache.olingo.client.core.ConfigurationImpl;

import java.io.IOException;

/**
 * This class is equivalent to <code>java.io.PipedInputStream</code>. In the
 * interface it only adds a constructor which allows for specifying the buffer
 * size. Its implementation, however, is much simpler and a lot more efficient
 * than its equivalent. It doesn't rely on polling. Instead it uses proper
 * synchronization with its counterpart <code>be.re.io.PipedOutputStream</code>.
 *
 * Multiple readers can read from this stream concurrently. The block asked for
 * by a reader is delivered completely, or until the end of the stream if less
 * is available. Other readers can't come in between.
 * 
 * @author WD
 */
public class PipedInputStream extends java.io.PipedInputStream {

  final Object sync = new Object();
  byte[] buffer;
  boolean closed = false;
  int readLaps = 0;
  int readPosition = 0;
  PipedOutputStream source;
  int writeLaps = 0;
  int writePosition = 0;

  /**
   * Creates an unconnected PipedInputStream with a default buffer size.
   */
  protected PipedInputStream() {
    this(null);
  }

  /**
   * Creates a PipedInputStream with a default buffer size and connects it to
   * <code>source</code>.
   * 
   * @exception IOException
   *   It was already connected.
   */
  public PipedInputStream(PipedOutputStream source) {
    this(source, ConfigurationImpl.DEFAULT_BUFFER_SIZE);
  }

  /**
   * Creates a PipedInputStream with buffer size <code>bufferSize</code> and
   * connects it to <code>source</code>.
   * 
   */
  public PipedInputStream(PipedOutputStream source, int bufferSize) {
    if (source != null) {
      try {
        connect(source);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    buffer = new byte[bufferSize];
  }

  public int available() {
    /*
     * The circular buffer is inspected to see where the reader and the
     * writer are located.
     */
    return writePosition > readPosition
        /* The writer is in the same lap. */ ? writePosition - readPosition
        : (writePosition < readPosition
            /* The writer is in the next lap. */ ? buffer.length
                - readPosition
                + 1
                + writePosition
            :
            /*
             * The writer is at the same position or a complete lap
             * ahead.
             */
            (writeLaps > readLaps ? buffer.length : 0));
  }

  /**
   * @exception IOException
   *   The pipe is not connected.
   */
  public void close() throws IOException {
    if (source == null) {
      throw new IOException("Unconnected pipe");
    }

    synchronized (sync) {
      closed = true;
      // Release any pending writers.
      sync.notifyAll();
    }
  }

  /**
   * @exception IOException
   *   The pipe is already connected.
   */
  public void connect(PipedOutputStream source) throws IOException {
    if (this.source != null) {
      throw new IOException("Pipe already connected");
    }

    this.source = source;
    source.sink = this;
  }

  public void mark(int readLimit) {
    /* not supported */
  }

  public boolean markSupported() {
    return false;
  }

  public int read() throws IOException {
    byte[] b = new byte[0];
    int result = read(b);

    return result == -1 ? -1 : b[0];
  }

  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * @exception IOException
   *   The pipe is not connected.
   */
  public int read(byte[] b, int off, int len) throws IOException {
    if (source == null) {
      throw new IOException("Unconnected pipe");
    }

    synchronized (sync) {
      if (writePosition == readPosition && writeLaps == readLaps) {
        if (closed) {
          return -1;
        }

        // Wait for any writer to put something in the circular buffer.

        try {
          sync.wait();
        } catch (InterruptedException e) {
          throw new IOException(e.getMessage());
        }

        // Try again.

        return read(b, off, len);
      }

      // Don't read more than the capacity indicated by len or what's
      // available
      // in the circular buffer.
      int amount = Math .min(len,
                  (writePosition > readPosition
                      ? writePosition
                      : buffer.length)
                      -
                      readPosition);

      System.arraycopy(
              buffer,
              readPosition,
              b,
              off,
              amount);
      readPosition += amount;

      if (readPosition == buffer.length) {
        // A lap was completed, so go // back.
        readPosition = 0;
        readLaps++;
      }

      // The buffer is only released when the complete desired block was
      // obtained.

      if (amount < len) {
        int second = read(b, off + amount, len - amount);

        return second == -1 ? amount : amount + second;
      } else {
        sync.notifyAll();
      }

      return amount;
    }
  }

}
