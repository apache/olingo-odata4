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
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.ex.ODataException;

public class ContainerProvider {

  public static final FullQualifiedName nameContainer = new FullQualifiedName(SchemaProvider.NAMESPACE, "Container");
  public static final String AIRT_STRING = "AIRTString";
  public static final String AIRT_COLL_STRING_TWO_PARAM = "AIRTCollStringTwoParam";
  public static final String AIRTCT_TWO_PRIM_PARAM = "AIRTCTTwoPrimParam";
  public static final String AIRT_COLL_CT_TWO_PRIM_PARAM = "AIRTCollCTTwoPrimParam";
  public static final String AIRTET_TWO_KEY_TWO_PRIM_PARAM = "AIRTETTwoKeyTwoPrimParam";
  public static final String AIRT_COLL_ET_KEY_NAV_PARAM = "AIRTCollETKeyNavParam";
  public static final String AIRTES_ALL_PRIM_PARAM = "AIRTESAllPrimParam";
  public static final String AIRT_COLL_ES_ALL_PRIM_PARAM = "AIRTCollESAllPrimParam";
  public static final String AIRT = "AIRT";
  public static final String AIRT_PARAM = "AIRTParam";
  public static final String AIRT_TWO_PARAM = "AIRTTwoParam";
  public static final String AIRT_BYTE_NINE_PARAM = "AIRTByteNineParam";

  private final CsdlEdmProvider prov;

  public ContainerProvider(final EdmTechProvider edmTechProvider) {
    prov = edmTechProvider;
  }

  public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName)
      throws ODataException {
    if (entityContainerName == null || entityContainerName.equals(nameContainer)) {
      return new CsdlEntityContainerInfo().setContainerName(nameContainer);
    }
    return null;
  }

  public CsdlEntityContainer getEntityContainer() throws ODataException {
    CsdlEntityContainer container = new CsdlEntityContainer();
    container.setName(ContainerProvider.nameContainer.getName());

    // EntitySets
    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
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
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESMixEnumDefCollComp"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESTwoBaseTwoKeyNav"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESKeyNavCont"));
    entitySets.add(prov.getEntitySet(ContainerProvider.nameContainer, "ESTwoKeyNavCont"));

    // Singletons
    List<CsdlSingleton> singletons = new ArrayList<CsdlSingleton>();
    container.setSingletons(singletons);
    singletons.add(prov.getSingleton(ContainerProvider.nameContainer, "SI"));
    singletons.add(prov.getSingleton(ContainerProvider.nameContainer, "SINav"));
    singletons.add(prov.getSingleton(ContainerProvider.nameContainer, "SIMedia"));

    // ActionImports
    List<CsdlActionImport> actionImports = new ArrayList<CsdlActionImport>();
    container.setActionImports(actionImports);
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT_STRING));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT_COLL_STRING_TWO_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRTCT_TWO_PRIM_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT_COLL_CT_TWO_PRIM_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRTET_TWO_KEY_TWO_PRIM_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT_COLL_ET_KEY_NAV_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRTES_ALL_PRIM_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT_COLL_ES_ALL_PRIM_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT_TWO_PARAM));
    actionImports.add(prov.getActionImport(ContainerProvider.nameContainer, AIRT_BYTE_NINE_PARAM));

    // FunctionImports
    List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();
    container.setFunctionImports(functionImports);
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINRTInt16"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINInvisibleRTInt16"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINInvisible2RTInt16"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTETKeyNav"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTESTwoKeyNav"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTETTwoKeyNavParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTStringTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollStringTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCTAllPrimTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTESMixPrimCollCompTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollETMixPrimCollCompTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINRTCollETMixPrimCollCompTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollCTTwoPrim"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTESMedia"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollESMedia"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCTTwoPrimTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCTTwoPrim"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollString"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTString"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollESTwoKeyNavParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollCTTwoPrimTwoParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINRTCollCTNavFiveProp"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FICRTCollESKeyNavContParam"));
    functionImports.add(prov.getFunctionImport(ContainerProvider.nameContainer, "FINRTByteNineParam"));

    return container;
  }

  public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String name) throws ODataException {
    if (entityContainer == nameContainer) {
      if (name.equals("ESAllPrim")) {
        return new CsdlEntitySet()
            .setName("ESAllPrim")
            .setType(EntityTypeProvider.nameETAllPrim)
            .setTitle("All PropertyTypes EntitySet")
            .setNavigationPropertyBindings(Arrays
                .asList(new CsdlNavigationPropertyBinding().setPath("NavPropertyETTwoPrimOne").setTarget("ESTwoPrim"),
                    new CsdlNavigationPropertyBinding().setPath("NavPropertyETTwoPrimMany").setTarget("ESTwoPrim")))
            .setAnnotations(Arrays.asList(new CsdlAnnotation().setTerm("Core.Description").setExpression(
                new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String,
                    "Contains entities with all primitive types")),
                new CsdlAnnotation().setTerm("Core.LongDescription").setQualifier("EnabledForEntitySet").setExpression(
                    new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String,
                        "System Query Options: $filter, $count, $orderby, $skip, $top, $expand, $select, $format; "
                            + "Operations: Create, Create with Deep Insert, Create with Bind Operation, Read")),
                new CsdlAnnotation().setTerm("Core.LongDescription").setQualifier("EnabledForEntity").setExpression(
                    new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String,
                        "System Query Options: $expand, $select, $format; Operations: "
                            + "Read, Update, Update with Bind Operation, Delete")),
                new CsdlAnnotation().setTerm("Core.LongDescription").setQualifier("EnabledNavigationProperties")
                    .setExpression(new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String,
                        "NavPropertyETTwoPrimOne, NavPropertyETTwoPrimMany"))));

      } else if (name.equals("ESCollAllPrim")) {
        return new CsdlEntitySet()
            .setName("ESCollAllPrim")
            .setType(EntityTypeProvider.nameETCollAllPrim)
            .setAnnotations(
                Arrays
                    .asList(
                        new CsdlAnnotation()
                            .setTerm("Org.OData.Core.V1.Description")
                            .setExpression(
                                new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                                    .setValue("Contains entities with collection of each primitive type")),
                        new CsdlAnnotation()
                            .setTerm("Org.OData.Core.V1.LongDescription")
                            .setQualifier("EnabledForEntitySet")
                            .setExpression(
                                new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                                    .setValue("System Query Options: $filter, $count, $orderby, "
                                        + "$skip, $top, $expand, $select, $format; Operations: Create, Read")),
                        new CsdlAnnotation()
                            .setTerm("Org.OData.Core.V1.LongDescription")
                            .setQualifier("EnabledForEntity")
                            .setExpression(
                                new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                                    .setValue("System Query Options: $expand, $select, "
                                        + "$format; Operations: Read, Update, Delete"))));

      } else if (name.equals("ESTwoPrim")) {
        return new CsdlEntitySet()
            .setName("ESTwoPrim")
            .setType(EntityTypeProvider.nameETTwoPrim)
            .setNavigationPropertyBindings(Arrays.asList(
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETAllPrimOne")
                    .setTarget("ESAllPrim"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETAllPrimMany")
                    .setTarget("ESAllPrim")))
            .setAnnotations(Arrays.asList(
                new CsdlAnnotation()
                    .setTerm("Core.Description")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("Contains entities with two primitve types")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntitySet")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $filter, $count, $orderby, $skip, $top, $expand, "
                                + "$select, $format; Operations: Create, Create with Deep Insert, "
                                + "Create with Bind Operation, Read")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntity")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $expand, $select, $format; Operations: Read, Update, "
                                + "Update with Bind Operation, Delete")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledNavigationProperties")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("NavPropertyETAllPrimOne, NavPropertyETAllPrimMany"))));
      } else if (name.equals("ESMixPrimCollComp")) {
        return new CsdlEntitySet()
            .setName("ESMixPrimCollComp")
            .setType(EntityTypeProvider.nameETMixPrimCollComp)
            .setAnnotations(Arrays.asList(
                new CsdlAnnotation()
                    .setTerm("Core.Description")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("Contains entities with various properties of type primitive, collection "
                                + "of primitve, complex and collection of complex")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntitySet")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $filter, $count, $orderby, $skip, $top, $expand, "
                                + "$select, $format; Operations: Create, Read")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntity")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $expand, $select, $format; Operations: Read, "
                                + "Update, Delete"))));

      } else if (name.equals("ESBase")) {
        return new CsdlEntitySet()
            .setName("ESBase")
            .setType(EntityTypeProvider.nameETBase)
            .setAnnotations(Arrays.asList(
                new CsdlAnnotation()
                    .setTerm("Core.Description")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("Contains entities with single inheritance")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntitySet")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $filter, $count, $orderby, $skip, $top, $expand, "
                                + "$select, $format; Operations: Create, Read")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntity")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $expand, $select, $format; Operations: Read, "
                                + "Update, Delete"))));

      } else if (name.equals("ESTwoBase")) {
        return new CsdlEntitySet()
            .setName("ESTwoBase")
            .setType(EntityTypeProvider.nameETTwoBase)
            .setAnnotations(Arrays.asList(
                new CsdlAnnotation()
                    .setTerm("Core.Description")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("Contains entities with double inheritance")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntitySet")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $filter, $count, $orderby, $skip, $top, $expand, "
                                + "$select, $format; Operations: Create, Read")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntity")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $expand, $select, $format; Operations: Read, "
                                + "Update, Delete"))));

      } else if (name.equals("ESTwoKeyTwoPrim")) {
        return new CsdlEntitySet()
            .setName("ESTwoKeyTwoPrim")
            .setType(EntityTypeProvider.nameETTwoKeyTwoPrim)
            .setAnnotations(Arrays.asList(
                new CsdlAnnotation()
                    .setTerm("Core.Description")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("Contains entities with two primitive types with two keys")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntitySet")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $filter, $count, $orderby, $skip, $top, $expand, $select,"
                                + " $format; Operations: Create, Read")),
                new CsdlAnnotation()
                    .setTerm("Core.LongDescription")
                    .setQualifier("EnabledForEntity")
                    .setExpression(
                        new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String)
                            .setValue("System Query Options: $expand, $select, $format; Operations: Read, Update, "
                                + "Delete"))));

      } else if (name.equals("ESBaseTwoKeyTwoPrim")) {
        return new CsdlEntitySet()
            .setName("ESBaseTwoKeyTwoPrim")
            .setType(EntityTypeProvider.nameETBaseTwoKeyTwoPrim);

      } else if (name.equals("ESTwoBaseTwoKeyTwoPrim")) {
        return new CsdlEntitySet()
            .setName("ESTwoBaseTwoKeyTwoPrim")
            .setType(EntityTypeProvider.nameETTwoBaseTwoKeyTwoPrim);

      } else if (name.equals("ESAllKey")) {
        return new CsdlEntitySet()
            .setName("ESAllKey")
            .setType(EntityTypeProvider.nameETAllKey);

      } else if (name.equals("ESCompAllPrim")) {
        return new CsdlEntitySet()
            .setName("ESCompAllPrim")
            .setType(EntityTypeProvider.nameETCompAllPrim);

      } else if (name.equals("ESCompCollAllPrim")) {
        return new CsdlEntitySet()
            .setName("ESCompCollAllPrim")
            .setType(EntityTypeProvider.nameETCompCollAllPrim);

      } else if (name.equals("ESCompComp")) {
        return new CsdlEntitySet()
            .setName("ESCompComp")
            .setType(EntityTypeProvider.nameETCompComp);

      } else if (name.equals("ESCompCollComp")) {
        return new CsdlEntitySet()
            .setName("ESCompCollComp")
            .setType(EntityTypeProvider.nameETCompCollComp);

      } else if (name.equals("ESMedia")) {
        return new CsdlEntitySet()
            .setName("ESMedia")
            .setType(EntityTypeProvider.nameETMedia)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("ESKeyTwoKeyComp")) {
        return new CsdlEntitySet()
            .setName("ESKeyTwoKeyComp")
            .setType(EntityTypeProvider.nameETKeyTwoKeyComp);

      } else if (name.equals("ESInvisible")) {
        return new CsdlEntitySet()
            .setName("ESInvisible")
            .setIncludeInServiceDocument(false)
            .setType(EntityTypeProvider.nameETAllPrim);

      } else if (name.equals("ESServerSidePaging")) {
        return new CsdlEntitySet()
            .setName("ESServerSidePaging")
            .setType(EntityTypeProvider.nameETServerSidePaging);

      } else if (name.equals("ESAllNullable")) {
        return new CsdlEntitySet()
            .setName("ESAllNullable")
            .setType(EntityTypeProvider.nameETAllNullable);

      } else if (name.equals("ESKeyNav")) {
        return new CsdlEntitySet()
            .setName("ESKeyNav")
            .setType(EntityTypeProvider.nameETKeyNav)
            .setNavigationPropertyBindings(Arrays.asList(
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavMany")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavOne")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETMediaOne")
                    .setTarget("ESMedia"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETMediaMany")
                    .setTarget("ESMedia"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/NavPropertyETTwoKeyNavOne")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/NavPropertyETMediaOne")
                    .setTarget("ESMedia"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/NavPropertyETMediaMany")
                    .setTarget("ESMedia"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompCompNav/PropertyCompNav/NavPropertyETTwoKeyNavOne")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompCompNav/PropertyCompNav/NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompCompNav/PropertyCompNav/NavPropertyETMediaOne")
                    .setTarget("ESMedia"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompCompNav/PropertyCompNav/NavPropertyETMediaMany")
                    .setTarget("ESMedia"),
                new CsdlNavigationPropertyBinding()
                    .setPath("ETKeyNav/PropertyCompNav/NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("ETKeyNav/PropertyCompNav/NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/com.corp.odata.test1.CTNavFiveProp/NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav")));

      } else if (name.equals("ESTwoKeyNav")) {
        return new CsdlEntitySet()
            .setName("ESTwoKeyNav")
            .setType(EntityTypeProvider.nameETTwoKeyNav)
            .setNavigationPropertyBindings(Arrays.asList(
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavMany")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavOne")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/NavPropertyETTwoKeyNavOne")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNav/NavPropertyETKeyNavMany")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("CollPropertyCompNav/NavPropertyETTwoKeyNavOne")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("CollPropertyCompNav/NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("CollPropertyCompNav/NavPropertyETMediaOne")
                    .setTarget("ESMedia"),
                new CsdlNavigationPropertyBinding()
                    .setPath("CollPropertyCompNav/NavPropertyETMediaMany")
                    .setTarget("ESMedia"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoBaseTwoKeyNavOne")
                    .setTarget("ESBaseTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("ETBaseTwoKeyNav/CollPropertyCompNav/NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("ETBaseTwoKeyNav/NavPropertyETTwoBaseTwoKeyNavOne")
                    .setTarget("ESBaseTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertySINav")
                    .setTarget("SINav")));

      } else if (name.equals("ESKeyNavCont")) {
        return new CsdlEntitySet()
            .setName("ESKeyNavCont")
            .setType(EntityTypeProvider.nameETKeyNavCont)
            .setNavigationPropertyBindings(Arrays.asList(
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavMany/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavContOne")
                    .setTarget("ESTwoKeyNavCont"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavContMany")
                    .setTarget("ESTwoKeyNavCont"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNavCont/NavPropertyETKeyNavOne/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNavCont/NavPropertyETKeyNavMany/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNavCont/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("PropertyCompNavCont/NavPropertyETTwoKeyNavMany/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav")));
        
      } else if (name.equals("ESTwoKeyNavCont")) {
        return new CsdlEntitySet()
            .setName("ESTwoKeyNavCont")
            .setType(EntityTypeProvider.nameETTwoKeyNavCont)
            .setNavigationPropertyBindings(Arrays.asList(
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavContOne/NavPropertyETTwoKeyNavContOne")
                    .setTarget("ESTwoKeyNavCont"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavContMany/NavPropertyETTwoKeyNavContOne")
                    .setTarget("ESTwoKeyNavCont"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavContOne/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavContMany/NavPropertyETTwoKeyNavMany/NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav")));
        
      } else if (name.equals("ESBaseTwoKeyNav")) {
        return new CsdlEntitySet()
            .setName("ESBaseTwoKeyNav")
            .setType(EntityTypeProvider.nameETBaseTwoKeyNav)
            .setNavigationPropertyBindings(Arrays.asList(
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavMany")
                    .setTarget("ESKeyNav")));

      } else if (name.equals("ESTwoBaseTwoKeyNav")) {
        return new CsdlEntitySet()
            .setName("ESTwoBaseTwoKeyNav")
            .setType(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

      } else if (name.equals("ESCompMixPrimCollComp")) {
        return new CsdlEntitySet()
            .setName("ESCompMixPrimCollComp")
            .setType(EntityTypeProvider.nameETCompMixPrimCollComp);

      } else if (name.equals("ESFourKeyAlias")) {
        return new CsdlEntitySet()
            .setName("ESFourKeyAlias")
            .setType(EntityTypeProvider.nameETFourKeyAlias);

      } else if (name.equals("ESMixEnumDefCollComp")) {
        return new CsdlEntitySet().setName("ESMixEnumDefCollComp")
            .setType(EntityTypeProvider.nameETMixEnumDefCollComp);
      }
    }

    return null;
  }

  public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String name)
      throws ODataException {
    if (entityContainer.equals(nameContainer)) {
      if (name.equals(AIRT_STRING)) {
        return new CsdlActionImport()
            .setName(AIRT_STRING)
            .setAction(ActionProvider.nameUARTString);

      } else if (name.equals(AIRT_COLL_STRING_TWO_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRT_COLL_STRING_TWO_PARAM)
            .setAction(ActionProvider.nameUARTCollStringTwoParam);

      } else if (name.equals(AIRTCT_TWO_PRIM_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRTCT_TWO_PRIM_PARAM)
            .setAction(ActionProvider.nameUARTCTTwoPrimParam);

      } else if (name.equals(AIRT_COLL_CT_TWO_PRIM_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRT_COLL_CT_TWO_PRIM_PARAM)
            .setAction(ActionProvider.nameUARTCollCTTwoPrimParam);

      } else if (name.equals(AIRTET_TWO_KEY_TWO_PRIM_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRTET_TWO_KEY_TWO_PRIM_PARAM)
            .setAction(ActionProvider.nameUARTETTwoKeyTwoPrimParam);

      } else if (name.equals(AIRT_COLL_ET_KEY_NAV_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRT_COLL_ET_KEY_NAV_PARAM)
            .setAction(ActionProvider.nameUARTCollETKeyNavParam);

      } else if (name.equals(AIRTES_ALL_PRIM_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRTES_ALL_PRIM_PARAM)
            .setEntitySet("ESAllPrim")
            .setAction(ActionProvider.nameUARTETAllPrimParam);

      } else if (name.equals(AIRT_COLL_ES_ALL_PRIM_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRT_COLL_ES_ALL_PRIM_PARAM)
            .setEntitySet("ESAllPrim")
            .setAction(ActionProvider.nameUARTCollETAllPrimParam);

      } else if (name.equals(AIRT)) {
        return new CsdlActionImport()
            .setName(AIRT)
            .setAction(ActionProvider.nameUART);

      } else if (name.equals(AIRT_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRT_PARAM)
            .setAction(ActionProvider.nameUARTParam);

      } else if (name.equals(AIRT_TWO_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRT_TWO_PARAM)
            .setAction(ActionProvider.nameUARTTwoParam);

      } else if (name.equals(AIRT_BYTE_NINE_PARAM)) {
        return new CsdlActionImport()
            .setName(AIRT_BYTE_NINE_PARAM)
            .setAction(ActionProvider.nameUARTByteNineParam);
      }
    }

    return null;
  }

  public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String name)
      throws ODataException {

    if (entityContainer.equals(nameContainer)) {
      if (name.equals("FINRTInt16")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setTitle("Simple FunctionImport")
            .setFunction(FunctionProvider.nameUFNRTInt16)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FINInvisibleRTInt16")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFNRTInt16);

      } else if (name.equals("FINInvisible2RTInt16")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFNRTInt16);

      } else if (name.equals("FICRTETKeyNav")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTETKeyNav);

      } else if (name.equals("FICRTESTwoKeyNav")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTETTwoKeyNav)
            .setEntitySet(entityContainer.getFullQualifiedNameAsString() + "/ESTwoKeyNav")
            .setIncludeInServiceDocument(true);
      } else if (name.equals("FICRTETTwoKeyNavParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTETTwoKeyNavParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTStringTwoParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTStringTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollStringTwoParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCollStringTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTAllPrimTwoParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCTAllPrimTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTESMixPrimCollCompTwoParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTESMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FINRTCollETMixPrimCollCompTwoParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFNRTCollETMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollETMixPrimCollCompTwoParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCollETMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollCTTwoPrim")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCollCTTwoPrim)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FINRTByteNineParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFNRTByteNineParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTESMedia")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTETMedia)
            .setEntitySet(entityContainer.getFullQualifiedNameAsString() + "/ESMedia")
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollESMedia")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCollETMedia)
            .setEntitySet(entityContainer.getFullQualifiedNameAsString() + "/ESMedia")
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTTwoPrimTwoParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCTTwoPrimTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTTwoPrim")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCTTwoPrim)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollString")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCollString)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTString")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTString)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollESTwoKeyNavParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCollETTwoKeyNavParam)
            .setEntitySet(entityContainer.getFullQualifiedNameAsString() + "/ESTwoKeyNav")
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollCTTwoPrimTwoParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCollCTTwoPrimTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FINRTCollCTNavFiveProp")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFNRTCollCTNavFiveProp)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollESKeyNavContParam")) {
        return new CsdlFunctionImport()
            .setName(name)
            .setFunction(FunctionProvider.nameUFCRTCollETKeyNavContParam)
            .setEntitySet("ESKeyNavCont")
            .setIncludeInServiceDocument(true);
      }
    }

    return null;
  }

  public CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String name) throws ODataException {
    if (entityContainer.equals(nameContainer)) {

      if (name.equals("SI")) {
        return new CsdlSingleton()
            .setName("SI")
            .setTitle("Simple Singleton")
            .setType(EntityTypeProvider.nameETTwoPrim);

      } else if (name.equals("SINav")) {
        return new CsdlSingleton()
            .setName("SINav")
            .setType(EntityTypeProvider.nameETTwoKeyNav)
            .setNavigationPropertyBindings(Arrays.asList(
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavMany")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavOne")
                    .setTarget("ESTwoKeyNav"),
                new CsdlNavigationPropertyBinding()
                    .setPath("NavPropertyETKeyNavOne")
                    .setTarget("ESKeyNav")));

      } else if (name.equals("SIMedia")) {
        return new CsdlSingleton()
            .setName("SIMedia")
            .setType(EntityTypeProvider.nameETMedia);
      }
    }
    return null;
  }
}
