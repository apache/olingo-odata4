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

package org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.ComplexType;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import java.io.Serializable;
import java.io.InputStream;
//CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.PrimitiveKeysService")
@org.apache.olingo.ext.proxy.api.annotations.EntityContainer(name = "TestContext",
  namespace = "Microsoft.Test.OData.Services.PrimitiveKeysService",
  isDefaultEntityContainer = true)
public interface TestContext extends PersistenceManager {

    EdmTimeSet getEdmTimeSet();

    EdmDoubleSet getEdmDoubleSet();

    EdmStringSet getEdmStringSet();

    EdmBooleanSet getEdmBooleanSet();

    EdmBinarySet getEdmBinarySet();

    EdmDecimalSet getEdmDecimalSet();

    EdmGuidSet getEdmGuidSet();

    EdmInt32Set getEdmInt32Set();

    EdmInt64Set getEdmInt64Set();

    EdmInt16Set getEdmInt16Set();

    EdmByteSet getEdmByteSet();

    EdmDateTimeSet getEdmDateTimeSet();

    Folders getFolders();

    EdmSingleSet getEdmSingleSet();

    EdmDateTimeOffsetSet getEdmDateTimeOffsetSet();





  Operations operations();

  public interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
  
    }

  <NE extends EntityType<?>> NE newEntityInstance(Class<NE> ref);

  <T extends EntityType<?>, NEC extends EntityCollection<T, ?, ?>> NEC newEntityCollection(Class<NEC> ref);

  <NE extends ComplexType<?>> NE newComplexInstance(Class<NE> ref);

  <T extends ComplexType<?>, NEC extends ComplexCollection<T, ?, ?>> NEC newComplexCollection(Class<NEC> ref);

  <T extends Serializable, NEC extends PrimitiveCollection<T>> NEC newPrimitiveCollection(Class<T> ref);

  EdmStreamValue newEdmStreamValue(String contentType, InputStream stream);
}
