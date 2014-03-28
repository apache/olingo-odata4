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
package org.apache.olingo.commons.core.data;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;

abstract class AbstractODataObject extends AbstractPayloadObject {

  private static final long serialVersionUID = -4391162864875546927L;

  private static final ISO8601DateFormat ISO_DATEFORMAT = new ISO8601DateFormat();

  private URI baseURI;

  private String id;

  private String title;

  private String summary;

  private Date updated;

  public URI getBaseURI() {
    return baseURI;
  }

  public void setBaseURI(final String baseURI) {
    this.baseURI = URI.create(baseURI);
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public String getSummary() {
    return summary;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setCommonProperty(final String key, final String value) throws ParseException {
    if ("id".equals(key)) {
      this.id = value;
    } else if ("title".equals(key)) {
      this.title = value;
    } else if ("summary".equals(key)) {
      this.summary = value;
    } else if ("updated".equals(key)) {
      this.updated = ISO_DATEFORMAT.parse(value);
    }
  }
}
