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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.data.ODataOperation;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import com.msopentech.odatajclient.engine.utils.XMLUtils;
import java.net.URI;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AtomDeserializer {

    private static final Logger LOG = LoggerFactory.getLogger(AtomDeserializer.class);

    private static final ISO8601DateFormat ISO_DATEFORMAT = new ISO8601DateFormat();

    private final ODataClient client;

    public AtomDeserializer(final ODataClient client) {
        this.client = client;
    }

    private void common(final Element input, final AtomObject object) {
        if (StringUtils.isNotBlank(input.getAttribute(ODataConstants.ATTR_XMLBASE))) {
            object.setBaseURI(input.getAttribute(ODataConstants.ATTR_XMLBASE));
        }

        final List<Element> ids = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_ID);
        if (!ids.isEmpty()) {
            object.setId(ids.get(0).getTextContent());
        }

        final List<Element> titles = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_TITLE);
        if (!titles.isEmpty()) {
            object.setTitle(titles.get(0).getTextContent());
        }

        final List<Element> summaries = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_SUMMARY);
        if (!summaries.isEmpty()) {
            object.setSummary(summaries.get(0).getTextContent());
        }

        final List<Element> updateds = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_UPDATED);
        if (!updateds.isEmpty()) {
            try {
                object.setUpdated(ISO_DATEFORMAT.parse(updateds.get(0).getTextContent()));
            } catch (Exception e) {
                LOG.error("Could not parse date {}", updateds.get(0).getTextContent(), e);
            }
        }
    }

    public AtomEntry entry(final Element input) {
        if (!ODataConstants.ATOM_ELEM_ENTRY.equals(input.getNodeName())) {
            return null;
        }

        final AtomEntry entry = new AtomEntry();

        common(input, entry);

        final String etag = input.getAttribute(ODataConstants.ATOM_ATTR_ETAG);
        if (StringUtils.isNotBlank(etag)) {
            entry.setETag(etag);
        }

        final List<Element> categories = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_CATEGORY);
        if (!categories.isEmpty()) {
            entry.setType(categories.get(0).getAttribute(ODataConstants.ATOM_ATTR_TERM));
        }

        final List<Element> links = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_LINK);
        for (Element linkElem : links) {
            final AtomLink link = new AtomLink();
            link.setRel(linkElem.getAttribute(ODataConstants.ATTR_REL));
            link.setTitle(linkElem.getAttribute(ODataConstants.ATTR_TITLE));
            link.setHref(linkElem.getAttribute(ODataConstants.ATTR_HREF));

            if (ODataConstants.SELF_LINK_REL.equals(link.getRel())) {
                entry.setSelfLink(link);
            } else if (ODataConstants.EDIT_LINK_REL.equals(link.getRel())) {
                entry.setEditLink(link);
            } else if (link.getRel().startsWith(
                    client.getWorkingVersion().getNamespaceMap().get(ODataVersion.NAVIGATION_LINK_REL))) {

                link.setType(linkElem.getAttribute(ODataConstants.ATTR_TYPE));
                entry.addNavigationLink(link);

                final List<Element> inlines = XMLUtils.getChildElements(linkElem, ODataConstants.ATOM_ELEM_INLINE);
                if (!inlines.isEmpty()) {
                    final List<Element> entries =
                            XMLUtils.getChildElements(inlines.get(0), ODataConstants.ATOM_ELEM_ENTRY);
                    if (!entries.isEmpty()) {
                        link.setInlineEntry(entry(entries.get(0)));
                    }

                    final List<Element> feeds =
                            XMLUtils.getChildElements(inlines.get(0), ODataConstants.ATOM_ELEM_FEED);
                    if (!feeds.isEmpty()) {
                        link.setInlineFeed(feed(feeds.get(0)));
                    }
                }
            } else if (link.getRel().startsWith(
                    client.getWorkingVersion().getNamespaceMap().get(ODataVersion.ASSOCIATION_LINK_REL))) {

                entry.addAssociationLink(link);
            } else if (link.getRel().startsWith(
                    client.getWorkingVersion().getNamespaceMap().get(ODataVersion.MEDIA_EDIT_LINK_REL))) {

                entry.addMediaEditLink(link);
            }
        }

        final List<Element> authors = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_AUTHOR);
        if (!authors.isEmpty()) {
            final AtomEntry.Author author = new AtomEntry.Author();
            for (Node child : XMLUtils.getChildNodes(input, Node.ELEMENT_NODE)) {
                if (ODataConstants.ATOM_ELEM_AUTHOR_NAME.equals(XMLUtils.getSimpleName(child))) {
                    author.setName(child.getTextContent());
                } else if (ODataConstants.ATOM_ELEM_AUTHOR_URI.equals(XMLUtils.getSimpleName(child))) {
                    author.setUri(child.getTextContent());
                } else if (ODataConstants.ATOM_ELEM_AUTHOR_EMAIL.equals(XMLUtils.getSimpleName(child))) {
                    author.setEmail(child.getTextContent());
                }
            }
            if (!author.isEmpty()) {
                entry.setAuthor(author);
            }
        }

        final List<Element> actions = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_ACTION);
        for (Element action : actions) {
            final ODataOperation operation = new ODataOperation();
            operation.setMetadataAnchor(action.getAttribute(ODataConstants.ATTR_METADATA));
            operation.setTitle(action.getAttribute(ODataConstants.ATTR_TITLE));
            operation.setTarget(URI.create(action.getAttribute(ODataConstants.ATTR_TARGET)));

            entry.addOperation(operation);
        }

        final List<Element> contents = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_CONTENT);
        if (!contents.isEmpty()) {
            final Element content = contents.get(0);

            List<Element> props = XMLUtils.getChildElements(content, ODataConstants.ELEM_PROPERTIES);
            if (props.isEmpty()) {
                entry.setMediaContentSource(content.getAttribute(ODataConstants.ATOM_ATTR_SRC));
                entry.setMediaContentType(content.getAttribute(ODataConstants.ATTR_TYPE));

                props = XMLUtils.getChildElements(input, ODataConstants.ELEM_PROPERTIES);
                if (!props.isEmpty()) {
                    entry.setMediaEntryProperties(props.get(0));
                }
            } else {
                entry.setContent(props.get(0));
            }
        }

        return entry;
    }

    public AtomFeed feed(final Element input) {
        if (!ODataConstants.ATOM_ELEM_FEED.equals(input.getNodeName())) {
            return null;
        }

        final AtomFeed feed = new AtomFeed();

        common(input, feed);

        final List<Element> entries = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_ENTRY);
        for (Element entry : entries) {
            feed.addEntry(entry(entry));
        }

        final List<Element> links = XMLUtils.getChildElements(input, ODataConstants.ATOM_ELEM_LINK);
        for (Element link : links) {
            if (ODataConstants.NEXT_LINK_REL.equals(link.getAttribute(ODataConstants.ATTR_REL))) {
                feed.setNext(URI.create(link.getAttribute(ODataConstants.ATTR_HREF)));
            }
        }

        final List<Element> counts = XMLUtils.getChildElements(input, ODataConstants.ATOM_ATTR_COUNT);
        if (!counts.isEmpty()) {
            try {
                feed.setCount(Integer.parseInt(counts.get(0).getTextContent()));
            } catch (Exception e) {
                LOG.error("Could not parse $inlinecount {}", counts.get(0).getTextContent(), e);
            }
        }

        return feed;
    }
}
