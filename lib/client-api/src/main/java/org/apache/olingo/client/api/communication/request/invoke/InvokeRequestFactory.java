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
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * OData request factory class.
 */
public interface InvokeRequestFactory {

  /**
   * Gets an invoke request instance for the operation bound to given URI.
   * <br/>
   * This method is mainly meant for internal usage, but defined for generic calls from proxy; normally, one of other
   * methods should be used instead.
   *
   * @param <T> OData domain object result
   * @param method HTTP invocation method
   * @param uri invocation URI
   * @param resultRef reference Class for result
   * @param parameters parameters to pass to function invocation
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getInvokeRequest(
          HttpMethod method, URI uri, Class<T> resultRef, Map<String, ClientValue> parameters);

  /**
   * Gets an invoke request instance for the function bound to given URI (no parameters).
   *
   * @param <T> OData domain object result
   * @param uri invocation URI
   * @param resultRef reference Class for result
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getFunctionInvokeRequest(URI uri, Class<T> resultRef);

  /**
   * Gets an invoke request instance for the function bound to given URI (with parameters).
   *
   * @param <T> OData domain object result
   * @param uri invocation URI
   * @param resultRef reference Class for result
   * @param parameters parameters to pass to function invocation
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getFunctionInvokeRequest(
          URI uri, Class<T> resultRef, Map<String, ClientValue> parameters);

  /**
   * Gets an invoke request instance for the action bound to given URI (no parameters).
   *
   * @param <T> OData domain object result
   * @param uri invocation URI
   * @param resultRef reference Class for result
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getActionInvokeRequest(URI uri, Class<T> resultRef);

  /**
   * Gets an invoke request instance for the action bound to given URI (with parameters).
   *
   * @param <T> OData domain object result
   * @param uri invocation URI
   * @param resultRef reference Class for result
   * @param parameters parameters to pass to action invocation
   * @return new {@link ODataInvokeRequest} instance.
   */
  <T extends ClientInvokeResult> ODataInvokeRequest<T> getActionInvokeRequest(
          URI uri, Class<T> resultRef, Map<String, ClientValue> parameters);
}
