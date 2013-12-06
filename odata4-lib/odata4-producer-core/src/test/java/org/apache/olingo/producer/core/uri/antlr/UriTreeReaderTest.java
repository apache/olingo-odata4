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

import org.apache.olingo.odata4.producer.api.uri.UriPathInfoKind;
import org.apache.olingo.odata4.producer.core.testutil.UriResourcePathValidator;

public class UriTreeReaderTest {
  UriResourcePathValidator test = null;

  public UriTreeReaderTest() {
    test = new UriResourcePathValidator();
    // test.setEdm(new EdmIm)
  }

  /*
   * @Test
   * public void testShortUris() {
   * test.run("$batch").isKind(UriInfoKind.batch);
   * test.run("$all").isKind(UriInfoKind.all);
   * test.run("$crossjoin(abc)").isKind(UriInfoKind.crossjoin);
   * }
   */

  // @Test
  public void testEntitySet() {
    test.run("Employees").isUriPathInfoKind(UriPathInfoKind.entitySet);

    test.run("Employees").isUriPathInfoKind(UriPathInfoKind.entitySet);
    test.run("Employees('1')").isUriPathInfoKind(UriPathInfoKind.entitySet);
    test.run("Employees(EmployeeId='1')").isUriPathInfoKind(UriPathInfoKind.entitySet);

    test.run("Employees('1')/EmployeeName").isUriPathInfoKind(UriPathInfoKind.entitySet);

    test.run("Employees/RefScenario.ManagerType").isUriPathInfoKind(UriPathInfoKind.entitySet);
    test.run("Employees/RefScenario.ManagerType('1')").isUriPathInfoKind(UriPathInfoKind.entitySet);

    test.run("Employees/Location").isUriPathInfoKind(UriPathInfoKind.entitySet);
    test.run("Employees/Location/Country").isUriPathInfoKind(UriPathInfoKind.entitySet);
  }
  /*
   * //@Test
   * public void testSingleton() {
   * test.run("Company").isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.singleton);
   * }
   */
  /*
   * //@Test
   * public void testActionImport() {
   * test.run("actionImport1").isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.actionImport);
   * }
   */
  /*
   * //@Test
   * public void testFunctionImport() {
   * test.run("MaximalAge").isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.functioncall);
   * }
   */
  /*
   * //@Test
   * public void testBoundFunctions() {
   * 
   * test.run("Employees/RefScenario.bf_entity_set_rt_entity(NonBindingParameter='1')").isUriPathInfoKind(
   * UriPathInfoImpl.UriPathInfoKind.boundFunctioncall);
   * test.run("Employees('1')/EmployeeName/RefScenario.bf_pprop_rt_entity_set()").isUriPathInfoKind(
   * UriPathInfoImpl.UriPathInfoKind.boundFunctioncall);
   * test.run("Company/RefScenario.bf_singleton_rt_entity_set()('1')").isUriPathInfoKind(
   * UriPathInfoImpl.UriPathInfoKind.boundFunctioncall);
   * // testUri("Company/RefScenario.bf_singleton_rt_entity_set()('1')/EmployeeName/"
   * // +"RefScenario.bf_pprop_rt_entity_set()",
   * // UriPathInfoImpl.UriPathInfoKind.boundFunctioncall);
   * }
   */
  /*
   * //@Test
   * public void testBoundActions() {
   * test.run("Employees('1')/RefScenario.ba_entity_rt_pprop")
   * .isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.boundActionImport);
   * test.run("Employees('1')/EmployeeName/RefScenario.ba_pprop_rt_entity_set").isUriPathInfoKind(
   * UriPathInfoImpl.UriPathInfoKind.boundActionImport);
   * }
   */
  /*
   * //@Test
   * public void testNavigationFunction() {
   * test.run("Employees('1')/ne_Manager").isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.navicationProperty);
   * test.run("Teams('1')/nt_Employees('1')").isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.navicationProperty);
   * // testUri("Teams('1')/nt_Employees('1')/EmployeeName", UriPathInfoImpl.UriPathInfoKind.navicationProperty);
   * }
   */

}
