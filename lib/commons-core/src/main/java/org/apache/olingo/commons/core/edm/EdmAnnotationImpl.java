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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotatable;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.annotation.EdmAnnotationExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicAnnotationExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmPropertyValue;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlDynamicExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;
import org.apache.olingo.commons.core.edm.annotation.EdmAndImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmAnnotationPathImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmApplyImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmCastImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmCollectionImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmConstantAnnotationExpressionImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmEqImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmGeImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmGtImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmIfImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmIsOfImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmLabeledElementImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmLabeledElementReferenceImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmLeImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmLtImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmNavigationPropertyPathImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmNeImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmNotImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmNullImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmOrImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmPathImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmPropertyPathImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmPropertyValueImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmRecordImpl;
import org.apache.olingo.commons.core.edm.annotation.EdmUrlRefImpl;

public class EdmAnnotationImpl extends AbstractEdmAnnotatable implements EdmAnnotation {

  private final CsdlAnnotation annotation;
  private EdmTerm term;
  private EdmAnnotationExpression expression;

  public EdmAnnotationImpl(final Edm edm, final CsdlAnnotation annotation) {
    super(edm, annotation);
    this.annotation = annotation;
  }

  @Override
  public EdmTerm getTerm() {
    if (term == null) {
      term = edm.getTerm(new FullQualifiedName(annotation.getTerm()));
    }
    return term;
  }

  @Override
  public String getQualifier() {
    return annotation.getQualifier();
  }

  private EdmAnnotationExpression getExpression(final CsdlExpression exp) {
    EdmAnnotationExpression _expression = null;

    if (exp.isConstant()) {
      _expression = new EdmConstantAnnotationExpressionImpl(exp.asConstant());
    } else if (annotation.getExpression().isDynamic()) {
      _expression = getDynamicExpression(exp.asDynamic());
    }

    return _expression;
  }

  private EdmDynamicAnnotationExpression getDynamicExpression(final CsdlDynamicExpression exp) {
    EdmDynamicAnnotationExpression _expression = null;

    if (exp.isLogicalOrComparison()) {
      switch (exp.asLogicalOrComparison().getType()) {
      case Not:
        _expression = new EdmNotImpl(getExpression(exp.asLogicalOrComparison().getLeft()));
        break;
      case And:
        _expression = new EdmAndImpl(
            getExpression(exp.asLogicalOrComparison().getLeft()),
            getExpression(exp.asLogicalOrComparison().getRight()));
        break;

      case Or:
        _expression = new EdmOrImpl(
            getExpression(exp.asLogicalOrComparison().getLeft()),
            getExpression(exp.asLogicalOrComparison().getRight()));
        break;

      case Eq:
        _expression = new EdmEqImpl(
            getExpression(exp.asLogicalOrComparison().getLeft()),
            getExpression(exp.asLogicalOrComparison().getRight()));
        break;

      case Ne:
        _expression = new EdmNeImpl(
            getExpression(exp.asLogicalOrComparison().getLeft()),
            getExpression(exp.asLogicalOrComparison().getRight()));
        break;

      case Ge:
        _expression = new EdmGeImpl(
            getExpression(exp.asLogicalOrComparison().getLeft()),
            getExpression(exp.asLogicalOrComparison().getRight()));
        break;

      case Gt:
        _expression = new EdmGtImpl(
            getExpression(exp.asLogicalOrComparison().getLeft()),
            getExpression(exp.asLogicalOrComparison().getRight()));
        break;

      case Le:
        _expression = new EdmLeImpl(
            getExpression(exp.asLogicalOrComparison().getLeft()),
            getExpression(exp.asLogicalOrComparison().getRight()));
        break;

      case Lt:
        _expression = new EdmLtImpl(
            getExpression(exp.asLogicalOrComparison().getLeft()),
            getExpression(exp.asLogicalOrComparison().getRight()));
        break;

      default:
      }
    } else if (exp.isAnnotationPath()) {
      _expression = new EdmAnnotationPathImpl(exp.asAnnotationPath().getValue());
    } else if (exp.isApply()) {
      final List<EdmAnnotationExpression> parameters =
          new ArrayList<EdmAnnotationExpression>(exp.asApply().getParameters().size());
      for (CsdlExpression param : exp.asApply().getParameters()) {
        parameters.add(getExpression(param));
      }
      _expression = new EdmApplyImpl(exp.asApply().getFunction(), parameters);
    } else if (exp.isCast()) {
      _expression = new EdmCastImpl(edm, exp.asCast(), getDynamicExpression(exp.asCast().getValue()));
    } else if (exp.isCollection()) {
      final List<EdmAnnotationExpression> items =
          new ArrayList<EdmAnnotationExpression>(exp.asCollection().getItems().size());
      for (CsdlExpression param : exp.asCollection().getItems()) {
        items.add(getExpression(param));
      }
      _expression = new EdmCollectionImpl(items);
    } else if (exp.isIf()) {
      _expression = new EdmIfImpl(
          getExpression(exp.asIf().getGuard()),
          getExpression(exp.asIf().getThen()),
          getExpression(exp.asIf().getElse()));
    } else if (exp.isIsOf()) {
      _expression = new EdmIsOfImpl(edm, exp.asIsOf(), getDynamicExpression(exp.asIsOf().getValue().asDynamic()));
    } else if (exp.isLabeledElement()) {
      _expression = new EdmLabeledElementImpl(
          exp.asLabeledElement().getName(), getDynamicExpression(exp.asLabeledElement().getValue().asDynamic()));
    } else if (exp.isLabeledElementReference()) {
      _expression = new EdmLabeledElementReferenceImpl(exp.asLabeledElementReference().getValue());
    } else if (exp.isNull()) {
      _expression = new EdmNullImpl();
    } else if (exp.isNavigationPropertyPath()) {
      _expression = new EdmNavigationPropertyPathImpl(exp.asNavigationPropertyPath().getValue());
    } else if (exp.isPath()) {
      _expression = new EdmPathImpl(exp.asPath().getValue());
    } else if (exp.isPropertyPath()) {
      _expression = new EdmPropertyPathImpl(exp.asPropertyPath().getValue());
    } else if (exp.isPropertyValue()) {
      _expression = new EdmPropertyValueImpl(
          exp.asPropertyValue().getProperty(), getExpression(exp.asPropertyValue().getValue()));
    } else if (exp.isRecord()) {
      final List<EdmPropertyValue> propertyValues =
          new ArrayList<EdmPropertyValue>(exp.asRecord().getPropertyValues().size());
      for (CsdlPropertyValue propertyValue : exp.asRecord().getPropertyValues()) {
        propertyValues.add(new EdmPropertyValueImpl(
            propertyValue.getProperty(), getExpression(propertyValue.getValue())));
      }
      _expression = new EdmRecordImpl(edm, exp.asRecord().getType(), propertyValues);
    } else if (exp.isUrlRef()) {
      _expression = new EdmUrlRefImpl(getExpression(exp.asUrlRef().getValue()));
    }

    if (_expression instanceof EdmAnnotatable && exp instanceof CsdlAnnotatable) {
      for (CsdlAnnotation _annotation : ((CsdlAnnotatable) exp).getAnnotations()) {
        ((EdmAnnotatable) _expression).getAnnotations().add(new EdmAnnotationImpl(edm, _annotation));
      }
    }

    return _expression;
  }

  @Override
  public EdmAnnotationExpression getExpression() {
    if (expression == null) {
      expression = getExpression(annotation.getExpression());
    }
    return expression;
  }
}
