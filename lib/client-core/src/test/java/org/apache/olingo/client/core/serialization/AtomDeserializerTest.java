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
package org.apache.olingo.client.core.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.junit.Test;

public class AtomDeserializerTest {

  @Test
  public void emptyInlineEntityOlingo540() throws Exception {
    final String content = "" +
        "<entry xmlns=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" "
        + "xml:base=\"http://services.odata.org/V3/OData/OData.svc/\">\r\n" +
        "    <id>http://services.odata.org/V3/OData/OData.svc/Products(3)</id>\r\n" +
        "    <category term=\"ODataDemo.Product\" "
        + "scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" />\r\n" +
        "    \r\n" +
        "    <link rel=\"edit\" title=\"Product\" href=\"Products\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/Categories\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Categories\" href=\"Products(3)/Categories\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/Supplier\" "
        + "type=\"application/atom+xml;type=entry\" title=\"Supplier\" href=\"Products(3)/Supplier\">\r\n" +
        "    <metadata:inline>\r\n" +
        "    </metadata:inline>\r\n" +
        "    </link>\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/ProductDetail\""
        + " type=\"application/atom+xml;type=entry\" title=\"ProductDetail\" "
        + "href=\"Products(3)/ProductDetail\" />\r\n" +
        "    <title type=\"text\">Havina Cola</title>\r\n" +
        "    <summary type=\"text\">The Original Key Lime Cola</summary>\r\n" +
        "    <updated>2015-01-26T08:57:02Z</updated>\r\n" +
        "    <author>\r\n" +
        "        <name />\r\n" +
        "    </author>\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Categories\" "
        + "type=\"application/xml\" title=\"Categories\" href=\"Products(3)/$links/Categories\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Supplier\" "
        + "type=\"application/xml\" title=\"Supplier\" href=\"Products(3)/$links/Supplier\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/ProductDetail\""
        + " type=\"application/xml\" title=\"ProductDetail\" href=\"Products(3)/$links/ProductDetail\" />\r\n" +
        "    <content type=\"application/xml\">\r\n" +
        "        <metadata:properties>\r\n" +
        "            <data:ID metadata:type=\"Edm.Int32\">3</data:ID>\r\n" +
        "            <data:ReleaseDate metadata:type=\"Edm.DateTime\">2005-10-01T00:00:00</data:ReleaseDate>\r\n" +
        "  <data:DiscontinuedDate metadata:type=\"Edm.DateTime\">2006-10-01T00:00:00</data:DiscontinuedDate>\r\n" +
        "            <data:Rating metadata:type=\"Edm.Int16\">3</data:Rating>\r\n" +
        "            <data:Price metadata:type=\"Edm.Double\">19.9</data:Price>\r\n" +
        "        </metadata:properties>\r\n" +
        "    </content>\r\n" +
        " </entry>";

    final AtomDeserializer deserializer = new AtomDeserializer();
    final InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
    final ResWrap<Entity> entity = deserializer.toEntity(in);

    assertNotNull(entity);
    assertNull(entity.getPayload().getNavigationLink("Supplier").getInlineEntitySet());
  }

  @Test
  public void filledInlineEntity() throws Exception {
    final String content = "" +
        "<entry xmlns=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "xmlns:georss=\"http://www.georss.org/georss\" "
        + "xmlns:gml=\"http://www.opengis.net/gml\" "
        + "xml:base=\"http://services.odata.org/V4/OData/OData.svc/\">\r\n" +
        "    <id>http://services.odata.org/V4/OData/OData.svc/Products(3)</id>\r\n" +
        "    <category term=\"#ODataDemo.Product\" "
        + "scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" />\r\n" +
        "    \r\n" +
        "    <link rel=\"edit\" title=\"Product\" href=\"Products\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/Categories\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Categories\" href=\"Products(3)/Categories\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/Supplier\" "
        + "type=\"application/atom+xml;type=entry\" title=\"Supplier\" href=\"Products(3)/Supplier\">\r\n" +
        "    <metadata:inline>\r\n" +
        "       <entry>\r\n" +
        "            <id>http://services.odata.org/V4/OData/OData.svc/Suppliers(0)</id>\r\n" +
        "            <category term=\"ODataDemo.Supplier\" "
        + "scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" />\r\n" +
        "            <link rel=\"edit\" title=\"Supplier\" href=\"Suppliers(0)\" />\r\n" +
        "            <link rel=\"http://docs.oasis-open.org/odata/ns/related/Products\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Products\" href=\"Suppliers(0)/Products\" />\r\n" +
        "            <title type=\"text\">Exotic Liquids</title>\r\n" +
        "            <updated>2015-01-26T08:57:02Z</updated>\r\n" +
        "            <author>\r\n" +
        "                <name />\r\n" +
        "            </author>\r\n" +
        "            <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Products\" "
        + "type=\"application/xml\" title=\"Products\" href=\"Suppliers(0)/$links/Products\" />\r\n" +
        "            <content type=\"application/xml\">\r\n" +
        "                 <metadata:properties>\r\n" +
        "                    <data:ID metadata:type=\"Edm.Int32\">0</data:ID>\r\n" +
        "                    <data:Name>Exotic Liquids</data:Name>\r\n" +
        "                    <data:Address metadata:type=\"ODataDemo.Address\">\r\n" +
        "                    <data:Street>NE 228th</data:Street>\r\n" +
        "                    <data:City>Sammamish</data:City>\r\n" +
        "                    <data:State>WA</data:State>\r\n" +
        "                    <data:ZipCode>98074</data:ZipCode>\r\n" +
        "                    <data:Country>USA</data:Country>\r\n" +
        "                     </data:Address>\r\n" +
        "                    <data:Location metadata:type=\"Edm.GeographyPoint\">\r\n" +
        "                    <gml:Point gml:srsName=\"http://www.opengis.net/def/crs/EPSG/0/4326\">\r\n" +
        "                    <gml:pos>47.6316604614258 -122.03547668457</gml:pos>\r\n" +
        "                    </gml:Point>\r\n" +
        "                    </data:Location>\r\n" +
        "                    <data:Concurrency metadata:type=\"Edm.Int32\">0</data:Concurrency>\r\n" +
        "                 </metadata:properties>\r\n" +
        "            </content>\r\n" +
        "         </entry>" +
        "    </metadata:inline>\r\n" +
        "    </link>\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/ProductDetail\" "
        + "type=\"application/atom+xml;type=entry\" "
        + "title=\"ProductDetail\" href=\"Products(3)/ProductDetail\" />\r\n" +
        "    <title type=\"text\">Havina Cola</title>\r\n" +
        "    <summary type=\"text\">The Original Key Lime Cola</summary>\r\n" +
        "    <updated>2015-01-26T08:57:02Z</updated>\r\n" +
        "    <author>\r\n" +
        "        <name />\r\n" +
        "    </author>\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Categories\" "
        + "type=\"application/xml\" title=\"Categories\" href=\"Products(3)/$links/Categories\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Supplier\" "
        + "type=\"application/xml\" title=\"Supplier\" href=\"Products(3)/$links/Supplier\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/ProductDetail\" "
        + "type=\"application/xml\" title=\"ProductDetail\" href=\"Products(3)/$links/ProductDetail\" />\r\n" +
        "    <content type=\"application/xml\">\r\n" +
        "        <metadata:properties>\r\n" +
        "            <data:ID metadata:type=\"Edm.Int32\">3</data:ID>\r\n" +
        "            <data:ReleaseDate metadata:type=\"Edm.DateTime\">2005-10-01T00:00:00</data:ReleaseDate>\r\n" +
        "  <data:DiscontinuedDate metadata:type=\"Edm.DateTime\">2006-10-01T00:00:00</data:DiscontinuedDate>\r\n" +
        "            <data:Rating metadata:type=\"Edm.Int16\">3</data:Rating>\r\n" +
        "            <data:Price metadata:type=\"Edm.Double\">19.9</data:Price>\r\n" +
        "        </metadata:properties>\r\n" +
        "    </content>\r\n" +
        " </entry>";
    final AtomDeserializer deserializer = new AtomDeserializer();
    final InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
    final ResWrap<Entity> entity = deserializer.toEntity(in);

    assertNotNull(entity);
    final Entity inlineEntity = entity.getPayload().getNavigationLink("Supplier").getInlineEntity();
    assertNotNull(inlineEntity);

    assertEquals(new Integer(0), inlineEntity.getProperty("ID").getValue());
    assertEquals("Exotic Liquids", inlineEntity.getProperty("Name").getValue());
  }

  @Test
  public void emptyInlineEntityCollection() throws Exception {
    final String content = "" +
        "<entry xmlns=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" "
        + "xml:base=\"http://services.odata.org/V3/OData/OData.svc/\">\r\n" +
        "    <id>http://services.odata.org/V3/OData/OData.svc/Products(3)</id>\r\n" +
        "    <category term=\"ODataDemo.Product\" "
        + "scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" />\r\n" +
        "    \r\n" +
        "    <link rel=\"edit\" title=\"Product\" href=\"Products(3)\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/Categories\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Categories\" href=\"Products(3)/Categories\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/Supplier\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Supplier\" href=\"Products(3)/Supplier\">\r\n" +
        "    <metadata:inline>\r\n" +
        "        <feed>\r\n" +
        "         </feed>\r\n" +
        "    </metadata:inline>\r\n" +
        "    </link>\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/ProductDetail\" "
        + "type=\"application/atom+xml;type=entry\" "
        + "title=\"ProductDetail\" href=\"Products(3)/ProductDetail\" />\r\n" +
        "    <title type=\"text\">Havina Cola</title>\r\n" +
        "    <summary type=\"text\">The Original Key Lime Cola</summary>\r\n" +
        "    <updated>2015-01-26T08:57:02Z</updated>\r\n" +
        "    <author>\r\n" +
        "        <name />\r\n" +
        "    </author>\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Categories\" "
        + "type=\"application/xml\" title=\"Categories\" href=\"Products(3)/$links/Categories\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Supplier\" "
        + "type=\"application/xml\" title=\"Supplier\" href=\"Products(3)/$links/Supplier\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/ProductDetail\" "
        + "type=\"application/xml\" title=\"ProductDetail\" href=\"Products(3)/$links/ProductDetail\" />\r\n" +
        "    <content type=\"application/xml\">\r\n" +
        "        <metadata:properties>\r\n" +
        "            <data:ID metadata:type=\"Edm.Int32\">3</data:ID>\r\n" +
        "            <data:ReleaseDate metadata:type=\"Edm.DateTime\">2005-10-01T00:00:00</data:ReleaseDate>\r\n" +
        "  <data:DiscontinuedDate metadata:type=\"Edm.DateTime\">2006-10-01T00:00:00</data:DiscontinuedDate>\r\n" +
        "            <data:Rating metadata:type=\"Edm.Int16\">3</data:Rating>\r\n" +
        "            <data:Price metadata:type=\"Edm.Double\">19.9</data:Price>\r\n" +
        "        </metadata:properties>\r\n" +
        "    </content>\r\n" +
        " </entry>";
    final AtomDeserializer deserializer = new AtomDeserializer();
    final InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
    final ResWrap<Entity> entity = deserializer.toEntity(in);

    assertNotNull(entity);
    final EntityCollection inlineEntitySet = entity.getPayload().getNavigationLink("Supplier").getInlineEntitySet();
    assertNotNull(inlineEntitySet);
    assertEquals(0, inlineEntitySet.getEntities().size());
  }

  @Test
  public void filledInlineEntityCollection() throws Exception {
    final String content = "" +
        "<entry xmlns=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" "
        + "xml:base=\"http://services.odata.org/V3/OData/OData.svc/\">\r\n" +
        "    <id>http://services.odata.org/V3/OData/OData.svc/Products(3)</id>\r\n" +
        "    <category term=\"ODataDemo.Product\" "
        + "scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" />\r\n" +
        "    \r\n" +
        "    <link rel=\"edit\" title=\"Product\" href=\"Products(3)\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/Categories\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Categories\" href=\"Products(3)/Categories\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/Supplier\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Supplier\" href=\"Products(3)/Supplier\">\r\n" +
        "    <metadata:inline>\r\n" +
        "        <feed>\r\n" +
        "        <entry>\r\n" +
        "            <id>http://services.odata.org/V3/OData/OData.svc/Suppliers(0)</id>\r\n" +
        "            <category term=\"ODataDemo.Supplier\" "
        + "scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" />\r\n" +
        "            <link rel=\"edit\" title=\"Supplier\" href=\"Suppliers(0)\" />\r\n" +
        "            <link rel=\"http://docs.oasis-open.org/odata/ns/related/Products\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Products\" href=\"Suppliers(0)/Products\" />\r\n" +
        "            <title type=\"text\">Exotic Liquids</title>\r\n" +
        "            <updated>2015-01-26T08:57:02Z</updated>\r\n" +
        "            <author>\r\n" +
        "                <name />\r\n" +
        "            </author>\r\n" +
        "            <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Products\" "
        + "type=\"application/xml\" title=\"Products\" href=\"Suppliers(0)/$links/Products\" />\r\n" +
        "            <content type=\"application/xml\">\r\n" +
        "                 <metadata:properties>\r\n" +
        "                    <data:ID metadata:type=\"Edm.Int32\">0</data:ID>\r\n" +
        "                    <data:Name>Exotic Liquids</data:Name>\r\n" +
        "                    <data:Address metadata:type=\"ODataDemo.Address\">\r\n" +
        "                    <data:Street>NE 228th</data:Street>\r\n" +
        "                    <data:City>Sammamish</data:City>\r\n" +
        "                    <data:State>WA</data:State>\r\n" +
        "                    <data:ZipCode>98074</data:ZipCode>\r\n" +
        "                    <data:Country>USA</data:Country>\r\n" +
        "                     </data:Address>\r\n" +
        "                    <data:Location metadata:type=\"Edm.GeographyPoint\">\r\n" +
        "                    <gml:Point gml:srsName=\"http://www.opengis.net/def/crs/EPSG/0/4326\">\r\n" +
        "                    <gml:pos>47.6316604614258 -122.03547668457</gml:pos>\r\n" +
        "                    </gml:Point>\r\n" +
        "                    </data:Location>\r\n" +
        "                    <data:Concurrency metadata:type=\"Edm.Int32\">0</data:Concurrency>\r\n" +
        "                 </metadata:properties>\r\n" +
        "            </content>\r\n" +
        "         </entry>\r\n" +
        "         </feed>\r\n" +
        "    </metadata:inline>\r\n" +
        "    </link>\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/related/ProductDetail\" "
        + "type=\"application/atom+xml;type=entry\" "
        + "title=\"ProductDetail\" href=\"Products(3)/ProductDetail\" />\r\n" +
        "    <title type=\"text\">Havina Cola</title>\r\n" +
        "    <summary type=\"text\">The Original Key Lime Cola</summary>\r\n" +
        "    <updated>2015-01-26T08:57:02Z</updated>\r\n" +
        "    <author>\r\n" +
        "        <name />\r\n" +
        "    </author>\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Categories\" "
        + "type=\"application/xml\" title=\"Categories\" href=\"Products(3)/$links/Categories\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/Supplier\" "
        + "type=\"application/xml\" title=\"Supplier\" href=\"Products(3)/$links/Supplier\" />\r\n" +
        "    <link rel=\"http://docs.oasis-open.org/odata/ns/relatedlinks/ProductDetail\" "
        + "type=\"application/xml\" title=\"ProductDetail\" href=\"Products(3)/$links/ProductDetail\" />\r\n" +
        "    <content type=\"application/xml\">\r\n" +
        "        <metadata:properties>\r\n" +
        "            <data:ID metadata:type=\"Edm.Int32\">3</data:ID>\r\n" +
        "            <data:ReleaseDate metadata:type=\"Edm.DateTime\">2005-10-01T00:00:00</data:ReleaseDate>\r\n" +
        "  <data:DiscontinuedDate metadata:type=\"Edm.DateTime\">2006-10-01T00:00:00</data:DiscontinuedDate>\r\n" +
        "            <data:Rating metadata:type=\"Edm.Int16\">3</data:Rating>\r\n" +
        "            <data:Price metadata:type=\"Edm.Double\">19.9</data:Price>\r\n" +
        "        </metadata:properties>\r\n" +
        "    </content>\r\n" +
        " </entry>";
    final AtomDeserializer deserializer = new AtomDeserializer();
    final InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
    final ResWrap<Entity> entity = deserializer.toEntity(in);

    assertNotNull(entity);
    final EntityCollection inlineEntitySet = entity.getPayload().getNavigationLink("Supplier").getInlineEntitySet();
    assertNotNull(inlineEntitySet);
    assertEquals(1, inlineEntitySet.getEntities().size());
  }
}
