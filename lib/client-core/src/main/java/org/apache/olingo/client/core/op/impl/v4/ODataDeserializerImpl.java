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
package org.apache.olingo.client.core.op.impl.v4;

import java.io.InputStream;

import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.edm.xml.v4.XMLMetadata;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.client.api.op.v4.ODataDeserializer;
import org.apache.olingo.client.core.data.v4.JSONServiceDocumentImpl;
import org.apache.olingo.client.core.data.v4.XMLServiceDocumentImpl;
import org.apache.olingo.client.core.edm.xml.v4.EdmxImpl;
import org.apache.olingo.client.core.edm.xml.v4.XMLMetadataImpl;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.core.op.AbstractODataDeserializer;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.data.v4.AtomDeltaImpl;
import org.apache.olingo.commons.core.data.v4.JSONDeltaImpl;

public class ODataDeserializerImpl extends AbstractODataDeserializer implements ODataDeserializer {

  private static final long serialVersionUID = 8593081342440470415L;

  public ODataDeserializerImpl(final ODataServiceVersion version) {
    super(version);
  }

  @Override
  public XMLMetadata toMetadata(final InputStream input) {
    try {
      return new XMLMetadataImpl(getXmlMapper().readValue(input, EdmxImpl.class));
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not parse as Edmx document", e);
    }
  }

  @Override
  public ResWrap<ServiceDocument> toServiceDocument(final InputStream input, final ODataFormat format) {
    return format == ODataFormat.XML
            ? this.<ServiceDocument, XMLServiceDocumentImpl>xml(input, XMLServiceDocumentImpl.class)
            : this.<ServiceDocument, JSONServiceDocumentImpl>json(input, JSONServiceDocumentImpl.class);

  }

  @Override
  public ResWrap<Delta> toDelta(final InputStream input, final ODataPubFormat format) {
    return format == ODataPubFormat.ATOM
            ? this.<Delta, AtomDeltaImpl>atom(input, AtomDeltaImpl.class)
            : this.<Delta, JSONDeltaImpl>json(input, JSONDeltaImpl.class);
  }

}
