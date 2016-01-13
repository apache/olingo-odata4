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
import static org.junit.Assert.assertTrue;
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

public class EdmImplCachingTest {

  private final FullQualifiedName NAME1 = new FullQualifiedName("testNamespace1", "testName1");

  private final FullQualifiedName NAME2 = new FullQualifiedName("testNamespace2", "testName2");

  private Edm edm;

  @Test
  public void cacheSchema() {
    List<EdmSchema> schemas = edm.getSchemas();
    assertNotNull(schemas);

    assertEquals(1, schemas.size());

    List<EdmSchema> cachedSchemas = edm.getSchemas();
    assertEquals(schemas, cachedSchemas);
  }

  @Test
  public void cacheEntityContainer() {
    EdmEntityContainer entityContainer = edm.getEntityContainer(null);
    assertNotNull(entityContainer);

    EdmEntityContainer cachedContainer = edm.getEntityContainer(NAME1);
    assertNotNull(entityContainer);

    assertTrue(entityContainer == cachedContainer);
    assertEquals(entityContainer, cachedContainer);

    cachedContainer = edm.getEntityContainer(NAME1);
    assertNotNull(cachedContainer);

    assertTrue(entityContainer == cachedContainer);
    assertEquals(entityContainer, cachedContainer);

    EdmEntityContainer entityContainer2 = edm.getEntityContainer(NAME2);
    assertNotNull(entityContainer2);

    assertNotSame(entityContainer, entityContainer2);
  }

  @Test
  public void cacheEnumType() {
    EdmEnumType enumType = edm.getEnumType(NAME1);
    assertNotNull(enumType);

    EdmEnumType cachedType = edm.getEnumType(NAME1);
    assertNotNull(cachedType);

    assertTrue(enumType == cachedType);
    assertEquals(enumType, cachedType);

    EdmEnumType enumType2 = edm.getEnumType(NAME2);
    assertNotNull(enumType2);

    assertNotSame(enumType, enumType2);
  }

  @Test
  public void cacheTypeDefinition() {
    EdmTypeDefinition typeDefinition = edm.getTypeDefinition(NAME1);
    assertNotNull(typeDefinition);

    EdmTypeDefinition cachedDefinition = edm.getTypeDefinition(NAME1);
    assertNotNull(cachedDefinition);

    assertTrue(typeDefinition == cachedDefinition);
    assertEquals(typeDefinition, cachedDefinition);

    EdmTypeDefinition typeDefinition2 = edm.getTypeDefinition(NAME2);
    assertNotNull(typeDefinition2);

    assertNotSame(typeDefinition, typeDefinition2);
  }

  @Test
  public void cacheEntityType() {
    EdmEntityType entityType = edm.getEntityType(NAME1);
    assertNotNull(entityType);

    EdmEntityType cachedType = edm.getEntityType(NAME1);
    assertNotNull(cachedType);

    assertTrue(entityType == cachedType);
    assertEquals(entityType, cachedType);

    EdmEntityType entityType2 = edm.getEntityType(NAME2);
    assertNotNull(entityType2);

    assertNotSame(entityType, entityType2);
  }

  @Test
  public void cacheComplexType() {
    EdmComplexType complexType = edm.getComplexType(NAME1);
    assertNotNull(complexType);

    EdmComplexType cachedType = edm.getComplexType(NAME1);
    assertNotNull(cachedType);

    assertTrue(complexType == cachedType);
    assertEquals(complexType, cachedType);

    EdmComplexType complexType2 = edm.getComplexType(NAME2);
    assertNotNull(complexType2);

    assertNotSame(complexType, complexType2);
  }

  @Test
  public void cacheUnboundAction() {
    EdmAction action = edm.getUnboundAction(NAME1);
    assertNotNull(action);

    EdmAction cachedAction = edm.getUnboundAction(NAME1);
    assertNotNull(cachedAction);

    assertTrue(action == cachedAction);
    assertEquals(action, cachedAction);

    EdmAction action2 = edm.getUnboundAction(NAME2);
    assertNotNull(action2);
    assertNotSame(action, action2);
  }

  @Test
  public void cacheBoundAction() {
    EdmAction action = edm.getBoundAction(NAME1, NAME2, true);
    assertNotNull(action);

    EdmAction cachedAction = edm.getBoundAction(NAME1, NAME2, true);
    assertNotNull(cachedAction);

    assertTrue(action == cachedAction);
    assertEquals(action, cachedAction);

    EdmAction action2 = edm.getBoundAction(NAME2, NAME2, true);
    assertNotNull(action2);
    assertNotSame(action, action2);

  }

  @Test
  public void cacheUnboundFunctionNoParameters() {
    EdmFunction function = edm.getUnboundFunction(NAME1, null);
    assertNotNull(function);

    EdmFunction cachedfunction = edm.getUnboundFunction(NAME1, null);
    assertNotNull(cachedfunction);

    assertTrue(function == cachedfunction);
    assertEquals(function, cachedfunction);

    EdmFunction function2 = edm.getBoundFunction(NAME2, null, false, null);
    assertNotNull(function2);

    assertNotSame(function, function2);
  }

  @Test
  public void cacheBoundFunction() {
    EdmFunction function = edm.getBoundFunction(NAME1, NAME2, true, new ArrayList<String>());
    assertNotNull(function);

    EdmFunction cachedfunction = edm.getBoundFunction(NAME1, NAME2, true, new ArrayList<String>());
    assertNotNull(cachedfunction);

    assertTrue(function == cachedfunction);
    assertEquals(function, cachedfunction);

    EdmFunction function2 = edm.getBoundFunction(NAME2, NAME2, true, new ArrayList<String>());
    assertNotNull(function2);

    assertNotSame(function, function2);
  }

  @Test
  public void cacheUnboundFunctionWithParameters() {
    ArrayList<String> parameters1 = new ArrayList<String>();
    parameters1.add("A");
    parameters1.add("B");
    EdmFunction function = edm.getBoundFunction(NAME1, NAME2, true, parameters1);
    assertNotNull(function);

    ArrayList<String> parameters2 = new ArrayList<String>();
    parameters2.add("B");
    parameters2.add("A");
    EdmFunction cachedfunction = edm.getBoundFunction(NAME1, NAME2, true, parameters2);
    assertNotNull(cachedfunction);

    assertTrue(function == cachedfunction);
    assertEquals(function, cachedfunction);

    EdmFunction function2 = edm.getBoundFunction(NAME2, NAME2, true, new ArrayList<String>());
    assertNotNull(function2);

    assertNotSame(function, function2);
  }

  @Test
  public void cacheTerm() {
    EdmTerm term1 = edm.getTerm(NAME1);
    assertNotNull(term1);

    EdmTerm cachedTerm = edm.getTerm(NAME1);
    assertNotNull(cachedTerm);

    assertEquals(term1, cachedTerm);
    assertTrue(term1 == cachedTerm);

    EdmTerm term2 = edm.getTerm(NAME2);
    assertNotNull(term2);

    assertNotSame(term1, term2);
  }

  @Test
  public void cacheAnnotationsGroup() {
    EdmAnnotations annotationGroup1 = edm.getAnnotationGroup(NAME1, null);
    assertNotNull(annotationGroup1);

    EdmAnnotations cachedAnnotationGroup = edm.getAnnotationGroup(NAME1, null);
    assertNotNull(cachedAnnotationGroup);

    assertEquals(annotationGroup1, cachedAnnotationGroup);
    assertTrue(annotationGroup1 == cachedAnnotationGroup);
    
    EdmAnnotations annotationGroup2 = edm.getAnnotationGroup(NAME1, "");
    assertNotNull(annotationGroup2);
    
    assertNotSame(annotationGroup1, annotationGroup2);
  }

  @Before
  public void setup() {
    edm = new LocalEdm();
  }

  private class LocalEdm extends AbstractEdm {

    @Override
    public EdmEntityContainer createEntityContainer(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || fqn == null) {
        EdmEntityContainer container = mock(EdmEntityContainer.class);
        when(container.getNamespace()).thenReturn(NAME1.getNamespace());
        when(container.getName()).thenReturn(NAME1.getName());
        return container;
      } else if (NAME2.equals(fqn)) {
        EdmEntityContainer container = mock(EdmEntityContainer.class);
        when(container.getNamespace()).thenReturn(fqn.getNamespace());
        when(container.getName()).thenReturn(fqn.getName());
        return container;
      }
      return null;
    }

    @Override
    public EdmEnumType createEnumType(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || NAME2.equals(fqn)) {
        EdmEnumType enumType = mock(EdmEnumType.class);
        when(enumType.getNamespace()).thenReturn(fqn.getNamespace());
        when(enumType.getName()).thenReturn(fqn.getName());
        return enumType;
      }
      return null;
    }

    @Override
    public EdmTypeDefinition createTypeDefinition(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || NAME2.equals(fqn)) {
        EdmTypeDefinition typeDefinition = mock(EdmTypeDefinition.class);
        when(typeDefinition.getNamespace()).thenReturn(fqn.getNamespace());
        when(typeDefinition.getName()).thenReturn(fqn.getName());
        return typeDefinition;
      }
      return null;
    }

    @Override
    public EdmEntityType createEntityType(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || NAME2.equals(fqn)) {
        EdmEntityType entityType = mock(EdmEntityType.class);
        when(entityType.getNamespace()).thenReturn(fqn.getNamespace());
        when(entityType.getName()).thenReturn(fqn.getName());
        return entityType;
      }
      return null;
    }

    @Override
    public EdmComplexType createComplexType(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || NAME2.equals(fqn)) {
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
      if (NAME1.equals(fqn)) {
        EdmAction action = mock(EdmAction.class);
        when(action.getNamespace()).thenReturn(fqn.getNamespace());
        when(action.getName()).thenReturn(fqn.getName());
        return action;
      } else if (NAME2.equals(fqn)) {
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
      if (NAME1.equals(fqn)) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return function;
      } else if (NAME2.equals(fqn)) {
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
      if (NAME1.equals(fqn)) {
        EdmAction action = mock(EdmAction.class);
        when(action.getNamespace()).thenReturn(fqn.getNamespace());
        when(action.getName()).thenReturn(fqn.getName());
        return action;
      } else if (NAME2.equals(fqn)) {
        EdmAction action = mock(EdmAction.class);
        when(action.getNamespace()).thenReturn(fqn.getNamespace());
        when(action.getName()).thenReturn(fqn.getName());
        return action;
      }
      return null;
    }

    @Override
    protected List<EdmFunction> createUnboundFunctions(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn)) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return Collections.singletonList(function);
      } else if (NAME2.equals(fqn)) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return Collections.singletonList(function);
      }
      return Collections.emptyList();
    }

    @Override
    protected EdmFunction createUnboundFunction(final FullQualifiedName fqn, final List<String> parameterNames) {
      if (NAME1.equals(fqn)) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return function;
      } else if (NAME2.equals(fqn)) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return function;
      }
      return null;
    }

    @Override
    protected Map<String, EdmSchema> createSchemas() {
      final EdmSchema schema = mock(EdmSchema.class);
      when(schema.getNamespace()).thenReturn(NAME1.getNamespace());
      return new HashMap<String, EdmSchema>() {
        private static final long serialVersionUID = 3109256773218160485L;

        {
          put("", schema);
        }
      };
    }

    @Override
    protected EdmTerm createTerm(final FullQualifiedName termName) {
      if(NAME1.equals(termName) || NAME2.equals(termName)){
        EdmTerm term = mock(EdmTerm.class);
        when(term.getFullQualifiedName()).thenReturn(termName);
        return term;
      }
      return null;
    }

    @Override
    protected EdmAnnotations createAnnotationGroup(final FullQualifiedName target, String qualifier) {
      if(NAME1.equals(target) && qualifier == null){
        EdmAnnotations annotationGroup = mock(EdmAnnotations.class);
        when(annotationGroup.getQualifier()).thenReturn(null);
        return annotationGroup;
      }else if(NAME1.equals(target) && "".equals(qualifier)){
        EdmAnnotations annotationGroup = mock(EdmAnnotations.class);
        when(annotationGroup.getQualifier()).thenReturn("");
        return annotationGroup;
      }
      return null;
    }
  }
}
