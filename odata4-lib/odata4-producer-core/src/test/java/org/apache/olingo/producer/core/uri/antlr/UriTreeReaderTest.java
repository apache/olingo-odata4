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
import org.apache.olingo.producer.core.testutil.UriResourcePathValidator;
import org.apache.olingo.producer.core.uri.UriInfoImpl;
import org.apache.olingo.producer.core.uri.UriPathInfoImpl;
import org.apache.olingo.producer.core.uri.UriParserImpl;
import org.junit.Test;

public class UriTreeReaderTest {
  UriResourcePathValidator test = null;

  public UriTreeReaderTest() {
    test = new UriResourcePathValidator();
    //test.setEdm(new EdmIm)
  }

  //@Test
  public void testEntitySet() {
    test.run("Employees").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);

    test.run("Employees").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);
    test.run("Employees('1')").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);
    test.run("Employees(EmployeeId='1')").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);

    test.run("Employees('1')/EmployeeName").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);

    test.run("Employees/RefScenario.ManagerType").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);
    test.run("Employees/RefScenario.ManagerType('1')").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);

    test.run("Employees/Location").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);
    test.run("Employees/Location/Country").isPathInfoType(UriPathInfoImpl.PathInfoType.entitySet);
  }

  //@Test
  public void testSingleton() {
    test.run("Company").isPathInfoType(UriPathInfoImpl.PathInfoType.singleton);
  }

  //@Test
  public void testActionImport() {
    test.run("actionImport1").isPathInfoType(UriPathInfoImpl.PathInfoType.actionImport);
  }

  //@Test
  public void testFunctionImport() {
    test.run("MaximalAge").isPathInfoType(UriPathInfoImpl.PathInfoType.functioncall);
  }

  //@Test
  public void testBoundFunctions() {

    test.run("Employees/RefScenario.bf_entity_set_rt_entity(NonBindingParameter='1')").isPathInfoType(
        UriPathInfoImpl.PathInfoType.boundFunctioncall);
    test.run("Employees('1')/EmployeeName/RefScenario.bf_pprop_rt_entity_set()").isPathInfoType(
        UriPathInfoImpl.PathInfoType.boundFunctioncall);
    test.run("Company/RefScenario.bf_singleton_rt_entity_set()('1')").isPathInfoType(
        UriPathInfoImpl.PathInfoType.boundFunctioncall);
    // testUri("Company/RefScenario.bf_singleton_rt_entity_set()('1')/EmployeeName/"
    // +"RefScenario.bf_pprop_rt_entity_set()",
    // UriPathInfoImpl.PathInfoType.boundFunctioncall);
  }

  //@Test
  public void testBoundActions() {
    test.run("Employees('1')/RefScenario.ba_entity_rt_pprop")
        .isPathInfoType(UriPathInfoImpl.PathInfoType.boundActionImport);
    test.run("Employees('1')/EmployeeName/RefScenario.ba_pprop_rt_entity_set").isPathInfoType(
        UriPathInfoImpl.PathInfoType.boundActionImport);
  }

  //@Test
  public void testNavigationFunction() {
    test.run("Employees('1')/ne_Manager").isPathInfoType(UriPathInfoImpl.PathInfoType.navicationProperty);
    test.run("Teams('1')/nt_Employees('1')").isPathInfoType(UriPathInfoImpl.PathInfoType.navicationProperty);
    // testUri("Teams('1')/nt_Employees('1')/EmployeeName", UriPathInfoImpl.PathInfoType.navicationProperty);
  }

}
