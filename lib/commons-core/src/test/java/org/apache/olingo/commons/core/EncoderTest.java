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
package org.apache.olingo.commons.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 * Tests for percent-encoding.
 *
 */
public class EncoderTest {

  private final static String RFC3986_UNRESERVED = "-._~"; // + ALPHA + DIGIT
  private final static String RFC3986_GEN_DELIMS = ":/?#[]@";
  private final static String RFC3986_SUB_DELIMS = "!$&'()*+,;=";
  private final static String RFC3986_RESERVED = RFC3986_GEN_DELIMS + RFC3986_SUB_DELIMS;

  @Test
  public void asciiCharacters() {
    final String s = "azAZ019";
    assertEquals(s, Encoder.encode(s));
    assertEquals(s, Encoder.encode(s));
  }

  @Test
  public void asciiControl() {
    assertEquals("%08%09%0A%0D", Encoder.encode("\b\t\n\r"));
  }

  @Test
  public void unsafe() {
    assertEquals("%3C%3E%25%26", Encoder.encode("<>%&"));
    assertEquals("%22%5C%60%7B%7D%7C", Encoder.encode("\"\\`{}|"));
  }

  @Test
  public void rfc3986Unreserved() {
    assertEquals(RFC3986_UNRESERVED, Encoder.encode(RFC3986_UNRESERVED));
  }

  @Test
  public void rfc3986GenDelims() {
    assertEquals("%3A%2F%3F%23%5B%5D%40", Encoder.encode(RFC3986_GEN_DELIMS));
  }

  @Test
  public void rfc3986SubDelims() {
    assertEquals("%21%24%26'%28%29%2A%2B%2C%3B%3D", Encoder.encode(RFC3986_SUB_DELIMS));
  }

  @Test
  public void rfc3986Reserved() {
    assertEquals("%3A%2F%3F%23%5B%5D%40%21%24%26'%28%29%2A%2B%2C%3B%3D", Encoder.encode(RFC3986_RESERVED));
  }

  @Test
  public void unicodeCharacters() {
    assertEquals("%E2%82%AC", Encoder.encode("€"));
    assertEquals("%EF%B7%BC", Encoder.encode("\uFDFC")); // RIAL SIGN
  }

  @Test
  public void charactersOutsideBmp() {
    // Unicode characters outside the Basic Multilingual Plane are stored
    // in a Java String in two surrogate characters.
    final String s = String.valueOf(Character.toChars(0x1F603));
    assertEquals("%F0%9F%98%83", Encoder.encode(s));
  }

  @Test
  public void uriDecoding() throws URISyntaxException {
    final String decodedValue = RFC3986_UNRESERVED + RFC3986_RESERVED + "0..1..a..z..A..Z..@"
        + "\u2323\uFDFC" + String.valueOf(Character.toChars(0x1F603));

    final String encodedPath = Encoder.encode(decodedValue) + "/" + Encoder.encode(decodedValue);
    final String encodedQuery = Encoder.encode(decodedValue);
    final URI uri = new URI("http://host:80/" + encodedPath + "?" + encodedQuery + "=" + encodedQuery);

    assertEquals(uri.getPath(), "/" + decodedValue + "/" + decodedValue);
    assertEquals(uri.getQuery(), decodedValue + "=" + decodedValue);

    assertEquals(uri.getRawPath(), "/" + encodedPath);
    assertEquals(uri.getRawQuery(), encodedQuery + "=" + encodedQuery);
  }
}
