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
import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.ActionImport;
import org.apache.olingo.server.api.edm.provider.AliasInfo;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.api.edm.provider.Function;
import org.apache.olingo.server.api.edm.provider.FunctionImport;
import org.apache.olingo.server.api.edm.provider.Schema;
import org.apache.olingo.server.api.edm.provider.Singleton;
import org.apache.olingo.server.api.edm.provider.Term;
import org.apache.olingo.server.api.edm.provider.TypeDefinition;
import org.junit.Before;
import org.junit.Test;

public class EdmSchemaImplTest {

  private EdmSchema schema;
  private Edm edm;

  @Before
  public void before() {
    EdmProvider provider = new LocalProvider();
    edm = new EdmProviderImpl(provider);
    schema = edm.getSchemas().get(0);

  }
  
  @Test
  public void initialSchemaTest() {
    EdmProvider provider = mock(EdmProvider.class);
    edm = new EdmProviderImpl(provider);
    edm.getSchemas();
  }

  @Test
  public void emptySchemaTest() throws Exception {
    ArrayList<Schema> schemas = new ArrayList<Schema>();
    Schema providerSchema = new Schema();
    schemas.add(providerSchema );
    EdmProvider provider = mock(EdmProvider.class);
    when(provider.getSchemas()).thenReturn(schemas);
    edm = new EdmProviderImpl(provider);
    edm.getSchemas();
  }
  
  @Test
  public void basicGetters() {
    assertEquals("namespace", schema.getNamespace());
    assertEquals("alias", schema.getAlias());
  }
  
  @Test
  public void getTypeDefinitions(){
    List<EdmTypeDefinition> typeDefinitions = schema.getTypeDefinitions();
    assertNotNull(typeDefinitions);
    assertEquals(2, typeDefinitions.size());
    
    for(EdmTypeDefinition def : typeDefinitions){
      assertTrue(def == edm.getTypeDefinition(new FullQualifiedName("namespace", def.getName())));
    }
  }

  @Test
  public void getEnumTypes() {
    List<EdmEnumType> enumTypes = schema.getEnumTypes();
    assertNotNull(enumTypes);
    assertEquals(2, enumTypes.size());

    for (EdmEnumType enumType : enumTypes) {
      assertTrue(enumType == edm.getEnumType(new FullQualifiedName("namespace", enumType.getName())));
    }
  }

  @Test
  public void getEntityTypes() {
    List<EdmEntityType> entityTypes = schema.getEntityTypes();
    assertNotNull(entityTypes);
    assertEquals(2, entityTypes.size());

    for (EdmEntityType entityType : entityTypes) {
      assertTrue(entityType == edm.getEntityType(new FullQualifiedName("namespace", entityType.getName())));
    }
  }

  @Test
  public void getComplexTypes() {
    List<EdmComplexType> complexTypes = schema.getComplexTypes();
    assertNotNull(complexTypes);
    assertEquals(2, complexTypes.size());

    for (EdmComplexType complexType : complexTypes) {
      assertTrue(complexType == edm.getComplexType(new FullQualifiedName("namespace", complexType.getName())));
    }
  }

  @Test
  public void getActions() {
    List<EdmAction> actions = schema.getActions();
    assertNotNull(actions);
    assertEquals(2, actions.size());

    for (EdmAction action : actions) {
      assertTrue(action == edm.getAction(new FullQualifiedName("namespace", action.getName()), null, null));
    }
  }

  @Test
  public void getFunctions() {
    List<EdmFunction> functions = schema.getFunctions();
    assertNotNull(functions);
    assertEquals(2, functions.size());

    for (EdmFunction function : functions) {
      FullQualifiedName functionName = new FullQualifiedName("namespace", function.getName());
      assertTrue(function == edm.getFunction(functionName, null, null, null));
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
      assertNotNull(obj.getAction());
    }

    List<EdmFunctionImport> functionImports = container.getFunctionImports();
    assertNotNull(functionImports);
    assertEquals(2, functionImports.size());
    for (EdmFunctionImport obj : functionImports) {
      assertNotNull(obj.getFunctionFqn());
    }
    
    assertTrue(container == edm.getEntityContainer(new FullQualifiedName(schema.getNamespace(), container.getName())));
    assertTrue(container == edm.getEntityContainer(null));
  }

  private class LocalProvider extends EdmProvider {

    public EnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public TypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public ComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public List<Action> getActions(final FullQualifiedName actionName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public List<Function> getFunctions(final FullQualifiedName functionName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public Term getTerm(final FullQualifiedName termName) throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public Singleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public ActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public FunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public EntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName)
        throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public List<AliasInfo> getAliasInfos() throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }

    public List<Schema> getSchemas() throws ODataException {
      Schema providerSchema = new Schema();
      providerSchema.setNamespace("namespace");
      providerSchema.setAlias("alias");
      EntityContainer container = new EntityContainer().setName("container");

      List<EntitySet> entitySets = new ArrayList<EntitySet>();
      entitySets.add(new EntitySet().setName("entitySetName")
          .setType(new FullQualifiedName("namespace", "entityType1")));
      entitySets
          .add(new EntitySet().setName("entitySetName2").setType(new FullQualifiedName("namespace", "entityType2")));
      container.setEntitySets(entitySets);

      List<Singleton> singletons = new ArrayList<Singleton>();
      singletons.add(new Singleton().setName("singletonName")
          .setType(new FullQualifiedName("namespace", "entityType1")));
      singletons
          .add(new Singleton().setName("singletonName2").setType(new FullQualifiedName("namespace", "entityType2")));
      container.setSingletons(singletons);

      List<ActionImport> actionImports = new ArrayList<ActionImport>();
      actionImports.add(new ActionImport().setName("actionImportName").setAction(
          new FullQualifiedName("namespace", "action1")));
      actionImports.add(new ActionImport().setName("actionImportName2").setAction(
          new FullQualifiedName("namespace", "action2")));
      container.setActionImports(actionImports);

      List<FunctionImport> functionImports = new ArrayList<FunctionImport>();
      functionImports.add(new FunctionImport().setName("functionImportName").setFunction(
          new FullQualifiedName("namespace", "function1")));
      functionImports.add(new FunctionImport().setName("functionImportName2").setFunction(
          new FullQualifiedName("namespace", "function2")));
      container.setFunctionImports(functionImports);
      providerSchema.setEntityContainer(container);

      List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();
      typeDefinitions.add(new TypeDefinition().setName("typeDefinition1"));
      typeDefinitions.add(new TypeDefinition().setName("typeDefinition2"));
      providerSchema.setTypeDefinitions(typeDefinitions);
      
      List<EnumType> enumTypes = new ArrayList<EnumType>();
      enumTypes.add(new EnumType().setName("enumType1"));
      enumTypes.add(new EnumType().setName("enumType2"));
      providerSchema.setEnumTypes(enumTypes);

      List<EntityType> entityTypes = new ArrayList<EntityType>();
      entityTypes.add(new EntityType().setName("entityType1"));
      entityTypes.add(new EntityType().setName("entityType2"));
      providerSchema.setEntityTypes(entityTypes);

      List<ComplexType> complexTypes = new ArrayList<ComplexType>();
      complexTypes.add(new ComplexType().setName("complexType1"));
      complexTypes.add(new ComplexType().setName("complexType2"));
      providerSchema.setComplexTypes(complexTypes);

      List<Action> actions = new ArrayList<Action>();
      actions.add(new Action().setName("action1"));
      actions.add(new Action().setName("action2"));
      providerSchema.setActions(actions);

      List<Function> functions = new ArrayList<Function>();
      functions.add(new Function().setName("function1"));
      functions.add(new Function().setName("function2"));
      providerSchema.setFunctions(functions);
      ArrayList<Schema> schemas = new ArrayList<Schema>();
      schemas.add(providerSchema);
      return schemas;
    }

    public EntityContainer getEntityContainer() throws ODataException {
      throw new RuntimeException("Provider must not be called in the schema case");
    }
  }
}
