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
package org.apache.olingo.commons.core.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.commons.api.domain.AbstractODataPayload;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;

public class ODataEntitySetImpl extends AbstractODataPayload implements ODataEntitySet {

  private static final long serialVersionUID = 9039605899821494024L;

  /**
   * Link to the next page.
   */
  private URI next;

  /**
   * Number of ODataEntities contained in this feed. If <tt>$inlinecount</tt> was requested, this value comes from
   * there.
   */
  private Integer count;

  /**
   * OData entities contained in this feed.
   */
  private List<ODataEntity> entities = new ArrayList<ODataEntity>();

  /**
   * Constructor.
   */
  public ODataEntitySetImpl() {
    super(null);
  }

  /**
   * Constructor.
   *
   * @param next next link.
   */
  public ODataEntitySetImpl(final URI next) {
    super(null);
    this.next = next;
  }

  /**
   * Gets next page link.
   *
   * @return next page link; null value if single page or last page reached.
   */
  @Override
  public URI getNext() {
    return next;
  }

  /**
   * Gets contained entities.
   *
   * @return feed entries.
   */
  @Override
  public List<ODataEntity> getEntities() {
    return entities;
  }

  /**
   * Gets in-line count.
   *
   * @return in-line count value.
   */
  @Override
  public int getCount() {
    return count == null ? entities.size() : count;
  }

  /**
   * Sets in-line count.
   *
   * @param count in-line count value.
   */
  @Override
  public void setCount(final int count) {
    this.count = count;
  }
}
