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
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class AtomEntryImpl extends AbstractEntry implements AtomObject {

  private static final long serialVersionUID = 6973729343868293279L;

  public static class Author {

    private String name;

    private String uri;

    private String email;

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public String getUri() {
      return uri;
    }

    public void setUri(final String uri) {
      this.uri = uri;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(final String email) {
      this.email = email;
    }

    public boolean isEmpty() {
      return StringUtils.isBlank(name) && StringUtils.isBlank(uri) && StringUtils.isBlank(email);
    }
  }
  private URI baseURI;

  private String title;

  private String summary;

  private Date updated;

  private Author author;

  @Override
  public void setBaseURI(final String baseURI) {
    this.baseURI = URI.create(baseURI);
  }

  @Override
  public URI getBaseURI() {
    return baseURI;
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
    return updated == null ? null : new Date(updated.getTime());
  }

  @Override
  public void setUpdated(final Date updated) {
    this.updated = new Date(updated.getTime());
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(final Author author) {
    this.author = author;
  }
}
