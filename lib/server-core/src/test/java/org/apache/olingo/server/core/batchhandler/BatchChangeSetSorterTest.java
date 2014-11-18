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
package org.apache.olingo.server.core.batchhandler;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.core.batchhandler.BatchChangeSetSorter;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;
import org.junit.Test;

public class BatchChangeSetSorterTest {

  private static final String BASE_URI = "http://localhost/odata.src";
  
  @Test
  public void test() throws BatchException {
    final List<ODataRequest> changeSet = new ArrayList<ODataRequest>();
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "$1/Adress", "2"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "/Employees", "1"));
    
   BatchChangeSetSorter sorter = new BatchChangeSetSorter(changeSet);
   final List<ODataRequest> sortedChangeSet = sorter.getOrderdRequests();
   
   assertEquals(2, sortedChangeSet.size());
   assertEquals("1", getContentId(sortedChangeSet.get(0)));
   assertEquals("2", getContentId(sortedChangeSet.get(1)));
  }
  
  private String getContentId(ODataRequest request) {
    return request.getHeader(BatchParserCommon.HTTP_CONTENT_ID);
  }
  
  @Test
  public void testNoContentId() throws BatchException {
    final List<ODataRequest> changeSet = new ArrayList<ODataRequest>();
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "$1/Department", "2"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employees", "1"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employee('2')/Address"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employee('3')/Address"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "$2/Manager", "3"));
    
    BatchChangeSetSorter sorter = new BatchChangeSetSorter(changeSet);
   final List<ODataRequest> sortedChangeSet = sorter.getOrderdRequests();
   
   assertEquals(5, sortedChangeSet.size());
   assertEquals("1", getContentId(sortedChangeSet.get(0)));
   assertEquals(null, getContentId(sortedChangeSet.get(1)));
   assertEquals(null, getContentId(sortedChangeSet.get(2)));
   assertEquals("2", getContentId(sortedChangeSet.get(3)));
   assertEquals("3", getContentId(sortedChangeSet.get(4)));
  }
  
  @SuppressWarnings("unused")
  @Test(expected=BatchException.class)
  public void testContentIdNotAvailable() throws BatchException {
    final List<ODataRequest> changeSet = new ArrayList<ODataRequest>();
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "$1/Department", "2"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employees", "1"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employee('2')/Address"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employee('3')/Address"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "$4/Manager", "3")); //4 is not available
    
   BatchChangeSetSorter sorter = new BatchChangeSetSorter(changeSet);
   final List<ODataRequest> sortedChangeSet = sorter.getOrderdRequests();
  }
  
  @Test
  public void testStringAsContentId() throws BatchException {
    final List<ODataRequest> changeSet = new ArrayList<ODataRequest>();
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "$One/Department", "Two"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employees", "One"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employee('2')/Address"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "Employee('3')/Address"));
    changeSet.add(createRequest(HttpMethod.POST, BASE_URI, "$Two/Manager", "Three"));
    
   BatchChangeSetSorter sorter = new BatchChangeSetSorter(changeSet);
   final List<ODataRequest> sortedChangeSet = sorter.getOrderdRequests();
   
   assertEquals(5, sortedChangeSet.size());
   assertEquals("One", getContentId(sortedChangeSet.get(0)));
   assertEquals(null, getContentId(sortedChangeSet.get(1)));
   assertEquals(null, getContentId(sortedChangeSet.get(2)));
   assertEquals("Two", getContentId(sortedChangeSet.get(3)));
   assertEquals("Three", getContentId(sortedChangeSet.get(4)));
  }
  
  @Test
  public void testRewriting() {
    final String CONTENT_ID = "1";
    final String ODATA_PATH ="/$" + CONTENT_ID + "/Address";
    final String RESOURCE_URI = "Employee('1')";
    final ODataRequest request = createRequest(HttpMethod.POST, BASE_URI, ODATA_PATH);
    
    BatchChangeSetSorter.replaceContentIdReference(request, CONTENT_ID, RESOURCE_URI);
    assertEquals(BASE_URI + "/" + "Employee('1')/Address", request.getRawRequestUri());
    assertEquals("Employee('1')/Address", request.getRawODataPath());
  }
  
  @Test
  public void testRewritingNoContentId() {
    final String CONTENT_ID = "1";
    final String ODATA_PATH = /* "$" + CONTENT_ID + */ "Address";
    final String RESOURCE_URI = "Employee('1')";
    final ODataRequest request = createRequest(HttpMethod.POST, BASE_URI, ODATA_PATH);
    
    BatchChangeSetSorter.replaceContentIdReference(request, CONTENT_ID, RESOURCE_URI);
    assertEquals(BASE_URI + "/" + "Address", request.getRawRequestUri());
    assertEquals("Address", request.getRawODataPath());
  }
  
  @Test
  public void testWrongRewriting() {
    final String CONTENT_ID = "1";
    final String ODATA_PATH = /*"$1" */ "$2" + "/Address";
    final String RESOURCE_URI = "Employee('1')";
    final ODataRequest request = createRequest(HttpMethod.POST, BASE_URI, ODATA_PATH);
    
    BatchChangeSetSorter.replaceContentIdReference(request, CONTENT_ID, RESOURCE_URI);
    assertEquals(BASE_URI + "/" + "$2/Address", request.getRawRequestUri());
    assertEquals("$2/Address", request.getRawODataPath());
  }
  
  private ODataRequest createRequest(HttpMethod method, String baseUrl, String oDataPath) {
    return createRequest(method, baseUrl, oDataPath, null);
  }
  
  private ODataRequest createRequest(HttpMethod method, String baseUrl, String oDataPath, String contentId) {
    final ODataRequest request = new ODataRequest();
    request.setBody(new ByteArrayInputStream(new byte[0]));
    request.setMethod(HttpMethod.GET);
    request.setRawBaseUri(baseUrl);
    request.setRawODataPath(oDataPath);
    request.setRawRequestUri(baseUrl + "/" + oDataPath);
    request.setRawQueryPath("");
    
    if(contentId != null) {
      request.addHeader(BatchParserCommon.HTTP_CONTENT_ID, Arrays.asList(new String[] { contentId }));
    }
    return request;
  }
}
