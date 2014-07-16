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

import org.apache.olingo.commons.api.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a container for the supported ODataServiceVersions.
 */
public enum ODataServiceVersion {

  V10("1.0"),
  V20("2.0"),
  V30("3.0"),
  V40("4.0");

  public enum NamespaceKey {
    DATASERVICES, METADATA, SCHEME,
    NAVIGATION_LINK_REL, ASSOCIATION_LINK_REL, MEDIA_EDIT_LINK_REL, DELTA_LINK_REL
  }

  private static final Map<NamespaceKey, String> V30_NAMESPACES = new HashMap<NamespaceKey, String>() {
    private static final long serialVersionUID = 3109256773218160485L;
    private static final String BASE = "http://schemas.microsoft.com/ado/2007/08/dataservices";
    {
      put(NamespaceKey.DATASERVICES, BASE);
      put(NamespaceKey.METADATA, BASE + "/metadata");
      put(NamespaceKey.SCHEME, BASE + "/scheme");
      put(NamespaceKey.NAVIGATION_LINK_REL, BASE + "/related/");
      put(NamespaceKey.ASSOCIATION_LINK_REL, BASE + "/relatedlinks/");
      put(NamespaceKey.MEDIA_EDIT_LINK_REL, BASE + "/edit-media/");
    }
  };

  private static final Map<NamespaceKey, String> V40_NAMESPACES = new HashMap<NamespaceKey, String>() {
    private static final long serialVersionUID = 3109256773218160485L;
    private static final String BASE = "http://docs.oasis-open.org/odata/ns/";
    {
      put(NamespaceKey.DATASERVICES, BASE + "data");
      put(NamespaceKey.METADATA, BASE + "metadata");
      put(NamespaceKey.SCHEME, BASE + "scheme");
      put(NamespaceKey.NAVIGATION_LINK_REL, BASE + "related/");
      put(NamespaceKey.ASSOCIATION_LINK_REL, BASE + "relatedlinks/");
      put(NamespaceKey.MEDIA_EDIT_LINK_REL, BASE + "edit-media/");
      put(NamespaceKey.DELTA_LINK_REL, BASE + "delta");
    }
  };

  public enum JsonKey {
    TYPE, ID, ETAG,
    READ_LINK, EDIT_LINK, MEDIA_READ_LINK, MEDIA_EDIT_LINK, MEDIA_CONTENT_TYPE, MEDIA_ETAG,
    ASSOCIATION_LINK, NAVIGATION_LINK,
    COUNT, NEXT_LINK, DELTA_LINK, ERROR
  }

  private static final Map<JsonKey, String> V30_JSON = new HashMap<JsonKey, String>() {
    private static final long serialVersionUID = 3109256773218160485L;
    {
      put(JsonKey.TYPE, "odata.type");
      put(JsonKey.ID, "odata.id");
      put(JsonKey.ETAG, "odata.etag");
      put(JsonKey.READ_LINK, "odata.readLink");
      put(JsonKey.EDIT_LINK, "odata.editLink");
      put(JsonKey.MEDIA_READ_LINK, "odata.mediaReadLink");
      put(JsonKey.MEDIA_EDIT_LINK, "odata.mediaEditLink");
      put(JsonKey.MEDIA_CONTENT_TYPE, "odata.mediaContentType");
      put(JsonKey.MEDIA_ETAG, "odata.mediaEtag");
      put(JsonKey.ASSOCIATION_LINK, "@odata.associationLinkUrl");
      put(JsonKey.NAVIGATION_LINK, "@odata.navigationLinkUrl");
      put(JsonKey.COUNT, "odata.count");
      put(JsonKey.NEXT_LINK, "odata.nextLink");
      put(JsonKey.ERROR, "odata.error");
    }
  };

  private static final Map<JsonKey, String> V40_JSON = new HashMap<JsonKey, String>() {
    private static final long serialVersionUID = 3109256773218160485L;
    {
      put(JsonKey.TYPE, Constants.JSON_TYPE);
      put(JsonKey.ID, Constants.JSON_ID);
      put(JsonKey.ETAG, Constants.JSON_ETAG);
      put(JsonKey.READ_LINK, Constants.JSON_READ_LINK);
      put(JsonKey.EDIT_LINK, Constants.JSON_EDIT_LINK);
      put(JsonKey.MEDIA_READ_LINK, Constants.JSON_MEDIA_READ_LINK);
      put(JsonKey.MEDIA_EDIT_LINK, Constants.JSON_MEDIA_EDIT_LINK);
      put(JsonKey.MEDIA_CONTENT_TYPE, Constants.JSON_MEDIA_CONTENT_TYPE);
      put(JsonKey.MEDIA_ETAG, Constants.JSON_MEDIA_ETAG);
      put(JsonKey.ASSOCIATION_LINK, Constants.JSON_ASSOCIATION_LINK);
      put(JsonKey.NAVIGATION_LINK, Constants.JSON_NAVIGATION_LINK);
      put(JsonKey.COUNT, Constants.JSON_COUNT);
      put(JsonKey.NEXT_LINK, Constants.JSON_NEXT_LINK);
      put(JsonKey.DELTA_LINK, Constants.JSON_DELTA_LINK);
      put(JsonKey.ERROR, Constants.JSON_ERROR);
    }
  };

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
          || V40.toString().equals(possibleDataServiceVersion);
    } else {
      throw new IllegalArgumentException(version);
    }
  }

  /**
   * actual > comparedTo
   * 
   * @param actual
   * @param comparedTo
   * @return <code>true</code> if actual is bigger than comparedTo
   */
  public static boolean isBiggerThan(final String actual, final String comparedTo) {
    if (!validateDataServiceVersion(comparedTo) || !validateDataServiceVersion(actual)) {
      throw new IllegalArgumentException("Illegal arguments: " + comparedTo + " and " + actual);
    }

    final double me = Double.parseDouble(extractDataServiceVersionString(actual));
    final double other = Double.parseDouble(extractDataServiceVersionString(comparedTo));

    return me > other;
  }

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

  public String getNamespace(final NamespaceKey key) {
    return this == V10 || this == V20 ? null : this == V30 ? V30_NAMESPACES.get(key) : V40_NAMESPACES.get(key);
  }

  public String getJsonName(final JsonKey key) {
    return this == V10 || this == V20 ? null : this == V30 ? V30_JSON.get(key) : V40_JSON.get(key);
  }

  @Override
  public String toString() {
    return version;
  }
}
