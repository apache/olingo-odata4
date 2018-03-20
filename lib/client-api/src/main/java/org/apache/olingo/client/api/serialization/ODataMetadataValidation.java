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
package org.apache.olingo.client.api.serialization;

import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.Edm;

public interface ODataMetadataValidation {

  /**
   * This method validates the metadata based on the Edm provided
   * @param edm
   */
  void validateMetadata(Edm edm);
  
  /**
   * This method validates the metadata based on the XMLMetadata provided
   * @param xmlMetadata
   */
  void validateMetadata(XMLMetadata xmlMetadata);
  
  /**
   * This method checks if if its a V4 metadata based on the XMLMetadata provided
   * @param xmlMetadata
   */
  boolean isV4Metadata(XMLMetadata xmlMetadata)throws Exception;
  
  /**
   * This method checks if if its a service document based on the XMLMetadata provided
   * @param xmlMetadata
   */
  boolean isServiceDocument(XMLMetadata xmlMetadata);
}
