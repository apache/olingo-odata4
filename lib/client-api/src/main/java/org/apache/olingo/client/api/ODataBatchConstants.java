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
package org.apache.olingo.client.api;

/**
 * Constant values related to the OData protocol.
 */
public class ODataBatchConstants {

  /**
   * Batch/Changeset content type.
   */
  public static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";

  /**
   * Batch item content type.
   */
  public static final String ITEM_CONTENT_TYPE = "application/http";

  /**
   * Boundary key.
   */
  public static final String BOUNDARY = "boundary";

  /**
   * Item content type.
   */
  public static String ITEM_CONTENT_TYPE_LINE = "Content-Type: application/http";

  /**
   * Item transfer encoding.
   */
  public static String ITEM_TRANSFER_ENCODING_LINE = "Content-Transfer-Encoding: binary";

  /**
   * Content id header name.
   */
  public static String CHANGESET_CONTENT_ID_NAME = "Content-ID";

}
