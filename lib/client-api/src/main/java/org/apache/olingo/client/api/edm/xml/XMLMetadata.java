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
package org.apache.olingo.client.api.edm.xml;

import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

/**
 * Entry point for access information about EDM metadata.
 */
public interface XMLMetadata {

  /**
   * Returns the Schema at the specified position in the EdM metadata document.
   *
   * @param index index of the Schema to return
   * @return the Schema at the specified position in the EdM metadata document
   */
  CsdlSchema getSchema(final int index);

  /**
   * Returns the Schema with the specified key (namespace or alias) in the EdM metadata document.
   *
   * @param key namespace or alias
   * @return the Schema with the specified key in the EdM metadata document
   */
  CsdlSchema getSchema(final String key);

  /**
   * Returns all Schema objects defined in the EdM metadata document.
   *
   * @return all Schema objects defined in the EdM metadata document
   */
  List<CsdlSchema> getSchemas();

  Map<String, CsdlSchema> getSchemaByNsOrAlias();
  
  List<Reference> getReferences();
}
