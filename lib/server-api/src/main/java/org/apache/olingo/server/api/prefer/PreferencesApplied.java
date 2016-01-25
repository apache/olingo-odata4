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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.olingo.commons.api.format.PreferenceName;
import org.apache.olingo.server.api.prefer.Preferences.Return;

/**
 * Provides methods to set values suitable for the Preference-Applied HTTP response header
 * as described in <a href="https://www.ietf.org/rfc/rfc7240.txt">RFC 7240</a>.
 * There are named methods for preferences defined in the OData standard.
 */
public final class PreferencesApplied {

  private static final Set<String> SAFE_PREFERENCE_NAMES = new HashSet<String>();
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
    for (final Map.Entry<String, String> entry : applied.entrySet()) {
      if (result.length() > 0) {
        result.append(',').append(' ');
      }
      final String key = entry.getKey();
      result.append(key);
      if (entry.getValue() != null) {
        final boolean safe = isSafe(key);
        result.append('=')
        .append(safe ? "" : '"')
        .append(entry.getValue().replaceAll("\\\\|\"", "\\\\$0"))
        .append(safe ? "" : '"');
      }
    }
    return result.toString();
  }

  private boolean isSafe(final String key) {
    if (SAFE_PREFERENCE_NAMES.isEmpty()) {
      SAFE_PREFERENCE_NAMES.add(PreferenceName.ALLOW_ENTITY_REFERENCES.getName());
      SAFE_PREFERENCE_NAMES.add(PreferenceName.CALLBACK.getName());
      SAFE_PREFERENCE_NAMES.add(PreferenceName.CONTINUE_ON_ERROR.getName());
      SAFE_PREFERENCE_NAMES.add(PreferenceName.MAX_PAGE_SIZE.getName());
      SAFE_PREFERENCE_NAMES.add(PreferenceName.TRACK_CHANGES.getName());
      SAFE_PREFERENCE_NAMES.add(PreferenceName.RETURN.getName());
      SAFE_PREFERENCE_NAMES.add(PreferenceName.RESPOND_ASYNC.getName());
      SAFE_PREFERENCE_NAMES.add(PreferenceName.WAIT.getName());
    }
    return SAFE_PREFERENCE_NAMES.contains(key);
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
      add(PreferenceName.ALLOW_ENTITY_REFERENCES.getName(), null);
      return this;
    }

    /** Sets <code>odata.callback</code>. */
    public Builder callback() {
      add(PreferenceName.CALLBACK.getName(), null);
      return this;
    }

    /** Sets <code>odata.continue-on-error</code>. */
    public Builder continueOnError() {
      add(PreferenceName.CONTINUE_ON_ERROR.getName(), null);
      return this;
    }

    /** Sets the value of the applied preference <code>odata.maxpagesize</code>. */
    public Builder maxPageSize(final Integer maxPageSize) {
      add(PreferenceName.MAX_PAGE_SIZE.getName(), Integer.toString(maxPageSize));
      return this;
    }

    /** Sets <code>odata.track-changes</code>. */
    public Builder trackChanges() {
      add(PreferenceName.TRACK_CHANGES.getName(), null);
      return this;
    }

    /** Sets the value of the applied preference <code>return</code>. */
    public Builder returnRepresentation(final Return returnRepresentation) {
      add(PreferenceName.RETURN.getName(), returnRepresentation.name().toLowerCase(Locale.ROOT));
      return this;
    }

    /** Sets <code>odata.respond-async</code>. */
    public Builder respondAsync() {
      add(PreferenceName.RESPOND_ASYNC.getName(), null);
      return this;
    }

    /** Sets the value of the applied preference <code>wait</code>. */
    public Builder waitPreference(final Integer wait) {
      add(PreferenceName.WAIT.getName(), Integer.toString(wait));
      return this;
    }

    /**
     * Sets an arbitrary preference as applied.
     * The preference name is converted to lowercase.
     * The value of this preference may be <code>null</code>.
     * Name and value are not checked for validity.
     * @param name preference name
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
