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
package org.apache.olingo.client.api;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * Constant values related to the OData protocol.
 */
public class Constants {

  // Other stuff
  public final static String UTF8 = "UTF-8";

  public final static String NAME = "name";

  public final static String PROPERTIES = "properties";

  // XML namespaces and prefixes
  public final static String NS_ATOM = "http://www.w3.org/2005/Atom";

  public static final String NS_GEORSS = "http://www.georss.org/georss";

  public static final String NS_GML = "http://www.opengis.net/gml";

  public static final String XMLNS_DATASERVICES = XMLConstants.XMLNS_ATTRIBUTE + ":d";

  public static final String PREFIX_DATASERVICES = "d";

  public static final String XMLNS_METADATA = XMLConstants.XMLNS_ATTRIBUTE + ":m";

  public static final String PREFIX_METADATA = "m";

  public static final String XMLNS_GEORSS = XMLConstants.XMLNS_ATTRIBUTE + ":georss";

  public static final String PREFIX_GEORSS = "georss";

  public static final String XMLNS_GML = XMLConstants.XMLNS_ATTRIBUTE + ":gml";

  public static final String PREFIX_GML = "gml";

  public final static String SRS_URLPREFIX = "http://www.opengis.net/def/crs/EPSG/0/";

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

  public static final String ATTR_M_TYPE = PREFIX_METADATA + ":" + ATTR_TYPE;

  public final static String ATTR_NULL = "null";

  public static final String ATTR_M_NULL = PREFIX_METADATA + ":" + ATTR_NULL;

  public static final String ATTR_XMLBASE = "xml:base";

  public static final String ATTR_REL = "rel";

  public static final String ATTR_HREF = "href";

  public static final String ATTR_METADATA = "metadata";

  public static final String ATTR_TITLE = "title";

  public static final String ATTR_TARGET = "target";

  public static final String ELEM_COLLECTION = "collection";

  public static final String ATTR_SRSNAME = "srsName";

  public static final String ELEM_POINT = "Point";

  public static final String ELEM_MULTIPOINT = "MultiPoint";

  public static final String ELEM_POINTMEMBERS = "pointMembers";

  public static final QName QNAME_POINTMEMBERS = new QName(Constants.NS_GML, ELEM_POINTMEMBERS);

  public static final String ELEM_LINESTRING = "LineString";

  public static final QName QNAME_LINESTRING = new QName(Constants.NS_GML, ELEM_LINESTRING);

  public static final String ELEM_MULTILINESTRING = "MultiCurve";

  public static final String ELEM_LINESTRINGMEMBERS = "curveMembers";

  public static final String ELEM_POLYGON = "Polygon";

  public static final QName QNAME_POLYGON = new QName(Constants.NS_GML, ELEM_POLYGON);

  public static final String ELEM_POLYGON_EXTERIOR = "exterior";

  public static final QName QNAME_POLYGON_EXTERIOR = new QName(Constants.NS_GML, ELEM_POLYGON_EXTERIOR);

  public static final String ELEM_POLYGON_INTERIOR = "interior";

  public static final QName QNAME_POLYGON_INTERIOR = new QName(Constants.NS_GML, ELEM_POLYGON_INTERIOR);

  public static final String ELEM_POLYGON_LINEARRING = "LinearRing";

  public static final String ELEM_MULTIPOLYGON = "MultiSurface";

  public static final String ELEM_SURFACEMEMBERS = "surfaceMembers";

  public static final String ELEM_GEOCOLLECTION = "MultiGeometry";

  public static final String ELEM_GEOMEMBERS = "geometryMembers";

  public static final QName QNAME_GEOMEMBERS = new QName(Constants.NS_GML, ELEM_GEOMEMBERS);

  public static final String ELEM_POS = "pos";

  public static final String ELEM_POSLIST = "posList";

  public static final String ELEM_PROPERTY = "property";

  public static final String ELEM_URI = "uri";

  // JSON stuff
  public final static String JSON_CONTEXT = "@odata.context";

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

  public final static String JSON_NULL = "odata.null";

  public final static String JSON_VALUE = "value";

  public final static String JSON_URL = "url";

  public final static String JSON_COORDINATES = "coordinates";

  public final static String JSON_GEOMETRIES = "geometries";

  public final static String JSON_CRS = "crs";

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
