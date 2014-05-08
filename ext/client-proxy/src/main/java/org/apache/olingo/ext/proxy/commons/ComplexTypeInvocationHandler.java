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
package org.apache.olingo.ext.proxy.commons;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataLinked;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.utils.EngineUtils;

public class ComplexTypeInvocationHandler<C extends CommonEdmEnabledODataClient<?>>
        extends AbstractTypeInvocationHandler<C> {

  private static final long serialVersionUID = 2629912294765040037L;

  @SuppressWarnings({"unchecked", "rawtypes"})
  static ComplexTypeInvocationHandler<?> getInstance(
          final ODataComplexValue<?> complex,
          final Class<?> typeRef,
          final EntityTypeInvocationHandler<?> handler) {

    return new ComplexTypeInvocationHandler(complex, typeRef, handler);
  }

  private ComplexTypeInvocationHandler(
          final ODataComplexValue<?> complex,
          final Class<?> typeRef,
          final EntityTypeInvocationHandler<C> handler) {

    super(handler.containerHandler.getClient(), typeRef, (ODataLinked) complex, handler);
  }

  public void setComplex(final ODataComplexValue<?> complex) {
    this.linked = (ODataLinked) complex;
    this.propertyChanges.clear();
    this.linkChanges.clear();
    this.propertiesTag = 0;
    this.linksTag = 0;
  }

  @Override
  public FullQualifiedName getName() {
    return new FullQualifiedName(((ODataComplexValue<?>) this.linked).getTypeName());
  }

  public ODataComplexValue<?> getComplex() {
    return (ODataComplexValue<?>) this.linked;
  }

  @Override
  protected Object getPropertyValue(final String name, final Type type) {
    try {
      final Object res;

      if (propertyChanges.containsKey(name)) {
        res = propertyChanges.get(name);
      } else {

        res = type == null
                ? EngineUtils.getValueFromProperty(
                client.getCachedEdm(), ((ODataComplexValue<?>) this.linked).get(name))
                : EngineUtils.getValueFromProperty(
                client.getCachedEdm(), ((ODataComplexValue<?>) this.linked).get(name), type);

        if (res != null) {
          int checkpoint = propertyChanges.hashCode();
          propertyChanges.put(name, res);
          updatePropertiesTag(checkpoint);
        }
      }

      return res;
    } catch (Exception e) {
      throw new IllegalArgumentException("Error getting value for property '" + name + "'", e);
    }
  }

  @Override
  public Collection<String> getAdditionalPropertyNames() {
    final Set<String> res = new HashSet<String>(propertyChanges.keySet());
    final Set<String> propertyNames = new HashSet<String>();
    for (Method method : typeRef.getMethods()) {
      final Annotation ann = method.getAnnotation(Property.class);
      if (ann != null) {
        final String property = ((Property) ann).name();
        propertyNames.add(property);

        // maybe someone could add a normal attribute to the additional set
        res.remove(property);
      }
    }

    for (Iterator<?> itor = ((ODataComplexValue<?>) this.linked).iterator(); itor.hasNext();) {
      CommonODataProperty property = (CommonODataProperty) itor.next();
      if (!propertyNames.contains(property.getName())) {
        res.add(property.getName());
      }
    }

    return res;
  }

  @Override
  protected void setPropertyValue(final Property property, final Object value) {
    propertyChanges.put(property.name(), value);

    if (!entityContext.isAttached(targetHandler)) {
      entityContext.attach(targetHandler, AttachedEntityStatus.CHANGED);
    }
  }

  @Override
  public boolean isChanged() {
    return targetHandler.isChanged();
  }
}
