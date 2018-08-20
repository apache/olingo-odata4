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

import java.util.List;

/**
 * Entity Data Model (EDM)
 * <br/>
 * Interface representing a Entity Data Model as described in the Conceptual Schema Definition.
 */
public interface Edm {

  /**
   * This method <b>DOES NOT</b> support lazy loading. All schemas are loaded completely!
   *
   * @return all schemas defined for this EDM
   */
  List<EdmSchema> getSchemas();

  /**
   * Get schema by namespace.
   *
   * @param namespace must not be null
   * @return {@link EdmSchema}
   */
  EdmSchema getSchema(String namespace);

  /**
   * Get main entity container.
   * <br/>
   * See {@link EdmEntityContainer} for more information.
   *
   * @return {@link EdmEntityContainer}
   */
  EdmEntityContainer getEntityContainer();

  /**
   * Get entity container by full qualified name.
   * <br/>
   * See {@link EdmEntityContainer} for more information.
   *
   * @param name full qualified name of entity container
   * @return {@link EdmEntityContainer}
   */
  EdmEntityContainer getEntityContainer(FullQualifiedName name);

  /**
   * Get enum type by full qualified name.
   * <br/>
   * See {@link EdmEnumType} for more information
   *
   * @param name full qualified name of enum type
   * @return {@link EdmEnumType}
   */
  EdmEnumType getEnumType(FullQualifiedName name);

  /**
   * Get a type definition by full qualified name.
   * <br/>
   * See {@link EdmTypeDefinition} for more information
   *
   * @param name full qualified name of type definition
   * @return {@link EdmTypeDefinition}
   */
  EdmTypeDefinition getTypeDefinition(FullQualifiedName name);

  /**
   * Get entity type by full qualified name.
   * <br/>
   * See {@link EdmEntityType} for more information.
   *
   * @param name full qualified name of entity type
   * @return {@link EdmEntityType}
   */
  EdmEntityType getEntityType(FullQualifiedName name);

  /**
   * Get entity type with annotations by full qualified name.
   * <br/>
   * See {@link EdmEntityType} for more information.
   *
   * @param name full qualified name of entity type
   * @return {@link EdmEntityType}
   */
  EdmEntityType getEntityTypeWithAnnotations(FullQualifiedName name);
  
  /**
   * Get complex type by full qualified name..
   * <br/>
   * See {@link EdmComplexType} for more information.
   *
   * @param name full qualified name of complex type
   * @return {@link EdmComplexType}
   */
  EdmComplexType getComplexType(FullQualifiedName name);

  /**
   * Get complex type with annotations by full qualified name..
   * <br/>
   * See {@link EdmComplexType} for more information.
   *
   * @param name full qualified name of complex type
   * @return {@link EdmComplexType}
   */
  EdmComplexType getComplexTypeWithAnnotations(FullQualifiedName name);
  
  /**
   * Get unbound Action by full qualified name.
   *
   * @param actionName must not be null
   * @return {@link EdmAction}
   */
  EdmAction getUnboundAction(FullQualifiedName actionName);

  /**
   * Get Action by full qualified name and binding parameter type.
   *
   * @param actionName must not be null
   * @param bindingParameterTypeName must not be null
   * @param isBindingParameterCollection may be null
   * @return {@link EdmAction}
   */
  EdmAction getBoundAction(FullQualifiedName actionName, FullQualifiedName bindingParameterTypeName,
      Boolean isBindingParameterCollection);

  /**
   * Get Action by full qualified name and binding parameter type.
   * Note: action can not be overloaded on binding type
   * 
   * @param bindingParameterTypeName must not be null
   * @param isBindingParameterCollection may be null
   * @return {@link EdmAction}
   */  
  EdmAction getBoundActionWithBindingType(FullQualifiedName bindingParameterTypeName,
      Boolean isBindingParameterCollection);

  /**
   * Get Function by full qualified name.
   *
   * @param functionName must not be null
   * @return {@link EdmFunction}
   */
  List<EdmFunction> getUnboundFunctions(FullQualifiedName functionName);

  /**
   * Get Function by full qualified name.
   *
   * @param functionName must not be null
   * @param parameterNames may be null: in this case it is considered as empty
   * @return {@link EdmFunction}
   */
  EdmFunction getUnboundFunction(FullQualifiedName functionName, List<String> parameterNames);

  /**
   * Get Function by full qualified name and binding parameter type and binding parameter names.
   *
   * @param functionName must not be null
   * @param bindingParameterTypeName must not be null
   * @param isBindingParameterCollection may be null
   * @param parameterNames may be null: in this case it is considered as empty
   * @return {@link EdmFunction}
   */
  EdmFunction getBoundFunction(FullQualifiedName functionName, FullQualifiedName bindingParameterTypeName,
      Boolean isBindingParameterCollection, List<String> parameterNames);

  /**
   * Get Function by binding parameter type
   *
   * @param bindingParameterTypeName must not be null
   * @param isBindingParameterCollection may be null
   * @return {@link EdmFunction}
   */
  List<EdmFunction> getBoundFunctionsWithBindingType(FullQualifiedName bindingParameterTypeName,
      Boolean isBindingParameterCollection);  

  /**
   * Get Term full by qualified name.
   *
   * @param termName must not be null
   * @return {@link EdmTerm}
   */
  EdmTerm getTerm(FullQualifiedName termName);

  /**
   * Get {@link EdmAnnotations} by target.
   *
   * @param targetName <tt>edm:Annotations</tt> target
   * @param qualifier for the target. Can be <code>NULL</code>
   * @return {@link EdmAnnotations}
   */
  EdmAnnotations getAnnotationGroup(FullQualifiedName targetName, String qualifier);
}
