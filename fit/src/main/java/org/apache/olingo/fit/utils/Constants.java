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
package org.apache.olingo.fit.utils;

import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.Map;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class Constants {

  private final static Map<ConstantKey, String> v4constants = new EnumMap<ConstantKey, String>(ConstantKey.class);

  private final static Map<ConstantKey, String> constants = new EnumMap<ConstantKey, String>(ConstantKey.class);

  /**
   * CR/LF.
   */
  public static final byte[] CRLF = {13, 10};

  public static Charset encoding = Charset.forName("UTF-8");

  static {

    // -----------------------------
    // V4 only 
    // -----------------------------
    v4constants.put(ConstantKey.JSON_NAVIGATION_SUFFIX, "@odata.navigationLink");
    v4constants.put(ConstantKey.DATASERVICES_NS, "http://docs.oasis-open.org/odata/ns/dataservices");
    v4constants.put(ConstantKey.METADATA_NS, "http://docs.oasis-open.org/odata/ns/metadata");
    v4constants.put(ConstantKey.ODATA_SERVICE_VERSION, "OData-Version");
    v4constants.put(ConstantKey.DEFAULT_SERVICE_URL, "http://localhost:9080/StaticService/V40/Static.svc/");
    v4constants.put(ConstantKey.ODATA_METADATA_PREFIX, "http://localhost:9080/StaticService/V40/Static.svc/$metadata#");
    // -----------------------------

    // -----------------------------
    // V3 and defaults
    // -----------------------------
    constants.put(ConstantKey.ODATA_SERVICE_VERSION, "DataServiceVersion");
    constants.put(ConstantKey.DEFAULT_SERVICE_URL, "http://localhost:9080/StaticService/V30/Static.svc/");
    constants.put(ConstantKey.ODATA_COUNT_NAME, "odata.count");
    constants.put(ConstantKey.ODATA_METADATA_PREFIX, "http://localhost:9080/StaticService/V30/Static.svc/$metadata#");
    constants.put(ConstantKey.ATOM_DEF_TYPE, "Edm.String");
    constants.put(ConstantKey.ATOM_PROPERTY_PREFIX, "d:");
    constants.put(ConstantKey.ATOM_METADATA_PREFIX, "m:");
    constants.put(ConstantKey.ATOM_METADATA_NS, "xmlns:m");
    constants.put(ConstantKey.ATOM_DATASERVICE_NS, "xmlns:d");
    constants.put(ConstantKey.ATOM_LINK_ENTRY, "application/atom+xml;type=entry");
    constants.put(ConstantKey.ATOM_LINK_FEED, "application/atom+xml;type=feed");
    constants.put(ConstantKey.ATOM_LINK_REL, "http://schemas.microsoft.com/ado/2007/08/dataservices/related/");
    constants.put(ConstantKey.TYPE, "m:type");
    constants.put(ConstantKey.INLINE_LOCAL, "inline");
    constants.put(ConstantKey.INLINE_FILE_PATH, "inline");
    constants.put(ConstantKey.LINKS_FILE_PATH, "links");
    constants.put(ConstantKey.INLINE, "m:inline");
    constants.put(ConstantKey.CONTENT, "content");
    constants.put(ConstantKey.PROPERTIES, "m:properties");
    constants.put(ConstantKey.LINK, "link");
    constants.put(ConstantKey.METADATA_NS, "http://schemas.microsoft.com/ado/2007/08/dataservices/metadta");
    constants.put(ConstantKey.DATASERVICES_NS, "http://schemas.microsoft.com/ado/2007/08/dataservices");
    constants.put(ConstantKey.METADATA, "metadata");
    constants.put(ConstantKey.SERVICES, "services");
    constants.put(ConstantKey.FEED, "feed");
    constants.put(ConstantKey.ENTITY, "entity");
    constants.put(ConstantKey.REF, "references");
    constants.put(ConstantKey.MEDIA_CONTENT_FILENAME, "$value.bin");
    constants.put(ConstantKey.SKIP_TOKEN, "skiptoken");
    constants.put(ConstantKey.FILTER, "filter");
    constants.put(ConstantKey.ORDERBY, "orderby");
    constants.put(ConstantKey.JSON_VALUE_NAME, "value");
    constants.put(ConstantKey.JSON_NEXTLINK_NAME, "odata.nextLink");
    constants.put(ConstantKey.JSON_NEXTLINK_SUFFIX, "@odata.nextLink");
    constants.put(ConstantKey.JSON_ODATAMETADATA_NAME, "odata.metadata");
    constants.put(ConstantKey.JSON_NAVIGATION_BIND_SUFFIX, "@odata.bind");
    constants.put(ConstantKey.JSON_NAVIGATION_SUFFIX, "@odata.navigationLinkUrl");
    constants.put(ConstantKey.JSON_MEDIA_SUFFIX, "@odata.mediaEditLink");
    constants.put(ConstantKey.JSON_TYPE_NAME, "odata.type");
    constants.put(ConstantKey.JSON_TYPE_SUFFIX, "@odata.type");
    constants.put(ConstantKey.JSON_ID_NAME, "odata.id");
    constants.put(ConstantKey.JSON_EDITLINK_NAME, "odata.editLink");
    constants.put(ConstantKey.XHTTP_HEADER_NAME, "X-HTTP-METHOD");
    // -----------------------------
  }

  public static String get(final ConstantKey key) {
    return get(null, key);
  }

  public static String get(final ODataServiceVersion version, final ConstantKey key) {
    return (version == null || version.compareTo(ODataServiceVersion.V30) <= 0 || !v4constants.containsKey(key)
            ? constants : v4constants).get(key);
  }
}
