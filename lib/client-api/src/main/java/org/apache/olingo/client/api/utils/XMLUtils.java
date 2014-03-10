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
package org.apache.olingo.client.api.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.Constants;
import org.apache.olingo.client.api.domain.EdmSimpleType;
import org.apache.olingo.client.api.domain.geospatial.Geospatial;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML utilities.
 */
public final class XMLUtils {

  /**
   * DOM factory.
   */
  public static final DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

  private XMLUtils() {
    // Empty private constructor for static utility classes       
  }

  /**
   * Gets XML node name.
   *
   * @param node node.
   * @return node name.
   */
  public static String getSimpleName(final Node node) {
    return node.getLocalName() == null
            ? node.getNodeName().substring(node.getNodeName().indexOf(':') + 1)
            : node.getLocalName();
  }

  /**
   * Gets the given node's children of the given type.
   *
   * @param node parent.
   * @param nodetype searched child type.
   * @return children.
   */
  public static List<Node> getChildNodes(final Node node, final short nodetype) {
    final List<Node> result = new ArrayList<Node>();

    final NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      final Node child = children.item(i);
      if (child.getNodeType() == nodetype) {
        result.add(child);
      }
    }

    return result;
  }

  /**
   * Gets the given node's children with the given name.
   *
   * @param node parent.
   * @param name searched child name.
   * @return children.
   */
  public static List<Element> getChildElements(final Element node, final String name) {
    final List<Element> result = new ArrayList<Element>();

    if (StringUtils.isNotBlank(name)) {
      final NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        final Node child = children.item(i);
        if ((child instanceof Element) && name.equals(child.getNodeName())) {
          result.add((Element) child);
        }
      }
    }

    return result;
  }

  /**
   * Checks if the given node has <tt>element</tt> children.
   *
   * @param node parent.
   * @return 'TRUE' if the given node has at least one <tt>element</tt> child; 'FALSE' otherwise.
   */
  public static boolean hasElementsChildNode(final Node node) {
    boolean found = false;

    for (Node child : getChildNodes(node, Node.ELEMENT_NODE)) {
      if (Constants.ELEM_ELEMENT.equals(XMLUtils.getSimpleName(child))) {
        found = true;
      }
    }

    return found;
  }

  /**
   * Checks if the given node has only text children.
   *
   * @param node parent.
   * @return 'TRUE' if the given node has only text children; 'FALSE' otherwise.
   */
  public static boolean hasOnlyTextChildNodes(final Node node) {
    boolean result = true;
    final NodeList children = node.getChildNodes();
    for (int i = 0; result && i < children.getLength(); i++) {
      final Node child = children.item(i);
      if (child.getNodeType() != Node.TEXT_NODE) {
        result = false;
      }
    }

    return result;
  }

  public static EdmSimpleType simpleTypeForNode(final Geospatial.Dimension dimension, final Node node) {
    EdmSimpleType type = null;

    if (Constants.ELEM_POINT.equals(node.getNodeName())) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? EdmSimpleType.GeographyPoint
              : EdmSimpleType.GeometryPoint;
    } else if (Constants.ELEM_MULTIPOINT.equals(node.getNodeName())) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? EdmSimpleType.GeographyMultiPoint
              : EdmSimpleType.GeometryMultiPoint;
    } else if (Constants.ELEM_LINESTRING.equals(node.getNodeName())) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? EdmSimpleType.GeographyLineString
              : EdmSimpleType.GeometryLineString;
    } else if (Constants.ELEM_MULTILINESTRING.equals(node.getNodeName())) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? EdmSimpleType.GeographyMultiLineString
              : EdmSimpleType.GeometryMultiLineString;
    } else if (Constants.ELEM_POLYGON.equals(node.getNodeName())) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? EdmSimpleType.GeographyPolygon
              : EdmSimpleType.GeometryPolygon;
    } else if (Constants.ELEM_MULTIPOLYGON.equals(node.getNodeName())) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? EdmSimpleType.GeographyMultiPolygon
              : EdmSimpleType.GeometryMultiPolygon;
    } else if (Constants.ELEM_GEOCOLLECTION.equals(node.getNodeName())
            || Constants.ELEM_GEOMEMBERS.equals(node.getNodeName())) {

      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? EdmSimpleType.GeographyCollection
              : EdmSimpleType.GeometryCollection;
    }

    return type;
  }
}
