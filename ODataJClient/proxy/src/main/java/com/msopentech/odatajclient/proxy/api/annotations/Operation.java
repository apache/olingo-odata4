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
package com.msopentech.odatajclient.proxy.api.annotations;

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark method as EDM function import.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Operation {

    String name();

    /**
     * EntitySet and EntitySetPath are mutually exclusive.
     *
     * @return static EntitySet
     */
    Class<? extends Serializable> entitySet() default Serializable.class;

    /**
     * Defines the EntitySet that contains the entities that are returned by the Operation when
     * that EntitySet is dependent on one of the Operation parameters.
     *
     * @return EntitySet path, dependent on one of the Operation parameters
     * @see Parameter
     */
    String entitySetPath() default "";

    String returnType() default "";

    /**
     * When httpMethod() is NONE, true: this annotates an action; false: this annotates a function
     *
     * @return
     */
    boolean isSideEffecting() default true;

    boolean isComposable() default false;

    /**
     * if not NONE, this annotates a legacy service operation.
     *
     * @return
     */
    HttpMethod httpMethod() default HttpMethod.GET;
}
