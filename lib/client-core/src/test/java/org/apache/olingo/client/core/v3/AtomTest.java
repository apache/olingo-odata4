/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.v3;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.client.core.AtomLinksQualifier;
import org.custommonkey.xmlunit.Diff;
import org.junit.Test;

public class AtomTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  protected ODataPubFormat getODataPubFormat() {
    return ODataPubFormat.ATOM;
  }

  protected ODataFormat getODataFormat() {
    return ODataFormat.XML;
  }

  private String cleanup(final String input) throws Exception {
    final TransformerFactory factory = TransformerFactory.newInstance();
    final Source xslt = new StreamSource(getClass().getResourceAsStream("atom_cleanup.xsl"));
    final Transformer transformer = factory.newTransformer(xslt);

    final StringWriter result = new StringWriter();
    transformer.transform(new StreamSource(new ByteArrayInputStream(input.getBytes())), new StreamResult(result));
    return result.toString();
  }

  protected void assertSimilar(final String filename, final String actual) throws Exception {
    final Diff diff = new Diff(cleanup(IOUtils.toString(getClass().getResourceAsStream(filename))), actual);
    diff.overrideElementQualifier(new AtomLinksQualifier());
    assertTrue(diff.similar());
  }

  protected void feed(final String filename, final ODataPubFormat format) throws Exception {
    final StringWriter writer = new StringWriter();
    getClient().getSerializer().feed(getClient().getDeserializer().toFeed(
            getClass().getResourceAsStream("Customer." + getSuffix(format)), format).getObject(), writer);

    assertSimilar("Customer." + getSuffix(format), writer.toString());
  }

  @Test
  public void feeds() throws Exception {
    feed("Customer", getODataPubFormat());
  }

  protected void entry(final String filename, final ODataPubFormat format) throws Exception {
    final StringWriter writer = new StringWriter();
    getClient().getSerializer().entry(getClient().getDeserializer().toEntry(
            getClass().getResourceAsStream(filename + "." + getSuffix(format)), format).getObject(), writer);

    assertSimilar(filename + "." + getSuffix(format), writer.toString());
  }

  @Test
  public void entries() throws Exception {
    entry("AllGeoTypesSet_-5", getODataPubFormat());
    entry("AllGeoTypesSet_-8", getODataPubFormat());
    entry("Car_16", getODataPubFormat());
    entry("ComputerDetail_-10", getODataPubFormat());
    entry("Customer_-10", getODataPubFormat());
    entry("Products_1", getODataPubFormat());
    entry("PersonDetails_0_Person", getODataPubFormat());
    entry("Products_0_Categories", getODataPubFormat());
  }

  protected void property(final String filename, final ODataFormat format) throws Exception {
    final StringWriter writer = new StringWriter();
    getClient().getSerializer().property(getClient().getDeserializer().
            toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(format)), format).getObject(), writer);

    assertSimilar(filename + "." + getSuffix(format), writer.toString());
  }

  @Test
  public void properties() throws Exception {
    property("Products_1_DiscontinuedDate", getODataFormat());
    property("AllGeoTypesSet_-10_GeogLine", getODataFormat());
    property("AllGeoTypesSet_-10_GeogPoint", getODataFormat());
    property("AllGeoTypesSet_-10_Geom", getODataFormat());
    property("AllGeoTypesSet_-3_GeomMultiPolygon", getODataFormat());
    property("AllGeoTypesSet_-5_GeogCollection", getODataFormat());
    property("AllGeoTypesSet_-5_GeogPolygon", getODataFormat());
    property("AllGeoTypesSet_-6_GeomMultiLine", getODataFormat());
    property("AllGeoTypesSet_-7_GeomMultiPoint", getODataFormat());
    property("AllGeoTypesSet_-8_GeomCollection", getODataFormat());
    property("Customer_-10_BackupContactInfo", getODataFormat());
    property("Customer_-10_PrimaryContactInfo", getODataFormat());
    property("MessageAttachment_guid'1126a28b-a4af-4bbd-bf0a-2b2c22635565'_Attachment", getODataFormat());
    property("MessageAttachment_guid'1126a28b-a4af-4bbd-bf0a-2b2c22635565'_AttachmentId", getODataFormat());
    property("Product_-10_ComplexConcurrency_QueriedDateTime", getODataFormat());
    property("Product_-10_Dimensions_Width", getODataFormat());
  }
}
