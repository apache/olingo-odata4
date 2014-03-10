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
package org.apache.olingo.client.core.xml;

import org.apache.olingo.client.api.utils.XMLUtils;

import java.io.InputStream;
import java.io.Writer;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AndroidDOMParserImpl extends AbstractDOMParser {

  @Override
  public Element deserialize(final InputStream input) {
    try {
      return XMLUtils.DOC_BUILDER_FACTORY.newDocumentBuilder().parse(input).getDocumentElement();
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not parse DOM", e);
    }
  }

  @Override
  public void serialize(final Node content, final Writer writer) {
    try {
      TransformerFactory.newInstance().newTransformer().
              transform(new DOMSource(content), new StreamResult(writer));
    } catch (Exception e) {
      throw new IllegalArgumentException("While serializing DOM element", e);
    }
  }
}
