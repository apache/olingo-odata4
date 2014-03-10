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
package org.apache.olingo.odata4.server.core.uri.queryoption;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.odata4.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SupportedQueryOptions;

public class SelectOptionImpl extends SystemQueryOptionImpl implements SelectOption {

  private List<SelectItemImpl> selectItems;

  public SelectOptionImpl() {
    setKind(SupportedQueryOptions.SELECT);
  }

  public SelectOptionImpl setSelectItems(final List<SelectItemImpl> selectItems) {
    this.selectItems = selectItems;
    return this;
  }

  @Override
  public List<SelectItem> getSelectItems() {
    List<SelectItem> retList = new ArrayList<SelectItem>();
    for (SelectItemImpl item : selectItems) {
      retList.add(item);
    }
    return retList;
  }

}
