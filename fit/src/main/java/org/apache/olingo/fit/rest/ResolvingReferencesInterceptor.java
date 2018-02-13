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

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.olingo.commons.core.Decoder;

public class ResolvingReferencesInterceptor extends AbstractPhaseInterceptor<Message> {

  public ResolvingReferencesInterceptor() {
    super(Phase.PRE_PROTOCOL);
  }

  @Override
  public void handleMessage(final Message message) throws Fault {
    final String path = (String) message.get(Message.PATH_INFO);
    final String query = (String) message.get(Message.QUERY_STRING);

    if (path.endsWith("$entity") && StringUtils.isNotBlank(query)
        && Decoder.decode(query).contains("$id=")) {

      final String id = Decoder.decode(query);
      final String newURL = id.substring(id.indexOf("$id=") + 4);

      final URI uri = URI.create(newURL);

      message.put(Message.REQUEST_URL, uri.toASCIIString());
      message.put(Message.REQUEST_URI, uri.getPath());
      message.put(Message.PATH_INFO, uri.getPath());
    }
  }
}
