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
package org.apache.olingo.odata4.client.core.edm.v3;

import java.util.List;
import org.apache.olingo.odata4.client.api.UnsupportedInV3Exception;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmServiceMetadataImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v3.XMLMetadataImpl;
import org.apache.olingo.odata4.commons.api.edm.EdmActionImportInfo;
import org.apache.olingo.odata4.commons.api.edm.EdmSingletonInfo;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;

public class EdmServiceMetadataImpl extends AbstractEdmServiceMetadataImpl {

  private static final ODataServiceVersion SERVICE_VERSION = ODataServiceVersion.V30;

  public EdmServiceMetadataImpl(final XMLMetadataImpl xmlMetadata) {
    super(xmlMetadata);
  }

  @Override
  public String getDataServiceVersion() {
    return SERVICE_VERSION.toString();
  }

  @Override
  public List<EdmSingletonInfo> getSingletonInfos() {
    throw new UnsupportedInV3Exception();
  }

  @Override
  public List<EdmActionImportInfo> getActionImportInfos() {
    throw new UnsupportedInV3Exception();
  }

}
