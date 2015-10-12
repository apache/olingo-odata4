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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmReferentialConstraint;
import org.apache.olingo.commons.api.edm.provider.CsdlReferentialConstraint;
import org.apache.olingo.commons.core.edm.EdmReferentialConstraintImpl;
import org.junit.Test;

public class EdmReferentialConstraintTest {

  @Test
  public void initialConstraint() {
    CsdlReferentialConstraint constraint = new CsdlReferentialConstraint();
    EdmReferentialConstraint edmConstraint = new EdmReferentialConstraintImpl(mock(Edm.class), constraint);

    assertNull(edmConstraint.getPropertyName());
    assertNull(edmConstraint.getReferencedPropertyName());
  }

  @Test
  public void basicConstraint() {
    CsdlReferentialConstraint constraint = new CsdlReferentialConstraint();
    constraint.setProperty("PropertyName");
    constraint.setReferencedProperty("referencedProperty");
    EdmReferentialConstraint edmConstraint = new EdmReferentialConstraintImpl(mock(Edm.class), constraint);

    assertEquals("PropertyName", edmConstraint.getPropertyName());
    assertEquals("referencedProperty", edmConstraint.getReferencedPropertyName());
  }

}
