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
package org.apache.olingo.commons.api.domain;

import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import java.net.URI;

/**
 * OData in-line entity set.
 */
public class ODataInlineEntitySet extends ODataLink {

  private CommonODataEntitySet entitySet;

  /**
   * Constructor.
   * 
   * @param version OData service version.
   * @param uri edit link.
   * @param type type.
   * @param title title.
   * @param entitySet entity set.
   */
  public ODataInlineEntitySet(final ODataServiceVersion version, final URI uri, final ODataLinkType type,
      final String title, final CommonODataEntitySet entitySet) {

    super(version, uri, type, title);
    this.entitySet = entitySet;
  }

  /**
   * Constructor.
   * 
   * @param version OData service version.
   * @param baseURI base URI.
   * @param href href.
   * @param type type.
   * @param title title.
   * @param entitySet entity set.
   */
  public ODataInlineEntitySet(final ODataServiceVersion version, final URI baseURI, final String href,
      final ODataLinkType type, final String title, final CommonODataEntitySet entitySet) {

    super(version, baseURI, href, type, title);
    this.entitySet = entitySet;
  }

  /**
   * Gets wrapped entity set.
   * 
   * @return wrapped entity set.
   */
  public CommonODataEntitySet getEntitySet() {
    return entitySet;
  }
}
