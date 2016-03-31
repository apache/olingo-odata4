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
package org.apache.olingo.server.core.serializer.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;

public class ServiceDocumentXmlSerializer {
  private static final String APP = "app";
  private static final String NS_APP = "http://www.w3.org/2007/app";
  private static final String ATOM = "atom";
  private static final String NS_ATOM = Constants.NS_ATOM;
  private static final String METADATA = "metadata";
  private static final String NS_METADATA = Constants.NS_METADATA;

  private final ServiceMetadata metadata;
  private final String serviceRoot;

  public ServiceDocumentXmlSerializer(final ServiceMetadata metadata, final String serviceRoot)
      throws SerializerException {
    if (metadata == null || metadata.getEdm() == null) {
      throw new SerializerException("Service Metadata and EDM must not be null for a service.",
          SerializerException.MessageKeys.NULL_METADATA_OR_EDM);
    }
    this.metadata = metadata;
    this.serviceRoot = serviceRoot;
  }

  public void writeServiceDocument(final XMLStreamWriter writer) throws XMLStreamException {
    final String metadataUri =
        (serviceRoot == null ? "" : serviceRoot.endsWith("/") ? serviceRoot : (serviceRoot + "/"))
            + Constants.METADATA;

    writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
    writer.writeStartElement(APP, "service", NS_APP);
    writer.writeNamespace(ATOM, NS_ATOM);
    writer.writeNamespace(APP, NS_APP);
    writer.writeNamespace(METADATA, NS_METADATA);
    writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT, metadataUri);

    if (metadata != null
        && metadata.getServiceMetadataETagSupport() != null
        && metadata.getServiceMetadataETagSupport().getMetadataETag() != null) {
      writer.writeAttribute(METADATA, NS_METADATA, Constants.ATOM_ATTR_METADATAETAG,
          metadata.getServiceMetadataETagSupport().getMetadataETag());
    }

    writer.writeStartElement(APP, "workspace", NS_APP);

    final EdmEntityContainer container = metadata.getEdm().getEntityContainer();
    if (container != null) {
      writer.writeStartElement(ATOM, Constants.ATOM_ELEM_TITLE, NS_ATOM);
      writer.writeCharacters(container.getFullQualifiedName().getFullQualifiedNameAsString());
      writer.writeEndElement();

      writeEntitySets(writer, container);
      writeFunctionImports(writer, container);
      writeSingletons(writer, container);
      writeServiceDocuments(writer);
    }
    writer.writeEndElement(); // end workspace
    writer.writeEndElement(); // end service
  }

  private void writeServiceDocuments(final XMLStreamWriter writer) throws XMLStreamException {
    for (EdmxReference reference : metadata.getReferences()) {
      final String referenceString = reference.getUri().toASCIIString();
      writeElement(writer, false, "service-document", referenceString, referenceString);
    }
  }

  private void writeEntitySets(final XMLStreamWriter writer, final EdmEntityContainer container)
      throws XMLStreamException {
    for (EdmEntitySet edmEntitySet : container.getEntitySets()) {
      if (edmEntitySet.isIncludeInServiceDocument()) {
        writeElement(writer, true, "collection", edmEntitySet.getName(), edmEntitySet.getTitle());
      }
    }
  }

  private void writeFunctionImports(final XMLStreamWriter writer, final EdmEntityContainer container)
      throws XMLStreamException {
    for (EdmFunctionImport edmFunctionImport : container.getFunctionImports()) {
      if (edmFunctionImport.isIncludeInServiceDocument()) {
        writeElement(writer, false, "function-import", edmFunctionImport.getName(), edmFunctionImport.getTitle());
      }
    }
  }

  private void writeSingletons(final XMLStreamWriter writer, final EdmEntityContainer container)
      throws XMLStreamException {
    for (EdmSingleton edmSingleton : container.getSingletons()) {
      writeElement(writer, false, "singleton", edmSingleton.getName(), edmSingleton.getTitle());
    }
  }

  private void writeElement(final XMLStreamWriter writer, final boolean isApp, final String kind, final String name,
      final String title) throws XMLStreamException {
    if (isApp) {
      writer.writeStartElement(APP, kind, NS_APP);
    } else {
      writer.writeStartElement(METADATA, kind, NS_METADATA);
    }
    writer.writeAttribute(Constants.ATTR_HREF, name);
    writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_NAME, name);
    writer.writeStartElement(ATOM, Constants.ATOM_ELEM_TITLE, NS_ATOM);
    if (title != null) {
      writer.writeCharacters(title);
    } else {
      writer.writeCharacters(name);
    }
    writer.writeEndElement();
    writer.writeEndElement();
  }
}
