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
package org.apache.olingo.client.core.edm.xml;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.apache.olingo.client.api.edm.xml.Include;
import org.apache.olingo.client.api.edm.xml.IncludeAnnotations;
import org.apache.olingo.client.api.edm.xml.Reference;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmItem;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ClientCsdlReference.ReferenceDeserializer.class)
class ClientCsdlReference extends CsdlAbstractEdmItem implements Serializable, Reference {

  private static final long serialVersionUID = 7720274712545267654L;

  private URI uri;
  private final List<Include> includes = new ArrayList<Include>();
  private final List<IncludeAnnotations> includeAnnotations = new ArrayList<IncludeAnnotations>();
  private final List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
  
  @Override
  public URI getUri() {
    return uri;
  }

  public void setUri(final URI uri) {
    this.uri = uri;
  }

  @Override
  public List<Include> getIncludes() {
    return includes;
  }

  @Override
  public List<IncludeAnnotations> getIncludeAnnotations() {
    return includeAnnotations;
  }

  static class ReferenceDeserializer extends AbstractClientCsdlEdmDeserializer<ClientCsdlReference> {
    @Override
    protected ClientCsdlReference doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
      final ClientCsdlReference reference = new ClientCsdlReference();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Uri".equals(jp.getCurrentName())) {
            reference.setUri(URI.create(jp.nextTextValue()));
          } else if ("Include".equals(jp.getCurrentName())) {
            jp.nextToken();
            reference.getIncludes().add(jp.readValueAs( ClientCsdlInclude.class));
          } else if ("IncludeAnnotations".equals(jp.getCurrentName())) {
            jp.nextToken();
            reference.getIncludeAnnotations().add(jp.readValueAs( ClientCsdlIncludeAnnotations.class));
          } else if ("Annotation".equals(jp.getCurrentName())) {
            jp.nextToken();
            reference.getAnnotations().add(jp.readValueAs( ClientCsdlAnnotation.class));
          }
        }
      }

      return reference;
    }
  }
}
