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
package org.apache.olingo.fit.proxy.demo.odatademo.types;

import java.util.concurrent.Future;

import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("ODataDemo")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "FeaturedProduct",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "ODataDemo.Product")
public interface FeaturedProduct extends Product {

  @Override
  FeaturedProduct load();

  @Override
  Future<? extends FeaturedProduct> loadAsync();

  @Override
  FeaturedProduct refs();

  @Override
  FeaturedProduct expand(String... expand);

  @Override
  FeaturedProduct select(String... select);

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
      srid = "")
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
      srid = "")
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
      srid = "")
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
      srid = "")
  java.sql.Timestamp getReleaseDate();

  @Override
  void setReleaseDate(java.sql.Timestamp _releaseDate);

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
      srid = "")
  java.sql.Timestamp getDiscontinuedDate();

  @Override
  void setDiscontinuedDate(java.sql.Timestamp _discontinuedDate);

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
      srid = "")
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
      srid = "")
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
  CategoryCollection getCategories();

  @Override
  void setCategories(CategoryCollection _categories);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Supplier",
      type = "ODataDemo.Supplier",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "Suppliers",
      containsTarget = false)
  Supplier getSupplier();

  @Override
  void setSupplier(Supplier _supplier);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "ProductDetail",
      type = "ODataDemo.ProductDetail",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "ProductDetails",
      containsTarget = false)
  ProductDetail getProductDetail();

  @Override
  void setProductDetail(ProductDetail _productDetail);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Advertisement",
      type = "ODataDemo.Advertisement",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "Advertisements",
      containsTarget = false)
  Advertisement getAdvertisement();

  void setAdvertisement(Advertisement _advertisement);

  @Override
  Operations operations();

  interface Operations extends Product.Operations {
    // No additional methods needed for now.
  }

  @Override
  Annotations annotations();

  interface Annotations extends Product.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getIDAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Description",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getDescriptionAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ReleaseDate",
        type = "Edm.DateTimeOffset")
    org.apache.olingo.ext.proxy.api.Annotatable getReleaseDateAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "DiscontinuedDate",
        type = "Edm.DateTimeOffset")
    org.apache.olingo.ext.proxy.api.Annotatable getDiscontinuedDateAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Rating",
        type = "Edm.Int16")
    org.apache.olingo.ext.proxy.api.Annotatable getRatingAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Price",
        type = "Edm.Double")
    org.apache.olingo.ext.proxy.api.Annotatable getPriceAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Categories",
        type = "ODataDemo.Category")
    org.apache.olingo.ext.proxy.api.Annotatable getCategoriesAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Supplier",
        type = "ODataDemo.Supplier")
    org.apache.olingo.ext.proxy.api.Annotatable getSupplierAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "ProductDetail",
        type = "ODataDemo.ProductDetail")
    org.apache.olingo.ext.proxy.api.Annotatable getProductDetailAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Advertisement",
        type = "ODataDemo.Advertisement")
    org.apache.olingo.ext.proxy.api.Annotatable getAdvertisementAnnotations();
  }

}
