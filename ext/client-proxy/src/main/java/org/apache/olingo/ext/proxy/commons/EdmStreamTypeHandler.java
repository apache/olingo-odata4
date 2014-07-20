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
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.ext.proxy.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;

public class EdmStreamTypeHandler extends AbstractInvocationHandler {

  private static final long serialVersionUID = 2629912294765040047L;

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EdmStreamTypeHandler.class);

  private CommonURIBuilder<?> uri;

  private EdmStreamValue value = null;

  protected EdmStreamTypeHandler(
          final CommonURIBuilder<?> uri,
          final Service<?> service) {

    super(service);
    this.uri = uri;
  }

  @Override
  public Object invoke(Object o, Method method, Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }

  public EdmStreamValue load() {
    if (value == null && this.uri != null) {
      final ODataRetrieveResponse<InputStream> res =
              getClient().getRetrieveRequestFactory().getMediaRequest(this.uri.build()).execute();
      value = new EdmStreamValue(res.getContentType(), res.getBody());
    }

    return value;
  }
}
