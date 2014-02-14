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

public class DynExprDoubleParamOp extends DynExprConstruct {

    private static final long serialVersionUID = -7974475975925167731L;

    public static enum Type {

        And,
        Or;

        public static Type fromString(final String value) {
            Type result = null;
            for (Type type : values()) {
                if (value.equals(type.name())) {
                    result = type;
                }
            }
            return result;
        }
    }

    private Type type;

    private DynExprConstruct left;

    private DynExprConstruct right;

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public DynExprConstruct getLeft() {
        return left;
    }

    public void setLeft(final DynExprConstruct left) {
        this.left = left;
    }

    public DynExprConstruct getRight() {
        return right;
    }

    public void setRight(final DynExprConstruct right) {
        this.right = right;
    }

}
