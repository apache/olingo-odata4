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
package org.apache.olingo.commons.core.op;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.data.AtomEntityImpl;
import org.apache.olingo.commons.core.data.AtomEntitySetImpl;
import org.apache.olingo.commons.core.data.AtomPropertyImpl;
import org.apache.olingo.commons.core.data.JSONEntityImpl;
import org.apache.olingo.commons.core.data.JSONEntitySetImpl;
import org.apache.olingo.commons.core.data.JSONPropertyImpl;

public class ResourceFactory {

  /**
   * Gets a new instance of <tt>EntitySet</tt>.
   *
   * @param resourceClass reference class.
   * @return {@link EntitySet} object.
   */
  public static EntitySet newEntitySet(final Class<? extends EntitySet> resourceClass) {
    EntitySet result = null;

    if (AtomEntitySetImpl.class.equals(resourceClass)) {
      result = new AtomEntitySetImpl();
    }
    if (JSONEntitySetImpl.class.equals(resourceClass)) {
      result = new JSONEntitySetImpl();
    }

    return result;
  }

  /**
   * Gets a new instance of <tt>Entity</tt>.
   *
   * @param resourceClass reference class.
   * @return {@link Entity} object.
   */
  public static Entity newEntity(final Class<? extends Entity> resourceClass) {
    Entity result = null;
    if (AtomEntityImpl.class.equals(resourceClass)) {
      result = new AtomEntityImpl();
    }
    if (JSONEntityImpl.class.equals(resourceClass)) {
      result = new JSONEntityImpl();
    }

    return result;
  }

  public static Property newProperty(final Class<? extends Entity> resourceClass) {
    Property result = null;
    if (AtomEntityImpl.class.equals(resourceClass)) {
      result = new AtomPropertyImpl();
    }
    if (JSONEntityImpl.class.equals(resourceClass)) {
      result = new JSONPropertyImpl();
    }

    return result;
  }

  /**
   * Gets entity set reference class from the given format.
   *
   * @param isXML whether it is JSON or XML / Atom
   * @return resource reference class.
   */
  public static Class<? extends EntitySet> entitySetClassForFormat(final boolean isXML) {
    return isXML ? AtomEntitySetImpl.class : JSONEntitySetImpl.class;
  }

  /**
   * Gets entity reference class from the given format.
   *
   * @param isXML whether it is JSON or XML / Atom
   * @return resource reference class.
   */
  public static Class<? extends Entity> entityClassForFormat(final boolean isXML) {
    return isXML ? AtomEntityImpl.class : JSONEntityImpl.class;
  }

  /**
   * Gets <tt>Entity</tt> object from entity set resource.
   *
   * @param resourceClass entity set reference class.
   * @return {@link Entity} object.
   */
  public static Class<? extends Entity> entityClassForEntitySet(final Class<? extends EntitySet> resourceClass) {
    Class<? extends Entity> result = null;

    if (AtomEntitySetImpl.class.equals(resourceClass)) {
      result = AtomEntityImpl.class;
    }
    if (JSONEntitySetImpl.class.equals(resourceClass)) {
      result = JSONEntityImpl.class;
    }

    return result;
  }

  public static ODataPubFormat formatForEntityClass(final Class<? extends Entity> reference) {
    return reference.equals(AtomEntityImpl.class) ? ODataPubFormat.ATOM : ODataPubFormat.JSON;
  }
}
