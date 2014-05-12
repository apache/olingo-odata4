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
package org.apache.olingo.commons.api.domain.v4;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class ODataLink extends org.apache.olingo.commons.api.domain.ODataLink implements ODataAnnotatatable {

  private static final long serialVersionUID = 8953805653775734101L;

  public static class Builder extends org.apache.olingo.commons.api.domain.ODataLink.Builder {

    @Override
    public Builder setVersion(final ODataServiceVersion version) {
      super.setVersion(version);
      return this;
    }

    @Override
    public Builder setURI(final URI uri) {
      super.setURI(uri);
      return this;
    }

    @Override
    public Builder setType(final ODataLinkType type) {
      super.setType(type);
      return this;
    }

    @Override
    public Builder setTitle(final String title) {
      super.setTitle(title);
      return this;
    }

    @Override
    public ODataLink build() {
      return new ODataLink(version, uri, type, title);
    }
  }

  private final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

  public ODataLink(final ODataServiceVersion version, final URI uri, final ODataLinkType type, final String title) {
    super(version, uri, type, title);
  }

  @Override
  public List<ODataAnnotation> getAnnotations() {
    return annotations;
  }

}
