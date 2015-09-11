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
package org.apache.olingo.client.api.data;

import java.net.URI;

/**
 * Outermost response payload wrapper.
 *
 * @param <T> the actual response payload
 */
public class ResWrap<T> {

  private final URI contextURL;
  private final String metadataETag;
  private final T payload;
  
  /**
   * Creates a new response payload wrapper
   * 
   * @param contextURL    Context URI of the response
   * @param metadataETag  ETag of the payload
   * @param payload       Payload of the response
   */
  public ResWrap(final URI contextURL, final String metadataETag, final T payload) {
    this.contextURL = contextURL;
    this.metadataETag = metadataETag;
    this.payload = payload;
  }

  /**
   * The context URL describes the content of the payload. It consists of the canonical metadata document URL and a
   * fragment identifying the relevant portion of the metadata document.
   * <br />
   * Request payloads generally do not require context URLs as the type of the payload can generally be determined from
   * the request URL.
   * <br />
   * For details on how the context URL is used to describe a payload, see the relevant sections in the particular
   * format.
   *
   * @return context URL.
   */
  public URI getContextURL() {
    return contextURL;
  }

  /**
   * An ETag header MAY also be returned on a metadata document request or service document request to allow the client
   * subsequently to make a conditional request for the metadata or service document. Clients can also compare the value
   * of the ETag header returned from a metadata document request to the metadata ETag returned in a response in order
   * to verify the version of the metadata used to generate that response.
   *
   * @return metadata ETag.
   */
  public String getMetadataETag() {
    return metadataETag;
  }

  /**
   * Gets contained object.
   *
   * @return contained object.
   */
  public T getPayload() {
    return payload;
  }
}
