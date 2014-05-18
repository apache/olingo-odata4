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
package org.apache.olingo.ext.proxy.commons;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.api.OperationExecutor;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

class OperationInvocationHandler extends AbstractInvocationHandler implements OperationExecutor {

  private static final long serialVersionUID = 2629912294765040027L;

  private final InvocationHandler target;

  private final FullQualifiedName targetFQN;

  static OperationInvocationHandler getInstance(final EntityContainerInvocationHandler containerHandler) {
    return new OperationInvocationHandler(containerHandler);
  }

  static OperationInvocationHandler getInstance(final EntityInvocationHandler entityHandler) {
    return new OperationInvocationHandler(entityHandler);
  }

  static OperationInvocationHandler getInstance(final EntityCollectionInvocationHandler<?> collectionHandler) {
    return new OperationInvocationHandler(collectionHandler);
  }

  private OperationInvocationHandler(final EntityContainerInvocationHandler containerHandler) {
    super(containerHandler);

    this.target = containerHandler;
    this.targetFQN = new FullQualifiedName(containerHandler.getSchemaName(), containerHandler.getEntityContainerName());
  }

  private OperationInvocationHandler(final EntityInvocationHandler entityHandler) {
    super(entityHandler.containerHandler);

    this.target = entityHandler;
    this.targetFQN = entityHandler.getEntity().getTypeName();
  }

  private OperationInvocationHandler(final EntityCollectionInvocationHandler<?> collectionHandler) {
    super(collectionHandler.containerHandler);

    this.target = collectionHandler;

    final String typeName = ClassUtils.getEntityTypeName(collectionHandler.getEntityReference());
    final String typeNamespace = ClassUtils.getNamespace(collectionHandler.getEntityReference());

    this.targetFQN = new FullQualifiedName(typeNamespace, typeName);
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else {
      final Annotation[] methodAnnots = method.getAnnotations();
      if (methodAnnots[0] instanceof Operation) {
        final Operation operation = (Operation) methodAnnots[0];

        final Annotation[][] annotations = method.getParameterAnnotations();
        final List<String> parameterNames;
        final LinkedHashMap<Parameter, Object> parameters = new LinkedHashMap<Parameter, Object>();
        if (annotations == null || annotations.length == 0) {
          parameterNames = null;
        } else {
          parameterNames = new ArrayList<String>();
          for (int i = 0; i < args.length; i++) {
            for (Annotation paramAnnotation : annotations[i]) {
              if (paramAnnotation instanceof Parameter) {
                parameterNames.add(((Parameter) paramAnnotation).name());
                parameters.put((Parameter) paramAnnotation, args[i]);
              }
            }

            if (parameters.size() <= i) {
              throw new IllegalArgumentException(
                      "Paramter " + i + " is not annotated as @" + Parameter.class.getSimpleName());
            }
          }
        }

        final Map.Entry<URI, EdmOperation> edmOperation;
        if (target instanceof EntityContainerInvocationHandler) {
          edmOperation = getUnboundOperation(operation, parameterNames);
        } else if (target instanceof EntityInvocationHandler) {
          edmOperation = getBoundOperation(operation, parameterNames);
        } else if (target instanceof EntityCollectionInvocationHandler) {
          edmOperation = getCollectionBoundOperation(operation, parameterNames);
        } else {
          throw new IllegalStateException("Invalid target invocation");
        }

        return invokeOperation(operation, method, parameters, edmOperation.getKey(), edmOperation.getValue());
      } else {
        throw new NoSuchMethodException(method.getName());
      }
    }
  }

  private Map.Entry<URI, EdmOperation> getUnboundOperation(
          final Operation operation, final List<String> parameterNames) {
    final EdmEntityContainer container = getClient().getCachedEdm().getEntityContainer(targetFQN);
    final EdmOperation edmOperation;

    if (operation.type() == OperationType.FUNCTION) {
      edmOperation = container.getFunctionImport(operation.name()).getUnboundFunction(parameterNames);
    } else {
      edmOperation = container.getActionImport(operation.name()).getUnboundAction();
    }

    final CommonURIBuilder<?> uriBuilder = getClient().getURIBuilder(getClient().getServiceRoot()).
            appendOperationCallSegment(URIUtils.operationImportURISegment(container, edmOperation.getName()));

    return new AbstractMap.SimpleEntry<URI, EdmOperation>(uriBuilder.build(), edmOperation);
  }

  private Map.Entry<URI, EdmOperation> getBoundOperation(final Operation operation, final List<String> parameterNames) {
    final CommonODataEntity entity = ((EntityInvocationHandler) target).getEntity();

    ODataOperation boundOp = entity.getOperation(operation.name());
    if (boundOp == null) {
      boundOp = entity.getOperation(new FullQualifiedName(targetFQN.getNamespace(), operation.name()).toString());
    }
    if (boundOp == null) {
      throw new IllegalArgumentException(String.format("Could not find any matching operation '%s' bound to %s",
              operation.name(), entity.getTypeName()));
    }

    final FullQualifiedName operationFQN = boundOp.getTitle().indexOf('.') == -1
            ? new FullQualifiedName(targetFQN.getNamespace(), boundOp.getTitle())
            : new FullQualifiedName(boundOp.getTitle());

    EdmEntityType entityType = getClient().getCachedEdm().getEntityType(entity.getTypeName());
    EdmOperation edmOperation = null;
    while (edmOperation == null && entityType != null) {
      edmOperation = operation.type() == OperationType.FUNCTION
              ? getClient().getCachedEdm().getBoundFunction(
                      operationFQN, entityType.getFullQualifiedName(), false, parameterNames)
              : getClient().getCachedEdm().getBoundAction(
                      operationFQN, entityType.getFullQualifiedName(), false);
      if (entityType.getBaseType() != null) {
        entityType = entityType.getBaseType();
      }
    }
    if (edmOperation == null) {
      throw new IllegalArgumentException(String.format("Could not find any matching operation '%s' bound to %s",
              operation.name(), entity.getTypeName()));
    }

    return new AbstractMap.SimpleEntry<URI, EdmOperation>(boundOp.getTarget(), edmOperation);
  }

  @SuppressWarnings("unchecked")
  private Map.Entry<URI, EdmOperation> getCollectionBoundOperation(
          final Operation operation, final List<String> parameterNames) {

    EdmOperation edmOperation;
    if (operation.type() == OperationType.FUNCTION) {
      edmOperation = getClient().getCachedEdm().getBoundFunction(
              new FullQualifiedName(targetFQN.getNamespace(), operation.name()), targetFQN, true, parameterNames);
    } else {
      edmOperation = getClient().getCachedEdm().getBoundAction(
              new FullQualifiedName(targetFQN.getNamespace(), operation.name()), targetFQN, true);
    }

    return new AbstractMap.SimpleEntry<URI, EdmOperation>(
            URI.create(((EntityCollectionInvocationHandler<?>) target).getURI().toASCIIString()
                    + "/" + edmOperation.getName()), edmOperation);
  }
}
