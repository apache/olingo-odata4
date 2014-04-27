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
package org.apache.olingo.client.api.op.v3;

import org.apache.olingo.commons.api.data.v3.LinkCollection;
import org.apache.olingo.client.api.domain.v3.ODataLinkCollection;
import org.apache.olingo.client.api.op.CommonODataBinder;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;

public interface ODataBinder extends CommonODataBinder {

  @Override
  ODataEntitySet getODataEntitySet(ResWrap<Feed> resource);

  @Override
  ODataEntity getODataEntity(ResWrap<Entry> resource);

  @Override
  ODataProperty getODataProperty(ResWrap<Property> resource);

  /**
   * Gets <tt>ODataLinkCollection</tt> from the given link collection resource.
   *
   * @param resource link collection resource.
   * @return <tt>ODataLinkCollection</tt> object.
   */
  ODataLinkCollection getLinkCollection(LinkCollection resource);

}
