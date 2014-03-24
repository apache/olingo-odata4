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
package org.apache.olingo.client.core.op;

import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.data.AtomEntryImpl;
import org.apache.olingo.commons.core.data.AtomFeedImpl;
import org.apache.olingo.commons.core.data.AtomPropertyImpl;
import org.apache.olingo.commons.core.data.JSONEntryImpl;
import org.apache.olingo.commons.core.data.JSONFeedImpl;
import org.apache.olingo.commons.core.data.JSONPropertyImpl;

public class ResourceFactory {

  /**
   * Gets a new instance of <tt>Feed</tt>.
   *
   * @param resourceClass reference class.
   * @return <tt>Feed</tt> object.
   */
  public static Feed newFeed(final Class<? extends Feed> resourceClass) {
    Feed result = null;

    if (AtomFeedImpl.class.equals(resourceClass)) {
      result = new AtomFeedImpl();
    }
    if (JSONFeedImpl.class.equals(resourceClass)) {
      result = new JSONFeedImpl();
    }

    return result;
  }

  /**
   * Gets a new instance of <tt>Entry</tt>.
   *
   * @param resourceClass reference class.
   * @return <tt>Entry</tt> object.
   */
  public static Entry newEntry(final Class<? extends Entry> resourceClass) {
    Entry result = null;
    if (AtomEntryImpl.class.equals(resourceClass)) {
      result = new AtomEntryImpl();
    }
    if (JSONEntryImpl.class.equals(resourceClass)) {
      result = new JSONEntryImpl();
    }

    return result;
  }

  public static Property newProperty(final Class<? extends Entry> resourceClass) {
    Property result = null;
    if (AtomEntryImpl.class.equals(resourceClass)) {
      result = new AtomPropertyImpl();
    }
    if (JSONEntryImpl.class.equals(resourceClass)) {
      result = new JSONPropertyImpl();
    }

    return result;
  }

  /**
   * Gets feed reference class from the given format.
   *
   * @param isXML whether it is JSON or XML / Atom
   * @return resource reference class.
   */
  public static Class<? extends Feed> feedClassForFormat(final boolean isXML) {
    return isXML ? AtomFeedImpl.class : JSONFeedImpl.class;
  }

  /**
   * Gets entry reference class from the given format.
   *
   * @param isXML whether it is JSON or XML / Atom
   * @return resource reference class.
   */
  public static Class<? extends Entry> entryClassForFormat(final boolean isXML) {
    return isXML ? AtomEntryImpl.class : JSONEntryImpl.class;
  }

  /**
   * Gets <tt>Entry</tt> object from feed resource.
   *
   * @param resourceClass feed reference class.
   * @return <tt>Entry</tt> object.
   */
  public static Class<? extends Entry> entryClassForFeed(final Class<? extends Feed> resourceClass) {
    Class<? extends Entry> result = null;

    if (AtomFeedImpl.class.equals(resourceClass)) {
      result = AtomEntryImpl.class;
    }
    if (JSONFeedImpl.class.equals(resourceClass)) {
      result = JSONEntryImpl.class;
    }

    return result;
  }

  public static ODataPubFormat formatForEntryClass(final Class<? extends Entry> reference) {
    return reference.equals(AtomEntryImpl.class) ? ODataPubFormat.ATOM : ODataPubFormat.JSON;
  }
}
