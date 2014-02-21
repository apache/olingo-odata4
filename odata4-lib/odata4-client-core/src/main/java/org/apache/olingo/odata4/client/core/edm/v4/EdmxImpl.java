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
package org.apache.olingo.odata4.client.core.edm.v4;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmx;

public class EdmxImpl extends AbstractEdmx<
        DataServicesImpl, SchemaImpl, EntityContainerImpl, EntityTypeImpl, ComplexTypeImpl, FunctionImportImpl> {

  private static final long serialVersionUID = -8031883176876401375L;

  private final List<ReferenceImpl> references = new ArrayList<ReferenceImpl>();

  public List<ReferenceImpl> getReferences() {
    return references;
  }

}
