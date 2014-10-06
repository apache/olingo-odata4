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
package org.apache.olingo.client.core.edm.xml;

import org.apache.olingo.client.api.edm.xml.DataServices;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DataServicesDeserializer.class)
public abstract class AbstractDataServices extends AbstractEdmItem implements DataServices {

  private static final long serialVersionUID = -4400718085220384367L;

  private String dataServiceVersion;

  private String maxDataServiceVersion;

  @Override
  public String getDataServiceVersion() {
    return dataServiceVersion;
  }

  public void setDataServiceVersion(final String version) {
    this.dataServiceVersion = version;
  }

  @Override
  public String getMaxDataServiceVersion() {
    return maxDataServiceVersion;
  }

  public void setMaxDataServiceVersion(final String version) {
    this.maxDataServiceVersion = version;
  }
}
