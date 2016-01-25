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

import static org.junit.Assert.assertEquals;

import org.apache.olingo.server.api.prefer.Preferences.Return;
import org.junit.Test;

public class PreferencesAppliedTest {

  @Test
  public void empty() {
    assertEquals("", PreferencesApplied.with().build().toValueString());
  }

  @Test
  public void all() {
    assertEquals("odata.allow-entityreferences, odata.callback,"
        + " odata.continue-on-error, odata.include-annotations=\"*\", odata.maxpagesize=42,"
        + " odata.track-changes, return=representation, respond-async, wait=12345",
        PreferencesApplied.with().allowEntityReferences().callback().continueOnError()
        .preference("odata.include-annotations", "*").maxPageSize(42).trackChanges()
        .returnRepresentation(Return.REPRESENTATION).respondAsync().waitPreference(12345)
        .build().toValueString());
  }

  @Test
  public void caseSensitivity() {
    assertEquals("odata.include-annotations=\"*\", odata.maxpagesize=255",
        PreferencesApplied.with()
        .preference("OData.Include-Annotations", "*").maxPageSize(0xFF)
        .build().toValueString());
  }

  @Test
  public void multipleValues() {
    assertEquals("return=minimal, wait=1",
        PreferencesApplied.with()
        .returnRepresentation(Return.MINIMAL).returnRepresentation(Return.REPRESENTATION)
        .preference(null, null).preference(null, "nullValue")
        .waitPreference(1).waitPreference(2).waitPreference(3)
        .build().toValueString());
  }

  @Test
  public void quotedValue() {
    assertEquals("strangepreference=\"x\\\\y,\\\"abc\\\"z\"",
        PreferencesApplied.with().preference("strangePreference", "x\\y,\"abc\"z").build().toValueString());
  }
}
