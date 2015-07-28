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
package org.apache.olingo.server.core.debug;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.debug.DebugSupport;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Response body debug information.
 */
public class DebugTabBody implements DebugTab {

  private static enum ResponseContent {
    JSON, XML, TEXT, IMAGE
  };

  private final ODataResponse response;
  private final ResponseContent responseContent;

  private final String serviceRoot;

//  private final boolean isXml;
//  private final boolean isJson;
//  private final boolean isText;
//  private final boolean isImage;

  public DebugTabBody(final ODataResponse response, final String serviceRoot) {
    this.response = response;
    this.serviceRoot = serviceRoot == null ? "/" : serviceRoot;
    if (response != null) {
      final String contentType = response.getHeaders().get(HttpHeader.CONTENT_TYPE);
      // TODO: Differentiate better
      if (contentType != null) {
        responseContent = ResponseContent.JSON;
      } else {
        responseContent = ResponseContent.TEXT;
      }
    } else {
      responseContent = ResponseContent.TEXT;
    }
//    isXml = contentType.contains("xml");
//    isJson = !isXml && contentType.startsWith(HttpContentType.APPLICATION_JSON);
//    isText = isXml || isJson || contentType.startsWith("text/")
//        || contentType.startsWith(HttpContentType.APPLICATION_HTTP)
//        || contentType.startsWith(HttpContentType.MULTIPART_MIXED);
//    isImage = !isText && contentType.startsWith("image/");
  }

  @Override
  public String getName() {
    return "Body";
  }

//
  @Override
  public void appendJson(final JsonGenerator gen) throws IOException {
    if (response == null || response.getContent() == null) {
      gen.writeNull();
    } else {
      gen.writeString(getContentString());
    }
  }

  private String getContentString() {
    try {
      String contentString;
      switch (responseContent) {
      case IMAGE:
        // TODO: DecodeString as base 64
        contentString = "Currently not supported";
        break;
      case JSON:
      case XML:
      case TEXT:
      default:
        // TODO: Remove IOUtils from core dependency
        contentString = IOUtils.toString(response.getContent(), "UTF-8");
        break;
      }
      return contentString;
    } catch (IOException e) {
      return "Could not parse Body for Debug Output";
    }
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {

    final String body =
        response == null || response.getContent() == null ? "ODataLibrary: No body." : getContentString();
    switch (responseContent) {
    case XML:
      writer.append("<pre class=\"code").append("xml").append("\">\n");
      writer.append(addLinks(DebugResponseHelperImpl.escapeHtml(body)));
      writer.append("</pre>\n");
      break;
    case JSON:
      writer.append("<pre class=\"code").append("json").append("\">\n");
      writer.append(addLinks(DebugResponseHelperImpl.escapeHtml(body)));
      writer.append("</pre>\n");
      break;
    case IMAGE:
      // Make header query case insensitive
      writer.append("<img src=\"data:").append(response.getHeaders().get(HttpHeader.CONTENT_TYPE)).append(";base64,")
          .append(body)
          .append("\" />\n");
      break;
    case TEXT:
    default:
      writer.append("<pre class=\"code").append("").append("\">\n");
      writer.append(DebugResponseHelperImpl.escapeHtml(body));
      writer.append("</pre>\n");
      break;
    }
  }

  private String addLinks(final String source) {
    final String debugOption = DebugSupport.ODATA_DEBUG_QUERY_PARAMETER + "=" + DebugSupport.ODATA_DEBUG_HTML;
    final String urlPattern = "("
        + (responseContent == ResponseContent.XML ?
            "(?:href|src|base)=" : "\"(?:uri|media_src|edit_media|__next)\":\\p{Space}*")
        + "\")(.+?)\"";
    return (responseContent == ResponseContent.XML ?
        source.replaceAll("(xmlns(?::\\p{Alnum}+)?=\")(.+?)\"", "$1<span class=\"ns\">$2</span>\"") : source)
        .replaceAll(urlPattern, "$1<a href=\"" + serviceRoot + "$2?" + debugOption + "\">$2</a>\"")
        .replaceAll("(<a href=\"" + Pattern.quote(serviceRoot) + ')' + Pattern.quote(serviceRoot), "$1")
        .replaceAll("<a href=\"(.+?)\\?(.+?)\\?" + debugOption, "<a href=\"$1?$2&amp;" + debugOption)
        .replaceAll("&amp;amp;", "&amp;");
  }
}
