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
package org.apache.olingo.client.core.v4;

import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.api.uri.v4.FilterArgFactory;
import org.apache.olingo.client.api.uri.v4.FilterFactory;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.client.core.edm.EdmEnumTypeImpl;
import org.apache.olingo.client.core.edm.xml.v4.EnumTypeImpl;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FilterFactoryTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  private FilterFactory getFilterFactory() {
    return getClient().getFilterFactory();
  }

  private FilterArgFactory getFilterArgFactory() {
    return getFilterFactory().getArgFactory();
  }

  @Test
  public void has() {
    final EdmEnumType pattern = new EdmEnumTypeImpl(ODataServiceVersion.V40,
            null, new FullQualifiedName("Sales", "Pattern"), new EnumTypeImpl());
    final URIFilter filter = getFilterFactory().has(getFilterArgFactory().property("style"), pattern, "Yellow");

    assertEquals("(style has Sales.Pattern'Yellow')", filter.build());
  }

  @Test
  public void contains() {
    final URIFilter filter = getFilterFactory().match(
            getFilterArgFactory().contains(
                    getFilterArgFactory().property("CompanyName"), getFilterArgFactory().literal("Alfreds")));

    assertEquals("contains(CompanyName,'Alfreds')", filter.build());
  }

  @Test
  public void maxdatetime() {
    final URIFilter filter = getFilterFactory().eq(
            getFilterArgFactory().property("EndTime"),
            getFilterArgFactory().maxdatetime());

    assertEquals("(EndTime eq maxdatetime())", filter.build());
  }

  @Test
  public void any() {
    final URIFilter filter = getFilterFactory().match(
            getFilterArgFactory().any(getFilterArgFactory().property("Items"),
                    getFilterFactory().gt("d:d/Quantity", 100)));

    assertEquals("Items/any(d:d/Quantity gt 100)", filter.build());
  }
}
