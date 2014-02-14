/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.performance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msopentech.odatajclient.engine.AbstractTest;
import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ResourceFactory;
import com.msopentech.odatajclient.engine.data.impl.v3.AtomEntry;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONEntry;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.MethodSorters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

@BenchmarkOptions(warmupRounds = 25, benchmarkRounds = 50)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BasicPerfTest extends AbstractTest {

    private static final Map<ODataPubFormat, String> input = new HashMap<ODataPubFormat, String>(2);

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @BeforeClass
    public static void setInput() {
        try {
            input.put(ODataPubFormat.ATOM,
                    IOUtils.toString(BasicPerfTest.class.getResourceAsStream("../Customer_-10.xml")));
            input.put(ODataPubFormat.JSON,
                    IOUtils.toString(BasicPerfTest.class.getResourceAsStream("../Customer_-10.json")));
        } catch (Exception e) {
            fail("Could not load sample file");
        }
    }

    protected ODataV3Client getClient() {
        return v3Client;
    }

    @Test
    public void readAtomViaLowerlevelLibs() throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final Document entry = builder.parse(IOUtils.toInputStream(input.get(ODataPubFormat.ATOM)));
        assertNotNull(entry);
        entry.getDocumentElement().normalize();
    }

    @Test
    public void readJSONViaLowerlevelLibs() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        final JsonNode entry = mapper.readTree(IOUtils.toInputStream(input.get(ODataPubFormat.JSON)));
        assertNotNull(entry);
    }

    private void readViaODataJClient(final ODataPubFormat format) {
        final ODataEntity entity = getClient().getBinder().getODataEntity(
                getClient().getDeserializer().toEntry(IOUtils.toInputStream(input.get(format)),
                        ResourceFactory.entryClassForFormat(format)));
        assertNotNull(entity);
    }

    @Test
    public void readAtomViaOdataJClient() {
        readViaODataJClient(ODataPubFormat.ATOM);
    }

    @Test
    public void readJSONViaOdataJClient() {
        readViaODataJClient(ODataPubFormat.JSON);
    }

    @Test
    public void writeAtomViaLowerlevelLibs()
            throws ParserConfigurationException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Element entry = doc.createElement("entry");
        entry.setAttribute("xmlns", "http://www.w3.org/2005/Atom");
        entry.setAttribute("xmlns:m", "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata");
        entry.setAttribute("xmlns:d", "http://schemas.microsoft.com/ado/2007/08/dataservices");
        entry.setAttribute("xmlns:gml", "http://www.opengis.net/gml");
        entry.setAttribute("xmlns:georss", "http://www.georss.org/georss");
        doc.appendChild(entry);

        final Element category = doc.createElement("category");
        category.setAttribute("term", "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");
        category.setAttribute("scheme", "http://schemas.microsoft.com/ado/2007/08/dataservices/scheme");
        entry.appendChild(category);

        final Element properties = doc.createElement("m:properties");
        entry.appendChild(properties);

        final Element name = doc.createElement("d:Name");
        name.setAttribute("m:type", "Edm.String");
        name.appendChild(doc.createTextNode("A name"));
        properties.appendChild(name);

        final Element customerId = doc.createElement("d:CustomerId");
        customerId.setAttribute("m:type", "Edm.Int32");
        customerId.appendChild(doc.createTextNode("0"));
        properties.appendChild(customerId);

        final Element bci = doc.createElement("d:BackupContactInfo");
        bci.setAttribute("m:type", "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)");
        properties.appendChild(bci);

        final Element topelement = doc.createElement("d:element");
        topelement.setAttribute("m:type", "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails");
        bci.appendChild(topelement);

        final Element altNames = doc.createElement("d:AlternativeNames");
        altNames.setAttribute("m:type", "Collection(Edm.String)");
        topelement.appendChild(altNames);

        final Element element1 = doc.createElement("d:element");
        element1.setAttribute("m:type", "Edm.String");
        element1.appendChild(doc.createTextNode("myname"));
        altNames.appendChild(element1);

        final Element emailBag = doc.createElement("d:EmailBag");
        emailBag.setAttribute("m:type", "Collection(Edm.String)");
        topelement.appendChild(emailBag);

        final Element element2 = doc.createElement("d:element");
        element2.setAttribute("m:type", "Edm.String");
        element2.appendChild(doc.createTextNode("myname@mydomain.com"));
        emailBag.appendChild(element2);

        final Element contactAlias = doc.createElement("d:ContactAlias");
        contactAlias.setAttribute("m:type", "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases");
        topelement.appendChild(contactAlias);

        final Element altNames2 = doc.createElement("d:AlternativeNames");
        altNames2.setAttribute("m:type", "Collection(Edm.String)");
        contactAlias.appendChild(altNames2);

        final Element element3 = doc.createElement("d:element");
        element3.setAttribute("m:type", "Edm.String");
        element3.appendChild(doc.createTextNode("myAlternativeName"));
        altNames2.appendChild(element3);

        final StringWriter writer = new StringWriter();

        final DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
        final DOMImplementationLS impl = (DOMImplementationLS) reg.getDOMImplementation("LS");
        final LSSerializer serializer = impl.createLSSerializer();
        final LSOutput lso = impl.createLSOutput();
        lso.setCharacterStream(writer);
        serializer.write(doc, lso);

        assertFalse(writer.toString().isEmpty());
    }

    @Test
    public void writeJSONViaLowerlevelLibs() throws IOException {
        final StringWriter writer = new StringWriter();

        final ObjectMapper mapper = new ObjectMapper();

        final JsonGenerator jgen = mapper.getFactory().createGenerator(writer);

        jgen.writeStartObject();

        jgen.writeStringField("odata.type", "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");

        jgen.writeStringField("Name@odata.type", "Edm.String");
        jgen.writeStringField("Name", "A name");

        jgen.writeStringField("CustomerId@odata.type", "Edm.Int32");
        jgen.writeNumberField("CustomerId", 0);

        jgen.writeArrayFieldStart("BackupContactInfo");

        jgen.writeStartObject();

        jgen.writeArrayFieldStart("AlternativeNames");
        jgen.writeString("myname");
        jgen.writeEndArray();

        jgen.writeArrayFieldStart("EmailBag");
        jgen.writeString("myname@mydomain.com");
        jgen.writeEndArray();

        jgen.writeObjectFieldStart("ContactAlias");
        jgen.writeStringField("odata.type", "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases");
        jgen.writeArrayFieldStart("AlternativeNames");
        jgen.writeString("myAlternativeName");
        jgen.writeEndArray();
        jgen.writeEndObject();

        jgen.writeEndObject();

        jgen.writeEndArray();

        jgen.writeEndObject();

        jgen.flush();

        assertFalse(writer.toString().isEmpty());
    }

    private ODataEntity sampleODataEntity() throws IOException {
        final ODataEntity entity = getClient().getObjectFactory().
                newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");

        // add name attribute
        entity.addProperty(getClient().getObjectFactory().newPrimitiveProperty("Name",
                getClient().getPrimitiveValueBuilder().setText("A name").setType(
                        EdmSimpleType.String).build()));

        // add key attribute
        entity.addProperty(getClient().getObjectFactory().newPrimitiveProperty("CustomerId",
                getClient().getPrimitiveValueBuilder().setText("0").setType(EdmSimpleType.Int32).
                build()));

        // add BackupContactInfo attribute (collection)
        final ODataCollectionValue bciv = new ODataCollectionValue(
                "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)");
        entity.addProperty(getClient().getObjectFactory().newCollectionProperty("BackupContactInfo", bciv));

        // add BackupContactInfo.ContactDetails attribute (complex)
        final ODataComplexValue contactDetails = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails");
        bciv.add(contactDetails);

        // add BackupContactInfo.ContactDetails.AlternativeNames attribute (collection)
        final ODataCollectionValue altNamesValue = new ODataCollectionValue("Collection(Edm.String)");
        altNamesValue.add(getClient().getPrimitiveValueBuilder().
                setText("myname").setType(EdmSimpleType.String).build());
        contactDetails.add(getClient().getObjectFactory().newCollectionProperty("AlternativeNames", altNamesValue));

        // add BackupContactInfo.ContactDetails.EmailBag attribute (collection)
        final ODataCollectionValue emailBagValue = new ODataCollectionValue("Collection(Edm.String)");
        emailBagValue.add(getClient().getPrimitiveValueBuilder().
                setText("myname@mydomain.com").setType(EdmSimpleType.String).build());
        contactDetails.add(getClient().getObjectFactory().newCollectionProperty("EmailBag", emailBagValue));

        // add BackupContactInfo.ContactDetails.ContactAlias attribute (complex)
        final ODataComplexValue contactAliasValue = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases");
        contactDetails.add(getClient().getObjectFactory().newComplexProperty("ContactAlias", contactAliasValue));

        // add BackupContactInfo.ContactDetails.ContactAlias.AlternativeNames attribute (collection)
        final ODataCollectionValue aanv = new ODataCollectionValue("Collection(Edm.String)");
        aanv.add(getClient().getPrimitiveValueBuilder().
                setText("myAlternativeName").setType(EdmSimpleType.String).build());
        contactAliasValue.add(getClient().getObjectFactory().newCollectionProperty("AlternativeNames", aanv));

        return entity;
    }

    @Test
    public void writeAtomViaOdataJClient() throws IOException {
        final StringWriter writer = new StringWriter();
        getClient().getSerializer().entry(
                getClient().getBinder().getEntry(sampleODataEntity(), AtomEntry.class, true), writer);
        assertFalse(writer.toString().isEmpty());
    }

    @Test
    public void writeJSONViaOdataJClient() throws IOException {
        final StringWriter writer = new StringWriter();
        getClient().getSerializer().entry(
                getClient().getBinder().getEntry(sampleODataEntity(), JSONEntry.class, true), writer);
        assertFalse(writer.toString().isEmpty());
    }
}
