/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit;

import java.io.InputStream;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import org.apache.olingo.fit.utils.XHTTPMethodInterceptor;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.FSManager;
import org.springframework.stereotype.Service;

@Service
@Path("/V30/Static.svc")
@InInterceptors(classes = XHTTPMethodInterceptor.class)
public class V3Services extends AbstractServices {

  public V3Services() throws Exception {
    super(ODataServiceVersion.V30);
  }

  @GET
  @Path("/InStreamErrorGetCustomer")
  public Response instreamErrorGetCustomer(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    final Accept acceptType;
    if (StringUtils.isNotBlank(format)) {
      acceptType = Accept.valueOf(format.toUpperCase());
    } else {
      acceptType = Accept.parse(accept, version);
    }

    try {
      final InputStream error = FSManager.instance(version).readFile("InStreamErrorGetCustomer", acceptType);

      return Response.ok(error).
              header(Constants.get(version, ConstantKey.ODATA_SERVICE_VERSION), version + ";").
              header("Content-Type", acceptType.toString(version)).
              build();
    } catch (Exception e) {
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Provide sample large metadata.
   *
   * @return metadata.
   */
  @GET
  @Path("/large/$metadata")
  @Produces(MediaType.APPLICATION_XML)
  public Response getLargeMetadata() {
    return getMetadata("large" + StringUtils.capitalize(Constants.get(version, ConstantKey.METADATA)));
  }

  /**
   * Provide sample large metadata.
   *
   * @return metadata.
   */
  @GET
  @Path("/openType/$metadata")
  @Produces(MediaType.APPLICATION_XML)
  public Response getOpenTypeMetadata() {
    return getMetadata("openType" + StringUtils.capitalize(Constants.get(version, ConstantKey.METADATA)));
  }

  @Override
  protected void setInlineCount(final Feed feed, final String count) {
    if ("allpages".equals(count)) {
      feed.setCount(feed.getEntries().size());
    }
  }
}
