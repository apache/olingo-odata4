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

package org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;
import org.apache.olingo.ext.proxy.api.AbstractEntityKey;
//CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.CompoundKey
public class ProductDetailKey extends AbstractEntityKey {

    private java.lang.Integer _productID;

    @CompoundKeyElement(name = "ProductID", position = 0)
    public java.lang.Integer getProductID() {
        return _productID;
    }

    public void setProductID(java.lang.Integer _productID) {
        this._productID = _productID;
    }

    private java.lang.Integer _productDetailID;

    @CompoundKeyElement(name = "ProductDetailID", position = 1)
    public java.lang.Integer getProductDetailID() {
        return _productDetailID;
    }

    public void setProductDetailID(java.lang.Integer _productDetailID) {
        this._productDetailID = _productDetailID;
    }
}
