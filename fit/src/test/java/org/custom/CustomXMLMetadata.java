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
package org.custom;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.edm.xml.Reference;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

public class CustomXMLMetadata implements XMLMetadata, Serializable {

  /*
   * This flag shows if a method of the class was called.
   */
  private static boolean methodsCalled = false;

  /*
   * Clear the methodsCalled flag.
   */
  public static void forgetMethodCalls() {
    methodsCalled = false;
  }

  /*
   * Returns true if any method of this class was called.
   */
  public static boolean detectedMethodCalls() {
    return methodsCalled;
  }

  public CustomXMLMetadata() {
    methodsCalled = true;
  }

  @Override
  public CsdlSchema getSchema(int index) {
    methodsCalled = true;
    throw new UnsupportedOperationException();
  }

  @Override
  public CsdlSchema getSchema(String key) {
    methodsCalled = true;
    throw new UnsupportedOperationException();
  }

  @Override
  public List<CsdlSchema> getSchemas() {
    methodsCalled = true;
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, CsdlSchema> getSchemaByNsOrAlias() {
    methodsCalled = true;
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Reference> getReferences() {
    methodsCalled = true;
    throw new UnsupportedOperationException();
  }

  @Override
  public List<List<String>> getSchemaNamespaces() {
    methodsCalled = true;
    throw new UnsupportedOperationException();
  }

  @Override
  public String getEdmVersion() {
    methodsCalled = true;
    throw new UnsupportedOperationException();
  }
}
