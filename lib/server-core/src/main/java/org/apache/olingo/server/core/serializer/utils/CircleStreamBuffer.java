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
package org.apache.olingo.server.core.serializer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Circular stream buffer to write/read into/from one single buffer.
 * With support of {@link InputStream} and {@link OutputStream} access to buffered data.
 *
 *
 */
public class CircleStreamBuffer {

  private static final int NEW_BUFFER_RESIZE_FACTOR = 2;
  private static final int READ_EOF = -1;
  private static final int DEFAULT_CAPACITY = 8192;
  private static final int MAX_CAPACITY = DEFAULT_CAPACITY * 32;

  private int currentAllocateCapacity = DEFAULT_CAPACITY;

  private boolean writeMode = true;
  private boolean writeClosed = false;
  private boolean readClosed = false;

  private Queue<ByteBuffer> bufferQueue = new LinkedBlockingQueue<ByteBuffer>();
  private ByteBuffer currentWriteBuffer;

  private final InternalInputStream inStream;
  private final InternalOutputStream outStream;

  /**
   * Creates a {@link CircleStreamBuffer} with default buffer size.
   */
  public CircleStreamBuffer() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Create a {@link CircleStreamBuffer} with given initial buffer size.
   *
   * @param initialCapacity initial capacity of internal buffer
   */
  public CircleStreamBuffer(final int initialCapacity) {
    currentAllocateCapacity = initialCapacity;
    createNewWriteBuffer();
    inStream = new InternalInputStream(this);
    outStream = new InternalOutputStream(this);
  }

  /**
   * Get {@link InputStream} for data read access.
   *
   * @return the stream
   */
  public InputStream getInputStream() {
    return inStream;
  }

  /**
   * Get {@link OutputStream} for write data.
   *
   * @return the stream
   */
  public OutputStream getOutputStream() {
    return outStream;
  }

  // #############################################
  // #
  // # Common parts
  // #
  // #############################################

  /**
   * Closes the write (input) part of the {@link CircleStreamBuffer}.
   * After this call the buffer can only be read out.
   */
  public void closeWrite() {
    writeClosed = true;
  }

  /**
   * Closes the read (output) part of the {@link CircleStreamBuffer}.
   * After this call it is possible to write into the buffer (but can never be read out).
   */
  public void closeRead() {
    readClosed = true;
    // clear references to byte buffers
    ByteBuffer buffer = bufferQueue.poll();
    while (buffer != null) {
      buffer.clear();
      buffer = bufferQueue.poll();
    }
  }

  /**
   * Closes write and read part (and hence the complete buffer).
   */
  public void close() {
    closeWrite();
    closeRead();
  }

  private int remaining() throws IOException {
    if (writeMode) {
      return currentWriteBuffer.remaining();
    } else {
      ByteBuffer toRead = getReadBuffer();
      if (toRead == null) {
        return 0;
      }
      return toRead.remaining();
    }
  }

  // #############################################
  // #
  // # Reading parts
  // #
  // #############################################

  private ByteBuffer getReadBuffer() throws IOException {
    if (readClosed) {
      throw new IOException("Tried to read from closed stream.");
    }

    boolean next = false;
    ByteBuffer tmp = null;
    if (writeMode) {
      writeMode = false;
      next = true;
    } else {
      tmp = bufferQueue.peek();
      if (tmp != null && !tmp.hasRemaining()) {
        tmp = bufferQueue.poll();
        next = true;
      }
    }

    if (next) {
      tmp = bufferQueue.peek();
      if (tmp != null) {
        tmp.flip();
      }
      tmp = getReadBuffer();
    }

    return tmp;
  }

  private int read(final byte[] b, final int off, final int len) throws IOException {
    ByteBuffer readBuffer = getReadBuffer();
    if (readBuffer == null) {
      return READ_EOF;
    }

    int toReadLength = readBuffer.remaining();
    if (len < toReadLength) {
      toReadLength = len;
    }
    readBuffer.get(b, off, toReadLength);
    return toReadLength;
  }

  private int read() throws IOException {
    ByteBuffer readBuffer = getReadBuffer();
    if (readBuffer == null) {
      return READ_EOF;
    }

    return readBuffer.get();
  }

  public ByteBuffer getBuffer() throws IOException {
    if (readClosed) {
      throw new IOException("Tried to read from closed stream.");
    }
    writeMode = false;

    // FIXME: mibo_160108: This is not efficient and only for test/poc reasons
    int reqSize = 0;
    for (ByteBuffer byteBuffer : bufferQueue) {
      reqSize += byteBuffer.position();
    }
    ByteBuffer tmp = ByteBuffer.allocateDirect(reqSize);
    for (ByteBuffer byteBuffer : bufferQueue) {
      byteBuffer.flip();
      tmp.put(byteBuffer);
    }
    return tmp;
  }

  // #############################################
  // #
  // # Writing parts
  // #
  // #############################################

  private void write(final byte[] data, final int off, final int len) throws IOException {
    ByteBuffer writeBuffer = getWriteBuffer(len);
    writeBuffer.put(data, off, len);
  }

  private ByteBuffer getWriteBuffer(final int size) throws IOException {
    if (writeClosed) {
      throw new IOException("Tried to write into closed stream.");
    }

    if (writeMode) {
      if (remaining() < size) {
        createNewWriteBuffer(size);
      }
    } else {
      writeMode = true;
      createNewWriteBuffer();
    }

    return currentWriteBuffer;
  }

  private void write(final int b) throws IOException {
    ByteBuffer writeBuffer = getWriteBuffer(1);
    writeBuffer.put((byte) b);
  }

  private void createNewWriteBuffer() {
    createNewWriteBuffer(currentAllocateCapacity);
  }

  /**
   * Creates a new buffer (per {@link #allocateBuffer(int)}) with the requested capacity as minimum capacity, add the
   * new allocated
   * buffer to the {@link #bufferQueue} and set it as {@link #currentWriteBuffer}.
   *
   * @param requestedCapacity minimum capacity for new allocated buffer
   */
  private void createNewWriteBuffer(final int requestedCapacity) {
    ByteBuffer b = allocateBuffer(requestedCapacity);
    bufferQueue.add(b);
    currentWriteBuffer = b;
  }

  /**
   * Allocate a new buffer with requested capacity
   *
   * @param requestedCapacity minimal capacity of new buffer
   * @return the buffer
   */
  private ByteBuffer allocateBuffer(final int requestedCapacity) {
    if (requestedCapacity > MAX_CAPACITY) {
      currentAllocateCapacity = MAX_CAPACITY;
      return ByteBuffer.allocate(requestedCapacity);
    }

    if (requestedCapacity <= currentAllocateCapacity) {
      currentAllocateCapacity *= NEW_BUFFER_RESIZE_FACTOR;
      if (currentAllocateCapacity > MAX_CAPACITY) {
        currentAllocateCapacity = MAX_CAPACITY;
      }
    } else {
      currentAllocateCapacity = requestedCapacity;
    }

    return ByteBuffer.allocate(currentAllocateCapacity);
  }

  // #############################################
  // #
  // # Inner classes (streams)
  // #
  // #############################################

  /**
   *
   */
  private static class InternalInputStream extends InputStream {

    private final CircleStreamBuffer inBuffer;

    public InternalInputStream(final CircleStreamBuffer csBuffer) {
      inBuffer = csBuffer;
    }

    @Override
    public int available() throws IOException {
      return inBuffer.remaining();
    }

    @Override
    public int read() throws IOException {
      return inBuffer.read();
    }

    @Override
    public int read(final byte[] buffer, final int off, final int len) throws IOException {
      return inBuffer.read(buffer, off, len);
    }

    @Override
    public void close() throws IOException {
      inBuffer.closeRead();
    }
  }

  /**
   *
   */
  private static class InternalOutputStream extends OutputStream {
    private final CircleStreamBuffer outBuffer;

    public InternalOutputStream(final CircleStreamBuffer csBuffer) {
      outBuffer = csBuffer;
    }

    @Override
    public void write(final int b) throws IOException {
      outBuffer.write(b);
    }

    @Override
    public void write(final byte[] buffer, final int off, final int len) throws IOException {
      outBuffer.write(buffer, off, len);
    }

    @Override
    public void close() throws IOException {
      outBuffer.closeWrite();
    }
  }
}
