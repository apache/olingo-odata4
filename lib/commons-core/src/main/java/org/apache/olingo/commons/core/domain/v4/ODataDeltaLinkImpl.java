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
package org.apache.olingo.commons.core.domain.v4;

import org.apache.olingo.commons.api.domain.ODataItem;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.commons.api.domain.v4.ODataDeltaLink;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ODataDeltaLinkImpl extends ODataItem implements ODataDeltaLink {

  private static final long serialVersionUID = -6686550836508873044L;

  private URI source;

  private String relationship;

  private URI target;

  private final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

  public ODataDeltaLinkImpl() {
    super(null);
  }

  @Override
  public URI getSource() {
    return source;
  }

  @Override
  public void setSource(final URI source) {
    this.source = source;
  }

  @Override
  public String getRelationship() {
    return relationship;
  }

  @Override
  public void setRelationship(final String relationship) {
    this.relationship = relationship;
  }

  @Override
  public URI getTarget() {
    return target;
  }

  @Override
  public void setTarget(final URI target) {
    this.target = target;
  }

  @Override
  public List<ODataAnnotation> getAnnotations() {
    return annotations;
  }

}
