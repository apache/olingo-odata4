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
package org.apache.olingo.server.core.responses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

public class ResponseUtil {
  public static Property createPrimitive(final String name, final String type, final Object value) {
    return new Property(type, name, ValueType.PRIMITIVE, value);
  }

  public static Property createPrimitiveCollection(final String name, final Object... values) {
    return new Property(null, name, ValueType.COLLECTION_PRIMITIVE, Arrays.asList(values));
  }

  public static Property createComplex(final String name, final String type, final Property... properties) {
    ComplexValue complexValue = new ComplexValue();
    for (final Property property : properties) {
      complexValue.getValue().add(property);
    }
    return new Property(type, name, ValueType.COMPLEX, complexValue);
  }

  public static Property createComplexCollection(final String name, final String type,
      final List<Property>... propertiesList) {
    List<ComplexValue> complexCollection = new ArrayList<ComplexValue>();
    for (final List<Property> properties : propertiesList) {
      ComplexValue complexValue = new ComplexValue();
      complexValue.getValue().addAll(properties);
      complexCollection.add(complexValue);
    }
    return new Property(type, name, ValueType.COLLECTION_COMPLEX, complexCollection);
  }

  public static void setLink(Entity entity, final String navigationPropertyName, final Entity target) {
    Link link = entity.getNavigationLink(navigationPropertyName);
    if (link == null) {
      link = new Link();
      link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
      link.setTitle(navigationPropertyName);
      entity.getNavigationLinks().add(link);
    }
    link.setInlineEntity(target);
  }

  public static void setLinks(Entity entity, final String navigationPropertyName, final Entity... targets) {
    Link link = entity.getNavigationLink(navigationPropertyName);
    if (link == null) {
      link = new Link();
      link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
      link.setTitle(navigationPropertyName);
      EntityCollection target = new EntityCollection();
      target.getEntities().addAll(Arrays.asList(targets));
      link.setInlineEntitySet(target);
      entity.getNavigationLinks().add(link);
    } else {
      link.getInlineEntitySet().getEntities().addAll(Arrays.asList(targets));
    }
  }
}
