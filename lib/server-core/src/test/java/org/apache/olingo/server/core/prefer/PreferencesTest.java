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
package org.apache.olingo.server.core.prefer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.olingo.server.api.prefer.Preferences;
import org.apache.olingo.server.api.prefer.Preferences.Preference;
import org.apache.olingo.server.api.prefer.Preferences.Return;
import org.junit.Test;

public class PreferencesTest {

  @Test
  public void empty() {
    final Preferences preferences = new PreferencesImpl(null);
    assertFalse(preferences.hasAllowEntityReferences());
    assertNull(preferences.getCallback());
    assertFalse(preferences.hasContinueOnError());
    assertNull(preferences.getMaxPageSize());
    assertFalse(preferences.hasTrackChanges());
    assertNull(preferences.getReturn());
    assertFalse(preferences.hasRespondAsync());
    assertNull(preferences.getWait());
  }

  @Test
  public void all() {
    final Preferences preferences = new PreferencesImpl(Collections.singleton(
        "odata.allow-entityreferences, odata.callback;url=\"callbackURI\","
            + "odata.continue-on-error, odata.include-annotations=\"*\", odata.maxpagesize=42,"
            + "odata.track-changes, return=representation, respond-async, wait=12345"));
    assertTrue(preferences.hasAllowEntityReferences());
    assertEquals(URI.create("callbackURI"), preferences.getCallback());
    assertNotNull(preferences.getPreference("odata.callback"));
    assertNull(preferences.getPreference("odata.callback").getValue());
    assertEquals("callbackURI", preferences.getPreference("odata.callback").getParameters().get("url"));
    assertTrue(preferences.hasContinueOnError());
    assertEquals("*", preferences.getPreference("odata.Include-Annotations").getValue());
    assertEquals(Integer.valueOf(42), preferences.getMaxPageSize());
    assertEquals("42", preferences.getPreference("odata.MaxPageSize").getValue());
    assertTrue(preferences.hasTrackChanges());
    assertEquals(Return.REPRESENTATION, preferences.getReturn());
    assertTrue(preferences.hasRespondAsync());
    assertEquals(Integer.valueOf(12345), preferences.getWait());
  }

  @Test
  public void caseSensitivity() {
    final Preferences preferences = new PreferencesImpl(Collections.singleton(
        "OData.Callback;URL=\"callbackURI\", return=REPRESENTATION, Wait=42"));
    assertEquals(URI.create("callbackURI"), preferences.getCallback());
    assertNull(preferences.getReturn());
    assertEquals(Integer.valueOf(42), preferences.getWait());
  }

  @Test
  public void multipleValues() {
    final Preferences preferences = new PreferencesImpl(Collections.singleton(
        ",return=minimal, ,, return=representation, wait=1, wait=2, wait=3,"));
    assertEquals(Return.MINIMAL, preferences.getReturn());
    assertEquals(Integer.valueOf(1), preferences.getWait());
  }

  @Test
  public void multipleValuesDifferentHeaders() {
    final Preferences preferences = new PreferencesImpl(Arrays.asList(
        null, "",
        "return=representation, wait=1",
        "return=minimal, wait=2",
        "wait=3"));
    assertEquals(Return.REPRESENTATION, preferences.getReturn());
    assertEquals(Integer.valueOf(1), preferences.getWait());
  }

  @Test
  public void multipleParameters() {
    final Preferences preferences = new PreferencesImpl(Collections.singleton(
        "preference=a;;b=c; d = e; f;; ; g; h=\"i\";, wait=42"));
    final Preference preference = preferences.getPreference("preference");
    assertEquals("a", preference.getValue());
    final Map<String, String> parameters = preference.getParameters();
    assertEquals(5, parameters.size());
    assertEquals("c", parameters.get("b"));
    assertEquals("e", parameters.get("d"));
    assertTrue(parameters.containsKey("f"));
    assertNull(parameters.get("f"));
    assertTrue(parameters.containsKey("g"));
    assertNull(parameters.get("g"));
    assertEquals("i", parameters.get("h"));
    assertEquals(Integer.valueOf(42), preferences.getWait());
  }

  @Test
  public void quotedValue() {
    final Preferences preferences = new PreferencesImpl(Collections.singleton(
        "strangePreference=\"x\\\\y,\\\"abc\\\"z\", wait=42"));
    assertEquals("x\\y,\"abc\"z", preferences.getPreference("strangePreference").getValue());
    assertEquals(Integer.valueOf(42), preferences.getWait());
  }

  @Test
  public void specialCharacters() {
    final Preferences preferences = new PreferencesImpl(Collections.singleton(
        "!#$%&'*+-.^_`|~ = \"!#$%&'()*+,-./:;<=>?@[]^_`{|}~¡\u00FF\", wait=42"));
    assertEquals("!#$%&'()*+,-./:;<=>?@[]^_`{|}~¡\u00FF",
        preferences.getPreference("!#$%&'*+-.^_`|~").getValue());
    assertEquals(Integer.valueOf(42), preferences.getWait());
  }

  @Test
  public void wrongContent() {
    final Preferences preferences = new PreferencesImpl(Arrays.asList(
        "odata.callback;url=\":\"",
        "odata.maxpagesize=12345678901234567890",
        "return=something",
        "wait=-1"));
    assertNull(preferences.getCallback());
    assertEquals(":", preferences.getPreference("odata.callback").getParameters().get("url"));
    assertNull(preferences.getMaxPageSize());
    assertEquals("12345678901234567890", preferences.getPreference("odata.maxpagesize").getValue());
    assertNull(preferences.getReturn());
    assertEquals("something", preferences.getPreference("return").getValue());
    assertNull(preferences.getWait());
    assertEquals("-1", preferences.getPreference("wait").getValue());
  }

  @Test
  public void wrongFormat() {
    final Preferences preferences = new PreferencesImpl(Arrays.asList(
        "return=, wait=1",
        "return=;, wait=2",
        "return=representation=, wait=3",
        "return=\"representation\"respond-async, wait=4",
        "respond-async[], wait=5",
        "odata.callback;=, wait=6",
        "odata.callback;url=, wait=7",
        "odata.callback;[], wait=8",
        "odata.callback;url=\"url\"parameter, wait=9",
        "wait=10"));
    assertEquals(Integer.valueOf(10), preferences.getWait());
  }
}
