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
package org.apache.olingo.client.core.edm.xml.v4.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.v4.annotation.AnnotationExpression;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Collection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CollectionDeserializer.class)
public class CollectionImpl extends AbstractDynamicAnnotationExpression implements Collection {

  private static final long serialVersionUID = -724749123749715643L;

  private final List<AnnotationExpression> items = new ArrayList<AnnotationExpression>();

  @Override
  public List<AnnotationExpression> getItems() {
    return items;
  }

}
