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
package org.apache.olingo.commons.api.data;

import java.net.URI;
import java.text.ParseException;

public abstract class AbstractODataObject extends Annotatable {

  private URI baseURI;
  private URI id;
  private String title;

  /**
   * Gets base URI.
   * 
   * @return base URI.
   */
  public URI getBaseURI() {
    return baseURI;
  }

  public void setBaseURI(final String baseURI) {
    this.baseURI = baseURI == null ? null : URI.create(baseURI);
  }


  /**
   * Gest ID.
   * 
   * @return ID.
   */
  public URI getId() {
    return id;
  }

  public void setId(final URI id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setCommonProperty(final String key, final String value) throws ParseException {
    if ("id".equals(key)) {
      id = URI.create(value);
    } else if ("title".equals(key)) {
      title = value;
    }
  }
}
