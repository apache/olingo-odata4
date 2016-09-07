/*
 * Copyright 2016 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olingo.fit.tecsvc.client;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;


public class JSONFormatConformanceTestITCase extends AbstractParamTecSvcITCase  {
  
  /**
   * MUST either:
   * <ol>
   * <li>understand <tt>odata.metadata=minimal</tt> (section 3.1.1) or</li>
   * <li>explicitly specify <tt>odata.metadata=none</tt>(section 3.1.3) or <tt>odata.metadata=full</tt> (section 3.1.2)
   * in the request (client)</li>
   * </ol>
   * .
   */
  @Test
  public void item1() throws EdmPrimitiveTypeException {
    assumeTrue("json conformance test with content type", isJson());
    final EdmEnabledODataClient edmClient = getEdmEnabledClient();
    Map<String, Object> segmentValues = new LinkedHashMap<String, Object>();
    segmentValues.put("PropertyInt16", 1);
    segmentValues.put("PropertyString", "1");
    final URI uri = edmClient.newURIBuilder().
        appendEntitySetSegment("ESKeyNav").appendKeySegment(1).
        appendNavigationSegment("NavPropertyETTwoKeyNavMany").appendKeySegment(segmentValues).build();
    final ODataEntityRequest<ClientEntity> req = edmClient.getRetrieveRequestFactory().getEntityRequest(uri);

    // request format (via Accept header) is set to minimal by default
    //FIXME: set format to minimal?
    assertEquals("application/json", req.getAccept());  

    final ODataRetrieveResponse<ClientEntity> res = req.execute();

    // response is odata.metadata=minimal
    assertFalse(res.getContentType().contains("odata.metadata=none"));
    assertFalse(res.getContentType().contains("odata.metadata=full"));

    // response payload is understood
    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    String entityType = entity.getTypeName().toString();
    assertEquals("olingo.odata.test1.ETTwoKeyNav", entityType);
    assertEquals(1, entity.getProperty("PropertyInt16").getPrimitiveValue().toCastValue(Integer.class), 0);
    assertEquals("olingo.odata.test1.CTPrimComp", entity.getProperty("PropertyComp").getComplexValue().getTypeName());
  }
}
