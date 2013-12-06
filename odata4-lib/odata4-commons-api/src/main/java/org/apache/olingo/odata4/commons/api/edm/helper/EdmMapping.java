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
package org.apache.olingo.odata4.commons.api.edm.helper;

/**
 * EdmMapping holds custom mapping information which can be applied to a CSDL element.
 */
public interface EdmMapping {

  /**
   * Get the mapping value
   * 
   * @return mapping name as String
   */
  String getInternalName();

  /**
   * Get the set object for this mapping
   * 
   * @return {@link Object} object
   */
  Object getObject();

  /**
   * Gets the key under which the resource source value can be found in the data map.
   * @return the key of the media resource source
   */
  String getMediaResourceSourceKey();

  /**
   * Gets the key under which the resource mime type can be found in the data map.
   * @return the key of the media resource type
   */
  String getMediaResourceMimeTypeKey();
}
