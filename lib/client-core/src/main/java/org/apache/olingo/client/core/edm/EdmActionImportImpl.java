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
package org.apache.olingo.client.core.edm;

import java.util.List;

import org.apache.olingo.client.api.edm.xml.v4.ActionImport;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.EdmAnnotationHelper;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

public class EdmActionImportImpl extends EdmOperationImportImpl implements EdmActionImport {

  private final ActionImport actionImport;

  private final EdmAnnotationHelper helper;

  private FullQualifiedName actionFQN;

  public EdmActionImportImpl(final Edm edm, final EdmEntityContainer container, final String name,
          final ActionImport actionImport) {

    super(edm, container, name, actionImport.getEntitySet());
    this.actionImport = actionImport;
    this.helper = new EdmAnnotationHelperImpl(edm, actionImport);
  }

  public FullQualifiedName getActionFQN() {
    if (actionFQN == null) {
      actionFQN = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(actionImport.getAction()).
              setDefaultNamespace(container.getNamespace()).build().getFullQualifiedName();
    }
    return actionFQN;
  }

  @Override
  public EdmAction getUnboundAction() {
    return edm.getUnboundAction(getActionFQN());
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.ActionImport;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return helper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return helper.getAnnotations();
  }
}
