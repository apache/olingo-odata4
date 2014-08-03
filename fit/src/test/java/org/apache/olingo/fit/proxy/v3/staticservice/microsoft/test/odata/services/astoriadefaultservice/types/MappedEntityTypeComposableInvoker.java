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
package org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
//CHECKSTYLE:ON (Maven checkstyle)

public interface MappedEntityTypeComposableInvoker 
  extends org.apache.olingo.ext.proxy.api.StructuredComposableInvoker<MappedEntityType, MappedEntityType.Operations>
   {

  @Override
  MappedEntityTypeComposableInvoker select(String... select);

  @Override
  MappedEntityTypeComposableInvoker expand(String... expand);

    

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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> getBagOfPrimitiveToLinks();

    void setBagOfPrimitiveToLinks(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> _bagOfPrimitiveToLinks);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.math.BigDecimal> getBagOfDecimals();

    void setBagOfDecimals(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.math.BigDecimal> _bagOfDecimals);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Double> getBagOfDoubles();

    void setBagOfDoubles(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Double> _bagOfDoubles);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Float> getBagOfSingles();

    void setBagOfSingles(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Float> _bagOfSingles);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Short> getBagOfBytes();

    void setBagOfBytes(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Short> _bagOfBytes);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Short> getBagOfInt16s();

    void setBagOfInt16s(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Short> _bagOfInt16s);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Integer> getBagOfInt32s();

    void setBagOfInt32s(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Integer> _bagOfInt32s);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Long> getBagOfInt64s();

    void setBagOfInt64s(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.Long> _bagOfInt64s);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.util.UUID> getBagOfGuids();

    void setBagOfGuids(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.util.UUID> _bagOfGuids);
    
    
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
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.sql.Timestamp> getBagOfDateTime();

    void setBagOfDateTime(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.sql.Timestamp> _bagOfDateTime);
    
    
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
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ComplexToCategoryCollection getBagOfComplexToCategories();

    void setBagOfComplexToCategories(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ComplexToCategoryCollection _bagOfComplexToCategories);
    
    
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
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone getComplexPhone();

    void setComplexPhone(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone _complexPhone);
    
    
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
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails getComplexContactDetails();

    void setComplexContactDetails(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails _complexContactDetails);
    


}
