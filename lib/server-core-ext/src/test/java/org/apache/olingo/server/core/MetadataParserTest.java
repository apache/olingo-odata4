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
package org.apache.olingo.server.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MetadataParserTest {
  final String NS = "Microsoft.OData.SampleService.Models.TripPin";
  final FullQualifiedName NSF = new FullQualifiedName(NS);
  final FullQualifiedName EC = new FullQualifiedName(NS, "DefaultContainer");

  CsdlEdmProvider provider = null;

  ReferenceResolver testReferenceResolver = new ReferenceResolver() {
    @Override
    public InputStream resolveReference(URI uri, String xmlBase) {
      String str = uri.toASCIIString();
      if (str.startsWith("http://localhost/")) {
        try {
          return new FileInputStream("src/test/resources/"+str.substring(17));
        } catch (FileNotFoundException e) {
          return null;
        }
      }
      return null;
    }
  };

  @Before
  public void setUp() throws Exception {
    MetadataParser parser = new MetadataParser();
    provider = (CsdlEdmProvider) parser.buildEdmProvider(new FileReader("src/test/resources/trippin.xml"));
  }

  @Test
  public void testAction() throws ODataException {
    // test action
    List<CsdlAction> actions = provider.getActions(new FullQualifiedName(NS, "ResetDataSource"));
    assertNotNull(actions);
    assertEquals(1, actions.size());
  }

  @Test
  public void testFunction() throws ODataException {
    // test function
    List<CsdlFunction> functions = provider
        .getFunctions(new FullQualifiedName(NS, "GetFavoriteAirline"));
    assertNotNull(functions);
    assertEquals(1, functions.size());
    assertEquals("GetFavoriteAirline", functions.get(0).getName());
    assertTrue(functions.get(0).isBound());
    assertTrue(functions.get(0).isComposable());
    assertEquals(
        "person/Trips/PlanItems/Microsoft.OData.SampleService.Models.TripPin.Flight/Airline",
        functions.get(0).getEntitySetPath());

    List<CsdlParameter> parameters = functions.get(0).getParameters();
    assertNotNull(parameters);
    assertEquals(1, parameters.size());
    assertEquals("person", parameters.get(0).getName());
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.Person",parameters.get(0).getType());
    assertFalse(parameters.get(0).isNullable());

    assertNotNull(functions.get(0).getReturnType());
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.Airline",
        functions.get(0).getReturnType().getType());
    assertFalse(functions.get(0).getReturnType().isNullable());
  }

  @Test
  public void testEnumType() throws ODataException {
    // test enum type
    CsdlEnumType enumType = provider.getEnumType(new FullQualifiedName(NS, "PersonGender"));
    assertNotNull(enumType);
    assertEquals("Male", enumType.getMembers().get(0).getName());
    assertEquals("Female", enumType.getMembers().get(1).getName());
    assertEquals("Unknown", enumType.getMembers().get(2).getName());
    assertEquals("0", enumType.getMembers().get(0).getValue());
    assertEquals("1", enumType.getMembers().get(1).getValue());
    assertEquals("2", enumType.getMembers().get(2).getValue());
  }

  @Test
  public void testEntityType() throws ODataException {
    // test Entity Type
    CsdlEntityType et = provider.getEntityType(new FullQualifiedName(NS, "Photo"));
    assertNotNull(et);
    assertNotNull(et.getKey());
    assertEquals("Id", et.getKey().get(0).getName());
    assertTrue(et.hasStream());
    assertEquals("Id", et.getProperties().get(0).getName());
    assertEquals("Edm.Int64", et.getProperties().get(0).getType());
    assertEquals("Name", et.getProperties().get(1).getName());
    assertEquals("Edm.String", et.getProperties().get(1).getType());
  }

  @Test
  public void testComplexType() throws ODataException {
    // Test Complex Type
    CsdlComplexType ct = provider.getComplexType(new FullQualifiedName(NS, "City"));
    assertNotNull(ct);
    assertEquals(3, ct.getProperties().size());
    CsdlProperty p = ct.getProperties().get(0);
    assertEquals("CountryRegion", p.getName());
    assertEquals("Edm.String", p.getType());
    assertFalse(p.isNullable());

    ct = provider.getComplexType(new FullQualifiedName(NS, "Location"));
    assertNotNull(ct);

    ct = provider.getComplexType(new FullQualifiedName(NS, "EventLocation"));
    assertNotNull(ct);
  }

  @Test
  public void testEntitySet() throws Exception {
    CsdlEntitySet es = provider.getEntitySet(EC, "People");
    assertNotNull(es);
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.Person",es.getType());

    List<CsdlNavigationPropertyBinding> bindings = es.getNavigationPropertyBindings();
    assertNotNull(bindings);
    assertEquals(6, bindings.size());
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.Flight/From", bindings.get(2)
        .getPath());
    assertEquals("Airports", bindings.get(2).getTarget());
  }

  @Test
  public void testFunctionImport() throws Exception {
    CsdlFunctionImport fi = provider.getFunctionImport(EC, "GetNearestAirport");
    assertNotNull(fi);
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.GetNearestAirport", fi.getFunction());
    assertEquals("Airports", fi.getEntitySet());
    assertTrue(fi.isIncludeInServiceDocument());
  }

  @Test
  public void testActionImport() throws Exception {
    CsdlActionImport ai = provider.getActionImport(EC, "ResetDataSource");
    assertNotNull(ai);
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.ResetDataSource", ai.getAction());
    assertNull(ai.getEntitySet());
  }

  @Test
  public void testSingleton() throws Exception {
    CsdlSingleton single = this.provider.getSingleton(EC, "Me");
    assertNotNull(single);

    assertEquals("Microsoft.OData.SampleService.Models.TripPin.Person",single.getType());

    List<CsdlNavigationPropertyBinding> bindings = single.getNavigationPropertyBindings();
    assertNotNull(bindings);
    assertEquals(6, bindings.size());
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.Flight/From", bindings.get(2).getPath());
    assertEquals("Airports", bindings.get(2).getTarget());
  }
  
  @Test
  public void testParsingWithNoFormat() throws Exception {
    MetadataParser parser = new MetadataParser();
    provider = (CsdlEdmProvider) parser.buildEdmProvider(new FileReader("src/test/resources/skip-annotation.xml"));
  } 
  
  @Test
  public void testReferenceLoad() throws Exception {
    MetadataParser parser = new MetadataParser();
    parser.recursivelyLoadReferences(false);
    parser.referenceResolver(this.testReferenceResolver);
    provider = (CsdlEdmProvider) parser.buildEdmProvider(new FileReader("src/test/resources/test.xml"));
  }

  @Test
  public void testReferenceLoadRecursively() throws Exception {
    MetadataParser parser = new MetadataParser();
    parser.recursivelyLoadReferences(true);
    parser.referenceResolver(testReferenceResolver);
    SchemaBasedEdmProvider providerTest = parser.buildEdmProvider(new FileReader("src/test/resources/test.xml"));

    Assert.assertNotNull(providerTest.getSchema("Microsoft.OData.SampleService.Models.TripPin", false));

    Assert.assertNull(providerTest.getSchema("org.apache.olingo.a", false));
    Assert.assertNull(providerTest.getSchema("org.apache.olingo.b", false));

    Assert.assertNotNull(providerTest.getSchema("org.apache.olingo.a", true));
    Assert.assertNotNull(providerTest.getSchema("org.apache.olingo.b", true));
  }

  @Test
  public void testCircleReferenceShouldNotStackOverflow() throws Exception {
    MetadataParser parser = new MetadataParser();
    parser.recursivelyLoadReferences(true);
    parser.referenceResolver(testReferenceResolver);
    SchemaBasedEdmProvider providerTest = parser.buildEdmProvider(new FileReader("src/test/resources/test.xml"));

    Assert.assertNull(providerTest.getSchema("Not Found", true));


  }

  @Test
  public void testLoadCoreVocabulary() throws Exception {
    MetadataParser parser = new MetadataParser();
    parser.implicitlyLoadCoreVocabularies(true);
    parser.referenceResolver(testReferenceResolver);
    SchemaBasedEdmProvider provider = parser.buildEdmProvider(new FileReader("src/test/resources/test.xml"));

    Assert.assertNotNull(provider.getVocabularySchema("Org.OData.Core.V1"));
    Assert.assertNotNull(provider.getSchema("Org.OData.Core.V1"));

  }
}
