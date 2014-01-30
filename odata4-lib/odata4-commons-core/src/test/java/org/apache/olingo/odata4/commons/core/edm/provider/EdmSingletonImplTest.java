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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.apache.olingo.odata4.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmSingleton;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.odata4.commons.api.edm.provider.PropertyRef;
import org.apache.olingo.odata4.commons.api.edm.provider.Singleton;
import org.apache.olingo.odata4.commons.api.edm.provider.Target;
import org.junit.Test;

public class EdmSingletonImplTest {

  @Test
  public void singleton() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    final FullQualifiedName typeName = new FullQualifiedName("ns", "entityType");
    final EntityType entityTypeProvider = new EntityType()
        .setName(typeName.getName())
        .setKey(Arrays.asList(new PropertyRef().setPropertyName("Id")));
    when(provider.getEntityType(typeName)).thenReturn(entityTypeProvider);

    final FullQualifiedName containerName = new FullQualifiedName("ns", "container");
    final EntityContainerInfo containerInfo = new EntityContainerInfo().setContainerName(containerName);
    when(provider.getEntityContainerInfo(containerName)).thenReturn(containerInfo);
    final EdmEntityContainer entityContainer = new EdmEntityContainerImpl(edm, provider, containerInfo);

    final String singletonName = "singleton";
    final Singleton singletonProvider = new Singleton()
        .setName(singletonName)
        .setType(typeName)
        .setNavigationPropertyBindings(Arrays.asList(
            new NavigationPropertyBinding().setPath("path")
                .setTarget(new Target().setEntityContainer(containerName).setTargetName(singletonName))));
    when(provider.getSingleton(containerName, singletonName)).thenReturn(singletonProvider);

    final EdmSingleton singleton = new EdmSingletonImpl(edm, entityContainer, singletonProvider);
    assertEquals(singletonName, entityContainer.getSingleton(singletonName).getName());
    assertEquals(singletonName, singleton.getName());
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
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    final FullQualifiedName containerName = new FullQualifiedName("ns", "container");
    final EntityContainerInfo containerInfo = new EntityContainerInfo().setContainerName(containerName);
    when(provider.getEntityContainerInfo(containerName)).thenReturn(containerInfo);

    final String singletonName = "singleton";
    final Singleton singletonProvider = new Singleton()
        .setNavigationPropertyBindings(Arrays.asList(
            new NavigationPropertyBinding().setPath("path")
                .setTarget(new Target().setEntityContainer(containerName).setTargetName("wrong"))));
    when(provider.getSingleton(containerName, singletonName)).thenReturn(singletonProvider);

    final EdmSingleton singleton = new EdmSingletonImpl(edm, null, singletonProvider);
    singleton.getRelatedBindingTarget("path");
  }

  @Test(expected = EdmException.class)
  public void wrongTargetContainer() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    final FullQualifiedName containerName = new FullQualifiedName("ns", "container");
    final String singletonName = "singleton";
    final Singleton singletonProvider = new Singleton()
        .setNavigationPropertyBindings(Arrays.asList(
            new NavigationPropertyBinding().setPath("path")
                .setTarget(new Target().setEntityContainer(new FullQualifiedName("ns", "wrongContainer"))
                    .setTargetName(singletonName))));
    when(provider.getSingleton(containerName, singletonName)).thenReturn(singletonProvider);

    final EdmSingleton singleton = new EdmSingletonImpl(edm, null, singletonProvider);
    singleton.getRelatedBindingTarget("path");
  }

  @Test(expected = EdmException.class)
  public void nonExsistingEntityType() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    Singleton singleton = new Singleton().setName("name");
    final EdmSingleton edmSingleton = new EdmSingletonImpl(edm, null, singleton);
    edmSingleton.getEntityType();
  }
}
