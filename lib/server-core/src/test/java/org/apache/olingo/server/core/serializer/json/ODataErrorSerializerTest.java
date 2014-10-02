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
package org.apache.olingo.server.core.serializer.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.domain.ODataErrorDetail;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class ODataErrorSerializerTest {

  ODataSerializer ser;

  @Before
  public void before() throws Exception {
    ser = OData.newInstance().createSerializer(ODataFormat.JSON);
  }

  @Test
  public void basicODataErrorNoCode() throws Exception {
    ODataServerError error = new ODataServerError();
    error.setMessage("ErrorMessage");
    InputStream stream = ser.error(error);
    String jsonString = IOUtils.toString(stream);
    assertEquals("{\"error\":{\"code\":null,\"message\":\"ErrorMessage\"}}", jsonString);
  }

  @Test
  public void basicODataErrorWithCode() throws Exception {
    ODataServerError error = new ODataServerError();
    error.setCode("Code").setMessage("ErrorMessage");
    InputStream stream = ser.error(error);
    String jsonString = IOUtils.toString(stream);
    assertEquals("{\"error\":{\"code\":\"Code\",\"message\":\"ErrorMessage\"}}", jsonString);
  }

  @Test
  public void basicODataErrorWithCodeAndTarget() throws Exception {
    ODataServerError error = new ODataServerError();
    error.setCode("Code").setMessage("ErrorMessage").setTarget("Target");
    InputStream stream = ser.error(error);
    String jsonString = IOUtils.toString(stream);
    assertEquals("{\"error\":{\"code\":\"Code\",\"message\":\"ErrorMessage\",\"target\":\"Target\"}}", jsonString);
  }

  @Test(expected = SerializerException.class)
  public void nullErrorResultsInException() throws Exception {
    ser.error(null);
  }

  @Test
  public void emptyDetailsList() throws Exception {
    ODataServerError error = new ODataServerError();
    error.setMessage("ErrorMessage").setDetails(new ArrayList<ODataErrorDetail>());
    InputStream stream = ser.error(error);
    String jsonString = IOUtils.toString(stream);
    assertEquals("{\"error\":{\"code\":null,\"message\":\"ErrorMessage\",\"details\":[]}}", jsonString);
  }

  @Test
  public void nothingSetAtODataErrorObject() throws Exception {
    ODataServerError error = new ODataServerError();
    InputStream stream = ser.error(error);
    String jsonString = IOUtils.toString(stream);
    assertEquals("{\"error\":{\"code\":null,\"message\":null}}", jsonString);
  }

  @Test
  public void singleDetailNothingSet() throws Exception {
    List<ODataErrorDetail> details = new ArrayList<ODataErrorDetail>();
    details.add(new ODataErrorDetail());
    ODataServerError error = new ODataServerError().setDetails(details);
    InputStream stream = ser.error(error);
    String jsonString = IOUtils.toString(stream);
    assertEquals("{\"error\":{\"code\":null,\"message\":null,\"details\":[{\"code\":null,\"message\":null}]}}",
        jsonString);
  }

  @Test
  public void verifiedWithJacksonParser() throws Exception {
    List<ODataErrorDetail> details = new ArrayList<ODataErrorDetail>();
    details.add(new ODataErrorDetail().setCode("detailCode").setMessage("detailMessage").setTarget("detailTarget"));
    ODataServerError error =
        new ODataServerError().setCode("Code").setMessage("Message").setTarget("Target").setDetails(details);
    InputStream stream = ser.error(error);
    JsonNode tree = new ObjectMapper().readTree(stream);
    assertNotNull(tree);
    tree = tree.get("error");
    assertNotNull(tree);
    assertEquals("Code", tree.get("code").textValue());
    assertEquals("Message", tree.get("message").textValue());
    assertEquals("Target", tree.get("target").textValue());

    tree = tree.get("details");
    assertNotNull(tree);
    assertEquals(JsonNodeType.ARRAY, tree.getNodeType());

    tree = tree.get(0);
    assertNotNull(tree);
    assertEquals("detailCode", tree.get("code").textValue());
    assertEquals("detailMessage", tree.get("message").textValue());
    assertEquals("detailTarget", tree.get("target").textValue());
  }
}
