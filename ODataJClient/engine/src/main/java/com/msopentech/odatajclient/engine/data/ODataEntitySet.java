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
import java.util.ArrayList;
import java.util.List;

/**
 * OData entity collection.
 * If pagination was used to get this instance, forward page navigation URI will be available.
 */
public class ODataEntitySet extends ODataItem implements ODataInvokeResult {

    private static final long serialVersionUID = 9039605899821494024L;

    /**
     * Link to the next page.
     */
    protected URI next;

    /**
     * Number of ODataEntities contained in this feed.
     * If <tt>$inlinecount</tt> was requested, this value comes from there.
     */
    protected Integer count;

    /**
     * OData entities contained in this feed.
     */
    protected List<ODataEntity> entities = new ArrayList<ODataEntity>();

    /**
     * Constructor.
     */
    ODataEntitySet() {
        super(null);
    }

    /**
     * Constructor.
     *
     * @param next next link.
     */
    ODataEntitySet(final URI next) {
        super(null);
        this.next = next;
    }

    /**
     * Gets next page link.
     *
     * @return next page link; null value if single page or last page reached.
     */
    public URI getNext() {
        return next;
    }

    /**
     * Sets in-line count.
     *
     * @param count in-line count value.
     */
    public void setCount(final int count) {
        this.count = count;
    }

    /**
     * Gets in-line count.
     *
     * @return in-line count value.
     */
    public int getCount() {
        return count == null ? entities.size() : count;
    }

    /**
     * Adds entity to the current feed.
     *
     * @param entity entity to be added.
     * @return 'FALSE' if already exists; 'TRUE' otherwise.
     */
    public boolean addEntity(final ODataEntity entity) {
        return entities.contains(entity) ? false : entities.add(entity);
    }

    /**
     * Removes an entity.
     *
     * @param entity entity to be removed.
     * @return 'TRUE' in case of success; 'FALSE' otherwise.
     */
    public boolean removeEntity(final ODataEntity entity) {
        return entities.remove(entity);
    }

    /**
     * Gets contained entities.
     *
     * @return feed entries.
     */
    public List<ODataEntity> getEntities() {
        return entities;
    }
}
