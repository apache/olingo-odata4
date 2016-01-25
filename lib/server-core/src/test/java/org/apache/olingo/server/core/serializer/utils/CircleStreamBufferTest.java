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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.junit.Test;

/**
 *
 */
public class CircleStreamBufferTest {

  private static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

  @Test
  public void testSimpleWriteReadSignBySign() throws Exception {
    CircleStreamBuffer csb = new CircleStreamBuffer();

    OutputStream write = csb.getOutputStream();
    byte[] writeData = "Test".getBytes(DEFAULT_CHARSET);
    for (byte element : writeData) {
      write.write(element);
    }

    InputStream inStream = csb.getInputStream();
    byte[] buffer = new byte[4];
    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = (byte) inStream.read();
    }

    String result = new String(buffer, DEFAULT_CHARSET);

    assertEquals("Test", result);
  }

  @Test
  public void testSimpleWriteReadSignBySignMoreThenBufferSize() throws Exception {
    CircleStreamBuffer csb = new CircleStreamBuffer(128);

    OutputStream write = csb.getOutputStream();
    int signs = 1024;
    String testData = createTestString(signs);
    byte[] writeData = testData.getBytes(DEFAULT_CHARSET);
    for (byte element : writeData) {
      write.write(element);
    }

    InputStream inStream = csb.getInputStream();
    byte[] buffer = new byte[signs];
    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = (byte) inStream.read();
    }

    String result = new String(buffer, DEFAULT_CHARSET);
    assertEquals(testData, result);
  }

  @Test
  public void testSimpleWriteReadOnce() throws Exception {
    CircleStreamBuffer csb = new CircleStreamBuffer();

    OutputStream write = csb.getOutputStream();
    write.write("Test".getBytes(DEFAULT_CHARSET), 0, 4);

    InputStream inStream = csb.getInputStream();

    String result = readFrom(inStream);
    assertEquals(4, result.length());
    assertEquals("Test", result);
  }

  @Test
  public void testSimpleWriteReadTwice() throws Exception {
    CircleStreamBuffer csb = new CircleStreamBuffer();

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();

    // first writeInternal/read cycle
    outStream.write("Test_1".getBytes(DEFAULT_CHARSET));
    String firstResult = readFrom(inStream);
    assertEquals("Test_1", firstResult);

    // second writeInternal/read cycle
    outStream.write("Test_2".getBytes(DEFAULT_CHARSET));
    String secondResult = readFrom(inStream);
    assertEquals("Test_2", secondResult);
  }

  @Test
  public void testSimpleWriteReadOnce8k() throws Exception {
    CircleStreamBuffer csb = new CircleStreamBuffer();

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();
    final int signs = 8192;

    String testData = createTestString(signs);
    outStream.write(testData.getBytes(DEFAULT_CHARSET));
    String result = readFrom(inStream);

    assertEquals(signs, result.length());
    assertEquals(testData, result);
  }

  @Test
  public void testSimpleWriteExactOneCharacterMoreThenBufferSize() throws Exception {
    int bufferSize = 4096;
    CircleStreamBuffer csb = new CircleStreamBuffer(bufferSize);

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();
    final int signs = bufferSize + 1;

    String testData = createTestString(signs);
    outStream.write(testData.getBytes(DEFAULT_CHARSET));
    String result = readFrom(inStream, bufferSize * 2);

    assertEquals(signs, result.length());
    assertEquals(testData, result);
  }

  @Test
  public void testSimpleWriteReadOneCharacterMoreThenBufferSize() throws Exception {
    int bufferSize = 4096;
    CircleStreamBuffer csb = new CircleStreamBuffer(bufferSize);

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();

    int signs = (1 + bufferSize) * 3;
    String testData = createTestString(signs);
    outStream.write(testData.getBytes(DEFAULT_CHARSET));
    String result = readFrom(inStream);

    assertEquals(signs, result.length());
    assertEquals(testData, result);
  }

  @Test
  public void testSimpleWriteMoreThenDefaultBufferSize() throws Exception {
    CircleStreamBuffer csb = new CircleStreamBuffer();

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();
    final int signs = 70110;

    String testData = createTestString(signs);
    outStream.write(testData.getBytes(DEFAULT_CHARSET));
    String result = readFrom(inStream);

    assertEquals(signs, result.length());
    assertEquals(testData, result);
  }

  @Test
  public void testSimpleWriteMoreThenBufferSize() throws Exception {
    int bufferSize = 4096;
    CircleStreamBuffer csb = new CircleStreamBuffer(bufferSize);

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();
    final int signs = bufferSize * 10;

    String testData = createTestString(signs);
    outStream.write(testData.getBytes(DEFAULT_CHARSET));
    String result = readFrom(inStream, bufferSize * 2);

    assertEquals(signs, result.length());
    assertEquals(testData, result);
  }

  @Test
  public void testSimpleWriteMoreThenBufferSizeAndUmlauts() throws Exception {
    int bufferSize = 4096;
    CircleStreamBuffer csb = new CircleStreamBuffer(bufferSize);

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();
    final int signs = bufferSize * 10;

    String testData = createTestString(signs);
    testData = "äüöÄÜÖ" + testData + "äüöÄÜÖ";
    outStream.write(testData.getBytes(DEFAULT_CHARSET));
    String result = readFrom(inStream);

    assertEquals(testData.length(), result.length());
    assertEquals(testData, result);
  }

  @Test
  public void testSimpleWriteMoreThenBufferSizeAndUmlautsIso() throws Exception {
    int bufferSize = 4096;
    CircleStreamBuffer csb = new CircleStreamBuffer(bufferSize);

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();
    final int signs = bufferSize * 10;

    String testData = createTestString(signs);
    testData = "äüöÄÜÖ" + testData + "äüöÄÜÖ";
    outStream.write(testData.getBytes("iso-8859-1"));
    String result = readFrom(inStream, "iso-8859-1");

    assertEquals(testData.length(), result.length());
    assertEquals(testData, result);
  }

  @Test
  public void testSimpleWriteALotMoreThenBufferSize() throws Exception {
    int bufferSize = 4096;
    CircleStreamBuffer csb = new CircleStreamBuffer(bufferSize);

    OutputStream outStream = csb.getOutputStream();
    InputStream inStream = csb.getInputStream();
    final int signs = bufferSize * 100;

    String testData = createTestString(signs);
    outStream.write(testData.getBytes(DEFAULT_CHARSET));
    String result = readFrom(inStream, bufferSize * 2);

    assertEquals(signs, result.length());
    assertEquals(testData, result);
  }

  @Test(expected = IOException.class)
  public void testCloseInputStream() throws Exception {
    CircleStreamBuffer csb = new CircleStreamBuffer();

    OutputStream write = csb.getOutputStream();
    write.write("Test".getBytes(DEFAULT_CHARSET), 0, 4);

    InputStream inStream = csb.getInputStream();
    inStream.close();
    byte[] buffer = new byte[4];
    int count = inStream.read(buffer);
    assertEquals(4, count);
  }

  @Test(expected = IOException.class)
  public void testCloseOutputStream() throws Exception {
    CircleStreamBuffer csb = new CircleStreamBuffer();

    OutputStream write = csb.getOutputStream();
    write.close();
    write.write("Test".getBytes(DEFAULT_CHARSET), 0, 4);
  }

  // ###################################################
  // #
  // # Below here are test helper methods
  // #
  // ###################################################

  private String readFrom(final InputStream stream) throws IOException {
    return readFrom(stream, DEFAULT_CHARSET, 128);
  }

  private String readFrom(final InputStream stream, final String charset) throws IOException {
    return readFrom(stream, Charset.forName(charset), 128);
  }

  private String readFrom(final InputStream stream, final int bufferSize) throws IOException {
    return readFrom(stream, DEFAULT_CHARSET, bufferSize);
  }

  private String readFrom(final InputStream stream, final Charset charset, final int bufferSize) throws IOException {
    StringBuilder b = new StringBuilder();
    int count;
    byte[] buffer = new byte[bufferSize];
    while ((count = stream.read(buffer)) >= 0) {
      b.append(new String(buffer, 0, count, charset));
    }
    return b.toString();
  }

  private String createTestString(final int signs) {
    StringBuilder b = new StringBuilder();

    for (int i = 0; i < signs; i++) {
      int sign = (int) (32 + (Math.random() * 90));
      b.append((char) sign);
    }

    return b.toString();
  }
}
