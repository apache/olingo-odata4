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

package org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
//CHECKSTYLE:ON (Maven checkstyle)


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "MappedEntityType",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface MappedEntityType 
  extends org.apache.olingo.ext.proxy.api.EntityType,org.apache.olingo.ext.proxy.api.Annotatable,org.apache.olingo.ext.proxy.api.SingleQuery<MappedEntityType> {


    

    @Key
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Id", 
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
    java.lang.Integer getId();

    void setId(java.lang.Integer _id);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Href", 
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
    java.lang.String getHref();

    void setHref(java.lang.String _href);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Title", 
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
    java.lang.String getTitle();

    void setTitle(java.lang.String _title);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "HrefLang", 
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
    java.lang.String getHrefLang();

    void setHrefLang(java.lang.String _hrefLang);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Type", 
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
    java.lang.String getType();

    void setType(java.lang.String _type);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Length", 
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
    java.lang.Integer getLength();

    void setLength(java.lang.Integer _length);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfPrimitiveToLinks", 
                type = "Edm.String", 
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
    java.util.Collection<java.lang.String> getBagOfPrimitiveToLinks();

    void setBagOfPrimitiveToLinks(java.util.Collection<java.lang.String> _bagOfPrimitiveToLinks);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Logo", 
                type = "Edm.Binary", 
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
    byte[] getLogo();

    void setLogo(byte[] _logo);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfDecimals", 
                type = "Edm.Decimal", 
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
    java.util.Collection<java.math.BigDecimal> getBagOfDecimals();

    void setBagOfDecimals(java.util.Collection<java.math.BigDecimal> _bagOfDecimals);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfDoubles", 
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
    java.util.Collection<java.lang.Double> getBagOfDoubles();

    void setBagOfDoubles(java.util.Collection<java.lang.Double> _bagOfDoubles);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfSingles", 
                type = "Edm.Single", 
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
    java.util.Collection<java.lang.Float> getBagOfSingles();

    void setBagOfSingles(java.util.Collection<java.lang.Float> _bagOfSingles);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfBytes", 
                type = "Edm.Byte", 
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
    java.util.Collection<java.lang.Short> getBagOfBytes();

    void setBagOfBytes(java.util.Collection<java.lang.Short> _bagOfBytes);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfInt16s", 
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
    java.util.Collection<java.lang.Short> getBagOfInt16s();

    void setBagOfInt16s(java.util.Collection<java.lang.Short> _bagOfInt16s);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfInt32s", 
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
    java.util.Collection<java.lang.Integer> getBagOfInt32s();

    void setBagOfInt32s(java.util.Collection<java.lang.Integer> _bagOfInt32s);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfInt64s", 
                type = "Edm.Int64", 
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
    java.util.Collection<java.lang.Long> getBagOfInt64s();

    void setBagOfInt64s(java.util.Collection<java.lang.Long> _bagOfInt64s);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfGuids", 
                type = "Edm.Guid", 
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
    java.util.Collection<java.util.UUID> getBagOfGuids();

    void setBagOfGuids(java.util.Collection<java.util.UUID> _bagOfGuids);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfDateTime", 
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
    java.util.Collection<java.sql.Timestamp> getBagOfDateTime();

    void setBagOfDateTime(java.util.Collection<java.sql.Timestamp> _bagOfDateTime);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BagOfComplexToCategories", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ComplexToCategory", 
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
    java.util.Collection<org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ComplexToCategory> getBagOfComplexToCategories();

    void setBagOfComplexToCategories(java.util.Collection<org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ComplexToCategory> _bagOfComplexToCategories);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComplexPhone", 
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
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone getComplexPhone();

    void setComplexPhone(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone _complexPhone);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComplexContactDetails", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails", 
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
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails getComplexContactDetails();

    void setComplexContactDetails(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails _complexContactDetails);
    



    Annotations annotations();

    interface Annotations {

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Id",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getIdAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Href",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getHrefAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Title",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getTitleAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "HrefLang",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getHrefLangAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Type",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getTypeAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Length",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getLengthAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfPrimitiveToLinks",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfPrimitiveToLinksAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Logo",
                   type = "Edm.Binary")
        org.apache.olingo.ext.proxy.api.Annotatable getLogoAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfDecimals",
                   type = "Edm.Decimal")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfDecimalsAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfDoubles",
                   type = "Edm.Double")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfDoublesAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfSingles",
                   type = "Edm.Single")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfSinglesAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfBytes",
                   type = "Edm.Byte")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfBytesAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfInt16s",
                   type = "Edm.Int16")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfInt16sAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfInt32s",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfInt32sAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfInt64s",
                   type = "Edm.Int64")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfInt64sAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfGuids",
                   type = "Edm.Guid")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfGuidsAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfDateTime",
                   type = "Edm.DateTime")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfDateTimeAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BagOfComplexToCategories",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ComplexToCategory")
        org.apache.olingo.ext.proxy.api.Annotatable getBagOfComplexToCategoriesAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ComplexPhone",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone")
        org.apache.olingo.ext.proxy.api.Annotatable getComplexPhoneAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ComplexContactDetails",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails")
        org.apache.olingo.ext.proxy.api.Annotatable getComplexContactDetailsAnnotations();


    }

}
