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
package org.apache.olingo.server.core;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.serializer.FixedFormatSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveValueSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;

public class PrimitiveValueResponse extends ServiceResponse {
  private final boolean returnCollection;
  private EdmProperty type;
  private EdmReturnType returnType;
  private final FixedFormatSerializer serializer;

  public static PrimitiveValueResponse getInstance(ServiceRequest request, ODataResponse response,
      boolean collection, EdmProperty type) {
    FixedFormatSerializer serializer = request.getOdata().createFixedFormatSerializer();
    return new PrimitiveValueResponse(serializer, response, collection, type);
  }

  public static PrimitiveValueResponse getInstance(ServiceRequest request, ODataResponse response,
      boolean collection, EdmReturnType type) {
    FixedFormatSerializer serializer = request.getOdata().createFixedFormatSerializer();
    return new PrimitiveValueResponse(serializer, response, collection, type);
  }

  private PrimitiveValueResponse(FixedFormatSerializer serializer, ODataResponse response,
      boolean collection, EdmProperty type) {
    super(response);
    this.returnCollection = collection;
    this.type = type;
    this.serializer = serializer;
  }

  private PrimitiveValueResponse(FixedFormatSerializer serializer, ODataResponse response,
      boolean collection, EdmReturnType type) {
    super(response);
    this.returnCollection = collection;
    this.returnType = type;
    this.serializer = serializer;
  }

  public void write(Object value) throws SerializerException {

    if (value == null) {
      writeNoContent(true);
      return;
    }

    if (this.type != null) {
      PrimitiveValueSerializerOptions options = PrimitiveValueSerializerOptions.with()
          .facetsFrom(this.type).build();

      this.response.setContent(this.serializer.primitiveValue((EdmPrimitiveType) this.type.getType(),
          value, options));
    } else {
      PrimitiveValueSerializerOptions options = PrimitiveValueSerializerOptions.with()
          .nullable(this.returnType.isNullable()).maxLength(this.returnType.getMaxLength())
          .precision(this.returnType.getPrecision()).scale(this.returnType.getScale()).build();
      this.response.setContent(this.serializer.primitiveValue(
          (EdmPrimitiveType) this.returnType.getType(), value, options));
    }

    writeOK(HttpContentType.TEXT_PLAIN);
  }

  public boolean isReturnCollection() {
    return returnCollection;
  }

  @Override
  public void accepts(ServiceResponseVisior visitor) throws ODataTranslatedException,
      ODataApplicationException {
    visitor.visit(this);
  }
}
