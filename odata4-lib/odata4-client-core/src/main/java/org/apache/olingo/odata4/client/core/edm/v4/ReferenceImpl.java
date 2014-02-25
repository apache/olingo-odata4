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
package org.apache.olingo.odata4.client.core.edm.v4;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.v4.Annotation;
import org.apache.olingo.odata4.client.api.edm.v4.Reference;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmItem;

@JsonDeserialize(using = ReferenceDeserializer.class)
public class ReferenceImpl extends AbstractEdmItem implements Reference {

  private static final long serialVersionUID = -5600031479702563436L;

  private URI uri;

  private final List<IncludeImpl> includes = new ArrayList<IncludeImpl>();

  private final List<IncludeAnnotationsImpl> includeAnnotations = new ArrayList<IncludeAnnotationsImpl>();

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public void setUri(final URI uri) {
    this.uri = uri;
  }

  @Override
  public List<IncludeImpl> getIncludes() {
    return includes;
  }

  @Override
  public List<IncludeAnnotationsImpl> getIncludeAnnotations() {
    return includeAnnotations;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

}
