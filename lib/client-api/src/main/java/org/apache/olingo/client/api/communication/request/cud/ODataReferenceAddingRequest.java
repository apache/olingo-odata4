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
package org.apache.olingo.client.api.communication.request.cud;

import org.apache.olingo.client.api.communication.request.ODataBasicRequest;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.response.ODataReferenceAddingResponse;

/**
 * This class implements an OData reference adding request.
 * 
 * ODataReferenceAdding requests eighter add or change the reference of navigation properties.
 * 
 * If the navigation property is a collection of navigation references, the request adds a new reference to the
 * collection. [OData Protocol 4.0 - 11.4.6.1]
 * 
 * If the request addresses an navigation property, which references a single entity, the reference will
 * be changed to the value provided by the request. [OData-Protocol 4.0 - 11.4.6.3]
 */
public interface ODataReferenceAddingRequest extends ODataBasicRequest<ODataReferenceAddingResponse>,
    ODataBatchableRequest {
//No additional methods needed for now.
}
