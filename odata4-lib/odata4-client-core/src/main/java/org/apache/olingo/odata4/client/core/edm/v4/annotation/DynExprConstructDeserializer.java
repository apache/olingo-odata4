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
package org.apache.olingo.odata4.client.core.edm.v4.annotation;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.olingo.odata4.client.core.op.impl.AbstractEdmDeserializer;

public class DynExprConstructDeserializer extends AbstractEdmDeserializer<DynExprConstructImpl> {

    private static final String[] EL_OR_ATTR = { AnnotationPath.class.getSimpleName(), Path.class.getSimpleName() };

    private static final String APPLY = Apply.class.getSimpleName();

    private static final String CAST = Cast.class.getSimpleName();

    private static final String COLLECTION = Collection.class.getSimpleName();

    private static final String IF = If.class.getSimpleName();

    private static final String IS_OF = IsOf.class.getSimpleName();

    private static final String LABELED_ELEMENT = LabeledElement.class.getSimpleName();

    private static final String NULL = Null.class.getSimpleName();

    private static final String RECORD = Record.class.getSimpleName();

    private static final String URL_REF = UrlRef.class.getSimpleName();

    private AbstractElOrAttrConstruct getElOrAttrInstance(final String simpleClassName) throws JsonParseException {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends AbstractElOrAttrConstruct> elOrAttrClass =
                    (Class<? extends AbstractElOrAttrConstruct>) ClassUtils.getClass(
                            getClass().getPackage().getName() + "." + simpleClassName);
            return elOrAttrClass.newInstance();
        } catch (Exception e) {
            throw new JsonParseException("Could not instantiate " + simpleClassName, JsonLocation.NA, e);
        }
    }

    private ExprConstructImpl parseConstOrEnumExprConstruct(final JsonParser jp) throws IOException {
        ExprConstructImpl result;
        if (isAnnotationConstExprConstruct(jp)) {
            result = parseAnnotationConstExprConstruct(jp);
        } else {
            result = jp.readValueAs( DynExprConstructImpl.class);
        }
        jp.nextToken();

        return result;
    }

    @Override
    protected DynExprConstructImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        DynExprConstructImpl construct = null;

        if (DynExprSingleParamOp.Type.fromString(jp.getCurrentName()) != null) {
            final DynExprSingleParamOp dynExprSingleParamOp = new DynExprSingleParamOp();
            dynExprSingleParamOp.setType(DynExprSingleParamOp.Type.fromString(jp.getCurrentName()));

            jp.nextToken();
            jp.nextToken();
            dynExprSingleParamOp.setExpression(jp.readValueAs( DynExprConstructImpl.class));

            construct = dynExprSingleParamOp;
        } else if (DynExprDoubleParamOp.Type.fromString(jp.getCurrentName()) != null) {
            final DynExprDoubleParamOp dynExprDoubleParamOp = new DynExprDoubleParamOp();
            dynExprDoubleParamOp.setType(DynExprDoubleParamOp.Type.fromString(jp.getCurrentName()));

            jp.nextToken();
            jp.nextToken();
            dynExprDoubleParamOp.setLeft(jp.readValueAs( DynExprConstructImpl.class));
            dynExprDoubleParamOp.setRight(jp.readValueAs( DynExprConstructImpl.class));

            construct = dynExprDoubleParamOp;
        } else if (ArrayUtils.contains(EL_OR_ATTR, jp.getCurrentName())) {
            final AbstractElOrAttrConstruct elOrAttr = getElOrAttrInstance(jp.getCurrentName());
            elOrAttr.setValue(jp.nextTextValue());

            construct = elOrAttr;
        } else if (APPLY.equals(jp.getCurrentName())) {
            jp.nextToken();
            construct = jp.readValueAs( Apply.class);
        } else if (CAST.equals(jp.getCurrentName())) {
            jp.nextToken();
            construct = jp.readValueAs( Cast.class);
        } else if (COLLECTION.equals(jp.getCurrentName())) {
            jp.nextToken();
            construct = jp.readValueAs( Collection.class);
        } else if (IF.equals(jp.getCurrentName())) {
            jp.nextToken();
            jp.nextToken();

            final If _if = new If();
            _if.setGuard(parseConstOrEnumExprConstruct(jp));
            _if.setThen(parseConstOrEnumExprConstruct(jp));
            _if.setElse(parseConstOrEnumExprConstruct(jp));

            construct = _if;
        } else if (IS_OF.equals(jp.getCurrentName())) {
            jp.nextToken();
            construct = jp.readValueAs( IsOf.class);
        } else if (LABELED_ELEMENT.equals(jp.getCurrentName())) {
            jp.nextToken();
            construct = jp.readValueAs( LabeledElement.class);
        } else if (NULL.equals(jp.getCurrentName())) {
            jp.nextToken();
            construct = jp.readValueAs( Null.class);
        } else if (RECORD.equals(jp.getCurrentName())) {
            jp.nextToken();
            construct = jp.readValueAs( Record.class);
        } else if (URL_REF.equals(jp.getCurrentName())) {
            jp.nextToken();
            construct = jp.readValueAs( UrlRef.class);
        }

        return construct;
    }
}
