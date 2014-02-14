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
package com.msopentech.odatajclient.engine.uri;

import com.msopentech.odatajclient.engine.client.V3Configuration;

public class V3URIBuilder extends AbstractURIBuilder {

    private static final long serialVersionUID = -3506851722447870532L;

    private final V3Configuration configuration;

    public V3URIBuilder(final V3Configuration configuration, final String serviceRoot) {
        super(serviceRoot);
        this.configuration = configuration;
    }

    @Override
    protected V3Configuration getConfiguration() {
        return configuration;
    }

}
