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
package org.apache.olingo.server.api.serializer;

import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OlingoExtension;

/**
 * <p>Processors that supports custom content types can implement this interface.</p>
 * <p>The processor can also remove default content types if the default (de-)serializers
 * of Olingo are not used. By default this interface is not implemented and
 * a processor supports content types implemented by Olingo's default (de-)serializer
 * (e.g., <code>application/xml</code> for the metadata and
 * </code>application/json</code> for the service document).</p>
 * <p>Requesting a content type that is not supported results in an HTTP error
 * 406 (Not Acceptable); sending content of an unsupported type results in an
 * HTTP error 415 (Unsupported Media Type).</p>
 */
public interface CustomContentTypeSupport extends OlingoExtension {

  /**
   * Returns a list of supported content types.
   * @param defaultContentTypes content types supported by Olingo's (de-)serializer
   * @param type the current type of representation
   * @return modified list of supported content types
   */
  List<ContentType> modifySupportedContentTypes(
      List<ContentType> defaultContentTypes, RepresentationType type);
}
