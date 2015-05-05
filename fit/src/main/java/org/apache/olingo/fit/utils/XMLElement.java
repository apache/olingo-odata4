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
package org.apache.olingo.fit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLElement {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(XMLElement.class);

  private StartElement start;

  private EndElement end;

  private ByteArrayOutputStream content = new ByteArrayOutputStream();

  public StartElement getStart() {
    return start;
  }

  public void setStart(final StartElement start) {
    this.start = start;
  }

  public EndElement getEnd() {
    return end;
  }

  public void setEnd(final EndElement end) {
    this.end = end;
  }

  public InputStream getContent() throws XMLStreamException {
    return new ByteArrayInputStream(content.toByteArray());
  }

  public XMLEventReader getContentReader()
      throws XMLStreamException, IOException {
    return new XMLEventReaderWrapper(getContent());
  }

  public void setContent(final InputStream content) throws IOException {
    this.content.reset();

    final InputStreamReader reader = new InputStreamReader(content, Constants.ENCODING);
    final OutputStreamWriter writer = new OutputStreamWriter(this.content, Constants.ENCODING);
    IOUtils.copyLarge(reader, writer);

    writer.flush();
    IOUtils.closeQuietly(reader);
    IOUtils.closeQuietly(writer);
    IOUtils.closeQuietly(content);
  }

  public InputStream toStream() {
    InputStream res;
    try {
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final OutputStreamWriter osw = new OutputStreamWriter(bos, Constants.ENCODING);

      getStart().writeAsEncodedUnicode(osw);

      IOUtils.copy(getContent(), osw, Constants.ENCODING);

      getEnd().writeAsEncodedUnicode(osw);
      osw.flush();
      osw.close();

      res = new ByteArrayInputStream(bos.toByteArray());
    } catch (IOException e) {
      LOG.error("Error serializing element", e);
      res = null;
    } catch (XMLStreamException e) {
      LOG.error("Error serializing element", e);
      res = null;
    }
    return res;
  }
}
