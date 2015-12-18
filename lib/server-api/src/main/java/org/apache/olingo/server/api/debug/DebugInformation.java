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
package org.apache.olingo.server.api.debug;

import java.util.List;
import java.util.Map;

import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.uri.UriInfo;

/**
 * This class contains all information necessary to construct a debug response.
 */
public class DebugInformation {

  private ODataRequest request;
  private ODataResponse applicationResponse;
  private UriInfo uriInfo;
  private Exception exception;
  private Map<String, String> serverEnvironmentVariables;
  private List<RuntimeMeasurement> runtimeInformation;

  /**
   * This method will return the ODataRequest the library created. This request will never be null but might be filled
   * incompletely if there has been an exception during the request parsing.
   * @return the ODataRequest the library built
   */
  public ODataRequest getRequest() {
    return request;
  }

  public void setRequest(final ODataRequest request) {
    this.request = request;
  }

  /**
   * This method will return the ODataResponse which was filled by the Application or the library in an exception case.
   * The response might be null or might not be filled completely.
   * @return the response filled by the application
   */
  public ODataResponse getApplicationResponse() {
    return applicationResponse;
  }

  public void setApplicationResponse(final ODataResponse applicationResponse) {
    this.applicationResponse = applicationResponse;
  }

  /**
   * The URI Info object the library created during URI parsing. Might be null if there was an exception during URI
   * parsing.
   * @return the URI Info Object
   */
  public UriInfo getUriInfo() {
    return uriInfo;
  }

  public void setUriInfo(final UriInfo uriInfo) {
    this.uriInfo = uriInfo;
  }

  /**
   * This method will return any exception that was thrown from the application or library. Will be null if there was no
   * exception.
   * @return an exception if thrown.
   */
  public Exception getException() {
    return exception;
  }

  public void setException(final Exception exception) {
    this.exception = exception;
  }

  /**
   * A map containing information about the runtime environment. Depending on the servlet or webserver used this map
   * might contain different information. Will never be null but might be empty.
   * @return environment variables
   */
  public Map<String, String> getServerEnvironmentVariables() {
    return serverEnvironmentVariables;
  }

  public void setServerEnvironmentVariables(final Map<String, String> serverEnvironmentVariables) {
    this.serverEnvironmentVariables = serverEnvironmentVariables;
  }

  /**
   * This method will return all runtime information which was collected inside the library. Might be null if no data
   * could be collected.
   * @return runtime information collected by the library
   */
  public List<RuntimeMeasurement> getRuntimeInformation() {
    return runtimeInformation;
  }

  public void setRuntimeInformation(final List<RuntimeMeasurement> runtimeInformation) {
    this.runtimeInformation = runtimeInformation;
  }

}
