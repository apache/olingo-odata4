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
package org.apache.olingo.commons.api.edm;

/**
 * Interface for CSDL elements thar can be evaluated as <tt>target</tt> for <tt>edm:Annotations</tt>.
 *
 * @see EdmAnnotations
 */
public interface EdmAnnotationsTarget {

  enum TargetType {

    ActionImport(EdmActionImport.class),
    ComplexType(EdmComplexType.class),
    EntityContainer(EdmEntityContainer.class),
    EntitySet(EdmEntitySet.class),
    EntityType(EdmEntityType.class),
    EnumType(EdmEnumType.class),
    FunctionImport(EdmFunctionImport.class),
    Member(EdmMember.class),
    NavigationProperty(EdmNavigationProperty.class),
    Property(EdmProperty.class),
    Singleton(EdmSingleton.class),
    Term(EdmTerm.class),
    TypeDefinition(EdmTypeDefinition.class);

    private final Class<?> edmClass;

    TargetType(final Class<?> edmClass) {
      this.edmClass = edmClass;
    }

    public Class<?> getEdmClass() {
      return edmClass;
    }

  }

  /**
   * @return {@link FullQualifiedName} of this target, or of the parent element if applicable
   *
   */
  FullQualifiedName getAnnotationsTargetFQN();

  /**
   * @return name of child element, not null if not needed
   */
  String getAnnotationsTargetPath();

  /**
   * @return {@link TargetType} of this target
   */
  TargetType getAnnotationsTargetType();
}
