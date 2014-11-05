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
package org.apache.olingo.server.core.batch.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.batch.BatchException.MessageKeys;
import org.apache.olingo.server.core.batch.parser.BatchParserCommon;

public class BatchChangeSetSorter {
  private static final String REG_EX_REFERENCE = "\\$(.*)(/.*)?";

  final List<ODataRequest> orderdList = new ArrayList<ODataRequest>();

  private static Pattern referencePattern = Pattern.compile(REG_EX_REFERENCE);
  private Set<String> knownContentIds = new HashSet<String>();
  private Map<String, List<ODataRequest>> requestReferenceMapping = new HashMap<String, List<ODataRequest>>();

  public BatchChangeSetSorter(List<ODataRequest> requests) throws BatchException {
    sort(requests);
  }

  public List<ODataRequest> getOrderdRequests() {
    return orderdList;
  }

  private List<ODataRequest> sort(final List<ODataRequest> requests) throws BatchException {
    extractUrlReference(requests);
    
    // Add requests without reference (roots)
    final List<ODataRequest> requestsWithoutReferences = getRequestsWithoutReferences();
    orderdList.addAll(requestsWithoutReferences);
    addRequestsToKnownContentIds(requestsWithoutReferences);
    
    // Find all requests which can be processed (parent request has been processed)
    // Compare level order
    boolean areRequestsProcessed = true;
    while (requestsToProcessAvailable() && areRequestsProcessed) {
      areRequestsProcessed = processNextLevel();
    }

    // Check if there are some requests which are not connected to a tree
    if (requestsToProcessAvailable()) {
      throw new BatchException("Invalid content id", MessageKeys.INVALID_CONTENT_ID, 0);
    }

    return orderdList;
  }

  private boolean requestsToProcessAvailable() {
    return requestReferenceMapping.keySet().size() != 0;
  }

  private boolean processNextLevel() {
    final List<ODataRequest> addedRequests = getRemainingRequestsWithKownContentId();
    addRequestsToKnownContentIds(addedRequests);
    orderdList.addAll(addedRequests);

    return addedRequests.size() != 0;
  }

  private List<ODataRequest> getRemainingRequestsWithKownContentId() {
    List<ODataRequest> result = new ArrayList<ODataRequest>();

    for (String contextId : knownContentIds) {
      final List<ODataRequest> requestsToProcess = requestReferenceMapping.get(contextId);
      if (requestsToProcess != null && requestsToProcess.size() != 0) {
        result.addAll(requestsToProcess);
        requestReferenceMapping.remove(contextId);
      }
    }

    return result;
  }

  private List<ODataRequest> getRequestsWithoutReferences() {
    List<ODataRequest> requests = requestReferenceMapping.get(null);
    requestReferenceMapping.remove(null);
    
    requests = (requests == null) ? new ArrayList<ODataRequest>() : requests;
    return requests;
  }

  private void addRequestsToKnownContentIds(List<ODataRequest> requestsWithoutReference) {
    for (ODataRequest request : requestsWithoutReference) {
      final String contentId = getContentIdFromHeader(request);
      if (contentId != null) {
        knownContentIds.add(contentId);
      }
    }
  }

  private String getContentIdFromHeader(ODataRequest request) {
    return request.getHeader(BatchParserCommon.HTTP_CONTENT_ID);
  }

  private void extractUrlReference(List<ODataRequest> requests) {
    for (ODataRequest request : requests) {
      final String reference = getReferenceInURI(request);
      addRequestToReferenceMapping(reference, request);
    }
  }

  private void addRequestToReferenceMapping(final String reference, final ODataRequest request) {
    List<ODataRequest> requestList = requestReferenceMapping.get(reference);
    requestList = (requestList == null) ? new ArrayList<ODataRequest>() : requestList;

    requestList.add(request);
    requestReferenceMapping.put(reference, requestList);
  }

  public static String getReferenceInURI(ODataRequest request) {
    Matcher matcher = referencePattern.matcher(removeSlash(removeSlash(request.getRawODataPath(), true), false));
    return (matcher.matches()) ? matcher.group(1) : null;
  }

  private static String removeSlash(String rawODataPath, boolean first) {
    final int indexOfSlash = rawODataPath.indexOf("/");
    if(first) {
      return (indexOfSlash == 0) ? rawODataPath.substring(1) : rawODataPath;
    } else {
      return (indexOfSlash != -1) ? rawODataPath.substring(0, indexOfSlash) : rawODataPath;
    }
  }

  public static void replaceContentIdReference(ODataRequest request, String contentId, String resourceUri) {
    final String newUri = request.getRawODataPath().replace("/$" + contentId, resourceUri);
    request.setRawODataPath(newUri);
    request.setRawRequestUri(request.getRawBaseUri() + "/" + newUri);
  }
}
