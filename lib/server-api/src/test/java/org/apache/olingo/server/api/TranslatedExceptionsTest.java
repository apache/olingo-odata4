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
package org.apache.olingo.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.apache.olingo.server.api.ODataTranslatedException.ODataErrorMessage;
import org.junit.Test;

public class TranslatedExceptionsTest {

  private static final String DEV = "devMessage";
  private static enum Keys implements ODataTranslatedException.MessageKey {
    BASIC, ONEPARAM, TWOPARAM, NOMESSAGE
  }

  public TranslatedExceptionsTest() {
    // for test reason we assume a system with a default Locale.ENGLISH
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  public void basic() {
    ODataTranslatedException exp = new ODataTranslatedException(DEV, Keys.BASIC);
    assertEquals(DEV, exp.getMessage());

    ODataErrorMessage translatedMessage = exp.getTranslatedMessage(null);
    assertNotNull(translatedMessage);
    assertEquals("Test Default", translatedMessage.getMessage());

    translatedMessage = exp.getTranslatedMessage(Locale.ENGLISH);
    assertNotNull(translatedMessage);
    assertEquals("Test Default", translatedMessage.getMessage());
    assertEquals(Locale.ENGLISH, translatedMessage.getLocale());

    translatedMessage = exp.getTranslatedMessage(Locale.UK);
    assertNotNull(translatedMessage);
    assertEquals("Test Default", translatedMessage.getMessage());

    translatedMessage = exp.getTranslatedMessage(Locale.GERMAN);
    assertNotNull(translatedMessage);
    assertEquals("Test DE", translatedMessage.getMessage());
    assertEquals(Locale.GERMAN, translatedMessage.getLocale());

    translatedMessage = exp.getTranslatedMessage(Locale.GERMANY);
    assertNotNull(translatedMessage);
    assertEquals("Test DE", translatedMessage.getMessage());
    assertEquals(Locale.GERMAN, translatedMessage.getLocale());
  }

  @Test
  public void unusedParametersMustNotResultInAnException() {
    ODataTranslatedException exp = new ODataTranslatedException(DEV, Keys.BASIC, "unusedParam1", "unusedParam2");
    assertEquals(DEV, exp.getMessage());

    ODataErrorMessage translatedMessage = exp.getTranslatedMessage(null);
    assertNotNull(translatedMessage);
    assertEquals("Test Default", translatedMessage.getMessage());
  }

  @Test
  public void useOneParameter() {
    ODataTranslatedException exp = new ODataTranslatedException(DEV, Keys.ONEPARAM, "usedParam1");
    assertEquals(DEV, exp.getMessage());

    ODataErrorMessage translatedMessage = exp.getTranslatedMessage(null);
    assertNotNull(translatedMessage);
    assertEquals("Param1: usedParam1", translatedMessage.getMessage());
  }

  @Test
  public void useOneParameterExpectedButMultipleGiven() {
    ODataTranslatedException exp = new ODataTranslatedException(DEV, Keys.ONEPARAM, "usedParam1", "unusedParam2");
    assertEquals(DEV, exp.getMessage());

    ODataErrorMessage translatedMessage = exp.getTranslatedMessage(null);
    assertNotNull(translatedMessage);
    assertEquals("Param1: usedParam1", translatedMessage.getMessage());
  }

  @Test
  public void useTwoParameter() {
    ODataTranslatedException exp = new ODataTranslatedException(DEV, Keys.TWOPARAM, "usedParam1", "usedParam2");
    assertEquals(DEV, exp.getMessage());

    ODataErrorMessage translatedMessage = exp.getTranslatedMessage(null);
    assertNotNull(translatedMessage);
    assertEquals("Param1: usedParam1 Param2: usedParam2", translatedMessage.getMessage());
  }

  @Test
  public void parametersNotGivenAltoughNeeded() {
    ODataTranslatedException exp = new ODataTranslatedException(DEV, Keys.ONEPARAM);
    assertEquals(DEV, exp.getMessage());

    ODataErrorMessage translatedMessage = exp.getTranslatedMessage(null);
    assertNotNull(translatedMessage);
    assertTrue(translatedMessage.getMessage().contains("Missing replacement for place holder in message"));
  }

  @Test
  public void noMessageForKey() {
    ODataTranslatedException exp = new ODataTranslatedException(DEV, Keys.NOMESSAGE);
    assertEquals(DEV, exp.getMessage());

    ODataErrorMessage translatedMessage = exp.getTranslatedMessage(null);
    assertNotNull(translatedMessage);
    assertTrue(translatedMessage.getMessage().contains("Missing message for key"));
  }

  @Test
  public void keyForRootBundleButNotPresentInDerivedBundle() {
    ODataTranslatedException exp =
        new ODataTranslatedException(DEV, ODataTranslatedException.MessageKeys.HTTP_METHOD_NOT_IMPLEMENTED, "param1");
    assertEquals(DEV, exp.getMessage());

    ODataErrorMessage translatedMessage = exp.getTranslatedMessage(Locale.GERMAN);
    assertNotNull(translatedMessage);
    assertEquals("Invalid HTTP method given: 'param1'.", translatedMessage.getMessage());
  }
}
