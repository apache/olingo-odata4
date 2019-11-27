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
 * This class is equivalent to <code>java.io.PipedOutputStream</code>. In the
 * interface it only adds a constructor which allows for specifying the buffer
 * size. Its implementation, however, is much simpler and a lot more efficient
 * than its equivalent. It doesn't rely on polling. Instead it uses proper
 * synchronization with its counterpart <code>be.re.io.PipedInputStream</code>.
 *
 * Multiple writers can write in this stream concurrently. The block written by
 * a writer is put in completely. Other writers can't come in between.
 * 
 * @author WD
 */
public class PipedOutputStream extends java.io.PipedOutputStream {

  PipedInputStream sink;

  /**
   * Creates an unconnected PipedOutputStream.
   */

  protected PipedOutputStream() {
    this(null);
  }

  /**
   * Creates a PipedOutputStream with a default buffer size and connects it to
   * <code>sink</code>.
   * 
   */
  public PipedOutputStream(PipedInputStream sink) {
    this(sink, ConfigurationImpl.DEFAULT_BUFFER_SIZE);
  }

  /**
   * Creates a PipedOutputStream with buffer size <code>bufferSize</code> and
   * connects it to <code>sink</code>.
   * 
   */
  public PipedOutputStream(PipedInputStream sink, int bufferSize) {
    if (sink != null) {
      try {
        connect(sink);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      sink.buffer = new byte[bufferSize];
    }
  }

  /**
   * @exception IOException
   * The pipe is not connected.
   */
  public void close() throws IOException {
    if (sink == null) {
      throw new IOException("Unconnected pipe");
    }

    synchronized (sink.sync) {
      sink.closed = true;
      flush();
    }
  }

  /**
   * @exception IOException
   * The pipe is already connected.
   */

  public void connect(PipedInputStream sink) throws IOException {
    if (this.sink != null) {
      throw new IOException("Pipe already connected");
    }

    this.sink = sink;
    sink.source = this;
  }

  public void flush() throws IOException {
    synchronized (sink.sync) {
      // Release all readers.
      sink.sync.notifyAll();
    }
  }

  public void write(int b) throws IOException {
    write(new byte[] { (byte) b });
  }

  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  /**
   * @exception IOException
   * The pipe is not connected or a reader has closed it.
   */

  public void write(byte[] b, int off, int len) throws IOException {
    if (sink == null) {
      throw new IOException("Unconnected pipe");
    }

    if (sink.closed) {
      throw new IOException("Broken pipe");
    }

    synchronized (sink.sync) {
      if (sink.writePosition == sink.readPosition
          && sink.writeLaps > sink.readLaps) {
        // The circular buffer is full, so wait for some reader to
        // consume something.
        try {
          sink.sync.wait();
        } catch (InterruptedException e) {
          throw new IOException(e.getMessage());
        }

        // Try again.

        write(b, off, len);

        return;
      }

      // Don't write more than the capacity indicated by len or the space
      // available in the circular buffer.

      int amount = Math.min(len,
                  (sink.writePosition < sink.readPosition
                      ? sink.readPosition
                      : sink.buffer.length)
                      - sink.writePosition);

      System.arraycopy(
              b,
              off,
              sink.buffer,
              sink.writePosition,
              amount);
      sink.writePosition += amount;

      if (sink.writePosition == sink.buffer.length) {
        sink.writePosition = 0;
        ++sink.writeLaps;
      }

      // The buffer is only released when the complete desired block was
      // written.
      if (amount < len) {
        write(b, off + amount, len - amount);
      } else {
        sink.sync.notifyAll();
      }
    }
  }

}