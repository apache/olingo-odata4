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
package org.apache.olingo.server.api;

import java.util.Collection;

/**
 * Information about the values of an ETag-relevant HTTP header.
 */
public class EtagInformation {
  private final boolean all;
  private final Collection<String> etags;

  public EtagInformation(final boolean all, final Collection<String> etags) {
    this.all = all;
    this.etags = etags;
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
  public Collection<String> getEtags() {
    return etags;
  }

  /**
   * <p>Checks whether a given ETag value is matched by this ETag information.</p>
   * <p>If the given value is <code>null</code>, or if this ETag information
   * does not contain anything, the result is <code>false</code>.</p>
   * @param etag the ETag value to match
   * @return a boolean match result
   */
  public boolean isMatchedBy(final String etag) {
    if (etag == null) {
      return false;
    } else if (all) {
      return true;
    } else {
      for (final String candidate : etags) {
        if ((etag.startsWith("W/") ? etag.substring(2) : etag)
            .equals(candidate.startsWith("W/") ? candidate.substring(2) : candidate)) {
          return true;
        }
      }
      return false;
    }
  }
}
