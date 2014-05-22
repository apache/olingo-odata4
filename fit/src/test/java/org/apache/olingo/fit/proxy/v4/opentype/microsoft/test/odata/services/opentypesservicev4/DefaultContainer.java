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

package org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4;

import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.EntityContainer;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.*;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.*;

import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import java.io.Serializable;
import java.util.Collection;
import java.util.Calendar;
import javax.xml.datatype.Duration;

@Namespace("Microsoft.Test.OData.Services.OpenTypesServiceV4")
@EntityContainer(name = "DefaultContainer",
  namespace = "Microsoft.Test.OData.Services.OpenTypesServiceV4",
  isDefaultEntityContainer = true)
public interface DefaultContainer extends PersistenceManager {

    Row getRow();
    RowIndex getRowIndex();



  Operations operations();

  public interface Operations {
  
    }

      ComplexFactory complexFactory();

    interface ComplexFactory {
          @Property(name = "ContactDetails",
                type = "Microsoft.Test.OData.Services.OpenTypesServiceV4.ContactDetails")
      org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.ContactDetails newContactDetails();

          @Property(name = "AccountInfo",
                type = "Microsoft.Test.OData.Services.OpenTypesServiceV4.AccountInfo")
      org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.AccountInfo newAccountInfo();

        }
  }
