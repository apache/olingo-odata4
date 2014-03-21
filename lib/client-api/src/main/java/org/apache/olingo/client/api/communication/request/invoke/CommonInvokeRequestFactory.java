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
package org.apache.olingo.client.api.communication.request.invoke;

import java.io.Serializable;
import java.net.URI;
import java.util.LinkedHashMap;
import org.apache.olingo.client.api.domain.ODataInvokeResult;
import org.apache.olingo.client.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

/**
 * OData request factory class.
 */
public interface CommonInvokeRequestFactory extends Serializable {

  /**
   * Gets an invoke request instance.
   *
   * @param <RES> OData domain object result, derived from return type defined in the function import
   * @param uri URI that identifies the function import
   * @param edm Edm metadata
   * @param container Entity container
   * @param functionImport function import to be invoked
   * @return new ODataInvokeRequest instance.
   */
  <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(
          URI uri, Edm edm, FullQualifiedName container, String functionImport);

  /**
   * Gets an invoke request instance.
   *
   * @param <RES> OData domain object result, derived from return type defined in the function import
   * @param uri URI that identifies the function import
   * @param edm Edm metadata
   * @param container Entity container
   * @param functionImport function import to be invoked
   * @param parameters parameters to pass to function import invocation
   * @return new ODataInvokeRequest instance.
   */
  <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(
          URI uri, Edm edm, FullQualifiedName container, String functionImport,
          LinkedHashMap<String, ODataValue> parameters);
}
