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

import org.apache.olingo.commons.api.format.ContentType;

/**
 * Constant values related to the OData protocol.
 */
public interface Constants {

  // Other stuff
  String UTF8 = "UTF-8";

  String METADATA = "$metadata";

  Integer DEFAULT_PRECISION = 40;

  Integer DEFAULT_SCALE = 25;

  // Common Namespaces
  String NS_BASE = "http://docs.oasis-open.org/odata/ns/";

  String NS_DATASERVICES = "http://docs.oasis-open.org/odata/ns/data";

  String NS_METADATA = "http://docs.oasis-open.org/odata/ns/metadata";

  String NS_SCHEME = "http://docs.oasis-open.org/odata/ns/scheme";

  String NS_NAVIGATION_LINK_REL = "http://docs.oasis-open.org/odata/ns/related/";

  String NS_ASSOCIATION_LINK_REL = "http://docs.oasis-open.org/odata/ns/relatedlinks/";

  String NS_MEDIA_EDIT_LINK_REL = "http://docs.oasis-open.org/odata/ns/edit-media/";
  
  String NS_MEDIA_READ_LINK_REL = "http://docs.oasis-open.org/odata/ns/mediaresource/";

  String NS_DELTA_LINK_REL = "http://docs.oasis-open.org/odata/ns/delta";

  // XML namespaces and prefixes
  String NS_ATOM = "http://www.w3.org/2005/Atom";

  String NS_GEORSS = "http://www.georss.org/georss";

  String NS_GML = "http://www.opengis.net/gml";

  String NS_ATOM_TOMBSTONE = "http://purl.org/atompub/tombstones/1.0";

  String PREFIX_DATASERVICES = "d";

  String PREFIX_METADATA = "m";

  String PREFIX_GEORSS = "georss";

  String PREFIX_GML = "gml";

  String SRS_URLPREFIX = "http://www.opengis.net/def/crs/EPSG/0/";

  // Link rel(s)
  String EDIT_LINK_REL = "edit";

  String SELF_LINK_REL = "self";

  String EDITMEDIA_LINK_REL = "edit-media";

  String NEXT_LINK_REL = "next";

  // XML elements and attributes
  String PROPERTIES = "properties";

  String ELEM_ELEMENT = "element";

  String ATTR_TYPE = "type";

  String ATTR_NULL = "null";

  String ATTR_XML_BASE = "base";

  QName QNAME_ATTR_XML_BASE = new QName(XMLConstants.XML_NS_URI, ATTR_XML_BASE);

  String CONTEXT = "context";

  String ATTR_REL = "rel";

  String ATTR_TITLE = "title";

  String ATTR_METADATA = "metadata";

  String ATTR_HREF = "href";
  
  String ATTR_NAME = "name";

  String ATTR_REF = "ref";

  String ATTR_TARGET = "target";

  String ATTR_SRSNAME = "srsName";

  QName QNAME_ATTR_SRSNAME = new QName(NS_GML, ATTR_SRSNAME);

  String ELEM_POINT = "Point";

  String ELEM_MULTIPOINT = "MultiPoint";

  String ELEM_POINTMEMBERS = "pointMembers";

  QName QNAME_POINTMEMBERS = new QName(NS_GML, ELEM_POINTMEMBERS);

  String ELEM_LINESTRING = "LineString";

  QName QNAME_LINESTRING = new QName(NS_GML, ELEM_LINESTRING);

  String ELEM_MULTILINESTRING = "MultiCurve";

  String ELEM_LINESTRINGMEMBERS = "curveMembers";

  String ELEM_POLYGON = "Polygon";

  QName QNAME_POLYGON = new QName(NS_GML, ELEM_POLYGON);

  String ELEM_POLYGON_EXTERIOR = "exterior";

  QName QNAME_POLYGON_EXTERIOR = new QName(NS_GML, ELEM_POLYGON_EXTERIOR);

  String ELEM_POLYGON_INTERIOR = "interior";

  QName QNAME_POLYGON_INTERIOR = new QName(NS_GML, ELEM_POLYGON_INTERIOR);

  String ELEM_POLYGON_LINEARRING = "LinearRing";

  String ELEM_MULTIPOLYGON = "MultiSurface";

  String ELEM_SURFACEMEMBERS = "surfaceMembers";

  String ELEM_GEOCOLLECTION = "MultiGeometry";

  String ELEM_GEOMEMBERS = "geometryMembers";

  QName QNAME_GEOMEMBERS = new QName(NS_GML, ELEM_GEOMEMBERS);

  String ELEM_POS = "pos";

  String ELEM_PROPERTY = "property";

  String ELEM_LINKS = "links";

  String ELEM_URI = "uri";

  String ELEM_REASON = "reason";

  String ELEM_DELETED_LINK = "deleted-link";

  String ATTR_SOURCE = "source";

  String ATTR_RELATIONSHIP = "relationship";

  String ANNOTATION = "annotation";

  // JSON stuff
  String JSON_METADATA = "odata.metadata";

  String JSON_TYPE = "@odata.type";

  String JSON_ID = "@odata.id";

  String JSON_READ_LINK = "@odata.readLink";

  String JSON_EDIT_LINK = "@odata.editLink";

  String JSON_CONTEXT = "@odata.context";

  String JSON_ETAG = "@odata.etag";

  String JSON_MEDIA_ETAG = "@odata.mediaEtag";

  String JSON_MEDIA_CONTENT_TYPE = "@odata.mediaContentType";

  String JSON_MEDIA_READ_LINK = "@odata.mediaReadLink";

  String JSON_MEDIA_EDIT_LINK = "@odata.mediaEditLink";

  String JSON_METADATA_ETAG = "@odata.metadataEtag";

  String JSON_BIND_LINK_SUFFIX = "@odata.bind";

  String JSON_ASSOCIATION_LINK = "@odata.associationLink";

  String JSON_NAVIGATION_LINK = "@odata.navigationLink";

  String JSON_COUNT = "@odata.count";

  String JSON_NEXT_LINK = "@odata.nextLink";

  String JSON_DELTA_LINK = "@odata.deltaLink";

  String JSON_ERROR = "error";

  String JSON_NULL = "odata.null";

  String VALUE = "value";

  String JSON_URL = "url";
  
  String JSON_TITLE = "title";

  String JSON_COORDINATES = "coordinates";

  String JSON_GEOMETRIES = "geometries";

  String JSON_CRS = "crs";

  String JSON_NAME = "name";

  // Atom stuff
  String ATOM_ELEM_ENTRY = "entry";

  QName QNAME_ATOM_ELEM_ENTRY = new QName(NS_ATOM, ATOM_ELEM_ENTRY);

  String ATOM_ELEM_ENTRY_REF = "ref";

  String ATOM_ATTR_ID = "id";

  QName QNAME_ATOM_ATTR_ID = new QName(ATOM_ATTR_ID);

  String ATOM_ELEM_FEED = "feed";

  QName QNAME_ATOM_ELEM_FEED = new QName(NS_ATOM, ATOM_ELEM_FEED);

  String ATOM_ELEM_CATEGORY = "category";

  QName QNAME_ATOM_ELEM_CATEGORY = new QName(NS_ATOM, ATOM_ELEM_CATEGORY);

  String ATOM_ELEM_COUNT = "count";

  String ATOM_ELEM_ID = "id";

  QName QNAME_ATOM_ELEM_ID = new QName(NS_ATOM, ATOM_ELEM_ID);

  String ATOM_ELEM_TITLE = "title";

  QName QNAME_ATOM_ELEM_TITLE = new QName(NS_ATOM, ATOM_ELEM_TITLE);

  String ATOM_ELEM_SUMMARY = "summary";

  QName QNAME_ATOM_ELEM_SUMMARY = new QName(NS_ATOM, ATOM_ELEM_SUMMARY);

  String ATOM_ELEM_UPDATED = "updated";

  QName QNAME_ATOM_ELEM_UPDATED = new QName(NS_ATOM, ATOM_ELEM_UPDATED);

  String ATOM_ELEM_LINK = "link";

  QName QNAME_ATOM_ELEM_LINK = new QName(NS_ATOM, ATOM_ELEM_LINK);

  String ATOM_ELEM_CONTENT = "content";

  QName QNAME_ATOM_ELEM_CONTENT = new QName(NS_ATOM, ATOM_ELEM_CONTENT);

  String ATOM_ELEM_ACTION = "action";
  
  String ATOM_ELEM_FUNCTION = "function";

  String ATOM_ELEM_INLINE = "inline";

  String ATOM_ATTR_TERM = "term";

  String ATOM_ATTR_SCHEME = "scheme";

  String ATOM_ATTR_SRC = "src";

  String ATOM_ATTR_ETAG = "etag";

  String ATOM_ATTR_METADATAETAG = "metadata-etag";

  String ATOM_ELEM_DELETED_ENTRY = "deleted-entry";

  // error stuff
  String ERROR_CODE = "code";

  String ERROR_MESSAGE = "message";

  String ERROR_TARGET = "target";

  String ERROR_DETAILS = "details";

  String ERROR_DETAIL = "detail";
  
  String ERROR_INNERERROR = "innererror";

  // canonical functions to be applied via dynamic annotation <tt>Apply</tt>
  String CANONICAL_FUNCTION_CONCAT = "odata.concat";

  String CANONICAL_FUNCTION_FILLURITEMPLATE = "odata.fillUriTemplate";

  String CANONICAL_FUNCTION_URIENCODE = "odata.uriEncode";

  String MEDIA_EDIT_LINK_TYPE = "*/*";
  String ENTITY_NAVIGATION_LINK_TYPE = ContentType.APPLICATION_ATOM_XML_ENTRY.toContentTypeString();
  String ENTITY_SET_NAVIGATION_LINK_TYPE = ContentType.APPLICATION_ATOM_XML_FEED.toContentTypeString();
  String ASSOCIATION_LINK_TYPE = ContentType.APPLICATION_XML.toContentTypeString();
  String ENTITY_COLLECTION_BINDING_LINK_TYPE = ContentType.APPLICATION_XML.toContentTypeString();
  String ENTITY_BINDING_LINK_TYPE = ContentType.APPLICATION_XML.toContentTypeString();
  
 //For v4.01 Delta
  
  String LINK = "/$link";
  String DELETEDLINK = "/$deletedLink";
  String DELTA = "/$delta";
  String DELTAVALUE = "delta";
  String AT = "@";
  String DELETEDENTITY = "/$deletedEntity";
  String DELTALINK = "@deltaLink";
  String HASH = "#";
  String REMOVED = "removed";
  String ENTITY = "/$entity";
  String REASON = "Reason";
  String ID = "@id";
}
