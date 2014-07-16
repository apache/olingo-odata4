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
package org.apache.olingo.client.core.edm.xml.v4;

import org.apache.olingo.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.client.api.edm.xml.v4.Parameter;
import org.apache.olingo.client.core.edm.xml.AbstractParameter;
import org.apache.olingo.commons.api.edm.geo.SRID;

import java.util.ArrayList;
import java.util.List;

public class ParameterImpl extends AbstractParameter implements Parameter {

  private static final long serialVersionUID = -1067642515116697747L;

  private SRID srid;

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  @Override
  public SRID getSrid() {
    return srid;
  }

  public void setSrid(final SRID srid) {
    this.srid = srid;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

}
