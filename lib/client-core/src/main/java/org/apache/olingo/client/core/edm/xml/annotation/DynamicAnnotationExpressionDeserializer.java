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
package org.apache.olingo.client.core.edm.xml.annotation;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.olingo.client.api.edm.xml.annotation.AnnotationPath;
import org.apache.olingo.client.api.edm.xml.annotation.Apply;
import org.apache.olingo.client.api.edm.xml.annotation.Cast;
import org.apache.olingo.client.api.edm.xml.annotation.Collection;
import org.apache.olingo.client.api.edm.xml.annotation.If;
import org.apache.olingo.client.api.edm.xml.annotation.IsOf;
import org.apache.olingo.client.api.edm.xml.annotation.LabeledElement;
import org.apache.olingo.client.api.edm.xml.annotation.NavigationPropertyPath;
import org.apache.olingo.client.api.edm.xml.annotation.Null;
import org.apache.olingo.client.api.edm.xml.annotation.Path;
import org.apache.olingo.client.api.edm.xml.annotation.PropertyPath;
import org.apache.olingo.client.api.edm.xml.annotation.Record;
import org.apache.olingo.client.api.edm.xml.annotation.TwoParamsOpDynamicAnnotationExpression;
import org.apache.olingo.client.api.edm.xml.annotation.UrlRef;
import org.apache.olingo.client.core.edm.xml.AbstractEdmDeserializer;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class DynamicAnnotationExpressionDeserializer
        extends AbstractEdmDeserializer<AbstractDynamicAnnotationExpression> {

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

  private AbstractElementOrAttributeExpression getElementOrAttributeExpressio(final String simpleClassName)
          throws JsonParseException {

    try {
      @SuppressWarnings("unchecked")
      Class<? extends AbstractElementOrAttributeExpression> elOrAttrClass =
              (Class<? extends AbstractElementOrAttributeExpression>) ClassUtils.getClass(
                      getClass().getPackage().getName() + "." + simpleClassName + "Impl");
      return elOrAttrClass.newInstance();
    } catch (Exception e) {
      throw new JsonParseException("Could not instantiate " + simpleClassName, JsonLocation.NA, e);
    }
  }

  private AbstractAnnotationExpression parseConstOrEnumExpression(final JsonParser jp) throws IOException {
    AbstractAnnotationExpression result;
    if (isAnnotationConstExprConstruct(jp)) {
      result = parseAnnotationConstExprConstruct(jp);
    } else {
      result = jp.readValueAs(AbstractDynamicAnnotationExpression.class);
    }
    jp.nextToken();

    return result;
  }

  @Override
  protected AbstractDynamicAnnotationExpression doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    AbstractDynamicAnnotationExpression expression = null;

    if ("Not".equals(jp.getCurrentName())) {
      final NotImpl not = new NotImpl();

      jp.nextToken();
      for (; jp.getCurrentToken() != JsonToken.FIELD_NAME; jp.nextToken()) {
      //Search for field name
      }
      not.setExpression(jp.readValueAs(AbstractDynamicAnnotationExpression.class));
      for (; jp.getCurrentToken() != JsonToken.END_OBJECT || !jp.getCurrentName().equals("Not"); jp.nextToken()) {
      //Search for end object
      }

      expression = not;
    } else if (TwoParamsOpDynamicAnnotationExpression.Type.fromString(jp.getCurrentName()) != null) {
      final TwoParamsOpDynamicAnnotationExpressionImpl dynExprDoubleParamOp =
              new TwoParamsOpDynamicAnnotationExpressionImpl();
      dynExprDoubleParamOp.setType(TwoParamsOpDynamicAnnotationExpression.Type.fromString(jp.getCurrentName()));

      jp.nextToken();
      for (; jp.getCurrentToken() != JsonToken.FIELD_NAME; jp.nextToken()) {
      //Search for field name
      }
      dynExprDoubleParamOp.setLeftExpression(jp.readValueAs(AbstractDynamicAnnotationExpression.class));
      dynExprDoubleParamOp.setRightExpression(jp.readValueAs(AbstractDynamicAnnotationExpression.class));
      for (; jp.getCurrentToken() != JsonToken.END_OBJECT
              || !jp.getCurrentName().equals(dynExprDoubleParamOp.getType().name()); jp.nextToken()) {
        //Search for expression
      }

      expression = dynExprDoubleParamOp;
    } else if (ArrayUtils.contains(EL_OR_ATTR, jp.getCurrentName())) {
      final AbstractElementOrAttributeExpression elOrAttr = getElementOrAttributeExpressio(jp.getCurrentName());
      elOrAttr.setValue(jp.nextTextValue());

      expression = elOrAttr;
    } else if (APPLY.equals(jp.getCurrentName())) {
      jp.nextToken();
      expression = jp.readValueAs(ApplyImpl.class);
    } else if (CAST.equals(jp.getCurrentName())) {
      jp.nextToken();
      expression = jp.readValueAs(CastImpl.class);
    } else if (COLLECTION.equals(jp.getCurrentName())) {
      jp.nextToken();
      expression = jp.readValueAs(CollectionImpl.class);
    } else if (IF.equals(jp.getCurrentName())) {
      jp.nextToken();
      jp.nextToken();

      final IfImpl _if = new IfImpl();
      _if.setGuard(parseConstOrEnumExpression(jp));
      _if.setThen(parseConstOrEnumExpression(jp));
      _if.setElse(parseConstOrEnumExpression(jp));

      expression = _if;
    } else if (IS_OF.equals(jp.getCurrentName())) {
      jp.nextToken();
      expression = jp.readValueAs(IsOfImpl.class);
    } else if (LABELED_ELEMENT.equals(jp.getCurrentName())) {
      jp.nextToken();
      expression = jp.readValueAs(LabeledElementImpl.class);
    } else if (NULL.equals(jp.getCurrentName())) {
      jp.nextToken();
      expression = jp.readValueAs(NullImpl.class);
    } else if (RECORD.equals(jp.getCurrentName())) {
      jp.nextToken();
      expression = jp.readValueAs(RecordImpl.class);
    } else if (URL_REF.equals(jp.getCurrentName())) {
      jp.nextToken();
      expression = jp.readValueAs(UrlRefImpl.class);
    }

    return expression;
  }
}
