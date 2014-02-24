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
package org.apache.olingo.odata4.client.api.data;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.olingo.odata4.client.api.Constants;
import org.apache.olingo.odata4.client.api.ODataClient;
import org.apache.olingo.odata4.client.api.data.geospatial.Geospatial;
import org.apache.olingo.odata4.client.api.data.geospatial.GeospatialCollection;
import org.apache.olingo.odata4.client.api.data.geospatial.LineString;
import org.apache.olingo.odata4.client.api.data.geospatial.MultiLineString;
import org.apache.olingo.odata4.client.api.data.geospatial.MultiPoint;
import org.apache.olingo.odata4.client.api.data.geospatial.MultiPolygon;
import org.apache.olingo.odata4.client.api.data.geospatial.Point;
import org.apache.olingo.odata4.client.api.data.geospatial.Polygon;
import org.apache.olingo.odata4.client.api.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ODataGeospatialValue extends ODataPrimitiveValue {

  private static final long serialVersionUID = -3984105137562291082L;

  /**
   * Geospatial value builder.
   */
  public static class Builder extends AbstractBuilder {

    private final ODataGeospatialValue ogv;

    /**
     * Constructor.
     */
    public Builder(final ODataClient client) {
      super(client);
      this.ogv = new ODataGeospatialValue(client);
    }

    /**
     * Sets the given value provided as a DOM tree.
     *
     * @param tree value.
     * @return the current builder.
     */
    public Builder setTree(final Element tree) {
      this.ogv.tree = tree;
      return this;
    }

    /**
     * Sets the actual object value.
     *
     * @param value value.
     * @return the current builder.
     */
    public <T extends Geospatial> Builder setValue(final T value) {
      this.ogv.value = value;
      return this;
    }

    /**
     * Sets actual value type.
     *
     * @param type type.
     * @return the current builder.
     */
    public Builder setType(final EdmSimpleType type) {
      isSupported(type);

      if (!type.isGeospatial()) {
        throw new IllegalArgumentException(
                "Use " + ODataPrimitiveValue.class.getSimpleName() + " for non-geospatial types");
      }

      if (type == EdmSimpleType.Geography || type == EdmSimpleType.Geometry) {
        throw new IllegalArgumentException(
                type + "is not an instantiable type. "
                + "An entity can declare a property to be of type Geometry. "
                + "An instance of an entity MUST NOT have a value of type Geometry. "
                + "Each value MUST be of some subtype.");
      }
      this.ogv.type = type;
      return this;
    }

    /**
     * Builds the geospatial value.
     *
     * @return <tt>ODataGeospatialValue</tt> object.
     */
    public ODataGeospatialValue build() {
      if (this.ogv.tree == null && this.ogv.value == null) {
        throw new IllegalArgumentException("Must provide either tree or value");
      }
      if (this.ogv.tree != null && this.ogv.value != null) {
        throw new IllegalArgumentException("Cannot provide both tree and value");
      }

      if (this.ogv.type == null) {
        throw new IllegalArgumentException("Must provide geospatial type");
      }

      if (this.ogv.tree != null) {
        this.ogv.value = this.ogv.parseTree(this.ogv.tree, this.ogv.type);
      }
      if (this.ogv.value != null) {
        this.ogv.tree = this.ogv.parseGeospatial((Geospatial) this.ogv.value);
      }

      return this.ogv;
    }
  }

  /**
   * DOM tree.
   */
  private Element tree;

  /**
   * Protected constructor, need to use the builder to instantiate this class.
   *
   * @see Builder
   */
  protected ODataGeospatialValue(final ODataClient client) {
    super(client);
  }

  private Geospatial.Dimension getDimension() {
    Geospatial.Dimension dimension;

    switch (this.type) {
      case Geography:
      case GeographyCollection:
      case GeographyLineString:
      case GeographyMultiLineString:
      case GeographyPoint:
      case GeographyMultiPoint:
      case GeographyPolygon:
      case GeographyMultiPolygon:
        dimension = Geospatial.Dimension.GEOGRAPHY;
        break;

      default:
        dimension = Geospatial.Dimension.GEOMETRY;
    }

    return dimension;
  }

  private List<Point> parsePoints(final NodeList posList) {
    final List<Point> result = new ArrayList<Point>();
    for (int i = 0; i < posList.getLength(); i++) {
      final String[] pointInfo = posList.item(i).getTextContent().split(" ");
      final Point point = new Point(getDimension());
      point.setX(Double.valueOf(pointInfo[0]));
      point.setY(Double.valueOf(pointInfo[1]));

      result.add(point);
    }
    return result;
  }

  private LineString parseLineString(final Element element) {
    return new LineString(getDimension(),
            parsePoints(element.getElementsByTagName(Constants.ELEM_POS)));
  }

  private Polygon parsePolygon(final Element element) {
    List<Point> extPoints = null;
    final Element exterior
            = (Element) element.getElementsByTagName(Constants.ELEM_POLYGON_EXTERIOR).item(0);
    if (exterior != null) {
      extPoints = parsePoints(
              ((Element) exterior.getElementsByTagName(Constants.ELEM_POLYGON_LINEARRING).item(0)).
              getElementsByTagName(Constants.ELEM_POS));
    }
    List<Point> intPoints = null;
    final Element interior
            = (Element) element.getElementsByTagName(Constants.ELEM_POLYGON_INTERIOR).item(0);
    if (interior != null) {
      intPoints = parsePoints(
              ((Element) interior.getElementsByTagName(Constants.ELEM_POLYGON_LINEARRING).item(0)).
              getElementsByTagName(Constants.ELEM_POS));
    }

    return new Polygon(getDimension(), intPoints, extPoints);
  }

  /**
   * Parses given tree as geospatial value.
   */
  private Geospatial parseTree(final Element tree, final EdmSimpleType type) {
    Geospatial value;

    switch (type) {
      case GeographyPoint:
      case GeometryPoint:
        value = parsePoints(tree.getElementsByTagName(Constants.ELEM_POS)).get(0);
        break;

      case GeographyMultiPoint:
      case GeometryMultiPoint:
        final Element pMembs
                = (Element) tree.getElementsByTagName(Constants.ELEM_POINTMEMBERS).item(0);
        final List<Point> points = pMembs == null
                ? Collections.<Point>emptyList()
                : parsePoints(pMembs.getElementsByTagName(Constants.ELEM_POS));
        value = new MultiPoint(getDimension(), points);
        break;

      case GeographyLineString:
      case GeometryLineString:
        value = parseLineString(tree);
        break;

      case GeographyMultiLineString:
      case GeometryMultiLineString:
        final Element mlMembs
                = (Element) tree.getElementsByTagName(Constants.ELEM_LINESTRINGMEMBERS).item(0);
        final List<LineString> lineStrings;
        if (mlMembs == null) {
          lineStrings = Collections.<LineString>emptyList();
        } else {
          lineStrings = new ArrayList<LineString>();
          final NodeList lineStringNodes = mlMembs.getElementsByTagName(Constants.ELEM_LINESTRING);
          for (int i = 0; i < lineStringNodes.getLength(); i++) {
            lineStrings.add(parseLineString((Element) lineStringNodes.item(i)));
          }
        }
        value = new MultiLineString(getDimension(), lineStrings);
        break;

      case GeographyPolygon:
      case GeometryPolygon:
        value = parsePolygon(tree);
        break;

      case GeographyMultiPolygon:
      case GeometryMultiPolygon:
        final Element mpMembs
                = (Element) tree.getElementsByTagName(Constants.ELEM_SURFACEMEMBERS).item(0);
        final List<Polygon> polygons;
        if (mpMembs == null) {
          polygons = Collections.<Polygon>emptyList();
        } else {
          polygons = new ArrayList<Polygon>();
          final NodeList polygonNodes = mpMembs.getElementsByTagName(Constants.ELEM_POLYGON);
          for (int i = 0; i < polygonNodes.getLength(); i++) {
            polygons.add(parsePolygon((Element) polygonNodes.item(i)));
          }
        }
        value = new MultiPolygon(getDimension(), polygons);
        break;

      case GeographyCollection:
      case GeometryCollection:
        final Element cMembs
                = (Element) tree.getElementsByTagName(Constants.ELEM_GEOMEMBERS).item(0);
        final List<Geospatial> geospatials;
        if (cMembs == null) {
          geospatials = Collections.<Geospatial>emptyList();
        } else {
          geospatials = new ArrayList<Geospatial>();
          for (Node geom : XMLUtils.getChildNodes(cMembs, Node.ELEMENT_NODE)) {
            geospatials.add(parseTree((Element) geom, XMLUtils.simpleTypeForNode(getDimension(), geom)));
          }
        }
        value = new GeospatialCollection(getDimension(), geospatials);
        break;

      default:
        value = null;
    }

    return value;
  }

  private void parsePoints(final Element parent, final Iterator<Point> itor, final boolean wrap) {
    while (itor.hasNext()) {
      final Point point = itor.next();

      final Element pos = parent.getOwnerDocument().
              createElementNS(Constants.NS_GML, Constants.ELEM_POS);
      pos.appendChild(parent.getOwnerDocument().createTextNode(
              Double.toString(point.getX()) + " " + point.getY()));

      final Element appendable;
      if (wrap) {
        final Element epoint = parent.getOwnerDocument().
                createElementNS(Constants.NS_GML, Constants.ELEM_POINT);
        parent.appendChild(epoint);
        appendable = epoint;
      } else {
        appendable = parent;
      }
      appendable.appendChild(pos);
    }
  }

  private void parseLineStrings(final Element parent, final Iterator<LineString> itor, final boolean wrap) {
    while (itor.hasNext()) {
      final LineString lineString = itor.next();

      final Element appendable;
      if (wrap) {
        final Element eLineString = parent.getOwnerDocument().
                createElementNS(Constants.NS_GML, Constants.ELEM_LINESTRING);
        parent.appendChild(eLineString);
        appendable = eLineString;
      } else {
        appendable = parent;
      }
      parsePoints(appendable, lineString.iterator(), false);
    }
  }

  private void parsePolygons(final Element parent, final Iterator<Polygon> itor, final boolean wrap) {
    while (itor.hasNext()) {
      final Polygon polygon = itor.next();

      final Element appendable;
      if (wrap) {
        final Element ePolygon = parent.getOwnerDocument().createElementNS(
                Constants.NS_GML, Constants.ELEM_POLYGON);
        parent.appendChild(ePolygon);
        appendable = ePolygon;
      } else {
        appendable = parent;
      }

      if (!polygon.getExterior().isEmpty()) {
        final Element exterior = parent.getOwnerDocument().createElementNS(
                Constants.NS_GML, Constants.ELEM_POLYGON_EXTERIOR);
        appendable.appendChild(exterior);
        final Element linearRing = parent.getOwnerDocument().createElementNS(
                Constants.NS_GML, Constants.ELEM_POLYGON_LINEARRING);
        exterior.appendChild(linearRing);

        parsePoints(linearRing, polygon.getExterior().iterator(), false);
      }
      if (!polygon.getInterior().isEmpty()) {
        final Element interior = parent.getOwnerDocument().createElementNS(
                Constants.NS_GML, Constants.ELEM_POLYGON_INTERIOR);
        appendable.appendChild(interior);
        final Element linearRing = parent.getOwnerDocument().createElementNS(
                Constants.NS_GML, Constants.ELEM_POLYGON_LINEARRING);
        interior.appendChild(linearRing);

        parsePoints(linearRing, polygon.getInterior().iterator(), false);
      }
    }
  }

  private Element parseGeospatial(final Geospatial value) {
    final DocumentBuilder builder;
    try {
      builder = XMLUtils.DOC_BUILDER_FACTORY.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Failure initializing Geospatial DOM tree", e);
    }
    final Document doc = builder.newDocument();

    final Element tree;

    switch (value.getEdmSimpleType()) {
      case GeographyPoint:
      case GeometryPoint:
        tree = doc.createElementNS(Constants.NS_GML, Constants.ELEM_POINT);

        parsePoints(tree, Collections.singleton((Point) value).iterator(), false);
        break;

      case GeometryMultiPoint:
      case GeographyMultiPoint:
        tree = doc.createElementNS(Constants.NS_GML, Constants.ELEM_MULTIPOINT);

        final Element pMembs = doc.createElementNS(Constants.NS_GML, Constants.ELEM_POINTMEMBERS);
        tree.appendChild(pMembs);

        parsePoints(pMembs, ((MultiPoint) value).iterator(), true);
        break;

      case GeometryLineString:
      case GeographyLineString:
        tree = doc.createElementNS(Constants.NS_GML, Constants.ELEM_LINESTRING);

        parseLineStrings(tree, Collections.singleton((LineString) value).iterator(), false);
        break;

      case GeometryMultiLineString:
      case GeographyMultiLineString:
        tree = doc.createElementNS(Constants.NS_GML, Constants.ELEM_MULTILINESTRING);

        final Element mlMembs
                = doc.createElementNS(Constants.NS_GML, Constants.ELEM_LINESTRINGMEMBERS);
        tree.appendChild(mlMembs);

        parseLineStrings(mlMembs, ((MultiLineString) value).iterator(), true);
        break;

      case GeographyPolygon:
      case GeometryPolygon:
        tree = doc.createElementNS(Constants.NS_GML, Constants.ELEM_POLYGON);
        parsePolygons(tree, Collections.singleton(((Polygon) value)).iterator(), false);
        break;

      case GeographyMultiPolygon:
      case GeometryMultiPolygon:
        tree = doc.createElementNS(Constants.NS_GML, Constants.ELEM_MULTIPOLYGON);

        final Element mpMembs
                = doc.createElementNS(Constants.NS_GML, Constants.ELEM_SURFACEMEMBERS);
        tree.appendChild(mpMembs);

        parsePolygons(mpMembs, ((MultiPolygon) value).iterator(), true);
        break;

      case GeographyCollection:
      case GeometryCollection:
        tree = doc.createElementNS(Constants.NS_GML, Constants.ELEM_GEOCOLLECTION);

        final Element gMembs
                = doc.createElementNS(Constants.NS_GML, Constants.ELEM_GEOMEMBERS);
        tree.appendChild(gMembs);

        final Iterator<Geospatial> itor = ((GeospatialCollection) value).iterator();
        while (itor.hasNext()) {
          final Geospatial geospatial = itor.next();
          gMembs.appendChild(doc.importNode(parseGeospatial(geospatial), true));
        }
        break;

      default:
        tree = null;
    }

    return tree;
  }

  public Element toTree() {
    return this.tree;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ODataGeospatialValue other = (ODataGeospatialValue) obj;
    return this.tree.isEqualNode(other.tree);
  }

  @Override
  public String toString() {
    final StringWriter writer = new StringWriter();
    client.getSerializer().dom(this.tree, writer);
    return writer.toString();
  }
}
