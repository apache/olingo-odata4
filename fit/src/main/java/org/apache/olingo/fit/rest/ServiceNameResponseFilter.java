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
package org.apache.olingo.fit.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.fit.utils.Constants;

@Provider
public class ServiceNameResponseFilter implements ContainerResponseFilter {

  @Override
  public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
      throws IOException {

    final String svcName =
        StringUtils.substringBefore(StringUtils.substringAfter(requestContext.getUriInfo().getPath(), "/"), "/");

    if ("OAuth2.svc".equals(svcName) && responseContext.getEntity() != null) {
      final String content = IOUtils.toString((InputStream) responseContext.getEntity(), Constants.ENCODING).
          replaceAll("Static\\.svc", svcName);

      final InputStream toBeStreamedBack = IOUtils.toInputStream(content, Constants.ENCODING);
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      IOUtils.copy(toBeStreamedBack, baos);
      IOUtils.closeQuietly(toBeStreamedBack);

      responseContext.setEntity(new ByteArrayInputStream(baos.toByteArray()));
    }
  }
}
