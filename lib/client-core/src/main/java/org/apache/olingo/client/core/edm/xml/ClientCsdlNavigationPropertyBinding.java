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
package org.apache.olingo.client.core.edm.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;

import java.io.IOException;
import java.io.Serializable;

@JsonDeserialize(using = ClientCsdlNavigationPropertyBinding.NavigationPropertyBindingDeserializer.class)
class ClientCsdlNavigationPropertyBinding extends CsdlNavigationPropertyBinding implements Serializable {

  private static final long serialVersionUID = -7056978592235483660L;

  @Override
  public CsdlNavigationPropertyBinding setPath(final String path) {
    super.setPath(path);
    return this;
  }

  @Override
  public CsdlNavigationPropertyBinding setTarget(final String target) {
    super.setTarget(target);
    return this;
  }

  static class NavigationPropertyBindingDeserializer extends
          AbstractClientCsdlEdmDeserializer<CsdlNavigationPropertyBinding> {
    @Override
    protected CsdlNavigationPropertyBinding doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

      final ClientCsdlNavigationPropertyBinding member = new ClientCsdlNavigationPropertyBinding();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Path".equals(jp.getCurrentName())) {
            member.setPath(jp.nextTextValue());
          } else if ("Target".equals(jp.getCurrentName())) {
            member.setTarget(jp.nextTextValue());
          }
        }
      }
      return member;
    }
  }
}
