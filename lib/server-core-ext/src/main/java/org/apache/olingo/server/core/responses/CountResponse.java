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
package org.apache.olingo.server.core.responses;

import java.util.Map;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.FixedFormatSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ServiceRequest;

public class CountResponse extends ServiceResponse {
  private final FixedFormatSerializer serializer;

  public static CountResponse getInstance(ServiceRequest request, ODataResponse response) {
    FixedFormatSerializer serializer = request.getOdata().createFixedFormatSerializer();
    return new CountResponse(request.getServiceMetaData(), serializer, response,
        request.getPreferences());
  }

  private CountResponse(ServiceMetadata metadata, FixedFormatSerializer serializer,
      ODataResponse response, Map<String, String> preferences) {
    super(metadata, response, preferences);
    this.serializer = serializer;
  }

  public void writeCount(int count) throws SerializerException {
    assert (!isClosed());

    this.response.setContent(this.serializer.count(count));
    writeOK(ContentType.TEXT_PLAIN);
    close();
  }

  @Override
  public void accepts(ServiceResponseVisior visitor) throws ODataLibraryException,
      ODataApplicationException {
    visitor.visit(this);
  }
}
