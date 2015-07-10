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
package org.apache.olingo.server.core.deserializer.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.server.api.ODataResponse;
import org.junit.Test;

public class BatchLineReaderTest {

  private static final String TEXT_COMBINED = "Test\r" +
      "Test2\r\n" +
      "Test3\n" +
      "Test4\r" +
      "\r" +
      "\r\n" +
      "\r\n" +
      "Test5\n" +
      "Test6\r\n" +
      "Test7\n" +
      "\n";

  private static final String TEXT_EMPTY = "";
  private static final String LF = "\n";

  @Test
  public void testSimpleText() throws Exception {
    final String TEXT = "Test";
    BatchLineReader reader = create(TEXT);

    assertEquals(TEXT, reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testNoText() throws Exception {
    final String TEXT = "";
    BatchLineReader reader = create(TEXT);

    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testNoBytes() throws Exception {
    BatchLineReader reader =
        new BatchLineReader(new ByteArrayInputStream(new byte[0]));

    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testCRLF() throws Exception {
    final String TEXT = "Test\r\n" +
        "Test2";

    BatchLineReader reader = create(TEXT);

    assertEquals("Test\r\n", reader.readLine());
    assertEquals("Test2", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testLF() throws Exception {
    final String TEXT = "Test\n" +
        "Test2";

    BatchLineReader reader = create(TEXT);

    assertEquals("Test\n", reader.readLine());
    assertEquals("Test2", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testCR() throws Exception {
    final String TEXT = "Test\r" +
        "Test2";

    BatchLineReader reader = create(TEXT);

    assertEquals("Test\r", reader.readLine());
    assertEquals("Test2", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testCombined() throws Exception {
    BatchLineReader reader = create(TEXT_COMBINED);

    assertEquals("Test\r", reader.readLine());
    assertEquals("Test2\r\n", reader.readLine());
    assertEquals("Test3\n", reader.readLine());
    assertEquals("Test4\r", reader.readLine());
    assertEquals("\r", reader.readLine());
    assertEquals("\r\n", reader.readLine());
    assertEquals("\r\n", reader.readLine());
    assertEquals("Test5\n", reader.readLine());
    assertEquals("Test6\r\n", reader.readLine());
    assertEquals("Test7\n", reader.readLine());
    assertEquals("\n", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testCombinedBufferSizeTwo() throws Exception {
    BatchLineReader reader = create(TEXT_COMBINED, 2);

    assertEquals("Test\r", reader.readLine());
    assertEquals("Test2\r\n", reader.readLine());
    assertEquals("Test3\n", reader.readLine());
    assertEquals("Test4\r", reader.readLine());
    assertEquals("\r", reader.readLine());
    assertEquals("\r\n", reader.readLine());
    assertEquals("\r\n", reader.readLine());
    assertEquals("Test5\n", reader.readLine());
    assertEquals("Test6\r\n", reader.readLine());
    assertEquals("Test7\n", reader.readLine());
    assertEquals("\n", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testCombinedBufferSizeOne() throws Exception {
    final String TEXT = "Test\r" +
        "Test2\r\n" +
        "Test3\n" +
        "Test4\r" +
        "\r" +
        "\r\n" +
        "\r\n" +
        "Test5\n" +
        "Test6\r\n" +
        "Test7\n" +
        "\r\n";

    BatchLineReader reader = create(TEXT, 1);

    assertEquals("Test\r", reader.readLine());
    assertEquals("Test2\r\n", reader.readLine());
    assertEquals("Test3\n", reader.readLine());
    assertEquals("Test4\r", reader.readLine());
    assertEquals("\r", reader.readLine());
    assertEquals("\r\n", reader.readLine());
    assertEquals("\r\n", reader.readLine());
    assertEquals("Test5\n", reader.readLine());
    assertEquals("Test6\r\n", reader.readLine());
    assertEquals("Test7\n", reader.readLine());
    assertEquals("\r\n", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());

    reader.close();
  }

  @Test
  public void testDoubleLF() throws Exception {
    final String TEXT = "Test\r" +
        "\r";

    BatchLineReader reader = create(TEXT, 1);

    assertEquals("Test\r", reader.readLine());
    assertEquals("\r", reader.readLine());
    reader.close();
  }

  @Test
  public void testReadMoreBufferCapacityThanCharacterAvailable() throws Exception {
    final String TEXT = "Foo";
    byte[] buffer = new byte[20];

    BatchLineReader reader = create(TEXT);
    assertEquals(3, reader.read(buffer, 0, 20));
    assertEquals(-1, reader.read(buffer, 0, 20));
    reader.close();

    BatchLineReader readerBufferOne = create(TEXT, 1);
    assertEquals(3, readerBufferOne.read(buffer, 0, 20));
    assertEquals(-1, readerBufferOne.read(buffer, 0, 20));
    readerBufferOne.close();
  }

  @Test
  public void testLineEqualsAndHashCode() {
    Line l1 = new Line("The first line", 1);
    Line l2 = new Line("The first line", 1);
    Line l3 = new Line("The second line", 2);

    assertEquals(l1, l2);
    assertFalse(l1.equals(l3));
    assertTrue(l1.hashCode() != l3.hashCode());
  }

  @Test
  public void testToList() throws Exception {
    BatchLineReader reader = create(TEXT_COMBINED);
    List<String> stringList = reader.toList();

    assertEquals(11, stringList.size());
    assertEquals("Test\r", stringList.get(0));
    assertEquals("Test2\r\n", stringList.get(1));
    assertEquals("Test3\n", stringList.get(2));
    assertEquals("Test4\r", stringList.get(3));
    assertEquals("\r", stringList.get(4));
    assertEquals("\r\n", stringList.get(5));
    assertEquals("\r\n", stringList.get(6));
    assertEquals("Test5\n", stringList.get(7));
    assertEquals("Test6\r\n", stringList.get(8));
    assertEquals("Test7\n", stringList.get(9));
    assertEquals("\n", stringList.get(10));
    reader.close();
  }

  @Test
  public void testToLineList() throws Exception {
    BatchLineReader reader = create(TEXT_COMBINED);
    List<Line> stringList = reader.toLineList();

    assertEquals(11, stringList.size());
    assertEquals("Test\r", stringList.get(0).toString());
    assertEquals("Test2\r\n", stringList.get(1).toString());
    assertEquals("Test3\n", stringList.get(2).toString());
    assertEquals("Test4\r", stringList.get(3).toString());
    assertEquals("\r", stringList.get(4).toString());
    assertEquals("\r\n", stringList.get(5).toString());
    assertEquals("\r\n", stringList.get(6).toString());
    assertEquals("Test5\n", stringList.get(7).toString());
    assertEquals("Test6\r\n", stringList.get(8).toString());
    assertEquals("Test7\n", stringList.get(9).toString());
    assertEquals("\n", stringList.get(10).toString());
    reader.close();
  }

  @Test
  public void testBatchContent() throws Exception {
    String batchContent = getFileContent("/batchLarge.batch", "utf-8");
    BatchLineReader reader = create(batchContent);

    List<String> lines = reader.toList();
    assertEquals(2422, lines.size());
    assertEquals("--batch_8194-cf13-1f56\n", lines.get(0));
    assertEquals("              <d:City m:type=\"RefScenario.c_City\">\n", lines.get(1402));
    assertEquals("\n", lines.get(1903));
    assertEquals("--batch_8194-cf13-1f56--", lines.get(2421));
  }

  private String getFileContent(String fileName, String charset) throws IOException {
    byte[] content = getFileContent(fileName);
    return new String(content, Charset.forName(charset));
  }

  private byte[] getFileContent(String fileName) throws IOException {
    final InputStream input = ClassLoader.class.getResourceAsStream(fileName);
    if (input == null) {
      throw new IOException("Requested file '" + fileName + "' was not found.");
    }
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      ByteBuffer inBuffer = ByteBuffer.allocate(8192);
      ReadableByteChannel ic = Channels.newChannel(input);
      WritableByteChannel oc = Channels.newChannel(output);
      while (ic.read(inBuffer) > 0) {
        inBuffer.flip();
        oc.write(inBuffer);
        inBuffer.rewind();
      }
      return output.toByteArray();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailBufferSizeZero() throws Exception {
    BatchLineReader reader = create(TEXT_EMPTY, 0);
    reader.close();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailBufferSizeNegative() throws Exception {
    BatchLineReader reader = create(TEXT_EMPTY, -1);
    reader.close();
  }

  private BatchLineReader create(final String inputString) throws Exception {
    return new BatchLineReader(new ByteArrayInputStream(inputString
        .getBytes("UTF-8")));
  }

  private BatchLineReader create(final String inputString, final int bufferSize) throws Exception {
    return new BatchLineReader(new ByteArrayInputStream(inputString
        .getBytes("UTF-8")), bufferSize);
  }

}
