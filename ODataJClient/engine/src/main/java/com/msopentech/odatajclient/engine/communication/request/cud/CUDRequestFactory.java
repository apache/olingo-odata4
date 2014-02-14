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
package com.msopentech.odatajclient.engine.communication.request.cud;

import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import java.io.Serializable;
import java.net.URI;

/**
 * OData request factory class.
 */
public interface CUDRequestFactory extends Serializable {

    /**
     * Gets a create request object instance.
     * <br/>
     * Use this kind of request to create a new entity.
     *
     * @param targetURI entity set URI.
     * @param entity entity to be created.
     * @return new ODataEntityCreateRequest instance.
     */
    ODataEntityCreateRequest getEntityCreateRequest(URI targetURI, ODataEntity entity);

    /**
     * Gets an update request object instance.
     *
     * @param targetURI edit link of the object to be updated.
     * @param type type of update to be performed.
     * @param changes changes to be applied.
     * @return new ODataEntityUpdateRequest instance.
     */
    ODataEntityUpdateRequest getEntityUpdateRequest(URI targetURI, UpdateType type, ODataEntity changes);

    /**
     * Gets an update request object instance; uses entity's edit link as endpoint.
     *
     * @param type type of update to be performed.
     * @param entity changes to be applied.
     * @return new ODataEntityUpdateRequest instance.
     */
    ODataEntityUpdateRequest getEntityUpdateRequest(UpdateType type, ODataEntity entity);

    /**
     * Gets a create request object instance.
     * <br/>
     * Use this kind of request to create a new value (e.g. http://Northwind.svc/Customer(1)/Picture/$value).
     *
     * @param targetURI entity set or entity or entity property URI.
     * @param type type of update to be performed.
     * @param value value to be created.
     * @return new ODataValueUpdateRequest instance.
     */
    ODataValueUpdateRequest getValueUpdateRequest(URI targetURI, UpdateType type, ODataPrimitiveValue value);

    /**
     * Gets an update request object instance.
     * <br/>
     * Use this kind of request to update a primitive property value.
     *
     * @param targetURI entity set or entity or entity property URI.
     * @param property value to be update.
     * @return new ODataPropertyUpdateRequest instance.
     */
    ODataPropertyUpdateRequest getPropertyPrimitiveValueUpdateRequest(URI targetURI, ODataProperty property);

    /**
     * Gets an update request object instance.
     * <br/>
     * Use this kind of request to update a complex property value.
     *
     * @param targetURI entity set or entity or entity property URI.
     * @param type type of update to be performed.
     * @param property value to be update.
     * @return new ODataPropertyUpdateRequest instance.
     */
    ODataPropertyUpdateRequest getPropertyComplexValueUpdateRequest(
            URI targetURI, UpdateType type, ODataProperty property);

    /**
     * Gets an update request object instance.
     * <br/>
     * Use this kind of request to update a collection property value.
     *
     * @param targetURI entity set or entity or entity property URI.
     * @param property value to be update.
     * @return new ODataPropertyUpdateRequest instance.
     */
    ODataPropertyUpdateRequest getPropertyCollectionValueUpdateRequest(URI targetURI, ODataProperty property);

    /**
     * Gets an add link request object instance.
     * <br/>
     * Use this kind of request to create a navigation link between existing entities.
     *
     * @param targetURI navigation property's link collection.
     * @param link navigation link to be added.
     * @return new ODataLinkCreateRequest instance.
     */
    ODataLinkCreateRequest getLinkCreateRequest(URI targetURI, ODataLink link);

    /**
     * Gets a link update request object instance.
     * <br/>
     * Use this kind of request to update a navigation link between existing entities.
     * <br/>
     * In case of the old navigation link doesn't exist the new one will be added as well.
     *
     * @param targetURI navigation property's link collection.
     * @param type type of update to be performed.
     * @param link URL that identifies the entity to be linked.
     * @return new ODataLinkUpdateRequest instance.
     */
    ODataLinkUpdateRequest getLinkUpdateRequest(URI targetURI, UpdateType type, ODataLink link);

    /**
     * Gets a delete request object instance.
     * <br/>
     * Use this kind of request to delete an entity and media entity as well.
     *
     * @param targetURI edit link of the object to be removed.
     * @return new ODataDeleteRequest instance.
     */
    ODataDeleteRequest getDeleteRequest(URI targetURI);
}
