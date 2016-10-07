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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.server.api.debug.RuntimeMeasurement;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Runtime debug information.
 */
public class DebugTabRuntime implements DebugTab {

  private static final int TO_MILLIS_DIVISOR = 1000;
  private final RuntimeNode rootNode;

  public DebugTabRuntime(final List<RuntimeMeasurement> runtimeInformation) {
    rootNode = new RuntimeNode();
    for (final RuntimeMeasurement runtimeMeasurement : runtimeInformation) {
      rootNode.add(runtimeMeasurement);
    }
    rootNode.combineRuntimeMeasurements();
  }

  @Override
  public String getName() {
    return "Runtime";
  }

  @Override
  public void appendJson(final JsonGenerator gen) throws IOException {
    appendJsonChildren(gen, rootNode);
  }

  private void appendJsonChildren(final JsonGenerator gen, final RuntimeNode node) throws IOException {
    gen.writeStartArray();
    for (RuntimeNode child : node.children) {
      appendJsonNode(gen, child);
    }
    gen.writeEndArray();
  }

  private void appendJsonNode(final JsonGenerator gen, final RuntimeNode node) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("class", node.className);
    gen.writeStringField("method", node.methodName);

    if (node.timeStopped == 0) {
      gen.writeNullField("duration");
    } else {
      gen.writeNumberField("duration", (node.timeStopped - node.timeStarted) / TO_MILLIS_DIVISOR);
      gen.writeStringField("unit", "µs");
    }

    if (!node.children.isEmpty()) {
      gen.writeFieldName("children");
      appendJsonChildren(gen, node);
    }

    gen.writeEndObject();
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {
    appendRuntimeNode(rootNode, "", true, writer);
  }

  private void appendRuntimeNode(final RuntimeNode node, final String draw, final boolean isLast, final Writer writer)
      throws IOException {
    if (node.className != null) {
      writer.append("<li>\n")
      .append("<span class=\"code\">")
      .append("<span class=\"draw\">").append(draw)
      .append(isLast ? "&#x2514;" : "&#x251C;").append("&#x2500;&nbsp;</span>")
      .append("<span class=\"class\">").append(node.className).append("</span>.")
      .append("<span class=\"method\">").append(node.methodName).append("(&hellip;)")
      .append("</span></span>");
      long time = node.timeStopped == 0 ? 0 : (node.timeStopped - node.timeStarted) / TO_MILLIS_DIVISOR;
      writer.append("<span class=\"").append(time == 0 ? "null" : "numeric")
      .append("\" title=\"").append(time == 0 ? "Stop time missing" : "Gross duration")
      .append("\">").append(time == 0 ? "unfinished" : Long.toString(time) + "&nbsp;&micro;s")
      .append("</span>\n");
    }
    if (!node.children.isEmpty()) {
      writer.append("<ol class=\"tree\">\n");
      for (final RuntimeNode childNode : node.children) {
        appendRuntimeNode(childNode,
            node.className == null ? draw : draw + (isLast ? "&nbsp;" : "&#x2502;") + "&nbsp;&nbsp;",
                node.children.indexOf(childNode) == node.children.size() - 1,
                writer);
      }
      writer.append("</ol>\n");
    }
    if (node.className != null) {
      writer.append("</li>\n");
    }
  }

  private class RuntimeNode {

    private String className;
    private String methodName;
    private long timeStarted;
    private long timeStopped;
    private List<RuntimeNode> children = new ArrayList<RuntimeNode>();

    protected RuntimeNode() {
      timeStarted = 0;
      timeStopped = Long.MAX_VALUE;
    }

    private RuntimeNode(final RuntimeMeasurement runtimeMeasurement) {
      className = runtimeMeasurement.getClassName();
      methodName = runtimeMeasurement.getMethodName();
      timeStarted = runtimeMeasurement.getTimeStarted();
      timeStopped = runtimeMeasurement.getTimeStopped();
    }

    protected boolean add(final RuntimeMeasurement runtimeMeasurement) {
      if (timeStarted <= runtimeMeasurement.getTimeStarted()
          && timeStopped != 0
          && timeStopped > runtimeMeasurement.getTimeStarted() // in case the stop time has not been set
          && timeStopped >= runtimeMeasurement.getTimeStopped()) {
        for (RuntimeNode candidate : children) {
          if (candidate.add(runtimeMeasurement)) {
            return true;
          }
        }
        children.add(new RuntimeNode(runtimeMeasurement));
        return true;
      } else {
        return false;
      }
    }

    /**
     * Combines runtime measurements with identical class names and method
     * names into one measurement, assuming that they originate from a loop
     * or a similar construct where a summary measurement has been intended.
     */
    protected void combineRuntimeMeasurements() {
      RuntimeNode preceding = null;
      for (Iterator<RuntimeNode> iterator = children.iterator(); iterator.hasNext();) {
        final RuntimeNode child = iterator.next();
        if (preceding != null
            && preceding.timeStopped != 0 && child.timeStopped != 0
            && preceding.timeStopped <= child.timeStarted
            && preceding.children.isEmpty() && child.children.isEmpty()
            && preceding.methodName.equals(child.methodName)
            && preceding.className.equals(child.className)) {
          preceding.timeStarted = child.timeStarted - (preceding.timeStopped - preceding.timeStarted);
          preceding.timeStopped = child.timeStopped;

          iterator.remove();
        } else {
          preceding = child;
          child.combineRuntimeMeasurements();
        }
      }
    }
  }
}
