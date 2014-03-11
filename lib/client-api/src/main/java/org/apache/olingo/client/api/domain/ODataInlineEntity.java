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
package org.apache.olingo.client.api.domain;

import java.net.URI;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.data.LinkType;

/**
 * OData in-line entity.
 */
public class ODataInlineEntity extends ODataLink {

  private static final long serialVersionUID = -4763341581843700743L;

  private final ODataEntity entity;

  /**
   * Constructor.
   *
   * @param uri edit link.
   * @param type type.
   * @param title title.
   * @param entity entity.
   */
  public ODataInlineEntity(final ODataClient client,
          final URI uri, final LinkType type, final String title, final ODataEntity entity) {

    super(client, uri, type, title);
    this.entity = entity;
  }

  /**
   * Constructor.
   *
   * @param baseURI base URI.
   * @param href href.
   * @param type type.
   * @param title title.
   * @param entity entity.
   */
  public ODataInlineEntity(final ODataClient client, final URI baseURI, final String href, final LinkType type,
          final String title, final ODataEntity entity) {

    super(client, baseURI, href, type, title);
    this.entity = entity;
  }

  /**
   * Gets wrapped entity.
   *
   * @return wrapped entity.
   */
  public ODataEntity getEntity() {
    return entity;
  }
}
