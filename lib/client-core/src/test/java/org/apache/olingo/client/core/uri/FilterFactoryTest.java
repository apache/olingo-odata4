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
package org.apache.olingo.client.core.uri;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.olingo.client.api.uri.FilterArgFactory;
import org.apache.olingo.client.api.uri.FilterFactory;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.core.Encoder;
import org.apache.olingo.commons.core.edm.EdmEnumTypeImpl;
import org.junit.Test;

public class FilterFactoryTest extends AbstractTest {

  private FilterFactory getFilterFactory() {
    return client.getFilterFactory();
  }

  private FilterArgFactory getFilterArgFactory() {
    return getFilterFactory().getArgFactory();
  }

  @Test
  public void has() {
    final EdmEnumType pattern =
        new EdmEnumTypeImpl(null, new FullQualifiedName("Sales", "Pattern"), new CsdlEnumType());
    final URIFilter filter = getFilterFactory().has(getFilterArgFactory().property("style"), pattern, "Yellow");

    assertEquals("(style has Sales.Pattern'Yellow')", filter.build());
  }

  @Test
  public void contains() {
    final URIFilter filter = getFilterFactory().match(getFilterArgFactory().contains(
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

  @Test
  public void issueOLINGO357() throws UnsupportedEncodingException {
    final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-8"));
    calendar.clear();
    calendar.set(2011, 2, 8, 14, 21, 12);

    final URIFilter filter = getFilterFactory().ge("OrderDate", calendar);
    assertEquals("(OrderDate ge " + Encoder.encode("2011-03-08T14:21:12-08:00") + ")",
        filter.build());
  }
  
  @Test
  public void issue1144Any() {
    URIFilter andFilExp = getFilterFactory().and(getFilterFactory().eq("d/Quantity", 100), 
        getFilterFactory().eq("d/Quantity", 50));
    final URIFilter filter = getFilterFactory().match(
        getFilterArgFactory().any(getFilterArgFactory().property("Items"), "d", andFilExp));
    assertEquals("Items/any(d:((d/Quantity eq 100) and (d/Quantity eq 50)))", filter.build());
  }

  @Test
  public void all() {
    final URIFilter filter = getFilterFactory().match(
        getFilterArgFactory().all(getFilterArgFactory().property("Items"),
            getFilterFactory().gt("d:d/Quantity", 100)));

    assertEquals("Items/all(d:d/Quantity gt 100)", filter.build());
  }
  
  @Test
  public void issue1144All() {
    URIFilter andFilExp = getFilterFactory().and(getFilterFactory().eq("d/Quantity", 100), 
        getFilterFactory().eq("d/Quantity", 50));
    final URIFilter filter = getFilterFactory().match(
        getFilterArgFactory().all(getFilterArgFactory().property("Items"), "d", andFilExp));
    assertEquals("Items/all(d:((d/Quantity eq 100) and (d/Quantity eq 50)))", filter.build());
  }
}
