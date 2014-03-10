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
package org.apache.olingo.odata4.commons.api.edm;

import java.io.InputStream;
import java.util.List;

/**
 * This interface gives access to the metadata of a service, the calculated Data Service Version and an info list of all
 * entity sets, singletons, and function imports inside this EntityDataModel.
 */
public interface EdmServiceMetadata {

  /**
   * @return {@link InputStream} containing the metadata document
   */
  InputStream getMetadata();

  /**
   * @return <b>String</b> data service version of this service
   */
  String getDataServiceVersion();

  /**
   * @return a list of {@link EdmEntitySetInfo} objects inside the data model
   */
  List<EdmEntitySetInfo> getEntitySetInfos();

  /**
   * @return a list of {@link EdmSingletonInfo} objects inside the data model
   */
  List<EdmSingletonInfo> getSingletonInfos();

  /**
   * @return a list of {@link EdmActionImportInfo} objects inside the data model
   */
  List<EdmActionImportInfo> getActionImportInfos();

  /**
   * @return a list of {@link EdmFunctionImportInfo} objects inside the data model
   */
  List<EdmFunctionImportInfo> getFunctionImportInfos();
}
