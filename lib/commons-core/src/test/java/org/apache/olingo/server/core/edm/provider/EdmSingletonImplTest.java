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
package org.apache.olingo.server.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.core.edm.EdmEntityContainerImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.commons.core.edm.EdmSingletonImpl;
import org.junit.Test;

public class EdmSingletonImplTest {

  @Test
  public void singleton() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    final FullQualifiedName typeName = new FullQualifiedName("ns", "entityType");
    final CsdlEntityType entityTypeProvider = new CsdlEntityType()
        .setName(typeName.getName())
        .setKey(Arrays.asList(new CsdlPropertyRef().setName("Id")));
    when(provider.getEntityType(typeName)).thenReturn(entityTypeProvider);

    final FullQualifiedName containerName = new FullQualifiedName("ns", "container");
    final CsdlEntityContainerInfo containerInfo = new CsdlEntityContainerInfo().setContainerName(containerName);
    when(provider.getEntityContainerInfo(containerName)).thenReturn(containerInfo);
    final EdmEntityContainer entityContainer = new EdmEntityContainerImpl(edm, provider, containerInfo);

    final String singletonName = "singleton";
    final CsdlSingleton singletonProvider =
        new CsdlSingleton()
            .setName(singletonName)
            .setTitle("title")
            .setType(typeName)
            .setNavigationPropertyBindings(
                Arrays.asList(
                    new CsdlNavigationPropertyBinding().setPath("path").setTarget(
                        containerName.getFullQualifiedNameAsString() + "/" + singletonName)));
    when(provider.getSingleton(containerName, singletonName)).thenReturn(singletonProvider);

    final EdmSingleton singleton = new EdmSingletonImpl(edm, entityContainer, singletonProvider);
    assertEquals(singletonName, entityContainer.getSingleton(singletonName).getName());
    assertEquals(singletonName, singleton.getName());
    assertEquals("title", singleton.getTitle());
    final EdmEntityType entityType = singleton.getEntityType();
    assertEquals(typeName.getNamespace(), entityType.getNamespace());
    assertEquals(typeName.getName(), entityType.getName());
    assertEquals(entityContainer, singleton.getEntityContainer());
    assertNull(singleton.getRelatedBindingTarget(null));
    final EdmBindingTarget target = singleton.getRelatedBindingTarget("path");
    assertEquals(singletonName, target.getName());
  }

  @Test(expected = EdmException.class)
  public void wrongTarget() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    final FullQualifiedName containerName = new FullQualifiedName("ns", "container");
    final CsdlEntityContainerInfo containerInfo = new CsdlEntityContainerInfo().setContainerName(containerName);
    when(provider.getEntityContainerInfo(containerName)).thenReturn(containerInfo);

    final String singletonName = "singleton";
    final CsdlSingleton singletonProvider = new CsdlSingleton()
        .setNavigationPropertyBindings(Arrays.asList(
            new CsdlNavigationPropertyBinding().setPath("path")
                .setTarget(containerName.getFullQualifiedNameAsString() + "/wrong")));
    when(provider.getSingleton(containerName, singletonName)).thenReturn(singletonProvider);

    final EdmSingleton singleton = new EdmSingletonImpl(edm, null, singletonProvider);
    singleton.getRelatedBindingTarget("path");
  }

  @Test(expected = EdmException.class)
  public void wrongTargetContainer() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    final FullQualifiedName containerName = new FullQualifiedName("ns", "container");
    final String singletonName = "singleton";
    final CsdlSingleton singletonProvider = new CsdlSingleton()
        .setNavigationPropertyBindings(Arrays.asList(
            new CsdlNavigationPropertyBinding().setPath("path").setTarget("ns.wrongContainer/" + singletonName)));
    when(provider.getSingleton(containerName, singletonName)).thenReturn(singletonProvider);

    final EdmSingleton singleton = new EdmSingletonImpl(edm, null, singletonProvider);
    singleton.getRelatedBindingTarget("path");
  }

  @Test(expected = EdmException.class)
  public void nonExsistingEntityType() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    CsdlSingleton singleton = new CsdlSingleton().setName("name");
    final EdmSingleton edmSingleton = new EdmSingletonImpl(edm, null, singleton);
    edmSingleton.getEntityType();
  }
}
