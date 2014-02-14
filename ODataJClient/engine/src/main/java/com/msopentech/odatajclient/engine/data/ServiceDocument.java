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
package com.msopentech.odatajclient.engine.data;

import java.net.URI;
import java.util.List;

/**
 * REST resource for an <tt>ODataServiceDocument</tt>.
 *
 * @see ODataServiceDocument
 */
public interface ServiceDocument {

    String getTitle();

    /**
     * Gets base URI.
     *
     * @return base URI.
     */
    URI getBaseURI();

    /**
     * Returns metadata context.
     *
     * @return metadata context
     */
    String getMetadataContext();

    /**
     * Returns metadata ETag.
     *
     * @return metadata ETag
     */
    String getMetadataETag();

    /**
     * Gets top level entity sets.
     *
     * @return top level entity sets.
     */
    List<ServiceDocumentElement> getEntitySets();

    /**
     * Gets top level entity set with given name.
     *
     * @param name entity set name
     * @return entity set with given name if found, otherwise null
     */
    ServiceDocumentElement getEntitySetByName(String name);

    /**
     * Gets top level entity set with given title.
     *
     * @param title entity set title
     * @return entity set with given title if found, otherwise null
     */
    ServiceDocumentElement getEntitySetByTitle(String title);

    /**
     * Gets top level function imports.
     *
     * @return top level function imports.
     */
    List<ServiceDocumentElement> getFunctionImports();

    /**
     * Gets top level function import set with given name.
     *
     * @param name function import name
     * @return function import with given name if found, otherwise null
     */
    ServiceDocumentElement getFunctionImportByName(String name);

    /**
     * Gets top level function import with given title.
     *
     * @param title function import title
     * @return function import with given title if found, otherwise null
     */
    ServiceDocumentElement getFunctionImportByTitle(String title);

    /**
     * Gets top level singletons.
     *
     * @return top level singletons.
     */
    List<ServiceDocumentElement> getSingletons();

    /**
     * Gets top level singleton with given name.
     *
     * @param name singleton name
     * @return singleton with given name if found, otherwise null
     */
    ServiceDocumentElement getSingletonByName(String name);

    /**
     * Gets top level singleton with given title.
     *
     * @param title singleton title
     * @return singleton with given title if found, otherwise null
     */
    ServiceDocumentElement getSingletonByTitle(String title);

    /**
     * Gets related service documents.
     *
     * @return related service documents.
     */
    List<ServiceDocumentElement> getRelatedServiceDocuments();

    /**
     * Gets related service document with given title.
     *
     * @param title related service document title
     * @return related service document with given title if found, otherwise null
     */
    ServiceDocumentElement getRelatedServiceDocumentByTitle(String title);

}
