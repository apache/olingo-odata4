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
package org.apache.olingo.fit.utils;

import java.io.InputStream;

public class LinkInfo {

  private final InputStream links;

  private boolean feed = false;

  private String etag;

  public LinkInfo(final InputStream links) {
    this.links = links;
  }

  public boolean isFeed() {
    return feed;
  }

  public void setFeed(final boolean feed) {
    this.feed = feed;
  }

  public String getEtag() {
    return etag;
  }

  public void setEtag(final String etag) {
    this.etag = etag;
  }

  public InputStream getLinks() {
    return links;
  }
}
