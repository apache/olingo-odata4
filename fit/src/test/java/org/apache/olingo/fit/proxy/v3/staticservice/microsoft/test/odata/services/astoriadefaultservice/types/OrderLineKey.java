/*
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
 */

package org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types;

import org.apache.olingo.ext.proxy.api.AbstractEntityKey;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;

@org.apache.olingo.ext.proxy.api.annotations.CompoundKey
public class OrderLineKey extends AbstractEntityKey {

  private static final long serialVersionUID = 1L;
  private java.lang.Integer _orderId;

  @CompoundKeyElement(name = "OrderId", position = 0)
  public java.lang.Integer getOrderId() {
    return _orderId;
  }

  public void setOrderId(final java.lang.Integer _orderId) {
    this._orderId = _orderId;
  }

  private java.lang.Integer _productId;

  @CompoundKeyElement(name = "ProductId", position = 1)
  public java.lang.Integer getProductId() {
    return _productId;
  }

  public void setProductId(final java.lang.Integer _productId) {
    this._productId = _productId;
  }
}
