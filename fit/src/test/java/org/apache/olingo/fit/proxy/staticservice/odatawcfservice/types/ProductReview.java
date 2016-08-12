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
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.KeyRef;

// CHECKSTYLE:ON (Maven checkstyle)

@KeyRef(ProductReviewKey.class)
@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "ProductReview",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface ProductReview
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<ProductReview>,
    org.apache.olingo.ext.proxy.api.StructuredQuery<ProductReview> {

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

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ReviewTitle",
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
  java.lang.String getReviewTitle();

  void setReviewTitle(java.lang.String _reviewTitle);

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "RevisionID",
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
  java.lang.Integer getRevisionID();

  void setRevisionID(java.lang.Integer _revisionID);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Comment",
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
  java.lang.String getComment();

  void setComment(java.lang.String _comment);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Author",
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
  java.lang.String getAuthor();

  void setAuthor(java.lang.String _author);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
    // No additional methods needed for now.
  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getProductIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductDetailID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getProductDetailIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ReviewTitle",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getReviewTitleAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "RevisionID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getRevisionIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Comment",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getCommentAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Author",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getAuthorAnnotations();

  }

}
