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
package org.apache.olingo.server.tecsvc.data;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Operation;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.tecsvc.provider.ActionProvider;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.FunctionProvider;
import org.apache.olingo.server.tecsvc.provider.SchemaProvider;

public class DataCreator {

  private static final UUID GUID = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");

  private final Map<String, EntityCollection> data;

  public DataCreator(final OData odata, final Edm edm) {
    data = new HashMap<String, EntityCollection>();
    data.put("ESTwoPrim", createESTwoPrim(edm, odata));
    data.put("ESAllPrim", createESAllPrim(edm, odata));
    data.put("ESCompAllPrim", createESCompAllPrim(edm, odata));
    data.put("ESCollAllPrim", createESCollAllPrim(edm, odata));
    data.put("ESMixPrimCollComp", createESMixPrimCollComp(edm, odata));
    data.put("ESAllKey", createESAllKey(edm, odata));
    data.put("ESCompComp", createESCompComp(edm, odata));
    data.put("ESMedia", createESMedia(edm, odata));
    data.put("ESKeyNav", createESKeyNav(edm, odata));
    data.put("ESTwoKeyNav", createESTwoKeyNav(edm, odata));
    data.put("ESCompCollComp", createESCompCollComp(edm, odata));
    data.put("ESServerSidePaging", createESServerSidePaging(edm, odata));
    data.put("ESStreamServerSidePaging", createESStreamServerSidePaging(edm, odata));
    data.put("ESTwoKeyTwoPrim", createESTwoKeyTwoPrim(edm, odata));
    data.put("ESAllNullable", createESAllNullable(edm, odata));
    data.put("ESTwoBase", createESTwoBase(edm, odata));
    data.put("ESBaseTwoKeyNav", createESBaseTwoKeyNav(edm, odata));
    data.put("ESCompCollAllPrim", createESCompCollAllPrim(edm, odata));
    data.put("ESFourKeyAlias", createESFourKeyAlias(edm, odata));
    data.put("ESBase", createESBase(edm, odata));
    data.put("ESCompMixPrimCollComp", createESCompMixPrimCollComp(edm, odata));
    data.put("ESMixEnumDefCollComp", createESMixEnumDefCollComp(edm, odata));
    data.put("ESStream", createESStream(edm, odata));
    data.put("ESWithStream", createESWithStream(edm, odata));
    data.put("ESPeople", createESPeople(edm, odata));
    data.put("SINav", createSINav(edm, odata));
    data.put("SI", createESTwoPrim(edm, odata));
    data.put("ESCompCollDerived", createESCompCollDerived(edm, odata));
    data.put("ESTwoPrimDerived", createESTwoPrimDerived(edm, odata));
    data.put("ESAllPrimDerived", createESAllPrimDerived(edm, odata));
    data.put("ESDelta", createESDelta(edm, odata));
    data.put("ESKeyNavCont", createESKeyNavCont(edm, odata));
    data.put("ETCont", createETCont(edm, odata));
    data.put("ETBaseCont", createETBaseCont(edm, odata));
    data.put("ETTwoCont", createETTwoCont(edm, odata));
    data.put("ESStreamOnComplexProp", createETStreamOnComplexProp(edm, odata));
    
    linkSINav(data);
    linkESTwoPrim(data);
    linkESAllPrim(data);
    linkESAllPrimDerived(data);
    linkESKeyNav(data);
    linkESTwoKeyNav(data);
    linkESPeople(data);
    linkESDelta(data);
    linkESKeyNavCont(data);
    linkETBaseCont(data);
    linkPropCompInESCompMixPrimCollCompToETTwoKeyNav(data);
    linkCollPropCompInESCompMixPrimCollCompToETTwoKeyNav(data);
    linkPropCompInESCompMixPrimCollCompToETMedia(data);
    linkPropMixPrimCompInESCompMixPrimCollCompToETTwoKeyNav(data);
    linkPropMixPrimCompInESCompMixPrimCollCompToCollETTwoKeyNav(data);
    linkColPropCompInESMixPrimCollCompToETTwoKeyNav(data);
    linkETStreamOnComplexPropWithNavigationProperty(data);
    linkETStreamOnComplexPropWithNavigationPropertyMany(data);
  }

  private void linkETStreamOnComplexPropWithNavigationProperty(Map<String, EntityCollection> data) {
    EntityCollection collection = data.get("ESStreamOnComplexProp");
    Entity entity = collection.getEntities().get(1);
    ComplexValue complexValue = entity.getProperties().get(3).asComplex();
    final List<Entity> esStreamTargets = data.get("ESWithStream").getEntities();
    Link link = new Link();
    link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETStreamOnComplexPropOne");
    link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
    link.setTitle("NavPropertyETStreamOnComplexPropOne");
    link.setInlineEntity(esStreamTargets.get(1));
    link.setHref(esStreamTargets.get(1).getId().toASCIIString());
    complexValue.getNavigationLinks().add(link);
  }
  
  private void linkETStreamOnComplexPropWithNavigationPropertyMany(
      Map<String, EntityCollection> data) {
    EntityCollection collection = data.get("ESStreamOnComplexProp");
    Entity entity = collection.getEntities().get(1);
    ComplexValue complexValue = entity.getProperties().get(3).asComplex();
    final List<Entity> esStreamTargets = data.get("ESWithStream").getEntities();
    Link link = new Link();
    link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETStreamOnComplexPropMany");
    link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
    link.setTitle("NavPropertyETStreamOnComplexPropMany");
    EntityCollection target = new EntityCollection();
    target.setCount(2);
    target.getEntities().addAll(Arrays.asList(esStreamTargets.get(0), esStreamTargets.get(1)));
    link.setInlineEntitySet(target);
    link.setHref(entity.getId().toASCIIString() + "/" + 
    entity.getProperties().get(3).getName() + "/"
        + "NavPropertyETStreamOnComplexPropMany");
    complexValue.getNavigationLinks().add(link);
  }

  private EntityCollection createETStreamOnComplexProp(Edm edm, OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    Link readLink = new Link();
    readLink.setRel(Constants.NS_MEDIA_READ_LINK_REL);
    readLink.setHref("readLink");
    Entity entity = new Entity();
    entity.addProperty(createPrimitive("PropertyStream", createImage("darkturquoise")));
    readLink.setInlineEntity(entity);
    
    Link readLink1 = new Link();
    readLink1.setRel(Constants.NS_MEDIA_READ_LINK_REL);
    readLink1.setHref("readLink");
    entity = new Entity();
    entity.addProperty(createPrimitive("PropertyEntityStream", createImage("darkturquoise")));
    readLink1.setInlineEntity(entity);
    
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE))
        .addProperty(new Property(null, "PropertyEntityStream", ValueType.PRIMITIVE, readLink1))
        .addProperty(createComplex("PropertyCompWithStream",
            ComplexTypeProvider.nameCTWithStreamProp.getFullQualifiedNameAsString(),
            new Property(null, "PropertyStream", ValueType.PRIMITIVE, readLink),
            createComplex("PropertyComp", 
                ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
                createPrimitive("PropertyInt16", (short) 333),
                createPrimitive("PropertyString", "TEST123")))));
    
    Link editLink = new Link();
    editLink.setRel(Constants.NS_MEDIA_EDIT_LINK_REL);
    editLink.setHref("http://mediaserver:1234/editLink");
    editLink.setMediaETag("eTag");
    editLink.setType("image/jpeg");
    entity = new Entity();
    entity.addProperty(createPrimitive("PropertyStream", createImage("royalblue")));
    editLink.setInlineEntity(entity);
    
    Link editLink2 = new Link();
    editLink2.setRel(Constants.NS_MEDIA_EDIT_LINK_REL);
    editLink2.setHref("http://mediaserver:1234/editLink");
    editLink2.setMediaETag("eTag");
    editLink2.setType("image/jpeg");
    entity = new Entity();
    entity.addProperty(createPrimitive("PropertyEntityStream", createImage("royalblue")));
    editLink2.setInlineEntity(entity);

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 7))
        .addProperty(createPrimitive("PropertyInt32", (Integer) 10))
        .addProperty(new Property(null, "PropertyEntityStream", ValueType.PRIMITIVE, editLink2))
        .addProperty(createComplex("PropertyCompWithStream",
            ComplexTypeProvider.nameCTWithStreamProp.getFullQualifiedNameAsString(),
            new Property(null, "PropertyStream", ValueType.PRIMITIVE, editLink),
            createComplex("PropertyComp", 
                ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
                createPrimitive("PropertyInt16", (short) 333),
                createPrimitive("PropertyString", "TEST123")))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETStreamOnComplexProp));
    createEntityId(edm, odata, "ESStreamOnComplexProp", entityCollection);
    createOperations("ESStreamOnComplexProp", entityCollection, EntityTypeProvider.nameETStreamOnComplexProp);
    return entityCollection;
  }

  @SuppressWarnings("unchecked")
  private void linkCollPropCompInESCompMixPrimCollCompToETTwoKeyNav(Map<String, EntityCollection> data2) {
    EntityCollection collection = data.get("ESCompMixPrimCollComp");
    Entity entity = collection.getEntities().get(0);
    ComplexValue complexValue = entity.getProperties().get(1).asComplex();
    List<ComplexValue> innerComplexValue = (List<ComplexValue>) complexValue.getValue().get(3).asCollection();
    final List<Entity> esTwoKeyNavTargets = data.get("ESTwoKeyNav").getEntities();
    for (ComplexValue innerValue : innerComplexValue) {
      Link link = new Link();
      link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETTwoKeyNavOne");
      link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
      link.setTitle("NavPropertyETTwoKeyNavOne");
      link.setHref(esTwoKeyNavTargets.get(1).getId().toASCIIString());
      innerValue.getNavigationLinks().add(link);
      link.setInlineEntity(esTwoKeyNavTargets.get(1));
    }
  }

  @SuppressWarnings("unchecked")
  private void linkColPropCompInESMixPrimCollCompToETTwoKeyNav(Map<String, EntityCollection> data2) {
    EntityCollection collection = data.get("ESMixPrimCollComp");
    Entity entity = collection.getEntities().get(0);
    List<ComplexValue> list = (List<ComplexValue>) entity.getProperties().get(3).asCollection();
    final List<Entity> esTwoKeyNavTargets = data.get("ESTwoKeyNav").getEntities();
    for (ComplexValue complexValue : list) {
      Link link = new Link();
      link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETTwoKeyNavOne");
      link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
      link.setTitle("NavPropertyETTwoKeyNavOne");
      link.setHref(esTwoKeyNavTargets.get(1).getId().toASCIIString());
      complexValue.getNavigationLinks().add(link);
      link.setInlineEntity(esTwoKeyNavTargets.get(1)); 
    }
    
    ComplexValue complexValue = (ComplexValue) entity.getProperties().get(2).asComplex();
    Link link = new Link();
    link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETTwoKeyNavOne");
    link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
    link.setTitle("NavPropertyETTwoKeyNavOne");
    link.setHref(esTwoKeyNavTargets.get(0).getId().toASCIIString());
    complexValue.getNavigationLinks().add(link);
    link.setInlineEntity(esTwoKeyNavTargets.get(0)); 
  }

  private void linkPropMixPrimCompInESCompMixPrimCollCompToETTwoKeyNav(
      Map<String, EntityCollection> data) {
    EntityCollection collection = data.get("ESCompMixPrimCollComp");
    Entity entity = collection.getEntities().get(0);
    ComplexValue complexValue = entity.getProperties().get(1).asComplex();
    final List<Entity> esTwoKeyNavTargets = data.get("ESTwoKeyNav").getEntities();
    Link link = new Link();
    link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETTwoKeyNavOne");
    link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
    link.setTitle("NavPropertyETTwoKeyNavOne");
    link.setInlineEntity(esTwoKeyNavTargets.get(1));
    link.setHref(esTwoKeyNavTargets.get(1).getId().toASCIIString());
    complexValue.getNavigationLinks().add(link);
  }
  
  private void linkPropMixPrimCompInESCompMixPrimCollCompToCollETTwoKeyNav(
      Map<String, EntityCollection> data) {
    EntityCollection collection = data.get("ESCompMixPrimCollComp");
    Entity entity = collection.getEntities().get(0);
    ComplexValue complexValue = entity.getProperties().get(1).asComplex();
    final List<Entity> esTwoKeyNavTargets = data.get("ESTwoKeyNav").getEntities();
    Link link = new Link();
    link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETTwoKeyNavMany");
    link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
    link.setTitle("NavPropertyETTwoKeyNavMany");
    EntityCollection target = new EntityCollection();
    target.setCount(2);
    target.getEntities().addAll(Arrays.asList(esTwoKeyNavTargets.get(0), esTwoKeyNavTargets.get(2)));
    link.setInlineEntitySet(target);
    link.setHref(entity.getId().toASCIIString() + "/" + 
    entity.getProperties().get(1).getName() + "/"
        + "NavPropertyETTwoKeyNavMany");
    complexValue.getNavigationLinks().add(link);
  }

  private void linkPropCompInESCompMixPrimCollCompToETMedia(Map<String, EntityCollection> data) {
    EntityCollection collection = data.get("ESCompMixPrimCollComp");
    Entity entity = collection.getEntities().get(0);
    ComplexValue complexValue = entity.getProperties().get(1).asComplex();
    ComplexValue innerComplexValue = complexValue.getValue().get(2).asComplex();
    final List<Entity> esMediaTargets = data.get("ESMedia").getEntities();
    Link link = new Link();
    link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETMediaOne");
    link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
    link.setTitle("NavPropertyETMediaOne");
    link.setHref(esMediaTargets.get(1).getId().toASCIIString());
    innerComplexValue.getNavigationLinks().add(link);
    link.setInlineEntity(esMediaTargets.get(1));
  }

  private void linkPropCompInESCompMixPrimCollCompToETTwoKeyNav(Map<String, EntityCollection> data) {
    EntityCollection collection = data.get("ESCompMixPrimCollComp");
    Entity entity = collection.getEntities().get(0);
    ComplexValue complexValue = entity.getProperties().get(1).asComplex();
    ComplexValue innerComplexValue = complexValue.getValue().get(2).asComplex();
    final List<Entity> esTwoKeyNavTargets = data.get("ESTwoKeyNav").getEntities();
    Link link = new Link();
    link.setRel(Constants.NS_NAVIGATION_LINK_REL + "NavPropertyETTwoKeyNavOne");
    link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
    link.setTitle("NavPropertyETTwoKeyNavOne");
    link.setHref(esTwoKeyNavTargets.get(1).getId().toASCIIString());
    innerComplexValue.getNavigationLinks().add(link);
    link.setInlineEntity(esTwoKeyNavTargets.get(1));
   }

  private EntityCollection createESDelta(final Edm edm, final OData odata) {
   EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "Number:" + Short.MAX_VALUE)));
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MIN_VALUE))
        .addProperty(createPrimitive("PropertyString", "Number:" + Short.MIN_VALUE)));
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", 0))
        .addProperty(createPrimitive("PropertyString", "Number:" + 0)));
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", 100))
        .addProperty(createPrimitive("PropertyString", "Number:" + 100)));
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", -1))
        .addProperty(createPrimitive("PropertyString", "Number:" + -1)));
  

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETDelta));
    createEntityId(edm, odata, "ESDelta", entityCollection);
    createOperations("ESDelta", entityCollection, EntityTypeProvider.nameETDelta);
    return entityCollection;
  }
  
  private EntityCollection createSINav(Edm edm, OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(createESTwoKeyNavEntity((short) 1, "1"));

  setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav));
  createEntityId(edm, odata, "ESTwoKeyNav", entityCollection);
  createOperations("ESTwoKeyNav", entityCollection, EntityTypeProvider.nameETTwoKeyNav);
  return entityCollection;
}
  
  
  @SuppressWarnings("unchecked")
  private EntityCollection createESCompCollDerived(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();
    Property derivedComplexProperty = 
        createComplex("PropertyCompAno",
            ComplexTypeProvider.nameCTBaseAno.getFullQualifiedNameAsString(),
            createPrimitive("PropertyString", "Num111"),
            createPrimitive("AdditionalPropString", "Test123")
        );
    ((ComplexValue)derivedComplexProperty.getValue()).setTypeName( 
        ComplexTypeProvider.nameCTBaseAno.getFullQualifiedNameAsString());
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(
            createComplexCollection("CollPropertyCompAno",
                ComplexTypeProvider.nameCTTwoPrimAno.getFullQualifiedNameAsString(),
                Arrays.asList(new Property[] {
                    createDerived("PropertyString", 
                        ComplexTypeProvider.nameCTTwoPrimAno.getFullQualifiedNameAsString(),
                        "TEST9876")
                }))
            )
        );
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", 12345))
        .addProperty(derivedComplexProperty)
        .addProperty(createComplexDerievedCollection("CollPropertyCompAno",
                ComplexTypeProvider.nameCTTwoPrimAno.getFullQualifiedNameAsString(),
                Arrays.asList(new ComplexValue[] {
                createComplexValue(ComplexTypeProvider.nameCTBaseAno.getFullQualifiedNameAsString(),
                    "CollPropertyCompAno", Arrays.asList(new Property[] {
                        createDerived("AdditionalPropString",
                            ComplexTypeProvider.nameCTBaseAno.getFullQualifiedNameAsString(),
                            "Additional12345"),
                        createDerived("PropertyString", 
                            ComplexTypeProvider.nameCTBaseAno.getFullQualifiedNameAsString(),
                            "TEST12345")
                    }
              )),
                createComplexValue(ComplexTypeProvider.nameCTTwoPrimAno.getFullQualifiedNameAsString(),
                    "CollPropertyCompAno", Arrays.asList(new Property[] {
                        createDerived("PropertyString", 
                            ComplexTypeProvider.nameCTTwoPrimAno.getFullQualifiedNameAsString(),
                            "TESTabcd")
                    }
              ))}
        
     ))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETDeriveCollComp));
    createEntityId(edm, odata, "ESCompCollDerived", entityCollection);
    createOperations("ESCompCollDerived", entityCollection, EntityTypeProvider.nameETDeriveCollComp);
    return entityCollection;
  }

  private ComplexValue createComplexValue(String type, String name, List<Property> properties) {
    ComplexValue complexValue = new ComplexValue();
    complexValue.getValue().addAll(properties);
    complexValue.setTypeName(type);  
    complexValue.setId(URI.create(name));
    return complexValue;
  }

  private Property createComplexDerievedCollection(final String name,
      String type, final List<ComplexValue> list) {
    List<ComplexValue> complexCollection = new ArrayList<ComplexValue>(); 
    complexCollection.addAll(list);
    Property property =  new Property(type, name, ValueType.COLLECTION_COMPLEX, complexCollection);
    createOperations(name, type, property);
    return property;
  
  }

  private EntityCollection createESMixEnumDefCollComp(Edm edm, OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(createETMixEnumDefCollComp("key1", (short) 1));
    entityCollection.getEntities().add(createETMixEnumDefCollComp("key1", (short) 3));
    entityCollection.getEntities().add(createETMixEnumDefCollComp("key1", (short) 4));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETMixEnumDefCollComp));
    createEntityId(edm, odata, "ESMixEnumDefCollComp", entityCollection);
    createOperations("ESMixEnumDefCollComp", entityCollection, EntityTypeProvider.nameETMixEnumDefCollComp);
    return entityCollection;
  }

  private Entity createETMixEnumDefCollComp(String typeDefString, Short enumValue) {
    return new Entity()
        .addProperty(createPrimitive("PropertyEnumString", enumValue))
        .addProperty(createPrimitive("PropertyDefString", typeDefString))
        .addProperty(createPrimitiveCollection("CollPropertyEnumString", enumValue))
        .addProperty(createPrimitiveCollection("CollPropertyDefString", typeDefString));
  }

  private EntityCollection createESCompMixPrimCollComp(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(createETCompMixPrimCollComp((short) 1));
    entityCollection.getEntities().add(createETCompMixPrimCollComp((short) 2));
    entityCollection.getEntities().add(createETCompMixPrimCollComp((short) 3));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETCompMixPrimCollComp));
    createEntityId(edm, odata, "ESCompMixPrimCollComp", entityCollection);
    createOperations("ESCompMixPrimCollComp", entityCollection, EntityTypeProvider.nameETCompMixPrimCollComp);
    return entityCollection;
  }

  @SuppressWarnings("unchecked")
  private Entity createETCompMixPrimCollComp(final Short propertyInt16) {
    return new Entity()
        .addProperty(createPrimitive("PropertyInt16", propertyInt16))
        .addProperty(createComplex("PropertyMixedPrimCollComp",
            ComplexTypeProvider.nameCTMixPrimCollComp.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 1),
            createPrimitiveCollection("CollPropertyString",
                "Employee1@company.example",
                "Employee2@company.example",
                "Employee3@company.example"
            ),
            createComplex("PropertyComp",
                ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
                createPrimitive("PropertyInt16", (short) 333),
                createPrimitive("PropertyString", "TEST123")
            ),
            createComplexCollection("CollPropertyComp",
                ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
                Arrays.asList(new Property[] {
                    createPrimitive("PropertyInt16", (short) 222),
                    createPrimitive("PropertyString", "TEST9876")
                }),
                Arrays.asList(new Property[] {
                    createPrimitive("PropertyInt16", (short) 333),
                    createPrimitive("PropertyString", "TEST123")
                })
            )
            ));
  }

  private EntityCollection createESBase(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 111))
        .addProperty(createPrimitive("PropertyString", "TEST A"))
        .addProperty(createPrimitive("AdditionalPropertyString_5", "TEST A 0815")));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 222))
        .addProperty(createPrimitive("PropertyString", "TEST B"))
        .addProperty(createPrimitive("AdditionalPropertyString_5", "TEST C 0815")));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 333))
        .addProperty(createPrimitive("PropertyString", "TEST C"))
        .addProperty(createPrimitive("AdditionalPropertyString_5", "TEST E 0815")));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETBase));
    createEntityId(edm, odata, "ESBase", entityCollection);
    createOperations("ESBase", entityCollection, EntityTypeProvider.nameETBase);
    return entityCollection;
  }

  private EntityCollection createESFourKeyAlias(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 1))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 11),
            createPrimitive("PropertyString", "Num11")
            ))
        .addProperty(createComplex("PropertyCompComp",
            ComplexTypeProvider.nameCTCompComp.getFullQualifiedNameAsString(),
            createComplex("PropertyComp",
                ComplexTypeProvider.nameCTBase.getFullQualifiedNameAsString(),
                createPrimitive("PropertyInt16", (short) 111),
                createPrimitive("PropertyString", "Num111"),
                createPrimitive("AdditionalPropString", "Test123")
            )
            ))
        );

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETFourKeyAlias));
    createEntityId(edm, odata, "ESFourKeyAlias", entityCollection);
    createOperations("ESFourKeyAlias", entityCollection, EntityTypeProvider.nameETFourKeyAlias);
    return entityCollection;
  }

  private EntityCollection createESCompCollAllPrim(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(createETCompCollAllPrim((short) 5678));
    entityCollection.getEntities().add(createETCompCollAllPrim((short) 12326));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETCompCollAllPrim));
    createEntityId(edm, odata, "ESCompCollAllPrim", entityCollection);
    createOperations("ESCompCollAllPrim", entityCollection, EntityTypeProvider.nameETCompCollAllPrim);
    return entityCollection;
  }

  private Entity createETCompCollAllPrim(short propertyInt16) {
    return new Entity()
        .addProperty(createPrimitive("PropertyInt16", propertyInt16))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTCollAllPrim.getFullQualifiedNameAsString(),
            createPrimitiveCollection("CollPropertyString",
                "Employee1@company.example",
                "Employee2@company.example",
                "Employee3@company.example"),
            createPrimitiveCollection("CollPropertyBoolean",
                true,
                false,
                true),
            createPrimitiveCollection("CollPropertyByte",
                (short) 50,
                (short) 200,
                (short) 249),
            createPrimitiveCollection("CollPropertySByte",
                (byte) -120,
                (byte) 120,
                (byte) 126),
            createPrimitiveCollection("CollPropertyInt16",
                (short) 1000,
                (short) 2000,
                (short) 30112),
            createPrimitiveCollection("CollPropertyInt32",
                23232323,
                11223355,
                10000001),
            createPrimitiveCollection("CollPropertyInt64",
                929292929292L,
                333333333333L,
                444444444444L),
            createPrimitiveCollection("CollPropertySingle",
                (float) 1790,
                (float) 26600,
                (float) 3210),
            createPrimitiveCollection("CollPropertyDouble",
                -17900D,
                -27800000D,
                3210D),
            createPrimitiveCollection("CollPropertyDecimal",
                BigDecimal.valueOf(12),
                BigDecimal.valueOf(-2),
                BigDecimal.valueOf(1234)),
            createPrimitiveCollection("CollPropertyByte",
                (short) 50,
                (short) 200,
                (short) 249),
            createPrimitiveCollection("CollPropertyBinary",
                new byte[] { -85, -51, -17 },
                new byte[] { 1, 35, 69 },
                new byte[] { 84, 103, -119 }
            ),
            createPrimitiveCollection("CollPropertyDate",
                getDate(1958, 12, 3),
                getDate(1999, 8, 5),
                getDate(2013, 6, 25)
            ),
            createPrimitiveCollection("CollPropertyDateTimeOffset",
                getDateTime(2015, 8, 12, 3, 8, 34),
                getDateTime(1970, 3, 28, 12, 11, 10),
                getDateTime(1948, 2, 17, 9, 9, 9)
            ),
            createPrimitiveCollection("CollPropertyDuration",
                getDuration(0, 0, 0, 13),
                getDuration(0, 5, 28, 20),
                getDuration(0, 1, 0, 0)
            ),
            createPrimitiveCollection("CollPropertyGuid",
                UUID.fromString("ffffff67-89ab-cdef-0123-456789aaaaaa"),
                UUID.fromString("eeeeee67-89ab-cdef-0123-456789bbbbbb"),
                UUID.fromString("cccccc67-89ab-cdef-0123-456789cccccc")
            ),
            createPrimitiveCollection("CollPropertyTimeOfDay",
                getTime(4, 14, 13),
                getTime(23, 59, 59),
                getTime(1, 12, 33)
            )
            ));
  }

  private EntityCollection createESBaseTwoKeyNav(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();
    entityCollection.getEntities().add(
        createESTwoKeyNavEntity((short) 1, "1")
            .addProperty(createPrimitive("PropertyDate", getDate(2013, 12, 12))));

    entityCollection.getEntities().add(
        createESTwoKeyNavEntity((short) 1, "2")
            .addProperty(createPrimitive("PropertyDate", getDate(2013, 12, 12))));

    entityCollection.getEntities().add(
        createESTwoKeyNavEntity((short) 2, "1")
            .addProperty(createPrimitive("PropertyDate", getDate(2013, 12, 12))));

    entityCollection.getEntities().add(
        createESTwoKeyNavEntity((short) 3, "1")
            .addProperty(createPrimitive("PropertyDate", getDate(2013, 12, 12))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav));
    createEntityId(edm, odata, "ESBaseTwoKeyNav", entityCollection);
    createOperations("ESBaseTwoKeyNav", entityCollection, EntityTypeProvider.nameETBaseTwoKeyNav);
    return entityCollection;
  }

  private EntityCollection createESTwoBase(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 111))
        .addProperty(createPrimitive("PropertyString", "TEST A"))
        .addProperty(createPrimitive("AdditionalPropertyString_5", "TEST A 0815"))
        .addProperty(createPrimitive("AdditionalPropertyString_6", "TEST B 0815")));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 222))
        .addProperty(createPrimitive("PropertyString", "TEST B"))
        .addProperty(createPrimitive("AdditionalPropertyString_5", "TEST C 0815"))
        .addProperty(createPrimitive("AdditionalPropertyString_6", "TEST D 0815")));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 333))
        .addProperty(createPrimitive("PropertyString", "TEST C"))
        .addProperty(createPrimitive("AdditionalPropertyString_5", "TEST E 0815"))
        .addProperty(createPrimitive("AdditionalPropertyString_6", "TEST F 0815")));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETTwoBase));
    createEntityId(edm, odata, "ESTwoBase", entityCollection);
    createOperations("ESTwoBase", entityCollection, EntityTypeProvider.nameETTwoBase);
    return entityCollection;
  }

  private EntityCollection createESAllNullable(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();
    entityCollection.getEntities().add(
        new Entity()
            .addProperty(createPrimitive("PropertyKey", (short) 1))
            .addProperty(createPrimitive("PropertyInt16", null))
            .addProperty(createPrimitive("PropertyString", null))
            .addProperty(createPrimitive("PropertyBoolean", null))
            .addProperty(createPrimitive("PropertyByte", null))
            .addProperty(createPrimitive("PropertySByte", null))
            .addProperty(createPrimitive("PropertyInt32", null))
            .addProperty(createPrimitive("PropertyInt64", null))
            .addProperty(createPrimitive("PropertySingle", null))
            .addProperty(createPrimitive("PropertyDouble", null))
            .addProperty(createPrimitive("PropertyDecimal", null))
            .addProperty(createPrimitive("PropertyBinary", null))
            .addProperty(createPrimitive("PropertyDate", null))
            .addProperty(createPrimitive("PropertyDateTimeOffset", null))
            .addProperty(createPrimitive("PropertyDuration", null))
            .addProperty(createPrimitive("PropertyGuid", null))
            .addProperty(createPrimitive("PropertyTimeOfDay", null))
            .addProperty(createPrimitiveCollection("CollPropertyString",
                "spiderman@comic.com",
                null,
                "spidergirl@comic.com"))
            .addProperty(createPrimitiveCollection("CollPropertyBoolean",
                true,
                null,
                false))
            .addProperty(createPrimitiveCollection("CollPropertyByte",
                (short) 50,
                null,
                (short) 249))
            .addProperty(createPrimitiveCollection("CollPropertySByte",
                (byte) -120,
                null,
                (byte) 126))
            .addProperty(createPrimitiveCollection("CollPropertyInt16",
                (short) 1000,
                null,
                (short) 30112))
            .addProperty(createPrimitiveCollection("CollPropertyInt32",
                23232323,
                null,
                10000001))
            .addProperty(createPrimitiveCollection("CollPropertyInt64",
                929292929292L,
                null,
                444444444444L))
            .addProperty(createPrimitiveCollection("CollPropertySingle",
                (float) 1790,
                null,
                (float) 3210))
            .addProperty(createPrimitiveCollection("CollPropertyDouble",
                -17900D,
                null,
                3210D))
            .addProperty(createPrimitiveCollection("CollPropertyDecimal",
                BigDecimal.valueOf(12),
                null,
                BigDecimal.valueOf(1234)))
            .addProperty(createPrimitiveCollection("CollPropertyBinary",
                new byte[] { -85, -51, -17 },
                null,
                new byte[] { 84, 103, -119 }))
            .addProperty(createPrimitiveCollection("CollPropertyDate",
                getDate(1958, 12, 3),
                null,
                getDate(2013, 6, 25)))
            .addProperty(createPrimitiveCollection("CollPropertyDateTimeOffset",
                getDateTime(2015, 8, 12, 3, 8, 34),
                null,
                getDateTime(1948, 2, 17, 9, 9, 9)))
            .addProperty(createPrimitiveCollection("CollPropertyDuration",
                getDuration(0, 0, 0, 13),
                null,
                getDuration(0, 1, 0, 0)))
            .addProperty(createPrimitiveCollection("CollPropertyGuid",
                UUID.fromString("ffffff67-89ab-cdef-0123-456789aaaaaa"),
                null,
                UUID.fromString("cccccc67-89ab-cdef-0123-456789cccccc")))
            .addProperty(createPrimitiveCollection("CollPropertyTimeOfDay",
                getTime(4, 14, 13),
                null,
                getTime(0, 37, 13))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETAllNullable));
    createEntityId(edm, odata, "ESAllNullable", entityCollection);
    createOperations("ESAllNullable", entityCollection, EntityTypeProvider.nameETAllNullable);
    return entityCollection;
  }

  protected Map<String, EntityCollection> getData() {
    return data;
  }

  private EntityCollection createESTwoKeyTwoPrim(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();
    entityCollection.getEntities().add(createETTwoKeyTwoPrimEntity((short) 32767, "Test String1"));
    entityCollection.getEntities().add(createETTwoKeyTwoPrimEntity((short) -365, "Test String2"));
    entityCollection.getEntities().add(createETTwoKeyTwoPrimEntity((short) -32766, "Test String3"));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETTwoKeyTwoPrim));
    createEntityId(edm, odata, "ESTwoKeyTwoPrim", entityCollection);
    createOperations("ESTwoKeyTwoPrim", entityCollection, EntityTypeProvider.nameETTwoKeyTwoPrim);
    return entityCollection;
  }

  private Entity createETTwoKeyTwoPrimEntity(final short propertyInt16, final String propertyString) {
    return new Entity()
        .addProperty(createPrimitive("PropertyInt16", propertyInt16))
        .addProperty(createPrimitive("PropertyString", propertyString));
  }

  private EntityCollection createESServerSidePaging(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    for (short i = 1; i <= 503; i++) {
      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("PropertyInt16", i))
          .addProperty(createPrimitive("PropertyString", "Number:" + i)));
    }

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETServerSidePaging));
    createEntityId(edm, odata, "ESServerSidePaging", entityCollection);
    createOperations("ESServerSidePaging", entityCollection, EntityTypeProvider.nameETServerSidePaging);
    return entityCollection;
  }
  
  private EntityCollection createESStreamServerSidePaging(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    for (short i = 1; i <= 503; i++) {

      Link readLink = new Link();
      readLink.setRel(Constants.NS_MEDIA_READ_LINK_REL);
      readLink.setHref("readLink");
      
      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("PropertyInt16", i))
          .addProperty(new Property(null, "PropertyStream", ValueType.PRIMITIVE, readLink)));

      Link editLink = new Link();
      editLink.setRel(Constants.NS_MEDIA_EDIT_LINK_REL);
      editLink.setHref("http://mediaserver:1234/editLink");
      editLink.setMediaETag("eTag");
      editLink.setType("image/jpeg");

      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("PropertyInt16", ++i))
          .addProperty(new Property(null, "PropertyStream", ValueType.PRIMITIVE, editLink)));   
    }

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETStreamServerSidePaging));
    
    createEntityId(edm, odata, "ESStreamServerSidePaging", entityCollection);
    createOperations("ESStreamServerSidePaging", entityCollection, EntityTypeProvider.nameETStreamServerSidePaging);
    return entityCollection;
  }

  private EntityCollection createESKeyNav(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(createETKeyNavEntity((short) 1, "I am String Property 1"));
    entityCollection.getEntities().add(createETKeyNavEntity((short) 2, "I am String Property 2"));
    entityCollection.getEntities().add(createETKeyNavEntity((short) 3, "I am String Property 3"));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETKeyNav));
    createEntityId(edm, odata, "ESKeyNav", entityCollection);
    createOperations("ESKeyNav", entityCollection, EntityTypeProvider.nameETKeyNav);
    return entityCollection;
  }

  @SuppressWarnings("unchecked")
  private Entity createETKeyNavEntity(final short propertyInt16, final String propertyString) {
    return new Entity()
        .addProperty(createPrimitive("PropertyInt16", propertyInt16))
        .addProperty(createPrimitive("PropertyString", propertyString))
        .addProperty(createComplex("PropertyCompNav",
            ComplexTypeProvider.nameCTNavFiveProp.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", 1)))
        .addProperty(createKeyNavAllPrimComplexValue("PropertyCompAllPrim"))
        .addProperty(createComplex("PropertyCompTwoPrim",
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 16),
            createPrimitive("PropertyString", "Test123")))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "Employee1@company.example",
            "Employee2@company.example",
            "Employee3@company.example"))
        .addProperty(createPrimitiveCollection("CollPropertyInt16",
            (short) 1000,
            (short) 2000,
            (short) 30112))
        .addProperty(createComplexCollection("CollPropertyComp",
            ComplexTypeProvider.nameCTPrimComp.getFullQualifiedNameAsString(),
            Arrays.asList(
                createPrimitive("PropertyInt16", (short) 1),
                createKeyNavAllPrimComplexValue("PropertyComp")),
            Arrays.asList(
                createPrimitive("PropertyInt16", (short) 2),
                createKeyNavAllPrimComplexValue("PropertyComp")),
            Arrays.asList(
                createPrimitive("PropertyInt16", (short) 3),
                createKeyNavAllPrimComplexValue("PropertyComp"))))
        .addProperty(createComplex("PropertyCompCompNav",
            ComplexTypeProvider.nameCTCompNav.getFullQualifiedNameAsString(),
            createPrimitive("PropertyString", "1"),
            createComplex("PropertyCompNav",
                ComplexTypeProvider.nameCTNavFiveProp.getFullQualifiedNameAsString(),
                createPrimitive("PropertyInt16", (short) 1))));
  }

  private EntityCollection createESTwoKeyNav(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(createESTwoKeyNavEntity((short) 1, "1"));
    entityCollection.getEntities().add(createESTwoKeyNavEntity((short) 1, "2"));
    entityCollection.getEntities().add(createESTwoKeyNavEntity((short) 2, "1"));
    entityCollection.getEntities().add(createESTwoKeyNavEntity((short) 3, "1"));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav));
    createEntityId(edm, odata, "ESTwoKeyNav", entityCollection);
    createOperations("ESTwoKeyNav", entityCollection, EntityTypeProvider.nameETTwoKeyNav);
    return entityCollection;
  }

  @SuppressWarnings("unchecked")
  private Entity createESTwoKeyNavEntity(final short propertyInt16, final String propertyString) {
    return new Entity()
        .addProperty(createPrimitive("PropertyInt16", propertyInt16))
        .addProperty(createPrimitive("PropertyString", propertyString))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTPrimComp.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", 11),
            createComplex("PropertyComp",
                ComplexTypeProvider.nameCTAllPrim.getFullQualifiedNameAsString(),
                createPrimitive("PropertyString", "StringValue"),
                createPrimitive("PropertyBinary", new byte[] { 1, 35, 69, 103, -119, -85, -51, -17 }),
                createPrimitive("PropertyBoolean", true),
                createPrimitive("PropertyByte", (short) 255),
                createPrimitive("PropertyDate", getDate(2012, 12, 3)),
                createPrimitive("PropertyDecimal", BigDecimal.valueOf(34)),
                createPrimitive("PropertySingle", (float) 179000000000000000000D),
                createPrimitive("PropertyDouble", -179000000000000000000D),
                createPrimitive("PropertyDuration", BigDecimal.valueOf(6)),
                createPrimitive("PropertyGuid", UUID.fromString("01234567-89ab-cdef-0123-456789abcdef")),
                createPrimitive("PropertyInt16", Short.MAX_VALUE),
                createPrimitive("PropertyInt32", Integer.MAX_VALUE),
                createPrimitive("PropertyInt64", Long.MAX_VALUE),
                createPrimitive("PropertySByte", Byte.MAX_VALUE),
                createPrimitive("PropertyTimeOfDay", getTime(21, 5, 59)))))
        .addProperty(createComplex("PropertyCompNav",
            ComplexTypeProvider.nameCTBasePrimCompNav.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 1),
            createKeyNavAllPrimComplexValue("PropertyComp")))
        .addProperty(createComplexCollection("CollPropertyComp", null))
        .addProperty(createComplexCollection("CollPropertyCompNav",
            ComplexTypeProvider.nameCTNavFiveProp.getFullQualifiedNameAsString(),
            Arrays.asList(
                createPrimitive("PropertyInt16", (short) 1))))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "1",
            "2"))
        .addProperty(createComplex("PropertyCompTwoPrim",
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 11),
            createPrimitive("PropertyString", "11")));
  }

  protected Property createKeyNavAllPrimComplexValue(final String name) {
    return createComplex(name,
        ComplexTypeProvider.nameCTAllPrim.getFullQualifiedNameAsString(),
        createPrimitive("PropertyString", "First Resource - positive values"),
        createPrimitive("PropertyBinary", new byte[] { 1, 35, 69, 103, -119, -85, -51, -17 }),
        createPrimitive("PropertyBoolean", true),
        createPrimitive("PropertyByte", (short) 255),
        createPrimitive("PropertyDate", getDate(2012, 12, 3)),
        createPrimitive("PropertyDateTimeOffset", getTimestamp(2012, 12, 3, 7, 16, 23, 0)),
        createPrimitive("PropertyDecimal", 34),
        createPrimitive("PropertySingle", (float) 179000000000000000000D),
        createPrimitive("PropertyDouble", -179000000000000000000D),
        createPrimitive("PropertyDuration", BigDecimal.valueOf(6)),
        createPrimitive("PropertyGuid", UUID.fromString("01234567-89ab-cdef-0123-456789abcdef")),
        createPrimitive("PropertyInt16", Short.MAX_VALUE), createPrimitive("PropertyInt32", Integer.MAX_VALUE),
        createPrimitive("PropertyInt64", Long.MAX_VALUE), createPrimitive("PropertySByte", Byte.MAX_VALUE),
        createPrimitive("PropertyTimeOfDay", getTime(21, 5, 59)));
  }

  @SuppressWarnings("unchecked")
  private EntityCollection createESCompCollComp(final Edm edm, final OData odata) {
    final EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTCompCollComp.getFullQualifiedNameAsString(),
            createComplexCollection("CollPropertyComp",
                ComplexTypeProvider.nameCTCompCollComp.getFullQualifiedNameAsString(),
                Arrays.asList(
                    createPrimitive("PropertyInt16", (short) 555),
                    createPrimitive("PropertyString", "1 Test Complex in Complex Property")),
                Arrays.asList(
                    createPrimitive("PropertyInt16", (short) 666),
                    createPrimitive("PropertyString", "2 Test Complex in Complex Property")),
                Arrays.asList(
                    createPrimitive("PropertyInt16", (short) 777),
                    createPrimitive("PropertyString", "3 Test Complex in Complex Property"))))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", 12345))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTCompCollComp.getFullQualifiedNameAsString(),
            createComplexCollection("CollPropertyComp",
                ComplexTypeProvider.nameCTCompCollComp.getFullQualifiedNameAsString(),
                Arrays.asList(
                    createPrimitive("PropertyInt16", (short) 888),
                    createPrimitive("PropertyString", "11 Test Complex in Complex Property")),
                Arrays.asList(
                    createPrimitive("PropertyInt16", (short) 999),
                    createPrimitive("PropertyString", "12 Test Complex in Complex Property")),
                Arrays.asList(
                    createPrimitive("PropertyInt16", (short) 0),
                    createPrimitive("PropertyString", "13 Test Complex in Complex Property"))))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETCompCollComp));
    createEntityId(edm, odata, "ESCompCollComp", entityCollection);
    createOperations("ESCompCollComp", entityCollection, EntityTypeProvider.nameETCompCollComp);
    return entityCollection;
  }

  private EntityCollection createESTwoPrim(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 32766))
        .addProperty(createPrimitive("PropertyString", "Test String1")));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) -365))
        .addProperty(createPrimitive("PropertyString", "Test String2")));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) -32766))
        .addProperty(createPrimitive("PropertyString", null)));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "Test String4")));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETTwoPrim));
    createEntityId(edm, odata, "ESTwoPrim", entityCollection);
    createOperations("ESTwoPrim", entityCollection, EntityTypeProvider.nameETTwoPrim);
    return entityCollection;
  }
  
  private EntityCollection createESTwoPrimDerived(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) -365))
        .addProperty(createPrimitive("PropertyString", "Test String2")));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) -32766))
        .addProperty(createPrimitive("PropertyString", null)));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "Test String4")));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETTwoPrim));
    
    Entity derivedEntity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 32766))
        .addProperty(createPrimitive("PropertyString", "Test String1"))
        .addProperty(createPrimitive("AdditionalPropertyString_5", "Additional String1"));
        derivedEntity.setType(EntityTypeProvider.nameETBase.getFullQualifiedNameAsString());
        entityCollection.getEntities().add(derivedEntity);
        
    createEntityId(edm, odata, "ESTwoPrimDerived", entityCollection);
    createOperations("ESTwoPrimDerived", entityCollection, EntityTypeProvider.nameETTwoPrim);
    return entityCollection;
  }

  private void setEntityType(EntityCollection entityCollection, final EdmEntityType type) {
    for (Entity entity : entityCollection.getEntities()) {
      entity.setType(type.getFullQualifiedName().getFullQualifiedNameAsString());
    }
  }

  private EntityCollection createESAllPrim(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "First Resource - positive values"))
        .addProperty(createPrimitive("PropertyBoolean", true))
        .addProperty(createPrimitive("PropertyByte", (short) 255))
        .addProperty(createPrimitive("PropertySByte", Byte.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MAX_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) 1.79000000E+20))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+19))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2012, 12, 3)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(6)))
        .addProperty(createPrimitive("PropertyGuid", GUID))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(3, 26, 5))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MIN_VALUE))
        .addProperty(createPrimitive("PropertyString", "Second Resource - negative values"))
        .addProperty(createPrimitive("PropertyBoolean", false))
        .addProperty(createPrimitive("PropertyByte", (short) 0))
        .addProperty(createPrimitive("PropertySByte", Byte.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MIN_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) -1.79000000E+08))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+05))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(-34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2015, 11, 5)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 7, 17, 8)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(9)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789dddfff")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(23, 49, 14))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 0))
        .addProperty(createPrimitive("PropertyString", ""))
        .addProperty(createPrimitive("PropertyBoolean", false))
        .addProperty(createPrimitive("PropertyByte", (short) 0))
        .addProperty(createPrimitive("PropertySByte", 0))
        .addProperty(createPrimitive("PropertyInt32", 0))
        .addProperty(createPrimitive("PropertyInt64", 0L))
        .addProperty(createPrimitive("PropertySingle", (float) 0))
        .addProperty(createPrimitive("PropertyDouble", 0D))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyBinary", new byte[] {}))
        .addProperty(createPrimitive("PropertyDate", getDate(1970, 1, 1)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 0, 0, 0)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789cccddd")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(0, 1, 1))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETAllPrim));
    createEntityId(edm, odata, "ESAllPrim", entityCollection);
    createOperations("ESAllPrim", entityCollection, EntityTypeProvider.nameETAllPrim);
    return entityCollection;
  }
  
  private EntityCollection createESAllPrimDerived(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "First Resource - positive values"))
        .addProperty(createPrimitive("PropertyBoolean", true))
        .addProperty(createPrimitive("PropertyByte", (short) 255))
        .addProperty(createPrimitive("PropertySByte", Byte.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MAX_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) 1.79000000E+20))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+19))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2012, 12, 3)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(6)))
        .addProperty(createPrimitive("PropertyGuid", GUID))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(3, 26, 5))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MIN_VALUE))
        .addProperty(createPrimitive("PropertyString", "Second Resource - negative values"))
        .addProperty(createPrimitive("PropertyBoolean", false))
        .addProperty(createPrimitive("PropertyByte", (short) 0))
        .addProperty(createPrimitive("PropertySByte", Byte.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MIN_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) -1.79000000E+08))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+05))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(-34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2015, 11, 5)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 7, 17, 8)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(9)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789dddfff")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(23, 49, 14))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 0))
        .addProperty(createPrimitive("PropertyString", ""))
        .addProperty(createPrimitive("PropertyBoolean", false))
        .addProperty(createPrimitive("PropertyByte", (short) 0))
        .addProperty(createPrimitive("PropertySByte", 0))
        .addProperty(createPrimitive("PropertyInt32", 0))
        .addProperty(createPrimitive("PropertyInt64", 0L))
        .addProperty(createPrimitive("PropertySingle", (float) 0))
        .addProperty(createPrimitive("PropertyDouble", 0D))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyBinary", new byte[] {}))
        .addProperty(createPrimitive("PropertyDate", getDate(1970, 1, 1)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 0, 0, 0)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789cccddd")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(0, 1, 1))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETAllPrim));
    createEntityId(edm, odata, "ESAllPrim", entityCollection);
    createOperations("ESAllPrim", entityCollection, EntityTypeProvider.nameETAllPrim);
    return entityCollection;
  }

  private void createOperations(String entitySetName,
      EntityCollection entityCollection, FullQualifiedName entityTypeName) {
    try {
      List<CsdlAction> actions = ActionProvider.getBoundActionsForEntityType(entityTypeName);
      for (CsdlAction action : actions) {
        if (action.getParameters().get(0).isCollection()) {
          Operation operation = buildOperation(action, entitySetName);
          entityCollection.getOperations().add(operation);          
        } else {
          for (Entity entity:entityCollection.getEntities()) {
            Operation operation = buildOperation(action, entity.getId().toASCIIString());
            entity.getOperations().add(operation);
          }
        }
      }
      
      List<CsdlFunction> functions = FunctionProvider.getBoundFunctionsForType(entityTypeName);
      for (CsdlFunction func : functions) {
        if (func.getParameters().get(0).isCollection()) {
          Operation operation = buildOperation(func, entitySetName);
          entityCollection.getOperations().add(operation);          
        } else {
          for (Entity entity:entityCollection.getEntities()) {
            Operation operation = buildOperation(func, entity.getId().toASCIIString());
            entity.getOperations().add(operation);
          }
        }
      }
    } catch (ODataException e) {
      e.printStackTrace();
    }
  }

  private static Operation buildOperation(CsdlFunction function, String id) {
    String fqn = SchemaProvider.NAMESPACE+"."+function.getName();
    Operation operation = new Operation();          
    operation.setType(Operation.Type.FUNCTION);
    operation.setTitle(fqn);
    StringBuilder params = new StringBuilder();
    StringBuilder nameFQN = new StringBuilder();
    params.append(fqn);
    nameFQN.append(fqn);
    if (!function.getParameters().isEmpty() && function.getParameters().size() > 1) {
      params.append("(");
      nameFQN.append("(");
      boolean first = true;
      for (int i = 1; i < function.getParameters().size(); i++) {
        CsdlParameter p = function.getParameters().get(i);
        if (first) {
          first = false;
        } else {
          params.append(",");
          nameFQN.append(",");                
        }
        params.append(p.getName()).append("=").append("@").append(p.getName());
        nameFQN.append(p.getName());
      }            
      params.append(")");
      nameFQN.append(")");
    }
    operation.setMetadataAnchor("#"+nameFQN);
    operation.setTarget(URI.create(id+"/"+params.toString()));
    return operation;
  }

  private Operation buildOperation(CsdlAction action, String id) {
    String fqn = SchemaProvider.NAMESPACE+"."+action.getName();
    Operation operation = new Operation();
    operation.setMetadataAnchor("#"+fqn);
    operation.setType(Operation.Type.ACTION);
    operation.setTitle(fqn);
    operation.setTarget(URI.create(id+"/"+fqn));
    return operation;
  }

  private EntityCollection createESCompAllPrim(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    Entity entity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTAllPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyString", "First Resource - first"),
            createPrimitive("PropertyBinary",
                new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }),
            createPrimitive("PropertyBoolean", true),
            createPrimitive("PropertyByte", (short) 255),
            createPrimitive("PropertyDate", getDate(2012, 10, 3)),
            createPrimitive("PropertyDateTimeOffset", getTimestamp(2012, 10, 3, 7, 16, 23, 123456700)),
            createPrimitive("PropertyDecimal", BigDecimal.valueOf(34.27)),
            createPrimitive("PropertySingle", (float) 1.79000000E+20),
            createPrimitive("PropertyDouble", -1.7900000000000000E+19),
            createPrimitive("PropertyDuration", BigDecimal.valueOf(6)),
            createPrimitive("PropertyGuid", GUID),
            createPrimitive("PropertyInt16", Short.MAX_VALUE),
            createPrimitive("PropertyInt32", Integer.MAX_VALUE),
            createPrimitive("PropertyInt64", Long.MAX_VALUE),
            createPrimitive("PropertySByte", Byte.MAX_VALUE),
            createPrimitive("PropertyTimeOfDay", getTime(1, 0, 1))));
    entity.setETag("W/\"" + Short.MAX_VALUE + '\"');
    entityCollection.getEntities().add(entity);

    entity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 7))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTAllPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyString", "Second Resource - second"),
            createPrimitive("PropertyBinary",
                new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }),
            createPrimitive("PropertyBoolean", true),
            createPrimitive("PropertyByte", (short) 255),
            createPrimitive("PropertyDate", getDate(2013, 11, 4)),
            createPrimitive("PropertyDateTimeOffset", getDateTime(2013, 11, 4, 7, 16, 23)),
            createPrimitive("PropertyDecimal", BigDecimal.valueOf(34.27)),
            createPrimitive("PropertySingle", (float) 1.79000000E+20),
            createPrimitive("PropertyDouble", -1.7900000000000000E+02),
            createPrimitive("PropertyDuration", BigDecimal.valueOf(6)),
            createPrimitive("PropertyGuid", GUID),
            createPrimitive("PropertyInt16", (short) 25),
            createPrimitive("PropertyInt32", Integer.MAX_VALUE),
            createPrimitive("PropertyInt64", Long.MAX_VALUE),
            createPrimitive("PropertySByte", Byte.MAX_VALUE),
            createPrimitive("PropertyTimeOfDay", getTimestamp(1970, 1, 1, 7, 45, 12, 765432100))));
    entity.setETag("W/\"7\"");
    entityCollection.getEntities().add(entity);

    entity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 0))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTAllPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyString", "Third Resource - third"),
            createPrimitive("PropertyBinary",
                new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }),
            createPrimitive("PropertyBoolean", true),
            createPrimitive("PropertyByte", (short) 255),
            createPrimitive("PropertyDate", getDate(2014, 12, 5)),
            createPrimitive("PropertyDateTimeOffset", getTimestamp(2014, 12, 5, 8, 17, 45, 123456700)),
            createPrimitive("PropertyDecimal", BigDecimal.valueOf(17.98)),
            createPrimitive("PropertySingle", 1.79000000E+20),
            createPrimitive("PropertyDouble", -1.7900000000000000E+02),
            createPrimitive("PropertyDuration", BigDecimal.valueOf(6)),
            createPrimitive("PropertyGuid", GUID),
            createPrimitive("PropertyInt16", (short) -25),
            createPrimitive("PropertyInt32", Integer.MAX_VALUE),
            createPrimitive("PropertyInt64", Long.MAX_VALUE),
            createPrimitive("PropertySByte", Byte.MAX_VALUE),
            createPrimitive("PropertyTimeOfDay", getTime(13, 27, 45))));
    entity.setETag("W/\"0\"");
    entityCollection.getEntities().add(entity);

    entity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) -32768))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTAllPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyString", null),
            createPrimitive("PropertyBinary", null),
            createPrimitive("PropertyBoolean", null),
            createPrimitive("PropertyByte", null),
            createPrimitive("PropertyDate", null),
            createPrimitive("PropertyDateTimeOffset", null),
            createPrimitive("PropertyDecimal", null),
            createPrimitive("PropertySingle", null),
            createPrimitive("PropertyDouble", null),
            createPrimitive("PropertyDuration", null),
            createPrimitive("PropertyGuid", null),
            createPrimitive("PropertyInt16", null),
            createPrimitive("PropertyInt32", null),
            createPrimitive("PropertyInt64", null),
            createPrimitive("PropertySByte", null),
            createPrimitive("PropertyTimeOfDay", null)));
    entity.setETag("W/\"-32768\"");
    entityCollection.getEntities().add(entity);

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETCompAllPrim));
    createEntityId(edm, odata, "ESCompAllPrim", entityCollection);
    createOperations("ESCompAllPrim", entityCollection, EntityTypeProvider.nameETCompAllPrim);
    return entityCollection;
  }

  private EntityCollection createESCollAllPrim(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(createETCollAllPrim((short) 1));
    entityCollection.getEntities().add(createETCollAllPrim((short) 2));
    entityCollection.getEntities().add(createETCollAllPrim((short) 3));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETCollAllPrim));
    createEntityId(edm, odata, "ESCollAllPrim", entityCollection);
    createOperations("ESCollAllPrim", entityCollection, EntityTypeProvider.nameETCollAllPrim);
    return entityCollection;
  }

  private Entity createETCollAllPrim(final Short propertyInt16) {
    return new Entity()
        .addProperty(createPrimitive("PropertyInt16", propertyInt16))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "Employee1@company.example",
            "Employee2@company.example",
            "Employee3@company.example"))
        .addProperty(createPrimitiveCollection("CollPropertyBoolean",
            true,
            false,
            true))
        .addProperty(createPrimitiveCollection("CollPropertyByte",
            (short) 50,
            (short) 200,
            (short) 249))
        .addProperty(createPrimitiveCollection("CollPropertySByte",
            -120,
            120,
            126))
        .addProperty(createPrimitiveCollection("CollPropertyInt16",
            (short) 1000,
            (short) 2000,
            (short) 30112))
        .addProperty(createPrimitiveCollection("CollPropertyInt32",
            23232323,
            11223355,
            10000001))
        .addProperty(createPrimitiveCollection("CollPropertyInt64",
            929292929292L,
            333333333333L,
            444444444444L))
        .addProperty(createPrimitiveCollection("CollPropertySingle",
            1.79000000E+03,
            2.66000000E+04,
            3.21000000E+03))
        .addProperty(createPrimitiveCollection("CollPropertyDouble",
            -1.7900000000000000E+04,
            -2.7800000000000000E+07,
            3.2100000000000000E+03))
        .addProperty(createPrimitiveCollection("CollPropertyDecimal",
            BigDecimal.valueOf(12),
            BigDecimal.valueOf(-2),
            BigDecimal.valueOf(1234)))
        .addProperty(createPrimitiveCollection("CollPropertyBinary",
            new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF },
            new byte[] { 0x01, 0x23, 0x45 },
            new byte[] { 0x54, 0x67, (byte) 0x89 }))
        .addProperty(createPrimitiveCollection("CollPropertyDate",
            getDate(1958, 12, 3),
            getDate(1999, 8, 5),
            getDate(2013, 6, 25)))
        .addProperty(createPrimitiveCollection("CollPropertyDateTimeOffset",
            getDateTime(2015, 8, 12, 3, 8, 34),
            getDateTime(1970, 3, 28, 12, 11, 10),
            getDateTime(1948, 2, 17, 9, 9, 9)))
        .addProperty(createPrimitiveCollection("CollPropertyDuration",
            BigDecimal.valueOf(13),
            BigDecimal.valueOf(19680),
            BigDecimal.valueOf(3600)))
        .addProperty(createPrimitiveCollection("CollPropertyGuid",
            UUID.fromString("ffffff67-89ab-cdef-0123-456789aaaaaa"),
            UUID.fromString("eeeeee67-89ab-cdef-0123-456789bbbbbb"),
            UUID.fromString("cccccc67-89ab-cdef-0123-456789cccccc")))
        .addProperty(createPrimitiveCollection("CollPropertyTimeOfDay",
            getTime(4, 14, 13),
            getTime(23, 59, 59),
            getTime(1, 12, 33)));
  }

  private EntityCollection createESMixPrimCollComp(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "Employee1@company.example",
            "Employee2@company.example",
            "Employee3@company.example"))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 111),
            createPrimitive("PropertyString", "TEST A")))
        .addProperty(createCollPropertyComp()));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 7))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "Employee1@company.example",
            "Employee2@company.example",
            "Employee3@company.example"))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 222),
            createPrimitive("PropertyString", "TEST B")))
        .addProperty(createCollPropertyComp()));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 0))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "Employee1@company.example",
            "Employee2@company.example",
            "Employee3@company.example"))
        .addProperty(createComplex("PropertyComp",
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 333),
            createPrimitive("PropertyString", "TEST C")))
        .addProperty(createCollPropertyComp()));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETMixPrimCollComp));
    createEntityId(edm, odata, "ESMixPrimCollComp", entityCollection);
    createOperations("ESMixPrimCollComp", entityCollection, EntityTypeProvider.nameETMixPrimCollComp);
    return entityCollection;
  }

  private EntityCollection createESStream(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "Streamed-Employee1@company.example",
            "Streamed-Employee2@company.example",
            "Streamed-Employee3@company.example"))
        .addProperty(createComplex("PropertyComp",
            null,
            createPrimitive("PropertyInt16", (short) 111),
            createPrimitive("PropertyString", "TEST A")))
        .addProperty(createCollPropertyComp()));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 7))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "Streamed-Employee1@company.example",
            "Streamed-Employee2@company.example",
            "Streamed-Employee3@company.example"))
        .addProperty(createComplex("PropertyComp",
            null,
            createPrimitive("PropertyInt16", (short) 222),
            createPrimitive("PropertyString", "TEST B")))
        .addProperty(createCollPropertyComp()));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 0))
        .addProperty(createPrimitiveCollection("CollPropertyString",
            "Streamed-Employee1@company.example",
            "Streamed-Employee2@company.example",
            "Streamed-Employee3@company.example"))
        .addProperty(createComplex("PropertyComp",
            null,
            createPrimitive("PropertyInt16", (short) 333),
            createPrimitive("PropertyString", "TEST C")))
        .addProperty(createCollPropertyComp()));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETMixPrimCollComp));
    createEntityId(edm, odata, "ESStream", entityCollection);
    createOperations("ESStream", entityCollection, EntityTypeProvider.nameETMixPrimCollComp);
    return entityCollection;
  }

  private EntityCollection createESPeople(final Edm edm, final OData odata) {
      EntityCollection entityCollection = new EntityCollection();

      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("id", 0))
          .addProperty(createPrimitive("name", "A")));

      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("id", 1))
          .addProperty(createPrimitive("name", "B")));

      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("id", 2))
          .addProperty(createPrimitive("name", "C")));

      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("id", 3))
          .addProperty(createPrimitive("name", "D")));

      setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETPeople));
      createEntityId(edm, odata, "ESPeople", entityCollection);
      return entityCollection;
  }
  
  private EntityCollection createESWithStream(final Edm edm, final OData odata) {
      EntityCollection entityCollection = new EntityCollection();

      Link readLink = new Link();
      readLink.setRel(Constants.NS_MEDIA_READ_LINK_REL);
      readLink.setHref("readLink");
      Entity entity = new Entity();
      entity.addProperty(createPrimitive("PropertyStream", createImage("darkturquoise")));
      readLink.setInlineEntity(entity);
      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
          .addProperty(new Property(null, "PropertyStream", ValueType.PRIMITIVE, readLink)));

      Link editLink = new Link();
      editLink.setRel(Constants.NS_MEDIA_EDIT_LINK_REL);
      editLink.setHref("http://mediaserver:1234/editLink");
      editLink.setMediaETag("eTag");
      editLink.setType("image/jpeg");
      entity = new Entity();
      entity.addProperty(createPrimitive("PropertyStream", createImage("royalblue")));
      editLink.setInlineEntity(entity);

      entityCollection.getEntities().add(new Entity()
          .addProperty(createPrimitive("PropertyInt16", (short) 7))
          .addProperty(new Property(null, "PropertyStream", ValueType.PRIMITIVE, editLink)));

      setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETStream));
      createEntityId(edm, odata, "ESWithStream", entityCollection);
      createOperations("ESWithStream", entityCollection, EntityTypeProvider.nameETStream);
      return entityCollection;
    }  
  
  private Property createCollPropertyComp() {
    return createComplexDerievedCollection("CollPropertyComp",
        ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
        Arrays.asList(new ComplexValue[] {
            createComplexValue(ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
                "CollPropertyComp", Arrays.asList(new Property[] {
                    createPrimitive("PropertyInt16", (short) 123),
                    createPrimitive("PropertyString", "TEST 1")
                }
          )),
            createComplexValue(ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
                "CollPropertyComp", Arrays.asList(new Property[] {
                    createPrimitive("PropertyInt16", (short) 456),
                    createPrimitive("PropertyString", "TEST 2")
                }
          )),
            createComplexValue(ComplexTypeProvider.nameCTBase.getFullQualifiedNameAsString(),
                "CollPropertyComp", Arrays.asList(new Property[] {
                    createPrimitive("PropertyInt16", (short) 789),
                    createPrimitive("PropertyString", "TEST 3"),
                    createPrimitive("AdditionalPropString", "ADD TEST")
                }
          ))}));
  }

  private EntityCollection createESAllKey(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyString", "First"))
        .addProperty(createPrimitive("PropertyBoolean", true))
        .addProperty(createPrimitive("PropertyByte", (short) 255))
        .addProperty(createPrimitive("PropertySByte", Byte.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MAX_VALUE))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(34)))
        .addProperty(createPrimitive("PropertyDate", getDate(2012, 12, 3)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(6)))
        .addProperty(createPrimitive("PropertyGuid", GUID))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(2, 48, 21))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyString", "Second"))
        .addProperty(createPrimitive("PropertyBoolean", true))
        .addProperty(createPrimitive("PropertyByte", (short) 254))
        .addProperty(createPrimitive("PropertySByte", (byte) 124))
        .addProperty(createPrimitive("PropertyInt16", (short) 32764))
        .addProperty(createPrimitive("PropertyInt32", 2147483644))
        .addProperty(createPrimitive("PropertyInt64", 9223372036854775804L))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(34)))
        .addProperty(createPrimitive("PropertyDate", getDate(2012, 12, 3)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(6)))
        .addProperty(createPrimitive("PropertyGuid", GUID))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(2, 48, 21))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETAllKey));
    createEntityId(edm, odata, "ESAllKey", entityCollection);
    createOperations( "ESAllKey", entityCollection, EntityTypeProvider.nameETAllKey);
    return entityCollection;
  }

  private EntityCollection createESCompComp(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    Entity entity = new Entity();
    entity.addProperty(createPrimitive("PropertyInt16", (short) 1));
    entity.addProperty(createComplex("PropertyComp",
        ComplexTypeProvider.nameCTCompComp.getFullQualifiedNameAsString(),
        createComplex("PropertyComp",
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 123),
            createPrimitive("PropertyString", "String 1"))));
    entityCollection.getEntities().add(entity);

    entity = new Entity();
    entity.addProperty(createPrimitive("PropertyInt16", (short) 2));
    entity.addProperty(createComplex("PropertyComp", 
        ComplexTypeProvider.nameCTCompComp.getFullQualifiedNameAsString(), 
        createComplex("PropertyComp",
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            createPrimitive("PropertyInt16", (short) 987),
            createPrimitive("PropertyString", "String 2"))));
    entityCollection.getEntities().add(entity);

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETCompComp));
    createEntityId(edm, odata, "ESCompComp", entityCollection);
    createOperations("ESCompComp", entityCollection, EntityTypeProvider.nameETCompComp);
    return entityCollection;
  }

  private EntityCollection createESMedia(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    Entity entity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 1))
        .addProperty(createPrimitive(DataProvider.MEDIA_PROPERTY_NAME, createImage("darkturquoise")));
    entity.setMediaContentType("image/svg+xml");
    entity.setMediaETag("W/\"1\"");
    entity.getMediaEditLinks().add(buildMediaLink("ESMedia", "ESMedia(1)/$value"));
    entityCollection.getEntities().add(entity);

    entity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 2))
        .addProperty(createPrimitive(DataProvider.MEDIA_PROPERTY_NAME, createImage("royalblue")));
    entity.setMediaContentType("image/svg+xml");
    entity.setMediaETag("W/\"2\"");
    entity.getMediaEditLinks().add(buildMediaLink("ESMedia", "ESMedia(2)/$value"));
    entityCollection.getEntities().add(entity);

    entity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 3))
        .addProperty(createPrimitive(DataProvider.MEDIA_PROPERTY_NAME, createImage("crimson")));
    entity.setMediaContentType("image/svg+xml");
    entity.setMediaETag("W/\"3\"");
    entity.getMediaEditLinks().add(buildMediaLink("ESMedia", "ESMedia(3)/$value"));
    entityCollection.getEntities().add(entity);

    entity = new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 4))
        .addProperty(createPrimitive(DataProvider.MEDIA_PROPERTY_NAME, createImage("black")));
    entity.setMediaContentType("image/svg+xml");
    entity.setMediaETag("W/\"4\"");
    entity.getMediaEditLinks().add(buildMediaLink("ESMedia", "ESMedia(4)/$value"));
    entityCollection.getEntities().add(entity);

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETMedia));
    createEntityId(edm, odata, "ESMedia", entityCollection);
    createOperations("ESMedia", entityCollection, EntityTypeProvider.nameETMedia);
    return entityCollection;
  }

  private byte[] createImage(final String color) {
    return ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 100 100\">\n"
        + "  <g stroke=\"darkmagenta\" stroke-width=\"16\" fill=\"" + color + "\">\n"
        + "    <circle cx=\"50\" cy=\"50\" r=\"42\"/>\n" + "  </g>\n" + "</svg>\n").getBytes(Charset.forName("UTF-8"));
  }
  
  
  private void linkESDelta(final Map<String, EntityCollection> data) {
    final EntityCollection entityCollection = data.get("ESDelta");
    final List<Entity> targetEntities = data.get("ESAllPrim").getEntities();

    setLinks(entityCollection.getEntities().get(0), "NavPropertyETAllPrimMany", targetEntities.get(1),
        targetEntities.get(2));
    setLink(entityCollection.getEntities().get(3), "NavPropertyETAllPrimOne", targetEntities.get(0));
  }

  private void linkESPeople(final Map<String, EntityCollection> data) {
      final EntityCollection entityCollection = data.get("ESPeople");
      final List<Entity> targetEntities = data.get("ESPeople").getEntities();

      setLinks(entityCollection.getEntities().get(0), "friends", targetEntities.get(1),
          targetEntities.get(2));
      setLinks(entityCollection.getEntities().get(1), "friends", targetEntities.get(0),
              targetEntities.get(2));
      setLinks(entityCollection.getEntities().get(2), "friends", targetEntities.get(0),
              targetEntities.get(3));      
  }
  
  private void linkESTwoPrim(final Map<String, EntityCollection> data) {
    final EntityCollection entityCollection = data.get("ESTwoPrim");
    final List<Entity> targetEntities = data.get("ESAllPrim").getEntities();

    setLinks(entityCollection.getEntities().get(1), "NavPropertyETAllPrimMany", targetEntities.get(1),
        targetEntities.get(2));
    setLink(entityCollection.getEntities().get(3), "NavPropertyETAllPrimOne", targetEntities.get(0));
  }

  private void linkESAllPrim(final Map<String, EntityCollection> data) {
    final EntityCollection entityCollection = data.get("ESAllPrim");
    final List<Entity> targetEntities = data.get("ESTwoPrim").getEntities();
    final List<Entity> targetESBaseEntities = data.get("ESBase").getEntities();

    setLinks(entityCollection.getEntities().get(0), "NavPropertyETTwoPrimMany", targetEntities.get(1));
    setLink(entityCollection.getEntities().get(0), "NavPropertyETTwoPrimOne", targetEntities.get(3));

    setLinks(entityCollection.getEntities().get(2), "NavPropertyETTwoPrimMany", targetEntities.get(0),
        targetEntities.get(2),
        targetEntities.get(3));
    
    setLink(entityCollection.getEntities().get(2), "NavPropertyETTwoPrimOne", targetESBaseEntities.get(0));
  }
  
  private void linkESAllPrimDerived(final Map<String, EntityCollection> data) {
    final EntityCollection entityCollection = data.get("ESAllPrimDerived");
    final List<Entity> targetEntities = data.get("ESTwoPrimDerived").getEntities();

    setLinks(entityCollection.getEntities().get(0), "NavPropertyETTwoPrimMany", targetEntities.get(1));
    setLink(entityCollection.getEntities().get(0), "NavPropertyETTwoPrimOne", targetEntities.get(3));

    setLinks(entityCollection.getEntities().get(2), "NavPropertyETTwoPrimMany", targetEntities.get(0),
        targetEntities.get(2),
        targetEntities.get(3));
  }

  private void linkESKeyNav(final Map<String, EntityCollection> data) {
    final EntityCollection entityCollection = data.get("ESKeyNav");
    final List<Entity> esKeyNavTargets = data.get("ESKeyNav").getEntities();
    final List<Entity> esTwoKeyNavTargets = data.get("ESTwoKeyNav").getEntities();
    final List<Entity> esMediaTargets = data.get("ESMedia").getEntities();

    // NavPropertyETKeyNavMany
    setLinks(entityCollection.getEntities().get(0), "NavPropertyETKeyNavMany", esKeyNavTargets.get(0),
        esKeyNavTargets.get(1));
    setLinks(entityCollection.getEntities().get(1), "NavPropertyETKeyNavMany", esKeyNavTargets.get(1),
        esKeyNavTargets.get(2));

    // NavPropertyETKeyNavOne
    setLink(entityCollection.getEntities().get(0), "NavPropertyETKeyNavOne", esKeyNavTargets.get(1));
    setLink(entityCollection.getEntities().get(1), "NavPropertyETKeyNavOne", esKeyNavTargets.get(2));

    // NavPropertyETTwoKeyNavOne
    setLink(entityCollection.getEntities().get(0), "NavPropertyETTwoKeyNavOne", esTwoKeyNavTargets.get(0));
    setLink(entityCollection.getEntities().get(1), "NavPropertyETTwoKeyNavOne", esTwoKeyNavTargets.get(1));
    setLink(entityCollection.getEntities().get(2), "NavPropertyETTwoKeyNavOne", esTwoKeyNavTargets.get(2));

    // NavPropertyETTwoKeyNavMany
    setLinks(entityCollection.getEntities().get(0), "NavPropertyETTwoKeyNavMany", esTwoKeyNavTargets.get(0),
        esTwoKeyNavTargets.get(1));
    setLinks(entityCollection.getEntities().get(1), "NavPropertyETTwoKeyNavMany", esTwoKeyNavTargets.get(2));
    setLinks(entityCollection.getEntities().get(2), "NavPropertyETTwoKeyNavMany", esTwoKeyNavTargets.get(3));

    // NavPropertyETMediaOne
    setLink(entityCollection.getEntities().get(0), "NavPropertyETMediaOne", esMediaTargets.get(0));
    setLink(entityCollection.getEntities().get(1), "NavPropertyETMediaOne", esMediaTargets.get(1));
    setLink(entityCollection.getEntities().get(2), "NavPropertyETMediaOne", esMediaTargets.get(2));

    // NavPropertyETMediaMany
    setLinks(entityCollection.getEntities().get(0), "NavPropertyETMediaMany", esMediaTargets.get(0),
        esMediaTargets.get(2));
    setLinks(entityCollection.getEntities().get(1), "NavPropertyETMediaMany", esMediaTargets.get(2));
    setLinks(entityCollection.getEntities().get(2), "NavPropertyETMediaMany", esMediaTargets.get(0),
        esMediaTargets.get(1));
  }

  private void linkESTwoKeyNav(final Map<String, EntityCollection> data) {
    final EntityCollection entityCollection = data.get("ESTwoKeyNav");
    final List<Entity> esKeyNavTargets = data.get("ESKeyNav").getEntities();
    final List<Entity> esTwoKeyNavTargets = data.get("ESTwoKeyNav").getEntities();
    final  List<Entity> SINav = data.get("SINav").getEntities();
    
    // NavPropertyETKeyNavOne
    setLink(entityCollection.getEntities().get(0), "NavPropertyETKeyNavOne", esKeyNavTargets.get(0));
    setLink(entityCollection.getEntities().get(1), "NavPropertyETKeyNavOne", esKeyNavTargets.get(0));
    setLink(entityCollection.getEntities().get(2), "NavPropertyETKeyNavOne", esKeyNavTargets.get(1));
    setLink(entityCollection.getEntities().get(3), "NavPropertyETKeyNavOne", esKeyNavTargets.get(2));

    // NavPropertyETKeyNavMany
    setLinks(entityCollection.getEntities().get(0), "NavPropertyETKeyNavMany", esKeyNavTargets.get(0),
        esKeyNavTargets.get(1));
    setLinks(entityCollection.getEntities().get(1), "NavPropertyETKeyNavMany", esKeyNavTargets.get(0),
        esKeyNavTargets.get(1));
    setLinks(entityCollection.getEntities().get(2), "NavPropertyETKeyNavMany", esKeyNavTargets.get(1),
        esKeyNavTargets.get(2));

    // NavPropertyETTwoKeyNavOne
    setLink(entityCollection.getEntities().get(0), "NavPropertyETTwoKeyNavOne", esTwoKeyNavTargets.get(0));
    setLink(entityCollection.getEntities().get(2), "NavPropertyETTwoKeyNavOne", esTwoKeyNavTargets.get(1));
    setLink(entityCollection.getEntities().get(3), "NavPropertyETTwoKeyNavOne", esTwoKeyNavTargets.get(2));

    // NavPropertyETTwoKeyNavMany
    setLinks(entityCollection.getEntities().get(0), "NavPropertyETTwoKeyNavMany", esTwoKeyNavTargets.get(0),
        esTwoKeyNavTargets.get(1));
    setLinks(entityCollection.getEntities().get(1), "NavPropertyETTwoKeyNavMany", esTwoKeyNavTargets.get(0));
    setLinks(entityCollection.getEntities().get(2), "NavPropertyETTwoKeyNavMany", esTwoKeyNavTargets.get(1));
    // NavPropertySINav
    setLink(entityCollection.getEntities().get(0), "NavPropertySINav", SINav.get(0));
       setLink(entityCollection.getEntities().get(1), "NavPropertySINav", SINav.get(0));
       setLink(entityCollection.getEntities().get(2), "NavPropertySINav", SINav.get(0));

  }

   private void linkSINav(final Map<String, EntityCollection> data) {
     final EntityCollection entityCollection = data.get("SINav");
     final List<Entity> esKeyNavTargets = data.get("ESKeyNav").getEntities();
     final List<Entity> esTwoKeyNavTargets = data.get("ESTwoKeyNav").getEntities();

     // NavPropertyETKeyNavOne
     setLink(entityCollection.getEntities().get(0), "NavPropertyETKeyNavOne", esKeyNavTargets.get(0));
   
     // NavPropertyETTwoKeyNavOne
     setLink(entityCollection.getEntities().get(0), "NavPropertyETTwoKeyNavOne", esTwoKeyNavTargets.get(0));

     // NavPropertyETTwoKeyNavMany
     setLinks(entityCollection.getEntities().get(0), "NavPropertyETTwoKeyNavMany", esTwoKeyNavTargets.get(0),
         esTwoKeyNavTargets.get(1));
  }
   
   private void linkESKeyNavCont(final Map<String, EntityCollection> data) {
     final EntityCollection entityCollection = data.get("ESKeyNavCont");
     final List<Entity> targetEntities = data.get("ETCont").getEntities();
     final List<Entity> etBaseContEntities = data.get("ETBaseCont").getEntities();
     final List<Entity> etTwoKeyNav = data.get("ESTwoKeyNav").getEntities();
     
     setLinks(entityCollection.getEntities().get(1), "NavPropertyETContMany", targetEntities.get(1),
         targetEntities.get(2));
     
     setLinkForContNav(entityCollection.getEntities().get(3), "NavPropertyETContOne", targetEntities.get(0));
     
     setLinks(entityCollection.getEntities().get(2), "NavPropertyETBaseContMany", etBaseContEntities.get(1),
         etBaseContEntities.get(2));

     setLinks(entityCollection.getEntities().get(4), "NavPropertyETTwoKeyNavMany", etTwoKeyNav.get(0),
         etTwoKeyNav.get(2));
     
     setLinkForContNav(entityCollection.getEntities().get(4), "NavPropertyETTwoKeyNavOne", etTwoKeyNav.get(0));
   }
   
   protected static void setLinkForContNav(final Entity entity, 
       final String navigationPropertyName, final Entity target) {
     Link link = entity.getNavigationLink(navigationPropertyName);
     if (link == null) {
       link = new Link();
       link.setRel(Constants.NS_NAVIGATION_LINK_REL + navigationPropertyName);
       link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
       link.setTitle(navigationPropertyName);
       link.setHref(entity.getId().toASCIIString() + 
           (navigationPropertyName != null && navigationPropertyName.length() > 0 ? "/" + navigationPropertyName: ""));
       entity.getNavigationLinks().add(link);
     }
     link.setInlineEntity(target);
   }

  protected static Property createPrimitive(final String name, final Object value) {
    return new Property(null, name, ValueType.PRIMITIVE, value);
  }

  protected static Property createPrimitiveCollection(final String name, final Object... values) {
    List<Object> propertyValues = new ArrayList<Object>();
    for (final Object value : values) {
      propertyValues.add(value);
    }
    return new Property(null, name, ValueType.COLLECTION_PRIMITIVE, propertyValues);
  }
  
  protected static Property createDerived(final String name, final String type, final Object value) {
    return new Property(type, name, ValueType.PRIMITIVE, value);
  }
  
  protected static Property createComplex(final String name, final String type, final Property... properties) {
    ComplexValue complexValue = new ComplexValue();
    for (final Property property : properties) {
      complexValue.getValue().add(property);
    }
    if (null != name) {
      complexValue.setId(URI.create(name));
    }
    Property property = new Property(type, name, ValueType.COMPLEX, complexValue);
    createOperations(name, type, property);
    return property;
  }

  private static void createOperations(final String name, final String type,
      Property property) {
    if (type != null) {
      try {
        List<CsdlFunction> functions = FunctionProvider.getBoundFunctionsForType(new FullQualifiedName(type));
        for (CsdlFunction func : functions) {
          if (func.getParameters().get(0).isCollection() && property.isCollection()) {
            Operation operation = buildOperation(func, name);
            property.getOperations().add(operation);          
          } else {
            Operation operation = buildOperation(func, name);
            property.getOperations().add(operation);
          }
        }        
      } catch (ODataException e) {
        // ignore
      }
    }
  }  

  protected static Property createComplexCollection(final String name,
      String type, final List<Property>... propertiesList) {
    List<ComplexValue> complexCollection = new ArrayList<ComplexValue>();
    for (final List<Property> properties : propertiesList) {
      ComplexValue complexValue = new ComplexValue();
      complexValue.getValue().addAll(properties);
      if (null != name) {
        complexValue.setId(URI.create(name));
      }
      complexCollection.add(complexValue);
    }
    Property property =  new Property(type, name, ValueType.COLLECTION_COMPLEX, complexCollection);
    createOperations(name, type, property);
    return property;
  }

  private static Calendar getDateTime(final int year, final int month, final int day,
      final int hour, final int minute, final int second) {
    // Date/Time values are serialized with a timezone offset, so we choose a predictable timezone.
    Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.clear();
    dateTime.set(year, month - 1, day, hour, minute, second);
    dateTime.set(Calendar.MILLISECOND, 0);
    return dateTime;
  }

  private static int getDuration(final int days, final int hours, final int minutes, final int seconds) {
    return days * 24 * 60 * 60
        + hours * 60 * 60
        + minutes * 60
        + seconds;
  }

  private static Calendar getDate(final int year, final int month, final int day) {
    // Date values are always in the local timezone.
    Calendar date = Calendar.getInstance();
    date.clear();
    date.set(year, month - 1, day, 0, 0, 0);
    date.set(Calendar.MILLISECOND, 0);
    return date;
  }

  private static Calendar getTime(final int hour, final int minute, final int second) {
    // Time values are always in the local timezone.
    Calendar time = Calendar.getInstance();
    time.clear();
    time.set(1970, Calendar.JANUARY, 1, hour, minute, second);
    time.set(Calendar.MILLISECOND, 0);
    return time;
  }

  private static Timestamp getTimestamp(final int year, final int month, final int day,
      final int hour, final int minute, final int second, final int nanosecond) {
    Timestamp timestamp = new Timestamp(getDateTime(year, month, day, hour, minute, second).getTimeInMillis());
    timestamp.setNanos(nanosecond);
    return timestamp;
  }

  protected static Link buildMediaLink(String title, String href) {
    Link link = new Link();
    link.setRel("edit-media");
    link.setTitle(title);
    link.setHref(href);
    return link;
  }

  protected static void setLink(final Entity entity, final String navigationPropertyName, final Entity target) {
    Link link = entity.getNavigationLink(navigationPropertyName);
    if (link == null) {
      link = new Link();
      link.setRel(Constants.NS_NAVIGATION_LINK_REL + navigationPropertyName);
      link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
      link.setTitle(navigationPropertyName);
      link.setHref(target.getId().toASCIIString());
      entity.getNavigationLinks().add(link);
    }
    link.setInlineEntity(target);
  }

  protected static void setLinks(final Entity entity, final String navigationPropertyName, final Entity... targets) {
    Link link = entity.getNavigationLink(navigationPropertyName);
    if (link == null) {
      link = new Link();
      link.setRel(Constants.NS_NAVIGATION_LINK_REL + navigationPropertyName);
      link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
      link.setTitle(navigationPropertyName);
      EntityCollection target = new EntityCollection();
      target.getEntities().addAll(Arrays.asList(targets));
      link.setInlineEntitySet(target);
      link.setHref(entity.getId().toASCIIString() + "/" + navigationPropertyName);
      entity.getNavigationLinks().add(link);
    } else {
      link.getInlineEntitySet().getEntities().addAll(Arrays.asList(targets));
    }
  }

  private void createEntityId(final Edm edm, final OData odata,
      final String entitySetName, final EntityCollection entities) {
    final EdmEntitySet entitySet = edm.getEntityContainer().getEntitySet(entitySetName);
    final UriHelper helper = odata.createUriHelper();
    for (Entity entity : entities.getEntities()) {
      try {
        entity.setId(URI.create(helper.buildCanonicalURL(entitySet, entity)));
      } catch (final SerializerException e) {
        entity.setId(null);
      }
    }
  }
  
  private EntityCollection createESKeyNavCont(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 32766))
        .addProperty(createPrimitive("PropertyString", "Test String1"))
        .addProperty(createComplex("PropertyCompNavCont",
            ComplexTypeProvider.nameCTNavCont.getFullQualifiedNameAsString())));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) -365))
        .addProperty(createPrimitive("PropertyString", "Test String2"))
        .addProperty(createComplex("PropertyCompNavCont",
            ComplexTypeProvider.nameCTNavCont.getFullQualifiedNameAsString())));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) -32766))
        .addProperty(createPrimitive("PropertyString", "Test String3"))
        .addProperty(createComplex("PropertyCompNavCont",
            ComplexTypeProvider.nameCTNavCont.getFullQualifiedNameAsString())));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "Test String4"))
        .addProperty(createComplex("PropertyCompNavCont",
            ComplexTypeProvider.nameCTNavCont.getFullQualifiedNameAsString())));
    
    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 1))
        .addProperty(createPrimitive("PropertyString", "Test String1"))
        .addProperty(createComplex("PropertyCompNavCont",
            ComplexTypeProvider.nameCTNavCont.getFullQualifiedNameAsString())));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETKeyNavCont));
    createEntityId(edm, odata, "ESKeyNavCont", entityCollection);
    createOperations("ESKeyNavCont", entityCollection, EntityTypeProvider.nameETKeyNavCont);
    return entityCollection;
  }
  
  private EntityCollection createETCont(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "First Resource - positive values"))
        .addProperty(createPrimitive("PropertyBoolean", true))
        .addProperty(createPrimitive("PropertyByte", (short) 255))
        .addProperty(createPrimitive("PropertySByte", Byte.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MAX_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) 1.79000000E+20))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+19))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2012, 12, 3)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(6)))
        .addProperty(createPrimitive("PropertyGuid", GUID))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(3, 26, 5))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MIN_VALUE))
        .addProperty(createPrimitive("PropertyString", "Second Resource - negative values"))
        .addProperty(createPrimitive("PropertyBoolean", false))
        .addProperty(createPrimitive("PropertyByte", (short) 0))
        .addProperty(createPrimitive("PropertySByte", Byte.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MIN_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) -1.79000000E+08))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+05))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(-34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2015, 11, 5)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 7, 17, 8)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(9)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789dddfff")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(23, 49, 14))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 0))
        .addProperty(createPrimitive("PropertyString", ""))
        .addProperty(createPrimitive("PropertyBoolean", false))
        .addProperty(createPrimitive("PropertyByte", (short) 0))
        .addProperty(createPrimitive("PropertySByte", 0))
        .addProperty(createPrimitive("PropertyInt32", 0))
        .addProperty(createPrimitive("PropertyInt64", 0L))
        .addProperty(createPrimitive("PropertySingle", (float) 0))
        .addProperty(createPrimitive("PropertyDouble", 0D))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyBinary", new byte[] {}))
        .addProperty(createPrimitive("PropertyDate", getDate(1970, 1, 1)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 0, 0, 0)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789cccddd")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(0, 1, 1))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETCont));
    createEntityId(edm, odata, "ESKeyNavCont", entityCollection);
    String id = "ESKeyNavCont(-365)";
    id = id + "/NavPropertyETContMany(" + 
    entityCollection.getEntities().get(1).getProperty("PropertyInt16").getValue() + ")";
    entityCollection.getEntities().get(1).setId(URI.create(id));
    id = "ESKeyNavCont(-365)";
    id = id + "/NavPropertyETContMany(" + 
    entityCollection.getEntities().get(2).getProperty("PropertyInt16").getValue() + ")";
    entityCollection.getEntities().get(2).setId(URI.create(id));
    id = "ESKeyNavCont(32766)";
    id = id + "/NavPropertyETContOne(" + 
    entityCollection.getEntities().get(0).getProperty("PropertyInt16").getValue() + ")";
    entityCollection.getEntities().get(0).setId(URI.create(id));
    return entityCollection;
  }
  
  private EntityCollection createETBaseCont(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "First Resource - positive values"))
        .addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MAX_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) 1.79000000E+20))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+19))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2012, 12, 3)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(6)))
        .addProperty(createPrimitive("PropertyGuid", GUID))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(3, 26, 5))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MIN_VALUE))
        .addProperty(createPrimitive("PropertyString", "Second Resource - negative values"))
        .addProperty(createPrimitive("PropertyInt32", Integer.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MIN_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) -1.79000000E+08))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+05))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(-34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2015, 11, 5)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 7, 17, 8)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(9)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789dddfff")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(23, 49, 14))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 0))
        .addProperty(createPrimitive("PropertyString", ""))
        .addProperty(createPrimitive("PropertyInt32", 0))
        .addProperty(createPrimitive("PropertyInt64", 0L))
        .addProperty(createPrimitive("PropertySingle", (float) 0))
        .addProperty(createPrimitive("PropertyDouble", 0D))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyBinary", new byte[] {}))
        .addProperty(createPrimitive("PropertyDate", getDate(1970, 1, 1)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 0, 0, 0)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789cccddd")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(0, 1, 1))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETBaseCont));
    createEntityId(edm, odata, "ESKeyNavCont", entityCollection);
    String id = "ESKeyNavCont(-32766)";
    id = id + "/NavPropertyETBaseContMany(" + 
    entityCollection.getEntities().get(1).getProperty("PropertyInt16").getValue() + ")";
    entityCollection.getEntities().get(1).setId(URI.create(id));
    id = "ESKeyNavCont(-32766)";
    id = id + "/NavPropertyETBaseContMany(" + 
    entityCollection.getEntities().get(2).getProperty("PropertyInt16").getValue() + ")";
    entityCollection.getEntities().get(2).setId(URI.create(id));
    return entityCollection;
  }
  
  private void linkETBaseCont(final Map<String, EntityCollection> data) {
    final EntityCollection entityCollection = data.get("ETBaseCont");
    final List<Entity> etTwoContEntities = data.get("ETTwoCont").getEntities();
    
    setLinkForContNav(entityCollection.getEntities().get(1), 
        "NavPropertyETBaseContTwoContOne", etTwoContEntities.get(0));
    
    setLinks(entityCollection.getEntities().get(2), "NavPropertyETBaseContTwoContMany", etTwoContEntities.get(1),
        etTwoContEntities.get(2));
  }
  
  private EntityCollection createETTwoCont(final Edm edm, final OData odata) {
    EntityCollection entityCollection = new EntityCollection();

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE))
        .addProperty(createPrimitive("PropertyString", "First Resource - positive values"))
        .addProperty(createPrimitive("PropertyBoolean", true))
        .addProperty(createPrimitive("PropertyByte", (short) 255))
        .addProperty(createPrimitive("PropertySByte", Byte.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MAX_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) 1.79000000E+20))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+19))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2012, 12, 3)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(6)))
        .addProperty(createPrimitive("PropertyGuid", GUID))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(3, 26, 5))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", Short.MIN_VALUE))
        .addProperty(createPrimitive("PropertyString", "Second Resource - negative values"))
        .addProperty(createPrimitive("PropertyBoolean", false))
        .addProperty(createPrimitive("PropertyByte", (short) 0))
        .addProperty(createPrimitive("PropertySByte", Byte.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt32", Integer.MIN_VALUE))
        .addProperty(createPrimitive("PropertyInt64", Long.MIN_VALUE))
        .addProperty(createPrimitive("PropertySingle", (float) -1.79000000E+08))
        .addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+05))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(-34)))
        .addProperty(createPrimitive("PropertyBinary",
            new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }))
        .addProperty(createPrimitive("PropertyDate", getDate(2015, 11, 5)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 7, 17, 8)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(9)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789dddfff")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(23, 49, 14))));

    entityCollection.getEntities().add(new Entity()
        .addProperty(createPrimitive("PropertyInt16", (short) 0))
        .addProperty(createPrimitive("PropertyString", ""))
        .addProperty(createPrimitive("PropertyBoolean", false))
        .addProperty(createPrimitive("PropertyByte", (short) 0))
        .addProperty(createPrimitive("PropertySByte", 0))
        .addProperty(createPrimitive("PropertyInt32", 0))
        .addProperty(createPrimitive("PropertyInt64", 0L))
        .addProperty(createPrimitive("PropertySingle", (float) 0))
        .addProperty(createPrimitive("PropertyDouble", 0D))
        .addProperty(createPrimitive("PropertyDecimal", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyBinary", new byte[] {}))
        .addProperty(createPrimitive("PropertyDate", getDate(1970, 1, 1)))
        .addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 0, 0, 0)))
        .addProperty(createPrimitive("PropertyDuration", BigDecimal.valueOf(0)))
        .addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789cccddd")))
        .addProperty(createPrimitive("PropertyTimeOfDay", getTime(0, 1, 1))));

    setEntityType(entityCollection, edm.getEntityType(EntityTypeProvider.nameETTwoCont));
    createEntityId(edm, odata, "ESKeyNavCont", entityCollection);
    String id = "ESKeyNavCont(-32766)/NavPropertyETBaseContMany(-32768)";
    id = id + "/NavPropertyETBaseContTwoContOne(" + 
    entityCollection.getEntities().get(0).getProperty("PropertyInt16").getValue() + ")";
    entityCollection.getEntities().get(0).setId(URI.create(id));
    id = "ESKeyNavCont(-32766)/NavPropertyETBaseContMany(0)";
    id = id + "/NavPropertyETBaseContTwoContMany(" + 
    entityCollection.getEntities().get(1).getProperty("PropertyInt16").getValue() + ")";
    entityCollection.getEntities().get(1).setId(URI.create(id));
    id = "ESKeyNavCont(-32766)/NavPropertyETBaseContMany(0)";
    id = id + "/NavPropertyETBaseContTwoContMany(" + 
        entityCollection.getEntities().get(2).getProperty("PropertyInt16").getValue() + ")";
        entityCollection.getEntities().get(2).setId(URI.create(id));
    return entityCollection;
  }
}
