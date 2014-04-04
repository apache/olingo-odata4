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
     * Get entity container by full qualified name.
     * <br/>
     * See {@link EdmEntityContainer} for more information.
     *
     * @param name
     * @return {@link EdmEntityContainer}
     */
    EdmEntityContainer getEntityContainer(FullQualifiedName name);

    /**
     * Get enum type by full qualified name.
     * <br/>
     * See {@link EdmEnumType} for more information
     *
     * @param name
     * @return {@link EdmEnumType}
     */
    EdmEnumType getEnumType(FullQualifiedName name);

    /**
     * Get a type definition by full qualified name.
     * <br/>
     * See {@link EdmTypeDefinition} for more information
     *
     * @param name
     * @return {@link EdmTypeDefinition}
     */
    EdmTypeDefinition getTypeDefinition(FullQualifiedName name);

    /**
     * Get entity type by full qualified name.
     * <br/>
     * See {@link EdmEntityType} for more information.
     *
     * @param name
     * @return {@link EdmEntityType}
     */
    EdmEntityType getEntityType(FullQualifiedName name);

    /**
     * Get complex type by full qualified name..
     * <br/>
     * See {@link EdmComplexType} for more information.
     *
     * @param name
     * @return {@link EdmComplexType}
     */
    EdmComplexType getComplexType(FullQualifiedName name);

    /**
     * Get Action by full qualified name and binding parameter type.
     *
     * @param actionName must not be null
     * @param bindingParameterTypeName may be null if it is an unbound action
     * @param isBindingParameterCollection may be null if it is an unbound action
     * @return {@link EdmAction}
     */
    EdmAction getAction(FullQualifiedName actionName, FullQualifiedName bindingParameterTypeName,
            Boolean isBindingParameterCollection);

    /**
     * Get Function by full qualified name and binding parameter type and binding parameter names.
     *
     * @param functionName
     * @param bindingParameterTypeName may be null if it is an unbound function
     * @param isBindingParameterCollection may be null if it is an unbound function
     * @param parameterNames may be null if it is an unbound function
     * @return {@link EdmFunction}
     */
    EdmFunction getFunction(FullQualifiedName functionName, FullQualifiedName bindingParameterTypeName,
            Boolean isBindingParameterCollection, List<String> parameterNames);

    /**
     * Get service metadata.
     * <br/>
     * See {@link EdmServiceMetadata} for more information.
     *
     * @return {@link EdmServiceMetadata}
     */
    EdmServiceMetadata getServiceMetadata();
}
