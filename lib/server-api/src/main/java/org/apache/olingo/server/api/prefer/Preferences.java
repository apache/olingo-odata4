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

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * Provides access methods to the preferences set in Prefer HTTP request headers
 * as described in <a href="https://www.ietf.org/rfc/rfc7240.txt">RFC 7240</a>.
 * Preferences defined in the OData standard can be accessed with named methods.
 */
public interface Preferences {

  /**
   * Gets named preference. Names are case insensitive.
   * @param name name of the preference
   * @return {@link Preference} or <code>null</code> if no such preference has been set
   */
  public Preference getPreference(String name);

  /** Whether the preference <code>odata.allow-entityreferences</code> has been set. */
  public boolean hasAllowEntityReferences();

  /**
   * Gets the value of the <code>url</code> parameter of the preference
   * <code>odata.callback</code> or <code>null</code> if not set or the URI is not valid.
   * @return the callback URI
   */
  public URI getCallback();

  /** Whether the preference <code>odata.continue-on-error</code> has been set. */
  public boolean hasContinueOnError();

  /**
   * Gets the value of the preference <code>odata.maxpagesize</code>
   * or <code>null</code> if not set or an invalid value has been set.
   * @return the page size for server-driven paging
   */
  public Integer getMaxPageSize();

  /** Whether the preference <code>odata.track-changes</code> has been set. */
  public boolean hasTrackChanges();

  public enum Return {
    REPRESENTATION, MINIMAL
  }

  /**
   * Gets the value of the preference <code>return</code> or <code>null</code> if not set
   * or the value is not valid.
   */
  public Return getReturn();

  /** Whether the preference <code>respond-async</code> has been set. */
  public boolean hasRespondAsync();

  /**
   * Gets the value of the preference <code>wait</code> or <code>null</code> if not set
   * or the value is not valid.
   * @return the number of seconds the client is prepared to wait for the service
   * to process the request synchronously
   */
  public Integer getWait();

  /** Generic preference object. */
  public class Preference {
    private final String value;
    private final Map<String, String> parameters;

    public Preference(final String value, final Map<String, String> parameters) {
      this.value = value;
      this.parameters = parameters;
    }

    /**
     * Gets the value of the preference. It may be <code>null</code> if the preference has
     * no value; this is not the same as the preference not set.
     */
    public String getValue() {
      return value;
    }

    /**
     * Gets the parameters of the preference. The value of a parameter may be
     * <code>null</code> if the parameter has no value; this is not the same as
     * the parameter not set. Parameter names are all lowercase.
     * @return a map from parameter names to parameter values
     */
    public Map<String, String> getParameters() {
      return parameters == null ?
          Collections.<String, String> emptyMap() :
            Collections.unmodifiableMap(parameters);
    }
  }
}
