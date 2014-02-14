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
package com.msopentech.odatajclient.engine.metadata;

import com.msopentech.odatajclient.engine.metadata.edm.AbstractComplexType;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityType;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEnumType;

public interface EdmType {

    /**
     * Checks if is a collection.
     *
     * @return 'TRUE' if is a collection; 'FALSE' otherwise.
     */
    boolean isCollection();

    /**
     * Checks if is a simple type.
     *
     * @return 'TRUE' if is a simple type; 'FALSE' otherwise.
     */
    boolean isSimpleType();

    /**
     * Gets type as a simple type.
     *
     * @return simple type. An <tt>EdmTypeNotFoundException</tt> will be raised if it is not a simple type.
     */
    EdmSimpleType getSimpleType();

    /**
     * Checks if is an enum type.
     *
     * @return 'TRUE' if is an enum type; 'FALSE' otherwise.
     */
    boolean isEnumType();

    /**
     * Gets type as enum type.
     *
     * @return enum type. An <tt>EdmTypeNotFoundException</tt> will be raised if it is not an enum type.
     */
    AbstractEnumType getEnumType();

    /**
     * Checks if is a complex type.
     *
     * @return 'TRUE' if is a complex type; 'FALSE' otherwise.
     */
    boolean isComplexType();

    /**
     * Gets type as complex type.
     *
     * @return complex type. An <tt>EdmTypeNotFoundException</tt> will be raised if it is not a complex type.
     */
    AbstractComplexType getComplexType();

    /**
     * Checks if is an entity type.
     *
     * @return 'TRUE' if is an entity type; 'FALSE' otherwise.
     */
    boolean isEntityType();

    /**
     * Gets type as entity type.
     *
     * @return entity type. An <tt>EdmTypeNotFoundException</tt> will be raised if it is not an entity type.
     */
    AbstractEntityType getEntityType();

    /**
     * Gets base type.
     *
     * @return base type.
     */
    String getBaseType();

    /**
     * Gets type expression.
     *
     * @return type expression.
     */
    String getTypeExpression();

    /**
     * Gets namespace or alias retrieved from the provided type expression.
     *
     * @return namespace or alias.
     */
    String getNamespaceOrAlias();
}
