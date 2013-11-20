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
package org.apache.olingo.producer.core.uri;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.producer.api.uri.UriInfo;
import org.apache.olingo.producer.api.uri.UriInfoBatch;
import org.apache.olingo.producer.api.uri.UriInfoKind;
import org.apache.olingo.producer.api.uri.UriPathInfo;

public class UriInfoImpl implements UriInfo, UriInfoBatch {
  private UriInfoKind kind;
  private List<UriPathInfo> uriPathInfos = new ArrayList<UriPathInfo>();

  public UriInfoKind getKind() {
    return kind;
  }

  public UriInfoImpl setKind(UriInfoKind kind) {
    this.kind = kind;
    return this;
  }

  public void addUriPathInfo(UriPathInfo uriPathInfo) {
    uriPathInfos.add(uriPathInfo);
  }
  
  /*
  private Edm edm = null;
  private List<UriPathInfoImpl> pathInfos = new ArrayList<UriPathInfoImpl>();

  public Edm getEdm() {
    return edm;
  }

  public void addUriPathInfo(final UriPathInfoImpl uriPathInfoImpl) {
    pathInfos.add(uriPathInfoImpl);
  }

  public UriPathInfoImpl getLastUriPathInfo() {
    if (!pathInfos.isEmpty()) {
      return pathInfos.get(pathInfos.size() - 1);
    } else {
      return null;
    }
  }*/

}
