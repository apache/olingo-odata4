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
package org.apache.olingo.server.core.debug;

import java.io.IOException;
import java.io.Writer;

/**
 * Writes JSON output.
 * 
 */
class JsonStreamWriter {
  private final Writer writer;

  public JsonStreamWriter(final Writer writer) {
    this.writer = writer;
  }

  public JsonStreamWriter beginObject() throws IOException {
    writer.append('{');
    return this;
  }

  public JsonStreamWriter endObject() throws IOException {
    writer.append('}');
    return this;
  }

  public JsonStreamWriter beginArray() throws IOException {
    writer.append('[');
    return this;
  }

  public JsonStreamWriter endArray() throws IOException {
    writer.append(']');
    return this;
  }

  public JsonStreamWriter name(final String name) throws IOException {
    writer.append('"').append(name).append('"').append(':');
    return this;
  }

  public JsonStreamWriter unquotedValue(final String value) throws IOException {
    writer.append(value == null ? "null" : value);
    return this;
  }

  public JsonStreamWriter stringValueRaw(final String value) throws IOException {
    if (value == null) {
      writer.append("null");
    } else {
      writer.append('"').append(value).append('"');
    }
    return this;
  }

  public JsonStreamWriter stringValue(final String value) throws IOException {
    if (value == null) {
      writer.append("null");
    } else {
      writer.append('"');
      escape(value);
      writer.append('"');
    }
    return this;
  }

  public JsonStreamWriter namedStringValueRaw(final String name, final String value) throws IOException {
    name(name);
    stringValueRaw(value);
    return this;
  }

  public JsonStreamWriter namedStringValue(final String name, final String value) throws IOException {
    name(name);
    stringValue(value);
    return this;
  }

  public JsonStreamWriter separator() throws IOException {
    writer.append(',');
    return this;
  }

  /**
   * Writes the JSON-escaped form of a Java String value according to RFC 4627.
   * @param value the Java String
   * @throws IOException if an I/O error occurs
   */
  protected void escape(final String value) throws IOException {
    // RFC 4627 says: "All Unicode characters may be placed within the
    // quotation marks except for the characters that must be escaped:
    // quotation mark, reverse solidus, and the control characters
    // (U+0000 through U+001F)."
    // All output here is done on character basis which should be faster
    // than writing Strings.
    for (int i = 0; i < value.length(); i++) {
      final char c = value.charAt(i);
      switch (c) {
      case '\\':
        writer.append('\\').append(c);
        break;
      case '"':
        writer.append('\\').append(c);
        break;
      case '\b':
        writer.append('\\').append('b');
        break;
      case '\t':
        writer.append('\\').append('t');
        break;
      case '\n':
        writer.append('\\').append('n');
        break;
      case '\f':
        writer.append('\\').append('f');
        break;
      case '\r':
        writer.append('\\').append('r');
        break;
      case '\u0000':
      case '\u0001':
      case '\u0002':
      case '\u0003':
      case '\u0004':
      case '\u0005':
      case '\u0006':
      case '\u0007':
      case '\u000B':
      case '\u000E':
      case '\u000F':
      case '\u0010':
      case '\u0011':
      case '\u0012':
      case '\u0013':
      case '\u0014':
      case '\u0015':
      case '\u0016':
      case '\u0017':
      case '\u0018':
      case '\u0019':
      case '\u001A':
      case '\u001B':
      case '\u001C':
      case '\u001D':
      case '\u001E':
      case '\u001F':
        final int lastHexDigit = c % 0x10;
        writer.append('\\').append('u').append('0').append('0')
            .append(c >= '\u0010' ? '1' : '0')
            .append((char) ((lastHexDigit > 9 ? 'A' : '0') + lastHexDigit % 10));
        break;
      default:
        writer.append(c);
      }
    }
  }
}
