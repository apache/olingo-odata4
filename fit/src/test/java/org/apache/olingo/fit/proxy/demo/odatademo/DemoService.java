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
package org.apache.olingo.fit.proxy.demo.odatademo;

// CHECKSTYLE:OFF (Maven checkstyle)
import java.io.InputStream;

// CHECKSTYLE:ON (Maven checkstyle)
import java.io.Serializable;

import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.ComplexType;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("ODataDemo")
@org.apache.olingo.ext.proxy.api.annotations.EntityContainer(name = "DemoService",
    namespace = "ODataDemo")
public interface DemoService extends PersistenceManager {

  Products getProducts();

  Advertisements getAdvertisements();

  Persons getPersons();

  Categories getCategories();

  PersonDetails getPersonDetails();

  Suppliers getSuppliers();

  ProductDetails getProductDetails();

  Operations operations();

  public interface Operations extends org.apache.olingo.ext.proxy.api.Operations {

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "IncreaseSalaries",
        type = OperationType.ACTION)
    org.apache.olingo.ext.proxy.api.Invoker<Void> increaseSalaries(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "percentage", type = "Edm.Int32",
            nullable = false) java.lang.Integer percentage
        );

  }

  <NE extends EntityType<?>> NE newEntityInstance(Class<NE> ref);

  <T extends EntityType<?>, NEC extends EntityCollection<T, ?, ?>> NEC newEntityCollection(Class<NEC> ref);

  <NE extends ComplexType<?>> NE newComplexInstance(Class<NE> ref);

  <T extends ComplexType<?>, NEC extends ComplexCollection<T, ?, ?>> NEC newComplexCollection(Class<NEC> ref);

  <T extends Serializable, NEC extends PrimitiveCollection<T>> NEC newPrimitiveCollection(Class<T> ref);

  EdmStreamValue newEdmStreamValue(String contentType, InputStream stream);
}
