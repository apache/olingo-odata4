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
package org.apache.olingo.client.api.op;

import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.format.ODataFormat;

/**
 * Utility class for serialization.
 */
public interface ODataSerializer extends Serializable {

  /**
   * Writes Feed object onto the given stream.
   *
   * @param obj object to be streamed.
   * @param out output stream.
   */
  void feed(Feed obj, OutputStream out);

  /**
   * Writes Feed object by the given writer.
   *
   * @param obj object to be streamed.
   * @param writer writer.
   */
  void feed(Feed obj, Writer writer);

  /**
   * Writes theEntry object onto the given stream.
   *
   * @param obj object to be streamed.
   * @param out output stream.
   */
  void entry(Entry obj, OutputStream out);

  /**
   * Writes the Entry object by the given writer.
   *
   * @param obj object to be streamed.
   * @param writer writer.
   */
  void entry(Entry obj, Writer writer);

  /**
   * Writes the property object onto the given stream.
   *
   * @param property object to be streamed.
   * @param out output stream.
   */
  void property(Property property, OutputStream out);

  /**
   * Writes the property object by the given writer.
   *
   * @param property object to be streamed.
   * @param writer writer.
   */
  void property(Property property, Writer writer);

  /**
   * Writes link onto the given stream.
   *
   * @param link OData link to be streamed.
   * @param format streaming format.
   * @param out output stream.
   */
  void link(Link link, ODataFormat format, OutputStream out);

  /**
   * Writes link by the given writer.
   *
   * @param link OData link to be streamed.
   * @param format streaming format.
   * @param writer writer.
   */
  void link(Link link, ODataFormat format, Writer writer);
}
