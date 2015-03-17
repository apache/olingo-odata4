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
package org.apache.olingo.server.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.Mapping;
import org.apache.olingo.commons.api.edm.provider.Parameter;
import org.apache.olingo.commons.api.edm.provider.Property;
import org.junit.Test;

public class EdmMappingTest {

  @Test
  public void initialMappingMustBeNull() {
    Property property = new Property().setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName());
    EdmProperty edmProperty = new EdmPropertyImpl(null, new FullQualifiedName("namespace.name"), property);

    assertNull(edmProperty.getMapping());

    Parameter parameter = new Parameter().setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName());
    EdmParameter edmParameter = new EdmParameterImpl(null, parameter);

    assertNull(edmParameter.getMapping());
  }

  @Test
  public void getDataClassForPrimTypeViaMapping() {
    Mapping mapping = new Mapping().setMappedJavaClass(Date.class);
    Property property = new Property()
        .setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName())
        .setMapping(mapping);
    EdmProperty edmProperty = new EdmPropertyImpl(null, new FullQualifiedName("namespace.name"), property);

    assertNotNull(edmProperty.getMapping());
    assertEquals(Date.class, edmProperty.getMapping().getMappedJavaClass());

    Parameter parameter = new Parameter()
        .setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName())
        .setMapping(mapping);
    EdmParameter edmParameter = new EdmParameterImpl(null, parameter);

    assertNotNull(edmParameter.getMapping());
    assertEquals(Date.class, edmParameter.getMapping().getMappedJavaClass());
  }
}
