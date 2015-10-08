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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotationsTarget.TargetType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.commons.core.edm.EdmTermImpl;
import org.junit.Before;
import org.junit.Test;

public class EdmTermImplTest {

  private EdmTerm initialTerm;
  private EdmTerm derivedTerm;

  @Before
  public void setupTypes() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    CsdlTerm csdlTerm = new CsdlTerm();
    FullQualifiedName csdlTerm1Name = new FullQualifiedName("namespace", "name1");
    csdlTerm.setName(csdlTerm1Name.getName());
    csdlTerm.setType("Edm.String");
    when(provider.getTerm(csdlTerm1Name)).thenReturn(csdlTerm);
    initialTerm = new EdmTermImpl(edm, "namespace", csdlTerm);

    CsdlTerm derivedCsdlTerm = new CsdlTerm();
    FullQualifiedName derivedTermName = new FullQualifiedName("namespace", "name2");
    derivedCsdlTerm.setName(derivedTermName.getName());
    derivedCsdlTerm.setType("Edm.String");
    derivedCsdlTerm.setBaseTerm("namespace.name1");
    List<String> appliesTo = new ArrayList<String>();
    appliesTo.add("Property");
    derivedCsdlTerm.setAppliesTo(appliesTo);
    List<CsdlAnnotation> csdlAnnotations = new ArrayList<CsdlAnnotation>();
    csdlAnnotations.add(new CsdlAnnotation().setTerm("name1"));
    derivedCsdlTerm.setAnnotations(csdlAnnotations );
    
    derivedCsdlTerm.setNullable(false);
    derivedCsdlTerm.setMaxLength(new Integer(15));
    derivedCsdlTerm.setDefaultValue("abc");
    derivedCsdlTerm.setPrecision(new Integer(14));
    derivedCsdlTerm.setScale(new Integer(13));
    
    when(provider.getTerm(derivedTermName)).thenReturn(derivedCsdlTerm);
    derivedTerm = new EdmTermImpl(edm, "namespace", derivedCsdlTerm);
    
  }

  @Test
  public void termBasics() throws Exception {
    assertEquals("name1", initialTerm.getName());
    assertEquals(new FullQualifiedName("namespace", "name1"), initialTerm.getFullQualifiedName());
    
    assertTrue(initialTerm.getAnnotations().isEmpty());
    assertTrue(initialTerm.getAppliesTo().isEmpty());
    assertNull(initialTerm.getBaseTerm());

    EdmPrimitiveType type = (EdmPrimitiveType) initialTerm.getType();
    assertEquals(type.getName(), "String");
    
    assertEquals(TargetType.Term, initialTerm.getAnnotationsTargetType());
    
    //initial facets
    assertTrue(initialTerm.isNullable());
    assertNull(initialTerm.getDefaultValue());
    assertNull(initialTerm.getMaxLength());
    assertNull(initialTerm.getPrecision());
    assertNull(initialTerm.getScale());
    assertNull(initialTerm.getSrid());
  }
  
  @Test 
  public void derivedTermTest() {
    assertEquals("name2", derivedTerm.getName());
    assertEquals(new FullQualifiedName("namespace", "name2"), derivedTerm.getFullQualifiedName());
    
    assertNotNull(derivedTerm.getBaseTerm());
    assertEquals("name1", derivedTerm.getBaseTerm().getName());
    assertFalse(derivedTerm.getAnnotations().isEmpty());
    assertEquals(1, derivedTerm.getAnnotations().size());
    assertFalse(derivedTerm.getAppliesTo().isEmpty());
    assertEquals("Property", derivedTerm.getAppliesTo().get(0));

    EdmPrimitiveType type = (EdmPrimitiveType) derivedTerm.getType();
    assertEquals(type.getName(), "String");
    
    assertEquals(TargetType.Term, derivedTerm.getAnnotationsTargetType());
    
    //set facets
    assertFalse(derivedTerm.isNullable());
    assertEquals("abc", derivedTerm.getDefaultValue());
    assertEquals(new Integer(15), derivedTerm.getMaxLength());
    assertEquals(new Integer(14), derivedTerm.getPrecision());
    assertEquals(new Integer(13), derivedTerm.getScale());
    assertNull(derivedTerm.getSrid());
  }

}
