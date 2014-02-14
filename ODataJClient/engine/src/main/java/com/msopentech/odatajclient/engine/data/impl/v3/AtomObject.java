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
package com.msopentech.odatajclient.engine.data.impl.v3;

import java.net.URI;
import java.util.Date;

/**
 * Common methods for <tt>AtomEntry</tt> and <tt>AtomFeed</tt>.
 *
 * @see AtomEntry
 * @see AtomFeed
 */
public interface AtomObject {

    URI getBaseURI();

    void setBaseURI(String baseURI);

    String getId();

    void setId(String id);

    String getTitle();

    void setTitle(String title);

    String getSummary();

    void setSummary(String summary);

    Date getUpdated();

    void setUpdated(Date updated);
}
