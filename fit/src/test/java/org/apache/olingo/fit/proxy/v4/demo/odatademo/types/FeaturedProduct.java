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

package org.apache.olingo.fit.proxy.v4.demo.odatademo.types;

import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("ODataDemo")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "FeaturedProduct",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "ODataDemo.Product")
public interface FeaturedProduct
    extends Annotatable, org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Product {

  @Override
  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ID",
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
  java.lang.Integer getID();

  @Override
  void setID(java.lang.Integer _iD);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Name",
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
  java.lang.String getName();

  @Override
  void setName(java.lang.String _name);

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
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ReleaseDate",
      type = "Edm.DateTimeOffset",
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
  java.util.Calendar getReleaseDate();

  @Override
  void setReleaseDate(java.util.Calendar _releaseDate);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "DiscontinuedDate",
      type = "Edm.DateTimeOffset",
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
  java.util.Calendar getDiscontinuedDate();

  @Override
  void setDiscontinuedDate(java.util.Calendar _discontinuedDate);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Rating",
      type = "Edm.Int16",
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
  java.lang.Short getRating();

  @Override
  void setRating(java.lang.Short _rating);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Price",
      type = "Edm.Double",
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
  java.lang.Double getPrice();

  @Override
  void setPrice(java.lang.Double _price);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Categories",
      type = "ODataDemo.Category",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "Categories",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.demo.odatademo.types.CategoryCollection getCategories();

  @Override
  void setCategories(org.apache.olingo.fit.proxy.v4.demo.odatademo.types.CategoryCollection _categories);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Supplier",
      type = "ODataDemo.Supplier",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "Suppliers",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Supplier getSupplier();

  @Override
  void setSupplier(org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Supplier _supplier);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "ProductDetail",
      type = "ODataDemo.ProductDetail",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "ProductDetails",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.demo.odatademo.types.ProductDetail getProductDetail();

  @Override
  void setProductDetail(org.apache.olingo.fit.proxy.v4.demo.odatademo.types.ProductDetail _productDetail);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Advertisement",
      type = "ODataDemo.Advertisement",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "Advertisements",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Advertisement getAdvertisement();

  void setAdvertisement(org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Advertisement _advertisement);

  @Override
  Operations operations();

  interface Operations extends org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Product.Operations {

  }

  @Override
  ComplexFactory factory();

  interface ComplexFactory extends org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Product.ComplexFactory {}

  @Override
  Annotations annotations();

  interface Annotations extends org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Product.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ID",
        type = "Edm.Int32")
    Annotatable getIDAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    Annotatable getNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Description",
        type = "Edm.String")
    Annotatable getDescriptionAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ReleaseDate",
        type = "Edm.DateTimeOffset")
    Annotatable getReleaseDateAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "DiscontinuedDate",
        type = "Edm.DateTimeOffset")
    Annotatable getDiscontinuedDateAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Rating",
        type = "Edm.Int16")
    Annotatable getRatingAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Price",
        type = "Edm.Double")
    Annotatable getPriceAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Categories",
        type = "ODataDemo.Category")
    Annotatable getCategoriesAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Supplier",
        type = "ODataDemo.Supplier")
    Annotatable getSupplierAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "ProductDetail",
        type = "ODataDemo.ProductDetail")
    Annotatable getProductDetailAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Advertisement",
        type = "ODataDemo.Advertisement")
    Annotatable getAdvertisementAnnotations();
  }

}
