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
package org.apache.olingo.server.core.etag;

import java.util.Collection;

/**
 * Information about the values of an ETag-relevant HTTP header.
 */
public class ETagInformation {
  private final boolean all;
  private final Collection<String> eTags;

  public ETagInformation(final boolean all, final Collection<String> eTags) {
    this.all = all;
    this.eTags = eTags;
  }

  /**
   * Gets the information whether the values contain "*".
   */
  public boolean isAll() {
    return all;
  }

  /**
   * Gets the collection of ETag values found.
   * It is empty if {@link #isAll()} returns <code>true</code>.
   */
  public Collection<String> getETags() {
    return eTags;
  }

  /**
   * <p>Checks whether a given ETag value is matched by this ETag information,
   * using weak comparison as described in
   * <a href="https://www.ietf.org/rfc/rfc7232.txt">RFC 7232</a>, section 2.3.2.</p>
   * <p>If the given value is <code>null</code>, or if this ETag information
   * does not contain anything, the result is <code>false</code>.</p>
   * @param eTag the ETag value to match
   * @return a boolean match result
   */
  public boolean isMatchedBy(final String eTag) {
    if (eTag == null) {
      return false;
    } else if (all) {
      return true;
    } else {
      for (final String candidate : eTags) {
        if ((eTag.startsWith("W/") ? eTag.substring(2) : eTag)
            .equals(candidate.startsWith("W/") ? candidate.substring(2) : candidate)) {
          return true;
        }
      }
      return false;
    }
  }
}