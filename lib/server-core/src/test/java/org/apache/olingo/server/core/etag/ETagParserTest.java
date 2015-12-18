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
package org.apache.olingo.server.core.etag;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class ETagParserTest {

  private static final ETagHelperImpl eTagHelper = new ETagHelperImpl();

  @Test
  public void empty() {
    final ETagInformation eTagInformation = eTagHelper.createETagInformation(null);
    assertFalse(eTagInformation.isAll());
    assertNotNull(eTagInformation.getETags());
    assertTrue(eTagInformation.getETags().isEmpty());
  }

  @Test
  public void loneStar() {
    final ETagInformation eTagInformation = eTagHelper.createETagInformation(Collections.singleton("*"));
    assertTrue(eTagInformation.isAll());
    assertNotNull(eTagInformation.getETags());
    assertTrue(eTagInformation.getETags().isEmpty());
  }

  @Test
  public void starWins() {
    final ETagInformation eTagInformation = eTagHelper.createETagInformation(Arrays.asList("\"ETag\"", "*"));
    assertTrue(eTagInformation.isAll());
    assertNotNull(eTagInformation.getETags());
    assertTrue(eTagInformation.getETags().isEmpty());
  }

  @Test
  public void starAsEtagAndEmptyEtag() {
    final ETagInformation eTagInformation = eTagHelper.createETagInformation(
        Collections.singleton("\"*\", \"\""));
    assertFalse(eTagInformation.isAll());
    assertNotNull(eTagInformation.getETags());
    assertThat(eTagInformation.getETags().size(), equalTo(2));
    assertThat(eTagInformation.getETags(), hasItems("\"*\"", "\"\""));
  }

  @Test
  public void severalEtags() {
    final ETagInformation eTagInformation = eTagHelper.createETagInformation(
        Arrays.asList("\"ETag1\"", "\"ETag2\",, , ,W/\"ETag3\", ,"));
    assertFalse(eTagInformation.isAll());
    assertNotNull(eTagInformation.getETags());
    assertThat(eTagInformation.getETags().size(), equalTo(3));
    assertThat(eTagInformation.getETags(), hasItems("\"ETag1\"", "\"ETag2\"", "W/\"ETag3\""));
  }

  @Test
  public void duplicateEtagValues() {
    final ETagInformation eTagInformation = eTagHelper.createETagInformation(
        Arrays.asList("\"ETag1\"", "\"ETag2\", W/\"ETag1\", \"ETag1\""));
    assertFalse(eTagInformation.isAll());
    assertNotNull(eTagInformation.getETags());
    assertThat(eTagInformation.getETags().size(), equalTo(3));
    assertThat(eTagInformation.getETags(), hasItems("\"ETag1\"", "\"ETag2\"", "W/\"ETag1\""));
  }

  @Test
  public void specialCharacters() {
    final ETagInformation eTagInformation = eTagHelper.createETagInformation(
        Collections.singleton("\"!#$%&'()*+,-./:;<=>?@[]^_`{|}~¡\u00FF\", \"ETag2\""));
    assertFalse(eTagInformation.isAll());
    assertNotNull(eTagInformation.getETags());
    assertThat(eTagInformation.getETags().size(), equalTo(2));
    assertThat(eTagInformation.getETags(), hasItems(
        "\"!#$%&'()*+,-./:;<=>?@[]^_`{|}~¡\u00FF\"", "\"ETag2\""));
  }

  @Test
  public void wrongFormat() {
    final ETagInformation eTagInformation = eTagHelper.createETagInformation(
        Arrays.asList("\"ETag1\", ETag2", "w/\"ETag3\"", "W//\"ETag4\"", "W/ETag5",
            "\"\"ETag6\"\"", " \"ETag7\"\"ETag7\" ", "\"ETag8\" \"ETag8\"",
            "\"ETag 9\"", "\"ETag10\""));
    assertFalse(eTagInformation.isAll());
    assertNotNull(eTagInformation.getETags());
    assertThat(eTagInformation.getETags().size(), equalTo(2));
    assertThat(eTagInformation.getETags(), hasItems("\"ETag1\"", "\"ETag10\""));
  }

  @Test
  public void match() {
    assertFalse(eTagHelper.createETagInformation(Collections.<String> emptySet()).isMatchedBy("\"ETag\""));
    assertFalse(eTagHelper.createETagInformation(Collections.singleton("\"ETag\"")).isMatchedBy(null));
    assertTrue(eTagHelper.createETagInformation(Collections.singleton("\"ETag\"")).isMatchedBy("\"ETag\""));
    assertTrue(eTagHelper.createETagInformation(Collections.singleton("*")).isMatchedBy("\"ETag\""));
    assertTrue(eTagHelper.createETagInformation(Collections.singleton("\"ETag\"")).isMatchedBy("W/\"ETag\""));
    assertTrue(eTagHelper.createETagInformation(Collections.singleton("W/\"ETag\"")).isMatchedBy("\"ETag\""));
    assertFalse(eTagHelper.createETagInformation(Collections.singleton("\"ETag\"")).isMatchedBy("W/\"ETag2\""));
    assertFalse(eTagHelper.createETagInformation(Collections.singleton("W/\"ETag\"")).isMatchedBy("\"ETag2\""));
    assertTrue(eTagHelper.createETagInformation(Arrays.asList("\"ETag1\",\"ETag2\"", "\"ETag3\",\"ETag4\""))
        .isMatchedBy("\"ETag4\""));
    assertFalse(eTagHelper.createETagInformation(Arrays.asList("\"ETag1\",\"ETag2\"", "\"ETag3\",\"ETag4\""))
        .isMatchedBy("\"ETag5\""));
  }
}
