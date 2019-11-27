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
package org.apache.olingo.commons.core.edm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.junit.Before;
import org.junit.Test;

public class EdmImplCallCreateTest {

  private final FullQualifiedName FQN = new FullQualifiedName("testNamespace", "testName");

  private final FullQualifiedName WRONG_FQN = new FullQualifiedName("wrong", "wrong");

  private Edm edm;

  @Test
  public void callCreateSchemas() {
    List<EdmSchema> schemas = edm.getSchemas();
    assertNotNull(schemas);
  }

  @Test
  public void callCreateEntityContainer() {
    EdmEntityContainer entityContainer = edm.getEntityContainer(FQN);
    assertNotNull(entityContainer);
    assertEquals(FQN.getNamespace(), entityContainer.getNamespace());
    assertEquals(FQN.getName(), entityContainer.getName());

    entityContainer = edm.getEntityContainer(null);
    assertNotNull(entityContainer);
    assertEquals(FQN.getNamespace(), entityContainer.getNamespace());
    assertEquals(FQN.getName(), entityContainer.getName());

    assertNull(edm.getEntityContainer(WRONG_FQN));
  }

  @Test
  public void callCreateEnumType() {
    EdmEnumType enumType = edm.getEnumType(FQN);
    assertNotNull(enumType);
    assertEquals(FQN.getNamespace(), enumType.getNamespace());
    assertEquals(FQN.getName(), enumType.getName());

    assertNull(edm.getEnumType(WRONG_FQN));
  }

  @Test
  public void callCreateTypeDefinition() {
    EdmTypeDefinition typeDefinition = edm.getTypeDefinition(FQN);
    assertNotNull(typeDefinition);
    assertEquals(FQN.getNamespace(), typeDefinition.getNamespace());
    assertEquals(FQN.getName(), typeDefinition.getName());

    assertNull(edm.getTypeDefinition(WRONG_FQN));
  }

  @Test
  public void callCreateEntityType() {
    EdmEntityType entityType = edm.getEntityType(FQN);
    assertNotNull(entityType);
    assertEquals(FQN.getNamespace(), entityType.getNamespace());
    assertEquals(FQN.getName(), entityType.getName());

    assertNull(edm.getEntityType(WRONG_FQN));
  }

  @Test
  public void callCreateComplexType() {
    EdmComplexType complexType = edm.getComplexType(FQN);
    assertNotNull(complexType);
    assertEquals(FQN.getNamespace(), complexType.getNamespace());
    assertEquals(FQN.getName(), complexType.getName());

    assertNull(edm.getComplexType(WRONG_FQN));
  }

  @Test
  public void callCreateAction() {
    EdmAction action = edm.getUnboundAction(FQN);
    assertNotNull(action);
    assertEquals(FQN.getNamespace(), action.getNamespace());
    assertEquals(FQN.getName(), action.getName());

    EdmAction action2 = edm.getBoundAction(FQN, FQN, true);
    assertNotNull(action2);
    assertEquals(FQN.getNamespace(), action2.getNamespace());
    assertEquals(FQN.getName(), action2.getName());

    assertNotSame(action, action2);

    assertNull(edm.getUnboundAction(WRONG_FQN));
  }

  @Test
  public void callCreateFunction() {
    EdmFunction function = edm.getUnboundFunction(FQN, null);
    assertNotNull(function);
    assertEquals(FQN.getNamespace(), function.getNamespace());
    assertEquals(FQN.getName(), function.getName());

    EdmFunction function2 = edm.getBoundFunction(FQN, FQN, true, new ArrayList<String>());
    assertNotNull(function2);
    assertEquals(FQN.getNamespace(), function2.getNamespace());
    assertEquals(FQN.getName(), function2.getName());

    assertNotSame(function, function2);

    assertNull(edm.getUnboundFunction(WRONG_FQN, null));
  }

  @Test
  public void callCreateTerm() {
    EdmTerm term = edm.getTerm(FQN);
    assertNotNull(term);

    assertEquals(FQN, term.getFullQualifiedName());

    assertNull(edm.getTerm(WRONG_FQN));
  }

  @Test
  public void callCreateAnnotationGroup() {
    EdmAnnotations annotationGroup = edm.getAnnotationGroup(FQN, null);
    assertNotNull(annotationGroup);

    assertEquals(null, annotationGroup.getQualifier());

    assertNull(edm.getAnnotationGroup(WRONG_FQN, null));
  }

  @Before
  public void setup() {
    edm = new LocalEdm();
  }

  private class LocalEdm extends AbstractEdm {

    @Override
    public EdmEntityContainer createEntityContainer(final FullQualifiedName fqn) {
      if (fqn == null || FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmEntityContainer container = mock(EdmEntityContainer.class);
        when(container.getNamespace()).thenReturn(FQN.getNamespace());
        when(container.getName()).thenReturn(FQN.getName());
        return container;
      }
      return null;
    }

    @Override
    public EdmEnumType createEnumType(final FullQualifiedName fqn) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmEnumType enumType = mock(EdmEnumType.class);
        when(enumType.getNamespace()).thenReturn(fqn.getNamespace());
        when(enumType.getName()).thenReturn(fqn.getName());
        return enumType;
      }
      return null;
    }

    @Override
    public EdmTypeDefinition createTypeDefinition(final FullQualifiedName fqn) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmTypeDefinition typeDefinition = mock(EdmTypeDefinition.class);
        when(typeDefinition.getNamespace()).thenReturn(fqn.getNamespace());
        when(typeDefinition.getName()).thenReturn(fqn.getName());
        return typeDefinition;
      }
      return null;
    }

    @Override
    public EdmEntityType createEntityType(final FullQualifiedName fqn) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmEntityType entityType = mock(EdmEntityType.class);
        when(entityType.getNamespace()).thenReturn(fqn.getNamespace());
        when(entityType.getName()).thenReturn(fqn.getName());
        return entityType;
      }
      return null;
    }

    @Override
    public EdmComplexType createComplexType(final FullQualifiedName fqn) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmComplexType complexType = mock(EdmComplexType.class);
        when(complexType.getNamespace()).thenReturn(fqn.getNamespace());
        when(complexType.getName()).thenReturn(fqn.getName());
        return complexType;
      }
      return null;
    }

    @Override
    public EdmAction createBoundAction(final FullQualifiedName fqn, final FullQualifiedName bindingParameterTypeName,
        final Boolean isBindingParameterCollection) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmAction action = mock(EdmAction.class);
        when(action.getNamespace()).thenReturn(fqn.getNamespace());
        when(action.getName()).thenReturn(fqn.getName());
        return action;
      }
      return null;
    }

    @Override
    public EdmFunction createBoundFunction(final FullQualifiedName fqn,
        final FullQualifiedName bindingParameterTypeName,
        final Boolean isBindingParameterCollection, final List<String> bindingParameterNames) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return function;
      }
      return null;
    }

    @Override
    protected Map<String, String> createAliasToNamespaceInfo() {
      return new HashMap<String, String>();
    }

    @Override
    protected EdmAction createUnboundAction(final FullQualifiedName fqn) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmAction action = mock(EdmAction.class);
        when(action.getNamespace()).thenReturn(fqn.getNamespace());
        when(action.getName()).thenReturn(fqn.getName());
        return action;
      }
      return null;
    }

    @Override
    protected List<EdmFunction> createUnboundFunctions(final FullQualifiedName fqn) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return Collections.singletonList(function);
      }
      return Collections.emptyList();
    }

    @Override
    protected EdmFunction createUnboundFunction(final FullQualifiedName fqn, final List<String> parameterNames) {
      if (FQN.getNamespace().equals(fqn.getNamespace()) && FQN.getName().equals(fqn.getName())) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return function;
      }
      return null;
    }

    @Override
    protected Map<String, EdmSchema> createSchemas() {
      return Collections.emptyMap();
    }

    @Override
    protected EdmTerm createTerm(final FullQualifiedName termName) {
      if (FQN.equals(termName)) {
        EdmTerm term = mock(EdmTerm.class);
        when(term.getFullQualifiedName()).thenReturn(FQN);
        return term;
      }
      return null;
    }

    @Override
    protected EdmAnnotations createAnnotationGroup(final FullQualifiedName targetName, String qualifier) {
      if (FQN.equals(targetName) && qualifier == null) {
        EdmAnnotations annotationGroup = mock(EdmAnnotations.class);
        when(annotationGroup.getQualifier()).thenReturn(qualifier);
        return annotationGroup;
      }
      return null;
    }
  }
  @Test
  public void callCreateComplexTypeWithAnnotations() {
    EdmComplexType complexType = edm.getComplexTypeWithAnnotations(FQN);
    assertNotNull(complexType);
    assertEquals(FQN.getNamespace(), complexType.getNamespace());
    assertEquals(FQN.getName(), complexType.getName());

    assertNull(edm.getComplexType(WRONG_FQN));
  }
  
  @Test
  public void callCreateComplexTypeWithAnnotationsDerivedFromES() {
    EdmComplexType complexType = ((AbstractEdm)edm).
        getComplexTypeWithAnnotations(FQN, true);
    assertNotNull(complexType);
    assertEquals(FQN.getNamespace(), complexType.getNamespace());
    assertEquals(FQN.getName(), complexType.getName());

    assertNull(edm.getComplexType(WRONG_FQN));
  }
  
  @Test
  public void callCreateEntityTypeWithAnnotation() {
    EdmEntityType entityType = edm.getEntityTypeWithAnnotations(FQN);
    assertNotNull(entityType);
    assertEquals(FQN.getNamespace(), entityType.getNamespace());
    assertEquals(FQN.getName(), entityType.getName());

    assertNull(edm.getEntityType(WRONG_FQN));
  }
  
  @Test
  public void callCreateEntityTypeWithAnnotationDerivedFromES() {
    EdmEntityType entityType = ((AbstractEdm)edm).
        getEntityTypeWithAnnotations(FQN, true);
    assertNotNull(entityType);
    assertEquals(FQN.getNamespace(), entityType.getNamespace());
    assertEquals(FQN.getName(), entityType.getName());

    assertNull(edm.getEntityType(WRONG_FQN));
  }
}
