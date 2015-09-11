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

import java.io.UnsupportedEncodingException;

/**
 * Encodes a Java String (in its internal UTF-16 encoding) into its
 * percent-encoded UTF-8 representation according to
 * <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>
 * (with consideration of its predecessor RFC 2396).
 *
 */
public class Encoder {

  /**
   * Encodes a Java String (in its internal UTF-16 encoding) into its
   * percent-encoded UTF-8 representation according to
   * <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>,
   * suitable for parts of an OData path segment.
   * @param value the Java String
   * @return the encoded String
   */
  public static String encode(final String value) {
    return encoder.encodeInternal(value);
  }

  // OData has special handling for "'", so we allow that to remain unencoded.
  // Other sub-delims not used neither by JAX-RS nor by OData could be added
  // if the encoding is considered to be too aggressive.
  // RFC 3986 would also allow the gen-delims ":" and "@" to appear literally
  // in path-segment parts.
  private static final String ODATA_UNENCODED = "'";

  // Character classes from RFC 3986
  private final static String UNRESERVED = "-._~"; // + ALPHA + DIGIT
  // RFC 3986 says: "For consistency, URI producers and normalizers should
  // use uppercase hexadecimal digits for all percent-encodings."
  private final static String[] hex = { "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07", "%08", "%09", "%0A",
    "%0B", "%0C", "%0D", "%0E", "%0F", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17", "%18", "%19", "%1A",
    "%1B", "%1C", "%1D", "%1E", "%1F", "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2A",
    "%2B", "%2C", "%2D", "%2E", "%2F", "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37", "%38", "%39", "%3A",
    "%3B", "%3C", "%3D", "%3E", "%3F", "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47", "%48", "%49", "%4A",
    "%4B", "%4C", "%4D", "%4E", "%4F", "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57", "%58", "%59", "%5A",
    "%5B", "%5C", "%5D", "%5E", "%5F", "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67", "%68", "%69", "%6A",
    "%6B", "%6C", "%6D", "%6E", "%6F", "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77", "%78", "%79", "%7A",
    "%7B", "%7C", "%7D", "%7E", "%7F", "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87", "%88",
    "%89", "%8A", "%8B", "%8C", "%8D", "%8E", "%8F", "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97", "%98",
    "%99", "%9A", "%9B", "%9C", "%9D", "%9E", "%9F", "%A0", "%A1", "%A2", "%A3", "%A4", "%A5", "%A6", "%A7", "%A8",
    "%A9", "%AA", "%AB", "%AC", "%AD", "%AE", "%AF", "%B0", "%B1", "%B2", "%B3", "%B4", "%B5", "%B6", "%B7", "%B8",
    "%B9", "%BA", "%BB", "%BC", "%BD", "%BE", "%BF", "%C0", "%C1", "%C2", "%C3", "%C4", "%C5", "%C6", "%C7", "%C8",
    "%C9", "%CA", "%CB", "%CC", "%CD", "%CE", "%CF", "%D0", "%D1", "%D2", "%D3", "%D4", "%D5", "%D6", "%D7", "%D8",
    "%D9", "%DA", "%DB", "%DC", "%DD", "%DE", "%DF", "%E0", "%E1", "%E2", "%E3", "%E4", "%E5", "%E6", "%E7", "%E8",
    "%E9", "%EA", "%EB", "%EC", "%ED", "%EE", "%EF", "%F0", "%F1", "%F2", "%F3", "%F4", "%F5", "%F6", "%F7", "%F8",
    "%F9", "%FA", "%FB", "%FC", "%FD", "%FE", "%FF" };

  private static final Encoder encoder = new Encoder(ODATA_UNENCODED);

  /** characters to remain unencoded in addition to {@link #UNRESERVED} */
  private final String unencoded;

  private Encoder(final String unencoded) {
    this.unencoded = unencoded == null ? "" : unencoded;
  }

  /**
   * <p>Returns the percent-encoded UTF-8 representation of a String.</p>
   * <p>In order to avoid producing percent-encoded CESU-8 (as described in
   * the Unicode Consortium's <a href="http://www.unicode.org/reports/tr26/">
   * Technical Report #26</a>), this is done in two steps:
   * <ol>
   * <li>Re-encode the characters from their Java-internal UTF-16 representations
   * into their UTF-8 representations.</li>
   * <li>Percent-encode each of the bytes in the UTF-8 representation.
   * This is possible on byte level because all characters that do not have
   * a <code>%xx</code> representation are represented in one byte in UTF-8.</li>
   * </ol></p>
   * @param input input String
   * @return encoded representation
   */
  private String encodeInternal(final String input) {
    StringBuilder resultStr = new StringBuilder();

    try {
      for (byte utf8Byte : input.getBytes("UTF-8")) {
        final char character = (char) utf8Byte;
        if (isUnreserved(character)) {
          resultStr.append(character);
        } else if (isUnencoded(character)) {
          resultStr.append(character);
        } else if (utf8Byte >= 0) {
          resultStr.append(hex[utf8Byte]);
        } else {
          // case UTF-8 continuation byte
          resultStr.append(hex[256 + utf8Byte]); // index adjusted for the usage of signed bytes
        }
      }
    } catch (final UnsupportedEncodingException e) { // should never happen; UTF-8 is always there
      return null;
    }
    return resultStr.toString();
  }

  private static boolean isUnreserved(final char character) {
    return 'A' <= character && character <= 'Z' // case A..Z
        || 'a' <= character && character <= 'z' // case a..z
        || '0' <= character && character <= '9' // case 0..9
        || UNRESERVED.indexOf(character) >= 0;
  }

  private boolean isUnencoded(final char character) {
    return unencoded.indexOf(character) >= 0;
  }
}
