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

import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataLinkedComplexValue;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.Term;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.olingo.ext.proxy.Service;

public class AnnotatableInvocationHandler extends AbstractInvocationHandler implements Annotatable {

  private static final long serialVersionUID = 3111228269617223332L;

  private final String propName;

  private final String navPropName;

  private final EntityInvocationHandler entityHandler;

  private final AbstractStructuredInvocationHandler targetHandler;

  private final Map<Class<? extends AbstractTerm>, Object> annotations =
          new HashMap<Class<? extends AbstractTerm>, Object>();

  public AnnotatableInvocationHandler(
          final Service<?> service,
          final String propName,
          final String navPropName,
          final EntityInvocationHandler entityHandler,
          final AbstractStructuredInvocationHandler targetHandler) {

    super(service);

    this.propName = propName;
    this.navPropName = navPropName;
    this.entityHandler = entityHandler;
    this.targetHandler = targetHandler;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    return invokeSelfMethod(method, args);
  }

  public Map<Class<? extends AbstractTerm>, Object> getAnnotations() {
    return annotations;
  }

  private List<ODataAnnotation> internalAnnotations() {
    List<ODataAnnotation> result = Collections.<ODataAnnotation>emptyList();

    if (entityHandler.getEntity() instanceof ODataEntity) {
      if (targetHandler.getInternal() instanceof ODataEntity) {
        result = propName == null
                ? ((org.apache.olingo.commons.api.domain.v4.ODataLink) ((ODataEntity) targetHandler.getInternal()).
                getNavigationLink(navPropName)).getAnnotations()
                : ((ODataEntity) targetHandler.getInternal()).getProperty(propName).getAnnotations();
      } else if (targetHandler.getInternal() instanceof ODataLinkedComplexValue) {
        result = propName == null
                ? ((org.apache.olingo.commons.api.domain.v4.ODataLink) ((ODataLinkedComplexValue) targetHandler.
                getInternal()).getNavigationLink(navPropName)).getAnnotations()
                : ((ODataLinkedComplexValue) targetHandler.getInternal()).get(propName).getAnnotations();
      }
    }

    return result;
  }

  @Override
  public void addAnnotation(final Class<? extends AbstractTerm> term, final Object value) {
    this.annotations.put(term, value);

    if (value != null) {
      Collection<?> coll;
      if (Collection.class.isAssignableFrom(value.getClass())) {
        coll = Collection.class.cast(value);
      } else {
        coll = Collections.singleton(value);
      }

      for (Object item : coll) {
        if (item instanceof Proxy) {
          final InvocationHandler handler = Proxy.getInvocationHandler(item);
          if ((handler instanceof ComplexInvocationHandler)
                  && ((ComplexInvocationHandler) handler).getEntityHandler() == null) {
            ((ComplexInvocationHandler) handler).setEntityHandler(entityHandler);
          }
        }
      }
    }

    entityHandler.attach(AttachedEntityStatus.CHANGED);
    if (navPropName == null) {
      targetHandler.putPropAnnotatableHandler(propName, this);
    } else {
      targetHandler.putNavPropAnnotatableHandler(navPropName, this);
    }
  }

  @Override
  public void removeAnnotation(final Class<? extends AbstractTerm> term) {
    this.annotations.remove(term);

    entityHandler.attach(AttachedEntityStatus.CHANGED);
    if (navPropName == null) {
      targetHandler.putPropAnnotatableHandler(propName, this);
    } else {
      targetHandler.putNavPropAnnotatableHandler(navPropName, this);
    }
  }

  @Override
  public Object getAnnotation(final Class<? extends AbstractTerm> term) {
    Object res = null;

    if (annotations.containsKey(term)) {
      res = annotations.get(term);
    } else if (entityHandler.getEntity() instanceof ODataEntity) {
      try {
        final Term termAnn = term.getAnnotation(Term.class);
        final Namespace namespaceAnn = term.getAnnotation(Namespace.class);
        ODataAnnotation annotation = null;
        for (ODataAnnotation _annotation : internalAnnotations()) {
          if ((namespaceAnn.value() + "." + termAnn.name()).equals(_annotation.getTerm())) {
            annotation = _annotation;
          }
        }
        res = annotation == null || annotation.hasNullValue()
                ? null
                : CoreUtils.getObjectFromODataValue(annotation.getValue(), null, targetHandler.service);
        if (res != null) {
          annotations.put(term, res);
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("Error getting annotation for term '" + term.getName() + "'", e);
      }
    }

    return res;
  }

  @Override
  public Collection<Class<? extends AbstractTerm>> getAnnotationTerms() {
    return entityHandler.getEntity() instanceof ODataEntity
            ? CoreUtils.getAnnotationTerms(internalAnnotations())
            : Collections.<Class<? extends AbstractTerm>>emptyList();
  }
}
