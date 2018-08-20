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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Before;
import org.junit.Test;

public class EdmSchemaImplTest {

  private EdmSchema schema;
  private Edm edm;
  public static final String NAMESPACE = "org.namespace";
  public static final String ALIAS = "alias";

  @Before
  public void before() {
    CsdlEdmProvider provider = new LocalProvider();
    edm = new EdmProviderImpl(provider);
    schema = edm.getSchemas().get(0);

  }

  @Test
  public void initialSchemaTest() {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    edm = new EdmProviderImpl(provider);
    edm.getSchemas();
  }

  @Test
  public void emptySchemaTest() throws Exception {
    ArrayList<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
    CsdlSchema providerSchema = new CsdlSchema();
    schemas.add(providerSchema);
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    when(provider.getSchemas()).thenReturn(schemas);
    edm = new EdmProviderImpl(provider);
    edm.getSchemas();
  }

  @Test
  public void basicGetters() {
    assertEquals("org.namespace", schema.getNamespace());
    assertEquals("alias", schema.getAlias());
  }

  @Test
  public void getTypeDefinitions() {
    List<EdmTypeDefinition> typeDefinitions = schema.getTypeDefinitions();
    assertNotNull(typeDefinitions);
    assertEquals(2, typeDefinitions.size());

    for (EdmTypeDefinition def : typeDefinitions) {
      assertTrue(def == edm.getTypeDefinition(new FullQualifiedName(NAMESPACE, def.getName())));
      assertTrue(def == edm.getTypeDefinition(new FullQualifiedName(ALIAS, def.getName())));
    }
  }

  @Test
  public void getEnumTypes() {
    List<EdmEnumType> enumTypes = schema.getEnumTypes();
    assertNotNull(enumTypes);
    assertEquals(2, enumTypes.size());

    for (EdmEnumType enumType : enumTypes) {
      assertTrue(enumType == edm.getEnumType(new FullQualifiedName(NAMESPACE, enumType.getName())));
      assertTrue(enumType == edm.getEnumType(new FullQualifiedName(ALIAS, enumType.getName())));
    }
  }

  @Test
  public void getEntityTypes() {
    List<EdmEntityType> entityTypes = schema.getEntityTypes();
    assertNotNull(entityTypes);
    assertEquals(2, entityTypes.size());

    for (EdmEntityType entityType : entityTypes) {
      assertTrue(entityType == edm.getEntityType(new FullQualifiedName(NAMESPACE, entityType.getName())));
      assertTrue(entityType == edm.getEntityType(new FullQualifiedName(ALIAS, entityType.getName())));
    }
  }

  @Test
  public void getComplexTypes() {
    List<EdmComplexType> complexTypes = schema.getComplexTypes();
    assertNotNull(complexTypes);
    assertEquals(2, complexTypes.size());

    for (EdmComplexType complexType : complexTypes) {
      assertTrue(complexType == edm.getComplexType(new FullQualifiedName(NAMESPACE, complexType.getName())));
      assertTrue(complexType == edm.getComplexType(new FullQualifiedName(ALIAS, complexType.getName())));
    }
  }

  @Test
  public void getActions() {
    List<EdmAction> actions = schema.getActions();
    assertNotNull(actions);
    assertEquals(2, actions.size());

    for (EdmAction action : actions) {
      assertTrue(action == edm.getUnboundAction(new FullQualifiedName(NAMESPACE, action.getName())));
      assertTrue(action == edm.getUnboundAction(new FullQualifiedName(ALIAS, action.getName())));
    }
  }

  @Test
  public void getFunctions() {
    List<EdmFunction> functions = schema.getFunctions();
    assertNotNull(functions);
    assertEquals(2, functions.size());

    for (EdmFunction function : functions) {
      FullQualifiedName functionName = new FullQualifiedName(NAMESPACE, function.getName());
      assertTrue(function == edm.getUnboundFunction(functionName, null));

      functionName = new FullQualifiedName(ALIAS, function.getName());
      assertTrue(function == edm.getUnboundFunction(functionName, null));
    }
  }

  @Test
  public void getAnnotationGroups() {
    List<EdmAnnotations> annotationGroups = schema.getAnnotationGroups();
    assertNotNull(annotationGroups);
    assertEquals(2, annotationGroups.size());

    for (EdmAnnotations annotationGroup : annotationGroups) {
      FullQualifiedName targetName = new FullQualifiedName(annotationGroup.getTargetPath());
      assertTrue(annotationGroup == edm.getAnnotationGroup(targetName, null));
      targetName = new FullQualifiedName(annotationGroup.getTargetPath().replace(NAMESPACE, ALIAS));
      assertTrue(annotationGroup == edm.getAnnotationGroup(targetName, null));
    }
  }

  @Test
  public void getContainer() {
    EdmEntityContainer container = schema.getEntityContainer();
    assertNotNull(container);

    List<EdmEntitySet> entitySets = container.getEntitySets();
    assertNotNull(entitySets);
    assertEquals(2, entitySets.size());
    for (EdmEntitySet obj : entitySets) {
      assertNotNull(obj.getEntityType());
    }

    List<EdmSingleton> singletons = container.getSingletons();
    assertNotNull(singletons);
    assertEquals(2, singletons.size());
    for (EdmSingleton obj : singletons) {
      assertNotNull(obj.getEntityType());
    }

    List<EdmActionImport> actionImports = container.getActionImports();
    assertNotNull(actionImports);
    assertEquals(2, actionImports.size());
    for (EdmActionImport obj : actionImports) {
      assertNotNull(obj.getUnboundAction());
    }

    List<EdmFunctionImport> functionImports = container.getFunctionImports();
    assertNotNull(functionImports);
    assertEquals(2, functionImports.size());
    for (EdmFunctionImport obj : functionImports) {
      assertNotNull(obj.getFunctionFqn());
    }

    assertTrue(container == edm.getEntityContainer(new FullQualifiedName(schema.getNamespace(), container.getName())));
    assertTrue(container == edm.getEntityContainer(null));
    assertTrue(container == edm.getEntityContainer());
  }

  private class LocalProvider implements CsdlEdmProvider {

    @Override
    public CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlTypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
      return null;
    }

    @Override
    public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlTerm getTerm(final FullQualifiedName termName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer,
        final String functionImportName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public List<CsdlAliasInfo> getAliasInfos() throws ODataException {
      return Collections.emptyList();
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
      CsdlSchema providerSchema = new CsdlSchema();
      providerSchema.setNamespace(NAMESPACE);
      providerSchema.setAlias(ALIAS);
      CsdlEntityContainer container = new CsdlEntityContainer().setName("container");

      List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
      entitySets.add(new CsdlEntitySet().setName("entitySetName")
          .setType(new FullQualifiedName(NAMESPACE, "entityType1")));
      entitySets
          .add(new CsdlEntitySet().setName("entitySetName2").setType(new FullQualifiedName(NAMESPACE, "entityType2")));
      container.setEntitySets(entitySets);

      List<CsdlSingleton> singletons = new ArrayList<CsdlSingleton>();
      singletons.add(new CsdlSingleton().setName("singletonName")
          .setType(new FullQualifiedName(NAMESPACE, "entityType1")));
      singletons
          .add(new CsdlSingleton().setName("singletonName2").setType(new FullQualifiedName(NAMESPACE, "entityType2")));
      container.setSingletons(singletons);

      List<CsdlActionImport> actionImports = new ArrayList<CsdlActionImport>();
      actionImports.add(new CsdlActionImport().setName("actionImportName").setAction(
          new FullQualifiedName(NAMESPACE, "action1")));
      actionImports.add(new CsdlActionImport().setName("actionImportName2").setAction(
          new FullQualifiedName(NAMESPACE, "action2")));
      container.setActionImports(actionImports);

      List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();
      functionImports.add(new CsdlFunctionImport().setName("functionImportName").setFunction(
          new FullQualifiedName(NAMESPACE, "function1")));
      functionImports.add(new CsdlFunctionImport().setName("functionImportName2").setFunction(
          new FullQualifiedName(NAMESPACE, "function2")));
      container.setFunctionImports(functionImports);
      providerSchema.setEntityContainer(container);

      List<CsdlTypeDefinition> typeDefinitions = new ArrayList<CsdlTypeDefinition>();
      typeDefinitions.add(new CsdlTypeDefinition().setName("typeDefinition1").setUnderlyingType(
          EdmPrimitiveTypeKind.String.getFullQualifiedName()));
      typeDefinitions.add(new CsdlTypeDefinition().setName("typeDefinition2").setUnderlyingType(
          EdmPrimitiveTypeKind.String.getFullQualifiedName()));
      providerSchema.setTypeDefinitions(typeDefinitions);

      List<CsdlEnumType> enumTypes = new ArrayList<CsdlEnumType>();
      enumTypes.add(new CsdlEnumType().setName("enumType1"));
      enumTypes.add(new CsdlEnumType().setName("enumType2"));
      providerSchema.setEnumTypes(enumTypes);

      List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
      entityTypes.add(new CsdlEntityType().setName("entityType1"));
      entityTypes.add(new CsdlEntityType().setName("entityType2")
          .setBaseType(new FullQualifiedName(NAMESPACE, "entityType1")));
      providerSchema.setEntityTypes(entityTypes);

      List<CsdlComplexType> complexTypes = new ArrayList<CsdlComplexType>();
      complexTypes.add(new CsdlComplexType().setName("complexType1"));
      complexTypes.add(new CsdlComplexType().setName("complexType2").setBaseType(
          new FullQualifiedName(NAMESPACE, "complexType1")));
      providerSchema.setComplexTypes(complexTypes);

      List<CsdlAction> actions = new ArrayList<CsdlAction>();
      actions.add(new CsdlAction().setName("action1"));
      actions.add(new CsdlAction().setName("action2"));
      providerSchema.setActions(actions);

      List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
      functions.add(new CsdlFunction().setName("function1"));
      functions.add(new CsdlFunction().setName("function2"));
      providerSchema.setFunctions(functions);

      List<CsdlAnnotations> annotationGroups = new ArrayList<CsdlAnnotations>();
      annotationGroups.add(new CsdlAnnotations().setTarget(NAMESPACE + ".entityType1"));
      annotationGroups.add(new CsdlAnnotations().setTarget(NAMESPACE + ".entityType2"));
      providerSchema.setAnnotationsGroup(annotationGroups);

      List<CsdlTerm> terms = new ArrayList<CsdlTerm>();
      terms.add(new CsdlTerm().setName("term1").setType("Edm.String"));
      terms.add(new CsdlTerm().setName("term2").setType("Edm.String"));
      providerSchema.setTerms(terms);

      ArrayList<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
      schemas.add(providerSchema);
      return schemas;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    @Override
    public CsdlAnnotations getAnnotationsGroup(FullQualifiedName targetName, String qualifier) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }
  }
}
