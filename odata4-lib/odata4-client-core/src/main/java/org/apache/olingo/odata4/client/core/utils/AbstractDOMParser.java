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
package org.apache.olingo.odata4.client.core.utils;

import java.io.InputStream;
import java.io.Writer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM Parser.
 */
public abstract class AbstractDOMParser {

  /**
   * Parses the given input into a DOM tree.
   *
   * @param input stream to be parsed and de-serialized.
   * @return DOM tree
   */
  public abstract Element deserialize(InputStream input);

  /**
   * Writes DOM object by the given writer.
   *
   * @param content DOM to be streamed.
   * @param writer writer.
   */
  public abstract void serialize(Node content, Writer writer);
}
