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
package org.apache.olingo.server.api;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.etag.ServiceMetadataETagSupport;

/**
 * Metadata of an OData service like the Entity Data Model.
 */
public interface ServiceMetadata {
  /**
   * Gets the entity data model.
   * @return entity data model of this service
   */
  Edm getEdm();

  /**
   * Get the data-service version.
   * @return data service version of this service
   */
  ODataServiceVersion getDataServiceVersion();

  /**
   * Gets the list of references defined for this service.
   * @return list of defined emdx references of this service
   */
  List<EdmxReference> getReferences();

  /**
   * Gets the helper for ETag support of the metadata document (may be NULL).
   * @return metadata ETag support
   */
  ServiceMetadataETagSupport getServiceMetadataETagSupport();
}
