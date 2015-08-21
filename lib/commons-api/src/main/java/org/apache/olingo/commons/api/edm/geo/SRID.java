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

import org.apache.olingo.commons.api.edm.geo.Geospatial.Dimension;

import java.io.Serializable;

/**
 * A geometry or geography property MAY define a value for the SRID attribute. The value of this attribute identifies
 * which spatial reference system is applied to values of the property on type instances.
 * <br/>
 * The value of the SRID attribute MUST be a non-negative integer or the special value <tt>variable</tt>. If no value is
 * specified, the attribute defaults to 0 for Geometry types or 4326 for Geography types.
 * <br/>
 * Standards Track Work Product Copyright © OASIS Open 2013. All Rights Reserved. 19 November 2013 Page 22 of 83The
 * valid values of the SRID attribute and their meanings are as defined by the European Petroleum Survey Group [EPSG].
 */
public final class SRID implements Serializable {

  private static final String VARIABLE = "variable";

  private static final long serialVersionUID = 8412685060902464629L;

  private Dimension dimension = Dimension.GEOGRAPHY;

  private Integer value;

  private Boolean variable;

  public static SRID valueOf(final String exp) {
    final SRID instance = new SRID();

    if (VARIABLE.equalsIgnoreCase(exp)) {
      instance.variable = Boolean.TRUE;
    } else {
      instance.value = Integer.valueOf(exp);
      if (instance.value < 0) {
        throw new IllegalArgumentException(
            "The value of the SRID attribute MUST be a non-negative integer or the special value 'variable'");
      }
    }

    return instance;
  }

  protected SRID() {
    // empty constructor for package instantiation
  }

  public Dimension getDimension() {
    return dimension;
  }

  public void setDimension(final Dimension dimension) {
    this.dimension = dimension;
  }

  private String getValue() {
    if (value == null) {
      if (dimension == Dimension.GEOMETRY) {
        return "0";
      } else {
        return "4326";
      }
    }

    return value.toString();
//    return value == null ? dimension == Dimension.GEOMETRY
//        ? "0"
//            : "4326"
//        : value.getName();
  }

  private boolean isVariable() {
    return variable != null && variable;
  }

  public boolean isNotDefault() {
    return value != null || variable != null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SRID srid = (SRID) o;

    if (dimension != srid.dimension) {
      return false;
    }
    if (value != null ? !value.equals(srid.value) : srid.value != null) {
      return false;
    }
    return !(variable != null ? !variable.equals(srid.variable) : srid.variable != null);

  }

  @Override
  public int hashCode() {
    int result = dimension != null ? dimension.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + (variable != null ? variable.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return isVariable()
        ? VARIABLE
        : getValue();
  }
}
