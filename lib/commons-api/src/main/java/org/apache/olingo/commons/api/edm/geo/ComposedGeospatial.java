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
package org.apache.olingo.commons.api.edm.geo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

 /**
 * Abstract base class for all Geometries that are composed out of other geospatial elements.
 *
 * @param <T> Type of the contained geospatial elements
 */
public abstract class ComposedGeospatial<T extends Geospatial> extends Geospatial implements Iterable<T> {

  private static final long serialVersionUID = 8796254901098541307L;

  protected final List<T> geospatials;

  protected ComposedGeospatial(final Dimension dimension, final Type type, final SRID srid,
      final List<T> geospatials) {

    super(dimension, type, srid);
    this.geospatials = new ArrayList<T>();
    if (geospatials != null) {
      this.geospatials.addAll(geospatials);
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public Iterator<T> iterator() {
    return this.geospatials.iterator();
  }

  /**
   * Checks if is empty.
   *
   * @return 'TRUE' if is empty; 'FALSE' otherwise.
   */
  public boolean isEmpty() {
    return geospatials.isEmpty();
  }
}
