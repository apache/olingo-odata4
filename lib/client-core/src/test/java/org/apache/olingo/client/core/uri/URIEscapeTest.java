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
package org.apache.olingo.client.core.uri;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import org.apache.olingo.client.core.edm.EdmEnumTypeImpl;
import org.apache.olingo.client.core.edm.xml.v4.EnumTypeImpl;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.junit.Test;

public class URIEscapeTest {

  @Test
  public void _null() {
    assertEquals("null", URIUtils.escape(ODataServiceVersion.V40, null));
  }

  @Test
  public void _boolean() {
    assertEquals("true", URIUtils.escape(ODataServiceVersion.V40, Boolean.TRUE));
  }

  @Test
  public void _enum() throws UnsupportedEncodingException {
    final EdmEnumType pattern = new EdmEnumTypeImpl(ODataServiceVersion.V40,
            null, new FullQualifiedName("Sales", "Pattern"), new EnumTypeImpl());

    assertEquals(URLEncoder.encode("Sales.Pattern'Yellow'", Constants.UTF8),
            URIUtils.escape(ODataServiceVersion.V40, pattern.toUriLiteral("Yellow")));
  }

  @Test
  public void geospatial() throws UnsupportedEncodingException {
    final Point point = new Point(Geospatial.Dimension.GEOGRAPHY, 0);
    point.setX(142.1);
    point.setY(64.1);

    assertEquals(URLEncoder.encode("geography'SRID=0;Point(142.1 64.1)'", Constants.UTF8),
            URIUtils.escape(ODataServiceVersion.V40, point));
  }

  @Test
  public void collection() {
    assertEquals("[\"red\",\"green\"]",
            URIUtils.escape(ODataServiceVersion.V40, Arrays.asList(new String[] {"red", "green"})));
  }

  @Test
  public void complex() {
    assertEquals("{\"Name\":\"Value\"}",
            URIUtils.escape(ODataServiceVersion.V40, Collections.singletonMap("Name", "Value")));
  }
}
