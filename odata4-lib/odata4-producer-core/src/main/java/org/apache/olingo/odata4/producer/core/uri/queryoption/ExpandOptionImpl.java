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
package org.apache.olingo.odata4.producer.core.uri.queryoption;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.producer.api.uri.queryoption.ExceptionVisitExpand;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandItem;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandVisitor;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SystemQueryOptionEnum;
import org.apache.olingo.odata4.producer.api.uri.queryoption.VisitableExpand;

public class ExpandOptionImpl extends SystemQueryOptionImpl implements ExpandOption, VisitableExpand {

  List<ExpandItemImpl> expandItems = new ArrayList<ExpandItemImpl>();

  public ExpandOptionImpl() {
    setKind(SystemQueryOptionEnum.EXPAND);
  }

  public void addExpandItem(ExpandItemImpl expandItem) {
    expandItems.add(expandItem);
  }

  public List<ExpandItem> getExpandItems() {
    List<ExpandItem> retList = new ArrayList<ExpandItem>();
    for (ExpandItemImpl item : expandItems) {
      retList.add(item);
    }
    return retList;
  }

  @Override
  public <T> T accept(ExpandVisitor<T> visitor) throws ExceptionVisitExpand {
    List<T> parameters = new ArrayList<T>();
    for (ExpandItemImpl expandItem : expandItems) {
      parameters.add(expandItem.accept(visitor));
    }

    return visitor.visitExpand(parameters);
  }

}
