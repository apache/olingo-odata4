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
package org.apache.olingo.commons.core.serialization;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.junit.Test;

public class AtomDeserializerTest {

  @Test
  public void emptyInlineEntityOlingo540() throws Exception {
    final String content = "" + 
        "<entry xmlns=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" " 
        + "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" " 
        + "xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" "
        + "xml:base=\"http://services.odata.org/V3/OData/OData.svc/\">\r\n" + 
        "    <id>http://services.odata.org/V3/OData/OData.svc/Products(3)</id>\r\n" + 
        "    <category term=\"ODataDemo.Product\" "
        + "scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" />\r\n" + 
        "    \r\n" + 
        "    <link rel=\"edit\" title=\"Product\" href=\"Products\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Categories\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Categories\" href=\"Products(3)/Categories\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Supplier\" "
        + "type=\"application/atom+xml;type=entry\" title=\"Supplier\" href=\"Products(3)/Supplier\">\r\n" + 
        "    <m:inline>\r\n" + 
        "    </m:inline>\r\n" + 
        "    </link>\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/ProductDetail\""
        + " type=\"application/atom+xml;type=entry\" title=\"ProductDetail\" "
        + "href=\"Products(3)/ProductDetail\" />\r\n" + 
        "    <title type=\"text\">Havina Cola</title>\r\n" + 
        "    <summary type=\"text\">The Original Key Lime Cola</summary>\r\n" + 
        "    <updated>2015-01-26T08:57:02Z</updated>\r\n" + 
        "    <author>\r\n" + 
        "        <name />\r\n" + 
        "    </author>\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Categories\" "
        + "type=\"application/xml\" title=\"Categories\" href=\"Products(3)/$links/Categories\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Supplier\" "
        + "type=\"application/xml\" title=\"Supplier\" href=\"Products(3)/$links/Supplier\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/ProductDetail\""
        + " type=\"application/xml\" title=\"ProductDetail\" href=\"Products(3)/$links/ProductDetail\" />\r\n" + 
        "    <content type=\"application/xml\">\r\n" + 
        "        <m:properties>\r\n" + 
        "            <d:ID m:type=\"Edm.Int32\">3</d:ID>\r\n" + 
        "            <d:ReleaseDate m:type=\"Edm.DateTime\">2005-10-01T00:00:00</d:ReleaseDate>\r\n" + 
        "            <d:DiscontinuedDate m:type=\"Edm.DateTime\">2006-10-01T00:00:00</d:DiscontinuedDate>\r\n" + 
        "            <d:Rating m:type=\"Edm.Int16\">3</d:Rating>\r\n" + 
        "            <d:Price m:type=\"Edm.Double\">19.9</d:Price>\r\n" + 
        "        </m:properties>\r\n" + 
        "    </content>\r\n" + 
        " </entry>";

    final AtomDeserializer deserializer = new AtomDeserializer(ODataServiceVersion.V30);
    final InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
    final ResWrap<Entity> entity = deserializer.toEntity(in);
    
    assertNotNull(entity);
    assertNull(entity.getPayload().getNavigationLink("Supplier").getInlineEntitySet());
  }
  
  @Test
  public void filledInlineEntity() throws Exception {
    final String content = "" + 
        "<entry xmlns=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" "
        + "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" "
        + "xmlns:georss=\"http://www.georss.org/georss\" "
        + "xmlns:gml=\"http://www.opengis.net/gml\" "
        + "xml:base=\"http://services.odata.org/V3/OData/OData.svc/\">\r\n" + 
        "    <id>http://services.odata.org/V3/OData/OData.svc/Products(3)</id>\r\n" + 
        "    <category term=\"ODataDemo.Product\" "
        + "scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" />\r\n" + 
        "    \r\n" + 
        "    <link rel=\"edit\" title=\"Product\" href=\"Products\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Categories\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Categories\" href=\"Products(3)/Categories\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Supplier\" "
        + "type=\"application/atom+xml;type=entry\" title=\"Supplier\" href=\"Products(3)/Supplier\">\r\n" + 
        "    <m:inline>\r\n" + 
        "       <entry>\r\n" + 
        "            <id>http://services.odata.org/V3/OData/OData.svc/Suppliers(0)</id>\r\n" + 
        "            <category term=\"ODataDemo.Supplier\" "
        + "scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" />\r\n" + 
        "            <link rel=\"edit\" title=\"Supplier\" href=\"Suppliers(0)\" />\r\n" + 
        "            <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Products\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Products\" href=\"Suppliers(0)/Products\" />\r\n" + 
        "            <title type=\"text\">Exotic Liquids</title>\r\n" + 
        "            <updated>2015-01-26T08:57:02Z</updated>\r\n" + 
        "            <author>\r\n" + 
        "                <name />\r\n" + 
        "            </author>\r\n" + 
        "            <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Products\" "
        + "type=\"application/xml\" title=\"Products\" href=\"Suppliers(0)/$links/Products\" />\r\n" + 
        "            <content type=\"application/xml\">\r\n" + 
        "                 <m:properties>\r\n" + 
        "                    <d:ID m:type=\"Edm.Int32\">0</d:ID>\r\n" + 
        "                    <d:Name>Exotic Liquids</d:Name>\r\n" + 
        "                     <d:Address m:type=\"ODataDemo.Address\">\r\n" + 
        "                    <d:Street>NE 228th</d:Street>\r\n" + 
        "                    <d:City>Sammamish</d:City>\r\n" + 
        "                    <d:State>WA</d:State>\r\n" + 
        "                    <d:ZipCode>98074</d:ZipCode>\r\n" + 
        "                    <d:Country>USA</d:Country>\r\n" + 
        "                     </d:Address>\r\n" + 
        "                    <d:Location m:type=\"Edm.GeographyPoint\">\r\n" + 
        "                    <gml:Point gml:srsName=\"http://www.opengis.net/def/crs/EPSG/0/4326\">\r\n" + 
        "                    <gml:pos>47.6316604614258 -122.03547668457</gml:pos>\r\n" + 
        "                    </gml:Point>\r\n" + 
        "                    </d:Location>\r\n" + 
        "                    <d:Concurrency m:type=\"Edm.Int32\">0</d:Concurrency>\r\n" + 
        "                 </m:properties>\r\n" + 
        "            </content>\r\n" + 
        "         </entry>" + 
        "    </m:inline>\r\n" + 
        "    </link>\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/ProductDetail\" "
        + "type=\"application/atom+xml;type=entry\" "
        + "title=\"ProductDetail\" href=\"Products(3)/ProductDetail\" />\r\n" + 
        "    <title type=\"text\">Havina Cola</title>\r\n" + 
        "    <summary type=\"text\">The Original Key Lime Cola</summary>\r\n" + 
        "    <updated>2015-01-26T08:57:02Z</updated>\r\n" + 
        "    <author>\r\n" + 
        "        <name />\r\n" + 
        "    </author>\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Categories\" "
        + "type=\"application/xml\" title=\"Categories\" href=\"Products(3)/$links/Categories\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Supplier\" "
        + "type=\"application/xml\" title=\"Supplier\" href=\"Products(3)/$links/Supplier\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/ProductDetail\" "
        + "type=\"application/xml\" title=\"ProductDetail\" href=\"Products(3)/$links/ProductDetail\" />\r\n" + 
        "    <content type=\"application/xml\">\r\n" + 
        "        <m:properties>\r\n" + 
        "            <d:ID m:type=\"Edm.Int32\">3</d:ID>\r\n" + 
        "            <d:ReleaseDate m:type=\"Edm.DateTime\">2005-10-01T00:00:00</d:ReleaseDate>\r\n" + 
        "            <d:DiscontinuedDate m:type=\"Edm.DateTime\">2006-10-01T00:00:00</d:DiscontinuedDate>\r\n" + 
        "            <d:Rating m:type=\"Edm.Int16\">3</d:Rating>\r\n" + 
        "            <d:Price m:type=\"Edm.Double\">19.9</d:Price>\r\n" + 
        "        </m:properties>\r\n" + 
        "    </content>\r\n" + 
        " </entry>";

    final AtomDeserializer deserializer = new AtomDeserializer(ODataServiceVersion.V30);
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
        + "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" "
        + "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" "
        + "xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" "
        + "xml:base=\"http://services.odata.org/V3/OData/OData.svc/\">\r\n" + 
        "    <id>http://services.odata.org/V3/OData/OData.svc/Products(3)</id>\r\n" + 
        "    <category term=\"ODataDemo.Product\" "
        + "scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" />\r\n" + 
        "    \r\n" + 
        "    <link rel=\"edit\" title=\"Product\" href=\"Products(3)\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Categories\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Categories\" href=\"Products(3)/Categories\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Supplier\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Supplier\" href=\"Products(3)/Supplier\">\r\n" + 
        "    <m:inline>\r\n" + 
        "        <feed>\r\n" + 
        "         </feed>\r\n" + 
        "    </m:inline>\r\n" + 
        "    </link>\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/ProductDetail\" "
        + "type=\"application/atom+xml;type=entry\" "
        +  "title=\"ProductDetail\" href=\"Products(3)/ProductDetail\" />\r\n" + 
        "    <title type=\"text\">Havina Cola</title>\r\n" + 
        "    <summary type=\"text\">The Original Key Lime Cola</summary>\r\n" + 
        "    <updated>2015-01-26T08:57:02Z</updated>\r\n" + 
        "    <author>\r\n" + 
        "        <name />\r\n" + 
        "    </author>\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Categories\" "
        + "type=\"application/xml\" title=\"Categories\" href=\"Products(3)/$links/Categories\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Supplier\" "
        + "type=\"application/xml\" title=\"Supplier\" href=\"Products(3)/$links/Supplier\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/ProductDetail\" "
        + "type=\"application/xml\" title=\"ProductDetail\" href=\"Products(3)/$links/ProductDetail\" />\r\n" + 
        "    <content type=\"application/xml\">\r\n" + 
        "        <m:properties>\r\n" + 
        "            <d:ID m:type=\"Edm.Int32\">3</d:ID>\r\n" + 
        "            <d:ReleaseDate m:type=\"Edm.DateTime\">2005-10-01T00:00:00</d:ReleaseDate>\r\n" + 
        "            <d:DiscontinuedDate m:type=\"Edm.DateTime\">2006-10-01T00:00:00</d:DiscontinuedDate>\r\n" + 
        "            <d:Rating m:type=\"Edm.Int16\">3</d:Rating>\r\n" + 
        "            <d:Price m:type=\"Edm.Double\">19.9</d:Price>\r\n" + 
        "        </m:properties>\r\n" + 
        "    </content>\r\n" + 
        " </entry>";

    final AtomDeserializer deserializer = new AtomDeserializer(ODataServiceVersion.V30);
    final InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
    final ResWrap<Entity> entity = deserializer.toEntity(in);
    
    assertNotNull(entity);
    final EntitySet inlineEntitySet = entity.getPayload().getNavigationLink("Supplier").getInlineEntitySet();
    assertNotNull(inlineEntitySet);
    assertEquals(0, inlineEntitySet.getEntities().size());
  }
  
  @Test
  public void filledInlineEntityCollection() throws Exception {
    final String content = "" + 
        "<entry xmlns=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" "
        + "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" "
        + "xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" "
        + "xml:base=\"http://services.odata.org/V3/OData/OData.svc/\">\r\n" + 
        "    <id>http://services.odata.org/V3/OData/OData.svc/Products(3)</id>\r\n" + 
        "    <category term=\"ODataDemo.Product\" "
        + "scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" />\r\n" + 
        "    \r\n" + 
        "    <link rel=\"edit\" title=\"Product\" href=\"Products(3)\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Categories\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Categories\" href=\"Products(3)/Categories\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Supplier\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Supplier\" href=\"Products(3)/Supplier\">\r\n" + 
        "    <m:inline>\r\n" + 
        "        <feed>\r\n" + 
        "        <entry>\r\n" + 
        "            <id>http://services.odata.org/V3/OData/OData.svc/Suppliers(0)</id>\r\n" + 
        "            <category term=\"ODataDemo.Supplier\" "
        + "scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" />\r\n" + 
        "            <link rel=\"edit\" title=\"Supplier\" href=\"Suppliers(0)\" />\r\n" + 
        "            <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Products\" "
        + "type=\"application/atom+xml;type=feed\" title=\"Products\" href=\"Suppliers(0)/Products\" />\r\n" + 
        "            <title type=\"text\">Exotic Liquids</title>\r\n" + 
        "            <updated>2015-01-26T08:57:02Z</updated>\r\n" + 
        "            <author>\r\n" + 
        "                <name />\r\n" + 
        "            </author>\r\n" + 
        "            <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Products\" "
        + "type=\"application/xml\" title=\"Products\" href=\"Suppliers(0)/$links/Products\" />\r\n" + 
        "            <content type=\"application/xml\">\r\n" + 
        "                 <m:properties>\r\n" + 
        "                    <d:ID m:type=\"Edm.Int32\">0</d:ID>\r\n" + 
        "                    <d:Name>Exotic Liquids</d:Name>\r\n" + 
        "                    <d:Address m:type=\"ODataDemo.Address\">\r\n" + 
        "                    <d:Street>NE 228th</d:Street>\r\n" + 
        "                    <d:City>Sammamish</d:City>\r\n" + 
        "                    <d:State>WA</d:State>\r\n" + 
        "                    <d:ZipCode>98074</d:ZipCode>\r\n" + 
        "                    <d:Country>USA</d:Country>\r\n" + 
        "                     </d:Address>\r\n" + 
        "                    <d:Location m:type=\"Edm.GeographyPoint\">\r\n" + 
        "                    <gml:Point gml:srsName=\"http://www.opengis.net/def/crs/EPSG/0/4326\">\r\n" + 
        "                    <gml:pos>47.6316604614258 -122.03547668457</gml:pos>\r\n" + 
        "                    </gml:Point>\r\n" + 
        "                    </d:Location>\r\n" + 
        "                    <d:Concurrency m:type=\"Edm.Int32\">0</d:Concurrency>\r\n" + 
        "                 </m:properties>\r\n" + 
        "            </content>\r\n" + 
        "         </entry>\r\n" +
        "         </feed>\r\n" + 
        "    </m:inline>\r\n" + 
        "    </link>\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/ProductDetail\" "
        + "type=\"application/atom+xml;type=entry\" "
        + "title=\"ProductDetail\" href=\"Products(3)/ProductDetail\" />\r\n" + 
        "    <title type=\"text\">Havina Cola</title>\r\n" + 
        "    <summary type=\"text\">The Original Key Lime Cola</summary>\r\n" + 
        "    <updated>2015-01-26T08:57:02Z</updated>\r\n" + 
        "    <author>\r\n" + 
        "        <name />\r\n" + 
        "    </author>\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Categories\" "
        + "type=\"application/xml\" title=\"Categories\" href=\"Products(3)/$links/Categories\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/Supplier\" "
        + "type=\"application/xml\" title=\"Supplier\" href=\"Products(3)/$links/Supplier\" />\r\n" + 
        "    <link rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/ProductDetail\" "
        + "type=\"application/xml\" title=\"ProductDetail\" href=\"Products(3)/$links/ProductDetail\" />\r\n" + 
        "    <content type=\"application/xml\">\r\n" + 
        "        <m:properties>\r\n" + 
        "            <d:ID m:type=\"Edm.Int32\">3</d:ID>\r\n" + 
        "            <d:ReleaseDate m:type=\"Edm.DateTime\">2005-10-01T00:00:00</d:ReleaseDate>\r\n" + 
        "            <d:DiscontinuedDate m:type=\"Edm.DateTime\">2006-10-01T00:00:00</d:DiscontinuedDate>\r\n" + 
        "            <d:Rating m:type=\"Edm.Int16\">3</d:Rating>\r\n" + 
        "            <d:Price m:type=\"Edm.Double\">19.9</d:Price>\r\n" + 
        "        </m:properties>\r\n" + 
        "    </content>\r\n" + 
        " </entry>";

    final AtomDeserializer deserializer = new AtomDeserializer(ODataServiceVersion.V30);
    final InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
    final ResWrap<Entity> entity = deserializer.toEntity(in);
    
    assertNotNull(entity);
    final EntitySet inlineEntitySet = entity.getPayload().getNavigationLink("Supplier").getInlineEntitySet();
    assertNotNull(inlineEntitySet);
    assertEquals(1, inlineEntitySet.getEntities().size());
  }
}
