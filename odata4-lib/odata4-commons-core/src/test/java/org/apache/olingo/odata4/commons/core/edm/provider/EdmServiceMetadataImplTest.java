/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEntitySetInfo;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImportInfo;
import org.apache.olingo.odata4.commons.api.edm.EdmServiceMetadata;
import org.apache.olingo.odata4.commons.api.edm.EdmSingletonInfo;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityContainer;
import org.apache.olingo.odata4.commons.api.edm.provider.EntitySet;
import org.apache.olingo.odata4.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.odata4.commons.api.edm.provider.Schema;
import org.apache.olingo.odata4.commons.api.edm.provider.Singleton;
import org.apache.olingo.odata4.commons.api.exception.ODataException;
import org.junit.Test;

public class EdmServiceMetadataImplTest {

  @Test
  public void allGettersMustDeliver() {
    EdmServiceMetadata serviceMetadata = new EdmServiceMetadataImpl(new CustomProvider(true));
    List<EdmEntitySetInfo> entitySetInfos = serviceMetadata.getEntitySetInfos();
    assertNotNull(entitySetInfos);
    assertEquals(2, entitySetInfos.size());

    List<EdmSingletonInfo> singletonInfos = serviceMetadata.getSingletonInfos();
    assertNotNull(singletonInfos);
    assertEquals(2, singletonInfos.size());

    List<EdmFunctionImportInfo> functionImportInfos = serviceMetadata.getFunctionImportInfos();
    assertNotNull(functionImportInfos);
    assertEquals(2, functionImportInfos.size());

    // Cache test
    assertTrue(entitySetInfos == serviceMetadata.getEntitySetInfos());
    assertTrue(singletonInfos == serviceMetadata.getSingletonInfos());
    assertTrue(functionImportInfos == serviceMetadata.getFunctionImportInfos());
  }

  @Test(expected = RuntimeException.class)
  public void getMetadataAsInputStreamIsNotImplemented() {
    EdmServiceMetadata serviceMetadata = new EdmServiceMetadataImpl(new CustomProvider(true));
    serviceMetadata.getMetadata();
  }

  @Test
  public void initialProvider() {
    EdmProvider provider = new EdmProvider() {};
    EdmServiceMetadata serviceMetadata = new EdmServiceMetadataImpl(provider);
    assertEquals(ODataServiceVersion.V40, serviceMetadata.getDataServiceVersion());
  }

  @Test(expected = EdmException.class)
  public void initialProviderEntitySetInfo() {
    EdmProvider provider = new EdmProvider() {};
    EdmServiceMetadata serviceMetadata = new EdmServiceMetadataImpl(provider);
    serviceMetadata.getEntitySetInfos();
  }

  @Test(expected = EdmException.class)
  public void initialProviderSingletonInfo() {
    EdmProvider provider = new EdmProvider() {};
    EdmServiceMetadata serviceMetadata = new EdmServiceMetadataImpl(provider);
    serviceMetadata.getSingletonInfos();
  }

  @Test(expected = EdmException.class)
  public void initialProviderFunctionImportInfo() {
    EdmProvider provider = new EdmProvider() {};
    EdmServiceMetadata serviceMetadata = new EdmServiceMetadataImpl(provider);
    serviceMetadata.getFunctionImportInfos();
  }

  @Test
  public void emptySchemaMustNotResultInException() {
    EdmServiceMetadata serviceMetadata = new EdmServiceMetadataImpl(new CustomProvider(false));
    assertNotNull(serviceMetadata.getEntitySetInfos());
    assertEquals(0, serviceMetadata.getEntitySetInfos().size());

    assertNotNull(serviceMetadata.getSingletonInfos());
    assertEquals(0, serviceMetadata.getSingletonInfos().size());

    assertNotNull(serviceMetadata.getFunctionImportInfos());
    assertEquals(0, serviceMetadata.getFunctionImportInfos().size());
  }

  @Test
  public void oDataExceptionsGetCaughtAndTransformed() {
    EdmProvider provider = new EdmProvider() {
      @Override
      public List<Schema> getSchemas() throws ODataException {
        throw new ODataException("msg");
      }
    };

    EdmServiceMetadata serviceMetadata = new EdmServiceMetadataImpl(provider);
    callGetEntitySetInfosAndExpectException(serviceMetadata);
    callGetSingletonInfosAndExpectException(serviceMetadata);
    callGetFunctionImportInfosAndExpectException(serviceMetadata);
  }

  private void callGetFunctionImportInfosAndExpectException(EdmServiceMetadata svc) {
    try {
      svc.getFunctionImportInfos();
    } catch (EdmException e) {
      assertEquals("org.apache.olingo.odata4.commons.api.exception.ODataException: msg", e.getMessage());
      return;
    }
    fail("Expected EdmException was not thrown");

  }

  private void callGetSingletonInfosAndExpectException(EdmServiceMetadata svc) {
    try {
      svc.getSingletonInfos();
    } catch (EdmException e) {
      assertEquals("org.apache.olingo.odata4.commons.api.exception.ODataException: msg", e.getMessage());
      return;
    }
    fail("Expected EdmException was not thrown");
  }

  private void callGetEntitySetInfosAndExpectException(EdmServiceMetadata svc) {
    try {
      svc.getEntitySetInfos();
    } catch (EdmException e) {
      assertEquals("org.apache.olingo.odata4.commons.api.exception.ODataException: msg", e.getMessage());
      return;
    }
    fail("Expected EdmException was not thrown");
  }

  private class CustomProvider extends EdmProvider {
    private List<Schema> schemas;

    public CustomProvider(boolean fillSchema) {
      schemas = new ArrayList<Schema>();
      if (fillSchema) {
        List<EntitySet> entitySets = new ArrayList<EntitySet>();
        entitySets.add(new EntitySet().setName("1"));
        entitySets.add(new EntitySet().setName("2"));
        List<Singleton> singletons = new ArrayList<Singleton>();
        singletons.add(new Singleton().setName("1"));
        singletons.add(new Singleton().setName("2"));
        List<FunctionImport> functionImports = new ArrayList<FunctionImport>();
        functionImports.add(new FunctionImport().setName("1"));
        functionImports.add(new FunctionImport().setName("2"));
        EntityContainer entityContainer =
            new EntityContainer().setName("cont").setEntitySets(entitySets).setSingletons(singletons)
                .setFunctionImports(functionImports);
        Schema schema = new Schema().setEntityContainer(entityContainer);
        schemas.add(schema);
      }
    }

    @Override
    public List<Schema> getSchemas() throws ODataException {
      return schemas;
    }
  }
}
