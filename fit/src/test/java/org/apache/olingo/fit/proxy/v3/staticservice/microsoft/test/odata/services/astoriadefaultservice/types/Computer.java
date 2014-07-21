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
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
//CHECKSTYLE:ON (Maven checkstyle)


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Computer",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface Computer 
  extends org.apache.olingo.ext.proxy.api.EntityType,org.apache.olingo.ext.proxy.api.Annotatable,org.apache.olingo.ext.proxy.api.SingleQuery<Computer> {


    

    @Key
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComputerId", 
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
    java.lang.Integer getComputerId();

    void setComputerId(java.lang.Integer _computerId);
    
    
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
    

    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "ComputerDetail", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ComputerDetail", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "ComputerDetail",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ComputerDetail getComputerDetail();

    void setComputerDetail(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ComputerDetail _computerDetail);
    

        Operations operations();

    interface Operations {
    
          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetComputer",
                    type = OperationType.ACTION,
                    returnType = "Microsoft.Test.OData.Services.AstoriaDefaultService.Computer")
      org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Computer getComputer(
            );

        }

    Annotations annotations();

    interface Annotations {

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ComputerId",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getComputerIdAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getNameAnnotations();



        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "ComputerDetail", 
                  type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ComputerDetail")
        org.apache.olingo.ext.proxy.api.Annotatable getComputerDetailAnnotations();
    }

}
