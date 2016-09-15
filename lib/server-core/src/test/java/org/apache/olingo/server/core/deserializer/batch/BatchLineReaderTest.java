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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

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

  @Test
  public void simpleText() throws Exception {
    final String TEXT = "Test";
    BatchLineReader reader = create(TEXT);

    assertEquals(TEXT, reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void noText() throws Exception {
    BatchLineReader reader = create(TEXT_EMPTY);

    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void noBytes() throws Exception {
    BatchLineReader reader = new BatchLineReader(new ByteArrayInputStream(new byte[0]));

    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void CRLF() throws Exception {
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
  public void LF() throws Exception {
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
  public void CR() throws Exception {
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
  public void combined() throws Exception {
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
  public void combinedBufferSizeTwo() throws Exception {
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
  public void combinedBufferSizeOne() throws Exception {
    BatchLineReader reader = create(TEXT_COMBINED, 1);

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
  public void doubleCR() throws Exception {
    final String TEXT = "Test\r" +
        "\r";

    BatchLineReader reader = create(TEXT, 1);

    assertEquals("Test\r", reader.readLine());
    assertEquals("\r", reader.readLine());
    reader.close();
  }

  @Test
  public void lineEqualsAndHashCode() {
    Line l1 = new Line("The first line", 1);
    Line l2 = new Line("The first line", 1);
    Line l3 = new Line("The second line", 2);

    assertEquals(l1, l2);
    assertFalse(l1.equals(l3));
    assertTrue(l1.hashCode() != l3.hashCode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void failBufferSizeZero() throws Exception {
    BatchLineReader reader = create(TEXT_EMPTY, 0);
    reader.close();
  }

  @Test(expected = IllegalArgumentException.class)
  public void failBufferSizeNegative() throws Exception {
    BatchLineReader reader = create(TEXT_EMPTY, -1);
    reader.close();
  }

  @Test
  public void toList() throws Exception {
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
  public void specialCharacters() throws Exception {
    final String text = "\r\n"
        + "Content-Type: text/plain; charset=UTF-8\r\n"
        + "\r\n"
        + "ä€\r\n"
        + "\uFDFC\r\n"  // RIAL SIGN
        // Unicode characters outside the Basic Multilingual Plane are stored
        // in a Java String in two surrogate characters.
        + String.valueOf(Character.toChars(0x1F603));
    BatchLineReader reader = create(text);
    reader.readLine();
    reader.readLine();
    reader.readLine();
    assertEquals("ä€\r\n", reader.readLine());
    assertEquals("\uFDFC\r\n", reader.readLine());
    assertEquals(String.valueOf(Character.toChars(0x1F603)), reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void rawBytes() throws Exception {
    byte[] content = new byte[Byte.MAX_VALUE - Byte.MIN_VALUE + 1];
    // binary content, not a valid UTF-8 representation of a string
    for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
      content[i - Byte.MIN_VALUE] = (byte) i;
    }
    BatchLineReader reader = new BatchLineReader(new ByteArrayInputStream(content));
    final String contentString = reader.readLine()  // initial part up to '\n'
        + reader.readLine()  // second part from '\n' to '\r'
        + reader.readLine();  // the rest
    assertArrayEquals(content, contentString.getBytes(Charset.forName("ISO-8859-1")));
    assertNull(reader.readLine());
    reader.close();
  }

  private BatchLineReader create(final String inputString) throws IOException {
    return new BatchLineReader(new ByteArrayInputStream(inputString.getBytes("UTF-8")));
  }

  private BatchLineReader create(final String inputString, final int bufferSize) throws IOException {
    return new BatchLineReader(new ByteArrayInputStream(inputString.getBytes("UTF-8")), bufferSize);
  }
}
