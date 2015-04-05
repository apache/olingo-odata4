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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * JPA Entity illustrating
 * <ol>
 * <li>Usage of javax.validation.constraints on attributes</li>
 * <li>N..1 unidirectional <b>self relationship</b> with "JoinColumn" annotation with optional referencedColumn
 * attribute</li>
 * <li>1..N bidirectional <b>self relationship</b> with "mappedBy" attribute</li>
 * <li>1..N bidirectional <b>self relationship</b> with "JoinColumn" annotation with name and referencedColumn
 * attributes</li>
 * </ol>
 */
@Entity
@Table(name = "T_EMPLOYEE")
public class Employee {
  @Id
  @Column(name = "EMPLOYEE_ID")
  private String id;

  @Column(name = "FIRST_NAME", length = 40, nullable = false)
  private String firstName;

  @Column(name = "MIDDLE_NAME", length = 40, nullable = true)
  private String middleName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "AGE")
  @Min(value = 18)
  @Max(value = 150)
  private short age;

  @ManyToOne
  @JoinColumn(name = "MANAGER_ID")
  private Employee manager;

  @OneToMany(mappedBy = "mentor")
  private List<Employee> mentees;

  @ManyToOne
  @JoinColumn(name = "MENTOR_ID", referencedColumnName = "EMPLOYEE_ID")
  private Employee mentor;

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public short getAge() {
    return age;
  }

  public void setAge(short age) {
    this.age = age;
  }
}
