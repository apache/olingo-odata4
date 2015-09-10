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
package org.apache.olingo.server.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;

public class SchemaBasedEdmProvider implements CsdlEdmProvider {
  private final List<CsdlSchema> edmSchemas = new ArrayList<CsdlSchema>();

  public void addSchema(CsdlSchema schema) {
    this.edmSchemas.add(schema);
  }

  private CsdlSchema getSchema(String ns) {
    for (CsdlSchema s : this.edmSchemas) {
      if (s.getNamespace().equals(ns)) {
        return s;
      }
    }
    return null;
  }

  @Override
  public CsdlEnumType getEnumType(FullQualifiedName fqn) throws ODataException {
    CsdlSchema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<CsdlEnumType> types = schema.getEnumTypes();
      if (types != null) {
        for (CsdlEnumType type : types) {
          if (type.getName().equals(fqn.getName())) {
            return type;
          }
        }
      }
    }
    return null;
  }

  @Override
  public CsdlTypeDefinition getTypeDefinition(FullQualifiedName fqn) throws ODataException {
    CsdlSchema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<CsdlTypeDefinition> types = schema.getTypeDefinitions();
      if (types != null) {
        for (CsdlTypeDefinition type : types) {
          if (type.getName().equals(fqn.getName())) {
            return type;
          }
        }
      }
    }
    return null;
  }

  @Override
  public List<CsdlFunction> getFunctions(FullQualifiedName fqn) throws ODataException {
    ArrayList<CsdlFunction> foundFuncs = new ArrayList<CsdlFunction>();
    CsdlSchema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<CsdlFunction> functions = schema.getFunctions();
      if (functions != null) {
        for (CsdlFunction func : functions) {
          if (func.getName().equals(fqn.getName())) {
            foundFuncs.add(func);
          }
        }
      }
    }
    return foundFuncs;
  }

  @Override
  public CsdlTerm getTerm(FullQualifiedName fqn) throws ODataException {
    CsdlSchema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<CsdlTerm> terms = schema.getTerms();
      if (terms != null) {
        for (CsdlTerm term : terms) {
          if (term.getName().equals(fqn.getName())) {
            return term;
          }
        }
      }
    }
    return null;
  }

  @Override
  public CsdlEntitySet getEntitySet(FullQualifiedName fqn, String entitySetName) throws ODataException {
    CsdlSchema schema = getSchema(fqn.getFullQualifiedNameAsString());
    if (schema != null) {
      CsdlEntityContainer ec = schema.getEntityContainer();
      if (ec != null && ec.getEntitySets() != null) {
        for (CsdlEntitySet es : ec.getEntitySets()) {
          if (es.getName().equals(entitySetName)) {
            return es;
          }
        }
      }
    }
    return null;
  }

  @Override
  public CsdlSingleton getSingleton(FullQualifiedName fqn, String singletonName) throws ODataException {
    CsdlSchema schema = getSchema(fqn.getFullQualifiedNameAsString());
    if (schema != null) {
      CsdlEntityContainer ec = schema.getEntityContainer();
      if (ec != null && ec.getSingletons() != null) {
        for (CsdlSingleton es : ec.getSingletons()) {
          if (es.getName().equals(singletonName)) {
            return es;
          }
        }
      }
    }
    return null;
  }

  @Override
  public CsdlActionImport getActionImport(FullQualifiedName fqn, String actionImportName)
      throws ODataException {
    CsdlSchema schema = getSchema(fqn.getFullQualifiedNameAsString());
    if (schema != null) {
      CsdlEntityContainer ec = schema.getEntityContainer();
      if (ec != null && ec.getActionImports() != null) {
        for (CsdlActionImport es : ec.getActionImports()) {
          if (es.getName().equals(actionImportName)) {
            return es;
          }
        }
      }
    }
    return null;
  }

  @Override
  public CsdlFunctionImport getFunctionImport(FullQualifiedName fqn, String functionImportName)
      throws ODataException {
    CsdlSchema schema = getSchema(fqn.getFullQualifiedNameAsString());
    if (schema != null) {
      CsdlEntityContainer ec = schema.getEntityContainer();
      if (ec != null && ec.getFunctionImports() != null) {
        for (CsdlFunctionImport es : ec.getFunctionImports()) {
          if (es.getName().equals(functionImportName)) {
            return es;
          }
        }
      }
    }
    return null;
  }

  @Override
  public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName fqn) throws ODataException {
    CsdlSchema schema = null;

    if (fqn == null) {
      for (CsdlSchema s : this.edmSchemas) {
        if (s.getEntityContainer() != null) {
          schema = s;
          break;
        }
      }
    } else {
      schema = getSchema(fqn.getFullQualifiedNameAsString());
    }

    if (schema != null) {
      CsdlEntityContainer ec = schema.getEntityContainer();
      if (ec != null) {
        CsdlEntityContainerInfo info = new CsdlEntityContainerInfo();
        info.setContainerName(new FullQualifiedName(schema.getNamespace()));
        if (schema.getEntityContainer().getExtendsContainer() != null) {
          info.setExtendsContainer(new FullQualifiedName(schema.getEntityContainer().getExtendsContainer()));
        }
        return info;
      }
    }
    return null;
  }

  @Override
  public List<CsdlAliasInfo> getAliasInfos() throws ODataException {
    CsdlSchema schema = null;
    for (CsdlSchema s : this.edmSchemas) {
      if (s.getEntityContainer() != null) {
        schema = s;
        break;
      }
    }

    if (schema == null) {
      schema = this.edmSchemas.get(0);
    }

    CsdlAliasInfo ai = new CsdlAliasInfo();
    ai.setAlias(schema.getAlias());
    ai.setNamespace(schema.getNamespace());
    return Arrays.asList(ai);
  }

  @Override
  public CsdlEntityContainer getEntityContainer() throws ODataException {
    // note that there can be many schemas, but only one needs to contain the
    // entity container in a given metadata document.
    for (CsdlSchema s : this.edmSchemas) {
      if (s.getEntityContainer() != null) {
        return s.getEntityContainer();
      }
    }
    return null;
  }

  @Override
  public List<CsdlSchema> getSchemas() throws ODataException {
    return new ArrayList<CsdlSchema>(this.edmSchemas);
  }

  @Override
  public CsdlEntityType getEntityType(final FullQualifiedName fqn) throws ODataException {
    CsdlSchema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      if (schema.getEntityTypes() != null) {
        for (CsdlEntityType type : schema.getEntityTypes()) {
          if (type.getName().equals(fqn.getName())) {
            return type;
          }
        }
      }
    }
    return null;
  }

  @Override
  public CsdlComplexType getComplexType(final FullQualifiedName fqn) throws ODataException {
    CsdlSchema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      if (schema.getComplexTypes() != null) {
        for (CsdlComplexType type : schema.getComplexTypes()) {
          if (type.getName().equals(fqn.getName())) {
            return type;
          }
        }
      }
    }
    return null;
  }

  @Override
  public List<CsdlAction> getActions(final FullQualifiedName fqn) throws ODataException {
    ArrayList<CsdlAction> actions = new ArrayList<CsdlAction>();
    CsdlSchema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<CsdlAction> types = schema.getActions();
      if (types != null) {
        for (CsdlAction type : types) {
          if (type.getName().equals(fqn.getName())) {
            actions.add(type);
          }
        }
      }
    }
    return actions;
  }

  @Override
  public CsdlAnnotations getAnnotationsGroup(FullQualifiedName targetName) throws ODataException {
    return null;
  }

  @Override
  public CsdlAnnotatable getAnnotatable(FullQualifiedName annotatedName) throws ODataException {
    return null;
  }
}
