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
package org.apache.olingo.ext.proxy.commons;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.ext.proxy.AbstractService;

public class EdmStreamValueHandler extends AbstractInvocationHandler {

  private URI uri;

  private InputStream stream;

  private String contentType;

  protected EdmStreamValueHandler(
          final URI uri,
          final AbstractService<?> service) {

    super(service);
    this.uri = uri;
  }

  protected EdmStreamValueHandler(
          final String contentType,
          final InputStream stream,
          final URI uri,
          final AbstractService<?> service) {

    super(service);
    this.contentType = contentType;
    this.stream = stream;
    this.uri = uri;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if ("load".equals(method.getName())) {
      load();
      return proxy;
    } else {
      if (isSelfMethod(method)) {
        return invokeSelfMethod(method, args);
      } else {
        throw new NoSuchMethodException(method.getName());
      }
    }
  }

  public void load() {
    if (this.uri != null) {
      final ODataRetrieveResponse<InputStream> res =
              getClient().getRetrieveRequestFactory().getMediaRequest(this.uri).execute();
      contentType = res.getContentType();
      stream = res.getBody();
    }
  }

  public String getContentType() {
    return contentType;
  }

  public InputStream getStream() {
    return stream;
  }

  public void close() {
    IOUtils.closeQuietly(stream);
    contentType = null;
    stream = null;
  }
}
