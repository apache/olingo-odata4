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
package org.apache.olingo.odata4.client.core.edm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.odata4.client.api.edm.xml.CommonParameter;
import org.apache.olingo.odata4.client.api.edm.xml.EnumType;
import org.apache.olingo.odata4.client.api.edm.xml.Schema;
import org.apache.olingo.odata4.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.odata4.client.api.edm.xml.v4.ComplexType;
import org.apache.olingo.odata4.client.api.edm.xml.v4.EntityContainer;
import org.apache.olingo.odata4.client.api.edm.xml.EntityType;
import org.apache.olingo.odata4.client.api.edm.xml.v4.TypeDefinition;
import org.apache.olingo.odata4.client.api.utils.EdmTypeInfo;
import org.apache.olingo.odata4.client.core.edm.xml.v4.ActionImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v4.FunctionImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v4.SchemaImpl;
import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmEnumType;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmServiceMetadata;
import org.apache.olingo.odata4.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.AbstractEdmImpl;

public class EdmClientImpl extends AbstractEdmImpl {

  private final XMLMetadata xmlMetadata;

  private final EdmServiceMetadata serviceMetadata;

  public EdmClientImpl(final XMLMetadata xmlMetadata) {
    this.xmlMetadata = xmlMetadata;
    this.serviceMetadata = AbstractEdmServiceMetadataImpl.getInstance(xmlMetadata);
  }

  public XMLMetadata getXMLMetadata() {
    return xmlMetadata;
  }

  @Override
  protected EdmServiceMetadata createServiceMetadata() {
    return serviceMetadata;
  }

  @Override
  protected Map<String, String> createAliasToNamespaceInfo() {
    final Map<String, String> aliasToNamespace = new HashMap<String, String>();

    for (Schema schema : xmlMetadata.getSchemas()) {
      aliasToNamespace.put(null, schema.getNamespace());
      if (StringUtils.isNotBlank(schema.getAlias())) {
        aliasToNamespace.put(schema.getAlias(), schema.getNamespace());
      }
    }

    return aliasToNamespace;
  }

  @Override
  protected EdmEntityContainer createEntityContainer(final FullQualifiedName containerName) {
    EdmEntityContainer result = null;

    final Schema schema = xmlMetadata.getSchema(containerName.getNamespace());
    if (schema != null) {
      final EntityContainer xmlEntityContainer = (EntityContainer) schema.getDefaultEntityContainer();
      if (xmlEntityContainer != null) {
        result = new EdmEntityContainerImpl(this, containerName, xmlEntityContainer);
      }
    }

    return result;
  }

  @Override
  protected EdmEnumType createEnumType(final FullQualifiedName enumName) {
    EdmEnumType result = null;

    final Schema schema = xmlMetadata.getSchema(enumName.getNamespace());
    if (schema != null) {
      final EnumType xmlEnumType = schema.getEnumType(enumName.getName());
      if (xmlEnumType != null) {
        result = new EdmEnumTypeImpl(this, enumName, xmlEnumType);
      }
    }

    return result;
  }

  @Override
  protected EdmTypeDefinition createTypeDefinition(final FullQualifiedName typeDefinitionName) {
    EdmTypeDefinition result = null;

    final Schema schema = xmlMetadata.getSchema(typeDefinitionName.getNamespace());
    if (schema instanceof SchemaImpl) {
      final TypeDefinition xmlTypeDefinition = ((SchemaImpl) schema).getTypeDefinition(typeDefinitionName.getName());
      if (xmlTypeDefinition != null) {
        result = new EdmTypeDefinitionImpl(this, typeDefinitionName, xmlTypeDefinition);
      }
    }

    return result;
  }

  @Override
  protected EdmEntityType createEntityType(final FullQualifiedName entityTypeName) {
    EdmEntityType result = null;

    final Schema schema = xmlMetadata.getSchema(entityTypeName.getNamespace());
    final EntityType xmlEntityType = schema.getEntityType(entityTypeName.getName());
    if (xmlEntityType != null) {
      result = EdmEntityTypeImpl.getInstance(this, entityTypeName, xmlEntityType);
    }

    return result;
  }

  @Override
  protected EdmComplexType createComplexType(final FullQualifiedName complexTypeName) {
    EdmComplexType result = null;

    final Schema schema = xmlMetadata.getSchema(complexTypeName.getNamespace());
    if (schema instanceof SchemaImpl) {
      final ComplexType xmlComplexType = ((SchemaImpl) schema).getComplexType(complexTypeName.getName());
      if (xmlComplexType != null) {
        result = EdmComplexTypeImpl.getInstance(this, complexTypeName, xmlComplexType);
      }
    }

    return result;
  }

  @Override
  protected EdmAction createUnboundAction(final FullQualifiedName actionName) {
    EdmAction result = null;

    final Schema schema = xmlMetadata.getSchema(actionName.getNamespace());
    if (schema instanceof SchemaImpl) {
      final List<ActionImpl> actions = ((SchemaImpl) schema).getActions(actionName.getName());
      boolean found = false;
      for (Iterator<ActionImpl> itor = actions.iterator(); itor.hasNext() && !found;) {
        final ActionImpl action = itor.next();
        if (!action.isBound()) {
          found = true;
          result = EdmActionImpl.getInstance(this, actionName, action);
        }
      }
    }

    return result;
  }

  @Override
  protected EdmFunction createUnboundFunction(final FullQualifiedName functionName, final List<String> parameterNames) {
    EdmFunction result = null;

    final Schema schema = xmlMetadata.getSchema(functionName.getNamespace());
    if (schema instanceof SchemaImpl) {
      final List<FunctionImpl> functions = ((SchemaImpl) schema).getFunctions(functionName.getName());
      boolean found = false;
      for (Iterator<FunctionImpl> itor = functions.iterator(); itor.hasNext() && !found;) {
        final FunctionImpl function = itor.next();
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
    }

    return result;
  }

  @Override
  protected EdmAction createBoundAction(final FullQualifiedName actionName,
          final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection) {

    EdmAction result = null;

    final Schema schema = xmlMetadata.getSchema(actionName.getNamespace());
    if (schema instanceof SchemaImpl) {
      final List<ActionImpl> actions = ((SchemaImpl) schema).getActions(actionName.getName());
      boolean found = false;
      for (Iterator<ActionImpl> itor = actions.iterator(); itor.hasNext() && !found;) {
        final ActionImpl action = itor.next();
        if (action.isBound()) {
          final EdmTypeInfo boundParam = new EdmTypeInfo(action.getParameters().get(0).getType());
          if (bindingParameterTypeName.equals(boundParam.getFullQualifiedName())
                  && isBindingParameterCollection.booleanValue() == boundParam.isCollection()) {

            found = true;
            result = EdmActionImpl.getInstance(this, actionName, action);
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

    final Schema schema = xmlMetadata.getSchema(functionName.getNamespace());
    if (schema instanceof SchemaImpl) {
      final List<FunctionImpl> functions = ((SchemaImpl) schema).getFunctions(functionName.getName());
      boolean found = false;
      for (Iterator<FunctionImpl> itor = functions.iterator(); itor.hasNext() && !found;) {
        final FunctionImpl function = itor.next();
        if (function.isBound()) {
          final EdmTypeInfo boundParam = new EdmTypeInfo(function.getParameters().get(0).getType());
          if (bindingParameterTypeName.equals(boundParam.getFullQualifiedName())
                  && isBindingParameterCollection.booleanValue() == boundParam.isCollection()) {

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
