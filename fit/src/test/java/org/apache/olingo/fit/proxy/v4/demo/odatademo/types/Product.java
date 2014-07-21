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

package org.apache.olingo.fit.proxy.v4.demo.odatademo.types;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
//CHECKSTYLE:ON (Maven checkstyle)


@org.apache.olingo.ext.proxy.api.annotations.Namespace("ODataDemo")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Product",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface Product 
  extends org.apache.olingo.ext.proxy.api.EntityType,org.apache.olingo.ext.proxy.api.Annotatable,org.apache.olingo.ext.proxy.api.SingleQuery<Product> {


    

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

    void setID(java.lang.Integer _iD);
    
    
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

    void setName(java.lang.String _name);
    
    
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

    void setDescription(java.lang.String _description);
    
    
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
    java.sql.Timestamp getReleaseDate();

    void setReleaseDate(java.sql.Timestamp _releaseDate);
    
    
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
    java.sql.Timestamp getDiscontinuedDate();

    void setDiscontinuedDate(java.sql.Timestamp _discontinuedDate);
    
    
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

    void setRating(java.lang.Short _rating);
    
    
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

    void setPrice(java.lang.Double _price);
    

    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Categories", 
                type = "ODataDemo.Category", 
                targetSchema = "ODataDemo", 
                targetContainer = "DemoService", 
                targetEntitySet = "Categories",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v4.demo.odatademo.types.CategoryCollection getCategories();

    void setCategories(org.apache.olingo.fit.proxy.v4.demo.odatademo.types.CategoryCollection _categories);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Supplier", 
                type = "ODataDemo.Supplier", 
                targetSchema = "ODataDemo", 
                targetContainer = "DemoService", 
                targetEntitySet = "Suppliers",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Supplier getSupplier();

    void setSupplier(org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Supplier _supplier);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "ProductDetail", 
                type = "ODataDemo.ProductDetail", 
                targetSchema = "ODataDemo", 
                targetContainer = "DemoService", 
                targetEntitySet = "ProductDetails",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v4.demo.odatademo.types.ProductDetail getProductDetail();

    void setProductDetail(org.apache.olingo.fit.proxy.v4.demo.odatademo.types.ProductDetail _productDetail);
    

        Operations operations();

    interface Operations {
    
          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "Discount",
                    type = OperationType.ACTION,
                    returnType = "Edm.Double")
      java.lang.Double discount(
                @Parameter(name = "discountPercentage", type = "Edm.Int32", nullable = false) java.lang.Integer discountPercentage
            );

        }

    Annotations annotations();

    interface Annotations {

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ID",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getIDAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getNameAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Description",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getDescriptionAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ReleaseDate",
                   type = "Edm.DateTimeOffset")
        org.apache.olingo.ext.proxy.api.Annotatable getReleaseDateAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "DiscontinuedDate",
                   type = "Edm.DateTimeOffset")
        org.apache.olingo.ext.proxy.api.Annotatable getDiscontinuedDateAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Rating",
                   type = "Edm.Int16")
        org.apache.olingo.ext.proxy.api.Annotatable getRatingAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Price",
                   type = "Edm.Double")
        org.apache.olingo.ext.proxy.api.Annotatable getPriceAnnotations();



        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Categories", 
                  type = "ODataDemo.Category")
        org.apache.olingo.ext.proxy.api.Annotatable getCategoriesAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Supplier", 
                  type = "ODataDemo.Supplier")
        org.apache.olingo.ext.proxy.api.Annotatable getSupplierAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "ProductDetail", 
                  type = "ODataDemo.ProductDetail")
        org.apache.olingo.ext.proxy.api.Annotatable getProductDetailAnnotations();
    }

}
