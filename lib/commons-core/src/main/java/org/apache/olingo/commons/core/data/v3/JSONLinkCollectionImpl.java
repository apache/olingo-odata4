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
package org.apache.olingo.commons.core.data.v3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.commons.api.data.v3.LinkCollection;
import org.apache.olingo.commons.core.data.AbstractPayloadObject;

/**
 * Link from an entity, represented via JSON.
 */
public class JSONLinkCollectionImpl extends AbstractPayloadObject implements LinkCollection {

  private static final long serialVersionUID = -5006368367235783907L;

  /**
   * JSON link URL representation.
   */
  static class JSONLinkURL extends AbstractPayloadObject {

    private static final long serialVersionUID = 5365055617973271468L;

    private URI url;

    public URI getUrl() {
      return url;
    }

    public void setUrl(final URI url) {
      this.url = url;
    }
  }

  @JsonProperty(value = "odata.metadata", required = false)
  private URI metadata;

  @JsonProperty(required = false)
  private URI url;

  @JsonProperty(value = "value", required = false)
  private final List<JSONLinkURL> links = new ArrayList<JSONLinkURL>();

  @JsonProperty(value = "odata.nextLink", required = false)
  private String next;

  /**
   * Gets the metadata URI.
   */
  public URI getMetadata() {
    return metadata;
  }

  /**
   * Sets the metadata URI.
   *
   * @param metadata metadata URI.
   */
  public void setMetadata(final URI metadata) {
    this.metadata = metadata;
  }

  /**
   * {@inheritDoc }
   */
  @JsonIgnore
  @Override
  public List<URI> getLinks() {
    final List<URI> result = new ArrayList<URI>();

    if (this.url == null) {
      for (JSONLinkURL link : links) {
        result.add(link.getUrl());
      }
    } else {
      result.add(this.url);
    }

    return result;
  }

  /**
   * {@inheritDoc }
   */
  @JsonIgnore
  @Override
  public void setNext(final URI next) {
    this.next = next == null ? null : next.toASCIIString();
  }

  /**
   * {@inheritDoc }
   */
  @JsonIgnore
  @Override
  public URI getNext() {
    return next == null ? null : URI.create(next);
  }
}
