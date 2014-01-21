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

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.provider.Action;
import org.apache.olingo.odata4.commons.api.edm.provider.EntitySetPath;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.Parameter;
import org.apache.olingo.odata4.commons.api.edm.provider.ReturnType;
import org.junit.Before;

public class EdmActionImplTest {

  private EdmActionImpl actionImpl1;
  private EdmActionImpl actionImpl2;
  private EdmActionImpl actionImpl3;

  @Before
  public void setup() {
    EdmProviderImpl provider = mock(EdmProviderImpl.class);
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(new Parameter().setName("Id").setType(new FullQualifiedName("namespace", "name")));
    FullQualifiedName action1Name = new FullQualifiedName("namespace", "action1");
    Action action1 = new Action().setName("action1").setBound(true).setParameters(parameters);
    actionImpl1 = new EdmActionImpl(provider, action1Name, action1);

    FullQualifiedName action2Name = new FullQualifiedName("namespace", "action2");
    FullQualifiedName returnTypeName = new FullQualifiedName("returnNamespace", "returnName");
    ReturnType returnType = new ReturnType().setType(returnTypeName);
    Action action2 = new Action().setName("action2").setParameters(parameters).setReturnType(returnType);
    actionImpl2 = new EdmActionImpl(provider, action2Name, action2);

    FullQualifiedName action3Name = new FullQualifiedName("namespace", "action3");
    EntitySetPath entitySetPath = new EntitySetPath().setBindingParameter("Id").setPath("path");
    Action action3 =
        new Action().setName("action3").setParameters(parameters).setReturnType(returnType).setEntitySetPath(
            entitySetPath);
    actionImpl3 = new EdmActionImpl(provider, action3Name, action3);

  }

}
