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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.format.ContentType;
import org.custommonkey.xmlunit.Diff;

public class AtomTest extends JSONTest {

  @Override
  protected ContentType getODataPubFormat() {
    return ContentType.APPLICATION_ATOM_XML;
  }

  @Override
  protected ContentType getODataFormat() {
    return ContentType.APPLICATION_XML;
  }

  private String cleanup(final String input) throws Exception {
    final TransformerFactory factory = TransformerFactory.newInstance();
    final Source xslt = new StreamSource(getClass().getResourceAsStream("atom_cleanup.xsl"));
    final Transformer transformer = factory.newTransformer(xslt);

    final StringWriter result = new StringWriter();
    transformer.transform(new StreamSource(new ByteArrayInputStream(input.getBytes())), new StreamResult(result));
    return result.toString();
  }

  @Override
  protected void assertSimilar(final String filename, final String actual) throws Exception {
    final Diff diff = new Diff(cleanup(IOUtils.toString(getClass().getResourceAsStream(filename))), actual);
    diff.overrideElementQualifier(new AtomLinksQualifier());
    assertTrue(diff.similar());
  }

  @Override
  public void additionalEntities() throws Exception {
    // no test
  }

  @Override
  public void issueOLINGO390() throws Exception {
    // no test
  }

}
