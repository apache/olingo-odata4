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
package org.apache.olingo.commons.api.edm.constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a container for the supported ODataServiceVersions.
 */
public enum ODataServiceVersion {
  /**
   * OData Version 1.0
   */
  V10("1.0"),
  /**
   * OData Version 2.0
   */
  V20("2.0"),
  /**
   * OData Version 3.0
   */
  V30("3.0"),
  /**
   * OData Version 4.0
   */
  V40("4.0"),
  /**
   * OData Version 4.01
   */
  V401("4.01");


  private static final Pattern DATASERVICEVERSIONPATTERN = Pattern.compile("(\\p{Digit}+\\.\\p{Digit}+)(:?;.*)?");

  /**
   * Validates format and range of a data service version string.
   *
   * @param version version string
   * @return <code>true</code> for a valid version
   */
  public static boolean validateDataServiceVersion(final String version) {
    final Matcher matcher = DATASERVICEVERSIONPATTERN.matcher(version);
    if (matcher.matches()) {
      final String possibleDataServiceVersion = matcher.group(1);
      return V10.toString().equals(possibleDataServiceVersion)
          || V20.toString().equals(possibleDataServiceVersion)
          || V30.toString().equals(possibleDataServiceVersion)
          || V40.toString().equals(possibleDataServiceVersion)
          || V401.toString().equals(possibleDataServiceVersion);
    } else {
      throw new IllegalArgumentException(version);
    }
  }

  /**
   * Check if <code>firstValue</code> is bigger then  <code>secondValue</code>
   *
   * @param firstValue first value which is compared
   * @param secondValue second value which is compared
   * @return <code>true</code> if firstValue is bigger than secondValue
   */
  public static boolean isBiggerThan(final String firstValue, final String secondValue) {
    if (!validateDataServiceVersion(secondValue) || !validateDataServiceVersion(firstValue)) {
      throw new IllegalArgumentException("Illegal arguments: " + secondValue + " and " + firstValue);
    }

    final double me = Double.parseDouble(extractDataServiceVersionString(firstValue));
    final double other = Double.parseDouble(extractDataServiceVersionString(secondValue));

    return me > other;
  }

  public static boolean isValidODataVersion(String value) {
    final double version4 = Double.parseDouble(extractDataServiceVersionString(ODataServiceVersion.V40.toString()));
    final double version401 = Double.parseDouble(extractDataServiceVersionString(ODataServiceVersion.V401.toString()));
    final double other = Double.parseDouble(extractDataServiceVersionString(value));
    
    return (Double.compare(other, version4) == 0) || (Double.compare(other, version401) == 0);
  }
  
  public static boolean isValidMaxODataVersion(String value) {
    final double version4 = Double.parseDouble(extractDataServiceVersionString(ODataServiceVersion.V40.toString()));
    final double other = Double.parseDouble(extractDataServiceVersionString(value));
    
    return other >= version4;
  }
  
  /**
   * Extract data service version and return it.
   *
   * @param rawDataServiceVersion raw data service version from which the service version gets extracted
   * @return the extracted data service version
   */
  private static String extractDataServiceVersionString(final String rawDataServiceVersion) {
    if (rawDataServiceVersion != null) {
      final String[] pattern = rawDataServiceVersion.split(";");
      return pattern[0];
    }

    return null;
  }

  private final String version;

  private ODataServiceVersion(final String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return version;
  }
}
