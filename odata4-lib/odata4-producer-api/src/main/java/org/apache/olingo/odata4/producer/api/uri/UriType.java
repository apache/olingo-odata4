/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.api.uri;

public enum UriType {
  TYPE_ENTITY_SET,
  TYPE_ENTITY_SET_COUNT,
  TYPE_MEDIA_REFERENCE,
  TYPE_REFERENCE_COLLECTION,
  /*
   * Singleton
   */
  TYPE_ENTITY,
  TYPE_MEDIA_STREAM,
  TYPE_REFERENCE,

  /*
   * Property Path
   */
  TYPE_PROPERTY_PRIMITIVE,
  TYPE_PROPERTY_PRIMITIVE_COLLECTION,
  TYPE_PROPERTY_PRIMITIVE_VALUE,
  TYPE_PROPERTY_COMPLEX,

  /*
   * Crossjoin
   */
  TYPE_CROSSJOIN,

  /*
   * $all
   */
  TYPE_SERVICE_ALL,
  /*
   * ActionImport
   */
  TYPE_AI_ENTITY,
  /*
   * FunctionImport
   */
  TYPE_FI_ENTITY,
  TYPE_FI_ENTITY_SET,
  TYPE_FI_ENTITY_SET_COUNT,
  TYPE_FI_PROPERTY_PRIMITIVE,
  TYPE_FI_PROPERTY_PRIMITIVE_COLL,
  TYPE_FI_PROPERTY_PRIMITIVE_COLL_COUNT,
  TYPE_FI_PROPERTY_COMPLEX,
  TYPE_FI_PROPERTY_COMPLEX_COLL,
  TYPE_FI_PROPERTY_COMPLEX_COLL_COUNT,
  /*
   * BoundFunction
   */
  TYPE_BF_ENTITY,
  TYPE_BF_ENTITY_SET,
  TYPE_BF_PROP_PRIM,
  TYPE_BF_PROP_COMP,
  TYPE_BF_PROP_COMP_COLL,
  TYPE_BF_PROP_PRIM_COLL,
  /*
   * BoundAction
   */
  TYPE_BA_ENTITY_SET,
  TYPE_BA_ENTITY,
  TYPE_BA_PROP_PRIM,
  TYPE_BA_PROP_PRIM_COLL,
  TYPE_BA_PROP_COMP;
}
