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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.debug.DebugResponseHelper;
import org.apache.olingo.server.api.debug.DebugSupport;
import org.apache.olingo.server.api.debug.RuntimeMeasurement;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class DebugResponseHelperImpl implements DebugResponseHelper {

  private static enum DebugFormat {
    JSON, HTML, DOWNLOAD
  };

  private final DebugFormat requestedFormat;

  public DebugResponseHelperImpl(String debugFormat) {
    if (DebugSupport.ODATA_DEBUG_HTML.equals(debugFormat)) {
      requestedFormat = DebugFormat.HTML;
    } else if (DebugSupport.ODATA_DEBUG_DOWNLOAD.equals(debugFormat)) {
      requestedFormat = DebugFormat.DOWNLOAD;
    } else {
      requestedFormat = DebugFormat.JSON;
    }
  }

  @Override
  public ODataResponse createDebugResponse(ODataRequest request, ODataResponse applicationResponse,
      Exception exception, Map<String, String> serverEnvironmentVaribles, List<RuntimeMeasurement> runtimeInformation) {

    try {
      final List<DebugInfo> parts =
          createParts(request, applicationResponse, exception, serverEnvironmentVaribles, runtimeInformation);
      
      ODataResponse response = new ODataResponse();
      String contentTypeString;
      InputStream body;
      switch (requestedFormat) {
      case DOWNLOAD:
        response.setHeader("Content-Disposition", "attachment; filename=OData-Response."
            + new Date().toString().replace(' ', '_').replace(':', '.') + ".html");
        // Download is the same as html except for the above header
      case HTML:
        body = wrapInHtml(parts);
        contentTypeString = ContentType.TEXT_HTML.toContentTypeString();
        break;
      case JSON:
      default:
        body = wrapInJson(parts);
        contentTypeString = ContentType.APPLICATION_JSON.toContentTypeString();
        break;
      }
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, contentTypeString);
      response.setContent(body);

      return response;
    } catch (IOException e) {
      // Should not happen
      // TODO: Check what we can do here.
      throw new ODataRuntimeException(e);
    }
  }

  private List<DebugInfo> createParts(ODataRequest request, ODataResponse applicationResponse, Exception exception,
      Map<String, String> serverEnvironmentVaribles, List<RuntimeMeasurement> runtimeInformation) {
    List<DebugInfo> parts = new ArrayList<DebugInfo>();

    // request
    parts.add(new DebugInfoRequest(request));

    // response
    // TODO: Check service URI
    parts.add(new DebugInfoResponse(applicationResponse, request.getRawBaseUri()));

    // server
    if (serverEnvironmentVaribles != null && !serverEnvironmentVaribles.isEmpty()) {
      parts.add(new DebugInfoServer(serverEnvironmentVaribles));
    }

//    // URI
//    Throwable candidate = exception;
//    while (candidate != null && !(candidate instanceof ExpressionParserException)) {
//      candidate = candidate.getCause();
//    }
//    final ExpressionParserException expressionParserException = (ExpressionParserException) candidate;
//    if (uriInfo != null
//        && (uriInfo.getFilter() != null || uriInfo.getOrderBy() != null
//            || !uriInfo.getExpand().isEmpty() || !uriInfo.getSelect().isEmpty())
//        || expressionParserException != null && expressionParserException.getFilterTree() != null) {
//      parts.add(new DebugInfoUri(uriInfo, expressionParserException));
//    }
//
//    // runtime measurements
    if (runtimeInformation != null && !runtimeInformation.isEmpty()) {
      parts.add(new DebugInfoRuntime(runtimeInformation));
    }
//
//    // exceptions
    if (exception != null) {
      parts.add(new DebugInfoException(exception));
    }

    return parts;
  }

  private InputStream wrapInJson(final List<DebugInfo> parts) throws IOException {
    CircleStreamBuffer csb = new CircleStreamBuffer();
    JsonGenerator gen = new JsonFactory().createGenerator(csb.getOutputStream(), JsonEncoding.UTF8);

    gen.writeStartObject();
    DebugInfo requestInfo = parts.get(0);
    // TODO: Should we really translate to lower case here?
    gen.writeFieldName(requestInfo.getName().toLowerCase(Locale.ROOT));
    requestInfo.appendJson(gen);

    DebugInfo responseInfo = parts.get(1);
    gen.writeFieldName(responseInfo.getName().toLowerCase(Locale.ROOT));
    responseInfo.appendJson(gen);

    gen.writeFieldName("server");
    gen.writeStartObject();
    String version = DebugResponseHelperImpl.class.getPackage().getImplementationVersion();
    if (version != null) {
      gen.writeStringField("version", version);
    } else {
      gen.writeNullField("version");
    }
    for (DebugInfo part : parts.subList(2, parts.size())) {
      gen.writeFieldName(part.getName().toLowerCase(Locale.ROOT));
      part.appendJson(gen);
    }
    gen.writeEndObject();

    gen.writeEndObject();
    gen.close();

    return csb.getInputStream();
  }

  private InputStream wrapInHtml(final List<DebugInfo> parts) throws IOException {
    StringWriter writer = new StringWriter();
//    PathInfo pathInfo = null;
//    try {
//      pathInfo = context.getPathInfo();
//    } catch (final ODataException e) {}
//
//    writer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n")
//        .append("  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n")
//        .append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n")
//        .append("<head>\n")
//        .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n")
//        .append("<title>")
//        .append(pathInfo == null ? "" :
//            escapeHtml(pathInfo.getServiceRoot().relativize(pathInfo.getRequestUri()).getPath()))
//        .append("</title>\n")
//        .append("<style type=\"text/css\">\n")
//        .append("body { font-family: Arial, sans-serif; font-size: 13px;\n")
//        .append("       line-height: 16px; margin: 0;\n")
//        .append("       background-color: #eeeeee; color: #333333; }\n")
//        .append(".header { float: left; }\n")
//        .append(".header a { line-height: 22px; padding: 10px 18px;\n")
//        .append("            text-decoration: none; color: #333333; }\n")
//        .append(":target, .header:nth-last-child(2) { background-color: #cccccc; }\n")
//        .append(":target ~ .header:nth-last-child(2) { background-color: inherit; }\n")
//        .append(".header:focus, .header:hover,\n")
//        .append("  .header:nth-last-child(2):focus, .header:nth-last-child(2):hover\n")
//        .append("    { background-color: #999999; }\n")
//        .append(".section { position: absolute; top: 42px; min-width: 100%;\n")
//        .append("           padding-top: 18px; border-top: 1px solid #dddddd; }\n")
//        .append(".section > * { margin-left: 18px; }\n")
//        .append(":target + .section, .section:last-child { display: block; }\n")
//        .append(".section, :target + .section ~ .section { display: none; }\n")
//        .append("h1 { font-size: 18px; font-weight: normal; margin: 10px 0; }\n")
//        .append("h2 { font-size: 15px; }\n")
//        .append("h2:not(:first-child) { margin-top: 2em; }\n")
//        .append("table { border-collapse: collapse; border-spacing: 0;\n")
//        .append("        margin-top: 1.5em; }\n")
//        .append("table, thead { border-width: 1px 0; border-style: solid;\n")
//        .append("               border-color: #dddddd; text-align: left; }\n")
//        .append("th.name, td.name { padding: 1ex 2em 1ex 0; }\n")
//        .append("tbody > tr:hover { background-color: #cccccc; }\n")
//        .append(".code { font-family: \"Courier New\", monospace; }\n")
//        .append(".code, .tree li { line-height: 15px; }\n")
//        .append(".code a { text-decoration: underline; color: #666666; }\n")
//        .append(".xml .ns { font-style: italic; color: #999999; }\n")
//        .append("ul, .tree { list-style-type: none; }\n")
//        .append("div > ul.expr, div > .expand, .tree { padding-left: 0; }\n")
//        .append(".expr, .expand, .null, .numeric { padding-left: 1.5em; }\n")
//        .append("</style>\n")
//        .append("</head>\n")
//        .append("<body>\n");
//    char count = '0';
//    for (final DebugInfo part : parts) {
//      writer.append("<div class=\"header\" id=\"sec").append(++count).append("\">\n")
//          .append("<h1><a href=\"#sec").append(count).append("\">")
//          .append(part.getName())
//          .append("</a></h1>\n")
//          .append("</div>\n")
//          .append("<div class=\"section\">\n");
//      part.appendHtml(writer);
//      writer.append("</div>\n");
//    }
//    writer.append("</body>\n")
//        .append("</html>\n")
//        .close();
    byte[] bytes = writer.toString().getBytes("UTF-8");
    return new ByteArrayInputStream(bytes);
  }

  protected static String escapeHtml(final String value) {
    return value == null ? null : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  protected static void appendJsonTable(final JsonGenerator gen, final Map<String, String> entries)
      throws IOException {
    gen.writeStartObject();

    for (Map.Entry<String, String> entry : entries.entrySet()) {
      if (entry.getValue() != null) {
        gen.writeStringField(entry.getKey(), entry.getValue());
      } else {
        gen.writeNullField(entry.getKey());
      }
    }
    gen.writeEndObject();
  }
//
//  protected static void appendHtmlTable(final Writer writer, final Map<String, String> entries) throws IOException {
//    writer.append("<table>\n<thead>\n")
//        .append("<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n")
//        .append("</thead>\n<tbody>\n");
//    for (final String name : entries.keySet()) {
//      final String value = entries.get(name);
//      if (value != null) {
//        writer.append("<tr><td class=\"name\">").append(name).append("</td>")
//            .append("<td class=\"value\">")
//            .append(ODataDebugResponseWrapper.escapeHtml(value))
//            .append("</td></tr>\n");
//      }
//    }
//    writer.append("</tbody>\n</table>\n");
//  }

}
