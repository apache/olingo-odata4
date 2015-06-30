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
package org.apache.olingo.server.core.serializer.xml;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.ODataErrorDetail;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.junit.Before;
import org.junit.Test;

public class ServerErrorXmlSerializerTest {

  ODataSerializer ser;

  @Before
  public void before() throws Exception {
    ser = OData.newInstance().createSerializer(ContentType.APPLICATION_XML);
  }

  @Test
  public void basicODataErrorWithCode() throws Exception {
    ODataServerError error = new ODataServerError();
    error.setCode("Code").setMessage("ErrorMessage");
    InputStream stream = ser.error(error).getContent();
    String jsonString = IOUtils.toString(stream);
    assertEquals("<?xml version='1.0' encoding='UTF-8'?>"
        + "<error xmlns=\"http://docs.oasis-open.org/odata/ns/metadata\">"
        + "<code>0</code>"
        + "<message>ErrorMessage</message>"
        + "</error>", 
        jsonString);
  }

  @Test(expected = SerializerException.class)
  public void nullErrorResultsInException() throws Exception {
    ser.error(null);
  }

  @Test
  public void singleDetailNothingSet() throws Exception {
    ODataErrorDetail detail = new ODataErrorDetail();
    detail.setCode("detail code");
    detail.setMessage("detail message");
    
    List<ODataErrorDetail> details = new ArrayList<ODataErrorDetail>();
    details.add(detail);
    
    ODataServerError error = new ODataServerError().setDetails(details);
    error.setCode("code");
    error.setMessage("err message");
    error.setTarget("target");
    
    InputStream stream = ser.error(error).getContent();
    String jsonString = IOUtils.toString(stream);
    assertEquals("<?xml version='1.0' encoding='UTF-8'?>"
        + "<error xmlns=\"http://docs.oasis-open.org/odata/ns/metadata\">"
        + "<code>0</code>"
        + "<message>err message</message>"
        + "<target>target</target>"
        + "<details>"
        + "<code>detail code</code>"
        + "<message>detail message</message>"
        + "</details>"
        + "</error>",
        jsonString);
  }
}
