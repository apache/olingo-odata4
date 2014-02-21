/**
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
package com.msopentech.odatajclient.testservice;

public class Constants {

    protected final static String DEFAULT_SERVICE_URL = "http://localhost:9080/StaticService/V3/Static.svc/";

    protected final static String ODATA_METADATA_NAME = "odata.metadata";

    protected final static String ODATA_COUNT_NAME = "odata.count";

    protected final static String ODATA_METADATA_PREFIX = DEFAULT_SERVICE_URL + "$metadata#";

    protected final static String ATOM_PROPERTY_PREFIX = "d:";

    protected final static String ATOM_METADATA_PREFIX = "m:";

    protected final static String TYPE = ATOM_METADATA_PREFIX + "type";

    protected final static String PROPERTIES = ATOM_METADATA_PREFIX + "properties";

    protected final static String DATASERVICES_NS = "http://schemas.microsoft.com/ado/2007/08/dataservices";

    protected final static String METADATA_NS = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";

    protected final static String METADATA = "metadata";

    protected final static String SERVICES = "services";

    protected final static String FEED = "feed";

    protected final static String ENTITY = "entity";

    protected final static String SKIP_TOKEN = "skiptoken";

    protected final static String FILTER = "filter";

    protected final static String INLINE = "inline";

    protected final static String ORDERBY = "orderby";

}
