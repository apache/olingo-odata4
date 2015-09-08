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
package org.apache.olingo.ext.pojogen;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

public abstract class AbstractUtility {

  protected static final String FC_TARGET_PATH = "fcTargetPath";

  protected static final String FC_SOURCE_PATH = "fcSourcePath";

  protected static final String FC_KEEP_IN_CONTENT = "fcKeepInContent";

  protected static final String FC_CONTENT_KIND = "fcContentKind";

  protected static final String FC_NS_PREFIX = "fcNSPrefix";

  protected static final String FC_NS_URI = "fcNSURI";

  protected static final String TYPE_SUB_PKG = "types";

  protected final String basePackage;

  protected final String schemaName;

  protected final String namespace;

  private final Edm edm;

  private final EdmSchema schema;

  protected final Map<String, List<EdmEntityType>> allEntityTypes = new HashMap<String, List<EdmEntityType>>();

  public AbstractUtility(final Edm metadata, final EdmSchema schema, final String basePackage) {
    this.basePackage = basePackage;
    this.schemaName = schema.getAlias();
    this.namespace = schema.getNamespace();
    this.edm = metadata;
    this.schema = schema;

    collectEntityTypes();
  }

  public EdmTypeInfo getEdmTypeInfo(final EdmType type) {
    return new EdmTypeInfo.Builder().setEdm(edm).
            setTypeExpression(type.getFullQualifiedName().toString()).build();
  }

  public EdmTypeInfo getEdmTypeInfo(final String expression) {
    return new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(expression).build();
  }

  public EdmTypeInfo getEdmType(final EdmSingleton singleton) {
    return getEdmTypeInfo(singleton.getEntityType().getFullQualifiedName().toString());
  }

  public EdmTypeInfo getEdmType(final EdmNavigationProperty navProp) {
    return getEdmTypeInfo(navProp.getType().getFullQualifiedName().toString());
  }

  public boolean isComplex(final FullQualifiedName fqn) {
    return edm.getComplexType(fqn) != null;
  }

  public Map<String, String> getEntityKeyType(final EdmSingleton singleton) {
    return getEntityKeyType(singleton.getEntityType());
  }

  public Map<String, String> getEntityKeyType(final EdmNavigationProperty navProp) {
    return getEntityKeyType(navProp.getType());
  }

  protected Edm getMetadata() {
    return edm;
  }

  protected EdmSchema getSchema() {
    return schema;
  }

  public String getNavigationType(final EdmNavigationProperty property) {
    return property.getType().getFullQualifiedName().toString();
  }

  public NavPropertyBindingDetails getNavigationBindingDetails(
          final EdmStructuredType sourceEntityType, final EdmNavigationProperty property) {

    if (property.containsTarget()) {
      return new NavPropertyContainsTarget(edm, property.getType());
    }

    try {
      return getNavigationBindings(sourceEntityType, property);
    } catch (Exception e) {
      // maybe source entity type without entity set ...
      return getNavigationBindings(property.getType(), property.getName());
    }
  }

  private NavPropertyBindingDetails getNavigationBindings(final EdmStructuredType type, String propertyName) {
    if (type == null) {
      throw new IllegalStateException("No EntitySet defined. Invalid navigation property: " + propertyName);
    }

    try {
      return new NavPropertyBindingDetails(edm, type);
    } catch (IllegalStateException ignore) {
      return getNavigationBindings(type.getBaseType(), propertyName);
    }
  }

  private NavPropertyBindingDetails getNavigationBindings(
          final EdmStructuredType sourceEntityType, final EdmNavigationProperty property) {

    if (sourceEntityType == null) {
      throw new IllegalStateException("Invalid navigation property " + property.getName());
    }

    try {
      return new NavPropertyBindingDetails(edm, sourceEntityType, property);
    } catch (IllegalStateException ignore) {
      return getNavigationBindingDetails(sourceEntityType.getBaseType(), property);
    }
  }

  public boolean isNavigationAlreadyDeclared(final EdmStructuredType type, final EdmNavigationProperty property) {
    EdmStructuredType basetype = type.getBaseType();
    while (basetype != null) {
      if (basetype.getNavigationProperty(property.getName()) != null) {
        return true;
      } else {
        basetype = basetype.getBaseType();
      }
    }
    return false;
  }

  public String getContainedEntitySet(final EdmNavigationProperty navProp) {
    return (StringUtils.isBlank(basePackage)
            ? new StringBuilder() : new StringBuilder().append(basePackage).append('.')).
            append(navProp.getType().getFullQualifiedName().getNamespace().toLowerCase()). // namespace
            append('.').append(capitalize(navProp.getName())).toString();
  }

  public EdmFunction getFunctionByName(final FullQualifiedName name) {
    final EdmSchema targetSchema = edm.getSchema(name.getNamespace());

    if (targetSchema != null) {
      for (EdmFunction function : targetSchema.getFunctions()) {
        if (function.getName().equals(name.getName())) {
          return function;
        }
      }
    }

    return null;
  }

  public EdmAction getActionByName(final FullQualifiedName name) {
    final EdmSchema targetSchema = edm.getSchema(name.getNamespace());

    if (targetSchema != null) {
      for (EdmAction action : targetSchema.getActions()) {
        if (action.getName().equals(name.getName())) {
          return action;
        }
      }
    }

    return null;
  }

  public boolean isStreamType(final EdmType type) {
    return type != null && type.getFullQualifiedName().equals(EdmPrimitiveTypeKind.Stream.getFullQualifiedName());
  }

  public List<EdmFunction> getFunctionsBoundTo(final String typeExpression, final boolean collection) {
    final List<EdmFunction> result = new ArrayList<EdmFunction>();

    for (EdmSchema sch : getMetadata().getSchemas()) {
      for (EdmFunction function : sch.getFunctions()) {
        if (function.isBound()) {
          final EdmParameter bound = function.getParameterNames().isEmpty()
                  ? null : function.getParameter(function.getParameterNames().get(0));

          if (bound != null
                  && isSameType(typeExpression, bound.getType().getFullQualifiedName().toString(), false)
                  && ((bound.isCollection() && collection) || (!bound.isCollection() && !collection))) {
            result.add(function);
          }
        }
      }
    }

    return result;
  }

  public List<EdmOperation> justInheritedOperationsBoundTo(final EdmEntityType entity) {
    final List<EdmOperation> result = new ArrayList<EdmOperation>();
    if (entity.getBaseType() != null) {
      result.addAll(getFunctionsBoundTo(entity.getBaseType().getName(), false));
      result.addAll(getActionsBoundTo(entity.getBaseType().getName(), false));
      result.addAll(justInheritedOperationsBoundTo(entity.getBaseType()));
    }

    return result;
  }

  public List<EdmAction> getActionsBoundTo(final String typeExpression, final boolean collection) {
    final List<EdmAction> result = new ArrayList<EdmAction>();

    for (EdmSchema sch : getMetadata().getSchemas()) {
      for (EdmAction action : sch.getActions()) {
        if (action.isBound()) {
          final EdmParameter bound = action.getParameterNames().isEmpty()
                  ? null : action.getParameter(action.getParameterNames().get(0));

          if (bound != null
                  && isSameType(typeExpression, bound.getType().getFullQualifiedName().toString(), false)
                  && ((bound.isCollection() && collection) || (!bound.isCollection() && !collection))) {
            result.add(action);
          }
        }
      }
    }

    return result;
  }

  private void collectEntityTypes() {
    for (EdmSchema _schema : getMetadata().getSchemas()) {
      allEntityTypes.put(_schema.getNamespace(), new ArrayList<EdmEntityType>(_schema.getEntityTypes()));
      if (StringUtils.isNotBlank(_schema.getAlias())) {
        allEntityTypes.put(_schema.getAlias(), new ArrayList<EdmEntityType>(_schema.getEntityTypes()));
      }
    }
  }

  public String getJavaType(final EdmType type, final Boolean forceCollection) {
    return getJavaType(type.getFullQualifiedName().toString(), forceCollection);
  }

  public String getJavaType(final EdmType type) {
    return getJavaType(type, false);
  }

  public String getJavaType(final EdmEntityType entityType, final Boolean forceCollection) {
    return getJavaType(entityType.getFullQualifiedName().toString(), forceCollection);
  }

  public String getJavaType(final EdmEntityType entityType) {
    return getJavaType(entityType, false);
  }

  public String getJavaType(final String typeExpression) {
    return getJavaType(typeExpression, false);
  }

  public String getJavaType(final String typeExpression, final boolean forceCollection) {
    final StringBuilder res = new StringBuilder();

    final EdmTypeInfo edmType = getEdmTypeInfo(typeExpression);

    final String basepkg = StringUtils.isBlank(basePackage) ? "" : basePackage + ".";

    if ("Edm.Stream".equals(typeExpression)) {
      res.append(InputStream.class.getName());
    } else if (edmType.isPrimitiveType()) {
      final Class<?> clazz = EdmPrimitiveTypeFactory.getInstance(edmType.getPrimitiveTypeKind()).getDefaultType();
      if (clazz.isArray()) {
        res.append(clazz.getComponentType().getName()).append("[]");
      } else {
        res.append(clazz.getName());
      }
    } else if (edmType.isComplexType()) {
      res.append(basepkg).
              append(edmType.getFullQualifiedName().getNamespace().toLowerCase()). // namespace
              append('.').append(TYPE_SUB_PKG).append('.').
              append(capitalize(edmType.getComplexType().getName())); // ComplexType capitalized name
    } else if (edmType.isEntityType()) {
      res.append(basepkg).
              append(edmType.getFullQualifiedName().getNamespace().toLowerCase()). // namespace
              append('.').append(TYPE_SUB_PKG).append('.').
              append(capitalize(edmType.getEntityType().getName())); // EntityType capitalized name
    } else if (edmType.isEnumType()) {
      res.append(basepkg).
              append(edmType.getFullQualifiedName().getNamespace().toLowerCase()). // namespace
              append('.').append(TYPE_SUB_PKG).append('.').
              append(capitalize(edmType.getEnumType().getName()));
    } else {
      throw new IllegalArgumentException("Invalid type expression '" + typeExpression + "'");
    }

    if (forceCollection || edmType.isCollection()) {
      if (edmType.isEntityType() || edmType.isComplexType()) {
        res.append("Collection");
      } else {
        res.insert(0, "org.apache.olingo.ext.proxy.api.PrimitiveCollection<").append(">");
      }
    }

    return res.toString();
  }

  public EdmTypeInfo getEdmType(final EdmEntitySet entitySet) {
    return getEdmTypeInfo(entitySet.getEntityType().getFullQualifiedName().toString());
  }

  public Map<String, String> getEntityKeyType(final EdmEntitySet entitySet) {
    return getEntityKeyType(getEdmType(entitySet).getEntityType());
  }

  public Map<String, String> getEntityKeyType(final EdmEntityType entityType) {
    EdmEntityType baseType = entityType;
    while (CollectionUtils.isEmpty(baseType.getKeyPredicateNames()) && baseType.getBaseType() != null) {
      baseType = getEdmTypeInfo(baseType.getBaseType().getFullQualifiedName().toString()).getEntityType();
    }

    final Map<String, String> res = new LinkedHashMap<String, String>();
    for (EdmKeyPropertyRef pref : baseType.getKeyPropertyRefs()) {
      res.put(pref.getName(),
              getJavaType(pref.getProperty().getType().getFullQualifiedName().toString()));
    }

    return res;
  }

  public final String getNameInNamespace(final String name) {
    return getSchema().getNamespace() + "." + name;
  }

  public boolean isSameType(
          final String entityTypeExpression, final String fullTypeExpression, final boolean collection) {

    final Set<String> types = new HashSet<String>(2);

    types.add((collection ? "Collection(" : StringUtils.EMPTY)
            + getNameInNamespace(entityTypeExpression)
            + (collection ? ")" : StringUtils.EMPTY));
    if (StringUtils.isNotBlank(getSchema().getAlias())) {
      types.add((collection ? "Collection(" : StringUtils.EMPTY)
              + getSchema().getAlias() + "." + entityTypeExpression
              + (collection ? ")" : StringUtils.EMPTY));
    }

    return types.contains(fullTypeExpression);
  }

  private void populateDescendants(final EdmTypeInfo base, final List<String> descendants) {
    for (Map.Entry<String, List<EdmEntityType>> entry : allEntityTypes.entrySet()) {
      for (EdmEntityType type : entry.getValue()) {
        if (type.getBaseType() != null
                && base.getFullQualifiedName().equals(type.getBaseType().getFullQualifiedName())) {

          final EdmTypeInfo entityTypeInfo = getEdmTypeInfo(type.getFullQualifiedName().toString());

          descendants.add(entityTypeInfo.getFullQualifiedName().toString());
          populateDescendants(entityTypeInfo, descendants);
        }
      }
    }
  }

  public List<String> getDescendantsOrSelf(final EdmTypeInfo entityType) {
    final List<String> descendants = new ArrayList<String>();

    descendants.add(entityType.getFullQualifiedName().toString());
    populateDescendants(entityType, descendants);

    return descendants;
  }

  public String getBasePackage() {
    return basePackage;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public String getNamespace() {
    return namespace;
  }

  public String capitalize(final String str) {
    return StringUtils.capitalize(str);
  }

  public String uncapitalize(final String str) {
    return StringUtils.uncapitalize(str);
  }

  public String join(final Object[] array, String sep) {
    return StringUtils.join(array, sep);
  }

  public Map<String, String> getFcProperties(final EdmProperty property) {
    return Collections.<String, String>emptyMap();
  }

  public final String getNameFromNS(final String ns) {
    return getNameFromNS(ns, false);
  }

  public final String getNameFromNS(final String ns, final boolean toLowerCase) {
    String res = null;

    if (StringUtils.isNotBlank(ns)) {
      final int lastpt = ns.lastIndexOf('.');
      res = ns.substring(lastpt < 0 ? 0 : lastpt + 1);
      res = toLowerCase ? res.toLowerCase() : res;
    }

    return res;
  }
}
