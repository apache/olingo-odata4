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

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.tecsvc.MetadataETagSupport;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Assert;
import org.junit.Test;

public class EdmAssistedJsonSerializerTest {
  private static final OData oData = OData.newInstance();
  private static final ServiceMetadata metadata = oData.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList(), null);
  private static final EdmEntityContainer entityContainer = metadata.getEdm().getEntityContainer();
  private final EdmAssistedJsonSerializer serializer = new EdmAssistedJsonSerializer(ContentType.JSON);

  @Test
  public void entity() throws Exception {
    Entity entity = new Entity();
    entity.setId(null);
    entity.addProperty(new Property(null, "Property1", ValueType.PRIMITIVE, 1.25F));
    final String resultString = IOUtils.toString(
        serializer.entity(metadata, null, entity,
            ContextURL.with().entitySetOrSingletonOrType("EntitySet").selectList("Property1")
                .suffix(Suffix.ENTITY)
                .build())
            .getContent());
    final String expectedResult =
        "{\"@odata.context\":\"$metadata#EntitySet(Property1)/$entity\","
        + "\"@odata.id\":null,"
        + "\"Property1@odata.type\":\"Single\",\"Property1\":1.25"
        + "}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityWithEdm() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    Entity entity = new Entity();
    entity.setId(null);
    entity.addProperty(new Property(null, "PropertyInt16", ValueType.PRIMITIVE, (short) 1))
        .addProperty(new Property(null, "PropertyString", ValueType.PRIMITIVE, "test"))
        .addProperty(new Property(null, "AdditionalProperty", ValueType.PRIMITIVE, (byte) 42));
    final String resultString = IOUtils.toString(
        serializer.entity(metadata, entitySet.getEntityType(), entity,
            ContextURL.with().entitySet(entitySet).suffix(Suffix.ENTITY).build())
            .getContent());
    final String expectedResult =
        "{\"@odata.context\":\"$metadata#ESTwoPrim/$entity\","
        + "\"@odata.id\":null,"
        + "\"PropertyInt16\":1,\"PropertyString\":\"test\","
        + "\"AdditionalProperty@odata.type\":\"SByte\",\"AdditionalProperty\":42"
        + "}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityCollection() throws Exception {
    Entity entity = new Entity();
    entity.setId(null);
    entity.addProperty(new Property(null, "Property0", ValueType.PRIMITIVE, null))
        .addProperty(new Property(null, "Property1", ValueType.PRIMITIVE, 1));
    Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    date.clear();
    date.set(2000, 1, 29);
    entity.addProperty(new Property("Edm.Date", "Property2", ValueType.PRIMITIVE, date))
        .addProperty(new Property("Edm.DateTimeOffset", "Property3", ValueType.PRIMITIVE, date))
        .addProperty(new Property(null, "Property4", ValueType.COLLECTION_PRIMITIVE,
            Arrays.asList(true, false, null)));
    EntityCollection entityCollection = new EntityCollection();
    entityCollection.getEntities().add(entity);
    entityCollection.setCount(2);
    entityCollection.setNext(URI.create("nextLink"));
    final String resultString = IOUtils.toString(
        serializer.entityCollection(metadata, null, entityCollection,
            ContextURL.with().entitySetOrSingletonOrType("EntitySet")
                .selectList("Property0,Property1,Property2,Property3,Property4")
                .build())
            .getContent());
    final String expectedResult =
        "{\"@odata.context\":\"$metadata#EntitySet(Property0,Property1,Property2,Property3,Property4)\","
        + "\"@odata.count\":2,"
        + "\"value\":[{\"@odata.id\":null,"
        + "\"Property0\":null,"
        + "\"Property1@odata.type\":\"Int32\",\"Property1\":1,"
        + "\"Property2@odata.type\":\"Date\",\"Property2\":\"2000-02-29\","
        + "\"Property3@odata.type\":\"DateTimeOffset\",\"Property3\":\"2000-02-29T00:00:00Z\","
        + "\"Property4@odata.type\":\"#Collection(Boolean)\",\"Property4\":[true,false,null]"
        + "}],"
        + "\"@odata.nextLink\":\"nextLink\""
        + "}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityCollectionIEEE754Compatible() throws Exception {
    EntityCollection entityCollection = new EntityCollection();
    entityCollection.getEntities().add(new Entity()
        .addProperty(new Property(null, "Property1", ValueType.PRIMITIVE, Long.MIN_VALUE))
        .addProperty(new Property(null, "Property2", ValueType.PRIMITIVE, BigDecimal.valueOf(Long.MAX_VALUE, 10)))
        .addProperty(new Property("Edm.Byte", "Property3", ValueType.PRIMITIVE, 20)));
    entityCollection.setCount(3);
    Assert.assertEquals(
        "{\"@odata.context\":\"$metadata#EntitySet(Property1,Property2,Property3)\","
            + "\"@odata.count\":\"3\","
            + "\"value\":[{\"@odata.id\":null,"
            + "\"Property1@odata.type\":\"Int64\",\"Property1\":\"-9223372036854775808\","
            + "\"Property2@odata.type\":\"Decimal\",\"Property2\":\"922337203.6854775807\","
            + "\"Property3@odata.type\":\"Byte\",\"Property3\":20}]}",
        IOUtils.toString(
            new EdmAssistedJsonSerializer(
                ContentType.create(ContentType.JSON, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true"))
                .entityCollection(metadata, null, entityCollection,
                    ContextURL.with().entitySetOrSingletonOrType("EntitySet")
                        .selectList("Property1,Property2,Property3")
                        .build())
                .getContent()));
  }

  @Test
  public void entityCollectionWithComplexProperty() throws Exception {
    Entity entity = new Entity();
    entity.setId(null);
    entity.addProperty(new Property(null, "Property1", ValueType.PRIMITIVE, 1L));
    ComplexValue complexValue = new ComplexValue();
    complexValue.getValue().add(new Property(null, "Inner1", ValueType.PRIMITIVE,
        BigDecimal.TEN.scaleByPowerOfTen(-5)));
    Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    time.clear();
    time.set(Calendar.HOUR_OF_DAY, 13);
    time.set(Calendar.SECOND, 59);
    time.set(Calendar.MILLISECOND, 999);
    complexValue.getValue().add(new Property("Edm.TimeOfDay", "Inner2", ValueType.PRIMITIVE, time));
    entity.addProperty(new Property("Namespace.ComplexType", "Property2", ValueType.COMPLEX, complexValue));
    EntityCollection entityCollection = new EntityCollection();
    entityCollection.getEntities().add(entity);
    final String resultString = IOUtils.toString(
        serializer.entityCollection(metadata, null, entityCollection,
            ContextURL.with().entitySetOrSingletonOrType("EntitySet").selectList("Property1,Property2").build())
            .getContent());
    final String expectedResult = "{\"@odata.context\":\"$metadata#EntitySet(Property1,Property2)\","
        + "\"value\":[{\"@odata.id\":null,"
        + "\"Property1@odata.type\":\"Int64\",\"Property1\":1,"
        + "\"Property2\":{"
        + "\"@odata.type\":\"#Namespace.ComplexType\","
        + "\"Inner1@odata.type\":\"Decimal\",\"Inner1\":0.00010,"
        + "\"Inner2@odata.type\":\"TimeOfDay\",\"Inner2\":\"13:00:59.999\""
        + "}}]}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityWithComplexCollection() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    ComplexValue complexValue1 = new ComplexValue();
    complexValue1.getValue().add(new Property(null, "PropertyInt16", ValueType.PRIMITIVE, 1));
    complexValue1.getValue().add(new Property(null, "PropertyString", ValueType.PRIMITIVE, "one"));
    ComplexValue complexValue2 = new ComplexValue();
    complexValue2.getValue().add(new Property(null, "PropertyInt16", ValueType.PRIMITIVE, 2));
    complexValue2.getValue().add(new Property(null, "PropertyString", ValueType.PRIMITIVE, "two"));
    ComplexValue complexValue3 = new ComplexValue();
    complexValue3.getValue().add(new Property(null, "PropertyInt16", ValueType.PRIMITIVE, 3));
    complexValue3.getValue().add(new Property(null, "PropertyString", ValueType.PRIMITIVE, "three"));
    final Entity entity = new Entity()
        .addProperty(new Property(null, "CollPropertyComp", ValueType.COLLECTION_COMPLEX,
            Arrays.asList(complexValue1, complexValue2, complexValue3)));
    final String resultString = IOUtils.toString(
        serializer.entity(metadata, entitySet.getEntityType(), entity,
            ContextURL.with().entitySet(entitySet).selectList("CollPropertyComp").build())
            .getContent());
    final String expectedResult = "{\"@odata.context\":\"$metadata#ESMixPrimCollComp(CollPropertyComp)\","
        + "\"@odata.id\":null,"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":1,\"PropertyString\":\"one\"},"
        + "{\"PropertyInt16\":2,\"PropertyString\":\"two\"},"
        + "{\"PropertyInt16\":3,\"PropertyString\":\"three\"}]}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityWithEmptyCollection() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final Entity entity = new Entity()
        .addProperty(new Property(null, "CollPropertyString", ValueType.COLLECTION_PRIMITIVE,
            Collections.emptyList()));
    Assert.assertEquals(
        "{\"@odata.context\":\"$metadata#ESMixPrimCollComp(CollPropertyString)\","
            + "\"@odata.id\":null,\"CollPropertyString\":[]}",
        IOUtils.toString(
            serializer.entity(metadata, entitySet.getEntityType(), entity,
                ContextURL.with().entitySet(entitySet).selectList("CollPropertyString").build())
                .getContent()));
  }

  @Test
  public void expand() throws Exception {
    final Entity relatedEntity1 = new Entity().addProperty(new Property(null, "Related1", ValueType.PRIMITIVE, 1.5));
    final Entity relatedEntity2 = new Entity().addProperty(new Property(null, "Related1", ValueType.PRIMITIVE, 2.75));
    EntityCollection target = new EntityCollection();
    target.getEntities().add(relatedEntity1);
    target.getEntities().add(relatedEntity2);
    Link link = new Link();
    link.setTitle("NavigationProperty");
    link.setInlineEntitySet(target);
    Entity entity = new Entity();
    entity.setId(null);
    entity.addProperty(new Property(null, "Property1", ValueType.PRIMITIVE, (short) 1));
    entity.getNavigationLinks().add(link);
    EntityCollection entityCollection = new EntityCollection();
    entityCollection.getEntities().add(entity);
    final String resultString = IOUtils.toString(
        serializer.entityCollection(metadata, null, entityCollection,
            ContextURL.with().entitySetOrSingletonOrType("EntitySet")
                .selectList("Property1,NavigationProperty(Related1)").build())
            .getContent());
    final String expectedResult =
        "{\"@odata.context\":\"$metadata#EntitySet(Property1,NavigationProperty(Related1))\","
        + "\"value\":[{\"@odata.id\":null,"
        + "\"Property1@odata.type\":\"Int16\",\"Property1\":1,"
        + "\"NavigationProperty\":["
        + "{\"@odata.id\":null,\"Related1@odata.type\":\"Double\",\"Related1\":1.5},"
        + "{\"@odata.id\":null,\"Related1@odata.type\":\"Double\",\"Related1\":2.75}"
        + "]}]}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void expandWithEdm() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    Entity entity = new Entity()
        .addProperty(new Property(null, "PropertyInt16", ValueType.PRIMITIVE, (short) 42))
        .addProperty(new Property(null, "PropertyString", ValueType.PRIMITIVE, "test"));
    final Entity target = new Entity()
        .addProperty(new Property(null, "PropertyInt16", ValueType.PRIMITIVE, (short) 2))
        .addProperty(new Property(null, "PropertyByte", ValueType.PRIMITIVE, 3L));
    Link link = new Link();
    link.setTitle("NavPropertyETAllPrimOne");
    link.setInlineEntity(target);
    entity.getNavigationLinks().add(link);
    final String resultString = IOUtils.toString(
        serializer.entity(metadata, entitySet.getEntityType(), entity,
            ContextURL.with().entitySet(entitySet).suffix(Suffix.ENTITY).build())
            .getContent());
    final String expectedResult =
        "{\"@odata.context\":\"$metadata#ESTwoPrim/$entity\","
        + "\"@odata.id\":null,"
        + "\"PropertyInt16\":42,\"PropertyString\":\"test\","
        + "\"NavPropertyETAllPrimOne\":{\"@odata.id\":null,\"PropertyInt16\":2,\"PropertyByte\":3}"
        + "}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void metadata() throws Exception {
    final ServiceMetadata metadata = oData.createServiceMetadata(null, Collections.<EdmxReference> emptyList(),
        new MetadataETagSupport("W/\"42\""));
    Entity entity = new Entity();
    entity.setType("Namespace.EntityType");
    entity.setId(URI.create("ID"));
    entity.setETag("W/\"1000\"");
    Link link = new Link();
    link.setHref("editLink");
    entity.setEditLink(link);
    entity.setMediaContentSource(URI.create("media"));
    entity.addProperty(new Property(null, "Property1", ValueType.PRIMITIVE,
        UUID.fromString("12345678-ABCD-1234-CDEF-123456789012")));
    final ContextURL contextURL = ContextURL.with().entitySetOrSingletonOrType("EntitySet").selectList("Property1")
        .suffix(Suffix.ENTITY).build();
    final String resultString = IOUtils.toString(serializer.entity(metadata, null, entity, contextURL).getContent());
    final String expectedResult =
        "{\"@odata.context\":\"$metadata#EntitySet(Property1)/$entity\","
        + "\"@odata.metadataEtag\":\"W/\\\"42\\\"\","
        + "\"@odata.etag\":\"W/\\\"1000\\\"\","
        + "\"@odata.type\":\"#Namespace.EntityType\","
        + "\"@odata.id\":\"ID\","
        + "\"Property1@odata.type\":\"Guid\",\"Property1\":\"12345678-abcd-1234-cdef-123456789012\","
        + "\"@odata.editLink\":\"editLink\","
        + "\"@odata.mediaReadLink\":\"editLink/$value\""
        + "}";
    Assert.assertEquals(expectedResult, resultString);

    Assert.assertEquals("{\"Property1\":\"12345678-abcd-1234-cdef-123456789012\"}",
        IOUtils.toString(new EdmAssistedJsonSerializer(ContentType.JSON_NO_METADATA)
            .entity(metadata, null, entity, contextURL).getContent()));
  }

  @Test(expected = SerializerException.class)
  public void enumType() throws Exception {
    serializer.entity(metadata, null,
        new Entity().addProperty(new Property(null, "Property1", ValueType.ENUM, 42)), null);
  }

  @Test(expected = SerializerException.class)
  public void collectionEnumType() throws Exception {
    serializer.entity(metadata, null,
        new Entity().addProperty(new Property(null, "Property1", ValueType.COLLECTION_ENUM, Arrays.asList(42))),
        null);
  }

  @Test(expected = SerializerException.class)
  public void geoType() throws Exception {
    serializer.entity(metadata, null,
        new Entity().addProperty(new Property(null, "Property1", ValueType.GEOSPATIAL, 1)), null);
  }

  @Test(expected = SerializerException.class)
  public void unsupportedType() throws Exception {
    serializer.entity(metadata, null,
        new Entity().addProperty(new Property(null, "Property1", ValueType.PRIMITIVE, TimeZone.getDefault())),
        null);
  }

  @Test(expected = SerializerException.class)
  public void wrongValueForType() throws Exception {
    serializer.entity(metadata, null,
        new Entity().addProperty(new Property("Edm.SByte", "Property1", ValueType.PRIMITIVE, "-1")),
        null);
  }

  @Test(expected = SerializerException.class)
  public void wrongValueForPropertyFacet() throws Exception {
    serializer.entity(metadata, entityContainer.getEntitySet("ESAllPrim").getEntityType(),
        new Entity().addProperty(
            new Property(null, "PropertyDecimal", ValueType.PRIMITIVE, BigDecimal.ONE.scaleByPowerOfTen(-11))),
        null);
  }

  @Test(expected = SerializerException.class)
  public void wrongValueForPropertyFacetInComplexProperty() throws Exception {
    ComplexValue innerComplexValue = new ComplexValue();
    innerComplexValue.getValue().add(new Property(null, "PropertyDecimal", ValueType.PRIMITIVE,
        BigDecimal.ONE.scaleByPowerOfTen(-6)));
    ComplexValue complexValue = new ComplexValue();
    complexValue.getValue().add(new Property(null, "PropertyComp", ValueType.COMPLEX,
        innerComplexValue));
    serializer.entity(metadata, entityContainer.getEntitySet("ESKeyNav").getEntityType(),
        new Entity().addProperty(
            new Property(null, "CollPropertyComp", ValueType.COLLECTION_COMPLEX,
                Collections.singletonList(complexValue))),
        null);
  }
}
