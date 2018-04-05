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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
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
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.commons.api.ex.ODataException;

public class SchemaBasedEdmProvider implements CsdlEdmProvider {
  private final List<CsdlSchema> edmSchemas = new ArrayList<CsdlSchema>();
  private final Map<String, EdmxReference> references = new ConcurrentHashMap<String, EdmxReference>();
  private final Map<String, SchemaBasedEdmProvider> referenceSchemas = 
      new ConcurrentHashMap<String, SchemaBasedEdmProvider>();
  private final Map<String, SchemaBasedEdmProvider> coreVocabularySchemas = 
      new ConcurrentHashMap<String, SchemaBasedEdmProvider>();
  
  protected void addSchema(CsdlSchema schema) {
    this.edmSchemas.add(schema);
  }
  
  public List<EdmxReference> getReferences(){
    return new ArrayList<EdmxReference>(references.values());
  }
  
  protected void addReferenceSchema(String ns, SchemaBasedEdmProvider provider) {
    this.referenceSchemas.put(ns, provider);
  }  
  
  protected void addVocabularySchema(String ns, SchemaBasedEdmProvider provider) {
    this.coreVocabularySchemas.put(ns, provider);
  }
  
  protected void addReference(EdmxReference reference) {
    for (EdmxReferenceInclude include : reference.getIncludes()) {
      this.references.put(include.getNamespace(), reference);
    }
  }  
  
  CsdlSchema getVocabularySchema(String ns) {
    SchemaBasedEdmProvider provider = this.coreVocabularySchemas.get(ns);
    if (provider != null) {
      return provider.getSchema(ns, false);
    }
    return null;
  }
  
  CsdlSchema getSchema(String ns) {
    return getSchema(ns, true);
  }  
  
  CsdlSchema getSchema(String ns, boolean checkReferences) {
    if (checkReferences) {
      return getSchemaRecursively(ns, new HashSet<String>());
    } else {
      return getSchemaDirectly(ns);
    }
  }

  CsdlSchema getSchemaDirectly(String ns) {
    for (CsdlSchema s : this.edmSchemas) {
      if (s.getNamespace().equals(ns)) {
        return s;
      }
    }
    return null;
  }

  CsdlSchema getSchemaRecursively(String ns, Set<String> parsedPath) {
    // find the schema by namespace in current provider
    CsdlSchema schema = getSchemaDirectly(ns);
    if (schema != null) {
      return schema;
    }

    // find the schema by namespace in the reference schema provider
    for (Map.Entry<String, SchemaBasedEdmProvider> entry : this.referenceSchemas.entrySet()) {
      String namespace = entry.getKey();
      if (parsedPath.contains(namespace)) {
        continue;
      }
      SchemaBasedEdmProvider provider = entry.getValue();
      parsedPath.add(namespace);
      schema = provider.getSchemaRecursively(ns, parsedPath);
      if (schema != null) {
        return schema;
      }
    }

    return getVocabularySchema(ns);
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
    CsdlSchema schema = getSchema(fqn.getNamespace());
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
    CsdlSchema schema = getSchema(fqn.getNamespace());
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
    CsdlSchema schema = getSchema(fqn.getNamespace());
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
    CsdlSchema schema = getSchema(fqn.getNamespace());
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
      schema = getSchema(fqn.getNamespace());
    }

    if (schema != null) {
      CsdlEntityContainer ec = schema.getEntityContainer();
      if (ec != null) {
        CsdlEntityContainerInfo info = new CsdlEntityContainerInfo();
        info.setContainerName(new FullQualifiedName(schema.getNamespace(), ec.getName()));
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
    ArrayList<CsdlAliasInfo> list = new ArrayList<CsdlAliasInfo>();
    for (CsdlSchema s : this.edmSchemas) {
      if (s.getAlias() != null) {
        CsdlAliasInfo ai = new CsdlAliasInfo();
        ai.setAlias(s.getAlias());
        ai.setNamespace(s.getNamespace());
        list.add(ai);
      }
    }
    for(EdmxReference reference:this.references.values()) {
      for(EdmxReferenceInclude include:reference.getIncludes()) {
        if (include.getAlias() != null) {
          CsdlAliasInfo ai = new CsdlAliasInfo();
          ai.setAlias(include.getAlias());
          ai.setNamespace(include.getNamespace());
          list.add(ai);          
        }
      }
    }
    for (SchemaBasedEdmProvider p:this.coreVocabularySchemas.values()) {
      for (CsdlSchema s:p.getSchemas()) {
        if (s.getAlias() != null) {
          CsdlAliasInfo ai = new CsdlAliasInfo();
          ai.setAlias(s.getAlias());
          ai.setNamespace(s.getNamespace());
          list.add(ai);
        }        
      }
    }
    return list;
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
    if (schema != null && schema.getEntityTypes() != null) {
      for (CsdlEntityType type : schema.getEntityTypes()) {
        if (type.getName().equals(fqn.getName())) {
          return type;
        }
      }
    }
    return null;
  }

  @Override
  public CsdlComplexType getComplexType(final FullQualifiedName fqn) throws ODataException {
    CsdlSchema schema = getSchema(fqn.getNamespace());
    if (schema != null && schema.getComplexTypes() != null) {
      for (CsdlComplexType type : schema.getComplexTypes()) {
        if (type.getName().equals(fqn.getName())) {
          return type;
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
  public CsdlAnnotations getAnnotationsGroup(FullQualifiedName targetName, String qualifier) throws ODataException {
    CsdlSchema schema = getSchema(targetName.getNamespace());
    if (schema != null) {
      return schema.getAnnotationGroup(targetName.getFullQualifiedNameAsString(), qualifier);
    }
    return null;
  } 
}
