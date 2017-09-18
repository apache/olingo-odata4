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
package org.apache.olingo.commons.api;

/**
 * Constant values related to the OData protocol.
 */
public interface IConstants {
  
  public String getMetadata();
  
  public String getType();
  
  public String getId();
  
  public String getReadLink();

  public String getEditLink();
  
  public String getContext();
  
  public String getEtag();
  
  public String getMediaEtag();
  
  public String getMediaContentType();
  
  public String getMediaReadLink();
  
  public String getMediaEditLink();
  
  public String getMetadataEtag();
  
  public String getBind();
  
  public String getAssociationLink();
  
  public String getNavigationLink();
  
  public String getCount();
  
  public String getNextLink();
  
  public String getDeltaLink();
}
