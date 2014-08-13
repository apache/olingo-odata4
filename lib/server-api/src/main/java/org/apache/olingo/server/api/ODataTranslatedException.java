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

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.MissingFormatArgumentException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.olingo.commons.api.ODataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ODataTranslatedException extends ODataException {

  private static final long serialVersionUID = -1210541002198287561L;
  private static final Logger log = LoggerFactory.getLogger(ODataTranslatedException.class);
  private static final String BUNDLE_NAME = "i18n";

  public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

  protected static interface MessageKey {}

  private MessageKey messageKey;
  private Object[] parameters;

  protected ODataTranslatedException(String developmentMessage, MessageKey messageKey, String... parameters) {
    super(developmentMessage);
    this.messageKey = messageKey;
    this.parameters = parameters;
  }

  protected ODataTranslatedException(String developmentMessage, Throwable cause, MessageKey messageKey,
      String... parameters) {
    super(developmentMessage, cause);
    this.messageKey = messageKey;
    this.parameters = parameters;
  }

  @Override
  public String getLocalizedMessage() {
    return getTranslatedMessage(DEFAULT_LOCALE).getMessage();
  }

  @Override
  public String toString() {
    return getMessage();
  }

  public MessageKey getMessageKey() {
    return messageKey;
  }

  public ODataErrorMessage getTranslatedMessage(final Locale locale) {
    if (messageKey == null) {
      return new ODataErrorMessage(getMessage(), DEFAULT_LOCALE);
    }
    ResourceBundle bundle = createResourceBundle(locale);
    if (bundle == null) {
      return new ODataErrorMessage(getMessage(), DEFAULT_LOCALE);
    }

    return buildMessage(bundle, locale);
  }

  private ResourceBundle createResourceBundle(final Locale locale) {
    try {
      return ResourceBundle.getBundle(BUNDLE_NAME, locale == null ? DEFAULT_LOCALE : locale);
    } catch (final MissingResourceException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private ODataErrorMessage buildMessage(ResourceBundle bundle, Locale locale) {
    String message = null;

    try {
      message = bundle.getString(getClass().getSimpleName() + '.' + messageKey);
      StringBuilder builder = new StringBuilder();
      Formatter f = new Formatter(builder, locale);
      f.format(message, parameters);
      f.close();
      Locale usedLocale = bundle.getLocale();
      if (Locale.ROOT.equals(usedLocale)) {
        usedLocale = DEFAULT_LOCALE;
      }
      return new ODataErrorMessage(builder.toString(), usedLocale);
    } catch (MissingResourceException e) {
      return new ODataErrorMessage("Missing message for key '" + messageKey + "'!", DEFAULT_LOCALE);
    } catch (MissingFormatArgumentException e) {
      return new ODataErrorMessage("Missing replacement for place holder in message '" + message +
          "' for following arguments '" + Arrays.toString(parameters) + "'!", DEFAULT_LOCALE);
    }
  }

  public class ODataErrorMessage {
    String message;
    Locale locale;

    public ODataErrorMessage(String message, Locale usedLocale) {
      this.message = message;
      this.locale = usedLocale;
    }

    public String getMessage() {
      return message;
    }

    public Locale getLocale() {
      return locale;
    }
  }
}
