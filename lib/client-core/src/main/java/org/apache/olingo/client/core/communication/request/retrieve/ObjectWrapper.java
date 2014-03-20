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
package org.apache.olingo.client.core.communication.request.retrieve;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.data.ODataError;
import org.apache.olingo.client.api.domain.ODataEntity;
import org.apache.olingo.client.api.domain.ODataEntitySet;
import org.apache.olingo.client.api.domain.ODataEntitySetIterator;
import org.apache.olingo.client.api.domain.ODataLinkCollection;
import org.apache.olingo.client.api.domain.ODataProperty;
import org.apache.olingo.client.api.domain.ODataServiceDocument;
import org.apache.olingo.client.api.domain.ODataValue;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.op.ODataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectWrapper {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ObjectWrapper.class);

  private final ODataReader reader;

  private final byte[] obj;

  private final String format;

  /**
   * Constructor.
   *
   * @param is source input stream.
   * @param format source format (<tt>ODataPubFormat</tt>, <tt>ODataFormat</tt>, <tt>ODataValueFormat</tt>,
   * <tt>ODataServiceDocumentFormat</tt>).
   */
  public ObjectWrapper(final ODataReader reader, final InputStream is, final String format) {
    this.reader = reader;
    try {
      this.obj = IOUtils.toByteArray(is);
      this.format = format;
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Parses stream as <tt>ODataEntitySetIterator</tt>.
   *
   * I
   *
   * @return <tt>ODataEntitySetIterator</tt> if success; null otherwise.
   */
  public ODataEntitySetIterator getODataEntitySetIterator() {
    return reader.read(new ByteArrayInputStream(obj), format, ODataEntitySetIterator.class);
  }

  /**
   * Parses stream as <tt>ODataEntitySet</tt>.
   *
   * @return <tt>ODataEntitySet</tt> if success; null otherwise.
   */
  public ODataEntitySet getODataEntitySet() {
    return reader.read(new ByteArrayInputStream(obj), format, ODataEntitySet.class);
  }

  /**
   * Parses stream as <tt>ODataEntity</tt>.
   *
   * @return <tt>ODataEntity</tt> if success; null otherwise.
   */
  public ODataEntity getODataEntity() {
    return reader.read(new ByteArrayInputStream(obj), format, ODataEntity.class);
  }

  /**
   * Parses stream as <tt>ODataProperty</tt>.
   *
   * @return <tt>ODataProperty</tt> if success; null otherwise.
   */
  public ODataProperty getODataProperty() {
    return reader.read(new ByteArrayInputStream(obj), format, ODataProperty.class);
  }

  /**
   * Parses stream as <tt>ODataLinkCollection</tt>.
   *
   * @return <tt>ODataLinkCollection</tt> if success; null otherwise.
   */
  public ODataLinkCollection getODataLinkCollection() {
    return reader.read(new ByteArrayInputStream(obj), format, ODataLinkCollection.class);
  }

  /**
   * Parses stream as <tt>ODataValue</tt>.
   *
   * @return <tt>ODataValue</tt> if success; null otherwise.
   */
  public ODataValue getODataValue() {
    return reader.read(new ByteArrayInputStream(obj), format, ODataValue.class);
  }

  /**
   * Parses stream as <tt>EdmMetadata</tt>.
   *
   * @return <tt>EdmMetadata</tt> if success; null otherwise.
   */
  public XMLMetadata getEdmMetadata() {
    return reader.read(new ByteArrayInputStream(obj), null, XMLMetadata.class);
  }

  /**
   * Parses stream as <tt>ODataServiceDocument</tt>.
   *
   * @return <tt>ODataServiceDocument</tt> if success; null otherwise.
   */
  public ODataServiceDocument getODataServiceDocument() {
    return reader.read(new ByteArrayInputStream(obj), format, ODataServiceDocument.class);
  }

  /**
   * Parses stream as <tt>ODataError</tt>.
   *
   * @return <tt>ODataError</tt> if success; null otherwise.
   */
  public ODataError getODataError() {
    return reader.read(new ByteArrayInputStream(obj), null, ODataError.class);
  }
}
