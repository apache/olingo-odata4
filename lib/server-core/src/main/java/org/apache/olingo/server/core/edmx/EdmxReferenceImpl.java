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
package org.apache.olingo.server.core.edmx;


import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.api.edmx.EdmxReferenceIncludeAnnotation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EdmxReferenceImpl implements EdmxReference {
  private final URI uri;
  private final List<EdmxReferenceInclude> edmxIncludes;
  private final List<EdmxReferenceIncludeAnnotation> edmxIncludeAnnotations;

  public EdmxReferenceImpl(URI uri) {
    this.uri = uri;
    edmxIncludes = new ArrayList<EdmxReferenceInclude>();
    edmxIncludeAnnotations = new ArrayList<EdmxReferenceIncludeAnnotation>();
  }

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public List<EdmxReferenceInclude> getIncludes() {
    return Collections.unmodifiableList(edmxIncludes);
  }

  public void addInclude(EdmxReferenceInclude include) {
    edmxIncludes.add(include);
  }

  @Override
  public List<EdmxReferenceIncludeAnnotation> getIncludeAnnotations() {
    return Collections.unmodifiableList(edmxIncludeAnnotations);
  }

  public void addIncludeAnnotation(EdmxReferenceIncludeAnnotation includeAnnotation) {
    edmxIncludeAnnotations.add(includeAnnotation);
  }
}