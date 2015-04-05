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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * JPA entity illustrating
 * <ol>
 * <li>M..N bidirectional relationship with JoinTable</li>
 * </ol>
 */
@Entity
@Table(name = "T_SUPPLIER")
public class Supplier extends BusinessPartner {
  @ManyToMany
  @JoinTable(name = "T_SUPPLIER_PRODUCT", joinColumns = @JoinColumn(name = "SUPPLIER_ID", referencedColumnName = "ID"),
      inverseJoinColumns = { @JoinColumn(name = "PRODUCT_NAME", referencedColumnName = "NAME"),
          @JoinColumn(name = "PRODUCT_TYPE", referencedColumnName = "TYPE") })
  private List<Product> products;

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }
}
