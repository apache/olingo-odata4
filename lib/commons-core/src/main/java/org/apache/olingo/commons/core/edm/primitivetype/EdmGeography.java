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
package org.apache.olingo.commons.core.edm.primitivetype;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.Geospatial.Dimension;

public final class EdmGeography extends AbstractGeospatialType<Geospatial> {

  private static final EdmGeography INSTANCE = new EdmGeography();

  public static EdmGeography getInstance() {
    return INSTANCE;
  }

  public EdmGeography() {
    super(Geospatial.class, Dimension.GEOGRAPHY, null);
  }

  @Override
  protected <T> T internalValueOfString(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode, final Class<T> returnType)
          throws EdmPrimitiveTypeException {
    throw new EdmPrimitiveTypeException("Not implemented!");
  }

  @Override
  protected <T> String internalValueToString(final T value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    throw new EdmPrimitiveTypeException("Not implemented!");
  }
}
