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

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * JPA Entity illustrating
 * <ol>
 * <li>Attributes with Enum type</li>
 * <li>M..N bidirectional relationship</li>
 * </ol>
 */
@Entity
@Table(name = "T_PRODUCT")
public class Product {

  @Id
  @Column(name = "NAME", length = 40, nullable = false)
  private String name;

  @Id
  @Column(name = "TYPE", length = 4, nullable = false)
  private ProductType type;

  @Temporal(TemporalType.DATE)
  private Date manufactured;

  @ManyToMany(targetEntity = org.apache.olingo.jpa.ref.model.Supplier.class, mappedBy = "products")
  private Set<Supplier> suppliers;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProductType getType() {
    return type;
  }

  public void setType(ProductType type) {
    this.type = type;
  }

  public Set<Supplier> getSuppliers() {
    return suppliers;
  }

  public void setSuppliers(Set<Supplier> suppliers) {
    this.suppliers = suppliers;
  }

  public Date getManufactured() {
    return manufactured;
  }

  public void setManufactured(Date manufactured) {
    this.manufactured = manufactured;
  }
}
