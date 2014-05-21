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
package org.apache.olingo.client.core.communication.request.retrieve.v4;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.client.api.edm.xml.v4.Annotations;
import org.apache.olingo.client.api.edm.xml.v4.Include;
import org.apache.olingo.client.api.edm.xml.v4.IncludeAnnotations;
import org.apache.olingo.client.api.edm.xml.v4.Reference;
import org.apache.olingo.client.api.edm.xml.v4.XMLMetadata;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.communication.request.retrieve.AbstractMetadataRequestImpl;
import org.apache.olingo.client.core.edm.xml.v4.AnnotationsImpl;
import org.apache.olingo.client.core.edm.xml.v4.SchemaImpl;

public class XMLMetadataRequestImpl extends AbstractMetadataRequestImpl<Map<String, Schema>>
        implements XMLMetadataRequest {

  XMLMetadataRequestImpl(final ODataClient odataClient, final URI uri) {
    super(odataClient, uri);
  }

  @Override
  public ODataRetrieveResponse<Map<String, Schema>> execute() {
    final SingleXMLMetadatRequestImpl rootReq = new SingleXMLMetadatRequestImpl((ODataClient) odataClient, uri);
    final ODataRetrieveResponse<XMLMetadata> rootRes = rootReq.execute();

    final XMLMetadataResponseImpl response = new XMLMetadataResponseImpl();

    final XMLMetadata rootMetadata = rootRes.getBody();
    for (Schema schema : rootMetadata.getSchemas()) {
      response.getBody().put(schema.getNamespace(), schema);
      if (StringUtils.isNotBlank(schema.getAlias())) {
        response.getBody().put(schema.getAlias(), schema);
      }
    }

    // process external references
    for (Reference reference : rootMetadata.getReferences()) {
      final SingleXMLMetadatRequestImpl includeReq = new SingleXMLMetadatRequestImpl(
              (ODataClient) odataClient, odataClient.newURIBuilder(reference.getUri().toASCIIString()).build());
      final XMLMetadata includeMetadata = includeReq.execute().getBody();

      // edmx:Include
      for (Include include : reference.getIncludes()) {
        final Schema includedSchema = includeMetadata.getSchema(include.getNamespace());
        if (includedSchema != null) {
          response.getBody().put(include.getNamespace(), includedSchema);
          if (StringUtils.isNotBlank(include.getAlias())) {
            response.getBody().put(include.getAlias(), includedSchema);
          }
        }
      }

      // edmx:IncludeAnnotations
      for (IncludeAnnotations include : reference.getIncludeAnnotations()) {
        for (Schema schema : includeMetadata.getSchemas()) {
          // create empty schema that will be fed with edm:Annotations that match the criteria in IncludeAnnotations
          final SchemaImpl forInclusion = new SchemaImpl();
          forInclusion.setNamespace(schema.getNamespace());
          forInclusion.setAlias(schema.getAlias());

          // process all edm:Annotations in each schema of the included document
          for (Annotations annotationGroup : ((SchemaImpl) schema).getAnnotationGroups()) {
              // take into account only when (TargetNamespace was either not provided or matches) and
            // (Qualifier was either not provided or matches)
            if ((StringUtils.isBlank(include.getTargetNamespace())
                    || include.getTargetNamespace().equals(
                            StringUtils.substringBeforeLast(annotationGroup.getTarget(), ".")))
                    && (StringUtils.isBlank(include.getQualifier())
                    || include.getQualifier().equals(annotationGroup.getQualifier()))) {

              final AnnotationsImpl toBeIncluded = new AnnotationsImpl();
              toBeIncluded.setTarget(annotationGroup.getTarget());
              toBeIncluded.setQualifier(annotationGroup.getQualifier());
              // only import annotations with terms matching the given TermNamespace
              for (Annotation annotation : annotationGroup.getAnnotations()) {
                if (include.getTermNamespace().equals(StringUtils.substringBeforeLast(annotation.getTerm(), "."))) {
                  toBeIncluded.getAnnotations().add(annotation);
                }
              }
              forInclusion.getAnnotationGroups().add(toBeIncluded);
            }
          }

          if (!forInclusion.getAnnotationGroups().isEmpty()) {
            response.getBody().put(forInclusion.getNamespace(), forInclusion);
            if (StringUtils.isNotBlank(forInclusion.getAlias())) {
              response.getBody().put(forInclusion.getAlias(), forInclusion);
            }
          }
        }
      }
    }

    return response;
  }

  private class SingleXMLMetadatRequestImpl extends AbstractMetadataRequestImpl<XMLMetadata> {

    public SingleXMLMetadatRequestImpl(final ODataClient odataClient, final URI uri) {
      super(odataClient, uri);
    }

    @Override
    public ODataRetrieveResponse<XMLMetadata> execute() {
      return new AbstractODataRetrieveResponse(httpClient, doExecute()) {

        @Override
        public XMLMetadata getBody() {
          try {
            return ((ODataClient) odataClient).getDeserializer().toMetadata(getRawResponse());
          } finally {
            this.close();
          }
        }
      };
    }
  }

  public class XMLMetadataResponseImpl extends AbstractODataRetrieveResponse {

    private final Map<String, Schema> schemas = new HashMap<String, Schema>();

    /**
     * Constructor.
     * <br/>
     * Just to create response templates to be initialized from batch.
     */
    private XMLMetadataResponseImpl() {
      super();
    }

    @Override
    public void close() {
      // just do nothing, this is a placeholder response
    }

    @Override
    public Map<String, Schema> getBody() {
      return schemas;
    }
  }

}
