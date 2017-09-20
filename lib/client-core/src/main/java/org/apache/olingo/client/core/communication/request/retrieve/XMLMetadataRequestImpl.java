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
package org.apache.olingo.client.core.communication.request.retrieve;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.Include;
import org.apache.olingo.client.api.edm.xml.IncludeAnnotations;
import org.apache.olingo.client.api.edm.xml.Reference;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;

public class XMLMetadataRequestImpl
    extends AbstractMetadataRequestImpl<XMLMetadata>
    implements XMLMetadataRequest {

  XMLMetadataRequestImpl(final ODataClient odataClient, final URI uri) {
    super(odataClient, uri);
  }

  @Override
  public ODataRetrieveResponse<XMLMetadata> execute() {
    SingleXMLMetadatRequestImpl rootReq = new SingleXMLMetadatRequestImpl(odataClient, uri);
    if (getPrefer() != null) {
      rootReq.setPrefer(getPrefer());
    }
    if (getIfMatch() != null) {
      rootReq.setIfMatch(getIfMatch());
    }
    if (getIfNoneMatch() != null) {
      rootReq.setIfNoneMatch(getIfNoneMatch());
    }
    if (getHeader() != null) {
      for (String key : getHeaderNames()) {
        rootReq.addCustomHeader(key, odataHeaders.getHeader(key));
      }
    }
    final ODataRetrieveResponse<XMLMetadata> rootRes = rootReq.execute();

    if (rootRes.getStatusCode() != HttpStatusCode.OK.getStatusCode()) {
      return rootRes;
    }
    final XMLMetadataResponseImpl response =
        new XMLMetadataResponseImpl(odataClient, httpClient, rootReq.getHttpResponse(), rootRes.getBody());

    // process external references
    for (Reference reference : rootRes.getBody().getReferences()) {
      final SingleXMLMetadatRequestImpl includeReq = new SingleXMLMetadatRequestImpl(
          odataClient,
          odataClient.newURIBuilder(uri.resolve(reference.getUri()).toASCIIString()).build());
      // Copying the headers from first request to next request
      for(String key : rootReq.getHeaderNames()){
         includeReq.addCustomHeader(key ,rootReq.getHeader(key));
      }
      final XMLMetadata includeMetadata = includeReq.execute().getBody();

      // edmx:Include
      for (Include include : reference.getIncludes()) {
        final CsdlSchema includedSchema = includeMetadata.getSchema(include.getNamespace());
        if (includedSchema != null) {
          response.getBody().getSchemas().add(includedSchema);
          if (StringUtils.isNotBlank(include.getAlias())) {
            includedSchema.setAlias(include.getAlias());
          }
        }
      }

      // edmx:IncludeAnnotations
      for (IncludeAnnotations include : reference.getIncludeAnnotations()) {
        for (CsdlSchema schema : includeMetadata.getSchemas()) {
          // create empty schema that will be fed with edm:Annotations that match the criteria in IncludeAnnotations
          final CsdlSchema forInclusion = new CsdlSchema();
          forInclusion.setNamespace(schema.getNamespace());
          forInclusion.setAlias(schema.getAlias());

          // process all edm:Annotations in each schema of the included document
          for (CsdlAnnotations annotationGroup : schema.getAnnotationGroups()) {
            // take into account only when (TargetNamespace was either not provided or matches) and
            // (Qualifier was either not provided or matches)
            if ((StringUtils.isBlank(include.getTargetNamespace())
                || include.getTargetNamespace().equals(
                    StringUtils.substringBeforeLast(annotationGroup.getTarget(), ".")))
                && (StringUtils.isBlank(include.getQualifier())
                    || include.getQualifier().equals(annotationGroup.getQualifier()))) {

              final CsdlAnnotations toBeIncluded = new CsdlAnnotations();
              toBeIncluded.setTarget(annotationGroup.getTarget());
              toBeIncluded.setQualifier(annotationGroup.getQualifier());
              // only import annotations with terms matching the given TermNamespace
              for (CsdlAnnotation annotation : annotationGroup.getAnnotations()) {
                if (include.getTermNamespace().equals(StringUtils.substringBeforeLast(annotation.getTerm(), "."))) {
                  toBeIncluded.getAnnotations().add(annotation);
                }
              }
              forInclusion.getAnnotationGroups().add(toBeIncluded);
            }
          }

          if (!forInclusion.getAnnotationGroups().isEmpty()) {
            response.getBody().getSchemas().add(forInclusion);
          }
        }
      }
    }

    return response;
  }

  private class SingleXMLMetadatRequestImpl extends AbstractMetadataRequestImpl<XMLMetadata> {

    private HttpResponse httpResponse;

    public SingleXMLMetadatRequestImpl(final ODataClient odataClient, final URI uri) {
      super(odataClient, uri);
    }

    public HttpResponse getHttpResponse() {
      return httpResponse;
    }

    @Override
    protected void checkRequest(final ODataClient odataClient, final HttpUriRequest request) {
      // override the parent check, as the reference urls in metadata can be spanning cross-site
    }

    @Override
    public ODataRetrieveResponse<XMLMetadata> execute() {
      httpResponse = doExecute();
      return new AbstractODataRetrieveResponse(odataClient, httpClient, httpResponse) {

        private XMLMetadata metadata = null;

        @Override
        public XMLMetadata getBody() {
          if (metadata == null) {
            try {
              metadata = odataClient.getDeserializer(ContentType.APPLICATION_XML).toMetadata(getRawResponse());
            } finally {
              this.close();
            }
          }
          return metadata;
        }
      };
    }
  }

  private class XMLMetadataResponseImpl extends AbstractODataRetrieveResponse {

    private final XMLMetadata metadata;

    private XMLMetadataResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
        final HttpResponse res, final XMLMetadata metadata) {

      super(odataClient, httpClient, null);
      initFromHttpResponse(res);
      this.metadata = metadata;
    }

    @Override
    public XMLMetadata getBody() {
      return metadata;
    }
  }

}
