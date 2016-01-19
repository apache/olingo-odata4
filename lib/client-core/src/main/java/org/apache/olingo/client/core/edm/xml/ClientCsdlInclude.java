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

import org.apache.olingo.client.api.edm.xml.Include;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmItem;

import java.io.IOException;
import java.io.Serializable;

@JsonDeserialize(using = ClientCsdlInclude.IncludeDeserializer.class)
class ClientCsdlInclude extends CsdlAbstractEdmItem implements Serializable, Include {

  private static final long serialVersionUID = -5450008299655584221L;

  private String namespace;
  private String alias;

  @Override
  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(final String namespace) {
    this.namespace = namespace;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(final String alias) {
    this.alias = alias;
  }

  static class IncludeDeserializer extends AbstractClientCsdlEdmDeserializer<Include> {
    @Override
    protected Include doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

      final ClientCsdlInclude include = new ClientCsdlInclude();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Namespace".equals(jp.getCurrentName())) {
            include.setNamespace(jp.nextTextValue());
          } else if ("Alias".equals(jp.getCurrentName())) {
            include.setAlias(jp.nextTextValue());
          }
        }
      }
      return include;
    }
  }
}
