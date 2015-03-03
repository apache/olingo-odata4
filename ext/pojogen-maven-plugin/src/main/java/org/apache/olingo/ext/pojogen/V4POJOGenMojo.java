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
package org.apache.olingo.ext.pojogen;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

/**
 * POJOs generator.
 */
@Mojo(name = "v4pojoGen", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class V4POJOGenMojo extends AbstractPOJOGenMojo {

  @Override
  protected void createUtility(final Edm edm, final EdmSchema schema, final String basePackage) {
    utility = new V4Utility(edm, schema, basePackage);
  }

  @Override
  protected V4Utility getUtility() {
    return (V4Utility) utility;
  }

  @Override
  protected String getVersion() {
    return ODataServiceVersion.V40.name().toLowerCase();
  }

  @Override
  protected ODataClient getClient() {
    return ODataClientFactory.getClient();
  }
}
