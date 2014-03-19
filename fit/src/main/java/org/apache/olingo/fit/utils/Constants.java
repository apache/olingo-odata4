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

public class Constants {

  public final static String ODATA_SERVICE_VERSION = "DataServiceVersion";

  public final static String DEFAULT_SERVICE_URL = "http://localhost:9080/StaticService/V30/Static.svc/";

  public final static String ODATA_COUNT_NAME = "odata.count";

  public final static String ODATA_METADATA_PREFIX = DEFAULT_SERVICE_URL + "$metadata#";

  public final static String ATOM_DEF_TYPE = "Edm.String";

  public final static String ATOM_PROPERTY_PREFIX = "d:";

  public final static String ATOM_METADATA_PREFIX = "m:";

  public final static String ATOM_METADATA_NS = "xmlns:m";

  public final static String ATOM_DATASERVICE_NS = "xmlns:d";

  public final static String ATOM_LINK_ENTRY = "application/atom+xml;type=entry";

  public final static String ATOM_LINK_FEED = "application/atom+xml;type=feed";

  public final static String ATOM_LINK_REL = "http://schemas.microsoft.com/ado/2007/08/dataservices/related/";

  public final static String TYPE = ATOM_METADATA_PREFIX + "type";

  public final static String INLINE_LOCAL = "inline";

  public final static String INLINE_FILE_PATH = "inline";

  public final static String LINKS_FILE_PATH = "links";

  public final static String INLINE = ATOM_METADATA_PREFIX + INLINE_LOCAL;

  public final static String CONTENT = "content";

  public final static String PROPERTIES = ATOM_METADATA_PREFIX + "properties";

  public final static String LINK = "link";

  public final static String DATASERVICES_NS = "http://schemas.microsoft.com/ado/2007/08/dataservices";

  public final static String METADATA_NS = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";

  public final static String METADATA = "metadata";

  public final static String SERVICES = "services";

  public final static String FEED = "feed";

  public final static String ENTITY = "entity";

  public final static String MEDIA_CONTENT_FILENAME = "$value.bin";

  public final static String SKIP_TOKEN = "skiptoken";

  public final static String FILTER = "filter";

  public final static String ORDERBY = "orderby";

  public final static String JSON_VALUE_NAME = "value";

  public final static String JSON_NEXTLINK_NAME = "odata.nextLink";

  public final static String JSON_NEXTLINK_SUFFIX = "@" + JSON_NEXTLINK_NAME;

  public final static String JSON_ODATAMETADATA_NAME = "odata.metadata";

  public final static String JSON_NAVIGATION_BIND_SUFFIX = "@odata.bind";

  public final static String JSON_NAVIGATION_SUFFIX = "@odata.navigationLinkUrl";

  public final static String JSON_MEDIA_SUFFIX = "@odata.mediaEditLink";

  public final static String JSON_TYPE_NAME = "odata.type";

  public final static String JSON_TYPE_SUFFIX = "@" + JSON_TYPE_NAME;

  public final static String JSON_ID_NAME = "odata.id";

  public final static String JSON_EDITLINK_NAME = "odata.editLink";

  public final static String XHTTP_HEADER_NAME = "X-HTTP-METHOD";

}
