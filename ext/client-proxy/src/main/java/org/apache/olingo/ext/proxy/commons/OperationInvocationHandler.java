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
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientOperation;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

final class OperationInvocationHandler extends AbstractInvocationHandler {

  private final InvocationHandler target;

  private final FullQualifiedName targetFQN;

  static OperationInvocationHandler getInstance(final EntityContainerInvocationHandler containerHandler) {
    return new OperationInvocationHandler(containerHandler);
  }

  static OperationInvocationHandler getInstance(final EntityInvocationHandler entityHandler) {
    return new OperationInvocationHandler(entityHandler);
  }

  static OperationInvocationHandler getInstance(final ComplexInvocationHandler complexHandler) {
    return new OperationInvocationHandler(complexHandler);
  }

  static OperationInvocationHandler getInstance(final EntityCollectionInvocationHandler<?> collectionHandler) {
    return new OperationInvocationHandler(collectionHandler);
  }

  static OperationInvocationHandler getInstance(final ComplexCollectionInvocationHandler<?> collectionHandler) {
    return new OperationInvocationHandler(collectionHandler);
  }

  static OperationInvocationHandler getInstance(final PrimitiveCollectionInvocationHandler<?> collectionHandler) {
    return new OperationInvocationHandler(collectionHandler);
  }

  private OperationInvocationHandler(final EntityContainerInvocationHandler containerHandler) {
    super(containerHandler.service);

    this.target = containerHandler;
    this.targetFQN = new FullQualifiedName(containerHandler.getSchemaName(), containerHandler.getEntityContainerName());
  }

  private OperationInvocationHandler(final EntityInvocationHandler entityHandler) {
    super(entityHandler.service);

    this.target = entityHandler;
    this.targetFQN = entityHandler.getEntity().getTypeName();
  }

  private OperationInvocationHandler(final ComplexInvocationHandler complexHandler) {
    super(complexHandler.service);

    this.target = complexHandler;
    this.targetFQN = new FullQualifiedName(complexHandler.getComplex().getTypeName());
  }

  private OperationInvocationHandler(final EntityCollectionInvocationHandler<?> collectionHandler) {
    super(collectionHandler.service);

    this.target = collectionHandler;

    final String typeName = ClassUtils.getEntityTypeName(collectionHandler.getTypeRef());
    final String typeNamespace = ClassUtils.getNamespace(collectionHandler.getTypeRef());

    this.targetFQN = new FullQualifiedName(typeNamespace, typeName);
  }

  private OperationInvocationHandler(final ComplexCollectionInvocationHandler<?> collectionHandler) {
    super(collectionHandler.service);

    this.target = collectionHandler;

    final String typeName = ClassUtils.getEntityTypeName(collectionHandler.getTypeRef());
    final String typeNamespace = ClassUtils.getNamespace(collectionHandler.getTypeRef());

    this.targetFQN = new FullQualifiedName(typeNamespace, typeName);
  }

  private OperationInvocationHandler(final PrimitiveCollectionInvocationHandler<?> collectionHandler) {
    super(collectionHandler.service);

    this.target = collectionHandler;

    final String typeName = ClassUtils.getEntityTypeName(collectionHandler.getTypeRef());
    final String typeNamespace = ClassUtils.getNamespace(collectionHandler.getTypeRef());

    this.targetFQN = new FullQualifiedName(typeNamespace, typeName);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method)) {
      return invokeSelfMethod(method, args);
    } else {
      final Operation operation = method.getAnnotation(Operation.class);
      if (operation != null) {
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

        final Map<String, ClientValue> parameterValues = new LinkedHashMap<String, ClientValue>();
        for (Map.Entry<Parameter, Object> parameter : parameters.entrySet()) {
          if (!parameter.getKey().nullable() && parameter.getValue() == null) {
            throw new IllegalArgumentException(
                    "Parameter " + parameter.getKey().name() + " is not nullable but a null value was provided");
          }

          final EdmTypeInfo parameterType = new EdmTypeInfo.Builder().
                  setEdm(service.getClient().getCachedEdm()).setTypeExpression(parameter.getKey().type()).build();

          final ClientValue paramValue = parameter.getValue() == null
                  ? null
                  : CoreUtils.getODataValue(service.getClient(), parameterType, parameter.getValue());

          parameterValues.put(parameter.getKey().name(), paramValue);
        }

        final EdmTypeInfo returnType = edmOperation.getValue().getReturnType() == null
                ? null
                : new EdmTypeInfo.Builder().setEdm(service.getClient().getCachedEdm()).setTypeExpression(
                        edmOperation.getValue().getReturnType().getType().getFullQualifiedName().toString()).build();

        final InvokerInvocationHandler handler = returnType != null
                && (returnType.isEntityType() || returnType.isComplexType()) && operation.isComposable()
                ? new StructuredComposableInvokerInvocationHandler(
                        edmOperation.getKey(),
                        parameterValues,
                        operation,
                        edmOperation.getValue(),
                        ClassUtils.getTypeArguments(method.getReturnType().getGenericInterfaces()[0]),
                        returnType,
                        service)
                : new InvokerInvocationHandler(
                        edmOperation.getKey(),
                        parameterValues,
                        operation,
                        edmOperation.getValue(),
                        ClassUtils.getTypeArguments(method.getGenericReturnType()),
                        service);
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {ClassUtils.getTypeClass(method.getGenericReturnType())},
                handler);
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

    final URIBuilder uriBuilder = getClient().newURIBuilder().
            appendOperationCallSegment(edmOperation.getName());

    return new AbstractMap.SimpleEntry<URI, EdmOperation>(uriBuilder.build(), edmOperation);
  }

  private Map.Entry<URI, EdmOperation> getBoundOperation(final Operation operation, final List<String> parameterNames) {
    final ClientEntity entity = EntityInvocationHandler.class.cast(target).getEntity();
    final URI entityURI = EntityInvocationHandler.class.cast(target).getEntityURI();

    ClientOperation boundOp = entity.getOperation(operation.name());
    if (boundOp == null) {
      boundOp = entity.getOperation(new FullQualifiedName(targetFQN.getNamespace(), operation.name()).toString());
    }

    final boolean useOperationFQN = this.getClient().getConfiguration().isUseUrlOperationFQN();

    EdmEntityType entityType = getClient().getCachedEdm().getEntityType(entity.getTypeName());
    EdmEntityType baseType = entityType;
    while (boundOp == null && baseType != null) {
      // json minimal/none metadata doesn't return operations for entity, so here try creating it from Edm: 
      final EdmAction action = this.getClient().getCachedEdm().getBoundAction(
              new FullQualifiedName(targetFQN.getNamespace(), operation.name()),
              baseType.getFullQualifiedName(),
              false);

      if (action == null) {
        baseType = baseType.getBaseType();
      } else {
        boundOp = new ClientOperation();
        boundOp.setMetadataAnchor(action.getFullQualifiedName().toString());
        boundOp.setTitle(boundOp.getMetadataAnchor());
        boundOp.setTarget(URI.create(entityURI.toASCIIString() + "/"
                + (useOperationFQN ? action.getFullQualifiedName().toString() : operation.name())));
      }
    }

    baseType = entityType;
    while (boundOp == null && baseType != null) {
      // json minimal/none metadata doesn't return operations for entity, so here try creating it from Edm: 
      final EdmFunction func = this.getClient().getCachedEdm().getBoundFunction(
              new FullQualifiedName(targetFQN.getNamespace(), operation.name()), baseType.getFullQualifiedName(),
              false, parameterNames);

      if (func == null) {
        baseType = baseType.getBaseType();
      } else {
        boundOp = new ClientOperation();
        boundOp.setMetadataAnchor(func.getFullQualifiedName().toString());
        boundOp.setTitle(boundOp.getMetadataAnchor());
        boundOp.setTarget(URI.create(entityURI.toASCIIString() + "/"
                + (useOperationFQN ? func.getFullQualifiedName().toString() : operation.name())));
      }
    }
    if (boundOp == null) {
      throw new IllegalArgumentException(String.format("Could not find any matching operation '%s' bound to %s",
              operation.name(), entity.getTypeName()));
    }

    final FullQualifiedName operationFQN = boundOp.getTitle().indexOf('.') == -1
            ? new FullQualifiedName(targetFQN.getNamespace(), boundOp.getTitle())
            : new FullQualifiedName(boundOp.getTitle());

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
