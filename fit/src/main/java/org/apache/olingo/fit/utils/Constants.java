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
package org.apache.olingo.fit.utils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.EnumMap;
import java.util.Map;

public class Constants {

  private static Map<ConstantKey, String> constants = new EnumMap<ConstantKey, String>(ConstantKey.class);

  /**
   * CR/LF.
   */
  public static final byte[] CRLF = { 13, 10 };
  public static final Charset ENCODING = Charset.forName("UTF-8");
  public static final CharsetDecoder DECODER = ENCODING.newDecoder();

  static {
    DECODER.onMalformedInput(CodingErrorAction.IGNORE);
    DECODER.onUnmappableCharacter(CodingErrorAction.IGNORE);

    constants.put(ConstantKey.JSON_ID_NAME, "@odata.id");
    constants.put(ConstantKey.JSON_TYPE_NAME, "@odata.type");
    constants.put(ConstantKey.JSON_NAVIGATION_SUFFIX, "@odata.navigationLink");
    constants.put(ConstantKey.JSON_EDITLINK_NAME, "@odata.editLink");
    constants.put(ConstantKey.DATASERVICES_NS, "http://docs.oasis-open.org/odata/ns/dataservices");
    constants.put(ConstantKey.METADATA_NS, "http://docs.oasis-open.org/odata/ns/metadata");
    constants.put(ConstantKey.GEORSS_NS, "http://www.georss.org/georss");
    constants.put(ConstantKey.GML_NS, "http://www.opengis.net/gml");
    constants.put(ConstantKey.EDM_NS, "http://docs.oasis-open.org/odata/ns/edm");
    constants.put(ConstantKey.ATOM_LINK_REL, "http://docs.oasis-open.org/odata/ns/related/");
    constants.put(ConstantKey.ODATA_SERVICE_VERSION, "OData-Version");
    constants.put(ConstantKey.DEFAULT_SERVICE_URL, "http://localhost:9080/stub/StaticService/V40/Static.svc/");
    constants.put(ConstantKey.ODATA_METADATA_PREFIX,
        "http://localhost:9080/stub/StaticService/V40/Static.svc/$metadata#");
    constants.put(ConstantKey.ODATA_METADATA_ENTITY_SUFFIX, "/$entity");
    constants.put(ConstantKey.ODATA_COUNT_NAME, "odata.count");
    constants.put(ConstantKey.ATOM_PROPERTY_PREFIX, "d:");
    constants.put(ConstantKey.ATOM_LINK_ENTRY, "application/atom+xml;type=entry");
    constants.put(ConstantKey.ATOM_LINK_FEED, "application/atom+xml;type=feed");
    constants.put(ConstantKey.INLINE_LOCAL, "inline");
    constants.put(ConstantKey.LINKS_FILE_PATH, "links");
    constants.put(ConstantKey.INLINE, "m:inline");
    constants.put(ConstantKey.PROPERTIES, "m:properties");
    constants.put(ConstantKey.LINK, "link");
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
    constants.put(ConstantKey.JSON_MEDIA_SUFFIX, "@odata.mediaEditLink");
    constants.put(ConstantKey.JSON_TYPE_SUFFIX, "@odata.type");
    constants.put(ConstantKey.XHTTP_HEADER_NAME, "X-HTTP-METHOD");
  }

  public static String get(final ConstantKey key) {
    return constants.get(key);
  }
}
