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

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.ActionImport;
import org.apache.olingo.server.api.edm.provider.AliasInfo;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.api.edm.provider.Function;
import org.apache.olingo.server.api.edm.provider.FunctionImport;
import org.apache.olingo.server.api.edm.provider.Schema;
import org.apache.olingo.server.api.edm.provider.Singleton;
import org.apache.olingo.server.api.edm.provider.Term;
import org.apache.olingo.server.api.edm.provider.TypeDefinition;

public class SchemaBasedEdmProvider extends EdmProvider {
  private final List<Schema> edmSchemas = new ArrayList<Schema>();

  protected void addSchema(Schema schema) {
    this.edmSchemas.add(schema);
  }

  private Schema getSchema(String ns) {
    for (Schema s : this.edmSchemas) {
      if (s.getNamespace().equals(ns)) {
        return s;
      }
    }
    return null;
  }

  @Override
  public EnumType getEnumType(FullQualifiedName fqn) throws ODataException {
    Schema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<EnumType> types = schema.getEnumTypes();
      if (types != null) {
        for (EnumType type : types) {
          if (type.getName().equals(fqn.getName())) {
            return type;
          }
        }
      }
    }
    return null;
  }

  @Override
  public TypeDefinition getTypeDefinition(FullQualifiedName fqn) throws ODataException {
    Schema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<TypeDefinition> types = schema.getTypeDefinitions();
      if (types != null) {
        for (TypeDefinition type : types) {
          if (type.getName().equals(fqn.getName())) {
            return type;
          }
        }
      }
    }
    return null;
  }

  @Override
  public List<Function> getFunctions(FullQualifiedName fqn) throws ODataException {
    ArrayList<Function> foundFuncs = new ArrayList<Function>();
    Schema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<Function> functions = schema.getFunctions();
      if (functions != null) {
        for (Function func : functions) {
          if (func.getName().equals(fqn.getName())) {
            foundFuncs.add(func);
          }
        }
      }
    }
    return foundFuncs;
  }

  @Override
  public Term getTerm(FullQualifiedName fqn) throws ODataException {
    Schema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<Term> terms = schema.getTerms();
      if (terms != null) {
        for (Term term : terms) {
          if (term.getName().equals(fqn.getName())) {
            return term;
          }
        }
      }
    }
    return null;
  }

  @Override
  public EntitySet getEntitySet(FullQualifiedName fqn, String entitySetName) throws ODataException {
    Schema schema = getSchema(fqn.getFullQualifiedNameAsString());
    if (schema != null) {
      EntityContainer ec = schema.getEntityContainer();
      if (ec != null && ec.getEntitySets() != null) {
        for (EntitySet es : ec.getEntitySets()) {
          if (es.getName().equals(entitySetName)) {
            return es;
          }
        }
      }
    }
    return null;
  }

  @Override
  public Singleton getSingleton(FullQualifiedName fqn, String singletonName) throws ODataException {
    Schema schema = getSchema(fqn.getFullQualifiedNameAsString());
    if (schema != null) {
      EntityContainer ec = schema.getEntityContainer();
      if (ec != null && ec.getSingletons() != null) {
        for (Singleton es : ec.getSingletons()) {
          if (es.getName().equals(singletonName)) {
            return es;
          }
        }
      }
    }
    return null;
  }

  @Override
  public ActionImport getActionImport(FullQualifiedName fqn, String actionImportName)
      throws ODataException {
    Schema schema = getSchema(fqn.getFullQualifiedNameAsString());
    if (schema != null) {
      EntityContainer ec = schema.getEntityContainer();
      if (ec != null && ec.getActionImports() != null) {
        for (ActionImport es : ec.getActionImports()) {
          if (es.getName().equals(actionImportName)) {
            return es;
          }
        }
      }
    }
    return null;
  }

  @Override
  public FunctionImport getFunctionImport(FullQualifiedName fqn, String functionImportName)
      throws ODataException {
    Schema schema = getSchema(fqn.getFullQualifiedNameAsString());
    if (schema != null) {
      EntityContainer ec = schema.getEntityContainer();
      if (ec != null && ec.getFunctionImports() != null) {
        for (FunctionImport es : ec.getFunctionImports()) {
          if (es.getName().equals(functionImportName)) {
            return es;
          }
        }
      }
    }
    return null;
  }

  @Override
  public EntityContainerInfo getEntityContainerInfo(FullQualifiedName fqn) throws ODataException {
    Schema schema = null;

    if (fqn == null) {
      for (Schema s : this.edmSchemas) {
        if (s.getEntityContainer() != null) {
          schema = s;
          break;
        }
      }
    } else {
      schema = getSchema(fqn.getFullQualifiedNameAsString());
    }

    if (schema != null) {
      EntityContainer ec = schema.getEntityContainer();
      if (ec != null) {
        EntityContainerInfo info = new EntityContainerInfo();
        info.setContainerName(new FullQualifiedName(schema.getNamespace()));
        info.setExtendsContainer(schema.getEntityContainer().getExtendsContainer());
        return info;
      }
    }
    return null;
  }

  @Override
  public List<AliasInfo> getAliasInfos() throws ODataException {
    Schema schema = null;
    for (Schema s : this.edmSchemas) {
      if (s.getEntityContainer() != null) {
        schema = s;
        break;
      }
    }

    if (schema == null) {
      schema = this.edmSchemas.get(0);
    }

    AliasInfo ai = new AliasInfo();
    ai.setAlias(schema.getAlias());
    ai.setNamespace(schema.getNamespace());
    return Arrays.asList(ai);
  }

  @Override
  public EntityContainer getEntityContainer() throws ODataException {
    // note that there can be many schemas, but only one needs to contain the
    // entity container in a given metadata document.
    for (Schema s : this.edmSchemas) {
      if (s.getEntityContainer() != null) {
        return s.getEntityContainer();
      }
    }
    return null;
  }

  @Override
  public List<Schema> getSchemas() throws ODataException {
    return new ArrayList<Schema>(this.edmSchemas);
  }

  @Override
  public EntityType getEntityType(final FullQualifiedName fqn) throws ODataException {
    Schema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      if (schema.getEntityTypes() != null) {
        for (EntityType type : schema.getEntityTypes()) {
          if (type.getName().equals(fqn.getName())) {
            return type;
          }
        }
      }
    }
    return null;
  }

  @Override
  public ComplexType getComplexType(final FullQualifiedName fqn) throws ODataException {
    Schema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      if (schema.getComplexTypes() != null) {
        for (ComplexType type : schema.getComplexTypes()) {
          if (type.getName().equals(fqn.getName())) {
            return type;
          }
        }
      }
    }
    return null;
  }

  @Override
  public List<Action> getActions(final FullQualifiedName fqn) throws ODataException {
    ArrayList<Action> actions = new ArrayList<Action>();
    Schema schema = getSchema(fqn.getNamespace());
    if (schema != null) {
      List<Action> types = schema.getActions();
      if (types != null) {
        for (Action type : types) {
          if (type.getName().equals(fqn.getName())) {
            actions.add(type);
          }
        }
      }
    }
    return actions;
  }
}
