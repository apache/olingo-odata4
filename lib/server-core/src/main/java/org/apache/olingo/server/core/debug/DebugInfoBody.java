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

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataResponse;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Response body debug information.
 */
public class DebugInfoBody implements DebugInfo {

  private static enum ResponseContent {JSON, XML, TEXT, IMAGE};
  
  private final ODataResponse response;
  private final ResponseContent responseContent;
  
  //private final String serviceRoot;
//  private final boolean isXml;
//  private final boolean isJson;
//  private final boolean isText;
//  private final boolean isImage;

  public DebugInfoBody(final ODataResponse response, final String serviceRoot) {
    this.response = response;
    // TODO: make header case insensitive
    final String contentType = response.getHeaders().get(HttpHeader.CONTENT_TYPE);
    //TODO: Differentiate better
    if (contentType != null) {
      responseContent = ResponseContent.JSON;
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
    gen.writeString(getContentString());
  }

  private String getContentString() {
    try {
      String contentString;
      switch (responseContent) {
      case IMAGE:
        //TODO: DecodeString as base 64
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

//
//  @Override
//  public void appendHtml(final Writer writer) throws IOException {
//    final String body = getContentString();
//    if (isImage) {
//      writer.append("<img src=\"data:").append(response.getContentHeader()).append(";base64,")
//          .append(body)
//          .append("\" />\n");
//    } else {
//      writer.append("<pre class=\"code").append(isXml ? " xml" : isJson ? " json" : "").append("\">\n")
//          .append(isXml || isJson ?
//              addLinks(ODataDebugResponseWrapper.escapeHtml(isXml ? formatXml(body) : formatJson(body)), isXml) :
//              ODataDebugResponseWrapper.escapeHtml(body))
//          .append("</pre>\n");
//    }
//  }
//
//  private String formatXml(final String xml) throws IOException {
//    try {
//      Transformer transformer = TransformerFactory.newInstance().newTransformer();
//      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//      StreamResult outputTarget = new StreamResult(new StringWriter());
//      transformer.transform(new StreamSource(new StringReader(xml)), outputTarget);
//      return outputTarget.getWriter().toString();
//    } catch (final TransformerException e) {
//      return xml;
//    }
//  }
//
//  private String formatJson(final String json) {
//    return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(new JsonParser().parse(json));
//  }
//
//  private String addLinks(final String source, final boolean isXml) {
//    final String debugOption = ODataDebugResponseWrapper.ODATA_DEBUG_QUERY_PARAMETER + "="
//        + ODataDebugResponseWrapper.ODATA_DEBUG_HTML;
//    final String urlPattern = "("
//        + (isXml ? "(?:href|src|base)=" : "\"(?:uri|media_src|edit_media|__next)\":\\p{Space}*")
//        + "\")(.+?)\"";
//    return (isXml ? source.replaceAll("(xmlns(?::\\p{Alnum}+)?=\")(.+?)\"", "$1<span class=\"ns\">$2</span>\"") :
//        source)
//        .replaceAll(urlPattern, "$1<a href=\"" + serviceRoot + "$2?" + debugOption + "\">$2</a>\"")
//        .replaceAll("(<a href=\"" + Pattern.quote(serviceRoot) + ')' + Pattern.quote(serviceRoot), "$1")
//        .replaceAll("<a href=\"(.+?)\\?(.+?)\\?" + debugOption, "<a href=\"$1?$2&amp;" + debugOption)
//        .replaceAll("&amp;amp;", "&amp;");
//  }

  @Override
  public void appendHtml(Writer writer) throws IOException {
    // TODO Auto-generated method stub

  }
}
