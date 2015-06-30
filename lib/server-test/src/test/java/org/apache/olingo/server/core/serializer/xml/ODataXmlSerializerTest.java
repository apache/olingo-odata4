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

import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.ServiceMetadataImpl;
import org.apache.olingo.server.core.serializer.ExpandSelectMock;
import org.apache.olingo.server.core.serializer.json.ODataJsonSerializer;
import org.apache.olingo.server.core.uri.UriHelperImpl;
import org.apache.olingo.server.tecsvc.MetadataETagSupport;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class ODataXmlSerializerTest  {
  private static final ServiceMetadata metadata = new ServiceMetadataImpl(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList(), new MetadataETagSupport("WmetadataETag"));
  private static final EdmEntityContainer entityContainer = metadata.getEdm().getEntityContainer();
  private final DataProvider data = new DataProvider(metadata.getEdm());
  private final ODataSerializer serializer = new ODataXmlSerializer();
  private final UriHelper helper = new UriHelperImpl();

  @BeforeClass
  public static void setup() {
    XMLUnit.setIgnoreComments(true);
    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalizeWhitespace(true);
    XMLUnit.setCompareUnmatched(false);
  }
  
  @Test
  public void entitySimple() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent();
    final String resultString = IOUtils.toString(result);
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:context=\"$metadata#ESAllPrim/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESAllPrim(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESAllPrim(32767)\" />\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "    href=\"ESTwoPrim(32767)\" />\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "    href=\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\" />\n" +         
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "      <d:PropertyString>First Resource - positive values</d:PropertyString>\n" + 
        "      <d:PropertyBoolean m:type=\"Boolean\">true</d:PropertyBoolean>\n" + 
        "      <d:PropertyByte m:type=\"Byte\">255</d:PropertyByte>\n" + 
        "      <d:PropertySByte m:type=\"SByte\">127</d:PropertySByte>\n" + 
        "      <d:PropertyInt32 m:type=\"Int32\">2147483647</d:PropertyInt32>\n" + 
        "      <d:PropertyInt64 m:type=\"Int64\">9223372036854775807\n" + 
        "      </d:PropertyInt64>\n" + 
        "      <d:PropertySingle m:type=\"Single\">1.79E20</d:PropertySingle>\n" + 
        "      <d:PropertyDouble m:type=\"Double\">-1.79E19</d:PropertyDouble>\n" + 
        "      <d:PropertyDecimal m:type=\"Decimal\">34</d:PropertyDecimal>\n" + 
        "      <d:PropertyBinary m:type=\"Binary\">ASNFZ4mrze8=\n" + 
        "      </d:PropertyBinary>\n" + 
        "      <d:PropertyDate m:type=\"Date\">2012-12-03</d:PropertyDate>\n" + 
        "      <d:PropertyDateTimeOffset m:type=\"DateTimeOffset\">2012-12-03T07:16:23Z\n" + 
        "      </d:PropertyDateTimeOffset>\n" + 
        "      <d:PropertyDuration m:type=\"Duration\">PT6S\n" + 
        "      </d:PropertyDuration>\n" + 
        "      <d:PropertyGuid m:type=\"Guid\">01234567-89ab-cdef-0123-456789abcdef\n" + 
        "      </d:PropertyGuid>\n" + 
        "      <d:PropertyTimeOfDay m:type=\"TimeOfDay\">03:26:05\n" + 
        "      </d:PropertyTimeOfDay>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void entityAllPrimAllNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.getProperties().retainAll(Arrays.asList(entity.getProperties().get(0)));
    final String resultString = IOUtils.toString(serializer.entity(metadata, edmEntitySet.getEntityType(),
        entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "+
        "m:context=\"$metadata#ESAllPrim/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESAllPrim(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
              .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESAllPrim(32767)\"/>\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "    href=\"ESTwoPrim(32767)\" />\n" +
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "    href=\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\" />\n" +         
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "      <d:PropertyString m:null=\"true\" />\n" + 
        "      <d:PropertyBoolean m:null=\"true\" />\n" + 
        "      <d:PropertyByte m:null=\"true\" />\n" + 
        "      <d:PropertySByte m:null=\"true\" />\n" + 
        "      <d:PropertyInt32 m:null=\"true\" />\n" + 
        "      <d:PropertyInt64 m:null=\"true\" />\n" + 
        "      <d:PropertySingle m:null=\"true\" />\n" + 
        "      <d:PropertyDouble m:null=\"true\" />\n" + 
        "      <d:PropertyDecimal m:null=\"true\" />\n" + 
        "      <d:PropertyBinary m:null=\"true\" />\n" + 
        "      <d:PropertyDate m:null=\"true\" />\n" + 
        "      <d:PropertyDateTimeOffset\n" + 
        "        m:null=\"true\" />\n" + 
        "      <d:PropertyDuration m:null=\"true\" />\n" + 
        "      <d:PropertyGuid m:null=\"true\" />\n" + 
        "      <d:PropertyTimeOfDay m:null=\"true\" />\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>\n" + 
        "";    
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test(expected = SerializerException.class)
  public void entityAllPrimKeyNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.getProperties().clear();
    serializer.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build());
  }

  @Test
  public void entityWrongData() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.getProperties().get(0).setValue(ValueType.PRIMITIVE, false);
    try {
      serializer.entity(metadata, edmEntitySet.getEntityType(), entity,
          EntitySerializerOptions.with()
              .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
              .build());
      Assert.fail("Expected exception not thrown!");
    } catch (final SerializerException e) {
      Assert.assertEquals(SerializerException.MessageKeys.WRONG_PROPERTY_VALUE, e.getMessageKey());
      final String message = e.getLocalizedMessage();
      Assert.assertThat(message, CoreMatchers.containsString("PropertyInt16"));
      Assert.assertThat(message, CoreMatchers.containsString("false"));
    }
  }

  @Test
  public void entitySetCompAllPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompAllPrim");
    EntityCollection entitySet = data.readAll(edmEntitySet);
    entitySet.setCount(entitySet.getEntities().size());
    entitySet.setNext(URI.create("/next"));
    CountOption countOption = Mockito.mock(CountOption.class);
    Mockito.when(countOption.getValue()).thenReturn(true);
    InputStream result = serializer.entityCollection(metadata, edmEntitySet.getEntityType(), entitySet,
        EntityCollectionSerializerOptions.with()
            .contextURL(ContextURL.with().serviceRoot(new URI("http://host:port"))
            .entitySet(edmEntitySet).build())
            .setId("http://host/svc/ESCompAllPrim")
            .count(countOption)
            .build()).getContent();
    final String resultString = IOUtils.toString(result);
    String prefix = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<a:feed xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:context=\"http://host:port$metadata#ESCompAllPrim\" "
        + "m:metadata-etag=\"WmetadataETag\">"
        + "<a:id>http://host/svc/ESCompAllPrim</a:id>"
        + "<m:count>4</m:count>"
        + "<a:link rel=\"next\" href=\"/next\"/>"
        + "<a:entry m:etag=\"W/&quot;32767&quot;\">"
        + "<a:id>ESCompAllPrim(32767)</a:id><a:title/><a:summary/>";
    Assert.assertTrue(resultString.startsWith(prefix));
  }

  @Test
  public void entityCollAllPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCollAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().serviceRoot(URI.create("http://host/service/"))
                .entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent();
    final String resultString = IOUtils.toString(result);
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:context=\"http://host/service/$metadata#ESCollAllPrim/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESCollAllPrim(1)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "<a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESCollAllPrim(1)\" />\n" + 
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETCollAllPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">1</d:PropertyInt16>\n" + 
        "      <d:CollPropertyString m:type=\"#Collection(String)\">\n" + 
        "        <m:element>Employee1@company.example</m:element>\n" + 
        "        <m:element>Employee2@company.example</m:element>\n" + 
        "        <m:element>Employee3@company.example</m:element>\n" + 
        "      </d:CollPropertyString>\n" + 
        "      <d:CollPropertyBoolean m:type=\"#Collection(Boolean)\">\n" + 
        "        <m:element>true</m:element>\n" + 
        "        <m:element>false</m:element>\n" + 
        "        <m:element>true</m:element>\n" + 
        "      </d:CollPropertyBoolean>\n" + 
        "      <d:CollPropertyByte m:type=\"#Collection(Byte)\">\n" + 
        "        <m:element>50</m:element>\n" + 
        "        <m:element>200</m:element>\n" + 
        "        <m:element>249</m:element>\n" + 
        "      </d:CollPropertyByte>\n" + 
        "      <d:CollPropertySByte m:type=\"#Collection(SByte)\">\n" + 
        "        <m:element>-120</m:element>\n" + 
        "        <m:element>120</m:element>\n" + 
        "        <m:element>126</m:element>\n" + 
        "      </d:CollPropertySByte>\n" + 
        "      <d:CollPropertyInt16 m:type=\"#Collection(Int16)\">\n" + 
        "        <m:element>1000</m:element>\n" + 
        "        <m:element>2000</m:element>\n" + 
        "        <m:element>30112</m:element>\n" + 
        "      </d:CollPropertyInt16>\n" + 
        "      <d:CollPropertyInt32 m:type=\"#Collection(Int32)\">\n" + 
        "        <m:element>23232323</m:element>\n" + 
        "        <m:element>11223355</m:element>\n" + 
        "        <m:element>10000001</m:element>\n" + 
        "      </d:CollPropertyInt32>\n" + 
        "      <d:CollPropertyInt64 m:type=\"#Collection(Int64)\">\n" + 
        "        <m:element>929292929292</m:element>\n" + 
        "        <m:element>333333333333</m:element>\n" + 
        "        <m:element>444444444444</m:element>\n" + 
        "      </d:CollPropertyInt64>\n" + 
        "      <d:CollPropertySingle m:type=\"#Collection(Single)\">\n" + 
        "        <m:element>1790.0</m:element>\n" + 
        "        <m:element>26600.0</m:element>\n" + 
        "        <m:element>3210.0</m:element>\n" + 
        "      </d:CollPropertySingle>\n" + 
        "      <d:CollPropertyDouble m:type=\"#Collection(Double)\">\n" + 
        "        <m:element>-17900.0</m:element>\n" + 
        "        <m:element>-2.78E7</m:element>\n" + 
        "        <m:element>3210.0</m:element>\n" + 
        "      </d:CollPropertyDouble>\n" + 
        "      <d:CollPropertyDecimal m:type=\"#Collection(Decimal)\">\n" + 
        "        <m:element>12</m:element>\n" + 
        "        <m:element>-2</m:element>\n" + 
        "        <m:element>1234</m:element>\n" + 
        "      </d:CollPropertyDecimal>\n" + 
        "      <d:CollPropertyBinary m:type=\"#Collection(Binary)\">\n" + 
        "        <m:element>q83v</m:element>\n" + 
        "        <m:element>ASNF</m:element>\n" + 
        "        <m:element>VGeJ</m:element>\n" + 
        "      </d:CollPropertyBinary>\n" + 
        "      <d:CollPropertyDate m:type=\"#Collection(Date)\">\n" + 
        "        <m:element>1958-12-03</m:element>\n" + 
        "        <m:element>1999-08-05</m:element>\n" + 
        "        <m:element>2013-06-25</m:element>\n" + 
        "      </d:CollPropertyDate>\n" + 
        "      <d:CollPropertyDateTimeOffset m:type=\"#Collection(DateTimeOffset)\">\n" + 
        "        <m:element>2015-08-12T03:08:34Z</m:element>\n" + 
        "        <m:element>1970-03-28T12:11:10Z</m:element>\n" + 
        "        <m:element>1948-02-17T09:09:09Z</m:element>\n" + 
        "      </d:CollPropertyDateTimeOffset>\n" + 
        "      <d:CollPropertyDuration m:type=\"#Collection(Duration)\">\n" + 
        "        <m:element>PT13S</m:element>\n" + 
        "        <m:element>PT5H28M0S</m:element>\n" + 
        "        <m:element>PT1H0S</m:element>\n" + 
        "      </d:CollPropertyDuration>\n" + 
        "      <d:CollPropertyGuid m:type=\"#Collection(Guid)\">\n" + 
        "        <m:element>ffffff67-89ab-cdef-0123-456789aaaaaa</m:element>\n" + 
        "        <m:element>eeeeee67-89ab-cdef-0123-456789bbbbbb</m:element>\n" + 
        "        <m:element>cccccc67-89ab-cdef-0123-456789cccccc</m:element>\n" + 
        "      </d:CollPropertyGuid>\n" + 
        "      <d:CollPropertyTimeOfDay m:type=\"#Collection(TimeOfDay)\">\n" + 
        "        <m:element>04:14:13</m:element>\n" + 
        "        <m:element>23:59:59</m:element>\n" + 
        "        <m:element>01:12:33</m:element>\n" + 
        "      </d:CollPropertyTimeOfDay>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void entityCompAllPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent();
    
    final String resultString = IOUtils.toString(result);
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:context=\"$metadata#ESCompAllPrim/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\" m:etag=\"W/&quot;32767&quot;\">\n" + 
        "  <a:id>ESCompAllPrim(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "<a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESCompAllPrim(32767)\" />\n" + 
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETCompAllPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "      <d:PropertyComp m:type=\"#olingo.odata.test1.CTAllPrim\">\n" + 
        "        <d:PropertyString>First Resource - first</d:PropertyString>\n" + 
        "        <d:PropertyBinary m:type=\"Binary\">ASNFZ4mrze8=</d:PropertyBinary>\n" + 
        "        <d:PropertyBoolean m:type=\"Boolean\">true</d:PropertyBoolean>\n" + 
        "        <d:PropertyByte m:type=\"Byte\">255</d:PropertyByte>\n" + 
        "        <d:PropertyDate m:type=\"Date\">2012-10-03</d:PropertyDate>\n" + 
        "        <d:PropertyDateTimeOffset m:type=\"DateTimeOffset\">2012-10-03T07:16:23.1234567Z"
        + "</d:PropertyDateTimeOffset>\n" + 
        "        <d:PropertyDecimal m:type=\"Decimal\">34.27</d:PropertyDecimal>\n" + 
        "        <d:PropertySingle m:type=\"Single\">1.79E20</d:PropertySingle>\n" + 
        "        <d:PropertyDouble m:type=\"Double\">-1.79E19</d:PropertyDouble>\n" + 
        "        <d:PropertyDuration m:type=\"Duration\">PT6S</d:PropertyDuration>\n" + 
        "        <d:PropertyGuid m:type=\"Guid\">01234567-89ab-cdef-0123-456789abcdef</d:PropertyGuid>\n" + 
        "        <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "        <d:PropertyInt32 m:type=\"Int32\">2147483647</d:PropertyInt32>\n" + 
        "        <d:PropertyInt64 m:type=\"Int64\">9223372036854775807</d:PropertyInt64>\n" + 
        "        <d:PropertySByte m:type=\"SByte\">127</d:PropertySByte>\n" + 
        "        <d:PropertyTimeOfDay m:type=\"TimeOfDay\">01:00:01</d:PropertyTimeOfDay>\n" + 
        "      </d:PropertyComp>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void entityMixPrimCollComp() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent();
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" \n" + 
        "  m:context=\"$metadata#ESMixPrimCollComp/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESMixPrimCollComp(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
          .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESMixPrimCollComp(32767)\"/>\n" + 
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETMixPrimCollComp\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "      <d:CollPropertyString m:type=\"#Collection(String)\">\n" + 
        "        <m:element>Employee1@company.example</m:element>\n" + 
        "        <m:element>Employee2@company.example</m:element>\n" + 
        "        <m:element>Employee3@company.example</m:element>\n" + 
        "      </d:CollPropertyString>\n" + 
        "      <d:PropertyComp m:type=\"#olingo.odata.test1.CTTwoPrim\">\n" + 
        "        <d:PropertyInt16 m:type=\"Int16\">111</d:PropertyInt16>\n" + 
        "        <d:PropertyString>TEST A</d:PropertyString>\n" + 
        "      </d:PropertyComp>\n" + 
        "        <d:CollPropertyComp m:type=\"#Collection(olingo.odata.test1.CTTwoPrim)\">\n" + 
        "          <m:element>\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">123</d:PropertyInt16>\n" + 
        "            <d:PropertyString>TEST 1</d:PropertyString>\n" + 
        "          </m:element>\n" + 
        "          <m:element>\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">456</d:PropertyInt16>\n" + 
        "            <d:PropertyString>TEST 2</d:PropertyString>\n" + 
        "          </m:element>\n" + 
        "          <m:element>\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">789</d:PropertyInt16>\n" + 
        "            <d:PropertyString>TEST 3</d:PropertyString>\n" + 
        "          </m:element>\n" + 
        "        </d:CollPropertyComp>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>\n"; 
    XMLAssert.assertXMLEqual(expectedResult, resultString);
  }

  @Test
  public void entityMixPrimCollCompAllNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.getProperties().retainAll(Arrays.asList(entity.getProperties().get(0)));
    final String resultString = IOUtils.toString(serializer.entity(metadata, edmEntitySet.getEntityType(), 
        entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent());
    final String expectedResult = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:context=\"$metadata#ESMixPrimCollComp/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESMixPrimCollComp(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESMixPrimCollComp(32767)\"/>\n" + 
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETMixPrimCollComp\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "      <d:CollPropertyString m:null=\"true\" />\n" + 
        "      <d:PropertyComp m:null=\"true\" />\n" + 
        "      <d:CollPropertyComp m:null=\"true\" />\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expectedResult, resultString);
  }

  @Test
  public void entityTwoPrimNoMetadata() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = new ODataJsonSerializer(ContentType.JSON_NO_METADATA)
        .entity(metadata, edmEntitySet.getEntityType(), entity, null).getContent();
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entitySetTwoPrimNoMetadata() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EntityCollection entitySet = data.readAll(edmEntitySet);
    InputStream result = new ODataJsonSerializer(ContentType.JSON_NO_METADATA)
        .entityCollection(metadata, edmEntitySet.getEntityType(), entitySet,
            EntityCollectionSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet).build()).build()).getContent();
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{\"value\":["
        + "{\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"},"
        + "{\"PropertyInt16\":-365,\"PropertyString\":\"Test String2\"},"
        + "{\"PropertyInt16\":-32766,\"PropertyString\":null},"
        + "{\"PropertyInt16\":32767,\"PropertyString\":\"Test String4\"}]}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityMedia() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMedia");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final String resultString = IOUtils.toString(serializer.entity(metadata, edmEntitySet.getEntityType(),
        entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent());
    final String expectedResult = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" " +
        "  m:context=\"$metadata#ESMedia/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESMedia(1)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
              .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESMedia(1)\"/>\n" + 
        "  <a:content type=\"image/svg+xml\" src=\"ESMedia(1)/$value\" />\n" +
        "  <a:link rel=\"edit-media\" title=\"ESMedia\" href=\"ESMedia(1)/$value\"/>\n" +
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETMedia\" />\n" + 
        "  <m:properties>\n" + 
        "    <d:PropertyInt16 m:type=\"Int16\">1</d:PropertyInt16>\n" + 
        "  </m:properties>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expectedResult, resultString);
  }

  @Test
  public void entitySetMedia() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMedia");
    final EntityCollection entitySet = data.readAll(edmEntitySet);
    final String resultString = IOUtils.toString(serializer.entityCollection(metadata,
        edmEntitySet.getEntityType(), entitySet,
        EntityCollectionSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
            .setId("http://host/svc/ESMedia")
            .build()).getContent());
    
    final String expectedResult = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:feed xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" m:context=\"$metadata#ESMedia\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>http://host/svc/ESMedia</a:id>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESMedia(1)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
              .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESMedia(1)\"/>\n" + 
        "    <a:content type=\"image/svg+xml\" src=\"ESMedia(1)/$value\" />\n" +
        "    <a:link rel=\"edit-media\" title=\"ESMedia\" href=\"ESMedia(1)/$value\"/>\n" +
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETMedia\" />\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">1</d:PropertyInt16>\n" + 
        "    </m:properties>\n" + 
        "  </a:entry>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESMedia(2)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
              .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESMedia(2)\"/>\n" + 
        "    <a:content type=\"image/svg+xml\" src=\"ESMedia(2)/$value\" />\n" + 
        "    <a:link rel=\"edit-media\" title=\"ESMedia\" href=\"ESMedia(2)/$value\"/>\n" +
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETMedia\" />\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">2</d:PropertyInt16>\n" + 
        "    </m:properties>\n" + 
        "  </a:entry>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESMedia(3)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
              .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESMedia(3)\"/>\n" + 
        "    <a:content type=\"image/svg+xml\" src=\"ESMedia(3)/$value\" />\n" + 
        "    <a:link rel=\"edit-media\" title=\"ESMedia\" href=\"ESMedia(3)/$value\"/>\n" +
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETMedia\" />\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">3</d:PropertyInt16>\n" + 
        "    </m:properties>\n" + 
        "  </a:entry>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESMedia(4)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
              .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESMedia(4)\"/>\n" + 
        "    <a:content type=\"image/svg+xml\" src=\"ESMedia(4)/$value\" />\n" + 
        "    <a:link rel=\"edit-media\" title=\"ESMedia\" href=\"ESMedia(4)/$value\"/>\n" +
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETMedia\" />\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">4</d:PropertyInt16>\n" + 
        "    </m:properties>\n" + 
        "  </a:entry>\n" + 
        "</a:feed>\n" + 
        "";
    XMLAssert.assertXMLEqual(expectedResult, resultString);
  }

  @Test
  public void primitiveValuesAllNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllNullable");
    final EntityCollection entitySet = data.readAll(edmEntitySet);
    final String resultString = IOUtils.toString(serializer.entityCollection(metadata,
        edmEntitySet.getEntityType(), entitySet,
                EntityCollectionSerializerOptions.with()
                    .contextURL(ContextURL.with().serviceRoot(URI.create("http://host/svc"))
                        .entitySet(edmEntitySet).build())
                    .setId("http://host/svc/ESAllNullable")
                    .build()).getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:feed xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:context=\"http://host/svc$metadata#ESAllNullable\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>http://host/svc/ESAllNullable</a:id>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESAllNullable(1)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "    <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESAllNullable(1)\" />\n" + 
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETAllNullable\" />\n" + 
        "    <a:content type=\"application/xml\">\n" + 
        "      <m:properties>\n" + 
        "        <d:PropertyKey m:type=\"Int16\">1</d:PropertyKey>\n" + 
        "        <d:PropertyInt16 m:null=\"true\" />\n" + 
        "        <d:PropertyString m:null=\"true\" />\n" + 
        "        <d:PropertyBoolean m:null=\"true\" />\n" + 
        "        <d:PropertyByte m:null=\"true\" />\n" + 
        "        <d:PropertySByte m:null=\"true\" />\n" + 
        "        <d:PropertyInt32 m:null=\"true\" />\n" + 
        "        <d:PropertyInt64 m:null=\"true\" />\n" + 
        "        <d:PropertySingle m:null=\"true\" />\n" + 
        "        <d:PropertyDouble m:null=\"true\" />\n" + 
        "        <d:PropertyDecimal m:null=\"true\" />\n" + 
        "        <d:PropertyBinary m:null=\"true\" />\n" + 
        "        <d:PropertyDate m:null=\"true\" />\n" + 
        "        <d:PropertyDateTimeOffset m:null=\"true\" />\n" + 
        "        <d:PropertyDuration m:null=\"true\" />\n" + 
        "        <d:PropertyGuid m:null=\"true\" />\n" + 
        "        <d:PropertyTimeOfDay m:null=\"true\" />\n" + 
        "        <d:CollPropertyString m:type=\"#Collection(String)\">\n" + 
        "          <m:element>spiderman@comic.com</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>spidergirl@comic.com</m:element>\n" + 
        "        </d:CollPropertyString>\n" + 
        "        <d:CollPropertyBoolean m:type=\"#Collection(Boolean)\">\n" + 
        "          <m:element>true</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>false</m:element>\n" + 
        "        </d:CollPropertyBoolean>\n" + 
        "        <d:CollPropertyByte m:type=\"#Collection(Byte)\">\n" + 
        "          <m:element>50</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>249</m:element>\n" + 
        "        </d:CollPropertyByte>\n" + 
        "        <d:CollPropertySByte m:type=\"#Collection(SByte)\">\n" + 
        "          <m:element>-120</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>126</m:element>\n" + 
        "        </d:CollPropertySByte>\n" + 
        "        <d:CollPropertyInt16 m:type=\"#Collection(Int16)\">\n" + 
        "          <m:element>1000</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>30112</m:element>\n" + 
        "        </d:CollPropertyInt16>\n" + 
        "        <d:CollPropertyInt32 m:type=\"#Collection(Int32)\">\n" + 
        "          <m:element>23232323</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>10000001</m:element>\n" + 
        "        </d:CollPropertyInt32>\n" + 
        "        <d:CollPropertyInt64 m:type=\"#Collection(Int64)\">\n" + 
        "          <m:element>929292929292</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>444444444444</m:element>\n" + 
        "        </d:CollPropertyInt64>\n" + 
        "        <d:CollPropertySingle m:type=\"#Collection(Single)\">\n" + 
        "          <m:element>1790.0</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>3210.0</m:element>\n" + 
        "        </d:CollPropertySingle>\n" + 
        "        <d:CollPropertyDouble m:type=\"#Collection(Double)\">\n" + 
        "          <m:element>-17900.0</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>3210.0</m:element>\n" + 
        "        </d:CollPropertyDouble>\n" + 
        "        <d:CollPropertyDecimal m:type=\"#Collection(Decimal)\">\n" + 
        "          <m:element>12</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>1234</m:element>\n" + 
        "        </d:CollPropertyDecimal>\n" + 
        "        <d:CollPropertyBinary m:type=\"#Collection(Binary)\">\n" + 
        "          <m:element>q83v</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>VGeJ</m:element>\n" + 
        "        </d:CollPropertyBinary>\n" + 
        "        <d:CollPropertyDate m:type=\"#Collection(Date)\">\n" + 
        "          <m:element>1958-12-03</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>2013-06-25</m:element>\n" + 
        "        </d:CollPropertyDate>\n" + 
        "        <d:CollPropertyDateTimeOffset m:type=\"#Collection(DateTimeOffset)\">\n" + 
        "          <m:element>2015-08-12T03:08:34Z</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>1948-02-17T09:09:09Z</m:element>\n" + 
        "        </d:CollPropertyDateTimeOffset>\n" + 
        "        <d:CollPropertyDuration m:type=\"#Collection(Duration)\">\n" + 
        "          <m:element>PT13S</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>PT1H0S</m:element>\n" + 
        "        </d:CollPropertyDuration>\n" + 
        "        <d:CollPropertyGuid m:type=\"#Collection(Guid)\">\n" + 
        "          <m:element>ffffff67-89ab-cdef-0123-456789aaaaaa</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>cccccc67-89ab-cdef-0123-456789cccccc</m:element>\n" + 
        "        </d:CollPropertyGuid>\n" + 
        "        <d:CollPropertyTimeOfDay m:type=\"#Collection(TimeOfDay)\">\n" + 
        "          <m:element>04:14:13</m:element>\n" + 
        "          <m:element d:null=\"true\" />\n" + 
        "          <m:element>00:37:13</m:element>\n" + 
        "        </d:CollPropertyTimeOfDay>\n" + 
        "      </m:properties>\n" + 
        "    </a:content>\n" + 
        "  </a:entry>\n" + 
        "</a:feed>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void select() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final SelectItem selectItem1 = ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyDate");
    final SelectItem selectItem2 = ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyBoolean");
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        selectItem1, selectItem2, selectItem2));
    InputStream result = serializer
        .entity(metadata, entityType, entity,
            EntitySerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(helper.buildContextURLSelectList(entityType, null, select))
                    .suffix(Suffix.ENTITY).build())
                .select(select)
                .build()).getContent();
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" " +
        "  m:context=\"$metadata#ESAllPrim(PropertyBoolean,PropertyDate)/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESAllPrim(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "    <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESAllPrim(32767)\"/>\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "    href=\"ESTwoPrim(32767)\" />\n" +
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "    href=\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\" />\n" +         
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyBoolean m:type=\"Boolean\">true</d:PropertyBoolean>\n" + 
        "      <d:PropertyDate m:type=\"Date\">2012-12-03</d:PropertyDate>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expectedResult, resultString);
  }

  @Test
  public void selectComplex() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompComp");
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final EntityCollection entitySet = data.readAll(edmEntitySet);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyComp", "PropertyComp", "PropertyString")));
    InputStream result = serializer
        .entityCollection(metadata, entityType, entitySet,
            EntityCollectionSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(helper.buildContextURLSelectList(entityType, null, select))
                    .build())
                .setId("http://host/svc/ESCompComp")
                .select(select)
                .build()).getContent();
    final String resultString = IOUtils.toString(result);
    final String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:feed xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\"\n" + 
        "  m:context=\"$metadata#ESCompComp(PropertyComp/PropertyComp/PropertyString)\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>http://host/svc/ESCompComp</a:id>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESCompComp(1)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "<a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
          .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESCompComp(1)\"/>\n" + 
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETCompComp\" />\n" + 
        "    <a:content type=\"application/xml\">\n" + 
        "      <m:properties>\n" + 
        "        <d:PropertyComp m:type=\"#olingo.odata.test1.CTCompComp\">\n" + 
        "          <d:PropertyComp m:type=\"#olingo.odata.test1.CTTwoPrim\">\n" + 
        "            <d:PropertyString>String 1</d:PropertyString>\n" + 
        "          </d:PropertyComp>\n" + 
        "        </d:PropertyComp>\n" + 
        "      </m:properties>\n" + 
        "    </a:content>\n" + 
        "  </a:entry>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESCompComp(2)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "<a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
            .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESCompComp(2)\"/>\n" + 
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETCompComp\" />\n" + 
        "    <a:content type=\"application/xml\">\n" + 
        "      <m:properties>\n" + 
        "        <d:PropertyComp m:type=\"#olingo.odata.test1.CTCompComp\">\n" + 
        "          <d:PropertyComp m:type=\"#olingo.odata.test1.CTTwoPrim\">\n" + 
        "            <d:PropertyString>String 2</d:PropertyString>\n" + 
        "          </d:PropertyComp>\n" + 
        "        </d:PropertyComp>\n" + 
        "      </m:properties>\n" + 
        "    </a:content>\n" + 
        "  </a:entry>\n" + 
        "</a:feed>\n"; 
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void selectComplexTwice() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompComp");
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final EntityCollection entitySet = data.readAll(edmEntitySet);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyComp", "PropertyComp", "PropertyString"),
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyComp", "PropertyComp")));
    final String resultString = IOUtils.toString(serializer
        .entityCollection(metadata, entityType, entitySet,
            EntityCollectionSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(helper.buildContextURLSelectList(entityType, null, select))
                    .build())
                .setId("http://host/svc/ESCompComp")
                .select(select)
                .build()).getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:feed xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" \n" + 
        "  m:context=\"$metadata#ESCompComp(PropertyComp/PropertyComp)\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>http://host/svc/ESCompComp</a:id>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESCompComp(1)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "    <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
                .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESCompComp(1)\"/>\n" + 
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETCompComp\" />\n" + 
        "    <a:content type=\"application/xml\">\n" + 
        "      <m:properties>\n" + 
        "        <d:PropertyComp m:type=\"#olingo.odata.test1.CTCompComp\">\n" + 
        "          <d:PropertyComp m:type=\"#olingo.odata.test1.CTTwoPrim\">\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">123</d:PropertyInt16>\n" + 
        "            <d:PropertyString>String 1</d:PropertyString>\n" + 
        "          </d:PropertyComp>\n" + 
        "        </d:PropertyComp>\n" + 
        "      </m:properties>\n" + 
        "    </a:content>\n" + 
        "  </a:entry>\n" + 
        "  <a:entry>\n" + 
        "    <a:id>ESCompComp(2)</a:id>\n" + 
        "    <a:title />\n" + 
        "    <a:summary />\n" + 
        "    <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
                .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "    <a:author>\n" + 
        "      <a:name />\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESCompComp(2)\"/>\n" + 
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "      term=\"#olingo.odata.test1.ETCompComp\" />\n" + 
        "    <a:content type=\"application/xml\">\n" + 
        "      <m:properties>\n" + 
        "        <d:PropertyComp m:type=\"#olingo.odata.test1.CTCompComp\">\n" + 
        "          <d:PropertyComp m:type=\"#olingo.odata.test1.CTTwoPrim\">\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">987</d:PropertyInt16>\n" + 
        "            <d:PropertyString>String 2</d:PropertyString>\n" + 
        "          </d:PropertyComp>\n" + 
        "        </d:PropertyComp>\n" + 
        "      </m:properties>\n" + 
        "    </a:content>\n" + 
        "  </a:entry>\n" + 
        "</a:feed>\n";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void expand() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(3);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(
        ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETAllPrimOne")));
    InputStream result = serializer.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .expand(expand)
            .build()).getContent();
    final String resultString = IOUtils.toString(result);
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" m:context=\"$metadata#ESTwoPrim/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESTwoPrim(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "    <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESTwoPrim(32767)\" />\n" +
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "    href=\"ESAllPrim(32767)\">\n" + 
        "    <m:inline>\n" + 
        "      <a:entry>\n" + 
        "        <a:id>ESAllPrim(32767)</a:id>\n" + 
        "        <a:title />\n" + 
        "        <a:summary />\n" + 
        "    <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "        <a:author>\n" + 
        "          <a:name />\n" + 
        "        </a:author>\n" + 
        "        <a:link rel=\"edit\" href=\"ESAllPrim(32767)\" />\n" + 
        "        <a:link\n" + 
        "          rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "          type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "          href=\"ESTwoPrim(32767)\" />\n" +
        "        <a:link\n" + 
        "          rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "          type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "          href=\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\" />\n" +         
        "        <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "          term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "        <a:content type=\"application/xml\">\n" + 
        "          <m:properties>\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "            <d:PropertyString>First Resource - positive values\n" + 
        "            </d:PropertyString>\n" + 
        "            <d:PropertyBoolean m:type=\"Boolean\">true\n" + 
        "            </d:PropertyBoolean>\n" + 
        "            <d:PropertyByte m:type=\"Byte\">255</d:PropertyByte>\n" + 
        "            <d:PropertySByte m:type=\"SByte\">127</d:PropertySByte>\n" + 
        "            <d:PropertyInt32 m:type=\"Int32\">2147483647\n" + 
        "            </d:PropertyInt32>\n" + 
        "            <d:PropertyInt64 m:type=\"Int64\">9223372036854775807\n" + 
        "            </d:PropertyInt64>\n" + 
        "            <d:PropertySingle m:type=\"Single\">1.79E20\n" + 
        "            </d:PropertySingle>\n" + 
        "            <d:PropertyDouble m:type=\"Double\">-1.79E19\n" + 
        "            </d:PropertyDouble>\n" + 
        "            <d:PropertyDecimal m:type=\"Decimal\">34</d:PropertyDecimal>\n" + 
        "            <d:PropertyBinary m:type=\"Binary\">ASNFZ4mrze8=\n" + 
        "            </d:PropertyBinary>\n" + 
        "            <d:PropertyDate m:type=\"Date\">2012-12-03</d:PropertyDate>\n" + 
        "            <d:PropertyDateTimeOffset m:type=\"DateTimeOffset\">2012-12-03T07:16:23Z\n" + 
        "            </d:PropertyDateTimeOffset>\n" + 
        "            <d:PropertyDuration m:type=\"Duration\">PT6S\n" + 
        "            </d:PropertyDuration>\n" + 
        "            <d:PropertyGuid m:type=\"Guid\">01234567-89ab-cdef-0123-456789abcdef\n" + 
        "            </d:PropertyGuid>\n" + 
        "            <d:PropertyTimeOfDay m:type=\"TimeOfDay\">03:26:05\n" + 
        "            </d:PropertyTimeOfDay>\n" + 
        "          </m:properties>\n" + 
        "        </a:content>\n" + 
        "      </a:entry>\n" + 
        "    </m:inline>\n" + 
        "  </a:link>\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimMany\"\n" + 
        "    href=\"ESTwoPrim(32767)/NavPropertyETAllPrimMany\" />"+
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "      <d:PropertyString>Test String4</d:PropertyString>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>\n" + 
        "";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void expandSelect() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(3);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(entityContainer.getEntitySet("ESAllPrim"), "PropertyDate")));
    ExpandItem expandItem = ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETAllPrimOne");
    Mockito.when(expandItem.getSelectOption()).thenReturn(select);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItem));
    final String resultString = IOUtils.toString(serializer
        .entity(metadata, entityType, entity,
            EntitySerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(helper.buildContextURLSelectList(entityType, expand, select))
                    .suffix(Suffix.ENTITY).build())
                .expand(expand)
                .build()).getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\"\n" + 
        "  m:context=\"$metadata#ESTwoPrim(NavPropertyETAllPrimOne(PropertyDate))/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESTwoPrim(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESTwoPrim(32767)\" />\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "    href=\"ESAllPrim(32767)\">\n" + 
        "    <m:inline>\n" + 
        "      <a:entry>\n" + 
        "        <a:id>ESAllPrim(32767)</a:id>\n" + 
        "        <a:title />\n" + 
        "        <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "        <a:author>\n" + 
        "          <a:name />\n" + 
        "        </a:author>\n" + 
        "        <a:link rel=\"edit\" href=\"ESAllPrim(32767)\" />\n" + 
        "        <a:link\n" + 
        "          rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "          type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "          href=\"ESTwoPrim(32767)\" />\n" +
        "        <a:link\n" + 
        "          rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "          type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "          href=\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\" />\n" +         
        "        <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "          term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "        <a:content type=\"application/xml\">\n" + 
        "          <m:properties>\n" + 
        "            <d:PropertyDate m:type=\"Date\">2012-12-03</d:PropertyDate>\n" + 
        "          </m:properties>\n" + 
        "        </a:content>\n" + 
        "      </a:entry>\n" + 
        "    </m:inline>\n" + 
        "  </a:link>\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimMany\"\n" + 
        "    href=\"ESTwoPrim(32767)/NavPropertyETAllPrimMany\" />" +
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "      <d:PropertyString>Test String4</d:PropertyString>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void expandAll() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final ExpandItem expandItem = ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETTwoPrimOne");
    ExpandItem expandItemAll = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItemAll.isStar()).thenReturn(true);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(
        expandItem, expandItem, expandItemAll));
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertySByte")));
    final String resultString = IOUtils.toString(serializer
        .entity(metadata, entityType, entity,
            EntitySerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(helper.buildContextURLSelectList(entityType, expand, select))
                    .suffix(Suffix.ENTITY).build())
                .expand(expand)
                .select(select)
                .build()).getContent());    
    final String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:context=\"$metadata#ESAllPrim(PropertySByte)/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESAllPrim(32767)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
              .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESAllPrim(32767)\"/>\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "    href=\"ESTwoPrim(32767)\">\n" + 
        "    <m:inline>\n" + 
        "      <a:entry>\n" + 
        "        <a:id>ESTwoPrim(32767)</a:id>\n" + 
        "        <a:title />\n" + 
        "        <a:summary />\n" + 
        "        <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
                  .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "        <a:author>\n" + 
        "          <a:name />\n" + 
        "        </a:author>\n" + 
        "        <a:link rel=\"edit\" href=\"ESTwoPrim(32767)\"/>\n" + 
        "        <a:link\n" + 
        "          rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "          type=\"application/atom+xml;type=entry\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "          href=\"ESAllPrim(32767)\" />\n" + 
        "       <a:link\n" + 
        "          rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimMany\"\n" + 
        "          type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimMany\"\n" + 
        "          href=\"ESTwoPrim(32767)/NavPropertyETAllPrimMany\" />"+
        "        <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "          term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "        <a:content type=\"application/xml\">\n" + 
        "          <m:properties>\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "            <d:PropertyString>Test String4</d:PropertyString>\n" + 
        "          </m:properties>\n" + 
        "        </a:content>\n" + 
        "      </a:entry>\n" + 
        "    </m:inline>\n" + 
        "  </a:link>\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "    href=\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\">\n" + 
        "    <m:inline>\n" + 
        "      <a:feed>\n" + 
        "        <a:entry>\n" + 
        "          <a:id>ESTwoPrim(-365)</a:id>\n" + 
        "          <a:title />\n" + 
        "          <a:summary />\n" + 
        "          <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
                  .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "          <a:author>\n" + 
        "            <a:name />\n" + 
        "          </a:author>\n" + 
        "          <a:link rel=\"edit\" href=\"ESTwoPrim(-365)\"/>\n" + 
        "         <a:link\n" + 
        "            rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "            type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "            href=\"ESTwoPrim(-365)/NavPropertyETAllPrimOne\" />"+
        "          <a:link\n" + 
        "            rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimMany\"\n" + 
        "            type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimMany\"\n" + 
        "            href=\"ESTwoPrim(-365)/NavPropertyETAllPrimMany\" />\n" + 
        "          <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "            term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "          <a:content type=\"application/xml\">\n" + 
        "            <m:properties>\n" + 
        "              <d:PropertyInt16 m:type=\"Int16\">-365</d:PropertyInt16>\n" + 
        "              <d:PropertyString>Test String2</d:PropertyString>\n" + 
        "            </m:properties>\n" + 
        "          </a:content>\n" + 
        "        </a:entry>\n" + 
        "      </a:feed>\n" + 
        "    </m:inline>\n" + 
        "  </a:link>\n" + 
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertySByte m:type=\"SByte\">127</d:PropertySByte>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>\n" + 
        "";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void expandNoData() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(1);
    ExpandItem expandItemAll = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItemAll.isStar()).thenReturn(true);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItemAll));
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyTimeOfDay")));
    final String resultString = IOUtils.toString(serializer
        .entity(metadata, entityType, entity,
            EntitySerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(helper.buildContextURLSelectList(entityType, expand, select))
                    .suffix(Suffix.ENTITY).build())
                .expand(expand)
                .select(select)
                .build()).getContent());    
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:context=\"$metadata#ESAllPrim(PropertyTimeOfDay)/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESAllPrim(-32768)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESAllPrim(-32768)\" />\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "    href=\"ESAllPrim(-32768)/NavPropertyETTwoPrimOne\">\n" + 
        "    <m:inline />\n" + 
        "  </a:link>\n" + 
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "    href=\"ESAllPrim(-32768)/NavPropertyETTwoPrimMany\">\n" + 
        "    <m:inline>\n" + 
        "      <a:feed />\n" + 
        "    </m:inline>\n" + 
        "  </a:link>"+
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyTimeOfDay m:type=\"TimeOfDay\">23:49:14\n" + 
        "      </d:PropertyTimeOfDay>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void expandTwoLevels() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final EdmEntitySet innerEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(1);
    ExpandItem expandItemSecond = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItemSecond.isStar()).thenReturn(true);
    final ExpandOption expandInner = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItemSecond));
    ExpandItem expandItemFirst = ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETAllPrimMany");
    Mockito.when(expandItemFirst.getExpandOption()).thenReturn(expandInner);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(innerEntitySet, "PropertyInt32")));
    Mockito.when(expandItemFirst.getSelectOption()).thenReturn(select);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItemFirst));
    final String resultString = IOUtils.toString(serializer
        .entity(metadata, entityType, entity,
            EntitySerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(helper.buildContextURLSelectList(entityType, expand, select))
                    .suffix(Suffix.ENTITY).build())
                .expand(expand)
                .build()).getContent());
    System.out.println(resultString);
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\"\n" + 
        "  m:context=\"$metadata#ESTwoPrim(NavPropertyETAllPrimMany(PropertyInt32))/$entity\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <a:id>ESTwoPrim(-365)</a:id>\n" + 
        "  <a:title />\n" + 
        "  <a:summary />\n" + 
        "  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "  <a:author>\n" + 
        "    <a:name />\n" + 
        "  </a:author>\n" + 
        "  <a:link rel=\"edit\" href=\"ESTwoPrim(-365)\" />\n" + 
        " <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "    href=\"ESTwoPrim(-365)/NavPropertyETAllPrimOne\" />" +
        "  <a:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimMany\"\n" + 
        "    href=\"ESTwoPrim(-365)/NavPropertyETAllPrimMany\">\n" + 
        "    <m:inline>\n" + 
        "      <a:feed>\n" + 
        "        <a:entry>\n" + 
        "          <a:id>ESAllPrim(-32768)</a:id>\n" + 
        "          <a:title />\n" + 
        "          <a:summary />\n" + 
        "          <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
        "          <a:author>\n" + 
        "            <a:name />\n" + 
        "          </a:author>\n" + 
        "          <a:link rel=\"edit\" href=\"ESAllPrim(-32768)\" />\n" + 
        "         <a:link\n" + 
        "            rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "            type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "            href=\"ESAllPrim(-32768)/NavPropertyETTwoPrimOne\">\n" + 
        "            <m:inline />\n" + 
        "          </a:link>\n" + 
        "          <a:link\n" + 
        "            rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "            type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "            href=\"ESAllPrim(-32768)/NavPropertyETTwoPrimMany\">\n" + 
        "            <m:inline>\n" + 
        "              <a:feed />\n" + 
        "            </m:inline>\n" + 
        "          </a:link>"+
        "          <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "            term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "          <a:content type=\"application/xml\">\n" + 
        "            <m:properties>\n" + 
        "              <d:PropertyInt32 m:type=\"Int32\">-2147483648</d:PropertyInt32>\n" + 
        "            </m:properties>\n" + 
        "          </a:content>\n" + 
        "        </a:entry>\n" + 
        "        <a:entry>\n" + 
        "          <a:id>ESAllPrim(0)</a:id>\n" + 
        "          <a:title />\n" + 
        "          <a:summary />\n" + 
        "          <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" + 
        "          <a:author>\n" + 
        "            <a:name />\n" + 
        "          </a:author>\n" + 
        "          <a:link rel=\"edit\" href=\"ESAllPrim(0)\" />\n" + 
        "         <a:link\n" + 
        "            rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "            type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "            href=\"ESAllPrim(0)/NavPropertyETTwoPrimOne\">\n" + 
        "            <m:inline />\n" + 
        "          </a:link>" +
        "          <a:link\n" + 
        "            rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "            type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "            href=\"ESAllPrim(0)/NavPropertyETTwoPrimMany\">\n" + 
        "            <m:inline>\n" + 
        "              <a:feed>\n" + 
        "                <a:entry>\n" + 
        "                  <a:id>ESTwoPrim(32766)</a:id>\n" + 
        "                  <a:title />\n" + 
        "                  <a:summary />\n" + 
        "                  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" +
 
        "                  <a:author>\n" + 
        "                    <a:name />\n" + 
        "                  </a:author>\n" + 
        "                  <a:link rel=\"edit\" href=\"ESTwoPrim(32766)\" />\n" + 
        "                 <a:link\n" + 
        "                    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "                    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "                    href=\"ESTwoPrim(32766)/NavPropertyETAllPrimOne\" />\n" + 
        "                  <a:link\n" + 
        "                    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimMany\"\n" + 
        "                    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimMany\"\n" + 
        "                    href=\"ESTwoPrim(32766)/NavPropertyETAllPrimMany\" />" +
        "                  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "                    term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "                  <a:content type=\"application/xml\">\n" + 
        "                    <m:properties>\n" + 
        "                      <d:PropertyInt16 m:type=\"Int16\">32766</d:PropertyInt16>\n" + 
        "                      <d:PropertyString>Test String1</d:PropertyString>\n" + 
        "                    </m:properties>\n" + 
        "                  </a:content>\n" + 
        "                </a:entry>\n" + 
        "                <a:entry>\n" + 
        "                  <a:id>ESTwoPrim(-32766)</a:id>\n" + 
        "                  <a:title />\n" + 
        "                  <a:summary />\n" + 
        "                  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" + 
        "                  <a:author>\n" + 
        "                    <a:name />\n" + 
        "                  </a:author>\n" + 
        "                  <a:link rel=\"edit\" href=\"ESTwoPrim(-32766)\" />\n" + 
        "                 <a:link\n" + 
        "                    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "                    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "                    href=\"ESTwoPrim(-32766)/NavPropertyETAllPrimOne\" />\n" + 
        "                  <a:link\n" + 
        "                    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimMany\"\n" + 
        "                    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimMany\"\n" + 
        "                    href=\"ESTwoPrim(-32766)/NavPropertyETAllPrimMany\" />" +
        "                  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "                    term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "                  <a:content type=\"application/xml\">\n" + 
        "                    <m:properties>\n" + 
        "                      <d:PropertyInt16 m:type=\"Int16\">-32766</d:PropertyInt16>\n" + 
        "                      <d:PropertyString m:null=\"true\" />\n" + 
        "                    </m:properties>\n" + 
        "                  </a:content>\n" + 
        "                </a:entry>\n" + 
        "                <a:entry>\n" + 
        "                  <a:id>ESTwoPrim(32767)</a:id>\n" + 
        "                  <a:title />\n" + 
        "                  <a:summary />\n" + 
        "                  <a:updated>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") 
        .format(new Date(System.currentTimeMillis()))+"</a:updated>" + 
        "                  <a:author>\n" + 
        "                    <a:name />\n" + 
        "                  </a:author>\n" + 
        "                  <a:link rel=\"edit\" href=\"ESTwoPrim(32767)\" />\n" + 
        "                  <a:link\n" + 
        "                    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "                    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "                    href=\"ESAllPrim(32767)\" />\n" + 
        "                 <a:link\n" + 
        "                    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimMany\"\n" + 
        "                    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETAllPrimMany\"\n" + 
        "                    href=\"ESTwoPrim(32767)/NavPropertyETAllPrimMany\" />" +
        "                  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "                    term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "                  <a:content type=\"application/xml\">\n" + 
        "                    <m:properties>\n" + 
        "                      <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "                      <d:PropertyString>Test String4</d:PropertyString>\n" + 
        "                    </m:properties>\n" + 
        "                  </a:content>\n" + 
        "                </a:entry>\n" + 
        "              </a:feed>\n" + 
        "            </m:inline>\n" + 
        "          </a:link>\n" + 
        "          <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "            term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "          <a:content type=\"application/xml\">\n" + 
        "            <m:properties>\n" + 
        "              <d:PropertyInt32 m:type=\"Int32\">0</d:PropertyInt32>\n" + 
        "            </m:properties>\n" + 
        "          </a:content>\n" + 
        "        </a:entry>\n" + 
        "      </a:feed>\n" + 
        "    </m:inline>\n" + 
        "  </a:link>\n" + 
        "  <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "  <a:content type=\"application/xml\">\n" + 
        "    <m:properties>\n" + 
        "      <d:PropertyInt16 m:type=\"Int16\">-365</d:PropertyInt16>\n" + 
        "      <d:PropertyString>Test String2</d:PropertyString>\n" + 
        "    </m:properties>\n" + 
        "  </a:content>\n" + 
        "</a:entry>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void primitiveProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyString");
    final Property property = data.readAll(edmEntitySet).getEntities().get(0).getProperty(edmProperty.getName());
    final String resultString = IOUtils.toString(serializer
        .primitive(metadata, (EdmPrimitiveType) edmProperty.getType(), property,
            PrimitiveSerializerOptions.with()
                .contextURL(ContextURL.with()
                    .entitySet(edmEntitySet).keyPath("32767").navOrPropertyPath(edmProperty.getName())
                    .build())
                .build()).getContent());
    
    String expected = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<m:value xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "m:context=\"$metadata#ESAllPrim(32767)/PropertyString\" "
        + "m:metadata-etag=\"WmetadataETag\">"
        + "First Resource - positive values</m:value>";
    Assert.assertEquals(expected, resultString);
  }

  @Test
  public void primitivePropertyNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyString");
    final Property property = new Property("Edm.String", edmProperty.getName(), ValueType.PRIMITIVE, null);
    String response = IOUtils.toString(serializer.primitive(metadata, (EdmPrimitiveType) edmProperty.getType(), 
        property,
        PrimitiveSerializerOptions.with()
            .contextURL(ContextURL.with()
                .entitySet(edmEntitySet).keyPath("4242").navOrPropertyPath(edmProperty.getName())
                .build())
            .build()).getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<m:value xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "m:context=\"$metadata#ESAllPrim(4242)/PropertyString\" "
        + "m:metadata-etag=\"WmetadataETag\" "
        + "m:null=\"true\"/>";
    Assert.assertEquals(expected, response);
  }

  @Test
  public void primitiveCollectionProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCollAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("CollPropertyString");
    final Property property = data.readAll(edmEntitySet).getEntities().get(0).getProperty(edmProperty.getName());

    final String resultString = IOUtils.toString(serializer
        .primitiveCollection(metadata, (EdmPrimitiveType) edmProperty.getType(), property,
            PrimitiveSerializerOptions.with()
                .contextURL(ContextURL.with()
                    .entitySet(edmEntitySet).keyPath("1").navOrPropertyPath(edmProperty.getName())
                    .build())
                .build()).getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<m:value xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "m:context=\"$metadata#ESCollAllPrim(1)/CollPropertyString\" "
        + "m:metadata-etag=\"WmetadataETag\"  m:type=\"#Collection(String)\">"
        + "<m:element>Employee1@company.example</m:element>"
        + "<m:element>Employee2@company.example</m:element>"
        + "<m:element>Employee3@company.example</m:element>"
        + "</m:value>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void complexProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyComp");
    final Property property = data.readAll(edmEntitySet).getEntities().get(0).getProperty("PropertyComp");

    final String resultString = IOUtils.toString(serializer
        .complex(metadata, (EdmComplexType) edmProperty.getType(), property,
            ComplexSerializerOptions.with()
                .contextURL(ContextURL.with()
                    .entitySet(edmEntitySet).keyPath("32767").navOrPropertyPath(edmProperty.getName())
                    .build())
                .build()).getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<m:value xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:type=\"#olingo.odata.test1.CTTwoPrim\" "
        + "m:context=\"$metadata#ESMixPrimCollComp(32767)/PropertyComp\" "
        + "m:metadata-etag=\"WmetadataETag\">"
        + "<d:PropertyInt16 m:type=\"Int16\">111</d:PropertyInt16>"
        + "<d:PropertyString>TEST A</d:PropertyString>"
        + "</m:value>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void complexCollectionProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("CollPropertyComp");
    final Property property = data.readAll(edmEntitySet).getEntities().get(0).getProperty(edmProperty.getName());

    final String resultString = IOUtils.toString(serializer
        .complexCollection(metadata, (EdmComplexType) edmProperty.getType(), property,
            ComplexSerializerOptions.with()
                .contextURL(ContextURL.with()
                    .entitySet(edmEntitySet).keyPath("32767").navOrPropertyPath(edmProperty.getName())
                    .build())
                .build()).getContent());
    String expected = "<m:value xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "m:type=\"#Collection(olingo.odata.test1.CTTwoPrim)\"\n" + 
        "  m:context=\"$metadata#ESMixPrimCollComp(32767)/CollPropertyComp\"\n" + 
        "  m:metadata-etag=\"WmetadataETag\">\n" + 
        "  <m:element>\n" + 
        "    <d:PropertyInt16 m:type=\"Int16\">123</d:PropertyInt16>\n" + 
        "    <d:PropertyString>TEST 1</d:PropertyString>\n" + 
        "  </m:element>\n" + 
        "  <m:element>\n" + 
        "    <d:PropertyInt16 m:type=\"Int16\">456</d:PropertyInt16>\n" + 
        "    <d:PropertyString>TEST 2</d:PropertyString>\n" + 
        "  </m:element>\n" + 
        "  <m:element>\n" + 
        "    <d:PropertyInt16 m:type=\"Int16\">789</d:PropertyInt16>\n" + 
        "    <d:PropertyString>TEST 3</d:PropertyString>\n" + 
        "  </m:element>\n" + 
        "</m:value>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void entityReference() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);

    ReferenceSerializerOptions options = ReferenceSerializerOptions.with()
        .contextURL(ContextURL.with().suffix(Suffix.REFERENCE).build()).build();

    final SerializerResult serializerResult = serializer.reference(metadata, edmEntitySet, entity,options);
    final String resultString = IOUtils.toString(serializerResult.getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<m:ref xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  m:context=\"$metadata#$ref\" id=\"ESAllPrim(32767)\" />";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void entityCollectionReference() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EntityCollection entityCollection = data.readAll(edmEntitySet);

    ReferenceCollectionSerializerOptions options = ReferenceCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().asCollection().suffix(Suffix.REFERENCE).build()).build();
    
    final SerializerResult serializerResult = serializer.referenceCollection(metadata,
        edmEntitySet,
        entityCollection,options);

    final String resultString = IOUtils.toString(serializerResult.getContent());
    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:feed xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  m:context=\"$metadata#Collection($ref)\">\n" + 
        "  <m:ref id=\"ESAllPrim(32767)\" />\n" + 
        "  <m:ref id=\"ESAllPrim(-32768)\" />\n" + 
        "  <m:ref id=\"ESAllPrim(0)\" />\n" + 
        "</a:feed>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }

  @Test
  public void entityCollectionReferenceEmpty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EntityCollection entityCollection = new EntityCollection();

    ReferenceCollectionSerializerOptions options = ReferenceCollectionSerializerOptions.with()
        .contextURL(ContextURL.with().asCollection().suffix(Suffix.REFERENCE).build()).build();
    
    final SerializerResult serializerResult = serializer.referenceCollection(metadata,
        edmEntitySet,
        entityCollection,options);

    final String resultString = IOUtils.toString(serializerResult.getContent());

    String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:feed xmlns:a=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  m:context=\"$metadata#Collection($ref)\">\n" + 
        "</a:feed>";
    XMLAssert.assertXMLEqual(expected, resultString);
  }
}
