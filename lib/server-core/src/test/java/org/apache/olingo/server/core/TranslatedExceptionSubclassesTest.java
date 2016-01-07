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
package org.apache.olingo.server.core;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Generic test for all exceptions which inherit from ODataTranslatedException
 * if their MessageKeys are available in the resource bundle and the parameters are replaced.
 */
public class TranslatedExceptionSubclassesTest {

  private final Properties properties;

  public TranslatedExceptionSubclassesTest() throws IOException {
    properties = new Properties();
    properties.load(Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("server-core-exceptions-i18n.properties"));
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  public void messageKeysValid() throws Exception {
    testException(ODataHandlerException.class, ODataHandlerException.MessageKeys.values());
    testException(UriParserSemanticException.class, UriParserSemanticException.MessageKeys.values());
    testException(UriParserSyntaxException.class, UriParserSyntaxException.MessageKeys.values());
    testException(ContentNegotiatorException.class, ContentNegotiatorException.MessageKeys.values());
    testException(SerializerException.class, SerializerException.MessageKeys.values());
    testException(UriValidationException.class, UriValidationException.MessageKeys.values());
    testException(UriParserSyntaxException.class, UriParserSyntaxException.MessageKeys.values());
  }

  private void testException(final Class<? extends ODataLibraryException> clazz,
      final ODataLibraryException.MessageKey[] messageKeys) throws Exception {

    for (ODataLibraryException.MessageKey messageKey : messageKeys) {
      String propKey = clazz.getSimpleName() + "." + messageKey.toString();
      String value = properties.getProperty(propKey);
      Assert.assertNotNull("No value found for message key '" + propKey + "'", value);
      //
      int paraCount = countParameters(value);
      Constructor<? extends ODataLibraryException> ctor =
          clazz.getConstructor(String.class, ODataLibraryException.MessageKey.class, String[].class);
      String[] paras = new String[paraCount];
      for (int i = 0; i < paras.length; i++) {
        paras[i] = "470" + i;
      }
      String developerMessage = UUID.randomUUID().toString();
      ODataLibraryException e = ctor.newInstance(developerMessage, messageKey, paras);
      try {
        throw e;
      } catch (ODataLibraryException translatedException) {
        Formatter formatter = new Formatter();
        String formattedValue = formatter.format(value, (Object[]) paras).toString();
        formatter.close();
        Assert.assertEquals(formattedValue, translatedException.getTranslatedMessage(null).getMessage());
        Assert.assertEquals(formattedValue, translatedException.getLocalizedMessage());
        Assert.assertEquals(developerMessage, translatedException.getMessage());
      }
    }
  }

  private int countParameters(final String value) {
    char[] chars = value.toCharArray();
    int count = 0;
    for (char aChar : chars) {
      if (aChar == '%') {
        count++;
      }
    }
    return count;
  }
}
