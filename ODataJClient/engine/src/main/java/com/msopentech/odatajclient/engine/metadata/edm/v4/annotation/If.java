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

public class If extends AnnotatedDynExprConstruct {

    private static final long serialVersionUID = 6752952406406218936L;

    private ExprConstruct guard;

    private ExprConstruct _then;

    private ExprConstruct _else;

    public ExprConstruct getGuard() {
        return guard;
    }

    public void setGuard(final ExprConstruct guard) {
        this.guard = guard;
    }

    public ExprConstruct getThen() {
        return _then;
    }

    public void setThen(final ExprConstruct _then) {
        this._then = _then;
    }

    public ExprConstruct getElse() {
        return _else;
    }

    public void setElse(final ExprConstruct _else) {
        this._else = _else;
    }

}
