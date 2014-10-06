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

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

/**
 * Major OData request/response header names.
 */
public enum HeaderName {

  /**
   * The OData protocol uses the Accept request-header field, as specified in [RFC2616].
   */
  accept("Accept", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  /**
   * As specified in [RFC2616], the client MAY specify the set of accepted character sets with the Accept-Charset
   * header.
   */
  acceptCharset("Accept-Charset", Arrays.asList(ODataServiceVersion.V40)),
  /**
   * As specified in [RFC2616], the client MAY specify the set of accepted natural languages with the Accept-Language
   * header.
   */
  acceptLanguage("Accept-Language", Arrays.asList(ODataServiceVersion.V40)),
  /**
   * The Content-Type header is used as specified in [RFC2616].
   * <br/>
   * OData request/response supports the following types:
   * <ul>
   * <li>application/atom+xml</li>
   * <li>application/atom+xml;type=entry</li>
   * <li>application/atom+xml;type=feed</li>
   * <li>application/json; odata=verbose</li>
   * <li>application/json</li>
   * <li>application/xml</li>
   * <li>text/plain</li>
   * <li>text/xml</li>
   * <li>octet/stream</li>
   * <li>multipart/mixed</li>
   * </ul>
   */
  contentType("Content-Type", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  /**
   * This header is a custom HTTP header defined for protocol versioning purposes. This header MAY be present on any
   * request or response message.
   */
  dataServiceVersion("DataServiceVersion", Arrays.asList(ODataServiceVersion.V30)),
  /**
   * This header is a custom HTTP header defined for protocol versioning purposes. This header MAY be present on any
   * request or response message.
   */
  odataVersion("OData-Version", Arrays.asList(ODataServiceVersion.V40)),
  /**
   * A response to a create operation that returns 204 No Content MUST include an OData-EntityId response header. The
   * value of the header is the entity-id of the entity that was acted on by the request. The syntax of the
   * OData-EntityId preference is specified in [OData-ABNF].
   */
  odataEntityId("OData-EntityId", Arrays.asList(ODataServiceVersion.V40)),
  /**
   * An ETag (entity tag) is an HTTP response header returned by an HTTP/1.1 compliant web server used to determine
   * change in content of a resource at a given URL. The value of the header is an opaque string representing the state
   * of the resource at the time the response was generated.
   */
  etag("ETag", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  /**
   * The If-Match request-header field is used with a method to make it conditional. As specified in [RFC2616], "the
   * purpose of this feature is to allow efficient updates of cached information with a minimum amount of transaction
   * overhead. It is also used, on updating requests, to prevent inadvertent modification of the wrong version of a
   * resource".
   */
  ifMatch("If-Match", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  /**
   * The If-None-Match request header is used with a method to make it conditional. As specified in [RFC2616], "The
   * purpose of this feature is to allow efficient updates of cached information with a minimum amount of transaction
   * overhead. It is also used to prevent a method (for example, PUT) from inadvertently modifying an existing resource
   * when the client believes that the resource does not exist."
   */
  ifNoneMatch("If-None-Match", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  /**
   * Clients SHOULD specify an OData-MaxVersion request header.
   * <br />
   * If specified the service MUST generate a response with an OData-Version less than or equal to the specified
   * OData-MaxVersion.
   * <br />
   * If OData-MaxVersion is not specified, then the service SHOULD interpret the request as having an OData-MaxVersion
   * equal to the maximum version supported by the service.
   */
  odataMaxVersion("OData-MaxVersion", Arrays.asList(ODataServiceVersion.V40)),
  /**
   * This header is a custom HTTP request only header defined for protocol versioning purposes. This header MAY be
   * present on any request message from client to server.
   */
  maxDataServiceVersion("MaxDataServiceVersion", Arrays.asList(ODataServiceVersion.V30)),
  /**
   * This header is a custom HTTP request only header defined for protocol versioning purposes. This header MAY be
   * present on any request message from client to server.
   */
  minDataServiceVersion("MinDataServiceVersion", Arrays.asList(ODataServiceVersion.V30)),
  /**
   * The OData-Isolation header specifies the isolation of the current request from external changes. The only supported
   * value for this header is snapshot.
   * <br />
   * If the service doesn’t support OData-Isolation:snapshot and this header was specified on the request, the service
   * MUST NOT process the request and MUST respond with 412 Precondition Failed.
   * <br />
   * Snapshot isolation guarantees that all data returned for a request, including multiple requests within a batch or
   * results retrieved across multiple pages, will be consistent as of a single point in time. Only data modifications
   * made within the request (for example, by a data modification request within the same batch) are visible. The effect
   * is as if the request generates a "snapshot" of the committed data as it existed at the start of the request.
   * <br />
   * The OData-Isolation header may be specified on a single or batch request. If it is specified on a batch then the
   * value is applied to all statements within the batch.
   * <br />
   * Next links returned within a snapshot return results within the same snapshot as the initial request; the client is
   * not required to repeat the header on each individual page request.
   * <br />
   * The OData-Isolation header has no effect on links other than the next link. Navigation links, read links, and edit
   * links return the current version of the data.
   * <br />
   * A service returns 410 Gone or 404 Not Found if a consumer tries to follow a next link referring to a snapshot that
   * is no longer available.
   * <br />
   * The syntax of the OData-Isolation header is specified in [OData-ABNF].
   * <br />
   * A service MAY specify the support for OData-Isolation:snapshot using an annotation with term
   * Capabilities.IsolationSupport, see [OData-VocCap].
   */
  odataIsolation("OData-Isolation", Arrays.asList(ODataServiceVersion.V40)),
  /**
   * A Prefer header is included in a request to state the client’s preferred, but not required, server behavior (that
   * is, a hint to the server). The Prefer header MAY be included on any request type (within a standalone or batch
   * request), and a server MAY honor the header for HTTP POST, PUT, PATCH, and MERGE requests. A Prefer header with a
   * value of “return-content” MUST NOT be specified on a DELETE request, a batch request as a whole, or a PUT request
   * to update a named stream.
   *
   * @see ODataPreferenceNames.
   */
  prefer("Prefer", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  /**
   * When a Prefer header value is successfully honored by the server, it MAY include a Preference-Applied response
   * header that states which preference values were honored by the server.
   */
  preferenceApplied("Preference-Applied", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  /**
   * The DataServiceId response header is returned by the server when the response payload for an HTTP PUT, POST, PATCH,
   * or MERGE request is empty. The value of the header is the identifier of the entity that was acted on by the PUT,
   * POST, PATCH, or MERGE request. The identifier, in this case, is the same identifier that would have been returned
   * in the response payload (for example, as the value of the atom:id element for Atom responses)
   */
  dataServiceId("DataServiceId", Arrays.asList(ODataServiceVersion.V30)),
  /**
   * Location header is used to specify the URL of an entity modified through a Data Modification request, or the
   * request URL to check on the status of an asynchronous operation as described in
   * <code>202 Accepted</code>.
   */
  location("Location", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  /**
   * A service must include a
   * <code>Retry-After</code> header in a
   * <code>202 Accepted</code>.
   */
  retryAfter("Retry-After", Arrays.asList(ODataServiceVersion.V30, ODataServiceVersion.V40)),
  dataServiceUrlConventions("DataServiceUrlConventions", Arrays.asList(ODataServiceVersion.V30)),
  slug("Slug", Arrays.asList(ODataServiceVersion.V30)),
  /**
   * This header is a custom HTTP request header.
   * <br/>
   * It is possible to instruct network intermediaries (proxies, firewalls, and so on) inspecting traffic at the
   * application protocol layer (for example, HTTP) to block requests that contain certain HTTP verbs. In practice, GET
   * and POST verbs are rarely blocked (traditional web pages rely heavily on these HTTP methods), while, for a variety
   * of reasons (such as security vulnerabilities in prior protocols), other HTTP methods (PUT, DELETE, and so on) are
   * at times blocked by intermediaries. Additionally, some existing HTTP libraries do not allow creation of requests
   * using verbs other than GET or POST. Therefore, an alternative way of specifying request types which use verbs other
   * than GET and POST is needed to ensure that this document works well in a wide range of environments.
   * <br/>
   * To address this need, the X-HTTP-Method header can be added to a POST request that signals that the server MUST
   * process the request not as a POST, but as if the HTTP verb specified as the value of the header was used as the
   * method on the HTTP request's request line, as specified in [RFC2616] section 5.1. This technique is often referred
   * to as "verb tunneling".
   * <br/>
   * This header is only valid when on POST requests.
   */
  xHttpMethod("X-HTTP-METHOD", Arrays.asList(ODataServiceVersion.V30));

  private final String headerName;

  private final List<ODataServiceVersion> supportedVersions;

  private HeaderName(final String headerName, final List<ODataServiceVersion> supportedVersions) {
    this.headerName = headerName;
    this.supportedVersions = supportedVersions;
  }

  final void isSupportedBy(final ODataServiceVersion serviceVersion) {
    if (!supportedVersions.contains(serviceVersion)) {
      throw new ODataRuntimeException("Unsupported header " + this.toString());
    }
  }

  @Override
  public String toString() {
    return headerName;
  }
}
