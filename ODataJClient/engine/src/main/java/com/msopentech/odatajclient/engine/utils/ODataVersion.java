/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ODataVersion {

    V3("3.0"),
    V4("4.0");

    public static final String NS_DATASERVICES = "dataservices";

    public static final String NS_METADATA = "metadata";

    public static final String NS_DATA = "data";

    public static final String NS_SCHEME = "scheme";

    public static final String NAVIGATION_LINK_REL = "navigationLinkRel";

    public static final String ASSOCIATION_LINK_REL = "associationLinkRel";

    public static final String MEDIA_EDIT_LINK_REL = "mediaEditLinkRel";

    private static final Map<String, String> V3_NAMESPACES = Collections.unmodifiableMap(new HashMap<String, String>() {

        private static final long serialVersionUID = 3109256773218160485L;

        {
            put(NS_DATASERVICES, "http://schemas.microsoft.com/ado/2007/08/dataservices");
            put(NS_METADATA, "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata");
            put(NS_SCHEME, "http://schemas.microsoft.com/ado/2007/08/dataservices/scheme");
            put(NAVIGATION_LINK_REL, "http://schemas.microsoft.com/ado/2007/08/dataservices/related/");
            put(ASSOCIATION_LINK_REL, "http://schemas.microsoft.com/ado/2007/08/dataservices/relatedlinks/");
            put(MEDIA_EDIT_LINK_REL, "http://schemas.microsoft.com/ado/2007/08/dataservices/edit-media/");
        }
    });

    private static final Map<String, String> V4_NAMESPACES = Collections.unmodifiableMap(new HashMap<String, String>() {

        private static final long serialVersionUID = 3109256773218160485L;

        {
            put(NS_METADATA, "http://docs.oasis-open.org/odata/ns/metadata");
            put(NS_DATA, "http://docs.oasis-open.org/odata/ns/data");
        }
    });

    private final String version;

    private ODataVersion(final String version) {
        this.version = version;
    }

    public Map<String, String> getNamespaceMap() {
        return this == V3 ? V3_NAMESPACES : V4_NAMESPACES;
    }

    @Override
    public String toString() {
        return version;
    }

}
