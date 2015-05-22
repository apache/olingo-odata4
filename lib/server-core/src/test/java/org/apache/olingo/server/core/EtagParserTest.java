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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.apache.olingo.server.api.EtagInformation;
import org.apache.olingo.server.api.OData;
import org.junit.Test;

public class EtagParserTest {

  private static final OData odata = OData.newInstance();

  @Test
  public void empty() {
    final EtagInformation etagInformation = odata.createEtagInformation(null);
    assertFalse(etagInformation.isAll());
    assertNotNull(etagInformation.getEtags());
    assertTrue(etagInformation.getEtags().isEmpty());
  }

  @Test
  public void loneStar() {
    final EtagInformation etagInformation = odata.createEtagInformation(Collections.singleton("*"));
    assertTrue(etagInformation.isAll());
    assertNotNull(etagInformation.getEtags());
    assertTrue(etagInformation.getEtags().isEmpty());
  }

  @Test
  public void starWins() {
    final EtagInformation etagInformation = odata.createEtagInformation(Arrays.asList("\"ETag\"", "*"));
    assertTrue(etagInformation.isAll());
    assertNotNull(etagInformation.getEtags());
    assertTrue(etagInformation.getEtags().isEmpty());
  }

  @Test
  public void starAsEtagAndEmptyEtag() {
    final EtagInformation etagInformation = odata.createEtagInformation(
        Collections.singleton("\"*\", \"\""));
    assertFalse(etagInformation.isAll());
    assertNotNull(etagInformation.getEtags());
    assertThat(etagInformation.getEtags().size(), equalTo(2));
    assertThat(etagInformation.getEtags(), hasItems("\"*\"", "\"\""));
  }

  @Test
  public void severalEtags() {
    final EtagInformation etagInformation = odata.createEtagInformation(
        Arrays.asList("\"ETag1\"", "\"ETag2\",, , ,W/\"ETag3\", ,"));
    assertFalse(etagInformation.isAll());
    assertNotNull(etagInformation.getEtags());
    assertThat(etagInformation.getEtags().size(), equalTo(3));
    assertThat(etagInformation.getEtags(), hasItems("\"ETag1\"", "\"ETag2\"", "W/\"ETag3\""));
  }

  @Test
  public void duplicateEtagValues() {
    final EtagInformation etagInformation = odata.createEtagInformation(
        Arrays.asList("\"ETag1\"", "\"ETag2\", W/\"ETag1\", \"ETag1\""));
    assertFalse(etagInformation.isAll());
    assertNotNull(etagInformation.getEtags());
    assertThat(etagInformation.getEtags().size(), equalTo(3));
    assertThat(etagInformation.getEtags(), hasItems("\"ETag1\"", "\"ETag2\"", "W/\"ETag1\""));
  }

  @Test
  public void specialCharacters() {
    final EtagInformation etagInformation = odata.createEtagInformation(
        Collections.singleton("\"!#$%&'()*+,-./:;<=>?@[]^_`{|}~¡\u00FF\", \"ETag2\""));
    assertFalse(etagInformation.isAll());
    assertNotNull(etagInformation.getEtags());
    assertThat(etagInformation.getEtags().size(), equalTo(2));
    assertThat(etagInformation.getEtags(), hasItems(
        "\"!#$%&'()*+,-./:;<=>?@[]^_`{|}~¡\u00FF\"", "\"ETag2\""));
  }

  @Test
  public void wrongFormat() {
    final EtagInformation etagInformation = odata.createEtagInformation(
        Arrays.asList("\"ETag1\", ETag2", "w/\"ETag3\"", "W//\"ETag4\"", "W/ETag5",
            "\"\"ETag6\"\"", " \"ETag7\"\"ETag7\" ", "\"ETag8\" \"ETag8\"",
            "\"ETag 9\"", "\"ETag10\""));
    assertFalse(etagInformation.isAll());
    assertNotNull(etagInformation.getEtags());
    assertThat(etagInformation.getEtags().size(), equalTo(2));
    assertThat(etagInformation.getEtags(), hasItems("\"ETag1\"", "\"ETag10\""));
  }

  @Test
  public void match() {
    assertFalse(odata.createEtagInformation(Collections.<String> emptySet()).isMatchedBy("\"ETag\""));
    assertFalse(odata.createEtagInformation(Collections.singleton("\"ETag\"")).isMatchedBy(null));
    assertTrue(odata.createEtagInformation(Collections.singleton("\"ETag\"")).isMatchedBy("\"ETag\""));
    assertTrue(odata.createEtagInformation(Collections.singleton("*")).isMatchedBy("\"ETag\""));
    assertTrue(odata.createEtagInformation(Collections.singleton("\"ETag\"")).isMatchedBy("W/\"ETag\""));
    assertTrue(odata.createEtagInformation(Collections.singleton("W/\"ETag\"")).isMatchedBy("\"ETag\""));
    assertFalse(odata.createEtagInformation(Collections.singleton("\"ETag\"")).isMatchedBy("W/\"ETag2\""));
    assertFalse(odata.createEtagInformation(Collections.singleton("W/\"ETag\"")).isMatchedBy("\"ETag2\""));
    assertTrue(odata.createEtagInformation(Arrays.asList("\"ETag1\",\"ETag2\"", "\"ETag3\",\"ETag4\""))
        .isMatchedBy("\"ETag4\""));
    assertFalse(odata.createEtagInformation(Arrays.asList("\"ETag1\",\"ETag2\"", "\"ETag3\",\"ETag4\""))
        .isMatchedBy("\"ETag5\""));
  }
}
