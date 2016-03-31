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
package org.apache.olingo.server.core.deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;

public class AbstractODataDeserializerTest {

  protected static final String NAMESPACE = "Namespace1_Alias";
  protected static final Edm edm = OData.newInstance()
      .createServiceMetadata(new EdmTechProvider(), Collections.<EdmxReference> emptyList())
      .getEdm();
  protected static final ServiceMetadata metadata = OData.newInstance()
      .createServiceMetadata(new EdmTechProvider(), Collections.<EdmxReference> emptyList());
  
  protected InputStream getFileAsStream(final String filename) throws IOException {
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
    if (in == null) {
      throw new IOException("Requested file '" + filename + "' was not found.");
    }
    return in;
  }
}
