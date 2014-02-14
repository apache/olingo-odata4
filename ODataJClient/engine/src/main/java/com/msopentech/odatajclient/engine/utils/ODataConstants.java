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

import javax.xml.XMLConstants;

/**
 * Constant values related to the OData protocol.
 */
public class ODataConstants {

    // Other stuff
    public final static String UTF8 = "UTF-8";

    public final static String NAME = "name";

    public final static String PROPERTIES = "properties";

    // XML namespaces and prefixes
    public final static String NS_ATOM = "http://www.w3.org/2005/Atom";

    public static final String NS_GEORSS = "http://www.georss.org/georss";

    public static final String NS_GML = "http://www.opengis.net/gml";

    public static final String XMLNS_DATASERVICES = XMLConstants.XMLNS_ATTRIBUTE + ":d";

    public static final String PREFIX_DATASERVICES = "d:";

    public static final String XMLNS_METADATA = XMLConstants.XMLNS_ATTRIBUTE + ":m";

    public static final String PREFIX_METADATA = "m:";

    public static final String XMLNS_GEORSS = XMLConstants.XMLNS_ATTRIBUTE + ":georss";

    public static final String PREFIX_GEORSS = "georss:";

    public static final String XMLNS_GML = XMLConstants.XMLNS_ATTRIBUTE + ":gml";

    public static final String PREFIX_GML = "gml:";

    /**
     * Edit link rel value.
     */
    public static final String EDIT_LINK_REL = "edit";

    /**
     * Self link rel value.
     */
    public static final String SELF_LINK_REL = "self";

    public static final String NEXT_LINK_REL = "next";

    // XML elements and attributes
    public static final String ELEM_PROPERTIES = PREFIX_METADATA + PROPERTIES;

    public static final String ELEM_ELEMENT = "element";

    public final static String ATTR_TYPE = "type";

    public static final String ATTR_M_TYPE = PREFIX_METADATA + ATTR_TYPE;

    public static final String ATTR_NULL = PREFIX_METADATA + "null";

    public static final String ATTR_XMLBASE = "xml:base";

    public static final String ATTR_REL = "rel";

    public static final String ATTR_HREF = "href";

    public static final String ATTR_METADATA = "metadata";

    public static final String ATTR_TITLE = "title";

    public static final String ATTR_TARGET = "target";

    public static final String ELEM_COLLECTION = "collection";

    public static final String ATTR_SRSNAME = PREFIX_GML + "srsName";

    public static final String ELEM_POINT = PREFIX_GML + "Point";

    public static final String ELEM_MULTIPOINT = PREFIX_GML + "MultiPoint";

    public static final String ELEM_POINTMEMBERS = PREFIX_GML + "pointMembers";

    public static final String ELEM_LINESTRING = PREFIX_GML + "LineString";

    public static final String ELEM_MULTILINESTRING = PREFIX_GML + "MultiCurve";

    public static final String ELEM_LINESTRINGMEMBERS = PREFIX_GML + "curveMembers";

    public static final String ELEM_POLYGON = PREFIX_GML + "Polygon";

    public static final String ELEM_POLYGON_EXTERIOR = PREFIX_GML + "exterior";

    public static final String ELEM_POLYGON_INTERIOR = PREFIX_GML + "interior";

    public static final String ELEM_POLYGON_LINEARRING = PREFIX_GML + "LinearRing";

    public static final String ELEM_MULTIPOLYGON = PREFIX_GML + "MultiSurface";

    public static final String ELEM_SURFACEMEMBERS = PREFIX_GML + "surfaceMembers";

    public static final String ELEM_GEOCOLLECTION = PREFIX_GML + "MultiGeometry";

    public static final String ELEM_GEOMEMBERS = PREFIX_GML + "geometryMembers";

    public static final String ELEM_POS = PREFIX_GML + "pos";

    public static final String ELEM_POSLIST = PREFIX_GML + "posList";

    public static final String ELEM_PROPERTY = "property";

    public static final String ELEM_URI = "uri";

    // JSON stuff
    public final static String JSON_METADATA = "odata.metadata";

    public final static String JSON_TYPE = "odata.type";

    public final static String JSON_ETAG = "odata.etag";

    public final static String JSON_MEDIA_ETAG = "odata.mediaETag";

    public final static String JSON_ID = "odata.id";

    public final static String JSON_READ_LINK = "odata.readLink";

    public final static String JSON_EDIT_LINK = "odata.editLink";

    public final static String JSON_MEDIAREAD_LINK = "odata.mediaReadLink";

    public final static String JSON_MEDIAEDIT_LINK = "odata.mediaEditLink";

    public final static String JSON_MEDIA_CONTENT_TYPE = "odata.mediaContentType";

    public final static String JSON_NAVIGATION_LINK_SUFFIX = "@odata.navigationLinkUrl";

    public final static String JSON_BIND_LINK_SUFFIX = "@odata.bind";

    public final static String JSON_ASSOCIATION_LINK_SUFFIX = "@odata.associationLinkUrl";

    public final static String JSON_MEDIAEDIT_LINK_SUFFIX = "@odata.mediaEditLink";

    public final static String JSON_VALUE = "value";

    public final static String JSON_URL = "url";

    public final static String JSON_COORDINATES = "coordinates";

    public final static String JSON_GEOMETRIES = "geometries";

    public final static String JSON_CRS = "crs";

    public final static String JSON_GIS_URLPREFIX = "http://www.opengis.net/def/crs/EPSG/0/";

    // Atom stuff
    public final static String ATOM_ELEM_ENTRY = "entry";

    public final static String ATOM_ELEM_FEED = "feed";

    public final static String ATOM_ELEM_CATEGORY = "category";

    public final static String ATOM_ELEM_ID = "id";

    public final static String ATOM_ELEM_LINK = "link";

    public final static String ATOM_ELEM_CONTENT = "content";

    public static final String ATOM_ELEM_TITLE = "title";

    public static final String ATOM_ELEM_SUMMARY = "summary";

    public static final String ATOM_ELEM_UPDATED = "updated";

    public static final String ATOM_ELEM_AUTHOR = "author";

    public static final String ATOM_ELEM_AUTHOR_NAME = "name";

    public static final String ATOM_ELEM_AUTHOR_URI = "uri";

    public static final String ATOM_ELEM_AUTHOR_EMAIL = "email";

    public static final String ATOM_ELEM_ACTION = PREFIX_METADATA + "action";

    public static final String ATOM_ELEM_INLINE = PREFIX_METADATA + "inline";

    public static final String ATOM_ATTR_TITLE = "atom:title";

    public static final String ATOM_ATTR_TERM = "term";

    public static final String ATOM_ATTR_SCHEME = "scheme";

    public static final String ATOM_ATTR_SRC = "src";

    public static final String ATOM_ATTR_ETAG = PREFIX_METADATA + "etag";

    public static final String ATOM_ATTR_COUNT = PREFIX_METADATA + "count";

}
