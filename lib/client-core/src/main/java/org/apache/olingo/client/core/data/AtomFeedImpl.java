/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.olingo.client.api.data.Entry;
import org.apache.olingo.client.api.data.Feed;

/**
 * List of entries, represented via Atom.
 *
 * @see AtomEntry
 */
public class AtomFeedImpl extends AbstractPayloadObject implements AtomObject, Feed {

  private static final long serialVersionUID = 5466590540021319153L;

  private URI baseURI;

  private String id;

  private String title;

  private String summary;

  private Date updated;

  private Integer count;

  private final List<Entry> entries = new ArrayList<Entry>();

  private URI next;

  @Override
  public URI getBaseURI() {
    return baseURI;
  }

  @Override
  public void setBaseURI(final String baseURI) {
    this.baseURI = URI.create(baseURI);
  }

  public String getId() {
    return id;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(final String title) {
    this.title = title;
  }

  public String getSummary() {
    return summary;
  }

  @Override
  public void setSummary(final String summary) {
    this.summary = summary;
  }

  public Date getUpdated() {
    return new Date(updated.getTime());
  }

  @Override
  public void setUpdated(final Date updated) {
    this.updated = new Date(updated.getTime());
  }

  public void setCount(final Integer count) {
    this.count = count;
  }

  @Override
  public Integer getCount() {
    return count;
  }

  @Override
  public List<Entry> getEntries() {
    return entries;
  }

  @Override
  public void setNext(final URI next) {
    this.next = next;
  }

  @Override
  public URI getNext() {
    return next;
  }
}
