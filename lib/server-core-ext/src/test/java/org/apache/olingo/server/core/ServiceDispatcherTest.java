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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.core.Encoder;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.core.requests.ActionRequest;
import org.apache.olingo.server.core.requests.DataRequest;
import org.apache.olingo.server.core.requests.FunctionRequest;
import org.apache.olingo.server.core.requests.MediaRequest;
import org.apache.olingo.server.core.requests.MetadataRequest;
import org.apache.olingo.server.core.responses.CountResponse;
import org.apache.olingo.server.core.responses.EntityResponse;
import org.apache.olingo.server.core.responses.EntitySetResponse;
import org.apache.olingo.server.core.responses.MetadataResponse;
import org.apache.olingo.server.core.responses.NoContentResponse;
import org.apache.olingo.server.core.responses.PrimitiveValueResponse;
import org.apache.olingo.server.core.responses.PropertyResponse;
import org.apache.olingo.server.core.responses.StreamResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class ServiceDispatcherTest {
  private static final int TOMCAT_PORT = 9900;
  private Tomcat tomcat = new Tomcat();

  public class SampleODataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ServiceHandler handler; // must be stateless
    private final ServiceMetadata metadata; // must be stateless

    public SampleODataServlet(ServiceHandler handler, ServiceMetadata metadata) {
      this.handler = handler;
      this.metadata = metadata;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
      OData odata = OData4Impl.newInstance();

      ODataHttpHandler handler = odata.createHandler(this.metadata);

      handler.register(this.handler);
      handler.process(request, response);
    }
  }
  
  public void beforeTest(ServiceHandler serviceHandler) throws Exception {
    MetadataParser parser = new MetadataParser();
    parser.parseAnnotations(true);
    parser.useLocalCoreVocabularies(true);
    parser.implicitlyLoadCoreVocabularies(true);
    ServiceMetadata metadata = parser.buildServiceMetadata(new FileReader("src/test/resources/trippin.xml"));

    File baseDir = new File(System.getProperty("java.io.tmpdir"));
    tomcat.setBaseDir(baseDir.getAbsolutePath());
    tomcat.getHost().setAppBase(baseDir.getAbsolutePath());
    Context cxt = tomcat.addContext("/trippin", baseDir.getAbsolutePath());
    Tomcat.addServlet(cxt, "trippin", new SampleODataServlet(serviceHandler, metadata));
    cxt.addServletMapping("/*", "trippin");
    tomcat.setPort(TOMCAT_PORT);
    tomcat.start();
  }

  public void afterTest() throws Exception {
    tomcat.stop();
    tomcat.destroy();
  }

  interface TestResult {
    void validate() throws Exception;
  }

  private HttpHost getLocalhost() {
    return new HttpHost(tomcat.getHost().getName(), 9900);
  }
  
  private HttpResponse httpGET(String url) throws Exception{
    HttpRequest request = new HttpGet(url);
    return httpSend(request);
  }

  private HttpResponse httpSend(HttpRequest request) throws Exception{
    DefaultHttpClient http = new DefaultHttpClient();
    HttpResponse response = http.execute(getLocalhost(), request);
    return response;
  }
  
  private void helpGETTest(ServiceHandler handler, String path, TestResult validator)
      throws Exception {
    beforeTest(handler);
    httpGET("http://localhost:" + TOMCAT_PORT + "/" + path);
    validator.validate();
    afterTest();
  }

  private void helpTest(ServiceHandler handler, String path, String method, String payload,
      TestResult validator) throws Exception {
    beforeTest(handler);

    DefaultHttpClient http = new DefaultHttpClient();
    
    String editUrl = "http://localhost:" + TOMCAT_PORT + "/" + path;
    HttpRequest request = new HttpGet(editUrl);
    if (method.equals("POST")) {
      HttpPost post = new HttpPost(editUrl);
      post.setEntity(new StringEntity(payload));
      request = post;
    } else if (method.equals("PUT")) {
      HttpPut put = new HttpPut(editUrl);
      put.setEntity(new StringEntity(payload));
      request = put;
    } else if (method.equals("DELETE")) {
      HttpDelete delete = new HttpDelete(editUrl);
      request = delete;
    }
    request.setHeader("Content-Type", "application/json;odata.metadata=minimal");
    http.execute(getLocalhost(), request);

    validator.validate();
    afterTest();
  }

  @Test
  public void testMetadata() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/$metadata", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<MetadataRequest> arg1 = ArgumentCaptor.forClass(MetadataRequest.class);
        ArgumentCaptor<MetadataResponse> arg2 = ArgumentCaptor.forClass(MetadataResponse.class);
        Mockito.verify(handler).readMetadata(arg1.capture(), arg2.capture());
      }
    });
  }

  @Test
  public void testEntitySet() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/Airports", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<EntityResponse> arg2 = ArgumentCaptor.forClass(EntityResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        // Need getName on ContextURL class
        // assertEquals("",
        // request.getContextURL(request.getOdata()).getName());
        assertEquals("application/json;odata.metadata=minimal", request.getResponseContentType()
            .toContentTypeString());
      }
    });
  }

  @Test
  public void testEntitySetCount() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/Airports/$count", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<CountResponse> arg2 = ArgumentCaptor.forClass(CountResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        // Need getName on ContextURL class
        // assertEquals("",
        // request.getContextURL(request.getOdata()).getName());
        assertEquals("text/plain", request.getResponseContentType().toContentTypeString());
      }
    });
  }

  @Test
  public void testEntity() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/Airports('0')", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<EntityResponse> arg2 = ArgumentCaptor.forClass(EntityResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        assertEquals(1, request.getUriResourceEntitySet().getKeyPredicates().size());
        assertEquals("application/json;odata.metadata=minimal", request.getResponseContentType()
            .toContentTypeString());
      }
    });
  }

  @Test
  public void testReadProperty() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/Airports('0')/IataCode", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<PropertyResponse> arg2 = ArgumentCaptor.forClass(PropertyResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        assertTrue(request.isPropertyRequest());
        assertFalse(request.isPropertyComplex());
        assertEquals(1, request.getUriResourceEntitySet().getKeyPredicates().size());
        assertEquals("application/json;odata.metadata=minimal", request.getResponseContentType()
            .toContentTypeString());
      }
    });
  }

  @Test
  public void testReadComplexProperty() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/Airports('0')/Location", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<PropertyResponse> arg2 = ArgumentCaptor.forClass(PropertyResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        assertTrue(request.isPropertyRequest());
        assertTrue(request.isPropertyComplex());
        assertEquals(1, request.getUriResourceEntitySet().getKeyPredicates().size());
        assertEquals("application/json;odata.metadata=minimal", request.getResponseContentType()
            .toContentTypeString());
      }
    });
  }

  @Test
  public void testReadProperty$Value() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/Airports('0')/IataCode/$value", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<PrimitiveValueResponse> arg2 = ArgumentCaptor
            .forClass(PrimitiveValueResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        assertTrue(request.isPropertyRequest());
        assertFalse(request.isPropertyComplex());
        assertEquals(1, request.getUriResourceEntitySet().getKeyPredicates().size());
        assertEquals("text/plain", request.getResponseContentType().toContentTypeString());
      }
    });
  }

  @Test
  public void testReadPropertyRef() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/Airports('0')/IataCode/$value", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<PrimitiveValueResponse> arg2 = ArgumentCaptor
            .forClass(PrimitiveValueResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        assertTrue(request.isPropertyRequest());
        assertFalse(request.isPropertyComplex());
        assertEquals(1, request.getUriResourceEntitySet().getKeyPredicates().size());
        assertEquals("text/plain", request.getResponseContentType().toContentTypeString());
      }
    });
  }

  @Test
  public void testFunctionImport() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/GetNearestAirport(lat=12.11,lon=34.23)", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<FunctionRequest> arg1 = ArgumentCaptor.forClass(FunctionRequest.class);
        ArgumentCaptor<PropertyResponse> arg3 = ArgumentCaptor.forClass(PropertyResponse.class);
        ArgumentCaptor<HttpMethod> arg2 = ArgumentCaptor.forClass(HttpMethod.class);
        Mockito.verify(handler).invoke(arg1.capture(), arg2.capture(), arg3.capture());

        arg1.getValue();
      }
    });
  }

  @Test
  public void testActionImport() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpTest(handler, "trippin/ResetDataSource", "POST", "", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<ActionRequest> arg1 = ArgumentCaptor.forClass(ActionRequest.class);
        ArgumentCaptor<NoContentResponse> arg2 = ArgumentCaptor.forClass(NoContentResponse.class);
        Mockito.verify(handler).invoke(arg1.capture(), Mockito.anyString(), arg2.capture());

        arg1.getValue();
      }
    });
  }

  @Test
  public void testReadMedia() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/Photos(1)/$value", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<MediaRequest> arg1 = ArgumentCaptor.forClass(MediaRequest.class);
        ArgumentCaptor<StreamResponse> arg2 = ArgumentCaptor.forClass(StreamResponse.class);
        Mockito.verify(handler).readMediaStream(arg1.capture(), arg2.capture());

        MediaRequest request = arg1.getValue();
        assertEquals("application/octet-stream", request.getResponseContentType()
            .toContentTypeString());
      }
    });
  }

  @Test
  public void testReadNavigation() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/People('russelwhyte')/Friends", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<EntitySetResponse> arg2 = ArgumentCaptor.forClass(EntitySetResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        assertEquals("application/json;odata.metadata=minimal", request.getResponseContentType()
            .toContentTypeString());
      }
    });
  }

  @Test
  public void testReadReference() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/People('russelwhyte')/Friends/$ref", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<EntitySetResponse> arg2 = ArgumentCaptor.forClass(EntitySetResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        assertEquals("application/json;odata.metadata=minimal", request.getResponseContentType()
            .toContentTypeString());
      }
    });
  }

  @Test
  public void testWriteReferenceCollection() throws Exception {
    String payload = "{\n" + "\"@odata.id\": \"/Photos(11)\"\n" + "}";

    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpTest(handler, "trippin/People('russelwhyte')/Friends/$ref", "POST", payload,
        new TestResult() {
          @Override
          public void validate() throws Exception {
            ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
            ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<URI> arg3 = ArgumentCaptor.forClass(URI.class);
            ArgumentCaptor<NoContentResponse> arg4 = ArgumentCaptor
                .forClass(NoContentResponse.class);
            Mockito.verify(handler).addReference(arg1.capture(), arg2.capture(), arg3.capture(),
                arg4.capture());

            DataRequest request = arg1.getValue();
            assertEquals("application/json;odata.metadata=minimal", request
                .getResponseContentType().toContentTypeString());
          }
        });
  }

  @Test
  public void testWriteReference() throws Exception {
    String payload = "{\n" + "\"@odata.id\": \"/Photos(11)\"\n" + "}";

    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpTest(handler, "trippin/People('russelwhyte')/Friends('someone')/Photo/$ref", "PUT", payload,
        new TestResult() {
          @Override
          public void validate() throws Exception {
            ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
            ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<URI> arg3 = ArgumentCaptor.forClass(URI.class);
            ArgumentCaptor<NoContentResponse> arg4 = ArgumentCaptor
                .forClass(NoContentResponse.class);
            Mockito.verify(handler).updateReference(arg1.capture(), arg2.capture(), arg3.capture(),
                arg4.capture());

            DataRequest request = arg1.getValue();
            assertEquals("application/json;odata.metadata=minimal", request
                .getResponseContentType().toContentTypeString());
          }
        });
  }
  
  @Test
  public void test$id() throws Exception {
    final ServiceHandler handler = Mockito.mock(ServiceHandler.class);
    helpGETTest(handler, "trippin/$entity?$id="+Encoder.encode("http://localhost:" + TOMCAT_PORT
        + "/trippin/People('russelwhyte')")+"&"+Encoder.encode("$")+"select=FirstName", new TestResult() {
      @Override
      public void validate() throws Exception {
        ArgumentCaptor<DataRequest> arg1 = ArgumentCaptor.forClass(DataRequest.class);
        ArgumentCaptor<EntityResponse> arg2 = ArgumentCaptor.forClass(EntityResponse.class);
        Mockito.verify(handler).read(arg1.capture(), arg2.capture());

        DataRequest request = arg1.getValue();
        assertEquals("application/json;odata.metadata=minimal", request.getResponseContentType()
            .toContentTypeString());
      }
    });
  }  
}
