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
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.serializer.ODataSerializer;

public class ServiceDocumentXmlSerializer {
  public static final String KIND = "kind";

  public static final String FUNCTION_IMPORT = "FunctionImport";
  public static final String SINGLETON = "Singleton";
  public static final String SERVICE_DOCUMENT = "ServiceDocument";
  
  private static final String APP = "app";
  private static final String NS_APP = "http://www.w3.org/2007/app";
  private static final String ATOM = "atom";
  private static final String NS_ATOM = "http://www.w3.org/2005/Atom";
  private static final String METADATA = "metadata";
  private static final String NS_METADATA = "http://docs.oasis-open.org/odata/ns/metadata";

  private final ServiceMetadata metadata;
  private final String serviceRoot;

  public ServiceDocumentXmlSerializer(final ServiceMetadata metadata, final String serviceRoot) {
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
    writer.writeAttribute(METADATA, NS_METADATA, "context", metadataUri);
    
    if (metadata != null
        && metadata.getServiceMetadataETagSupport() != null
        && metadata.getServiceMetadataETagSupport().getMetadataETag() != null) {
      writer.writeAttribute(METADATA, NS_METADATA, "metadata-etag",
          metadata.getServiceMetadataETagSupport().getMetadataETag());
    }

    writer.writeStartElement(APP, "workspace", NS_APP);
    
    final Edm edm = metadata.getEdm();

    writer.writeStartElement(ATOM, "title", NS_APP);
    writer.writeCharacters(edm.getEntityContainer(null).getFullQualifiedName().getFullQualifiedNameAsString());
    writer.writeEndElement();
    
    writeEntitySets(writer, edm);
    writeFunctionImports(writer, edm);
    writeSingletons(writer, edm);
    writeServiceDocuments(writer);
    writer.writeEndElement(); // end workspace
    writer.writeEndElement(); // end service   
  }

  private void writeServiceDocuments(XMLStreamWriter writer) throws XMLStreamException {
    
    for (EdmxReference reference : this.metadata.getReferences()) {
      writer.writeStartElement(METADATA , "service-document", NS_METADATA);
      writer.writeAttribute("href", reference.getUri().toASCIIString());
      writer.writeStartElement(ATOM, "title", NS_ATOM);
      writer.writeCharacters(reference.getUri().toASCIIString());
      writer.writeEndElement();
      writer.writeEndElement();
    }
  }

  private void writeEntitySets(final XMLStreamWriter writer, final Edm edm) throws XMLStreamException {
    EdmEntityContainer container = edm.getEntityContainer(null);
    for (EdmEntitySet edmEntitySet : container.getEntitySets()) {
      if (edmEntitySet.isIncludeInServiceDocument()) {
        writer.writeStartElement(APP, "collection", NS_APP);
        writer.writeAttribute("href", edmEntitySet.getName());
        writer.writeStartElement(ATOM, "title", NS_ATOM);
        writer.writeCharacters(edmEntitySet.getName());
        writer.writeEndElement();
        writer.writeEndElement();
      }
    }
  }

  private void writeFunctionImports(final XMLStreamWriter writer, final Edm edm) throws XMLStreamException {
    EdmEntityContainer container = edm.getEntityContainer(null);

    for (EdmFunctionImport edmFunctionImport : container.getFunctionImports()) {
      if (edmFunctionImport.isIncludeInServiceDocument()) {
        writer.writeStartElement(METADATA, "function-import", NS_METADATA);
        writer.writeAttribute("href", edmFunctionImport.getName());
        writer.writeStartElement(ATOM, "title", NS_ATOM);
        writer.writeCharacters(edmFunctionImport.getName());
        writer.writeEndElement();
        writer.writeEndElement();        
      }
    }
  }

  private void writeSingletons(final XMLStreamWriter writer, final Edm edm) throws XMLStreamException {
    EdmEntityContainer container = edm.getEntityContainer(null);
    for (EdmSingleton edmSingleton : container.getSingletons()) {
      writer.writeStartElement(METADATA, "singleton", NS_METADATA);
      writer.writeAttribute("href", edmSingleton.getName());
      writer.writeStartElement(ATOM, "title", NS_ATOM);
      writer.writeCharacters( edmSingleton.getName());
      writer.writeEndElement();
      writer.writeEndElement();
    }
  }
}
