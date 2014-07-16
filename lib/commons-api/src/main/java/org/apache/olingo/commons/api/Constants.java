/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.api;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * Constant values related to the OData protocol.
 */
public interface Constants {

  // Other stuff
  public final static String UTF8 = "UTF-8";

  public final static String METADATA = "$metadata";

  public final static Integer DEFAULT_PRECISION = 40;
  public final static Integer DEFAULT_SCALE = 25;

  public final static String PROXY_TERM_CLASS_LIST = "org.apache.olingo.ext.proxy.term";
  public final static String PROXY_ENUM_CLASS_LIST = "org.apache.olingo.ext.proxy.enum";
  public final static String PROXY_COMPLEX_CLASS_LIST = "org.apache.olingo.ext.proxy.complex";

  // XML namespaces and prefixes
  public final static String NS_ATOM = "http://www.w3.org/2005/Atom";
  public static final String NS_GEORSS = "http://www.georss.org/georss";
  public static final String NS_GML = "http://www.opengis.net/gml";
  public static final String NS_ATOM_TOMBSTONE = "http://purl.org/atompub/tombstones/1.0";

  public static final String PREFIX_DATASERVICES = "d";
  public static final String PREFIX_METADATA = "m";
  public static final String PREFIX_GEORSS = "georss";
  public static final String PREFIX_GML = "gml";

  public final static String SRS_URLPREFIX = "http://www.opengis.net/def/crs/EPSG/0/";

  // Link rel(s)
  public static final String EDIT_LINK_REL = "edit";
  public static final String SELF_LINK_REL = "self";
  public static final String EDITMEDIA_LINK_REL = "edit-media";
  public static final String NEXT_LINK_REL = "next";

  // XML elements and attributes
  public static final String PROPERTIES = "properties";

  public static final String ELEM_ELEMENT = "element";

  public final static String ATTR_TYPE = "type";
  public final static String ATTR_NULL = "null";

  public static final String ATTR_XML_BASE = "base";
  public static final QName QNAME_ATTR_XML_BASE = new QName(XMLConstants.XML_NS_URI, ATTR_XML_BASE);

  public static final String CONTEXT = "context";

  public static final String ATTR_REL = "rel";
  public static final String ATTR_TITLE = "title";
  public static final String ATTR_METADATA = "metadata";
  public static final String ATTR_HREF = "href";
  public static final String ATTR_REF = "ref";
  public static final String ATTR_TARGET = "target";

  public static final String ATTR_SRSNAME = "srsName";
  public static final QName QNAME_ATTR_SRSNAME = new QName(NS_GML, ATTR_SRSNAME);

  public static final String ELEM_POINT = "Point";

  public static final String ELEM_MULTIPOINT = "MultiPoint";

  public static final String ELEM_POINTMEMBERS = "pointMembers";
  public static final QName QNAME_POINTMEMBERS = new QName(NS_GML, ELEM_POINTMEMBERS);

  public static final String ELEM_LINESTRING = "LineString";
  public static final QName QNAME_LINESTRING = new QName(NS_GML, ELEM_LINESTRING);

  public static final String ELEM_MULTILINESTRING = "MultiCurve";

  public static final String ELEM_LINESTRINGMEMBERS = "curveMembers";

  public static final String ELEM_POLYGON = "Polygon";
  public static final QName QNAME_POLYGON = new QName(NS_GML, ELEM_POLYGON);

  public static final String ELEM_POLYGON_EXTERIOR = "exterior";
  public static final QName QNAME_POLYGON_EXTERIOR = new QName(NS_GML, ELEM_POLYGON_EXTERIOR);

  public static final String ELEM_POLYGON_INTERIOR = "interior";
  public static final QName QNAME_POLYGON_INTERIOR = new QName(NS_GML, ELEM_POLYGON_INTERIOR);

  public static final String ELEM_POLYGON_LINEARRING = "LinearRing";

  public static final String ELEM_MULTIPOLYGON = "MultiSurface";

  public static final String ELEM_SURFACEMEMBERS = "surfaceMembers";

  public static final String ELEM_GEOCOLLECTION = "MultiGeometry";

  public static final String ELEM_GEOMEMBERS = "geometryMembers";
  public static final QName QNAME_GEOMEMBERS = new QName(NS_GML, ELEM_GEOMEMBERS);

  public static final String ELEM_POS = "pos";

  public static final String ELEM_PROPERTY = "property";

  public static final String ELEM_LINKS = "links";

  public static final String ELEM_URI = "uri";

  public final static String ELEM_REASON = "reason";

  public static final String ELEM_DELETED_LINK = "deleted-link";

  public static final String ATTR_SOURCE = "source";
  public static final String ATTR_RELATIONSHIP = "relationship";

  public static final String ANNOTATION = "annotation";

  // JSON stuff
  public final static String JSON_METADATA = "odata.metadata";

  public final static String JSON_TYPE = "@odata.type";
  public final static String JSON_ID = "@odata.id";
  public final static String JSON_READ_LINK = "@odata.readLink";
  public final static String JSON_EDIT_LINK = "@odata.editLink";
  public final static String JSON_CONTEXT = "@odata.context";
  public final static String JSON_ETAG = "@odata.etag";
  public final static String JSON_MEDIA_ETAG = "@odata.mediaEtag";
  public final static String JSON_MEDIA_CONTENT_TYPE = "@odata.mediaContentType";
  public final static String JSON_MEDIA_READ_LINK = "@odata.mediaReadLink";
  public final static String JSON_MEDIA_EDIT_LINK = "@odata.mediaEditLink";
  public final static String JSON_METADATA_ETAG = "@odata.metadataEtag";
  public final static String JSON_BIND_LINK_SUFFIX = "@odata.bind";
  public final static String JSON_ASSOCIATION_LINK = "@odata.associationLink";
  public final static String JSON_NAVIGATION_LINK = "@odata.navigationLink";
  public final static String JSON_COUNT = "@odata.count";
  public final static String JSON_NEXT_LINK = "@odata.nextLink";
  public final static String JSON_DELTA_LINK = "@odata.deltaLink";
  public final static String JSON_ERROR = "error";

  public final static String JSON_NULL = "odata.null";

  public final static String VALUE = "value";

  public final static String JSON_URL = "url";

  public final static String JSON_COORDINATES = "coordinates";
  public final static String JSON_GEOMETRIES = "geometries";
  public final static String JSON_CRS = "crs";

  public final static String JSON_NAME = "name";

  // Atom stuff
  public final static String ATOM_ELEM_ENTRY = "entry";
  public static final QName QNAME_ATOM_ELEM_ENTRY = new QName(NS_ATOM, ATOM_ELEM_ENTRY);

  public final static String ATOM_ELEM_ENTRY_REF = "ref";

  public final static String ATOM_ATTR_ID = "id";
  public final static QName QNAME_ATOM_ATTR_ID = new QName(ATOM_ATTR_ID);

  public final static String ATOM_ELEM_FEED = "feed";
  public static final QName QNAME_ATOM_ELEM_FEED = new QName(NS_ATOM, ATOM_ELEM_FEED);

  public final static String ATOM_ELEM_CATEGORY = "category";
  public static final QName QNAME_ATOM_ELEM_CATEGORY = new QName(NS_ATOM, ATOM_ELEM_CATEGORY);

  public static final String ATOM_ELEM_COUNT = "count";

  public final static String ATOM_ELEM_ID = "id";
  public static final QName QNAME_ATOM_ELEM_ID = new QName(NS_ATOM, ATOM_ELEM_ID);

  public static final String ATOM_ELEM_TITLE = "title";
  public static final QName QNAME_ATOM_ELEM_TITLE = new QName(NS_ATOM, ATOM_ELEM_TITLE);

  public static final String ATOM_ELEM_SUMMARY = "summary";
  public static final QName QNAME_ATOM_ELEM_SUMMARY = new QName(NS_ATOM, ATOM_ELEM_SUMMARY);

  public static final String ATOM_ELEM_UPDATED = "updated";
  public static final QName QNAME_ATOM_ELEM_UPDATED = new QName(NS_ATOM, ATOM_ELEM_UPDATED);

  public final static String ATOM_ELEM_LINK = "link";
  public static final QName QNAME_ATOM_ELEM_LINK = new QName(NS_ATOM, ATOM_ELEM_LINK);

  public final static String ATOM_ELEM_CONTENT = "content";
  public static final QName QNAME_ATOM_ELEM_CONTENT = new QName(NS_ATOM, ATOM_ELEM_CONTENT);

  public static final String ATOM_ELEM_ACTION = "action";

  public static final String ATOM_ELEM_INLINE = "inline";

  public static final String ATOM_ATTR_TERM = "term";
  public static final String ATOM_ATTR_SCHEME = "scheme";
  public static final String ATOM_ATTR_SRC = "src";
  public static final String ATOM_ATTR_ETAG = "etag";
  public static final String ATOM_ATTR_METADATAETAG = "metadata-etag";

  public static final String ATOM_ELEM_DELETED_ENTRY = "deleted-entry";

  // error stuff
  public static final String ERROR_CODE = "code";
  public static final String ERROR_MESSAGE = "message";
  public static final String ERROR_TARGET = "target";
  public static final String ERROR_DETAILS = "details";
  public static final String ERROR_INNERERROR = "innererror";

  // canonical functions to be applied via dynamic annotation <tt>Apply</tt>
  public static final String CANONICAL_FUNCTION_CONCAT = "odata.concat";
  public static final String CANONICAL_FUNCTION_FILLURITEMPLATE = "odata.fillUriTemplate";
  public static final String CANONICAL_FUNCTION_URIENCODE = "odata.uriEncode";
}
