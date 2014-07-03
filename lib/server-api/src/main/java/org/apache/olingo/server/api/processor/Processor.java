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
package org.apache.olingo.server.api.processor;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.OData;

/**
 * Base interface for all processor types. Processors are responsible to read and write data and marshaling content
 * within a request - response cycle.
 */
public interface Processor {

  /**
   * Initialize processor for each http request - response cycle.
   * @param odata - Olingos root object which acts as a factory for various object types
   * @param edm - the edm which needs to be created before the OData request handling takes place
   */
  void init(OData odata, Edm edm);

}
