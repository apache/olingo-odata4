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

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.olingo.commons.api.edm.provider.annotation.AnnotationPath;
import org.apache.olingo.commons.api.edm.provider.annotation.Apply;
import org.apache.olingo.commons.api.edm.provider.annotation.Cast;
import org.apache.olingo.commons.api.edm.provider.annotation.Collection;
import org.apache.olingo.commons.api.edm.provider.annotation.DynamicAnnotationExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.If;
import org.apache.olingo.commons.api.edm.provider.annotation.IsOf;
import org.apache.olingo.commons.api.edm.provider.annotation.LabeledElement;
import org.apache.olingo.commons.api.edm.provider.annotation.LabeledElementReference;
import org.apache.olingo.commons.api.edm.provider.annotation.NavigationPropertyPath;
import org.apache.olingo.commons.api.edm.provider.annotation.Not;
import org.apache.olingo.commons.api.edm.provider.annotation.Null;
import org.apache.olingo.commons.api.edm.provider.annotation.Path;
import org.apache.olingo.commons.api.edm.provider.annotation.PropertyPath;
import org.apache.olingo.commons.api.edm.provider.annotation.PropertyValue;
import org.apache.olingo.commons.api.edm.provider.annotation.Record;
import org.apache.olingo.commons.api.edm.provider.annotation.TwoParamsOpDynamicAnnotationExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.UrlRef;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = AbstractClientCsdlDynamicAnnotationExpression.DynamicAnnotationExpressionDeserializer.class)
abstract class AbstractClientCsdlDynamicAnnotationExpression
        extends AbstractClientCsdlAnnotationExpression implements DynamicAnnotationExpression {

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

  static class DynamicAnnotationExpressionDeserializer
          extends AbstractClientCsdlEdmDeserializer<AbstractClientCsdlDynamicAnnotationExpression> {

    private static final String[] EL_OR_ATTR = {
            AnnotationPath.class.getSimpleName(), NavigationPropertyPath.class.getSimpleName(),
            Path.class.getSimpleName(), PropertyPath.class.getSimpleName()
    };

    private static final String APPLY = Apply.class.getSimpleName();
    private static final String CAST = Cast.class.getSimpleName();
    private static final String COLLECTION = Collection.class.getSimpleName();
    private static final String IF = If.class.getSimpleName();
    private static final String IS_OF = IsOf.class.getSimpleName();
    private static final String LABELED_ELEMENT = LabeledElement.class.getSimpleName();
    private static final String NULL = Null.class.getSimpleName();
    private static final String RECORD = Record.class.getSimpleName();
    private static final String URL_REF = UrlRef.class.getSimpleName();

    private AbstractClientCsdlElementOrAttributeExpression getElementOrAttributeExpression(final String simpleClassName)
            throws JsonParseException {

      try {
        @SuppressWarnings("unchecked")
        Class<? extends AbstractClientCsdlElementOrAttributeExpression> elOrAttrClass =
                (Class<? extends AbstractClientCsdlElementOrAttributeExpression>) ClassUtils.getClass(
                        getClass().getPackage().getName() + ".ClientCsdl" + simpleClassName);
        return elOrAttrClass.newInstance();
      } catch (Exception e) {
        throw new JsonParseException("Could not instantiate " + simpleClassName, JsonLocation.NA, e);
      }
    }

    private AbstractClientCsdlAnnotationExpression parseConstOrEnumExpression(final JsonParser jp) throws IOException {
      AbstractClientCsdlAnnotationExpression result;
      if (isAnnotationConstExprConstruct(jp)) {
        result = parseAnnotationConstExprConstruct(jp);
      } else {
        result = jp.readValueAs(AbstractClientCsdlDynamicAnnotationExpression.class);
      }
      jp.nextToken();

      return result;
    }

    @Override
    protected AbstractClientCsdlDynamicAnnotationExpression doDeserialize(final JsonParser jp,
        final DeserializationContext ctxt) throws IOException {

      AbstractClientCsdlDynamicAnnotationExpression expression = null;

      if ("Not".equals(jp.getCurrentName())) {
        final ClientCsdlNot not = new ClientCsdlNot();

        jp.nextToken();
        //Search for field name
        while (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
          jp.nextToken();
        }
        not.setExpression(jp.readValueAs(AbstractClientCsdlDynamicAnnotationExpression.class));
        //Search for end object
        while (jp.getCurrentToken() != JsonToken.END_OBJECT || !jp.getCurrentName().equals("Not")) {
          jp.nextToken();
        }

        expression = not;
      } else if (TwoParamsOpDynamicAnnotationExpression.Type.fromString(jp.getCurrentName()) != null) {
        final ClientCsdlTwoParamsOpDynamicAnnotationExpression dynExprDoubleParamOp =
                new ClientCsdlTwoParamsOpDynamicAnnotationExpression();
        dynExprDoubleParamOp.setType(TwoParamsOpDynamicAnnotationExpression.Type.fromString(jp.getCurrentName()));

        jp.nextToken();
        //Search for field name
        while (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
          jp.nextToken();
        }
        dynExprDoubleParamOp.setLeftExpression(jp.readValueAs(AbstractClientCsdlDynamicAnnotationExpression.class));
        dynExprDoubleParamOp.setRightExpression(jp.readValueAs(AbstractClientCsdlDynamicAnnotationExpression.class));
        //Search for expression
        while (jp.getCurrentToken() != JsonToken.END_OBJECT || !jp.getCurrentName().equals(dynExprDoubleParamOp
                .getType().name())) {
          jp.nextToken();
        }

        expression = dynExprDoubleParamOp;
      } else if (ArrayUtils.contains(EL_OR_ATTR, jp.getCurrentName())) {
        final AbstractClientCsdlElementOrAttributeExpression elOrAttr =
            getElementOrAttributeExpression(jp.getCurrentName());
        elOrAttr.setValue(jp.nextTextValue());
        expression = elOrAttr;
      } else if (APPLY.equals(jp.getCurrentName())) {
        jp.nextToken();
        expression = jp.readValueAs(ClientCsdlApply.class);
      } else if (CAST.equals(jp.getCurrentName())) {
        jp.nextToken();
        expression = jp.readValueAs(ClientCsdlCast.class);
      } else if (COLLECTION.equals(jp.getCurrentName())) {
        jp.nextToken();
        expression = jp.readValueAs(ClientCsdlCollection.class);
      } else if (IF.equals(jp.getCurrentName())) {
        jp.nextToken();
        jp.nextToken();

        final ClientCsdlIf ifImpl = new ClientCsdlIf();
        ifImpl.setGuard(parseConstOrEnumExpression(jp));
        ifImpl.setThen(parseConstOrEnumExpression(jp));
        ifImpl.setElse(parseConstOrEnumExpression(jp));

        expression = ifImpl;
      } else if (IS_OF.equals(jp.getCurrentName())) {
        jp.nextToken();
        expression = jp.readValueAs(ClientCsdlIsOf.class);
      } else if (LABELED_ELEMENT.equals(jp.getCurrentName())) {
        jp.nextToken();
        expression = jp.readValueAs(ClientCsdlLabeledElement.class);
      } else if (NULL.equals(jp.getCurrentName())) {
        jp.nextToken();
        expression = jp.readValueAs(ClientCsdlNull.class);
      } else if (RECORD.equals(jp.getCurrentName())) {
        jp.nextToken();
        expression = jp.readValueAs(ClientCsdlRecord.class);
      } else if (URL_REF.equals(jp.getCurrentName())) {
        jp.nextToken();
        expression = jp.readValueAs(ClientCsdlUrlRef.class);
      }

      return expression;
    }
  }
}
