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
package org.apache.olingo.commons.api.http;

/**
 * HTTP status codes as defined in RFC2616-sec10
 * and additional status codes as defined in RFC6585
 *
 */
public enum HttpStatusCode {
  OK(200, "OK"), CREATED(201, "Created"), ACCEPTED(202, "Accepted"), NO_CONTENT(204, "No Content"), RESET_CONTENT(205,
      "Reset Content"), PARTIAL_CONTENT(206, "Partial Content"),

  MOVED_PERMANENTLY(301, "Moved Permanently"), FOUND(302, "Found"), SEE_OTHER(303, "See Other"), NOT_MODIFIED(304,
      "Not Modified"), USE_PROXY(305, "Use Proxy"), TEMPORARY_REDIRECT(307, "Temporary Redirect"),

  BAD_REQUEST(400, "Bad Request"), UNAUTHORIZED(401, "Unauthorized"), PAYMENT_REQUIRED(402, "Payment Required"),
  FORBIDDEN(403, "Forbidden"), NOT_FOUND(404, "Not Found"), METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
  NOT_ACCEPTABLE(406, "Not Acceptable"), PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
  REQUEST_TIMEOUT(408, "Request Timeout"), CONFLICT(409, "Conflict"), GONE(410, "Gone"),
  LENGTH_REQUIRED(411, "Length Required"), PRECONDITION_FAILED(412, "Precondition Failed"),
  REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"), REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
  UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),  
  REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
  EXPECTATION_FAILED(417, "Expectation Failed"), PRECONDITION_REQUIRED(428, "Precondition Required"),

  INTERNAL_SERVER_ERROR(500, "Internal Server Error"), NOT_IMPLEMENTED(501, "Not Implemented"), BAD_GATEWAY(502,
      "Bad Gateway"), SERVICE_UNAVAILABLE(503, "Service Unavailable"), GATEWAY_TIMEOUT(504, "Gateway Timeout"),
  HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");
  private final int code;
  private final String info;

  HttpStatusCode(final int statusCode, final String info) {
    code = statusCode;
    this.info = info;
  }

  /**
   * Convert a numerical status code into the corresponding status enum object.
   *
   * @param statusCode the numerical status code
   * @return the matching status enum object or null if no matching enum is defined
   */
  public static HttpStatusCode fromStatusCode(final int statusCode) {
    for (final HttpStatusCode s : HttpStatusCode.values()) {
      if (s.code == statusCode) {
        return s;
      }
    }
    return null;
  }

  /**
   * Get the associated status code.
   *
   * @return the status code.
   */
  public int getStatusCode() {
    return code;
  }

  /**
   * Get the status code info.
   *
   * @return the status code info
   */
  public String getInfo() {
    return info;
  }

  /**
   * Get the status code info.
   *
   * @return the status code info
   */
  @Override
  public String toString() {
    return getInfo();
  }
}
