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
package org.apache.olingo.client.core.edm.xml;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.apache.olingo.client.api.edm.xml.DataServices;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmItem;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ClientCsdlDataServices.DataServicesDeserializer.class)
class ClientCsdlDataServices extends CsdlAbstractEdmItem implements Serializable, DataServices {

  private static final long serialVersionUID = 4200317286476885204L;

  private final List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
  
  private String dataServiceVersion;

  private String maxDataServiceVersion;

  @Override
  public String getDataServiceVersion() {
    return dataServiceVersion;
  }

  public void setDataServiceVersion(final String version) {
    this.dataServiceVersion = version;
  }

  @Override
  public String getMaxDataServiceVersion() {
    return maxDataServiceVersion;
  }

  public void setMaxDataServiceVersion(final String version) {
    this.maxDataServiceVersion = version;
  }

  @Override
  public List<CsdlSchema> getSchemas() {
    return schemas;
  }

  static class DataServicesDeserializer extends AbstractClientCsdlEdmDeserializer<ClientCsdlDataServices> {

    @Override
    protected ClientCsdlDataServices doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

      final ClientCsdlDataServices dataServices = new ClientCsdlDataServices();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("DataServiceVersion".equals(jp.getCurrentName())) {
            dataServices.setDataServiceVersion(jp.nextTextValue());
          } else if ("MaxDataServiceVersion".equals(jp.getCurrentName())) {
            dataServices.setMaxDataServiceVersion(jp.nextTextValue());
          } else if ("Schema".equals(jp.getCurrentName())) {
            jp.nextToken();
            dataServices.getSchemas().add(jp.readValueAs(ClientCsdlSchema.class));
          }
        }
      }

      return dataServices;
    }
  }
}
