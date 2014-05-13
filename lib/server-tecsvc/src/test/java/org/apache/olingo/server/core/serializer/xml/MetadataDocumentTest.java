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
package org.apache.olingo.server.core.serializer.xml;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.Target;
import org.apache.olingo.server.api.ODataServer;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.ActionImport;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.EnumMember;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.api.edm.provider.Function;
import org.apache.olingo.server.api.edm.provider.FunctionImport;
import org.apache.olingo.server.api.edm.provider.NavigationProperty;
import org.apache.olingo.server.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.server.api.edm.provider.Parameter;
import org.apache.olingo.server.api.edm.provider.Property;
import org.apache.olingo.server.api.edm.provider.ReturnType;
import org.apache.olingo.server.api.edm.provider.Schema;
import org.apache.olingo.server.api.edm.provider.Singleton;
import org.apache.olingo.server.api.edm.provider.TypeDefinition;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class MetadataDocumentTest {

  @Test(expected = ODataRuntimeException.class)
  public void metadataOnJsonResultsInException() {
    ODataSerializer serializer = ODataServer.newInstance().getSerializer(ODataFormat.JSON);
    serializer.metadataDocument(mock(Edm.class));
  }

  @Test
  public void writeMetadataWithEmptyMockedEdm() {
    ODataSerializer serializer = ODataServer.newInstance().getSerializer(ODataFormat.XML);
    Edm edm = mock(Edm.class);
    serializer.metadataDocument(edm);
  }

  @Test
  public void writeMetadataWithLocalTestEdm() throws Exception {
    ODataSerializer serializer = ODataServer.newInstance().getSerializer(ODataFormat.XML);
    Edm edm = new EdmProviderImpl(new TestMetadataProvider());
    InputStream metadata = serializer.metadataDocument(edm);
    assertNotNull(metadata);
    
    String metadataString = IOUtils.toString(metadata);
    assertTrue(metadataString
        .contains("<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"));

    assertTrue(metadataString
        .contains("<Schema xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" " +
            "Namespace=\"namespace\" Alias=\"alias\">"));

    assertTrue(metadataString
        .contains("<EntityType Name=\"ETBaseName\"><Property Name=\"P1\" Type=\"Edm.Int16\"/><NavigationProperty " +
            "Name=\"N1\" Type=\"namespace.ETBaseName\" Nullable=\"true\" Partner=\"N1\"/></EntityType>"));

    assertTrue(metadataString
        .contains("<EntityType Name=\"ETDerivedName\" BaseType=\"namespace.ETBaseName\"><Property Name=\"P2\" " +
            "Type=\"Edm.Int16\"/><NavigationProperty Name=\"N2\" Type=\"namespace.ETDerivedName\" Nullable=\"true\" " +
            "Partner=\"N2\"/></EntityType>"));

    assertTrue(metadataString
        .contains("<ComplexType Name=\"CTBaseName\"><Property Name=\"P1\" Type=\"Edm.Int16\"/><NavigationProperty " +
            "Name=\"N1\" Type=\"namespace.ETBaseName\" Nullable=\"true\" Partner=\"N1\"/></ComplexType>"));

    assertTrue(metadataString
        .contains("<ComplexType Name=\"CTDerivedName\" BaseType=\"namespace.CTBaseName\"><Property Name=\"P2\" " +
            "Type=\"Edm.Int16\"/><NavigationProperty Name=\"N2\" Type=\"namespace.ETDerivedName\" Nullable=\"true\" " +
            "Partner=\"N2\"/></ComplexType>"));

    assertTrue(metadataString.contains("<TypeDefinition Name=\"typeDef\" Type=\"Edm.Int16\"/>"));

    assertTrue(metadataString.contains("<Action Name=\"ActionWOParameter\" IsBound=\"false\"/>"));

    assertTrue(metadataString
        .contains("<Action Name=\"ActionName\" IsBound=\"true\"><Parameter Name=\"param\" Type=\"Edm.Int16\"/>" +
            "<Parameter Name=\"param2\" Type=\"Collection(Edm.Int16)\"/><ReturnType Type=\"namespace.CTBaseName\"/>" +
            "</Action>"));

    assertTrue(metadataString
        .contains("<Function Name=\"FunctionWOParameter\" IsBound=\"false\" IsComposable=\"false\"><ReturnType " +
            "Type=\"namespace.CTBaseName\"/></Function>"));

    assertTrue(metadataString
        .contains("<Function Name=\"FunctionName\" IsBound=\"true\" IsComposable=\"false\"><Parameter Name=\"param\" " +
            "Type=\"Edm.Int16\"/><Parameter Name=\"param2\" Type=\"Collection(Edm.Int16)\"/><ReturnType " +
            "Type=\"namespace.CTBaseName\"/></Function>"));

    assertTrue(metadataString.contains("<EntityContainer Name=\"container\">"));

    assertTrue(metadataString
        .contains("<EntitySet Name=\"EntitySetName\" EntityType=\"namespace.ETBaseName\"><NavigationPropertyBinding " +
            "Path=\"N1\" Target=\"namespace.container/EntitySetName\"/></EntitySet>"));
    assertTrue(metadataString
        .contains("<Singleton Name=\"SingletonName\" EntityType=\"namespace.ETBaseName\"><NavigationPropertyBinding " +
            "Path=\"N1\" Target=\"namespace.container/EntitySetName\"/></Singleton>"));

    assertTrue(metadataString.contains("<ActionImport Name=\"actionImport\" Action=\"namespace.ActionWOParameter\"/>"));

    assertTrue(metadataString
        .contains("<FunctionImport Name=\"actionImport\" Function=\"namespace.FunctionName\" " +
            "EntitySet=\"namespace.EntitySetName\" IncludeInServiceDocument=\"false\"/>"));

    assertTrue(metadataString.contains("</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>"));
  }

  @Test
  public void writeMetadataWithTechnicalScenario() {
    ODataSerializer serializer = ODataServer.newInstance().getSerializer(ODataFormat.XML);
    EdmProviderImpl edm = new EdmProviderImpl(new EdmTechProvider());
    InputStream metadata = serializer.metadataDocument(edm);
    assertNotNull(metadata);
    // The technical scenario is too big to verify. We are content for now to make sure we can serialize it.
    // System.out.println(StringUtils.inputStreamToString(metadata, false));
  }

  private class TestMetadataProvider extends EdmProvider {

    @Override
    public List<Schema> getSchemas() throws ODataException {
      Property p1 = new Property().setName("P1").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName());
      String ns = "namespace";
      NavigationProperty n1 = new NavigationProperty().setName("N1")
          .setType(new FullQualifiedName(ns, "ETBaseName")).setNullable(true).setPartner("N1");
      Property p2 = new Property().setName("P2").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName());
      NavigationProperty n2 = new NavigationProperty().setName("N2")
          .setType(new FullQualifiedName(ns, "ETDerivedName")).setNullable(true).setPartner("N2");
      Schema schema = new Schema().setNamespace(ns).setAlias("alias");
      List<ComplexType> complexTypes = new ArrayList<ComplexType>();
      schema.setComplexTypes(complexTypes);
      ComplexType ctBase =
          new ComplexType().setName("CTBaseName").setProperties(Arrays.asList(p1)).setNavigationProperties(
              Arrays.asList(n1));
      complexTypes.add(ctBase);
      ComplexType ctDerived =
          new ComplexType().setName("CTDerivedName").setBaseType(new FullQualifiedName(ns, "CTBaseName"))
              .setProperties(Arrays.asList(p2)).setNavigationProperties(Arrays.asList(n2));
      complexTypes.add(ctDerived);

      List<EntityType> entityTypes = new ArrayList<EntityType>();
      schema.setEntityTypes(entityTypes);
      EntityType etBase =
          new EntityType().setName("ETBaseName").setProperties(Arrays.asList(p1)).setNavigationProperties(
              Arrays.asList(n1));
      entityTypes.add(etBase);
      EntityType etDerived =
          new EntityType().setName("ETDerivedName").setBaseType(new FullQualifiedName(ns, "ETBaseName"))
              .setProperties(Arrays.asList(p2)).setNavigationProperties(Arrays.asList(n2));
      entityTypes.add(etDerived);

      List<Action> actions = new ArrayList<Action>();
      schema.setActions(actions);
      // TODO:EntitySetPath
      actions.add((new Action().setName("ActionWOParameter")));
      List<Parameter> parameters = new ArrayList<Parameter>();
      parameters.add(new Parameter().setName("param").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName()));
      parameters.add(new Parameter().setName("param2").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName())
          .setCollection(true));
      actions.add(new Action().setName("ActionName").setBound(true).setParameters(parameters).setReturnType(
          new ReturnType().setType(new FullQualifiedName(ns, "CTBaseName"))));

      List<Function> functions = new ArrayList<Function>();
      schema.setFunctions(functions);
      functions.add((new Function().setName("FunctionWOParameter")
          .setReturnType(new ReturnType().setType(new FullQualifiedName(ns, "CTBaseName")))));
      functions.add(new Function().setName("FunctionName").setBound(true).setParameters(parameters).setReturnType(
          new ReturnType().setType(new FullQualifiedName(ns, "CTBaseName"))));

      List<EnumType> enumTypes = new ArrayList<EnumType>();
      schema.setEnumTypes(enumTypes);
      List<EnumMember> members = new ArrayList<EnumMember>();
      members.add(new EnumMember().setName("member").setValue("1"));
      enumTypes.add(new EnumType().setName("EnumName").setFlags(true).setMembers(members));

      List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();
      schema.setTypeDefinitions(typeDefinitions);
      typeDefinitions.add(new TypeDefinition().setName("typeDef")
          .setUnderlyingType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName()));

      EntityContainer container = new EntityContainer().setName("container");
      schema.setEntityContainer(container);

      List<ActionImport> actionImports = new ArrayList<ActionImport>();
      container.setActionImports(actionImports);
      actionImports.add(new ActionImport().setName("actionImport").setAction(
          new FullQualifiedName(ns, "ActionWOParameter")).setEntitySet(
          new Target().setEntityContainer(new FullQualifiedName(ns, "container")).setTargetName("EntitySetName")));

      List<FunctionImport> functionImports = new ArrayList<FunctionImport>();
      container.setFunctionImports(functionImports);
      functionImports.add(new FunctionImport().setName("actionImport").setFunction(
          new FullQualifiedName(ns, "FunctionName")).setEntitySet(
          new Target().setEntityContainer(new FullQualifiedName(ns, "container")).setTargetName("EntitySetName")));

      List<EntitySet> entitySets = new ArrayList<EntitySet>();
      container.setEntitySets(entitySets);
      List<NavigationPropertyBinding> nPB = new ArrayList<NavigationPropertyBinding>();
      nPB.add(new NavigationPropertyBinding().setPath("N1").setTarget(
          new Target().setEntityContainer(new FullQualifiedName(ns, "container")).setTargetName("EntitySetName")));
      entitySets.add(new EntitySet().setName("EntitySetName").setType(new FullQualifiedName(ns, "ETBaseName"))
          .setNavigationPropertyBindings(nPB));

      List<Singleton> singletons = new ArrayList<Singleton>();
      container.setSingletons(singletons);
      singletons.add(new Singleton().setName("SingletonName").setType(new FullQualifiedName(ns, "ETBaseName"))
          .setNavigationPropertyBindings(nPB));

      List<Schema> schemas = new ArrayList<Schema>();
      schemas.add(schema);
      return schemas;
    }
  }
}
