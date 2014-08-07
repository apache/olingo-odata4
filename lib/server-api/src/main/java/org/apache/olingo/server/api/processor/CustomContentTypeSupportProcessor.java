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

import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;

/**
 * A processor which supports custom content types can implement this interface. The processor can also remove default
 * content types if the default serializer of Olingo are not used. By default this interface is not implemented and
 * a processor supports content types implemented by Olingos default serializer (e.g. application/xml for metadata and
 * application/json for service document).
 * Requesting a content type which is not supported results in a http error 406 (Not Acceptable).
 */
public interface CustomContentTypeSupportProcessor {

  /**
   * Returns a list of supported content types.
   * @param defaultContentTypes content types supported by Olingos serializer
   * @return modified list of supported content types
   * 
   */
  public List<ContentType> modifySupportedContentTypes(
      List<ContentType> defaultContentTypes, Class<? extends Processor> processorClass);

}
