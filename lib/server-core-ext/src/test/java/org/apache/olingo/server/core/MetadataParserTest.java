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

import java.io.FileReader;
import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.ActionImport;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.api.edm.provider.Function;
import org.apache.olingo.server.api.edm.provider.FunctionImport;
import org.apache.olingo.server.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.server.api.edm.provider.Parameter;
import org.apache.olingo.server.api.edm.provider.Property;
import org.apache.olingo.server.api.edm.provider.Singleton;
import org.junit.Before;
import org.junit.Test;

public class MetadataParserTest {
  final String NS = "Microsoft.OData.SampleService.Models.TripPin";
  final FullQualifiedName NSF = new FullQualifiedName(NS);

  EdmProvider provider = null;

  @Before
  public void setUp() throws Exception {
    MetadataParser parser = new MetadataParser();
    provider = parser.buildEdmProvider(new FileReader("src/test/resources/trippin.xml"));
  }

  @Test
  public void testAction() throws ODataException {
    // test action
    List<Action> actions = provider.getActions(new FullQualifiedName(NS, "ResetDataSource"));
    assertNotNull(actions);
    assertEquals(1, actions.size());
  }

  @Test
  public void testFunction() throws ODataException {
    // test function
    List<Function> functions = provider
        .getFunctions(new FullQualifiedName(NS, "GetFavoriteAirline"));
    assertNotNull(functions);
    assertEquals(1, functions.size());
    assertEquals("GetFavoriteAirline", functions.get(0).getName());
    assertTrue(functions.get(0).isBound());
    assertTrue(functions.get(0).isComposable());
    assertEquals(
        "person/Trips/PlanItems/Microsoft.OData.SampleService.Models.TripPin.Flight/Airline",
        functions.get(0).getEntitySetPath().getPath());

    List<Parameter> parameters = functions.get(0).getParameters();
    assertNotNull(parameters);
    assertEquals(1, parameters.size());
    assertEquals("person", parameters.get(0).getName());
    assertEquals(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Person"),
        parameters.get(0).getType());
    assertFalse(parameters.get(0).getNullable());

    assertNotNull(functions.get(0).getReturnType());
    assertEquals(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Airline"),
        functions.get(0).getReturnType().getType());
    assertFalse(functions.get(0).getReturnType().getNullable());
  }

  @Test
  public void testEnumType() throws ODataException {
    // test enum type
    EnumType enumType = provider.getEnumType(new FullQualifiedName(NS, "PersonGender"));
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
    EntityType et = provider.getEntityType(new FullQualifiedName(NS, "Photo"));
    assertNotNull(et);
    assertNotNull(et.getKey());
    assertEquals("Id", et.getKey().get(0).getPropertyName());
    assertTrue(et.hasStream());
    assertEquals("Id", et.getProperties().get(0).getName());
    assertEquals("Edm.Int64", et.getProperties().get(0).getType().getFullQualifiedNameAsString());
    assertEquals("Name", et.getProperties().get(1).getName());
    assertEquals("Edm.String", et.getProperties().get(1).getType().getFullQualifiedNameAsString());
  }

  @Test
  public void testComplexType() throws ODataException {
    // Test Complex Type
    ComplexType ct = provider.getComplexType(new FullQualifiedName(NS, "City"));
    assertNotNull(ct);
    assertEquals(3, ct.getProperties().size());
    Property p = ct.getProperties().get(0);
    assertEquals("CountryRegion", p.getName());
    assertEquals("Edm.String", p.getType().getFullQualifiedNameAsString());
    assertEquals(false, p.getNullable());

    ct = provider.getComplexType(new FullQualifiedName(NS, "Location"));
    assertNotNull(ct);

    ct = provider.getComplexType(new FullQualifiedName(NS, "EventLocation"));
    assertNotNull(ct);
  }

  @Test
  public void testEntitySet() throws Exception {
    EntitySet es = provider.getEntitySet(NSF, "People");
    assertNotNull(es);
    assertEquals(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Person"),
        es.getType());

    List<NavigationPropertyBinding> bindings = es.getNavigationPropertyBindings();
    assertNotNull(bindings);
    assertEquals(6, bindings.size());
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.Flight/From", bindings.get(2)
        .getPath());
    assertEquals("Airports", bindings.get(2).getTarget().getTargetName());
  }

  @Test
  public void testFunctionImport() throws Exception {
    FunctionImport fi = provider.getFunctionImport(NSF, "GetNearestAirport");
    assertNotNull(fi);
    assertEquals(new FullQualifiedName(
        "Microsoft.OData.SampleService.Models.TripPin.GetNearestAirport"), fi.getFunction());
    assertEquals("Airports", fi.getEntitySet().getTargetName());
    assertTrue(fi.isIncludeInServiceDocument());
  }

  @Test
  public void testActionImport() throws Exception {
    ActionImport ai = provider.getActionImport(NSF, "ResetDataSource");
    assertNotNull(ai);
    assertEquals(new FullQualifiedName(
        "Microsoft.OData.SampleService.Models.TripPin.ResetDataSource"), ai.getAction());
    assertNull(ai.getEntitySet());
  }

  @Test
  public void testSingleton() throws Exception {
    Singleton single = this.provider.getSingleton(NSF, "Me");
    assertNotNull(single);

    assertEquals(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Person"),
        single.getType());

    List<NavigationPropertyBinding> bindings = single.getNavigationPropertyBindings();
    assertNotNull(bindings);
    assertEquals(6, bindings.size());
    assertEquals("Microsoft.OData.SampleService.Models.TripPin.Flight/From", bindings.get(2).getPath());
    assertEquals("Airports", bindings.get(2).getTarget().getTargetName());

  }
}
