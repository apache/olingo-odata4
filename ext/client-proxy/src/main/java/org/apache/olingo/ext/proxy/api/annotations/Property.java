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

import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.client.api.edm.StoreGeneratedPattern;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind POJO field to EDM property.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {

  String name();

  String type();

  boolean nullable() default true;

  String defaultValue() default "";

  int maxLenght() default Integer.MAX_VALUE;

  boolean fixedLenght() default false;

  int precision() default 0;

  int scale() default 0;

  boolean unicode() default true;

  String collation() default "";

  String srid() default "";

  ConcurrencyMode concurrencyMode() default ConcurrencyMode.None;

  String mimeType() default "";

  /* -- Feed Customization annotations -- */
  String fcSourcePath() default "";

  String fcTargetPath() default "";

  EdmContentKind fcContentKind() default EdmContentKind.text;

  String fcNSPrefix() default "";

  String fcNSURI() default "";

  boolean fcKeepInContent() default false;

  StoreGeneratedPattern storeGeneratedPattern() default StoreGeneratedPattern.None;
}
