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

import org.apache.olingo.odata4.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.odata4.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SupportedQueryOptions;

public class OrderByOptionImpl extends SystemQueryOptionImpl implements OrderByOption {

  private List<OrderByItemImpl> orders = new ArrayList<OrderByItemImpl>();

  public OrderByOptionImpl() {
    setKind(SupportedQueryOptions.ORDERBY);
  }

  @Override
  public List<OrderByItem> getOrders() {
    List<OrderByItem> retList = new ArrayList<OrderByItem>();
    for (OrderByItemImpl item : orders) {
      retList.add(item);
    }
    return retList;
  }

  public OrderByOptionImpl addOrder(final OrderByItemImpl order) {
    orders.add(order);
    return this;
  }

}
