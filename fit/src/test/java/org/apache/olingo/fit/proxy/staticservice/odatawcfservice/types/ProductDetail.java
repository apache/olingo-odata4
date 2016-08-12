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
package org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types;

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.OperationType;

// CHECKSTYLE:ON (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.KeyRef;

@KeyRef(ProductDetailKey.class)
@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "ProductDetail",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface ProductDetail
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<ProductDetail>,
    org.apache.olingo.ext.proxy.api.StructuredQuery<ProductDetail> {

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ProductID",
      type = "Edm.Int32",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Integer getProductID();

  void setProductID(java.lang.Integer _productID);

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ProductDetailID",
      type = "Edm.Int32",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Integer getProductDetailID();

  void setProductDetailID(java.lang.Integer _productDetailID);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ProductName",
      type = "Edm.String",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.String getProductName();

  void setProductName(java.lang.String _productName);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Description",
      type = "Edm.String",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.String getDescription();

  void setDescription(java.lang.String _description);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "RelatedProduct",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Product",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Products",
      containsTarget = false)
  Product
      getRelatedProduct();

      void
      setRelatedProduct(
          Product _relatedProduct);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Reviews",
      type = "Microsoft.Test.OData.Services.ODataWCFService.ProductReview",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "ProductReviews",
      containsTarget = false)
  ProductReviewCollection
      getReviews();

      void
      setReviews(
          ProductReviewCollection _reviews);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "GetRelatedProduct",
        type = OperationType.FUNCTION,
        isComposable = true,
        referenceType = Product.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.Product")
    ProductComposableInvoker
        getRelatedProduct(
        );

  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getProductIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductDetailID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getProductDetailIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getProductNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Description",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getDescriptionAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "RelatedProduct",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Product")
    org.apache.olingo.ext.proxy.api.Annotatable getRelatedProductAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Reviews",
        type = "Microsoft.Test.OData.Services.ODataWCFService.ProductReview")
    org.apache.olingo.ext.proxy.api.Annotatable getReviewsAnnotations();
  }

}
