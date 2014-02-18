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
package org.apache.olingo.odata4.server.core.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.server.api.uri.UriInfoAll;
import org.apache.olingo.odata4.server.api.uri.UriInfoBatch;
import org.apache.olingo.odata4.server.api.uri.UriInfoCrossjoin;
import org.apache.olingo.odata4.server.api.uri.UriInfoEntityId;
import org.apache.olingo.odata4.server.api.uri.UriInfoKind;
import org.apache.olingo.odata4.server.api.uri.UriInfoMetadata;
import org.apache.olingo.odata4.server.api.uri.UriInfoResource;
import org.apache.olingo.odata4.server.api.uri.UriInfoService;
import org.apache.olingo.odata4.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.odata4.server.core.testutil.EdmTechProvider;
import org.apache.olingo.odata4.server.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriInfoImpl;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriResourceActionImpl;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriResourceEntitySetImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.InlineCountOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.LevelsOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.QueryOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SearchOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SkipOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SkipTokenOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.TopOptionImpl;
import org.junit.Test;

public class UriInfoImplTest {
  
  Edm edm = new EdmProviderImpl(new EdmTechTestProvider());
  
  @Test
  public void testKind() {
    UriInfoImpl uriInfo = new UriInfoImpl().setKind(UriInfoKind.all);
    assertEquals(UriInfoKind.all, uriInfo.getKind());
  }

  @Test
  public void testCasts() {
    UriInfoImpl uriInfo = new UriInfoImpl();

    UriInfoAll all = uriInfo.asUriInfoAll();
    assertEquals(uriInfo, all);

    UriInfoBatch batch = uriInfo.asUriInfoBatch();
    assertEquals(uriInfo, batch);

    UriInfoCrossjoin crossjoin = uriInfo.asUriInfoCrossjoin();
    assertEquals(uriInfo, crossjoin);

    UriInfoEntityId entityID = uriInfo.asUriInfoEntityId();
    assertEquals(uriInfo, entityID);

    UriInfoMetadata metadata = uriInfo.asUriInfoMetadata();
    assertEquals(uriInfo, metadata);

    UriInfoResource resource = uriInfo.asUriInfoResource();
    assertEquals(uriInfo, resource);

    UriInfoService service = uriInfo.asUriInfoService();
    assertEquals(uriInfo, service);

  }

  @Test
  public void testEntityNames() {
    UriInfoImpl uriInfo = new UriInfoImpl();
    uriInfo.addEntitySetName("A");
    uriInfo.addEntitySetName("B");

    assertEquals("A", uriInfo.getEntitySetNames().get(0));
    assertEquals("B", uriInfo.getEntitySetNames().get(1));
    
  }

  @Test
  public void testResourceParts() {
    UriInfoImpl uriInfo = new UriInfoImpl();

    UriResourceActionImpl action = new UriResourceActionImpl();
    UriResourceEntitySetImpl entitySet0 = new UriResourceEntitySetImpl();
    UriResourceEntitySetImpl entitySet1 = new UriResourceEntitySetImpl();

    uriInfo.addResourcePart(action);
    uriInfo.addResourcePart(entitySet0);

    assertEquals(action, uriInfo.getUriResourceParts().get(0));
    assertEquals(entitySet0, uriInfo.getUriResourceParts().get(1));
    
    assertEquals(entitySet0, uriInfo.getLastResourcePart());
    
    uriInfo.addResourcePart(entitySet1);
    assertEquals(entitySet1, uriInfo.getLastResourcePart());
  }

  @Test
  public void testCustomQueryOption() {
    UriInfoImpl uriInfo = new UriInfoImpl();

    List<QueryOptionImpl> queryOptions = new ArrayList<QueryOptionImpl>();

    ExpandOptionImpl expand = new ExpandOptionImpl();
    FilterOptionImpl filter = new FilterOptionImpl();
    FormatOptionImpl format = new FormatOptionImpl();
    IdOptionImpl id = new IdOptionImpl();
    InlineCountOptionImpl inlinecount = new InlineCountOptionImpl();
    OrderByOptionImpl orderby = new OrderByOptionImpl();
    SearchOptionImpl search = new SearchOptionImpl();
    SelectOptionImpl select = new SelectOptionImpl();
    SkipOptionImpl skip = new SkipOptionImpl();
    SkipTokenOptionImpl skipToken = new SkipTokenOptionImpl();
    TopOptionImpl top = new TopOptionImpl();
    LevelsOptionImpl levels = new LevelsOptionImpl();

    CustomQueryOptionImpl customOption0 = new CustomQueryOptionImpl();
    customOption0.setText("A");
    CustomQueryOptionImpl customOption1 = new CustomQueryOptionImpl();
    customOption1.setText("B");
    
    QueryOptionImpl queryOption = new QueryOptionImpl();

    queryOptions.add(expand);
    queryOptions.add(filter);
    queryOptions.add(format);
    queryOptions.add(id);
    queryOptions.add(inlinecount);
    queryOptions.add(orderby);
    queryOptions.add(search);
    queryOptions.add(select);
    queryOptions.add(skip);
    queryOptions.add(skipToken);
    queryOptions.add(top);
    queryOptions.add(customOption0);
    queryOptions.add(customOption1);
    queryOptions.add(levels);//not stored
    queryOptions.add(queryOption);//not stored
    uriInfo.setQueryOptions(queryOptions);

    assertEquals(expand, uriInfo.getExpandOption());
    assertEquals(filter, uriInfo.getFilterOption());
    assertEquals(format, uriInfo.getFormatOption());
    assertEquals(id, uriInfo.getIdOption());
    assertEquals(inlinecount, uriInfo.getInlineCountOption());
    assertEquals(orderby, uriInfo.getOrderByOption());
    assertEquals(search, uriInfo.getSearchOption());
    assertEquals(select, uriInfo.getSelectOption());
    assertEquals(skip, uriInfo.getSkipOption());
    assertEquals(skipToken, uriInfo.getSkipTokenOption());
    assertEquals(top, uriInfo.getTopOption());

    List<CustomQueryOption> customQueryOptions = uriInfo.getCustomQueryOptions();
    assertEquals(customOption0, customQueryOptions.get(0));
    assertEquals(customOption1, customQueryOptions.get(1));
  }
  
  @Test
  public void testFragment() {
    UriInfoImpl uriInfo = new UriInfoImpl();
    uriInfo.setFragment("F");
    assertEquals("F",uriInfo.getFragment());
  }
  
  @Test
  public void testEntityTypeCast() {
    UriInfoImpl uriInfo = new UriInfoImpl();
    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETKeyNav);
    assertNotNull(entityType);
    
    uriInfo.setEntityTypeCast(entityType);
    assertEquals(entityType,uriInfo.getEntityTypeCast());
  }
}
