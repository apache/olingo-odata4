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
package org.apache.olingo.client.core.op.impl.v3;

import java.io.InputStream;

import org.apache.olingo.client.api.domain.ODataServiceDocument;
import org.apache.olingo.client.api.domain.v3.ODataLinkCollection;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.client.api.op.v3.ODataReader;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.v3.ODataClientImpl;
import org.apache.olingo.client.core.edm.EdmClientImpl;
import org.apache.olingo.client.core.op.impl.AbstractODataReader;
import org.apache.olingo.commons.api.edm.Edm;

public class ODataReaderImpl extends AbstractODataReader implements ODataReader {

  private static final long serialVersionUID = -2481293269536406956L;

  public ODataReaderImpl(final ODataClient client) {
    super(client);
  }

  @Override
  public Edm readMetadata(final InputStream input) {
    return new EdmClientImpl(client.getServiceVersion(), client.getDeserializer().toMetadata(input));
  }

  @Override
  public ODataServiceDocument readServiceDocument(final InputStream input, final ODataFormat format) {
    return ((ODataClientImpl) client).getBinder().getODataServiceDocument(
            ((ODataClientImpl) client).getDeserializer().toServiceDocument(input, format));
  }

  @Override
  public ODataLinkCollection readLinks(final InputStream input, final ODataFormat format) {
    return ((ODataClient) client).getBinder().getLinkCollection(
            ((ODataClient) client).getDeserializer().toLinkCollection(input, format));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T read(final InputStream src, final String format, final Class<T> reference) {
    return (ODataLinkCollection.class.isAssignableFrom(reference)
            ? (T) readLinks(src, ODataFormat.fromString(format))
            : super.read(src, format, reference));
  }

}
