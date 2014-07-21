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
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "ComputerDetail",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface ComputerDetail 
  extends org.apache.olingo.ext.proxy.api.EntityType,org.apache.olingo.ext.proxy.api.Annotatable,org.apache.olingo.ext.proxy.api.SingleQuery<ComputerDetail> {


    

    @Key
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComputerDetailId", 
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
    java.lang.Integer getComputerDetailId();

    void setComputerDetailId(java.lang.Integer _computerDetailId);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Manufacturer", 
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
    java.lang.String getManufacturer();

    void setManufacturer(java.lang.String _manufacturer);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Model", 
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
    java.lang.String getModel();

    void setModel(java.lang.String _model);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Serial", 
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
    java.lang.String getSerial();

    void setSerial(java.lang.String _serial);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "SpecificationsBag", 
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
    java.util.Collection<java.lang.String> getSpecificationsBag();

    void setSpecificationsBag(java.util.Collection<java.lang.String> _specificationsBag);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PurchaseDate", 
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
    java.sql.Timestamp getPurchaseDate();

    void setPurchaseDate(java.sql.Timestamp _purchaseDate);
    
    
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
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions getDimensions();

    void setDimensions(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions _dimensions);
    

    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Computer", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Computer", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Computer",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Computer getComputer();

    void setComputer(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Computer _computer);
    


    Annotations annotations();

    interface Annotations {

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ComputerDetailId",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getComputerDetailIdAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Manufacturer",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getManufacturerAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Model",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getModelAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Serial",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getSerialAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "SpecificationsBag",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getSpecificationsBagAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PurchaseDate",
                   type = "Edm.DateTime")
        org.apache.olingo.ext.proxy.api.Annotatable getPurchaseDateAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Dimensions",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions")
        org.apache.olingo.ext.proxy.api.Annotatable getDimensionsAnnotations();



        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Computer", 
                  type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Computer")
        org.apache.olingo.ext.proxy.api.Annotatable getComputerAnnotations();
    }

}
