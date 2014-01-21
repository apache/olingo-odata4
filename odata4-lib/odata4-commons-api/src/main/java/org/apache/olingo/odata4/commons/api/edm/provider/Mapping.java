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
package org.apache.olingo.odata4.commons.api.edm.provider;

import org.apache.olingo.odata4.commons.api.edm.EdmMapping;

public class Mapping implements EdmMapping {

  private String value;
  private Object object;
  private String mediaResourceSourceKey;
  private String mediaResourceMimeTypeKey;

  @Override
  public String getInternalName() {
    return value;
  }

  @Override
  public Object getObject() {
    return object;
  }

  @Override
  public String getMediaResourceSourceKey() {
    return mediaResourceSourceKey;
  }

  @Override
  public String getMediaResourceMimeTypeKey() {
    return mediaResourceMimeTypeKey;
  }

  /**
   * Sets the value for this {@link Mapping}.
   * @param value
   * @return {@link Mapping} for method chaining
   */
  public Mapping setInternalName(final String value) {
    this.value = value;
    return this;
  }

  /**
   * Sets an object. This method can be used by a provider to set whatever it wants to associate with this.
   * @param object
   * @return {@link Mapping} for method chaining
   */
  public Mapping setObject(final Object object) {
    this.object = object;
    return this;
  }

  /**
   * Sets the key for the resource source key which is used for the lookup in the data map
   * @param mediaResourceSourceKey under which the source can be found in the data map
   * @return {@link Mapping} for method chaining
   */
  public Mapping setMediaResourceSourceKey(final String mediaResourceSourceKey) {
    this.mediaResourceSourceKey = mediaResourceSourceKey;
    return this;
  }

  /**
   * Sets the key for the resource mime type key which is used for the lookup in the data map
   * @param mediaResourceMimeTypeKey under which the mime type can be found in the data map
   * @return {@link Mapping} for method chaining
   */
  public Mapping setMediaResourceMimeTypeKey(final String mediaResourceMimeTypeKey) {
    this.mediaResourceMimeTypeKey = mediaResourceMimeTypeKey;
    return this;
  }
}
