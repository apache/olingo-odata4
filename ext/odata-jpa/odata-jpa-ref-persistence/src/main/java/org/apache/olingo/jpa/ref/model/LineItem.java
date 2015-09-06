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
package org.apache.olingo.jpa.ref.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

/**
 * JPA Entity illustrating
 * <ol>
 * <li>Embedded Key attributes</li>
 * <li>N..1 bidirectional relationship with "JoinColumn" annotation having name and referencedColumn attributes</li>
 * <li>1..1 unidirectional relationship with "JoinColumns" annotation</li>
 * </ol>
 */
@Entity
@Table(name = "T_LINEITEM")
public class LineItem {
  @EmbeddedId
  private LineItemKey key;

  @Column(name = "NET_AMOUNT")
  @Digits(fraction = 2, integer = 10)
  private float netAmount;

  private Currency currency;

  @Column(name = "QUANTITY")
  private int quantity;

  @ManyToOne
  @JoinColumn(name = "SO_ID", referencedColumnName = "ID", insertable = false, updatable = false)
  private SalesOrder order;

  @Column(name = "DELIVERED")
  private boolean isDelivered;

  @OneToOne
  @JoinColumns(value = { @JoinColumn(name = "PRODUCT_NAME", referencedColumnName = "NAME"),
      @JoinColumn(name = "PRODUCT_TYPE", referencedColumnName = "TYPE") })
  private Product product;

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public LineItemKey getKey() {
    return key;
  }

  public void setKey(LineItemKey key) {
    this.key = key;
  }

  public SalesOrder getOrder() {
    return order;
  }

  public void setOrder(SalesOrder order) {
    this.order = order;
  }

  public float getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(float netAmount) {
    this.netAmount = netAmount;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public boolean isDelivered() {
    return isDelivered;
  }

  public void setDelivered(boolean isDelivered) {
    this.isDelivered = isDelivered;
  }
}
