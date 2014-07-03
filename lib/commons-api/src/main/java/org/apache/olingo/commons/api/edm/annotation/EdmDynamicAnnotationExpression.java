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
package org.apache.olingo.commons.api.edm.annotation;

public interface EdmDynamicAnnotationExpression extends EdmAnnotationExpression {

  boolean isNot();

  EdmNot asNot();

  boolean isAnd();

  EdmAnd asAnd();

  boolean isOr();

  EdmOr asOr();

  boolean isEq();

  EdmEq asEq();

  boolean isNe();

  EdmNe asNe();

  boolean isGt();

  EdmGt asGt();

  boolean isGe();

  EdmGe asGe();

  boolean isLt();

  EdmLt asLt();

  boolean isLe();

  EdmLe asLe();

  boolean isAnnotationPath();

  EdmAnnotationPath asAnnotationPath();

  boolean isApply();

  EdmApply asApply();

  boolean isCast();

  EdmCast asCast();

  boolean isCollection();

  EdmCollection asCollection();

  boolean isIf();

  EdmIf asIf();

  boolean isIsOf();

  EdmIsOf asIsOf();

  boolean isLabeledElement();

  EdmLabeledElement asLabeledElement();

  boolean isLabeledElementReference();

  EdmLabeledElementReference asLabeledElementReference();

  boolean isNull();

  EdmNull asNull();

  boolean isNavigationPropertyPath();

  EdmNavigationPropertyPath asNavigationPropertyPath();

  boolean isPath();

  EdmPath asPath();

  boolean isPropertyPath();

  EdmPropertyPath asPropertyPath();

  boolean isPropertyValue();

  EdmPropertyValue asPropertyValue();

  boolean isRecord();

  EdmRecord asRecord();

  boolean isUrlRef();

  EdmUrlRef asUrlRef();
}
