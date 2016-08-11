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
package org.apache.olingo.commons.api.edm;

import org.apache.olingo.commons.api.edm.annotation.EdmAnd;
import org.apache.olingo.commons.api.edm.annotation.EdmApply;
import org.apache.olingo.commons.api.edm.annotation.EdmCast;
import org.apache.olingo.commons.api.edm.annotation.EdmEq;
import org.apache.olingo.commons.api.edm.annotation.EdmGe;
import org.apache.olingo.commons.api.edm.annotation.EdmGt;
import org.apache.olingo.commons.api.edm.annotation.EdmIf;
import org.apache.olingo.commons.api.edm.annotation.EdmIsOf;
import org.apache.olingo.commons.api.edm.annotation.EdmLabeledElement;
import org.apache.olingo.commons.api.edm.annotation.EdmLe;
import org.apache.olingo.commons.api.edm.annotation.EdmLt;
import org.apache.olingo.commons.api.edm.annotation.EdmNe;
import org.apache.olingo.commons.api.edm.annotation.EdmNot;
import org.apache.olingo.commons.api.edm.annotation.EdmNull;
import org.apache.olingo.commons.api.edm.annotation.EdmOr;
import org.apache.olingo.commons.api.edm.annotation.EdmPropertyValue;
import org.apache.olingo.commons.api.edm.annotation.EdmRecord;
import org.apache.olingo.commons.api.edm.annotation.EdmUrlRef;
import org.apache.olingo.commons.api.edm.constants.EdmOnDelete;
import org.apache.olingo.commons.api.edmx.EdmxReference;

public enum TargetType {

  // CSDL Types
  Action(EdmAction.class),
  ActionImport(EdmActionImport.class),
  Annotation(EdmAnnotation.class),
  Apply(EdmApply.class),
  Cast(EdmCast.class),
  ComplexType(EdmComplexType.class),
  EntityContainer(EdmEntityContainer.class),
  EntitySet(EdmEntitySet.class),
  EntityType(EdmEntityType.class),
  EnumType(EdmEnumType.class),
  Function(EdmFunction.class),
  FunctionImport(EdmFunctionImport.class),
  If(EdmIf.class),
  IsOf(EdmIsOf.class),
  LabeledElement(EdmLabeledElement.class),
  Member(EdmMember.class),
  NavigationProperty(EdmNavigationProperty.class),
  Null(EdmNull.class),
  OnDelete(EdmOnDelete.class),
  Property(EdmProperty.class),
  PropertyValue(EdmPropertyValue.class),
  Parameter(EdmParameter.class),
  Record(EdmRecord.class),
  ReferentialConstraint(EdmReferentialConstraint.class),
  ReturnType(EdmReturnType.class),
  Schema(EdmSchema.class),
  Singleton(EdmSingleton.class),
  Term(EdmTerm.class),
  TypeDefinition(EdmTypeDefinition.class),
  URLRef(EdmUrlRef.class),
  Reference(EdmxReference.class),
  // Logical Operators
  And(EdmAnd.class),
  Or(EdmOr.class),
  Not(EdmNot.class),
  // ComparisonOperators
  Eq(EdmEq.class),
  Ne(EdmNe.class),
  Gt(EdmGt.class),
  Ge(EdmGe.class),
  Lt(EdmLt.class),
  Le(EdmLe.class);

  private final Class<?> edmClass;

  TargetType(final Class<?> edmClass) {
    this.edmClass = edmClass;
  }

  public Class<?> getEdmClass() {
    return edmClass;
  }

}