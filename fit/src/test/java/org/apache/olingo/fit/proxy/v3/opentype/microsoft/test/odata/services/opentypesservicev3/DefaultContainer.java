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

package org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3;

import org.apache.olingo.ext.proxy.api.PersistenceManager;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.OpenTypesServiceV3")
@org.apache.olingo.ext.proxy.api.annotations.EntityContainer(name = "DefaultContainer",
  namespace = "Microsoft.Test.OData.Services.OpenTypesServiceV3",
  isDefaultEntityContainer = true)
public interface DefaultContainer extends PersistenceManager {

    Row getRow();

    RowIndex getRowIndex();





  Operations operations();

  public interface Operations {
  
    }

      ComplexFactory complexFactory();

    interface ComplexFactory {
          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ContactDetails",
                type = "Microsoft.Test.OData.Services.OpenTypesServiceV3.ContactDetails")
      org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3.types.ContactDetails newContactDetails();

        }
  }
