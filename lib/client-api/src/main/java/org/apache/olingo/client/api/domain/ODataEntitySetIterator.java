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
package org.apache.olingo.client.api.domain;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * OData entity set iterator class.
 * <br/>
 * <b>Please don't forget to call the <tt>close()>/</tt> method when not needed any more.</b>
 *
 * @param <E> concrete ODataEntity implementation
 * @param <ES> concrete ODataEntitySet implementation
 */
public class ODataEntitySetIterator<ES extends CommonODataEntitySet, E extends CommonODataEntity>
        implements Iterator<E> {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ODataEntitySetIterator.class);

  protected final CommonODataClient<?> odataClient;

  private final InputStream stream;

  private final ODataFormat format;

  protected ResWrap<Entity> cached;

  private ES entitySet;

  private final ByteArrayOutputStream osEntitySet;

  private final String namespaces;

  private boolean available = true;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param stream source stream.
   * @param format OData format.
   */
  public ODataEntitySetIterator(final CommonODataClient<?> odataClient, final InputStream stream,
          final ODataFormat format) {

    this.odataClient = odataClient;
    this.stream = stream;
    this.format = format;
    this.osEntitySet = new ByteArrayOutputStream();

    if (format == ODataFormat.ATOM) {
      namespaces = getAllElementAttributes(stream, "feed", osEntitySet);
    } else {
      namespaces = null;
      try {
        if (consume(stream, "\"value\":", osEntitySet, true) >= 0) {
          int c = 0;
          while (c != '[' && (c = stream.read()) >= 0) {
            osEntitySet.write(c);
          }
        }
      } catch (IOException e) {
        LOG.error("Error parsing entity set", e);
        throw new IllegalStateException(e);
      }
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean hasNext() {
    if (available && cached == null) {
      if (format == ODataFormat.ATOM) {
        cached = nextAtomEntityFromEntitySet(stream, osEntitySet, namespaces);
      } else {
        cached = nextJSONEntityFromEntitySet(stream, osEntitySet);
      }

      if (cached == null) {
        available = false;
        try {
          entitySet = (ES) odataClient.getReader().
                  readEntitySet(new ByteArrayInputStream(osEntitySet.toByteArray()), format);
        } catch (final ODataDeserializerException e) {
          available = false;
        }
        close();
      }
    }

    return available;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public E next() {
    if (hasNext()) {
      @SuppressWarnings("unchecked")
      final E res = (E) odataClient.getBinder().getODataEntity(cached);
      cached = null;
      return res;
    }

    throw new NoSuchElementException("No entity found");
  }

  /**
   * Unsupported operation.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Operation not supported");
  }

  /**
   * Closes the current iterator.
   */
  public void close() {
    IOUtils.closeQuietly(stream);
    IOUtils.closeQuietly(osEntitySet);
  }

  /**
   * Gets the next link if exists.
   *
   * @return next link if exists; null otherwise.
   */
  public URI getNext() {
    if (entitySet == null) {
      throw new IllegalStateException("Iteration must be completed in order to retrieve the link for next page");
    }
    return entitySet.getNext();
  }

  private ResWrap<Entity> nextJSONEntityFromEntitySet(final InputStream input, final OutputStream osEntitySet) {
    final ByteArrayOutputStream entity = new ByteArrayOutputStream();

    ResWrap<Entity> jsonEntity = null;
    try {
      int c;

      boolean foundNewOne = false;

      do {
        c = input.read();
        if (c == '{') {
          entity.write(c);
          c = -1;
          foundNewOne = true;
        }
        if (c == ']') {
          osEntitySet.write(c);
          c = -1;
        }
      } while (c >= 0);

      if (foundNewOne) {
        int count = 1;
        c = 0;

        while (count > 0 && c >= 0) {
          c = input.read();
          if (c == '{') {
            count++;
          } else if (c == '}') {
            count--;
          }
          entity.write(c);
        }

        if (c >= 0) {
          jsonEntity = odataClient.getDeserializer(ODataFormat.JSON).toEntity(
                  new ByteArrayInputStream(entity.toByteArray()));
        }
      } else {
        while ((c = input.read()) >= 0) {
          osEntitySet.write(c);
        }
      }
    } catch (Exception e) {
      LOG.error("Error retrieving entities from EntitySet", e);
    }

    return jsonEntity;
  }

  private ResWrap<Entity> nextAtomEntityFromEntitySet(
          final InputStream input, final OutputStream osEntitySet, final String namespaces) {

    final ByteArrayOutputStream entity = new ByteArrayOutputStream();

    ResWrap<Entity> atomEntity = null;

    try {
      if (consume(input, "<entry>", osEntitySet, false) >= 0) {
        entity.write("<entry ".getBytes(Constants.UTF8));
        entity.write(namespaces.getBytes(Constants.UTF8));
        entity.write(">".getBytes(Constants.UTF8));

        if (consume(input, "</entry>", entity, true) >= 0) {
          atomEntity = odataClient.getDeserializer(ODataFormat.ATOM).
                  toEntity(new ByteArrayInputStream(entity.toByteArray()));
        }
      }
    } catch (Exception e) {
      LOG.error("Error retrieving entities from EntitySet", e);
    }

    return atomEntity;
  }

  private String getAllElementAttributes(final InputStream input, final String name, final OutputStream os) {
    final ByteArrayOutputStream attrs = new ByteArrayOutputStream();

    String res;

    try {
      byte[] attrsDeclaration = null;

      final String key = "<" + name + " ";
      if (consume(input, key, os, true) >= 0 && consume(input, ">", attrs, false) >= 0) {
        attrsDeclaration = attrs.toByteArray();
        os.write(attrsDeclaration);
        os.write('>');
      }

      res = attrsDeclaration == null
              ? StringUtils.EMPTY
              : new String(attrsDeclaration, Constants.UTF8).trim();
    } catch (Exception e) {
      LOG.error("Error retrieving entities from EntitySet", e);
      res = StringUtils.EMPTY;
    }

    return res.endsWith("/") ? res.substring(0, res.length() - 1) : res;
  }

  private int consume(
          final InputStream input, final String end, final OutputStream os, final boolean includeEndKey)
          throws IOException {

    final char[] endKey = end.toCharArray();
    final char[] endLowerKey = end.toLowerCase().toCharArray();
    final char[] endUpperKey = end.toUpperCase().toCharArray();

    int pos = 0;
    int c = 0;
    while (pos < endKey.length && (c = input.read()) >= 0) {
      if (c == endLowerKey[pos] || c == endUpperKey[pos]) {
        pos++;
        if (includeEndKey && os != null) {
          os.write(c);
        }
      } else if (pos > 0) {
        if (!includeEndKey && os != null) {
          for (int i = 0; i < pos; i++) {
            os.write(endKey[i]);
          }
        }
        if (os != null) {
          os.write(c);
        }
        pos = 0;
      } else {
        if (os != null) {
          os.write(c);
        }
      }
    }

    return c;
  }
}
