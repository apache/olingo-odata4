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
package org.apache.olingo.client.api.op.v3;

import java.io.InputStream;
import org.apache.olingo.client.api.domain.v3.ODataLinkCollection;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.client.api.op.CommonODataReader;

public interface ODataReader extends CommonODataReader {

  /**
   * Parses a $links request response.
   *
   * @param input stream to de-serialize.
   * @param format de-serialize as XML or JSON
   * @return List of URIs.
   */
  ODataLinkCollection readLinks(InputStream input, ODataFormat format);

}
