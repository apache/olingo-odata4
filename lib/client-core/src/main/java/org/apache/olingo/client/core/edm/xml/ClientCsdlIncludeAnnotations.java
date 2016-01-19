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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.olingo.client.api.edm.xml.IncludeAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmItem;

import java.io.IOException;
import java.io.Serializable;

@JsonDeserialize(using = ClientCsdlIncludeAnnotations.IncludeAnnotationsDeserializer.class)
class ClientCsdlIncludeAnnotations extends CsdlAbstractEdmItem implements Serializable, IncludeAnnotations {

  private static final long serialVersionUID = -8157841387011422396L;

  private String termNamespace;
  private String qualifier;
  private String targetNamespace;

  @Override
  public String getTermNamespace() {
    return termNamespace;
  }

  public void setTermNamespace(final String termNamespace) {
    this.termNamespace = termNamespace;
  }

  @Override
  public String getQualifier() {
    return qualifier;
  }

  public void setQualifier(final String qualifier) {
    this.qualifier = qualifier;
  }

  @Override
  public String getTargetNamespace() {
    return targetNamespace;
  }

  public void setTargetNamespace(final String targetNamespace) {
    this.targetNamespace = targetNamespace;
  }

  static class IncludeAnnotationsDeserializer extends AbstractClientCsdlEdmDeserializer<IncludeAnnotations> {
    @Override
    protected IncludeAnnotations doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

      final ClientCsdlIncludeAnnotations member = new ClientCsdlIncludeAnnotations();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("TermNamespace".equals(jp.getCurrentName())) {
            member.setTermNamespace(jp.nextTextValue());
          } else if ("Qualifier".equals(jp.getCurrentName())) {
            member.setQualifier(jp.nextTextValue());
          } else if ("TargetNamespace".equals(jp.getCurrentName())) {
            member.setTargetNamespace(jp.nextTextValue());
          }
        }
      }
      return member;
    }
  }
}
