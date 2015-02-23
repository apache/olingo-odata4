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
package org.apache.olingo.commons.core.domain;

import org.apache.olingo.commons.api.domain.AbstractODataPayload;
import org.apache.olingo.commons.api.domain.ODataEntitySet;

import java.net.URI;

public abstract class AbstractODataEntitySet extends AbstractODataPayload implements ODataEntitySet {

  /**
   * Link to the next page.
   */
  private URI next;

  /**
   * Number of ODataEntities contained in this entity set.
   * <br/>
   * If <tt>$inlinecount</tt> was requested, this value comes from there.
   */
  private Integer count;

  /**
   * Constructor.
   */
  public AbstractODataEntitySet() {
    super(null);
  }

  /**
   * Constructor.
   * 
   * @param next next link.
   */
  public AbstractODataEntitySet(final URI next) {
    super(null);
    this.next = next;
  }

  @Override
  public URI getNext() {
    return next;
  }

  @Override
  public Integer getCount() {
    return count;
  }

  @Override
  public void setCount(final int count) {
    this.count = count;
  }
}
