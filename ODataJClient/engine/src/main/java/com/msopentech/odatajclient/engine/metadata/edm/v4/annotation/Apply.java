/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.metadata.edm.v4.annotation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = ApplyDeserializer.class)
public class Apply extends AnnotatedDynExprConstruct {

    private static final long serialVersionUID = 6198019768659098819L;

    public static final String CANONICAL_FUNCTION_CONCAT = "odata.concat";

    public static final String CANONICAL_FUNCTION_FILLURITEMPLATE = "odata.fillUriTemplate";

    public static final String CANONICAL_FUNCTION_URIENCODE = "odata.uriEncode";

    private String function;

    private final List<ExprConstruct> parameters = new ArrayList<ExprConstruct>();

    public String getFunction() {
        return function;
    }

    public void setFunction(final String function) {
        this.function = function;
    }

    public List<ExprConstruct> getParameters() {
        return parameters;
    }

}
