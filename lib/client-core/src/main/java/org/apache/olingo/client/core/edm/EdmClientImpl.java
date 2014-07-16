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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.client.api.edm.xml.CommonParameter;
import org.apache.olingo.client.api.edm.xml.ComplexType;
import org.apache.olingo.client.api.edm.xml.EntityContainer;
import org.apache.olingo.client.api.edm.xml.EntityType;
import org.apache.olingo.client.api.edm.xml.EnumType;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.client.api.edm.xml.v4.Action;
import org.apache.olingo.client.api.edm.xml.v4.Annotatable;
import org.apache.olingo.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.client.api.edm.xml.v4.Annotations;
import org.apache.olingo.client.api.edm.xml.v4.Function;
import org.apache.olingo.client.api.edm.xml.v4.Term;
import org.apache.olingo.client.api.edm.xml.v4.TypeDefinition;
import org.apache.olingo.client.core.edm.v3.EdmActionProxy;
import org.apache.olingo.client.core.edm.v3.EdmFunctionProxy;
import org.apache.olingo.client.core.edm.v3.FunctionImportUtils;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmServiceMetadata;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.AbstractEdm;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EdmClientImpl extends AbstractEdm {

  private final ODataServiceVersion version;

  private final List<Schema> xmlSchemas;

  private final Map<String, Schema> xmlSchemaByNamespace;

  private final EdmServiceMetadata serviceMetadata;

  public EdmClientImpl(final ODataServiceVersion version, final Map<String, Schema> xmlSchemas) {
    this.version = version;

    this.xmlSchemaByNamespace = xmlSchemas;

    this.xmlSchemas = new ArrayList<Schema>();
    for (Schema schema : xmlSchemaByNamespace.values()) {
      if (!this.xmlSchemas.contains(schema)) {
        this.xmlSchemas.add(schema);
      }
    }

    this.serviceMetadata = AbstractEdmServiceMetadataImpl.getInstance(version, this.xmlSchemas);
  }

  @Override
  protected EdmServiceMetadata createServiceMetadata() {
    return serviceMetadata;
  }

  @Override
  protected Map<String, String> createAliasToNamespaceInfo() {
    final Map<String, String> aliasToNamespace = new HashMap<String, String>();

    for (Schema schema : xmlSchemas) {
      aliasToNamespace.put(null, schema.getNamespace());
      if (StringUtils.isNotBlank(schema.getAlias())) {
        aliasToNamespace.put(schema.getAlias(), schema.getNamespace());
      }
    }

    return aliasToNamespace;
  }

  @Override
  protected Map<String, EdmSchema> createSchemas() {
    final Map<String, EdmSchema> _schemas = new LinkedHashMap<String, EdmSchema>(xmlSchemas.size());
    for (Schema schema : xmlSchemas) {
      _schemas.put(schema.getNamespace(), new EdmSchemaImpl(version, this, xmlSchemas, schema));
    }
    return _schemas;
  }

  @Override
  protected EdmEntityContainer createEntityContainer(final FullQualifiedName containerName) {
    EdmEntityContainer result = null;

    final Schema schema = xmlSchemaByNamespace.get(containerName.getNamespace());
    if (schema != null) {
      final EntityContainer xmlEntityContainer = schema.getDefaultEntityContainer();
      if (xmlEntityContainer != null) {
        result = new EdmEntityContainerImpl(this, containerName, xmlEntityContainer, xmlSchemas);
      }
    }

    return result;
  }

  @Override
  protected EdmEnumType createEnumType(final FullQualifiedName enumName) {
    EdmEnumType result = null;

    final Schema schema = xmlSchemaByNamespace.get(enumName.getNamespace());
    if (schema != null) {
      final EnumType xmlEnumType = schema.getEnumType(enumName.getName());
      if (xmlEnumType != null) {
        result = new EdmEnumTypeImpl(version, this, enumName, xmlEnumType);
      }
    }

    return result;
  }

  @Override
  protected EdmTypeDefinition createTypeDefinition(final FullQualifiedName typeDefinitionName) {
    EdmTypeDefinition result = null;

    final Schema schema = xmlSchemaByNamespace.get(typeDefinitionName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final TypeDefinition xmlTypeDefinition = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).
              getTypeDefinition(typeDefinitionName.getName());
      if (xmlTypeDefinition != null) {
        result = new EdmTypeDefinitionImpl(version, this, typeDefinitionName, xmlTypeDefinition);
      }
    }

    return result;
  }

  @Override
  protected EdmEntityType createEntityType(final FullQualifiedName entityTypeName) {
    EdmEntityType result = null;

    final Schema schema = xmlSchemaByNamespace.get(entityTypeName.getNamespace());
    if (schema != null) {
      final EntityType xmlEntityType = schema.getEntityType(entityTypeName.getName());
      if (xmlEntityType != null) {
        result = EdmEntityTypeImpl.getInstance(this, entityTypeName, xmlSchemas, xmlEntityType);
      }
    }

    return result;
  }

  @Override
  protected EdmComplexType createComplexType(final FullQualifiedName complexTypeName) {
    EdmComplexType result = null;

    final Schema schema = xmlSchemaByNamespace.get(complexTypeName.getNamespace());
    if (schema != null) {
      final ComplexType xmlComplexType = schema.getComplexType(complexTypeName.getName());
      if (xmlComplexType != null) {
        result = EdmComplexTypeImpl.getInstance(this, complexTypeName, xmlSchemas, xmlComplexType);
      }
    }

    return result;
  }

  @Override
  protected EdmAction createUnboundAction(final FullQualifiedName actionName) {
    EdmAction result = null;

    final Schema schema = xmlSchemaByNamespace.get(actionName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Action> actions = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).
              getActions(actionName.getName());
      boolean found = false;
      for (final Iterator<Action> itor = actions.iterator(); itor.hasNext() && !found;) {
        final Action action = itor.next();
        if (!action.isBound()) {
          found = true;
          result = EdmActionImpl.getInstance(this, actionName, action);
        }
      }
    } else {
      for (EntityContainer entityContainer : schema.getEntityContainers()) {
        @SuppressWarnings("unchecked")
        final List<FunctionImport> functionImports = (List<FunctionImport>) entityContainer.
                getFunctionImports(actionName.getName());
        boolean found = false;
        for (final Iterator<FunctionImport> itor = functionImports.iterator(); itor.hasNext() && !found;) {
          final FunctionImport functionImport = itor.next();
          if (!FunctionImportUtils.canProxyFunction(functionImport) && !functionImport.isBindable()) {
            found = functionImport.getParameters().isEmpty();
            result = EdmActionProxy.getInstance(this, actionName, functionImport);
          }
        }
      }
    }

    return result;
  }

  @Override
  protected List<EdmFunction> createUnboundFunctions(final FullQualifiedName functionName) {
    final List<EdmFunction> result = new ArrayList<EdmFunction>();

    final Schema schema = xmlSchemaByNamespace.get(functionName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Function> functions = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).
              getFunctions(functionName.getName());
      for (final Iterator<Function> itor = functions.iterator(); itor.hasNext();) {
        final Function function = itor.next();
        if (!function.isBound()) {
          result.add(EdmFunctionImpl.getInstance(this, functionName, function));
        }
      }
    } else {
      for (EntityContainer entityContainer : schema.getEntityContainers()) {
        @SuppressWarnings("unchecked")
        final List<FunctionImport> functionImports =
                (List<FunctionImport>) entityContainer.getFunctionImports(functionName.getName());
        for (final Iterator<FunctionImport> itor = functionImports.iterator(); itor.hasNext();) {
          final FunctionImport functionImport = itor.next();
          if (FunctionImportUtils.canProxyFunction(functionImport) && !functionImport.isBindable()) {
            result.add(EdmFunctionProxy.getInstance(this, functionName, functionImport));
          }
        }
      }
    }

    return result;
  }

  @Override
  protected EdmFunction createUnboundFunction(final FullQualifiedName functionName, final List<String> parameterNames) {
    EdmFunction result = null;

    final Schema schema = xmlSchemaByNamespace.get(functionName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Function> functions = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).
              getFunctions(functionName.getName());
      boolean found = false;
      for (final Iterator<Function> itor = functions.iterator(); itor.hasNext() && !found;) {
        final Function function = itor.next();
        if (!function.isBound()) {
          final Set<String> functionParamNames = new HashSet<String>();
          for (CommonParameter param : function.getParameters()) {
            functionParamNames.add(param.getName());
          }
          found = parameterNames == null
                  ? functionParamNames.isEmpty()
                  : functionParamNames.containsAll(parameterNames);
          result = EdmFunctionImpl.getInstance(this, functionName, function);
        }
      }
    } else {
      for (EntityContainer entityContainer : schema.getEntityContainers()) {
        @SuppressWarnings("unchecked")
        final List<FunctionImport> functionImports =
                (List<FunctionImport>) entityContainer.getFunctionImports(functionName.getName());
        boolean found = false;
        for (final Iterator<FunctionImport> itor = functionImports.iterator(); itor.hasNext() && !found;) {
          final FunctionImport functionImport = itor.next();
          if (FunctionImportUtils.canProxyFunction(functionImport) && !functionImport.isBindable()) {
            final Set<String> functionParamNames = new HashSet<String>();
            for (CommonParameter param : functionImport.getParameters()) {
              functionParamNames.add(param.getName());
            }
            found = parameterNames == null
                    ? functionParamNames.isEmpty()
                    : functionParamNames.containsAll(parameterNames);
            result = EdmFunctionProxy.getInstance(this, functionName, functionImport);
          }
        }
      }
    }

    return result;
  }

  @Override
  protected EdmAction createBoundAction(final FullQualifiedName actionName,
          final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection) {

    EdmAction result = null;

    final Schema schema = xmlSchemaByNamespace.get(actionName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Action> actions =
              ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getActions(actionName.getName());
      boolean found = false;
      for (final Iterator<Action> itor = actions.iterator(); itor.hasNext() && !found;) {
        final Action action = itor.next();
        if (action.isBound()) {
          final EdmTypeInfo boundParam = new EdmTypeInfo.Builder().setEdm(this).
                  setTypeExpression(action.getParameters().get(0).getType()).build();
          if (bindingParameterTypeName.equals(boundParam.getFullQualifiedName())
                  && (isBindingParameterCollection == null
                  || isBindingParameterCollection.booleanValue() == boundParam.isCollection())) {

            found = true;
            result = EdmActionImpl.getInstance(this, actionName, action);
          }
        }
      }
    } else {
      for (EntityContainer entityContainer : schema.getEntityContainers()) {
        @SuppressWarnings("unchecked")
        final List<FunctionImport> functionImports =
                (List<FunctionImport>) entityContainer.getFunctionImports(actionName.getName());
        boolean found = false;
        for (final Iterator<FunctionImport> itor = functionImports.iterator(); itor.hasNext() && !found;) {
          final FunctionImport functionImport = itor.next();
          if (!FunctionImportUtils.canProxyFunction(functionImport) && functionImport.isBindable()) {
            final EdmTypeInfo boundParam = new EdmTypeInfo.Builder().setEdm(this).
                    setTypeExpression(functionImport.getParameters().get(0).getType()).build();
            if (bindingParameterTypeName.equals(boundParam.getFullQualifiedName())
                    && (isBindingParameterCollection == null
                    || isBindingParameterCollection.booleanValue() == boundParam.isCollection())) {

              found = true;
              result = EdmActionProxy.getInstance(this, actionName, functionImport);
            }
          }
        }
      }
    }

    return result;
  }

  @Override
  protected EdmFunction createBoundFunction(final FullQualifiedName functionName,
          final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection,
          final List<String> parameterNames) {

    EdmFunction result = null;

    final Schema schema = xmlSchemaByNamespace.get(functionName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Function> functions = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).
              getFunctions(functionName.getName());
      boolean found = false;
      for (final Iterator<Function> itor = functions.iterator(); itor.hasNext() && !found;) {
        final Function function = itor.next();
        if (function.isBound()) {
          final EdmTypeInfo boundParam = new EdmTypeInfo.Builder().setEdm(this).
                  setTypeExpression(function.getParameters().get(0).getType()).build();
          if (bindingParameterTypeName.equals(boundParam.getFullQualifiedName())
                  && (isBindingParameterCollection == null
                  || isBindingParameterCollection.booleanValue() == boundParam.isCollection())) {

            final Set<String> functionParamNames = new HashSet<String>();
            for (CommonParameter param : function.getParameters()) {
              functionParamNames.add(param.getName());
            }
            found = parameterNames == null
                    ? functionParamNames.isEmpty()
                    : functionParamNames.containsAll(parameterNames);
            result = EdmFunctionImpl.getInstance(this, functionName, function);
          }
        }
      }
    } else {
      for (EntityContainer entityContainer : schema.getEntityContainers()) {
        @SuppressWarnings("unchecked")
        final List<FunctionImport> functionImports =
                (List<FunctionImport>) entityContainer.getFunctionImports(functionName.getName());
        boolean found = false;
        for (final Iterator<FunctionImport> itor = functionImports.iterator(); itor.hasNext() && !found;) {
          final FunctionImport functionImport = itor.next();
          if (FunctionImportUtils.canProxyFunction(functionImport) && functionImport.isBindable()) {
            final EdmTypeInfo boundParam = new EdmTypeInfo.Builder().setEdm(this).
                    setTypeExpression(functionImport.getParameters().get(0).getType()).build();
            if (bindingParameterTypeName.equals(boundParam.getFullQualifiedName())
                    && (isBindingParameterCollection == null
                    || isBindingParameterCollection.booleanValue() == boundParam.isCollection())) {

              final Set<String> functionParamNames = new HashSet<String>();
              for (CommonParameter param : functionImport.getParameters()) {
                functionParamNames.add(param.getName());
              }
              found = parameterNames == null
                      ? functionParamNames.isEmpty()
                      : functionParamNames.containsAll(parameterNames);
              result = EdmFunctionProxy.getInstance(this, functionName, functionImport);
            }
          }
        }
      }
    }

    return result;
  }

  @Override
  protected EdmTerm createTerm(final FullQualifiedName termName) {
    EdmTerm result = null;

    final Schema schema = xmlSchemaByNamespace.get(termName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final Term term = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getTerm(termName.getName());
      if (term != null) {
        result = new EdmTermImpl(this, schema.getNamespace(), term);
      }
    }

    return result;
  }

  @Override
  protected EdmAnnotations createAnnotationGroup(final FullQualifiedName targetName) {
    EdmAnnotationsImpl result = null;

    final Schema schema = xmlSchemaByNamespace.get(targetName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final Annotations annotationGroup =
              ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getAnnotationGroup(targetName.getName());
      if (annotationGroup != null) {
        result = new EdmAnnotationsImpl(this, schemas.get(schema.getNamespace()), annotationGroup);
      }
    }

    return result;
  }

  @Override
  protected List<EdmAnnotation> createAnnotations(final FullQualifiedName annotatedName) {
    List<EdmAnnotation> result = null;

    final Schema schema = xmlSchemaByNamespace.get(annotatedName.getNamespace());
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final Annotatable annotatable =
              ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getAnnotatables().get(annotatedName.getName());
      if (annotatable != null && annotatable.getAnnotations() != null) {
        result = new ArrayList<EdmAnnotation>();
        for (Annotation annotation : annotatable.getAnnotations()) {
          final EdmTerm term = getTerm(new FullQualifiedName(annotation.getTerm()));
          result.add(new EdmAnnotationImpl(this, annotation));
        }
      }
    }

    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
