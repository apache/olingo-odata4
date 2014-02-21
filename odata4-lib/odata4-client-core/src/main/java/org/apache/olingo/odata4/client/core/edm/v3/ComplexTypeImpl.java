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
package org.apache.olingo.odata4.client.core.edm.v3;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.ComplexType;
import org.apache.olingo.odata4.client.api.edm.Property;
import org.apache.olingo.odata4.client.core.edm.AbstractComplexType;

public class ComplexTypeImpl extends AbstractComplexType implements ComplexType {

  private static final long serialVersionUID = -1251230308269425962L;

  private final List<Property> properties = new ArrayList<Property>();

  @Override
  public List<Property> getProperties() {
    return properties;
  }

  @Override
  public Property getProperty(final String name) {
    Property result = null;
    for (Property property : getProperties()) {
      if (name.equals(property.getName())) {
        result = property;
      }
    }
    return result;
  }
}
