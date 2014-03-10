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
package org.apache.olingo.odata4.client.core;

import org.apache.olingo.odata4.client.api.V3Configuration;

public class V3ConfigurationImpl extends AbstractConfiguration implements V3Configuration {

  private static final long serialVersionUID = -8719958537946884777L;

  private static final String KEY_AS_SEGMENT = "keyAsSegment";

  protected V3ConfigurationImpl() {
    super();
  }

  /**
   * Checks whether URIs contain entity key between parentheses (standard) or instead as additional segment. Example:
   * http://services.odata.org/V4/OData/OData.svc/Products(0) or http://services.odata.org/V4/OData/OData.svc/Products/0
   *
   * @return whether URIs shall be built with entity key between parentheses (standard) or instead as additional
   * segment.
   */
  @Override
  public boolean isKeyAsSegment() {
    return (Boolean) getProperty(KEY_AS_SEGMENT, false);
  }

  /**
   * Sets whether URIs shall be built with entity key between parentheses (standard) or instead as additional segment.
   * Example: http://services.odata.org/V4/OData/OData.svc/Products(0) or
   * http://services.odata.org/V4/OData/OData.svc/Products/0
   *
   * @param value 'TRUE' to use this feature.
   */
  @Override
  public void setKeyAsSegment(final boolean value) {
    setProperty(KEY_AS_SEGMENT, value);
  }

}
