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
package org.apache.olingo.client.core.edm.xml.v4.annotation;

import org.apache.olingo.client.api.edm.xml.v4.annotation.AnnotationPath;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Apply;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Cast;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Collection;
import org.apache.olingo.client.api.edm.xml.v4.annotation.DynamicAnnotationExpression;
import org.apache.olingo.client.api.edm.xml.v4.annotation.If;
import org.apache.olingo.client.api.edm.xml.v4.annotation.IsOf;
import org.apache.olingo.client.api.edm.xml.v4.annotation.LabeledElement;
import org.apache.olingo.client.api.edm.xml.v4.annotation.LabeledElementReference;
import org.apache.olingo.client.api.edm.xml.v4.annotation.NavigationPropertyPath;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Not;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Null;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Path;
import org.apache.olingo.client.api.edm.xml.v4.annotation.PropertyPath;
import org.apache.olingo.client.api.edm.xml.v4.annotation.PropertyValue;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Record;
import org.apache.olingo.client.api.edm.xml.v4.annotation.TwoParamsOpDynamicAnnotationExpression;
import org.apache.olingo.client.api.edm.xml.v4.annotation.UrlRef;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DynamicAnnotationExpressionDeserializer.class)
public abstract class AbstractDynamicAnnotationExpression
        extends AbstractAnnotationExpression implements DynamicAnnotationExpression {

  private static final long serialVersionUID = 1093411847477874348L;

  @Override
  public boolean isNot() {
    return this instanceof Not;
  }

  @Override
  public Not asNot() {
    return isNot() ? (Not) this : null;

  }

  @Override
  public boolean isTwoParamsOp() {
    return this instanceof TwoParamsOpDynamicAnnotationExpression;
  }

  @Override
  public TwoParamsOpDynamicAnnotationExpression asTwoParamsOp() {
    return isTwoParamsOp() ? (TwoParamsOpDynamicAnnotationExpression) this : null;
  }

  @Override
  public boolean isAnnotationPath() {
    return this instanceof AnnotationPath;
  }

  @Override
  public AnnotationPath asAnnotationPath() {
    return isAnnotationPath() ? (AnnotationPath) this : null;
  }

  @Override
  public boolean isApply() {
    return this instanceof Apply;
  }

  @Override
  public Apply asApply() {
    return isApply() ? (Apply) this : null;
  }

  @Override
  public boolean isCast() {
    return this instanceof Cast;
  }

  @Override
  public Cast asCast() {
    return isCast() ? (Cast) this : null;
  }

  @Override
  public boolean isCollection() {
    return this instanceof Collection;
  }

  @Override
  public Collection asCollection() {
    return isCollection() ? (Collection) this : null;
  }

  @Override
  public boolean isIf() {
    return this instanceof If;
  }

  @Override
  public If asIf() {
    return isIf() ? (If) this : null;
  }

  @Override
  public boolean isIsOf() {
    return this instanceof IsOf;
  }

  @Override
  public IsOf asIsOf() {
    return isIsOf() ? (IsOf) this : null;
  }

  @Override
  public boolean isLabeledElement() {
    return this instanceof LabeledElement;
  }

  @Override
  public LabeledElement asLabeledElement() {
    return isLabeledElement() ? (LabeledElement) this : null;
  }

  @Override
  public boolean isLabeledElementReference() {
    return this instanceof LabeledElementReference;
  }

  @Override
  public LabeledElementReference asLabeledElementReference() {
    return isLabeledElementReference() ? (LabeledElementReference) this : null;
  }

  @Override
  public boolean isNull() {
    return this instanceof Null;
  }

  @Override
  public Null asNull() {
    return isNull() ? (Null) this : null;
  }

  @Override
  public boolean isNavigationPropertyPath() {
    return this instanceof NavigationPropertyPath;
  }

  @Override
  public NavigationPropertyPath asNavigationPropertyPath() {
    return isNavigationPropertyPath() ? (NavigationPropertyPath) this : null;
  }

  @Override
  public boolean isPath() {
    return this instanceof Path;
  }

  @Override
  public Path asPath() {
    return isPath() ? (Path) this : null;
  }

  @Override
  public boolean isPropertyPath() {
    return this instanceof PropertyPath;
  }

  @Override
  public PropertyPath asPropertyPath() {
    return isPropertyPath() ? (PropertyPath) this : null;
  }

  @Override
  public boolean isPropertyValue() {
    return this instanceof PropertyValue;
  }

  @Override
  public PropertyValue asPropertyValue() {
    return isPropertyValue() ? (PropertyValue) this : null;
  }

  @Override
  public boolean isRecord() {
    return this instanceof Record;
  }

  @Override
  public Record asRecord() {
    return isRecord() ? (Record) this : null;
  }

  @Override
  public boolean isUrlRef() {
    return this instanceof UrlRef;
  }

  @Override
  public UrlRef asUrlRef() {
    return isUrlRef() ? (UrlRef) this : null;
  }
}
