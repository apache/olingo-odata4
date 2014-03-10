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
package org.apache.olingo.client.core.op.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.client.api.op.ODataV3Deserializer;
import org.apache.olingo.client.core.data.v3.JSONServiceDocumentImpl;
import org.apache.olingo.client.core.data.v4.XMLServiceDocumentImpl;
import org.apache.olingo.client.core.edm.xml.v3.EdmxImpl;
import org.apache.olingo.client.core.edm.xml.v3.XMLMetadataImpl;
import org.apache.olingo.client.core.op.impl.AbstractODataDeserializer;

public class ODataV3DeserializerImpl extends AbstractODataDeserializer implements ODataV3Deserializer {

  private static final long serialVersionUID = -8221085862548914611L;

  public ODataV3DeserializerImpl(final ODataClient client) {
    super(client);
  }

  @Override
  public XMLMetadataImpl toMetadata(final InputStream input) {
    try {
      return new XMLMetadataImpl(getXmlMapper().readValue(input, EdmxImpl.class));
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not parse as Edmx document", e);
    }
  }

  @Override
  public ServiceDocument toServiceDocument(final InputStream input, final ODataFormat format) {
    try {
      return format == ODataFormat.XML
              ? getXmlMapper().readValue(input, XMLServiceDocumentImpl.class)
              : getObjectMapper().readValue(input, JSONServiceDocumentImpl.class);
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not parse Service Document", e);
    }
  }

//    @Override
//    protected JSONEntry toJSONEntry(final InputStream input) {
//        try {
//            return getObjectMapper().readValue(input, JSONEntry.class);
//        } catch (IOException e) {
//            throw new IllegalArgumentException("While deserializing JSON entry", e);
//        }
//    }
}
