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
import org.w3c.dom.Element;

/**
 * REST resource for an <tt>ODataEntity</tt>.
 *
 * @see ODataEntity
 */
public interface Entry {

    /**
     * Gets ETag.
     *
     * @return ETag.
     */
    String getETag();

    /**
     * Sets ETag.
     *
     * @param eTag ETag.
     */
    void setETag(String eTag);

    /**
     * Gets base URI.
     *
     * @return base URI.
     */
    URI getBaseURI();

    /**
     * Gets entry type.
     *
     * @return entry type.
     */
    String getType();

    /**
     * Sets entry type.
     *
     * @param type entry type.
     */
    void setType(String type);

    /**
     * Gest entry ID.
     *
     * @return entry ID.
     */
    String getId();

    /**
     * Sets entry ID.
     *
     * @param id entry ID.
     */
    void setId(String id);

    /**
     * Gets entry self link.
     *
     * @return self link.
     */
    Link getSelfLink();

    /**
     * Sets entry self link.
     *
     * @param selfLink self link.
     */
    void setSelfLink(Link selfLink);

    /**
     * Gets entry edit link.
     *
     * @return edit link.
     */
    Link getEditLink();

    /**
     * Sets entry edit link.
     *
     * @param editLink edit link.
     */
    void setEditLink(Link editLink);

    /**
     * Gets association links.
     *
     * @return association links.
     */
    List<? extends Link> getAssociationLinks();

    /**
     * Adds an association link.
     *
     * @param associationLink link.
     * @return 'TRUE' in case of success; false otherwise.
     */
    boolean addAssociationLink(Link associationLink);

    /**
     * Sets association links.
     *
     * @param associationLinks links.
     * @return 'TRUE' in case of success; false otherwise.
     */
    void setAssociationLinks(List<Link> associationLinks);

    /**
     * Gets navigation links.
     *
     * @return links.
     */
    List<? extends Link> getNavigationLinks();

    /**
     * Adds a navigation link.
     *
     * @param navigationLink link.
     * @return 'TRUE' in case of success; false otherwise.
     */
    boolean addNavigationLink(Link navigationLink);

    /**
     * Sets navigation links.
     *
     * @param navigationLink links.
     * @return 'TRUE' in case of success; false otherwise.
     */
    void setNavigationLinks(List<Link> navigationLinks);

    /**
     * Gets media entity links.
     *
     * @return links.
     */
    List<? extends Link> getMediaEditLinks();

    /**
     * Adds a media entity link.
     *
     * @param mediaEditLink link.
     * @return 'TRUE' in case of success; false otherwise.
     */
    boolean addMediaEditLink(Link mediaEditLink);

    /**
     * Sets media entity links.
     *
     * @param mediaEditLinks links.
     * @return 'TRUE' in case of success; false otherwise.
     */
    void setMediaEditLinks(List<Link> mediaEditLinks);

    /**
     * Gets operations.
     *
     * @return operations.
     */
    List<ODataOperation> getOperations();

    /**
     * Gets content.
     *
     * @return content.
     */
    Element getContent();

    /**
     * Sets content.
     *
     * @param content content.
     */
    void setContent(Element content);

    /**
     * Gets media entry properties.
     *
     * @return media entry properties.
     */
    Element getMediaEntryProperties();

    /**
     * Sets media entry properties.
     *
     * @param content media entry properties.
     */
    void setMediaEntryProperties(Element content);

    /**
     * Gets media content type.
     *
     * @return media content type.
     */
    String getMediaContentType();

    /**
     * Gets media content resource.
     *
     * @return media content resource.
     */
    String getMediaContentSource();

    /**
     * Set media content source.
     *
     * @param mediaContentSource media content source.
     */
    void setMediaContentSource(String mediaContentSource);

    /**
     * Set media content type.
     *
     * @param mediaContentType media content type.
     */
    void setMediaContentType(String mediaContentType);

    /**
     * Checks if the current entry is a media entry.
     *
     * @return 'TRUE' if is a media entry; 'FALSE' otherwise.
     */
    boolean isMediaEntry();
}
