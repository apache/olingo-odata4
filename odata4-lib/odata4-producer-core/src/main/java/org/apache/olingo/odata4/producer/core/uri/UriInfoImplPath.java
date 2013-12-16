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
package org.apache.olingo.odata4.producer.core.uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.core.uri.expression.Expression;

public class UriInfoImplPath extends UriInfoImpl {

  private List<UriPathInfoImpl> pathInfos = new ArrayList<UriPathInfoImpl>();

  private Expression spFilter;

  public UriInfoImplPath() {
    this.setKind(UriInfoKind.path);
  }

  public UriInfoImpl addPathInfo(UriPathInfoImpl pathInfo) {
    pathInfos.add(pathInfo);
    return this;
  }

  public UriPathInfoImpl getLastUriPathInfo() {
    if (pathInfos.size() > 0) {
      return pathInfos.get(pathInfos.size() - 1);
    }
    return null;
  }

  public UriPathInfoImpl getUriPathInfo(int index) {
    return pathInfos.get(index);
  }

  public void setSystemParameter(SystemQueryParameter filter, Expression expression) {
    spFilter = expression;
    addQueryParameter(filter.toString(), expression);
  }

  public Expression getFilter() {
    return this.spFilter;
  }

  @Override
  public String toString() {
    String ret = "";
    int i = 0;
    while (i < pathInfos.size()) {
      if ( i > 0 ) { 
        ret += "/";
      }
        
      ret += pathInfos.get(i).toString();
      
      
      
      i++;
      
    }
    
    

    return ret;
  }

}
