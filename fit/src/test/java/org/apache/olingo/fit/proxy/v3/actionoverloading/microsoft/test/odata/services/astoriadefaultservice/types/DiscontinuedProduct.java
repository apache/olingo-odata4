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

package org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types;

import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "DiscontinuedProduct",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product")
public interface DiscontinuedProduct
    extends Annotatable,
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Product {

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Picture",
      type = "Edm.Stream",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.io.InputStream getPicture();

  @Override
  void setPicture(java.io.InputStream _picture);

  @Override
  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ProductId",
      type = "Edm.Int32",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.lang.Integer getProductId();

  @Override
  void setProductId(java.lang.Integer _productId);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Description",
      type = "Edm.String",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.lang.String getDescription();

  @Override
  void setDescription(java.lang.String _description);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Dimensions",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions
      getDimensions();

  @Override
      void
      setDimensions(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions _dimensions);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BaseConcurrency",
      type = "Edm.String",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.lang.String getBaseConcurrency();

  @Override
  void setBaseConcurrency(java.lang.String _baseConcurrency);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComplexConcurrency",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo
      getComplexConcurrency();

  @Override
      void
      setComplexConcurrency(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo _complexConcurrency);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "NestedComplexConcurrency",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo
      getNestedComplexConcurrency();

  @Override
      void
      setNestedComplexConcurrency(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo _nestedComplexConcurrency);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Discontinued",
      type = "Edm.DateTime",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.util.Calendar getDiscontinued();

  void setDiscontinued(java.util.Calendar _discontinued);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ReplacementProductId",
      type = "Edm.Int32",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.lang.Integer getReplacementProductId();

  void setReplacementProductId(java.lang.Integer _replacementProductId);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "DiscontinuedPhone",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone
      getDiscontinuedPhone();

      void
      setDiscontinuedPhone(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone _discontinuedPhone);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ChildConcurrencyToken",
      type = "Edm.String",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.lang.String getChildConcurrencyToken();

  void setChildConcurrencyToken(java.lang.String _childConcurrencyToken);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "RelatedProducts",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Product",
      containsTarget = false)
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection
      getRelatedProducts();

  @Override
      void
      setRelatedProducts(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection _relatedProducts);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Detail",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductDetail",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "ProductDetail",
      containsTarget = false)
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ProductDetail
      getDetail();

  @Override
      void
      setDetail(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ProductDetail _detail);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Reviews",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductReview",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "ProductReview",
      containsTarget = false)
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ProductReviewCollection
      getReviews();

  @Override
      void
      setReviews(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ProductReviewCollection _reviews);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Photos",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductPhoto",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "ProductPhoto",
      containsTarget = false)
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ProductPhotoCollection
      getPhotos();

  @Override
      void
      setPhotos(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ProductPhotoCollection _photos);

  @Override
  Operations operations();

  interface Operations
      extends
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Product.Operations {

  }

  @Override
  ComplexFactory factory();

  interface ComplexFactory
      extends
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Product.ComplexFactory {
    @Override
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Dimensions",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions")
        org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions
        newDimensions();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComplexConcurrency",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo")
        org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo
        newComplexConcurrency();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "NestedComplexConcurrency",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo")
        org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo
        newNestedComplexConcurrency();

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "DiscontinuedPhone",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone")
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone
        newDiscontinuedPhone();

  }

  @Override
  Annotations annotations();

  interface Annotations
      extends
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Product.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Picture",
        type = "Edm.Stream")
    Annotatable getPictureAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductId",
        type = "Edm.Int32")
    Annotatable getProductIdAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Description",
        type = "Edm.String")
    Annotatable getDescriptionAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Dimensions",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions")
    Annotatable getDimensionsAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BaseConcurrency",
        type = "Edm.String")
    Annotatable getBaseConcurrencyAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ComplexConcurrency",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo")
    Annotatable getComplexConcurrencyAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "NestedComplexConcurrency",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo")
    Annotatable getNestedComplexConcurrencyAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Discontinued",
        type = "Edm.DateTime")
    Annotatable getDiscontinuedAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ReplacementProductId",
        type = "Edm.Int32")
    Annotatable getReplacementProductIdAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "DiscontinuedPhone",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone")
    Annotatable getDiscontinuedPhoneAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ChildConcurrencyToken",
        type = "Edm.String")
    Annotatable getChildConcurrencyTokenAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "RelatedProducts",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product")
    Annotatable getRelatedProductsAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Detail",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductDetail")
    Annotatable getDetailAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Reviews",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductReview")
    Annotatable getReviewsAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Photos",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductPhoto")
    Annotatable getPhotosAnnotations();
  }

}
