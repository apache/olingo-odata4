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
package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractEdm implements Edm {

  protected Map<String, EdmSchema> schemas;

  protected List<EdmSchema> schemaList;

  private final Map<FullQualifiedName, EdmEntityContainer> entityContainers =
      new HashMap<FullQualifiedName, EdmEntityContainer>();

  private final Map<FullQualifiedName, EdmEnumType> enumTypes = new HashMap<FullQualifiedName, EdmEnumType>();

  private final Map<FullQualifiedName, EdmTypeDefinition> typeDefinitions =
      new HashMap<FullQualifiedName, EdmTypeDefinition>();

  private final Map<FullQualifiedName, EdmEntityType> entityTypes = new HashMap<FullQualifiedName, EdmEntityType>();

  private final Map<FullQualifiedName, EdmComplexType> complexTypes = new HashMap<FullQualifiedName, EdmComplexType>();

  private final Map<FullQualifiedName, EdmAction> unboundActions = new HashMap<FullQualifiedName, EdmAction>();

  private final Map<FullQualifiedName, List<EdmFunction>> unboundFunctionsByName =
      new HashMap<FullQualifiedName, List<EdmFunction>>();

  private final Map<FunctionMapKey, EdmFunction> unboundFunctionsByKey = new HashMap<FunctionMapKey, EdmFunction>();

  private final Map<ActionMapKey, EdmAction> boundActions = new HashMap<ActionMapKey, EdmAction>();

  private final Map<FunctionMapKey, EdmFunction> boundFunctions = new HashMap<FunctionMapKey, EdmFunction>();

  private final Map<FullQualifiedName, EdmTerm> terms = new HashMap<FullQualifiedName, EdmTerm>();

  private final Map<FullQualifiedName, EdmAnnotations> annotationGroups =
      new HashMap<FullQualifiedName, EdmAnnotations>();

  private final Map<FullQualifiedName, List<EdmAnnotation>> annotations =
      new HashMap<FullQualifiedName, List<EdmAnnotation>>();

  private Map<String, String> aliasToNamespaceInfo;

  @Override
  public List<EdmSchema> getSchemas() {
    initSchemas();
    return schemaList;
  }

  @Override
  public EdmSchema getSchema(final String namespace) {
    initSchemas();

    EdmSchema schema = schemas.get(namespace);
    if (schema == null) {
      if (aliasToNamespaceInfo == null) {
        aliasToNamespaceInfo = createAliasToNamespaceInfo();
      }
      schema = schemas.get(aliasToNamespaceInfo.get(namespace));
    }

    return schema;
  }

  private void initSchemas() {
    if (schemas == null) {
      schemas = createSchemas();
      if (schemas != null) {
        schemaList = Collections.unmodifiableList(new ArrayList<EdmSchema>(schemas.values()));
        aliasToNamespaceInfo = new HashMap<String, String>();
        for (EdmSchema schema : schemas.values()) {
          final String namespace = schema.getNamespace();

          if (schema.getAlias() != null) {
            aliasToNamespaceInfo.put(schema.getAlias(), namespace);
          }

          final List<EdmEnumType> localEnumTypes = schema.getEnumTypes();
          if (localEnumTypes != null) {
            for (EdmEnumType enumType : localEnumTypes) {
              enumTypes.put(new FullQualifiedName(namespace, enumType.getName()), enumType);
            }
          }

          final List<EdmTypeDefinition> localTypeDefinitions = schema.getTypeDefinitions();
          if (localTypeDefinitions != null) {
            for (EdmTypeDefinition typeDef : localTypeDefinitions) {
              typeDefinitions.put(new FullQualifiedName(namespace, typeDef.getName()), typeDef);
            }
          }

          final List<EdmComplexType> localComplexTypes = schema.getComplexTypes();
          if (localComplexTypes != null) {
            for (EdmComplexType complexType : localComplexTypes) {
              complexTypes.put(new FullQualifiedName(namespace, complexType.getName()), complexType);
            }
          }

          List<EdmEntityType> localEntityTypes = schema.getEntityTypes();
          if (localEntityTypes != null) {
            for (EdmEntityType entityType : localEntityTypes) {
              entityTypes.put(new FullQualifiedName(namespace, entityType.getName()), entityType);
            }
          }

          final List<EdmAction> localActions = schema.getActions();
          if (localActions != null) {
            for (EdmAction action : localActions) {
              final FullQualifiedName name = new FullQualifiedName(namespace, action.getName());
              if (action.isBound()) {
                final ActionMapKey key = new ActionMapKey(name,
                    action.getBindingParameterTypeFqn(), action.isBindingParameterTypeCollection());
                boundActions.put(key, action);
              } else {
                unboundActions.put(name, action);
              }
            }
          }

          final List<EdmFunction> localFunctions = schema.getFunctions();
          if (localFunctions != null) {
            for (EdmFunction function : localFunctions) {
              final FullQualifiedName name = new FullQualifiedName(namespace, function.getName());
              final FunctionMapKey key = new FunctionMapKey(name,
                  function.getBindingParameterTypeFqn(), function.isBindingParameterTypeCollection(),
                  function.getParameterNames());

              if (function.isBound()) {
                boundFunctions.put(key, function);
              } else {
                if (!unboundFunctionsByName.containsKey(name)) {
                  unboundFunctionsByName.put(name, new ArrayList<EdmFunction>());
                }
                unboundFunctionsByName.get(name).add(function);

                unboundFunctionsByKey.put(key, function);
              }
            }
          }

          final EdmEntityContainer entityContainer = schema.getEntityContainer();
          if (entityContainer != null) {
            entityContainers.put(new FullQualifiedName(namespace, entityContainer.getName()), entityContainer);
            if (!entityContainers.containsKey(null)) {
              entityContainers.put(null, entityContainer);
            }
          }
        }
      }
    }
  }

  @Override
  public EdmEntityContainer getEntityContainer(final FullQualifiedName namespaceOrAliasFQN) {
    final FullQualifiedName fqn = resolvePossibleAlias(namespaceOrAliasFQN);
    EdmEntityContainer container = entityContainers.get(fqn);
    if (container == null) {
      container = createEntityContainer(fqn);
      if (container != null) {
        entityContainers.put(fqn, container);
        if (fqn == null) {
          entityContainers.put(new FullQualifiedName(container.getNamespace(), container.getName()), container);
        }
      }
    }
    return container;
  }

  @Override
  public EdmEnumType getEnumType(final FullQualifiedName namespaceOrAliasFQN) {
    final FullQualifiedName fqn = resolvePossibleAlias(namespaceOrAliasFQN);
    EdmEnumType enumType = enumTypes.get(fqn);
    if (enumType == null) {
      enumType = createEnumType(fqn);
      if (enumType != null) {
        enumTypes.put(fqn, enumType);
      }
    }
    return enumType;
  }

  @Override
  public EdmTypeDefinition getTypeDefinition(final FullQualifiedName namespaceOrAliasFQN) {
    final FullQualifiedName fqn = resolvePossibleAlias(namespaceOrAliasFQN);
    EdmTypeDefinition typeDefinition = typeDefinitions.get(fqn);
    if (typeDefinition == null) {
      typeDefinition = createTypeDefinition(fqn);
      if (typeDefinition != null) {
        typeDefinitions.put(fqn, typeDefinition);
      }
    }
    return typeDefinition;
  }

  @Override
  public EdmEntityType getEntityType(final FullQualifiedName namespaceOrAliasFQN) {
    final FullQualifiedName fqn = resolvePossibleAlias(namespaceOrAliasFQN);
    EdmEntityType entityType = entityTypes.get(fqn);
    if (entityType == null) {
      entityType = createEntityType(fqn);
      if (entityType != null) {
        entityTypes.put(fqn, entityType);
      }
    }
    return entityType;
  }

  @Override
  public EdmComplexType getComplexType(final FullQualifiedName namespaceOrAliasFQN) {
    final FullQualifiedName fqn = resolvePossibleAlias(namespaceOrAliasFQN);
    EdmComplexType complexType = complexTypes.get(fqn);
    if (complexType == null) {
      complexType = createComplexType(fqn);
      if (complexType != null) {
        complexTypes.put(fqn, complexType);
      }
    }
    return complexType;
  }

  @Override
  public EdmAction getUnboundAction(final FullQualifiedName actionName) {
    EdmAction action = unboundActions.get(actionName);
    if (action == null) {
      action = createUnboundAction(resolvePossibleAlias(actionName));
      if (action != null) {
        unboundActions.put(actionName, action);
      }
    }

    return action;
  }

  @Override
  public EdmAction getBoundAction(final FullQualifiedName actionName,
      final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection) {

    final FullQualifiedName actionFqn = resolvePossibleAlias(actionName);
    final FullQualifiedName bindingParameterTypeFqn = resolvePossibleAlias(bindingParameterTypeName);
    final ActionMapKey key = new ActionMapKey(actionFqn, bindingParameterTypeFqn, isBindingParameterCollection);
    EdmAction action = boundActions.get(key);
    if (action == null) {
      action = createBoundAction(actionFqn, bindingParameterTypeFqn, isBindingParameterCollection);
      if (action != null) {
        boundActions.put(key, action);
      }
    }

    return action;
  }

  @Override
  public List<EdmFunction> getUnboundFunctions(final FullQualifiedName functionName) {
    final FullQualifiedName functionFqn = resolvePossibleAlias(functionName);

    List<EdmFunction> functions = unboundFunctionsByName.get(functionFqn);
    if (functions == null) {
      functions = createUnboundFunctions(functionFqn);
      if (functions != null) {
        unboundFunctionsByName.put(functionFqn, functions);

        for (EdmFunction unbound : functions) {
          final FunctionMapKey key = new FunctionMapKey(
              new FullQualifiedName(unbound.getNamespace(), unbound.getName()),
              unbound.getBindingParameterTypeFqn(),
              unbound.isBindingParameterTypeCollection(),
              unbound.getParameterNames());
          unboundFunctionsByKey.put(key, unbound);
        }
      }
    }

    return functions;
  }

  @Override
  public EdmFunction getUnboundFunction(final FullQualifiedName functionName, final List<String> parameterNames) {
    final FullQualifiedName functionFqn = resolvePossibleAlias(functionName);

    final FunctionMapKey key = new FunctionMapKey(functionFqn, null, null, parameterNames);
    EdmFunction function = unboundFunctionsByKey.get(key);
    if (function == null) {
      function = createUnboundFunction(functionFqn, parameterNames);
      if (function != null) {
        unboundFunctionsByKey.put(key, function);
      }
    }

    return function;
  }

  @Override
  public EdmFunction getBoundFunction(final FullQualifiedName functionName,
      final FullQualifiedName bindingParameterTypeName,
      final Boolean isBindingParameterCollection, final List<String> parameterNames) {

    final FullQualifiedName functionFqn = resolvePossibleAlias(functionName);
    final FullQualifiedName bindingParameterTypeFqn = resolvePossibleAlias(bindingParameterTypeName);
    final FunctionMapKey key =
        new FunctionMapKey(functionFqn, bindingParameterTypeFqn, isBindingParameterCollection, parameterNames);
    EdmFunction function = boundFunctions.get(key);
    if (function == null) {
      function = createBoundFunction(functionFqn, bindingParameterTypeFqn, isBindingParameterCollection,
          parameterNames);
      if (function != null) {
        boundFunctions.put(key, function);
      }
    }

    return function;
  }

  @Override
  public EdmTerm getTerm(final FullQualifiedName termName) {
    final FullQualifiedName fqn = resolvePossibleAlias(termName);
    EdmTerm term = terms.get(fqn);
    if (term == null) {
      term = createTerm(fqn);
      if (term != null) {
        terms.put(fqn, term);
      }
    }
    return term;
  }

  @Override
  public EdmAnnotations getAnnotationGroup(final FullQualifiedName targetName) {
    EdmAnnotations _annotations = annotationGroups.get(targetName);
    if (_annotations == null) {
      _annotations = createAnnotationGroup(targetName);
      if (_annotations != null) {
        annotationGroups.put(targetName, _annotations);
      }
    }
    return _annotations;
  }

  @Override
  public List<EdmAnnotation> getAnnotations(final FullQualifiedName annotatableName) {
    final FullQualifiedName fqn = resolvePossibleAlias(annotatableName);
    List<EdmAnnotation> _annotations = annotations.get(fqn);
    if (_annotations == null) {
      _annotations = createAnnotations(fqn);
      if (_annotations != null) {
        annotations.put(fqn, _annotations);
      }
    }
    return _annotations;
  }

  @Override
  public EdmAnnotation getAnnotation(final FullQualifiedName annotatableName, final EdmTerm term) {
    final List<EdmAnnotation> _annotations = getAnnotations(annotatableName);
    EdmAnnotation result = null;
    if (_annotations != null) {
      for (EdmAnnotation annotation : _annotations) {
        if (term.getFullQualifiedName().equals(annotation.getTerm().getFullQualifiedName())) {
          result = annotation;
        }
      }
    }
    return result;
  }

  private FullQualifiedName resolvePossibleAlias(final FullQualifiedName namespaceOrAliasFQN) {
    if (aliasToNamespaceInfo == null) {
      aliasToNamespaceInfo = createAliasToNamespaceInfo();
    }
    FullQualifiedName finalFQN = null;
    if (namespaceOrAliasFQN != null) {
      final String namespace = aliasToNamespaceInfo.get(namespaceOrAliasFQN.getNamespace());
      // If not contained in info it must be a namespace
      if (namespace == null) {
        finalFQN = namespaceOrAliasFQN;
      } else {
        finalFQN = new FullQualifiedName(namespace, namespaceOrAliasFQN.getName());
      }
    }
    return finalFQN;
  }

  protected abstract Map<String, EdmSchema> createSchemas();

  protected abstract Map<String, String> createAliasToNamespaceInfo();

  protected abstract EdmEntityContainer createEntityContainer(FullQualifiedName containerName);

  protected abstract EdmEnumType createEnumType(FullQualifiedName enumName);

  protected abstract EdmTypeDefinition createTypeDefinition(FullQualifiedName typeDefinitionName);

  protected abstract EdmEntityType createEntityType(FullQualifiedName entityTypeName);

  protected abstract EdmComplexType createComplexType(FullQualifiedName complexTypeName);

  protected abstract EdmAction createUnboundAction(FullQualifiedName actionName);

  protected abstract List<EdmFunction> createUnboundFunctions(FullQualifiedName functionName);

  protected abstract EdmFunction createUnboundFunction(FullQualifiedName functionName, List<String> parameterNames);

  protected abstract EdmAction createBoundAction(FullQualifiedName actionName,
      FullQualifiedName bindingParameterTypeName,
      Boolean isBindingParameterCollection);

  protected abstract EdmFunction createBoundFunction(FullQualifiedName functionName,
      FullQualifiedName bindingParameterTypeName, Boolean isBindingParameterCollection,
      List<String> parameterNames);

  protected abstract EdmTerm createTerm(FullQualifiedName termName);

  protected abstract EdmAnnotations createAnnotationGroup(FullQualifiedName targetName);

  protected abstract List<EdmAnnotation> createAnnotations(FullQualifiedName annotatedName);
}
