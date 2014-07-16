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
package org.apache.olingo.client.core.edm.v3;

import org.apache.olingo.client.api.edm.xml.CommonFunctionImport;
import org.apache.olingo.client.api.edm.xml.EntityContainer;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.client.api.v3.UnsupportedInV3Exception;
import org.apache.olingo.client.core.edm.AbstractEdmServiceMetadataImpl;
import org.apache.olingo.commons.api.edm.EdmActionImportInfo;
import org.apache.olingo.commons.api.edm.EdmFunctionImportInfo;
import org.apache.olingo.commons.api.edm.EdmSingletonInfo;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmActionImportInfoImpl;
import org.apache.olingo.commons.core.edm.EdmFunctionImportInfoImpl;

import java.util.ArrayList;
import java.util.List;

public class EdmServiceMetadataImpl extends AbstractEdmServiceMetadataImpl {

  private static final ODataServiceVersion SERVICE_VERSION = ODataServiceVersion.V30;

  public EdmServiceMetadataImpl(final List<? extends Schema> xmlSchemas) {
    super(xmlSchemas);
  }

  @Override
  public ODataServiceVersion getDataServiceVersion() {
    return SERVICE_VERSION;
  }

  @Override
  public List<EdmSingletonInfo> getSingletonInfos() {
    throw new UnsupportedInV3Exception();
  }

  @Override
  public List<EdmFunctionImportInfo> getFunctionImportInfos() {
    synchronized (this) {
      if (functionImportInfos == null) {
        functionImportInfos = new ArrayList<EdmFunctionImportInfo>();
        for (Schema schema : xmlSchemas) {
          for (EntityContainer entityContainer : schema.getEntityContainers()) {
            for (CommonFunctionImport functionImport : entityContainer.getFunctionImports()) {
              final FunctionImport _funFunctionImport = (FunctionImport) functionImport;
              if (FunctionImportUtils.canProxyFunction(_funFunctionImport)) {
                functionImportInfos.add(
                        new EdmFunctionImportInfoImpl(entityContainer.getName(), functionImport.getName()));
              }
            }
          }
        }
      }
      return functionImportInfos;
    }
  }

  @Override
  public List<EdmActionImportInfo> getActionImportInfos() {
    synchronized (this) {
      if (actionImportInfos == null) {
        actionImportInfos = new ArrayList<EdmActionImportInfo>();
        for (Schema schema : xmlSchemas) {
          for (EntityContainer entityContainer : schema.getEntityContainers()) {
            for (CommonFunctionImport functionImport : entityContainer.getFunctionImports()) {
              final FunctionImport _funFunctionImport = (FunctionImport) functionImport;
              if (!FunctionImportUtils.canProxyFunction(_funFunctionImport)) {
                actionImportInfos.add(
                        new EdmActionImportInfoImpl(entityContainer.getName(), functionImport.getName()));
              }
            }
          }
        }
      }
      return actionImportInfos;
    }
  }

}
