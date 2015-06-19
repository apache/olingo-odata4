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
package org.apache.olingo.server.api.prefer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.ODataPreferenceNames;
import org.apache.olingo.server.api.prefer.Preferences.Return;

/**
 * Provides methods to set values suitable for the Preference-Applied HTTP response header
 * as described in <a href="https://www.ietf.org/rfc/rfc7240.txt">RFC 7240</a>.
 * There are named methods for preferences defined in the OData standard.
 */
public class PreferencesApplied {

  private Map<String, String> applied;

  private PreferencesApplied() {
    applied = new LinkedHashMap<String, String>();
  }

  /**
   * Gets the applied preferences.
   * @return a map from preference names to preference values
   */
  public Map<String, String> getAppliedPreferences() {
    return Collections.unmodifiableMap(applied);
  }

  /** Returns a string representation that can be used as value of a Preference-Applied HTTP response header. */
  public String toValueString() {
    StringBuilder result = new StringBuilder();
    for (final String name : applied.keySet()) {
      if (result.length() > 0) {
        result.append(',').append(' ');
      }
      result.append(name);
      if (applied.get(name) != null) {
        final boolean safe = ODataPreferenceNames.ALLOW_ENTITY_REFERENCES.toString().equals(name)
            || ODataPreferenceNames.CALLBACK.toString().equals(name)
            || ODataPreferenceNames.CONTINUE_ON_ERROR.toString().equals(name)
            || ODataPreferenceNames.MAX_PAGE_SIZE.toString().equals(name)
            || ODataPreferenceNames.TRACK_CHANGES.toString().equals(name)
            || ODataPreferenceNames.RETURN.toString().equals(name)
            || ODataPreferenceNames.RESPOND_ASYNC.toString().equals(name)
            || ODataPreferenceNames.WAIT.toString().equals(name);
        result.append('=')
            .append(safe ? "" : '"')
            .append(applied.get(name).replaceAll("\\\\|\"", "\\\\$0"))
            .append(safe ? "" : '"');
      }
    }
    return result.toString();
  }

  @Override
  public String toString() {
    return toValueString();
  }

  /** Initializes the builder. */
  public static Builder with() {
    return new Builder();
  }

  /** Builder of OData serializer options. */
  public static final class Builder {

    private final PreferencesApplied preferencesApplied;

    private Builder() {
      preferencesApplied = new PreferencesApplied();
    }

    /** Sets <code>odata.allow-entityreferences</code>. */
    public Builder allowEntityReferences() {
      add(ODataPreferenceNames.ALLOW_ENTITY_REFERENCES.toString(), null);
      return this;
    }

    /** Sets <code>odata.callback</code>. */
    public Builder callback() {
      add(ODataPreferenceNames.CALLBACK.toString(), null);
      return this;
    }

    /** Sets <code>odata.continue-on-error</code>. */
    public Builder continueOnError() {
      add(ODataPreferenceNames.CONTINUE_ON_ERROR.toString(), null);
      return this;
    }

    /** Sets the value of the applied preference <code>odata.maxpagesize</code>. */
    public Builder maxPageSize(final Integer maxPageSize) {
      add(ODataPreferenceNames.MAX_PAGE_SIZE.toString(), Integer.toString(maxPageSize));
      return this;
    }

    /** Sets <code>odata.track-changes</code>. */
    public Builder trackChanges() {
      add(ODataPreferenceNames.TRACK_CHANGES.toString(), null);
      return this;
    }

    /** Sets the value of the applied preference <code>return</code>. */
    public Builder returnRepresentation(final Return returnRepresentation) {
      add(ODataPreferenceNames.RETURN.toString(), returnRepresentation.name().toLowerCase(Locale.ROOT));
      return this;
    }

    /** Sets <code>odata.respond-async</code>. */
    public Builder respondAsync() {
      add(ODataPreferenceNames.RESPOND_ASYNC.toString(), null);
      return this;
    }

    /** Sets the value of the applied preference <code>wait</code>. */
    public Builder waitPreference(final Integer wait) {
      add(ODataPreferenceNames.WAIT.toString(), Integer.toString(wait));
      return this;
    }

    /**
     * Sets an arbitrary preference as applied.
     * The preference name is converted to lowercase.
     * The value of this preference may be <code>null</code>.
     * Name and value are not checked for validity.
     * @param name  preference name
     * @param value preference value
     */
    public Builder preference(final String name, final String value) {
      if (name != null) {
        add(name.toLowerCase(Locale.ROOT), value);
      }
      return this;
    }

    /** Builds the applied preferences. */
    public PreferencesApplied build() {
      return preferencesApplied;
    }

    private void add(final String name, final String value) {
      if (!preferencesApplied.applied.containsKey(name)) {
        preferencesApplied.applied.put(name, value);
      }
    }
  }
}
