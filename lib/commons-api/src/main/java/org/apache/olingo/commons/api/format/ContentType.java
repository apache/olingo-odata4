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
package org.apache.olingo.commons.api.format;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public abstract class ContentType {

  public static final String APPLICATION_ATOM_XML = "application/atom+xml";

  public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

  public static final String APPLICATION_JSON = "application/json";

  public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

  public static final String MULTIPART_MIXED = "multipart/mixed";

  public static final String APPLICATION_SVG_XML = "application/svg+xml";

  public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";

  public static final String APPLICATION_XML = "application/xml";

  public static final String MULTIPART_FORM_DATA = "multipart/form-data";

  public static final String TEXT_HTML = "text/html";

  public static final String TEXT_PLAIN = "text/plain";

  public static final String TEXT_XML = "text/xml";

  public static final String WILDCARD = "*/*";

  public static final EnumMap<ODataServiceVersion, Map<String, String>> formatPerVersion =
          new EnumMap<ODataServiceVersion, Map<String, String>>(ODataServiceVersion.class);

  static {
    final Map<String, String> v3 = new HashMap<String, String>();
    v3.put(ODataFormat.JSON_NO_METADATA.name(), ContentType.APPLICATION_JSON + ";odata=nometadata");
    v3.put(ODataFormat.JSON.name(), ContentType.APPLICATION_JSON + ";odata=minimalmetadata");
    v3.put(ODataFormat.JSON_FULL_METADATA.name(), ContentType.APPLICATION_JSON + ";odata=fullmetadata");
    formatPerVersion.put(ODataServiceVersion.V30, v3);

    final Map<String, String> v4 = new HashMap<String, String>();
    v4.put(ODataFormat.JSON_NO_METADATA.name(), ContentType.APPLICATION_JSON + ";odata.metadata=none");
    v4.put(ODataFormat.JSON.name(), ContentType.APPLICATION_JSON + ";odata.metadata=minimal");
    v4.put(ODataFormat.JSON_FULL_METADATA.name(), ContentType.APPLICATION_JSON + ";odata.metadata=full");
    formatPerVersion.put(ODataServiceVersion.V40, v4);
  }
}
