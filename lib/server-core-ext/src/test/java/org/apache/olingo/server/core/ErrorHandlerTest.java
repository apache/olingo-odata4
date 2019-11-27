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

import static org.junit.Assert.assertNotNull;

import java.io.FileReader;
import java.net.URI;
import java.util.Collections;
import java.util.Locale;

import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.etag.ServiceMetadataETagSupport;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.legacy.ProcessorServiceHandler;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.junit.Before;
import org.junit.Test;

public class ErrorHandlerTest {
  CsdlEdmProvider provider = null;
  
  @Before
  public void setUp() throws Exception {
    MetadataParser parser = new MetadataParser();
    parser.parseAnnotations(true);
    parser.useLocalCoreVocabularies(true);
    provider = (CsdlEdmProvider) parser.buildEdmProvider(new FileReader("src/test/resources/annotations.xml"));
  }
  
  @Test
  public void testError(){
    EdmxReference reference = new EdmxReference
        (URI.create("../v4.0/cs02/vocabularies/Org.OData.Core.V1.xml"));
    reference.addInclude(new EdmxReferenceInclude("Org.OData.Core.V1", "Core"));
    ServiceHandler handler = new ProcessorServiceHandler();
    ServiceMetadata metadata = new ServiceMetadataImpl(provider,  
        Collections.singletonList(reference), new ServiceMetadataETagSupport() {
      @Override
      public String getServiceDocumentETag() {
        return "W/\"serviceDocumentETag\"";
      }
      @Override
      public String getMetadataETag() {
        return "W/\"metadataETag\"";
      }
    } );
    OData odata = new ODataImpl();
    ErrorHandler error = new ErrorHandler(odata , metadata, handler,
        ContentType.APPLICATION_ATOM_XML);
    assertNotNull(error);
    UriValidationException e =new UriValidationException("message", 
        UriValidationException.MessageKeys.DOUBLE_KEY_PROPERTY , "param"); 
    ODataRequest request = new ODataRequest();
    ODataResponse response = new ODataResponse();
    error.handleException(e, request , response);
    error.handleException(new UriParserSemanticException("message",
        UriParserSemanticException.MessageKeys.COLLECTION_NOT_ALLOWED, "param")
        , request , response);
    error.handleException(new UriParserSyntaxException("message",
        UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION, "param")
        , request , response);
    String[] param = new String[2];
    error.handleException(new UriParserExceptionCustom("message",
        UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION, param)
        , request , response);
    error.handleException(new BatchDeserializerException("message",
        BatchDeserializerException.MessageKeys.INVALID_BASE_URI, "param")
        , request , response);
    error.handleException(new SerializerException("message",
        SerializerException.MessageKeys.IO_EXCEPTION, "param")
        , request , response);
    error.handleException(new ContentNegotiatorException("message",
        ContentNegotiatorException.MessageKeys.NO_CONTENT_TYPE_SUPPORTED, "param")
        , request , response);
    error.handleException(new UriParserSyntaxException("message",
        UriParserSyntaxException.MessageKeys.DUPLICATED_ALIAS, "param")
        , request , response);
    error.handleException(new DeserializerException("message",
        DeserializerException.MessageKeys.DUPLICATE_PROPERTY, "param")
        , request , response);
    error.handleException(new ODataHandlerException("message",
        ODataHandlerException.MessageKeys.AMBIGUOUS_XHTTP_METHOD, "param")
        , request , response);
    error.handleException(new ODataApplicationException("message",
        500, Locale.ENGLISH)
        , request , response);
    error.handleException(new NullPointerException("message")
        , request , response);
    error.handleServerError(request, response, new ODataServerError());
  }

  class UriParserExceptionCustom extends UriParserException{

    public UriParserExceptionCustom(String developmentMessage, MessageKey messageKey,
        String[] parameters) {
      super(developmentMessage, messageKey, parameters);
    }
    
  }
}
