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
package org.apache.olingo.server.tecsvc.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.ActionImport;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.api.edm.provider.Function;
import org.apache.olingo.server.api.edm.provider.FunctionImport;
import org.apache.olingo.server.api.edm.provider.Schema;
import org.apache.olingo.server.api.edm.provider.Singleton;

public class SchemaProvider {

  private EdmTechProvider prov;

  public static final String nameSpace = "com.sap.odata.test1";

  public SchemaProvider(final EdmTechProvider prov) {
    this.prov = prov;
  }

  public List<Schema> getSchemas() throws ODataException {
    List<Schema> schemas = new ArrayList<Schema>();
    Schema schema = new Schema();
    schema.setNamespace("com.sap.odata.test1");
    schema.setAlias("Namespace1_Alias");
    schemas.add(schema);
    // EnumTypes
    List<EnumType> enumTypes = new ArrayList<EnumType>();
    schema.setEnumTypes(enumTypes);
    enumTypes.add(prov.getEnumType(EnumTypeProvider.nameENString));
    // EntityTypes
    List<EntityType> entityTypes = new ArrayList<EntityType>();
    schema.setEntityTypes(entityTypes);

    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETAllPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCollAllPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETMixPrimCollComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoKeyTwoPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETBase));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoBase));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETAllKey));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompAllPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompCollAllPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompCollComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETMedia));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETFourKeyAlias));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETServerSidePaging));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETAllNullable));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETKeyNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoKeyNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoBaseTwoKeyNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompMixPrimCollComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETKeyPrimNav));

    // ComplexTypes
    List<ComplexType> complexType = new ArrayList<ComplexType>();
    schema.setComplexTypes(complexType);
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTPrim));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTAllPrim));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTCollAllPrim));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTTwoPrim));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTMixPrimCollComp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTBase));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTTwoBase));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTCompComp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTCompCollComp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTPrimComp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTNavFiveProp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTPrimEnum));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTBasePrimCompNav));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTTwoBasePrimCompNav));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTCompNav));

    // TypeDefinitions

    // Actions
    List<Action> actions = new ArrayList<Action>();
    schema.setActions(actions);
    actions.addAll(prov.getActions(ActionProvider.nameBAETTwoKeyNavRTETTwoKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameBAESAllPrimRTETAllPrim));
    actions.addAll(prov.getActions(ActionProvider.nameBAESTwoKeyNavRTESTwoKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameUARTPrimParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTPrimCollParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTCompParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTCompCollParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTETParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTESParam));

    // Functions
    List<Function> functions = new ArrayList<Function>();
    schema.setFunctions(functions);

    functions.addAll(prov.getFunctions(FunctionProvider.nameUFNRTInt16));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETTwoKeyNavParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETTwoKeyNavParamCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTStringTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTESTwoKeyNavParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTString));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollStringTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollString));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCTAllPrimTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCTTwoPrimParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollCTTwoPrimParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETMedia));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFNRTESMixPrimCollCompTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETAllPrimTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTESMixPrimCollCompTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFNRTCollCTNavFiveProp));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCStringRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETBaseTwoKeyNavRTETTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESBaseTwoKeyNavRTESBaseTwoKey));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESAllPrimRTCTAllPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCollCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTString));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCollString));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETTwoKeyNavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETBaseTwoKeyNavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCSINavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETBaseTwoKeyNavRTESBaseTwoKey));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCollStringRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCTPrimCompRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCTPrimCompRTESBaseTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCollCTPrimCompRTESAllPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESKeyNavRTETKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETKeyNavRTETKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFESTwoKeyNavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETTwoKeyNavRTETTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETTwoKeyNavRTCTTwoPrim));

    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCTNavFiveProp));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCollCTNavFiveProp));

    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTStringParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESKeyNavRTETKeyNavParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCTPrimCompRTETTwoKeyNavParam));
    // functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCTPrimCompRTESTwoKeyNavParam));

    // EntityContainer
    EntityContainer container = new EntityContainer();
    schema.setEntityContainer(container);
    container.setName(ContainerProvider.nameContainer.getName());

    // EntitySets
    List<EntitySet> entitySets = new ArrayList<EntitySet>();
    container.setEntitySets(entitySets);
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESAllPrim"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESCollAllPrim"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESTwoPrim"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESMixPrimCollComp"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESBase"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESTwoBase"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESTwoKeyTwoPrim"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESBaseTwoKeyTwoPrim"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESTwoBaseTwoKeyTwoPrim"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESAllKey"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESCompAllPrim"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESCompCollAllPrim"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESCompComp"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESCompCollComp"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESMedia"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESKeyTwoKeyComp"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESInvisible"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESServerSidePaging"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESAllNullable"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESKeyNav"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESTwoKeyNav"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESBaseTwoKeyNav"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESCompMixPrimCollComp"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESFourKeyAlias"));

    // Singletons
    List<Singleton> singletons = new ArrayList<Singleton>();
    container.setSingletons(singletons);
    singletons.add(prov.getSingleton(ContainerProvider.nameContainer, "SI"));
    singletons.add(prov.getSingleton(ContainerProvider.nameContainer, "SINav"));
    singletons.add(prov.getSingleton(ContainerProvider.nameContainer, "SIMedia"));

    // ActionImports
    List<ActionImport> actionImports = new ArrayList<ActionImport>();
    container.setActionImports(actionImports);
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, "AIRTPrimParam"));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, "AIRTPrimCollParam"));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, "AIRTCompParam"));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, "AIRTCompCollParam"));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, "AIRTETParam"));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, "AIRTETCollAllPrimParam"));

    // FunctionImports
    List<FunctionImport> functionImports = new ArrayList<FunctionImport>();
    container.setFunctionImports(functionImports);
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINRTInt16"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINInvisibleRTInt16"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINInvisible2RTInt16"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTETKeyNav"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTETTwoKeyNavParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTStringTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollStringTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCTAllPrimTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTESMixPrimCollCompTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINRTESMixPrimCollCompTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollCTTwoPrim"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTETMedia"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCTTwoPrimParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCTTwoPrim"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollString"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTString"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTESTwoKeyNavParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollCTTwoPrimParam"));

    return schemas;
  }

}
