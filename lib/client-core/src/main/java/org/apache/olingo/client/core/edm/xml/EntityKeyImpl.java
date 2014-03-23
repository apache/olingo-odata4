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
package org.apache.olingo.client.core.edm.xml;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.EntityKey;
import org.apache.olingo.client.api.edm.xml.PropertyRef;
import org.apache.olingo.client.core.op.EntityKeyDeserializer;

@JsonDeserialize(using = EntityKeyDeserializer.class)
public class EntityKeyImpl extends AbstractEdmItem implements EntityKey {

  private static final long serialVersionUID = 2586047015894794685L;

  private final List<PropertyRef> propertyRefs = new ArrayList<PropertyRef>();

  @Override
  public List<PropertyRef> getPropertyRefs() {
    return propertyRefs;
  }
}
