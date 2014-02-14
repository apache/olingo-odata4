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
package com.msopentech.odatajclient.engine.data;

import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;

/**
 * OData writer.
 * <br/>
 * Use this interface to serialize an OData request body.
 * <br/>
 * This interface provides method helpers to serialize a set of entities and a single entity as well.
 */
public interface ODataWriter extends Serializable {

    /**
     * Writes a collection of OData entities.
     *
     * @param entities entities to be serialized.
     * @param format serialization format.
     * @return stream of serialized objects.
     */
    InputStream writeEntities(Collection<ODataEntity> entities, ODataPubFormat format);

    /**
     * Writes a collection of OData entities.
     *
     * @param entities entities to be serialized.
     * @param format serialization format.
     * @param outputType whether to explicitly output type information.
     * @return stream of serialized objects.
     */
    InputStream writeEntities(Collection<ODataEntity> entities, ODataPubFormat format, boolean outputType);

    /**
     * Serializes a single OData entity.
     *
     * @param entity entity to be serialized.
     * @param format serialization format.
     * @return stream of serialized object.
     */
    InputStream writeEntity(ODataEntity entity, ODataPubFormat format);

    /**
     * Serializes a single OData entity.
     *
     * @param entity entity to be serialized.
     * @param format serialization format.
     * @param outputType whether to explicitly output type information.
     * @return stream of serialized object.
     */
    InputStream writeEntity(ODataEntity entity, ODataPubFormat format, boolean outputType);

    /**
     * Writes a single OData entity property.
     *
     * @param property entity property to be serialized.
     * @param format serialization format.
     * @return stream of serialized object.
     */
    InputStream writeProperty(ODataProperty property, ODataFormat format);

    /**
     * Writes an OData link.
     *
     * @param link link to be serialized.
     * @param format serialization format.
     * @return stream of serialized object.
     */
    InputStream writeLink(ODataLink link, ODataFormat format);
}
