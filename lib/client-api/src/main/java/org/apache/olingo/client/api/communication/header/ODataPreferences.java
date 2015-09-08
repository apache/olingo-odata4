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
package org.apache.olingo.client.api.communication.header;

import org.apache.olingo.commons.api.format.PreferenceName;

/**
 * Values of the Prefer header.
 */
public class ODataPreferences {

  /**
   * <code>Prefer</code> header, return content.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   */
  public String returnContent() {
    return PreferenceName.RETURN.getName();
  }

  /**
   * <code>Prefer</code> header, return no content.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   */
  public String returnNoContent() {
    return PreferenceName.RETURN_NO_CONTENT.getName();
  }

  /**
   * Gets the value of the non-standard preference that the URL should follow the convention
   * that keys are formatted as separate segments.
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   */
  public String keyAsSegment() {
    return PreferenceName.KEY_AS_SEGMENT.getName();
  }

  /**
   * The odata.allow-entityreferences preference indicates that the service is allowed to return entity references in
   * place of entities that have previously been returned, with at least the properties requested, in the same response
   * (for example, when serializing the expanded results of many-to-many relationships). The service MUST NOT return
   * entity references in place of requested entities if odata.allow-entityreferences has not been specified in the
   * request, unless explicitly defined by other rules in this document. The syntax of the odata.allow-entityreferences
   * preference is specified in [OData-ABNF].
   * <br />
   * In the case the service applies the odata.allow-entityreferences preference it MUST include a Preference-Applied
   * response header containing the odata.allow-entityreferences preference to indicate that entity references MAY be
   * returned in place of entities that have previously been returned.
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String allowEntityReferences() {
    return PreferenceName.ALLOW_ENTITY_REFERENCES.getName();
  }

  /**
   * For scenarios in which links returned by the service are used by the client to poll for additional information, the
   * client can specify the odata.callback preference to request that the service notify the client when data is
   * available.
   * <br />
   * The odata.callback preference can be specified:
   * <ul>
   * <li>when requesting asynchronous processing of a request with the respond-async preference, or</li>
   * <li>on a GET request to a delta link.</li>
   * </ul>
   * <br />
   * The odata.callback preference MUST include the parameter url whose value is the URL of a callback endpoint to be
   * invoked by the OData service when data is available. The syntax of the odata.callback preference is specified in
   * [OData-ABNF]. For HTTP based callbacks, the OData service executes an HTTP GET request against the specified URL.
   * <br />
   * Services that support odata.callback SHOULD support notifying the client through HTTP. Services can advertise
   * callback support using the Capabilities.CallbackSupport annotation term defined in [OData-VocCap].
   * <br />
   * If the service applies the odata.callback preference it MUST include the odata.callback preference in the
   * Preference-Applied response header.
   * <br />
   * When the odata.callback preference is applied to asynchronous requests, the OData service invokes the callback
   * endpoint once it has finished processing the request. The status monitor resource, returned in the Location header
   * of the previously returned 202 Accepted response, can then be used to retrieve the results of the asynchronously
   * executed request.
   * <br />
   * When the odata.callback preference is specified on a GET request to a delta link and there are no changes
   * available, the OData service returns a 202 Accepted response with a Location header specifying the delta link to be
   * used to check for future updates. The OData service then invokes the specified callback endpoint once new changes
   * become available.
   * <br />
   * Combining respond-async, odata.callback and odata.track-changes preferences on a GET request to a delta-link might
   * influence the response in a couple of ways.
   * <ul>
   * <li>If the service processes the request synchronously, and no updates are available, then the response is the same
   * as if the respond-async hadn’t been specified and results in a response as described above.</li>
   * <li>If the service processes the request asynchronously, then it responds with a 202 Accepted response specifying
   * the URL to the status monitor resource as it would have with any other asynchronous request. Once the service has
   * finished processing the asynchronous request to the delta link resource, if changes are available it invokes the
   * specified callback endpoint. If no changes are available, the service SHOULD wait to notify the client until
   * changes are available. Once notified, the client uses the status monitor resource from the Location header of the
   * previously returned 202 Accepted response to retrieve the results. In case no updates were available after
   * processing the initial request, the result will contain no updates and the client can use the delta-link contained
   * in the result to retrieve the updates that have since become available.</li>
   * </ul>
   * <br />
   * If the consumer specifies the same URL as callback endpoint in multiple requests, the service MAY collate them into
   * a single notification once additional data is available for any of the requests. However, the consumer MUST be
   * prepared to deal with receiving up to as many notifications as it requested.
   * <br /><br />
   * Example: using a HTTP callback endpoint to receive notification
   * <br /><br />
   * Prefer: odata.callback; url="http://myserver/notfication/token/12345"
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String callback(final String url) {
    return PreferenceName.CALLBACK.getName() + ";url=\"" + url + "\"";
  }

  /**
   * The odata.continue-on-error preference on a batch request is used to request that, upon encountering a request
   * within the batch that returns an error, the service return the error for that request and continue processing
   * additional requests within the batch. The syntax of the odata.continue-on-error preference is specified in
   * [OData-ABNF].
   * <br />
   * If not specified, upon encountering an error the service MUST return the error within the batch and stop processing
   * additional requests within the batch.
   * <br />
   * A service MAY specify the support for the odata.continue-on-error preference using an annotation with term
   * Capabilities.BatchContinueOnErrorSupported, see [OData-VocCap].
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String continueOnError() {
    return PreferenceName.CONTINUE_ON_ERROR.getName();
  }

  /**
   * The odata.include-annotations preference in a request for data or metadata is used to specify the set of
   * annotations the client requests to be included, where applicable, in the response.
   * <br/>
   * The value of the odata.include-annotations preference is a comma-separated list of namespaces or namespace
   * qualified term names to include or exclude, with "*" representing all. The full syntax of the
   * odata.include-annotations preference is defined in [OData-ABNF].
   * <br/>
   * The most specific identifier always takes precedence. If the same identifier value is requested to both be excluded
   * and included the behavior is undefined; the service MAY return or omit the specified vocabulary but MUST NOT raise
   * an exception.
   * <br/><br/>
   * Example 1: a Prefer header requesting all annotations within a metadata document to be returned
   * <br/><br/>
   * Prefer: odata.include-annotations="*"
   * <br/><br/>
   * Example 2: a Prefer header requesting that no annotations are returned
   * <br/><br/>
   * Prefer: odata.include-annotations="-*"
   * <br/><br/>
   * Example 3: a Prefer header requesting that all annotations defined under the "display" namespace (recursively) be
   * returned
   * <br/><br/>
   * Prefer: odata.include-annotations="display.*"
   * <br/><br/>
   * Example 4: a Prefer header requesting that the annotation with the term name subject within the display namespace
   * be returned if applied
   * <br/><br/>
   * Prefer: odata.include-annotations="display.subject"
   * <br/><br/>
   * The odata.include-annotations preference is only a hint to the service. The service MAY ignore the preference and
   * is free to decide whether or not to return annotations not specified in the odata.include-annotations preference.
   * <br/>
   * In the case that the client has specified the odata.include-annotations preference in the request, the service
   * SHOULD include a Preference-Applied response header containing the odata.include-annotations preference to specify
   * the annotations actually included, where applicable, in the response. This value may differ from the annotations
   * requested in the Prefer header of the request.
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String includeAnnotations(final String value) {
    return PreferenceName.INCLUDE_ANNOTATIONS.getName() + "=" + value;
  }

  /**
   * The odata.maxpagesize preference is used to request that each collection within the response contain no more than
   * the number of items specified as the positive integer value of this preference. The syntax of the odata.maxpagesize
   * preference is specified in [OData-ABNF].
   * <br/><br/>
   * Example: a request for customers and their orders would result in a response containing one collection with
   * customer entities and for every customer a separate collection with order entities. The client could specify
   * <br/>
   * odata.maxpagesize=50
   * <br/>in order to request that each page of results contain a maximum of 50 customers, each with a maximum of 50
   * orders.
   * <br/><br/>
   * If a collection within the result contains more than the specified odata.maxpagesize, the collection SHOULD be a
   * partial set of the results with a next link to the next page of results. The client MAY specify a different value
   * for this preference with every request following a next link.
   * <br/>
   * In the example given above, the result page should include a next link for the customer collection, if there are
   * more than 50 customers, and additional next links for all returned orders collections with more than 50 entities.
   * <br/>
   * If the client has specified the odata.maxpagesize preference in the request, and the service limits the number of
   * items in collections within the response through server-driven paging, the service MAY include a Preference-Applied
   * response header containing the odata.maxpagesize preference and the maximum page size applied. This value may
   * differ from the value requested by the client.
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String maxPageSize(final int size) {
    return PreferenceName.MAX_PAGE_SIZE.getName() + "=" + size;
  }

  /**
   * The odata.track-changes preference is used to request that the service return a delta link that can subsequently be
   * used to obtain changes (deltas) to this result. The syntax of the odata.track-changes preference is specified in
   * [OData-ABNF].
   * <br />
   * For paged results, the preference MUST be specified on the initial request. Services MUST ignore the
   * odata.track-changes preference if applied to the next link.
   * <br />
   * The delta link MUST NOT be returned prior to the final page of results.
   * <br />
   * The service includes a Preference-Applied response header in the first page of the response containing the
   * odata.track-changes preference to signal that changes are being tracked.
   * <br />
   * A service MAY specify the support for the odata.track-changes preference using an annotation with term
   * Capabilities.ChangeTrackingSupport, see [OData-VocCap].
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String trackChanges() {
    return PreferenceName.TRACK_CHANGES.getName();
  }

  /**
   * The respond-async preference, as defined in [HTTP-Prefer], allows clients to request that the service process the
   * request asynchronously.
   * <br/>
   * If the client has specified respond-async in the request, the service MAY process the request asynchronously and
   * return a 202 Accepted response.
   * <br/>
   * The respond-async preference MAY be used for batch requests, but the service MUST ignore the respond-async
   * preference for individual requests within a batch request.
   * <br/>
   * In the case that the service applies the respond-async preference it MUST include a Preference-Applied response
   * header containing the respond-async preference.
   * <br/>
   * A service MAY specify the support for the respond-async preference using an annotation with term
   * Capabilities.AsynchronousRequestsSupported, see [OData-VocCap].
   * <br/><br/>
   * Example: a service receiving the following header might choose to respond
   * <ul>
   * <li>asynchronously if the synchronous processing of the request will take longer than 10 seconds</li>
   * <li>synchronously after 5 seconds</li>
   * <li>asynchronously (ignoring the wait preference)</li>
   * <li>synchronously after 15 seconds (ignoring respond-async preference and the wait preference)</li>
   * </ul>
   * <br/>
   * Prefer: respond-async, wait=10
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String respondAsync() {
    return PreferenceName.RESPOND_ASYNC.getName();
  }

  /**
   * The wait preference, as defined in [HTTP-Prefer], is used to establish an upper bound on the length of time, in
   * seconds, the client is prepared to wait for the service to process the request synchronously once it has been
   * received.
   * <br/>
   * If the respond-async preference is also specified, the client requests that the service respond asynchronously
   * after the specified length of time.
   * <br/>
   * If the respond-async preference has not been specified, the service MAY interpret the wait as a request to timeout
   * after the specified period of time.
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String wait(final int value) {
    return PreferenceName.WAIT.getName() + "=" + value;
  }

  /**
   * The return=representation and return=minimal preferences are defined in [HTTP-Prefer],
   * <br/>
   * In OData, return=representation or return=minimal is defined for use with a POST, PUT, or PATCH Data Modification
   * Request other than to a stream property, or to an Action Request. Specifying a preference of return=representation
   * or return=minimal in a GET or DELETE request, or any request to a stream property, SHOULD return a 4xx Client
   * Error.
   * <br/>
   * A preference of return=representation or return=minimal is allowed on an individual Data Modification Request or
   * Action Request within a batch, subject to the same restrictions, but SHOULD return a 4xx Client Error if specified
   * on the batch request itself.
   * <br/>
   * A preference of return=minimal requests that the service invoke the request but does not return content in the
   * response. The service MAY apply this preference by returning 204 No Content in which case it MAY include a
   * Preference-Applied response header containing the return=minimal preference.
   * <br/>
   * A preference of return=representation requests that the service invokes the request and returns the modified
   * entity. The service MAY apply this preference by returning the successfully modified resource in the body of the
   * response, formatted according to the rules specified for the requested format. In this case the service MAY include
   * a Preference-Applied response header containing the return=representation preference.
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String returnMinimal() {
    return PreferenceName.RETURN.getName() + "=minimal";
  }

  /**
   * The return=representation and return=minimal preferences are defined in [HTTP-Prefer],
   * <br/>
   * In OData, return=representation or return=minimal is defined for use with a POST, PUT, or PATCH Data Modification
   * Request other than to a stream property, or to an Action Request. Specifying a preference of return=representation
   * or return=minimal in a GET or DELETE request, or any request to a stream property, SHOULD return a 4xx Client
   * Error.
   * <br/>
   * A preference of return=representation or return=minimal is allowed on an individual Data Modification Request or
   * Action Request within a batch, subject to the same restrictions, but SHOULD return a 4xx Client Error if specified
   * on the batch request itself.
   * <br/>
   * A preference of return=minimal requests that the service invoke the request but does not return content in the
   * response. The service MAY apply this preference by returning 204 No Content in which case it MAY include a
   * Preference-Applied response header containing the return=minimal preference.
   * <br/>
   * A preference of return=representation requests that the service invokes the request and returns the modified
   * entity. The service MAY apply this preference by returning the successfully modified resource in the body of the
   * response, formatted according to the rules specified for the requested format. In this case the service MAY include
   * a Preference-Applied response header containing the return=representation preference.
   * <br/><br/>
   * Supported by OData version 4.0 only.
   *
   * @see org.apache.olingo.commons.api.http.HttpHeader#PREFER
   * @return preference.
   */
  public String returnRepresentation() {
    return PreferenceName.RETURN.getName() + "=representation";
  }
}
