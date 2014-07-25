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
package org.apache.olingo.ext.proxy.api.annotations;

import org.apache.olingo.ext.proxy.api.OperationType;

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

  Class<?> referenceType() default Void.class;

  /**
   * Operation type, function or action.
   *
   * @return operation type.
   */
  OperationType type();

  /**
   * The action/function MAY/MUST specify a return type using the edm:ReturnType element. The return type must be a
   * scalar, entity or complex type, or a collection of scalar, entity or complex types.
   *
   * @return operation return type.
   */
  String returnType() default "";

  /**
   * A function element MAY specify a Boolean value for the IsComposable attribute. If no value is specified for the
   * IsComposable attribute, the value defaults to false.
   * <br/>
   * Functions whose IsComposable attribute is true are considered composable. A composable function can be invoked with
   * additional path segments or system query options appended to the path that identifies the composable function as
   * appropriate for the type returned by the composable function.
   *
   * @return <tt>TRUE</tt> if is composable; <tt>FALSE</tt> otherwise.
   */
  boolean isComposable() default false;
}
