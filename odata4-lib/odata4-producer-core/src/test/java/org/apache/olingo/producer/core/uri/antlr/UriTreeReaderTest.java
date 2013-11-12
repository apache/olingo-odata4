/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.producer.core.uri.antlr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.producer.core.testutil.EdmMock;
import org.apache.olingo.producer.core.uri.UriInfoImpl;
import org.apache.olingo.producer.core.uri.UriPathInfoImpl;
import org.apache.olingo.producer.core.uri.UriTreeReader;
import org.junit.Test;

public class UriTreeReaderTest {

  @Test
  public void testEntitySet() {
    testUri("Employees", UriPathInfoImpl.PathInfoType.entitySet);
    testUri("Employees('1')", UriPathInfoImpl.PathInfoType.entitySet);
    testUri("Employees(EmployeeId='1')", UriPathInfoImpl.PathInfoType.entitySet);

    testUri("Employees('1')/EmployeeName", UriPathInfoImpl.PathInfoType.entitySet);

    testUri("Employees/RefScenario.ManagerType", UriPathInfoImpl.PathInfoType.entitySet);
    testUri("Employees/RefScenario.ManagerType('1')", UriPathInfoImpl.PathInfoType.entitySet);

    testUri("Employees/Location", UriPathInfoImpl.PathInfoType.entitySet);
    testUri("Employees/Location/Country", UriPathInfoImpl.PathInfoType.entitySet);
  }

  @Test
  public void testSingleton() {
    testUri("Company", UriPathInfoImpl.PathInfoType.singleton);
  }

  @Test
  public void testActionImport() {
    testUri("actionImport1", UriPathInfoImpl.PathInfoType.actionImport);
  }

  @Test
  public void testFunctionImport() {
    testUri("MaximalAge", UriPathInfoImpl.PathInfoType.functioncall);
  }

  @Test
  public void testBoundFunctions() {
    testUri("Employees/RefScenario.bf_entity_set_rt_entity(NonBindingParameter='1')",
        UriPathInfoImpl.PathInfoType.boundFunctioncall);
    testUri("Employees('1')/EmployeeName/RefScenario.bf_pprop_rt_entity_set()",
        UriPathInfoImpl.PathInfoType.boundFunctioncall);
    testUri("Company/RefScenario.bf_singleton_rt_entity_set()('1')",
        UriPathInfoImpl.PathInfoType.boundFunctioncall);
    // testUri("Company/RefScenario.bf_singleton_rt_entity_set()('1')/EmployeeName/"
    // +"RefScenario.bf_pprop_rt_entity_set()",
    // UriPathInfoImpl.PathInfoType.boundFunctioncall);
  }

  @Test
  public void testBoundActions() {
    testUri("Employees('1')/RefScenario.ba_entity_rt_pprop", UriPathInfoImpl.PathInfoType.boundActionImport);
    testUri("Employees('1')/EmployeeName/RefScenario.ba_pprop_rt_entity_set",
        UriPathInfoImpl.PathInfoType.boundActionImport);
  }

  @Test
  public void testNavigationFunction() {
    testUri("Employees('1')/ne_Manager", UriPathInfoImpl.PathInfoType.navicationProperty);
    testUri("Teams('1')/nt_Employees('1')", UriPathInfoImpl.PathInfoType.navicationProperty);
    // testUri("Teams('1')/nt_Employees('1')/EmployeeName", UriPathInfoImpl.PathInfoType.navicationProperty);
  }

  private static UriInfoImpl parseUri(final String uri) {
    UriTreeReader reader = new UriTreeReader();
    UriInfoImpl uriInfo = reader.readUri(uri, new EdmMock());
    return uriInfo;
  }

  private static void testUri(final String uri, final UriPathInfoImpl.PathInfoType expectedType) {
    UriInfoImpl uriInfo = parseUri(uri);
    assertNotNull(uriInfo.getLastUriPathInfo());
    assertEquals(expectedType, uriInfo.getLastUriPathInfo().getType());
  }

}
