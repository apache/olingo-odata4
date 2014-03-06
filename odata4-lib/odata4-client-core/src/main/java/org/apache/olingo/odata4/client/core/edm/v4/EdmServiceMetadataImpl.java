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
package org.apache.olingo.odata4.client.core.edm.v4;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.xml.CommonFunctionImport;
import org.apache.olingo.odata4.client.api.edm.xml.Schema;
import org.apache.olingo.odata4.client.api.edm.xml.v4.ActionImport;
import org.apache.olingo.odata4.client.api.edm.xml.v4.EntityContainer;
import org.apache.olingo.odata4.client.api.edm.xml.v4.Singleton;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmServiceMetadataImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v4.XMLMetadataImpl;
import org.apache.olingo.odata4.commons.api.edm.EdmActionImportInfo;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImportInfo;
import org.apache.olingo.odata4.commons.api.edm.EdmSingletonInfo;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.odata4.commons.core.edm.EdmActionImportInfoImpl;
import org.apache.olingo.odata4.commons.core.edm.EdmFunctionImportInfoImpl;
import org.apache.olingo.odata4.commons.core.edm.EdmSingletonInfoImpl;

public class EdmServiceMetadataImpl extends AbstractEdmServiceMetadataImpl {

  private static final ODataServiceVersion SERVICE_VERSION = ODataServiceVersion.V40;

  private List<EdmSingletonInfo> singletonInfos;

  private List<EdmActionImportInfo> actionImportInfos;

  public EdmServiceMetadataImpl(final XMLMetadataImpl xmlMetadata) {
    super(xmlMetadata);
  }

  @Override
  public String getDataServiceVersion() {
    return SERVICE_VERSION.toString();
  }

  @Override
  public List<EdmSingletonInfo> getSingletonInfos() {
    synchronized (this) {
      if (singletonInfos == null) {
        singletonInfos = new ArrayList<EdmSingletonInfo>();
        for (Schema schema : xmlMetadata.getSchemas()) {
          final EntityContainer entityContainer = (EntityContainer) schema.getDefaultEntityContainer();
          for (Singleton singleton : entityContainer.getSingletons()) {
            singletonInfos.add(new EdmSingletonInfoImpl(entityContainer.getName(), singleton.getName()));
          }
        }
      }
      return singletonInfos;
    }
  }

  @Override
  public List<EdmFunctionImportInfo> getFunctionImportInfos() {
    synchronized (this) {
      if (functionImportInfos == null) {
        functionImportInfos = new ArrayList<EdmFunctionImportInfo>();
        for (Schema schema : xmlMetadata.getSchemas()) {
          for (org.apache.olingo.odata4.client.api.edm.xml.EntityContainer entityContainer : schema.getEntityContainers()) {
            for (CommonFunctionImport functionImport : entityContainer.getFunctionImports()) {
              functionImportInfos.add(
                      new EdmFunctionImportInfoImpl(entityContainer.getName(), functionImport.getName()));
            }
          }
        }
      }
    }
    return functionImportInfos;
  }

  @Override
  public List<EdmActionImportInfo> getActionImportInfos() {
    synchronized (this) {
      if (actionImportInfos == null) {
        actionImportInfos = new ArrayList<EdmActionImportInfo>();
        for (Schema schema : xmlMetadata.getSchemas()) {
          final EntityContainer entityContainer = (EntityContainer) schema.getDefaultEntityContainer();
          for (ActionImport actionImport : entityContainer.getActionImports()) {
            actionImportInfos.add(new EdmActionImportInfoImpl(entityContainer.getName(), actionImport.getName()));
          }
        }
      }
      return actionImportInfos;
    }
  }
}
