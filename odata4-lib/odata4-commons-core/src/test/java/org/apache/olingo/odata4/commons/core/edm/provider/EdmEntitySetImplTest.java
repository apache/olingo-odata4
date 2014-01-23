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
import org.apache.olingo.odata4.commons.api.edm.EdmEntitySet;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata4.commons.api.edm.provider.EntitySet;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.odata4.commons.api.edm.provider.PropertyRef;
import org.apache.olingo.odata4.commons.api.edm.provider.Target;
import org.junit.Test;

public class EdmEntitySetImplTest {

  @Test
  public void entitySet() throws Exception {
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

    final String entitySetName = "entitySet";
    final EntitySet entitySetProvider = new EntitySet()
        .setName(entitySetName)
        .setType(typeName)
        .setIncludeInServiceDocument(true)
        .setNavigationPropertyBindings(Arrays.asList(
            new NavigationPropertyBinding().setPath("path")
                .setTarget(new Target().setEntityContainer(containerName).setTargetName(entitySetName))));
    when(provider.getEntitySet(containerName, entitySetName)).thenReturn(entitySetProvider);

    final EdmEntitySet entitySet = new EdmEntitySetImpl(edm, entityContainer, entitySetProvider);
    assertEquals(entitySetName, entityContainer.getEntitySet(entitySetName).getName());
    assertEquals(entitySetName, entitySet.getName());
    final EdmEntityType entityType = entitySet.getEntityType();
    assertEquals(typeName.getNamespace(), entityType.getNamespace());
    assertEquals(typeName.getName(), entityType.getName());
    assertEquals(entityContainer, entitySet.getEntityContainer());
    assertNull(entitySet.getRelatedBindingTarget(null));
    final EdmBindingTarget target = entitySet.getRelatedBindingTarget("path");
    assertEquals(entitySetName, target.getName());
  }
}
