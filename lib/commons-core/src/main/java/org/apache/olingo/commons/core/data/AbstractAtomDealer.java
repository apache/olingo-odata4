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
package org.apache.olingo.commons.core.data;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

abstract class AbstractAtomDealer {

  protected static final String TYPE_TEXT = "text";

  protected final ODataServiceVersion version;

  protected final QName etagQName;

  protected final QName metadataEtagQName;

  protected final QName inlineQName;

  protected final QName actionQName;

  protected final QName propertiesQName;

  protected final QName typeQName;

  protected final QName nullQName;

  protected final QName elementQName;

  protected final QName countQName;

  protected final QName uriQName;

  protected final QName nextQName;

  protected final QName annotationQName;

  protected final QName contextQName;

  protected final QName entryRefQName;

  protected final QName propertyValueQName;

  protected final QName deletedEntryQName;

  protected final QName reasonQName;

  protected final QName linkQName;

  protected final QName deletedLinkQName;

  protected final QName errorCodeQName;

  protected final QName errorMessageQName;

  protected final QName errorTargetQName;

  public AbstractAtomDealer(final ODataServiceVersion version) {
    this.version = version;

    this.etagQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ATTR_ETAG);
    this.metadataEtagQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ATTR_METADATAETAG);
    this.inlineQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ELEM_INLINE);
    this.actionQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ELEM_ACTION);
    this.propertiesQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.PROPERTIES);
    this.typeQName = new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATTR_TYPE);
    this.nullQName = new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATTR_NULL);
    this.elementQName = version.compareTo(ODataServiceVersion.V40) < 0
            ? new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES), Constants.ELEM_ELEMENT)
            : new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ELEM_ELEMENT);
    this.countQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ELEM_COUNT);
    this.uriQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES), Constants.ELEM_URI);
    this.nextQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES), Constants.NEXT_LINK_REL);
    this.annotationQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ANNOTATION);
    this.contextQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.CONTEXT);
    this.entryRefQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ELEM_ENTRY_REF);
    this.propertyValueQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.VALUE);

    this.deletedEntryQName = new QName(Constants.NS_ATOM_TOMBSTONE, Constants.ATOM_ELEM_DELETED_ENTRY);
    this.reasonQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ELEM_REASON);
    this.linkQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ELEM_LINK);
    this.deletedLinkQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ELEM_DELETED_LINK);

    this.errorCodeQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ERROR_CODE);
    this.errorMessageQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ERROR_MESSAGE);
    this.errorTargetQName =
            new QName(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ERROR_TARGET);
  }

  protected void namespaces(final XMLStreamWriter writer) throws XMLStreamException {
    writer.writeNamespace(StringUtils.EMPTY, Constants.NS_ATOM);
    writer.writeNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
    writer.writeNamespace(Constants.PREFIX_METADATA, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA));
    writer.writeNamespace(
            Constants.PREFIX_DATASERVICES, version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES));
    writer.writeNamespace(Constants.PREFIX_GML, Constants.NS_GML);
    writer.writeNamespace(Constants.PREFIX_GEORSS, Constants.NS_GEORSS);
  }
}
