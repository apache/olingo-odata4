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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.olingo.server.core.deserializer.batch.BufferedReaderIncludingLineEndings;
import org.apache.olingo.server.core.deserializer.batch.Line;
import org.junit.Test;

public class BufferedReaderIncludingLineEndingsTest {


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

  private static final String TEXT_SMALL = "Test\r" +
      "123";
  private static final String TEXT_EMPTY = "";

  @Test
  public void testSimpleText() throws Exception {
    final String TEXT = "Test";
    BufferedReaderIncludingLineEndings reader = create(TEXT);

    assertEquals(TEXT, reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testNoText() throws Exception {
    final String TEXT = "";
    BufferedReaderIncludingLineEndings reader = create(TEXT);

    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testNoBytes() throws Exception {
    BufferedReaderIncludingLineEndings reader =
        new BufferedReaderIncludingLineEndings(new InputStreamReader(new ByteArrayInputStream(new byte[0])));

    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testCRLF() throws Exception {
    final String TEXT = "Test\r\n" +
        "Test2";

    BufferedReaderIncludingLineEndings reader = create(TEXT);

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

    BufferedReaderIncludingLineEndings reader = create(TEXT);

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

    BufferedReaderIncludingLineEndings reader = create(TEXT);

    assertEquals("Test\r", reader.readLine());
    assertEquals("Test2", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testCombined() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_COMBINED);

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
    BufferedReaderIncludingLineEndings reader = create(TEXT_COMBINED, 2);

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

    BufferedReaderIncludingLineEndings reader = create(TEXT, 1);

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

    BufferedReaderIncludingLineEndings reader = create(TEXT, 1);

    assertEquals("Test\r", reader.readLine());
    assertEquals("\r", reader.readLine());
    reader.close();
  }

  @Test
  public void testSkipSimple() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_SMALL);

    assertEquals(5, reader.skip(5)); // Test\r
    assertEquals("123", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testSkipBufferOne() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_SMALL, 1);

    assertEquals(5, reader.skip(5)); // Test\r
    assertEquals("123", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testReadThanSkip() throws Exception {
    final String TEXT = "Test\r" +
        "\r" +
        "123";

    BufferedReaderIncludingLineEndings reader = create(TEXT);

    assertEquals("Test\r", reader.readLine());
    assertEquals(1, reader.skip(1)); // Test\r
    assertEquals("123", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testReadMoreBufferCapacityThanCharacterAvailable() throws Exception {
    final String TEXT = "Foo";
    char[] buffer = new char[20];

    BufferedReaderIncludingLineEndings reader = create(TEXT);
    assertEquals(3, reader.read(buffer, 0, 20));
    assertEquals(-1, reader.read(buffer, 0, 20));
    reader.close();

    BufferedReaderIncludingLineEndings readerBufferOne = create(TEXT, 1);
    assertEquals(3, readerBufferOne.read(buffer, 0, 20));
    assertEquals(-1, readerBufferOne.read(buffer, 0, 20));
    readerBufferOne.close();
  }

  @Test
  public void testSkipZero() throws Exception {
    final String TEXT = "Test\r" +
        "123\r\n";

    BufferedReaderIncludingLineEndings reader = create(TEXT);

    assertEquals(0, reader.skip(0)); // Test\r
    assertEquals("Test\r", reader.readLine());
    assertEquals("123\r\n", reader.readLine());
    assertNull(reader.readLine());
    assertNull(reader.readLine());
    reader.close();
  }

  @Test
  public void testSkipToMuch() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_SMALL);

    assertEquals(8, reader.skip(10)); // Test\r
    assertEquals(null, reader.readLine());
    reader.close();
  }

  @Test
  public void testReadBufferOne() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_SMALL, 1);

    assertEquals('T', reader.read());
    assertEquals('e', reader.read());
    assertEquals('s', reader.read());
    assertEquals('t', reader.read());
    assertEquals('\r', reader.read());
    assertEquals('1', reader.read());
    assertEquals('2', reader.read());
    assertEquals('3', reader.read());
    assertEquals(-1, reader.read());
    assertEquals(-1, reader.read());
  }

  @Test
  public void testReadZeroBytes() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_SMALL, 1);

    char[] buffer = new char[3];
    assertEquals(0, reader.read(buffer, 0, 0));
    assertEquals('T', reader.read());
    assertEquals(0, reader.read(buffer, 0, 0));
    assertEquals("est\r", reader.readLine());
    assertEquals("123", reader.readLine());

    reader.close();
  }

  @Test
  public void testRead() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_SMALL);

    assertEquals('T', reader.read());
    assertEquals('e', reader.read());
    assertEquals('s', reader.read());
    assertEquals('t', reader.read());
    assertEquals('\r', reader.read());
    assertEquals('1', reader.read());
    assertEquals('2', reader.read());
    assertEquals('3', reader.read());
    assertEquals(-1, reader.read());
    assertEquals(-1, reader.read());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testFailReadBufferAndOffsetBiggerThanBuffer() throws Exception {
    BufferedReaderIncludingLineEndings reader = create("");

    final char[] buffer = new char[3];
    reader.read(buffer, 1, 3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testFailLengthNegative() throws Exception {
    final char[] buffer = new char[3];
    BufferedReaderIncludingLineEndings reader = create("123");

    reader.read(buffer, 1, -2);
    reader.close();
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testFailOffsetNegative() throws Exception {
    final char[] buffer = new char[3];
    BufferedReaderIncludingLineEndings reader = create("123");

    reader.read(buffer, -1, 2);
    reader.close();
  }

  @Test
  public void testReadAndReadLine() throws Exception {
    final String TEXT = "Test\r" +
        "bar\n" +
        "123\r\n" +
        "foo";

    BufferedReaderIncludingLineEndings reader = create(TEXT);

    assertEquals('T', reader.read());
    assertEquals('e', reader.read());
    assertEquals('s', reader.read());
    assertEquals('t', reader.read());
    assertEquals("\r", reader.readLine());
    assertEquals("bar\n", reader.readLine());
    assertEquals('1', reader.read());
    assertEquals('2', reader.read());
    assertEquals("3\r\n", reader.readLine());
    assertEquals("foo", reader.readLine());
    assertEquals(null, reader.readLine());
    assertEquals(-1, reader.read());
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
  
  @Test(expected = IllegalArgumentException.class)
  public void testSkipNegative() throws Exception {
    BufferedReaderIncludingLineEndings reader = create("123");
    reader.skip(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailBufferSizeZero() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_EMPTY, 0);
    reader.close();
  }

  @Test(expected = NullPointerException.class)
  public void testInputStreamIsNull() throws Exception {
    // Same behaviour like BufferedReader
    BufferedReaderIncludingLineEndings reader = new BufferedReaderIncludingLineEndings(null);
    reader.close();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailBufferSizeNegative() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_EMPTY, -1);
    reader.close();
  }

  @Test
  public void testMarkSupoorted() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_EMPTY);

    assertEquals(false, reader.markSupported());
    reader.close();
  }

  @Test(expected = Exception.class)
  public void testFailMark() throws Exception {
    BufferedReaderIncludingLineEndings reader = create("123");

    reader.mark(1);
  }

  @Test(expected = Exception.class)
  public void testFailReset() throws Exception {
    BufferedReaderIncludingLineEndings reader = create("123");

    reader.reset();
  }

  @Test
  public void testReady() throws Exception {
    BufferedReaderIncludingLineEndings reader = create("123\r123");
    assertEquals(false, reader.ready());
    assertEquals("123\r", reader.readLine());
    assertEquals(true, reader.ready());
    assertEquals("123", reader.readLine());
    assertEquals(false, reader.ready());

    reader.close();
  }

  @Test
  public void testToList() throws Exception {
    BufferedReaderIncludingLineEndings reader = create(TEXT_COMBINED);
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

  private BufferedReaderIncludingLineEndings create(final String inputString) throws Exception {
    return new BufferedReaderIncludingLineEndings(new InputStreamReader(new ByteArrayInputStream(inputString
        .getBytes("UTF-8"))));
  }

  private BufferedReaderIncludingLineEndings create(final String inputString, int bufferSize) throws Exception {
    return new BufferedReaderIncludingLineEndings(new InputStreamReader(new ByteArrayInputStream(inputString
        .getBytes("UTF-8"))), bufferSize);
  }

}
