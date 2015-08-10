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

import java.net.URI;
import java.util.Map;

import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public interface EdmEnabledInvokeRequestFactory extends InvokeRequestFactory {

  /**
   * Gets an invoke request instance for the function import with the given name and no parameters.
   *
   * @param <T> OData domain object result, derived from return type defined in the function import
   * @param functionImportName operation to be invoked
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getFunctionImportInvokeRequest(
          String functionImportName);

  /**
   * Gets an invoke request instance for the function import with the given name and matching parameter names.
   *
   * @param <T> OData domain object result, derived from return type defined in the function import
   * @param functionImportName operation to be invoked
   * @param parameters parameters to pass to operation import invocation
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getFunctionImportInvokeRequest(
          String functionImportName, Map<String, ClientValue> parameters);

  /**
   * Gets an invoke request instance for the action import with the given name.
   *
   * @param <T> OData domain object result, derived from return type defined in the action import
   * @param actionImportName operation to be invoked
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getActionImportInvokeRequest(
          String actionImportName);

  /**
   * Gets an invoke request instance for the action import with the given name.
   *
   * @param <T> OData domain object result, derived from return type defined in the action import
   * @param actionImportName operation to be invoked
   * @param parameters parameters to pass to operation import invocation
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getActionImportInvokeRequest(
          String actionImportName, Map<String, ClientValue> parameters);

  /**
   * Gets an invoke request instance for the function bound to given URI (no parameters).
   *
   * @param <T> OData domain object result
   * @param bindingParameterURI binding parameter URI
   * @param functionName operation to be invoked
   * @param bindingParameterTypeName binding parameter type full qualified name
   * @param isBindingParameterCollection whether binding parameter is collection
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getBoundFunctionInvokeRequest(
          URI bindingParameterURI, FullQualifiedName functionName, FullQualifiedName bindingParameterTypeName,
          Boolean isBindingParameterCollection);

  /**
   * Gets an invoke request instance for the function bound to given URI (with parameters).
   *
   * @param <T> OData domain object result
   * @param bindingParameterURI binding parameter URI
   * @param functionName operation to be invoked
   * @param bindingParameterTypeName binding parameter type full qualified name
   * @param isBindingParameterCollection whether binding parameter is collection
   * @param parameters parameters to pass to function invocation
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getBoundFunctionInvokeRequest(
          URI bindingParameterURI, FullQualifiedName functionName, FullQualifiedName bindingParameterTypeName,
          Boolean isBindingParameterCollection, Map<String, ClientValue> parameters);

  /**
   * Gets an invoke request instance for the action bound to given URI (no parameters).
   *
   * @param <T> OData domain object result
   * @param bindingParameterURI binding parameter URI
   * @param actionName operation to be invoked
   * @param bindingParameterTypeName binding parameter type full qualified name
   * @param isBindingParameterCollection whether binding parameter is collection
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getBoundActionInvokeRequest(
          URI bindingParameterURI, FullQualifiedName actionName, FullQualifiedName bindingParameterTypeName,
          Boolean isBindingParameterCollection);

  /**
   * Gets an invoke request instance for the action bound to given URI (with parameters).
   *
   * @param <T> OData domain object result
   * @param bindingParameterURI binding parameter URI
   * @param actionName operation to be invoked
   * @param bindingParameterTypeName binding parameter type full qualified name
   * @param isBindingParameterCollection whether binding parameter is collection
   * @param parameters parameters to pass to function invocation
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getBoundActionInvokeRequest(
          URI bindingParameterURI, FullQualifiedName actionName, FullQualifiedName bindingParameterTypeName,
          Boolean isBindingParameterCollection, Map<String, ClientValue> parameters);

}
