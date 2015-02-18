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
package org.apache.olingo.client.core.edm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.edm.xml.ComplexType;
import org.apache.olingo.client.api.edm.xml.EntityContainer;
import org.apache.olingo.client.api.edm.xml.EntityType;
import org.apache.olingo.client.api.edm.xml.EnumType;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.v4.Action;
import org.apache.olingo.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.client.api.edm.xml.v4.Annotations;
import org.apache.olingo.client.api.edm.xml.v4.Function;
import org.apache.olingo.client.api.edm.xml.v4.Term;
import org.apache.olingo.client.api.edm.xml.v4.TypeDefinition;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.AbstractEdmSchema;

public class EdmSchemaImpl extends AbstractEdmSchema {

  private final ODataServiceVersion version;

  private final Edm edm;

  private final List<? extends Schema> xmlSchemas;

  private final Schema schema;

  private Map<FullQualifiedName, EdmEntityContainer> entityContainerByName;

  private List<EdmEntityContainer> entityContainers;

  public EdmSchemaImpl(final ODataServiceVersion version, final Edm edm,
      final List<? extends Schema> xmlSchemas, final Schema schema) {

    super(schema.getNamespace(), schema.getAlias());

    this.version = version;
    this.edm = edm;
    this.xmlSchemas = xmlSchemas;
    this.schema = schema;
  }

  @Override
  public List<EdmEntityContainer> getEntityContainers() {
    if (entityContainers == null) {
      entityContainerByName = new HashMap<FullQualifiedName, EdmEntityContainer>();

      if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
        entityContainers = super.getEntityContainers();
        if (getEntityContainer() != null) {
          entityContainerByName.put(getEntityContainer().getFullQualifiedName(), getEntityContainer());
        }
      } else {
        entityContainers = new ArrayList<EdmEntityContainer>(schema.getEntityContainers().size());
        for (EntityContainer entityContainer : schema.getEntityContainers()) {
          final EdmEntityContainer edmContainer = createEntityContainer(entityContainer.getName());
          if (edmContainer != null) {
            entityContainers.add(edmContainer);
            entityContainerByName.put(edmContainer.getFullQualifiedName(), edmContainer);
          }
        }
      }
    }

    return entityContainers;
  }

  @Override
  public EdmEntityContainer getEntityContainer(final FullQualifiedName name) {
    getEntityContainers();
    return entityContainerByName.get(name);
  }

  private EdmEntityContainer createEntityContainer(final String name) {
    final EntityContainer defaultContainer = schema.getEntityContainer(name);
    if (defaultContainer != null) {
      final FullQualifiedName entityContainerName =
          new FullQualifiedName(schema.getNamespace(), defaultContainer.getName());
      return new EdmEntityContainerImpl(edm, entityContainerName, defaultContainer, xmlSchemas);
    }
    return null;
  }

  @Override
  protected EdmEntityContainer createEntityContainer() {
    final EntityContainer defaultContainer = schema.getDefaultEntityContainer();
    if (defaultContainer != null) {
      return createEntityContainer(defaultContainer.getName());
    }
    return null;
  }

  @Override
  protected List<EdmTypeDefinition> createTypeDefinitions() {
    final List<EdmTypeDefinition> typeDefinitions = new ArrayList<EdmTypeDefinition>();
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<TypeDefinition> providerTypeDefinitions =
          ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getTypeDefinitions();
      if (providerTypeDefinitions != null) {
        for (TypeDefinition def : providerTypeDefinitions) {
          typeDefinitions.add(
              new EdmTypeDefinitionImpl(version, edm, new FullQualifiedName(namespace, def.getName()), def));
        }
      }
    }
    return typeDefinitions;
  }

  @Override
  protected List<EdmEnumType> createEnumTypes() {
    final List<EdmEnumType> enumTypes = new ArrayList<EdmEnumType>();
    final List<EnumType> providerEnumTypes = schema.getEnumTypes();
    if (providerEnumTypes != null) {
      for (EnumType enumType : providerEnumTypes) {
        enumTypes.add(
            new EdmEnumTypeImpl(version, edm, new FullQualifiedName(namespace, enumType.getName()), enumType));
      }
    }
    return enumTypes;
  }

  @Override
  protected List<EdmEntityType> createEntityTypes() {
    final List<EdmEntityType> entityTypes = new ArrayList<EdmEntityType>();
    final List<? extends EntityType> providerEntityTypes = schema.getEntityTypes();
    if (providerEntityTypes != null) {
      for (EntityType entityType : providerEntityTypes) {
        entityTypes.add(EdmEntityTypeImpl.getInstance(edm,
            new FullQualifiedName(namespace, entityType.getName()), xmlSchemas, entityType));
      }
    }
    return entityTypes;
  }

  @Override
  protected List<EdmComplexType> createComplexTypes() {
    final List<EdmComplexType> complexTypes = new ArrayList<EdmComplexType>();
    final List<? extends ComplexType> providerComplexTypes = schema.getComplexTypes();
    if (providerComplexTypes != null) {
      for (ComplexType complexType : providerComplexTypes) {
        complexTypes.add(EdmComplexTypeImpl.getInstance(edm, new FullQualifiedName(namespace, complexType.getName()),
            xmlSchemas, complexType));
      }
    }
    return complexTypes;
  }

  @Override
  protected List<EdmAction> createActions() {
    final List<EdmAction> actions = new ArrayList<EdmAction>();
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Action> providerActions = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getActions();
      if (providerActions != null) {
        for (Action action : providerActions) {
          actions.add(EdmActionImpl.getInstance(edm, new FullQualifiedName(namespace, action.getName()), action));
        }
      }
    }
    return actions;
  }

  @Override
  protected List<EdmFunction> createFunctions() {
    final List<EdmFunction> functions = new ArrayList<EdmFunction>();
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Function> providerFunctions = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getFunctions();
      if (providerFunctions != null) {
        for (Function function : providerFunctions) {
          functions.add(
              EdmFunctionImpl.getInstance(edm, new FullQualifiedName(namespace, function.getName()), function));
        }
        return functions;
      }
    }
    return functions;
  }

  @Override
  protected List<EdmTerm> createTerms() {
    final List<EdmTerm> terms = new ArrayList<EdmTerm>();
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Term> providerTerms = ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getTerms();
      if (providerTerms != null) {
        for (Term term : providerTerms) {
          terms.add(new EdmTermImpl(edm, getNamespace(), term));
        }
      }
    }
    return terms;
  }

  @Override
  protected List<EdmAnnotations> createAnnotationGroups() {
    final List<EdmAnnotations> annotationGroups = new ArrayList<EdmAnnotations>();
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Annotations> providerAnnotations =
          ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getAnnotationGroups();
      if (providerAnnotations != null) {
        for (Annotations annotationGroup : providerAnnotations) {
          annotationGroups.add(new EdmAnnotationsImpl(edm, this, annotationGroup));
        }
      }
    }
    return annotationGroups;
  }

  @Override
  protected List<EdmAnnotation> createAnnotations() {
    final List<EdmAnnotation> annotations = new ArrayList<EdmAnnotation>();
    if (schema instanceof org.apache.olingo.client.api.edm.xml.v4.Schema) {
      final List<Annotation> providerAnnotations =
          ((org.apache.olingo.client.api.edm.xml.v4.Schema) schema).getAnnotations();
      if (providerAnnotations != null) {
        for (Annotation annotation : providerAnnotations) {
          annotations.add(new EdmAnnotationImpl(edm, annotation));
        }
      }
    }
    return annotations;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    EdmAnnotation result = null;
    for (EdmAnnotation annotation : getAnnotations()) {
      if (term.getFullQualifiedName().equals(annotation.getTerm().getFullQualifiedName())) {
        result = annotation;
      }
    }

    return result;
  }
}
