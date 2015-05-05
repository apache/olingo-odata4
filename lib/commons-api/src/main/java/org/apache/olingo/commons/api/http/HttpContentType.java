/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.commons.api.http;

/**
 * Constants for <code>Http Content Type</code> definitions as specified in
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">RFC 2616 Section 14</a>.
 *
 */
public interface HttpContentType {

  public static final String APPLICATION_XML = "application/xml";
  public static final String APPLICATION_XML_UTF8 = APPLICATION_XML + ";charset=utf-8";

  public static final String APPLICATION_ATOM_XML = "application/atom+xml";
  public static final String APPLICATION_ATOM_XML_UTF8 = APPLICATION_ATOM_XML + ";charset=utf-8";
  public static final String APPLICATION_ATOM_XML_ENTRY = APPLICATION_ATOM_XML + ";type=entry";
  public static final String APPLICATION_ATOM_XML_ENTRY_UTF8 = APPLICATION_ATOM_XML_ENTRY + ";charset=utf-8";
  public static final String APPLICATION_ATOM_XML_FEED = APPLICATION_ATOM_XML + ";type=feed";
  public static final String APPLICATION_ATOM_XML_FEED_UTF8 = APPLICATION_ATOM_XML_FEED + ";charset=utf-8";
  public static final String APPLICATION_ATOM_SVC = "application/atomsvc+xml";
  public static final String APPLICATION_ATOM_SVC_UTF8 = APPLICATION_ATOM_SVC + ";charset=utf-8";

  public static final String APPLICATION_JSON = "application/json";
  public static final String APPLICATION_JSON_VERBOSE = APPLICATION_JSON + ";odata=verbose";
  public static final String APPLICATION_JSON_UTF8 = APPLICATION_JSON + ";charset=utf-8";
  public static final String APPLICATION_JSON_UTF8_VERBOSE = APPLICATION_JSON_UTF8 + ";odata=verbose";

  public static final String TEXT_PLAIN = "text/plain";
  public static final String TEXT_PLAIN_UTF8 = TEXT_PLAIN + ";charset=utf-8";

  public static final String TEXT_HTML = "text/html";

  public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

  public static final String APPLICATION_HTTP = "application/http";

  public static final String MULTIPART_MIXED = "multipart/mixed";

  public static final String WILDCARD = "*/*";
}
