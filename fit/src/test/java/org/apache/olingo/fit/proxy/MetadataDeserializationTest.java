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
package org.apache.olingo.fit.proxy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.custom.CustomXMLMetadata;
import org.junit.Test;

public class MetadataDeserializationTest {

  @Test
  public void testDeserializationWithNotAllowedCustomClass() throws IOException {
    CustomXMLMetadata metadata = new CustomXMLMetadata();
    assertTrue(CustomXMLMetadata.detectedMethodCalls());
    CustomXMLMetadata.forgetMethodCalls();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    GZIPOutputStream gos = new GZIPOutputStream(baos);
    ObjectOutputStream oos = new ObjectOutputStream(gos);

    oos.writeObject(metadata);
    oos.flush();
    gos.finish();
    gos.flush();

    String compressedCustomMetadata = new Base64().encodeToString(baos.toByteArray());

    assertFalse(CustomXMLMetadata.detectedMethodCalls());
    try {
      new ServiceImpl(compressedCustomMetadata, "etag", ODataServiceVersion.V40, null, false);
    } catch (Exception e) {
      // okay
    }

    assertFalse(CustomXMLMetadata.detectedMethodCalls());
  }

  @Test
  public void testDeserializationWithAllowedCustomClass() throws IOException {
    CustomXMLMetadata metadata = new CustomXMLMetadata();
    assertTrue(CustomXMLMetadata.detectedMethodCalls());
    CustomXMLMetadata.forgetMethodCalls();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    GZIPOutputStream gos = new GZIPOutputStream(baos);
    ObjectOutputStream oos = new ObjectOutputStream(gos);

    oos.writeObject(metadata);
    oos.flush();
    gos.finish();
    gos.flush();

    String compressedCustomMetadata = new Base64().encodeToString(baos.toByteArray());

    assertFalse(CustomXMLMetadata.detectedMethodCalls());
    try {
      new CustomService(compressedCustomMetadata, "etag", ODataServiceVersion.V40, null, false);
    } catch (Exception e) {
      // okay
    }

    assertTrue(CustomXMLMetadata.detectedMethodCalls());
  }

  private static class ServiceImpl extends AbstractService {

    ServiceImpl(String compressedMetadata, String metadataETag,
        ODataServiceVersion version, String serviceRoot, boolean transactional) {
      super(compressedMetadata, metadataETag, version, serviceRoot, transactional);
    }

    @Override
    public Class<?> getEntityTypeClass(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getComplexTypeClass(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getEnumTypeClass(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends AbstractTerm> getTermClass(String name) {
      throw new UnsupportedOperationException();
    }
  }

  /*
   * A service which allows custom XML metadata.
   */
  public static class CustomService extends AbstractService {

    CustomService(String compressedMetadata, String metadataETag,
        ODataServiceVersion version, String serviceRoot, boolean transactional) {
      super(compressedMetadata, metadataETag, version, serviceRoot, transactional);
    }

    @Override
    public Class<?> getEntityTypeClass(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getComplexTypeClass(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getEnumTypeClass(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends AbstractTerm> getTermClass(String name) {
      throw new UnsupportedOperationException();
    }

    /*
     * Allows CustomXMLMetadata.
     */
    @Override
    protected Set<String> getAllowedClasses() {
      Set<String> classes = new HashSet<>();
      classes.add(CustomXMLMetadata.class.getCanonicalName());
      return Collections.unmodifiableSet(classes);
    }
  }

}
