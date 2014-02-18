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
package org.apache.olingo.odata4.server.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmActionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntitySet;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.server.api.edm.provider.ActionImport;
import org.apache.olingo.odata4.server.api.edm.provider.Target;
import org.junit.Before;
import org.junit.Test;

public class EdmActionImportImplTest {

  EdmEntityContainer container;
  EdmActionImport actionImport;
  private EdmAction action;
  private EdmEntitySet entitySet;

  @Before
  public void setup() {
    FullQualifiedName actionFqn = new FullQualifiedName("namespace", "actionName");
    FullQualifiedName entityContainerFqn = new FullQualifiedName("namespace", "containerName");
    Target target = new Target().setEntityContainer(entityContainerFqn).setTargetName("entitySetName");
    ActionImport providerActionImport =
        new ActionImport().setName("actionImportName").setAction(actionFqn).setEntitySet(target);

    EdmProviderImpl edm = mock(EdmProviderImpl.class);
    container = mock(EdmEntityContainer.class);
    when(edm.getEntityContainer(entityContainerFqn)).thenReturn(container);
    action = mock(EdmAction.class);
    when(edm.getAction(actionFqn, null, null)).thenReturn(action);

    entitySet = mock(EdmEntitySet.class);
    when(container.getEntitySet("entitySetName")).thenReturn(entitySet);
    actionImport = new EdmActionImportImpl(edm, "actionImportName", container, providerActionImport);
  }

  @Test
  public void simpleActionTest() {
    assertEquals("actionImportName", actionImport.getName());
    assertTrue(container == actionImport.getEntityContainer());
    assertTrue(action == actionImport.getAction());
  }

  @Test
  public void getReturnedEntitySet() {
    EdmEntitySet returnedEntitySet = actionImport.getReturnedEntitySet();
    assertNotNull(returnedEntitySet);
    assertTrue(returnedEntitySet == entitySet);

    // Chaching
    assertTrue(returnedEntitySet == actionImport.getReturnedEntitySet());
  }

  @Test(expected = EdmException.class)
  public void getReturnedEntitySetNonExistingContainer() {
    Target target = new Target();
    ActionImport providerActionImport = new ActionImport().setName("actionImportName").setEntitySet(target);
    EdmActionImport actionImport =
        new EdmActionImportImpl(mock(EdmProviderImpl.class), "actionImportName", container, providerActionImport);
    actionImport.getReturnedEntitySet();
  }

  @Test(expected = EdmException.class)
  public void getReturnedEntitySetNonExistingEntitySet() {
    Target target = new Target();
    ActionImport providerActionImport = new ActionImport().setName("actionImportName").setEntitySet(target);
    EdmProviderImpl edm = mock(EdmProviderImpl.class);
    when(edm.getEntityContainer(null)).thenReturn(container);
    EdmActionImport actionImport = new EdmActionImportImpl(edm, "actionImportName", container, providerActionImport);
    actionImport.getReturnedEntitySet();
  }

}
