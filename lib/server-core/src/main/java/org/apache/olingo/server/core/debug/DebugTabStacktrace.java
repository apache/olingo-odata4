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

import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataLibraryException.ODataErrorMessage;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Exception debug information.
 */
public class DebugTabStacktrace implements DebugTab {

  private final Exception exception;

  public DebugTabStacktrace(final Exception exception) {
    this.exception = exception;
  }

  @Override
  public String getName() {
    return "Stacktrace";
  }

  @Override
  public void appendJson(final JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    gen.writeFieldName("exceptions");
    gen.writeStartArray();
    Throwable throwable = exception;
    while (throwable != null) {
      gen.writeStartObject();
      gen.writeStringField("class", throwable.getClass().getCanonicalName());
      gen.writeStringField("message", getMessageText(throwable));
      gen.writeFieldName("invocation");
      appendJsonStackTraceElement(gen, throwable.getStackTrace()[0]);
      gen.writeEndObject();

      // Get next exception in the cause list
      throwable = throwable.getCause();
    }
    gen.writeEndArray();

    gen.writeFieldName("stacktrace");
    gen.writeStartArray();
    if(exception != null){
      for (final StackTraceElement stackTraceElement : exception.getStackTrace()) {
        appendJsonStackTraceElement(gen, stackTraceElement);
      }
    }
    gen.writeEndArray();

    gen.writeEndObject();
  }

  private String getMessageText(final Throwable throwable) {
    String message;
    if (throwable instanceof ODataLibraryException) {
      ODataLibraryException ex = (ODataLibraryException) throwable;
      // We use the default locale
      ODataErrorMessage translatedMessage = ex.getTranslatedMessage(null);
      // We provide the best message we can
      message = translatedMessage.getMessage() == null ? ex.getMessage() : translatedMessage.getMessage();
    } else {
      message = throwable.getMessage();
    }
    return message;
  }

  private void appendJsonStackTraceElement(final JsonGenerator gen, final StackTraceElement element)
      throws IOException {
    gen.writeStartObject();
    gen.writeStringField("class", element.getClassName());
    gen.writeStringField("method", element.getMethodName());
    gen.writeNumberField("line", element.getLineNumber());
    gen.writeEndObject();
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {
    appendException(exception, writer);
    writer.append("<h2>Stacktrace</h2>\n");
    int count = 0;
    for (final StackTraceElement stackTraceElement : exception.getStackTrace()) {
      appendStackTraceElement(stackTraceElement, ++count == 1, count == exception.getStackTrace().length, writer);
    }
  }

  private void appendException(final Throwable throwable, final Writer writer) throws IOException {
    if (throwable.getCause() != null) {
      appendException(throwable.getCause(), writer);
    }
    final StackTraceElement details = throwable.getStackTrace()[0];
    writer.append("<h2>").append(throwable.getClass().getCanonicalName()).append("</h2>\n")
    .append("<p>")
    .append(DebugResponseHelperImpl.escapeHtml(getMessageText(throwable)))
    .append("</p>\n");
    appendStackTraceElement(details, true, true, writer);
  }

  private void appendStackTraceElement(final StackTraceElement stackTraceElement,
      final boolean isFirst, final boolean isLast, final Writer writer) throws IOException {
    if (isFirst) {
      writer.append("<table>\n<thead>\n")
      .append("<tr>\n<th class=\"name\">Class</th>\n")
      .append("<th class=\"name\">Method</th>\n")
      .append("<th class=\"value\">Line number in class</th>\n</tr>\n")
      .append("</thead>\n<tbody>\n");
    }
    writer.append("<tr>\n<td class=\"name\">").append(stackTraceElement.getClassName()).append("</td>\n")
    .append("<td class=\"name\">").append(stackTraceElement.getMethodName()).append("</td>\n")
    .append("<td class=\"value\">").append(Integer.toString(stackTraceElement.getLineNumber()))
    .append("</td>\n</tr>\n");
    if (isLast) {
      writer.append("</tbody>\n</table>\n");
    }
  }
}
