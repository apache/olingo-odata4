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
package org.apache.olingo.server.core.serializer.json;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.DeletedEntity;
import org.apache.olingo.commons.api.data.DeletedEntity.Reason;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.DeltaLink;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EdmDeltaSerializer;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.ExpandSelectMock;
import org.apache.olingo.server.tecsvc.MetadataETagSupport;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class JsonDeltaSerializerTest {

  final EdmDeltaSerializer ser;
  private static final OData odata = OData.newInstance();
  private static final ServiceMetadata metadata = odata.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList(), new MetadataETagSupport("W/\"metadataETag\""));
  private static final EdmEntityContainer entityContainer = metadata.getEdm().getEntityContainer();
  private final DataProvider data = new DataProvider(odata, metadata.getEdm());
  

  public JsonDeltaSerializerTest() throws SerializerException {
    List<String> versions = new ArrayList<String>();
    versions.add("4.0");
    ser = OData.newInstance().createEdmDeltaSerializer(ContentType.JSON, versions);
  }

  @Test
  public void addedDeltaLink() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
           + "\"@odata.context\":\"#ESDelta/$link\",\"source\":\"ESDelta(100)\","
           + "\"relationship\":\"NavPropertyETAllPrimOne\","
           + "\"target\":\"ESAllPrim(0)\"}]"          
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void deletedDeltaLink() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    List<DeltaLink> deletedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    deletedLinks.add(link1 );
    delta.getDeletedLinks().addAll(deletedLinks);
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
           + "\"@odata.context\":\"#ESDelta/$deletedLink\",\"source\":\"ESDelta(100)\","
           + "\"relationship\":\"NavPropertyETAllPrimOne\","
           + "\"target\":\"ESAllPrim(0)\"}]"          
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void deletedEntity() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    List<DeletedEntity> deletedEntity = new ArrayList<DeletedEntity>();
    DeletedEntity entity1 = new DeletedEntity();
    entity1.setId(new URI("ESDelta(100)"));
    entity1.setReason(Reason.deleted);
    DeletedEntity entity2 = new DeletedEntity();
    entity2.setId(new URI("ESDelta(-32768)"));
    entity2.setReason(Reason.changed);    
    deletedEntity.add(entity1);
    deletedEntity.add(entity2);
    delta.getDeletedEntities().addAll(deletedEntity);
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           +"\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
           + "\"@odata.context\":\"#ESDelta(100)/$deletedEntity\","
           + "\"id\":\"ESDelta(100)\",\"reason\":\"deleted\"},{"
           + "\"@odata.context\":\"#ESDelta(-32768)/$deletedEntity\","
           + "\"id\":\"ESDelta(-32768)\",\"reason\":\"changed\"}]"
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  

  @Test
  public void addedChangedDeltaEntity() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final Entity entity2 = data.readAll(edmEntitySet).getEntities().get(1);
    List<Entity> addedEntity = new ArrayList<Entity>();
    Entity changedEntity = new Entity();
    changedEntity.setId(entity2.getId());
    changedEntity.addProperty(entity2.getProperty("PropertyString"));
    addedEntity.add(entity);
    addedEntity.add(changedEntity);
    delta.getEntities().addAll(addedEntity);
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
           + "\"@odata.id\":\"ESDelta(32767)\",\"PropertyInt16\":32767,"
           + "\"PropertyString\":\"Number:32767\"},{\"@odata.id\":\"ESDelta(-32768)\","
           + "\"PropertyString\":\"Number:-32768\"}]"
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  @Test
  public void basicDeltaTest() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
    
    List<DeltaLink> deletedLinks = new ArrayList<DeltaLink>();
    DeltaLink delLink = new DeltaLink();
    delLink.setRelationship("NavPropertyETAllPrimOne");
    delLink.setSource(new URI("ESDelta(100)"));
    delLink.setTarget(new URI("ESAllPrim(0)"));
    deletedLinks.add(delLink );
    delta.getDeletedLinks().addAll(deletedLinks);
    
    List<DeletedEntity> deletedEntity = new ArrayList<DeletedEntity>();
    DeletedEntity delEntity1 = new DeletedEntity();
    delEntity1.setId(new URI("ESDelta(100)"));
    delEntity1.setReason(Reason.deleted);
    DeletedEntity delEntity2 = new DeletedEntity();
    delEntity2.setId(new URI("ESDelta(-32768)"));
    delEntity2.setReason(Reason.changed);    
    deletedEntity.add(delEntity1);
    deletedEntity.add(delEntity2);
    delta.getDeletedEntities().addAll(deletedEntity);
    
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final Entity entity2 = data.readAll(edmEntitySet).getEntities().get(1);
    List<Entity> addedEntity = new ArrayList<Entity>();
    Entity changedEntity = new Entity();
    changedEntity.setId(entity2.getId());
    changedEntity.addProperty(entity2.getProperty("PropertyString"));
    addedEntity.add(entity);
    addedEntity.add(changedEntity);
    delta.getEntities().addAll(addedEntity);
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
         +"\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
         + "\"@odata.id\":\"ESDelta(32767)\",\"PropertyInt16\":32767,"
         + "\"PropertyString\":\"Number:32767\"},{\"@odata.id\":\"ESDelta(-32768)\","
         + "\"PropertyString\":\"Number:-32768\"},{\"@odata.context\":\"#ESDelta(100)/$deletedEntity\","
         + "\"id\":\"ESDelta(100)\",\"reason\":\"deleted\"},"
         + "{\"@odata.context\":\"#ESDelta(-32768)/$deletedEntity\",\"id\":\"ESDelta(-32768)\","
         + "\"reason\":\"changed\"},{\"@odata.context\":\"#ESDelta/$link\",\"source\":\"ESDelta(100)\","
         + "\"relationship\":\"NavPropertyETAllPrimOne\",\"target\":\"ESAllPrim(0)\"},{\"@odata.context\":"
         + "\"#ESDelta/$deletedLink\",\"source\":\"ESDelta(100)\",\"relationship\":\"NavPropertyETAllPrimOne\","
         + "\"target\":\"ESAllPrim(0)\"}]"
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void addedDifferentdDeltaEntity() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmEntitySet edmEntitySet2 = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    List<Entity> addedEntity = new ArrayList<Entity>();
    addedEntity.add(entity);
    delta.getEntities().addAll(addedEntity);
     InputStream stream = ser.entityCollection(metadata, edmEntitySet2.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet2).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
         +"\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
         + "\"@odata.context\":\"#ESAllPrim/$entity\",\"@odata.id\":\"ESAllPrim(32767)\","
         + "\"PropertyInt16\":32767,\"PropertyString\":\"First Resource - positive values\"}]"
         + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void testDeltaToken() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
    delta.setDeltaLink(new URI("23042017"));
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
           + "\"@odata.context\":\"#ESDelta/$link\",\"source\":\"ESDelta(100)\","
           + "\"relationship\":\"NavPropertyETAllPrimOne\","
           + "\"target\":\"ESAllPrim(0)\"}],"          
           + "\"@odata.deltaLink\":\"23042017\""
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void testSkipToken() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
    delta.setNext(new URI("23042017"));
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
           + "\"@odata.context\":\"#ESDelta/$link\",\"source\":\"ESDelta(100)\","
           + "\"relationship\":\"NavPropertyETAllPrimOne\","
           + "\"target\":\"ESAllPrim(0)\"}],"          
           + "\"@odata.nextLink\":\"23042017\""
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void testSkipDeltaToken() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
    delta.setNext(new URI("23042017"));
    delta.setDeltaLink(new URI("02052017"));
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[{"
           + "\"@odata.context\":\"#ESDelta/$link\",\"source\":\"ESDelta(100)\","
           + "\"relationship\":\"NavPropertyETAllPrimOne\","
           + "\"target\":\"ESAllPrim(0)\"}],"          
           + "\"@odata.nextLink\":\"23042017\""
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void testDeltaCount() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    CountOption countOption = Mockito.mock(CountOption.class);
    Mockito.when(countOption.getValue()).thenReturn(true);
    Delta delta = new Delta();
    delta.setCount(1);
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
    delta.setDeltaLink(new URI("23042017"));
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .count(countOption)
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESDelta/$delta\","
           + "\"@odata.count\":\"1\","
           + "\"value\":[{"
           + "\"@odata.context\":\"#ESDelta/$link\",\"source\":\"ESDelta(100)\","
           + "\"relationship\":\"NavPropertyETAllPrimOne\","
           + "\"target\":\"ESAllPrim(0)\"}],"          
           + "\"@odata.deltaLink\":\"23042017\""
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  @Test
  public void testEmptyDelta() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":[]"
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     }
  
  @Test
  public void testDeltaForStream() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESWithStream");
    Delta delta = new Delta();
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESWithStream/$delta\","
           + "\"value\":[{\"@odata.context\":\"#ESWithStream/$link\","
           + "\"source\":\"ESDelta(100)\",\"relationship\":\"NavPropertyETAllPrimOne\","
           + "\"target\":\"ESAllPrim(0)\"}]"      
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void selectInDelta() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final UriHelper helper = odata.createUriHelper();
    final SelectOption select = ExpandSelectMock.mockSelectOption(Collections.singletonList(
        ExpandSelectMock.mockSelectItem(entityContainer.getEntitySet("ESAllPrim"), "PropertyString")));
    
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final Entity entity2 = data.readAll(edmEntitySet).getEntities().get(1);
       
       Delta delta = new Delta();
       List<Entity> addedEntity = new ArrayList<Entity>();
       Entity changedEntity = new Entity();
       changedEntity.setId(entity2.getId());
       changedEntity.addProperty(entity2.getProperty("PropertyString"));
       changedEntity.addProperty(entity2.getProperty("PropertyInt16"));
       addedEntity.add(entity);
       addedEntity.add(changedEntity);
       delta.getEntities().addAll(addedEntity);
       InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
           EntityCollectionSerializerOptions.with()
           .contextURL(ContextURL.with().entitySet(edmEntitySet)
               .selectList(helper.buildContextURLSelectList(entityType, null, select))
               .suffix(Suffix.ENTITY).build())
           .select(select).build()).getContent();
          String jsonString = IOUtils.toString(stream);
    Assert.assertEquals("{"
        +"\"@odata.context\":\"$metadata#ESDelta(PropertyInt16,PropertyString)/$entity/$delta\","
        + "\"value\":[{\"@odata.id\":\"ESDelta(32767)\",\"PropertyString\":\"Number:32767\"},"
        + "{\"@odata.id\":\"ESDelta(-32768)\",\"PropertyString\":\"Number:-32768\"}]}",
        jsonString);
  }
  
  @Test
  public void testCollPropertyInDelta() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCollAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    Delta delta = new Delta();
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    List<Entity> addedEntities = new ArrayList<Entity>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    addedEntities.add(entity);
    delta.getAddedLinks().addAll(addedLinks );
    delta.getEntities().addAll(addedEntities);
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
           + "\"@odata.context\":\"$metadata#ESCollAllPrim/$delta\","
           + "\"value\":[{\"@odata.id\":\"ESCollAllPrim(1)\",\"PropertyInt16\":1,"
           + "\"CollPropertyString\":[\"Employee1@company.example\",\"Employee2@company.example\","
           + "\"Employee3@company.example\"],\"CollPropertyBoolean\":[true,false,true],"
           + "\"CollPropertyByte\":[50,200,249],\"CollPropertySByte\":[-120,120,126],\"CollPropertyInt16\":"
           + "[1000,2000,30112],\"CollPropertyInt32\":[23232323,11223355,10000001],\"CollPropertyInt64\":"
           + "[929292929292,333333333333,444444444444],\"CollPropertySingle\":[1790.0,26600.0,3210.0],"
           + "\"CollPropertyDouble\":[-17900.0,-2.78E7,3210.0],\"CollPropertyDecimal\":"
           + "[12,-2,1234],\"CollPropertyBinary\":"
           + "[\"q83v\",\"ASNF\",\"VGeJ\"],\"CollPropertyDate\":[\"1958-12-03\",\"1999-08-05\",\"2013-06-25\"],"
           + "\"CollPropertyDateTimeOffset\":[\"2015-08-12T03:08:34Z\",\"1970-03-28T12:11:10Z\","
           + "\"1948-02-17T09:09:09Z\"],"
           + "\"CollPropertyDuration\":[\"PT13S\",\"PT5H28M0S\",\"PT1H0S\"],\"CollPropertyGuid\":"
           + "[\"ffffff67-89ab-cdef-0123-456789aaaaaa\",\"eeeeee67-89ab-cdef-0123-456789bbbbbb\","
           + "\"cccccc67-89ab-cdef-0123-456789cccccc\"],\"CollPropertyTimeOfDay\":[\"04:14:13\",\"23:59:59\","
           + "\"01:12:33\"]},{\"@odata.context\":\"#ESCollAllPrim/$link\",\"source\":\"ESDelta(100)\",\"relationship\":"
           + "\"NavPropertyETAllPrimOne\",\"target\":\"ESAllPrim(0)\"}]"
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void testComplexCollPropertyInDelta() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESKeyNav");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    Delta delta = new Delta();
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    List<Entity> addedEntities = new ArrayList<Entity>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    addedEntities.add(entity);
    delta.getAddedLinks().addAll(addedLinks );
    delta.getEntities().addAll(addedEntities);
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"
         +"\"@odata.context\":\"$metadata#ESKeyNav/$delta\","
         + "\"value\":[{\"@odata.id\":\"ESKeyNav(1)\",\"PropertyInt16\":1,"
         + "\"PropertyString\":\"I am String Property 1\",\"PropertyCompNav\":"
         + "{\"PropertyInt16\":1},\"PropertyCompAllPrim\":{\"PropertyString\":"
         + "\"First Resource - positive values\",\"PropertyBinary\":"
         + "\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,\"PropertyByte\":"
         + "255,\"PropertyDate\":\"2012-12-03\",\"PropertyDateTimeOffset\":"
         + "\"2012-12-03T07:16:23Z\",\"PropertyDecimal\":"
         + "34,\"PropertySingle\":1.79E20,\"PropertyDouble\":-1.79E20,\"PropertyDuration\":\"PT6S\",\"PropertyGuid\":"
         + "\"01234567-89ab-cdef-0123-456789abcdef\",\"PropertyInt16\":32767,"
         + "\"PropertyInt32\":2147483647,\"PropertyInt64\":"
         + "9223372036854775807,\"PropertySByte\":127,\"PropertyTimeOfDay\":\"21:05:59\"},\"PropertyCompTwoPrim\":"
         + "{\"PropertyInt16\":16,\"PropertyString\":\"Test123\"},\"CollPropertyString\":"
         + "[\"Employee1@company.example\","
         + "\"Employee2@company.example\",\"Employee3@company.example\"],\"CollPropertyInt16\":[1000,2000,30112],"
         + "\"CollPropertyComp\":[{\"PropertyInt16\":1,\"PropertyComp\":{\"PropertyString\":"
         + "\"First Resource - positive values\","
         + "\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,\"PropertyByte\":"
         + "255,\"PropertyDate\":\"2012-12-03\","
         + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\",\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,"
         + "\"PropertyDouble\":-1.79E20,\"PropertyDuration\":\"PT6S\",\"PropertyGuid\":"
         + "\"01234567-89ab-cdef-0123-456789abcdef\","
         + "\"PropertyInt16\":32767,\"PropertyInt32\":2147483647,\"PropertyInt64\":"
         + "9223372036854775807,\"PropertySByte\":127,"
         + "\"PropertyTimeOfDay\":\"21:05:59\"}},{\"PropertyInt16\":2,\"PropertyComp\":{\"PropertyString\":"
         + "\"First Resource - positive values\",\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,"
         + "\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\",\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
         + "\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,\"PropertyDouble\":-1.79E20,"
         + "\"PropertyDuration\":\"PT6S\","
         + "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
         + "\"PropertyInt16\":32767,\"PropertyInt32\":2147483647,"
         + "\"PropertyInt64\":9223372036854775807,\"PropertySByte\":127,"
         + "\"PropertyTimeOfDay\":\"21:05:59\"}},{\"PropertyInt16\":3,"
         + "\"PropertyComp\":{\"PropertyString\":\"First Resource - positive values\","
         + "\"PropertyBinary\":\"ASNFZ4mrze8=\","
         + "\"PropertyBoolean\":true,\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\",\"PropertyDateTimeOffset\":"
         + "\"2012-12-03T07:16:23Z\",\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,\"PropertyDouble\":-1.79E20,"
         + "\"PropertyDuration\":\"PT6S\",\"PropertyGuid\":"
         + "\"01234567-89ab-cdef-0123-456789abcdef\",\"PropertyInt16\":32767,"
         + "\"PropertyInt32\":2147483647,\"PropertyInt64\":9223372036854775807,"
         + "\"PropertySByte\":127,\"PropertyTimeOfDay\":"
         + "\"21:05:59\"}}],\"PropertyCompCompNav\":{\"PropertyString\":\"1\","
         + "\"PropertyCompNav\":{\"PropertyInt16\":1}}},"
         + "{\"@odata.context\":\"#ESKeyNav/$link\",\"source\":\"ESDelta(100)\","
         + "\"relationship\":\"NavPropertyETAllPrimOne\","
         + "\"target\":\"ESAllPrim(0)\"}]"
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     } 
  
  @Test
  public void navigationEntityInDeltaEntity() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final Entity entity2 = data.readAll(edmEntitySet).getEntities().get(3);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Collections.singletonList(
        ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETAllPrimOne")));
    List<Entity> addedEntity = new ArrayList<Entity>();
    Entity changedEntity = new Entity();
    changedEntity.setId(entity.getId());
    changedEntity.addProperty(entity.getProperty("PropertyString"));
    addedEntity.add(entity);
    addedEntity.add(entity2);
    delta.getEntities().addAll(addedEntity);
     InputStream stream = ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build()).expand(expand)
        .build()).getContent();
       String jsonString = IOUtils.toString(stream);
       final String expectedResult = "{"         
           +  "\"@odata.context\":\"$metadata#ESDelta/$delta\",\"value\":"
           + "[{\"@odata.id\":\"ESDelta(32767)\",\"PropertyInt16\":32767,\"PropertyString\":"
           + "\"Number:32767\"},{\"@odata.id\":\"ESDelta(100)\",\"PropertyInt16\":100,"
           + "\"PropertyString\":\"Number:100\"}]"
           + "}";
       Assert.assertNotNull(jsonString);
       Assert.assertEquals(expectedResult, jsonString);
     }
  
  @Test(expected = SerializerException.class)
  public void negativeDeltaEntityTest() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    
    final Entity entity2 = data.readAll(edmEntitySet).getEntities().get(1);
    List<Entity> addedEntity = new ArrayList<Entity>();
    Entity changedEntity = new Entity();
    changedEntity.addProperty(entity2.getProperty("PropertyString"));
    addedEntity.add(changedEntity);
    delta.getEntities().addAll(addedEntity);
     ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
      
     } 
  
  @Test(expected = SerializerException.class)
  public void negativeLinkDeltaTest1() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setSource(new URI("ESDelta(100)"));
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
       
    ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();      
     } 
  
  @Test(expected = SerializerException.class)
  public void negativeLinkDeltaTest2() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setTarget(new URI("ESAllPrim(0)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
       
    ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();      
     }
  @Test(expected = SerializerException.class)
  public void negativeLinkDeltaTest3() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = new DeltaLink();
    link1.setRelationship("NavPropertyETAllPrimOne");
    link1.setSource(new URI("ESDelta(100)"));
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
       
    ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();      
     }
  
  @Test(expected = SerializerException.class)
  public void negativeLinkDeltaTest4() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();
    
    List<DeltaLink> addedLinks = new ArrayList<DeltaLink>();
    DeltaLink link1 = null;
    addedLinks.add(link1 );
    delta.getAddedLinks().addAll(addedLinks );
       
    ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();      
     } 
  @Test(expected = SerializerException.class)
  public void negativeDeltaDeletedEntityTest1() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();   
      
    List<DeletedEntity> deletedEntity = new ArrayList<DeletedEntity>();
    DeletedEntity delEntity1 = new DeletedEntity();
    delEntity1.setReason(Reason.deleted);
   
    deletedEntity.add(delEntity1);
    delta.getDeletedEntities().addAll(deletedEntity);    
   
    ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
      
     } 
  
  @Test(expected = SerializerException.class)
  public void negativeDeltaDeletedEntityTest2() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESDelta");
    Delta delta = new Delta();   
      
    List<DeletedEntity> deletedEntity = new ArrayList<DeletedEntity>();
    DeletedEntity delEntity1 = new DeletedEntity();
    delEntity1.setId(new URI("ESDelta(100)"));
   
    deletedEntity.add(delEntity1);
    delta.getDeletedEntities().addAll(deletedEntity);    
   
    ser.entityCollection(metadata, edmEntitySet.getEntityType(), delta ,
        EntityCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
        .build()).getContent();
      
     } 
  
}
