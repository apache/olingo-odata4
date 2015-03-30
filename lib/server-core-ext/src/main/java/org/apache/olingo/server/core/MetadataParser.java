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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.Action;
import org.apache.olingo.commons.api.edm.provider.ActionImport;
import org.apache.olingo.commons.api.edm.provider.ComplexType;
import org.apache.olingo.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.commons.api.edm.provider.EntityContainer;
import org.apache.olingo.commons.api.edm.provider.EntitySet;
import org.apache.olingo.commons.api.edm.provider.EntityType;
import org.apache.olingo.commons.api.edm.provider.EnumMember;
import org.apache.olingo.commons.api.edm.provider.EnumType;
import org.apache.olingo.commons.api.edm.provider.Function;
import org.apache.olingo.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.commons.api.edm.provider.NavigationProperty;
import org.apache.olingo.commons.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.OnDelete;
import org.apache.olingo.commons.api.edm.provider.OnDeleteAction;
import org.apache.olingo.commons.api.edm.provider.Operation;
import org.apache.olingo.commons.api.edm.provider.Parameter;
import org.apache.olingo.commons.api.edm.provider.Property;
import org.apache.olingo.commons.api.edm.provider.PropertyRef;
import org.apache.olingo.commons.api.edm.provider.ReferentialConstraint;
import org.apache.olingo.commons.api.edm.provider.ReturnType;
import org.apache.olingo.commons.api.edm.provider.Schema;
import org.apache.olingo.commons.api.edm.provider.Singleton;
import org.apache.olingo.commons.api.edm.provider.Term;
import org.apache.olingo.commons.api.edm.provider.TypeDefinition;

/**
 * This class can convert a CSDL document into EDMProvider object
 */
public class MetadataParser {

  public EdmProvider buildEdmProvider(Reader csdl) throws XMLStreamException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader = xmlInputFactory.createXMLEventReader(csdl);

    SchemaBasedEdmProvider provider = new SchemaBasedEdmProvider();
    new ElementReader<SchemaBasedEdmProvider>() {
      @Override
      void build(XMLEventReader reader, StartElement element, SchemaBasedEdmProvider provider,
          String name) throws XMLStreamException {
        String version = attr(element, "Version");
        if (version.equals("4.0")) {
          readDataServicesAndReference(reader, element, provider);
        }
      }
    }.read(reader, null, provider, "Edmx");

    return provider;
  }

  private void readDataServicesAndReference(XMLEventReader reader, StartElement element,
      SchemaBasedEdmProvider provider) throws XMLStreamException {
    new ElementReader<SchemaBasedEdmProvider>() {
      @Override
      void build(XMLEventReader reader, StartElement element, SchemaBasedEdmProvider provider,
          String name) throws XMLStreamException {
        if (name.equals("DataServices")) {
          readSchema(reader, element, provider);
        } else if (name.equals("Reference")) {
          readReference(reader, element, provider, "Reference");
        }
      }
    }.read(reader, element, provider, "DataServices", "Reference");
  }

  private void readReference(XMLEventReader reader, StartElement element,
      SchemaBasedEdmProvider provider, String name) throws XMLStreamException {
    new ElementReader<SchemaBasedEdmProvider>() {
      @Override
      void build(XMLEventReader reader, StartElement element, SchemaBasedEdmProvider t, String name)
          throws XMLStreamException {
        // TODO:
      }
    }.read(reader, element, provider, name);
  }

  private void readSchema(XMLEventReader reader, StartElement element,
      SchemaBasedEdmProvider provider) throws XMLStreamException {

    Schema schema = new Schema();
    schema.setComplexTypes(new ArrayList<ComplexType>());
    schema.setActions(new ArrayList<Action>());
    schema.setEntityTypes(new ArrayList<EntityType>());
    schema.setEnumTypes(new ArrayList<EnumType>());
    schema.setFunctions(new ArrayList<Function>());
    schema.setTerms(new ArrayList<Term>());
    schema.setTypeDefinitions(new ArrayList<TypeDefinition>());

    new ElementReader<Schema>() {
      @Override
      void build(XMLEventReader reader, StartElement element, Schema schema, String name)
          throws XMLStreamException {
        schema.setNamespace(attr(element, "Namespace"));
        schema.setAlias(attr(element, "Alias"));
        readSchemaContents(reader, schema);
      }
    }.read(reader, element, schema, "Schema");
    provider.addSchema(schema);
  }

  private void readSchemaContents(XMLEventReader reader, Schema schema) throws XMLStreamException {
    new ElementReader<Schema>() {
      @Override
      void build(XMLEventReader reader, StartElement element, Schema schema, String name)
          throws XMLStreamException {
        if (name.equals("Action")) {
          readAction(reader, element, schema);
        } else if (name.equals("Annotations")) {
          // TODO:
        } else if (name.equals("Annotation")) {
          // TODO:
        } else if (name.equals("ComplexType")) {
          readComplexType(reader, element, schema);
        } else if (name.equals("EntityContainer")) {
          readEntityContainer(reader, element, schema);
        } else if (name.equals("EntityType")) {
          readEntityType(reader, element, schema);
        } else if (name.equals("EnumType")) {
          readEnumType(reader, element, schema);
        } else if (name.equals("Function")) {
          readFunction(reader, element, schema);
        } else if (name.equals("Term")) {
          schema.getTerms().add(readTerm(element));
        } else if (name.equals("TypeDefinition")) {
          schema.getTypeDefinitions().add(readTypeDefinition(element));
        }
      }
    }.read(reader, null, schema, "Action", "Annotations", "Annotation", "ComplexType",
        "EntityContainer", "EntityType", "EnumType", "Function", "Term", "TypeDefinition");
  }

  private void readAction(XMLEventReader reader, StartElement element, Schema schema)
      throws XMLStreamException {

    Action action = new Action();
    action.setParameters(new ArrayList<Parameter>());
    action.setName(attr(element, "Name"));
    action.setBound(Boolean.parseBoolean(attr(element, "IsBound")));
    String entitySetPath = attr(element, "EntitySetPath");
    if (entitySetPath != null) {
      // TODO: need to parse into binding and path.
      action.setEntitySetPath(entitySetPath);
    }
    readOperationParameters(reader, action);
    schema.getActions().add(action);
  }

  private FullQualifiedName readType(StartElement element) {
    String type = attr(element, "Type");
    if (type.startsWith("Collection(") && type.endsWith(")")) {
      return new FullQualifiedName(type.substring(11, type.length() - 1));
    }
    return new FullQualifiedName(type);
  }

  private boolean isCollectionType(StartElement element) {
    String type = attr(element, "Type");
    if (type.startsWith("Collection(") && type.endsWith(")")) {
      return true;
    }
    return false;
  }

  private void readReturnType(StartElement element, Operation operation) {
    ReturnType returnType = new ReturnType();
    returnType.setType(readType(element));
    returnType.setCollection(isCollectionType(element));
    returnType.setNullable(Boolean.parseBoolean(attr(element, "Nullable")));

    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      returnType.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      returnType.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      returnType.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      // TODO: no olingo support yet.
    }
    operation.setReturnType(returnType);
  }

  private void readParameter(StartElement element, Operation operation) {
    Parameter parameter = new Parameter();
    parameter.setName(attr(element, "Name"));
    parameter.setType(readType(element));
    parameter.setCollection(isCollectionType(element));
    parameter.setNullable(Boolean.parseBoolean(attr(element, "Nullable")));

    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      parameter.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      parameter.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      parameter.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      // TODO: no olingo support yet.
    }
    operation.getParameters().add(parameter);
  }

  private TypeDefinition readTypeDefinition(StartElement element) {
    TypeDefinition td = new TypeDefinition();
    td.setName(attr(element, "Name"));
    td.setUnderlyingType(new FullQualifiedName(attr(element, "UnderlyingType")));
    td.setUnicode(Boolean.parseBoolean(attr(element, "Unicode")));

    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      td.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      td.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      td.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      // TODO: no olingo support yet.
    }
    return td;
  }

  private Term readTerm(StartElement element) {
    Term term = new Term();
    term.setName(attr(element, "Name"));
    term.setType(attr(element, "Type"));
    if (attr(element, "BaseTerm") != null) {
      term.setBaseTerm(attr(element, "BaseTerm"));
    }
    if (attr(element, "DefaultValue") != null) {
      term.setDefaultValue(attr(element, "DefaultValue"));
    }
    if (attr(element, "AppliesTo") != null) {
      term.setAppliesTo(Arrays.asList(attr(element, "AppliesTo")));
    }
    term.setNullable(Boolean.parseBoolean(attr(element, "Nullable")));
    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      term.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      term.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      term.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      // TODO: no olingo support yet.
    }
    return term;
  }

  private void readFunction(XMLEventReader reader, StartElement element, Schema schema)
      throws XMLStreamException {
    Function function = new Function();
    function.setParameters(new ArrayList<Parameter>());
    function.setName(attr(element, "Name"));
    function.setBound(Boolean.parseBoolean(attr(element, "IsBound")));
    function.setComposable(Boolean.parseBoolean(attr(element, "IsComposable")));
    String entitySetPath = attr(element, "EntitySetPath");
    if (entitySetPath != null) {
      // TODO: need to parse into binding and path.
      function.setEntitySetPath(entitySetPath);
    }
    readOperationParameters(reader, function);
    schema.getFunctions().add(function);
  }

  private void readOperationParameters(XMLEventReader reader, final Operation operation)
      throws XMLStreamException {
    new ElementReader<Operation>() {
      @Override
      void build(XMLEventReader reader, StartElement element, Operation operation, String name)
          throws XMLStreamException {
        if (name.equals("Parameter")) {
          readParameter(element, operation);
        } else if (name.equals("ReturnType")) {
          readReturnType(element, operation);
        }
      }
    }.read(reader, null, operation, "Parameter", "ReturnType");
  }

  private void readEnumType(XMLEventReader reader, StartElement element, Schema schema)
      throws XMLStreamException {
    EnumType type = new EnumType();
    type.setMembers(new ArrayList<EnumMember>());
    type.setName(attr(element, "Name"));
    if (attr(element, "UnderlyingType") != null) {
      type.setUnderlyingType(new FullQualifiedName(attr(element, "UnderlyingType")));
    }
    type.setFlags(Boolean.parseBoolean(attr(element, "IsFlags")));

    readEnumMembers(reader, element, type);
    schema.getEnumTypes().add(type);
  }

  private void readEnumMembers(XMLEventReader reader, StartElement element, EnumType type)
      throws XMLStreamException {
    new ElementReader<EnumType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, EnumType type, String name)
          throws XMLStreamException {
        EnumMember member = new EnumMember();
        member.setName(attr(element, "Name"));
        member.setValue(attr(element, "Value"));
        type.getMembers().add(member);
      }
    }.read(reader, element, type, "Member");
  }

  private void readEntityType(XMLEventReader reader, StartElement element, Schema schema)
      throws XMLStreamException {
    EntityType entityType = new EntityType();
    entityType.setProperties(new ArrayList<Property>());
    entityType.setNavigationProperties(new ArrayList<NavigationProperty>());
    entityType.setKey(new ArrayList<PropertyRef>());
    entityType.setName(attr(element, "Name"));
    if (attr(element, "BaseType") != null) {
      entityType.setBaseType(new FullQualifiedName(attr(element, "BaseType")));
    }
    entityType.setAbstract(Boolean.parseBoolean(attr(element, "Abstract")));
    entityType.setOpenType(Boolean.parseBoolean(attr(element, "OpenType")));
    entityType.setHasStream(Boolean.parseBoolean(attr(element, "HasStream")));
    readEntityProperties(reader, entityType);
    schema.getEntityTypes().add(entityType);
  }

  private void readEntityProperties(XMLEventReader reader, EntityType entityType)
      throws XMLStreamException {
    new ElementReader<EntityType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, EntityType entityType, String name)
          throws XMLStreamException {
        if (name.equals("Property")) {
          entityType.getProperties().add(readProperty(element));
        } else if (name.equals("NavigationProperty")) {
          entityType.getNavigationProperties().add(readNavigationProperty(reader, element));
        } else if (name.equals("Key")) {
          readKey(reader, element, entityType);
        }
      }
    }.read(reader, null, entityType, "Property", "NavigationProperty", "Key");
  }

  private void readKey(XMLEventReader reader, StartElement element, EntityType entityType)
      throws XMLStreamException {
    new ElementReader<EntityType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, EntityType entityType, String name)
          throws XMLStreamException {
        PropertyRef ref = new PropertyRef();
        ref.setName(attr(element, "Name"));
        ref.setAlias(attr(element, "Alias"));
        entityType.getKey().add(ref);
      }
    }.read(reader, element, entityType, "PropertyRef");
  }

  private NavigationProperty readNavigationProperty(XMLEventReader reader, StartElement element)
      throws XMLStreamException {
    NavigationProperty property = new NavigationProperty();
    property.setReferentialConstraints(new ArrayList<ReferentialConstraint>());

    property.setName(attr(element, "Name"));
    property.setType(readType(element));
    property.setCollection(isCollectionType(element));
    property.setNullable(Boolean.parseBoolean(attr(element, "Nullable")));
    property.setPartner(attr(element, "Partner"));
    property.setContainsTarget(Boolean.parseBoolean(attr(element, "ContainsTarget")));

    new ElementReader<NavigationProperty>() {
      @Override
      void build(XMLEventReader reader, StartElement element, NavigationProperty property,
          String name) throws XMLStreamException {
        if (name.equals("ReferentialConstraint")) {
          ReferentialConstraint constraint = new ReferentialConstraint();
          constraint.setProperty(attr(element, "Property"));
          constraint.setReferencedProperty(attr(element, "ReferencedProperty"));
          property.getReferentialConstraints().add(constraint);
        } else if (name.equals("OnDelete")) {
          property.setOnDelete(new OnDelete().setAction(OnDeleteAction.valueOf(attr(element, "Action"))));
        }
      }
    }.read(reader, element, property, "ReferentialConstraint", "OnDelete");
    return property;
  }

  private String attr(StartElement element, String name) {
    Attribute attr = element.getAttributeByName(new QName(name));
    if (attr != null) {
      return attr.getValue();
    }
    return null;
  }

  private Property readProperty(StartElement element) {
    Property property = new Property();
    property.setName(attr(element, "Name"));
    property.setType(readType(element));
    property.setCollection(isCollectionType(element));
    property.setNullable(Boolean.parseBoolean(attr(element, "Nullable") == null ? "true" : attr(
        element, "Nullable")));
    property.setUnicode(Boolean.parseBoolean(attr(element, "Unicode")));

    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      property.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      property.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      property.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      // TODO: no olingo support yet.
    }
    String defaultValue = attr(element, "DefaultValue");
    if (defaultValue != null) {
      property.setDefaultValue(defaultValue);
    }
    return property;
  }

  private void readEntityContainer(XMLEventReader reader, StartElement element, Schema schema)
      throws XMLStreamException {
    final EntityContainer container = new EntityContainer();
    container.setName(attr(element, "Name"));
    if (attr(element, "Extends") != null) {
      container.setExtendsContainer(attr(element, "Extends"));
    }
    container.setActionImports(new ArrayList<ActionImport>());
    container.setFunctionImports(new ArrayList<FunctionImport>());
    container.setEntitySets(new ArrayList<EntitySet>());
    container.setSingletons(new ArrayList<Singleton>());

    new ElementReader<Schema>() {
      @Override
      void build(XMLEventReader reader, StartElement element, Schema schema, String name)
          throws XMLStreamException {
        if (name.equals("EntitySet")) {
          readEntitySet(reader, element, container);
        } else if (name.equals("Singleton")) {
          readSingleton(reader, element, container);
        } else if (name.equals("ActionImport")) {
          readActionImport(element, container);
        } else if (name.equals("FunctionImport")) {
          readFunctionImport(element, container);
        }
      }

      private void readFunctionImport(StartElement element, EntityContainer container) {
        FunctionImport functionImport = new FunctionImport();
        functionImport.setName(attr(element, "Name"));
        functionImport.setFunction(new FullQualifiedName(attr(element, "Function")));
        functionImport.setIncludeInServiceDocument(Boolean.parseBoolean(attr(element,
            "IncludeInServiceDocument")));

        String entitySet = attr(element, "EntitySet");
        if (entitySet != null) {
          functionImport.setEntitySet(entitySet);
        }
        container.getFunctionImports().add(functionImport);
      }

      private void readActionImport(StartElement element, EntityContainer container) {
        ActionImport actionImport = new ActionImport();
        actionImport.setName(attr(element, "Name"));
        actionImport.setAction(new FullQualifiedName(attr(element, "Action")));

        String entitySet = attr(element, "EntitySet");
        if (entitySet != null) {
          actionImport.setEntitySet(entitySet);
        }
        container.getActionImports().add(actionImport);
      }

      private void readSingleton(XMLEventReader reader, StartElement element,
          EntityContainer container) throws XMLStreamException {
        Singleton singleton = new Singleton();
        singleton.setNavigationPropertyBindings(new ArrayList<NavigationPropertyBinding>());
        singleton.setName(attr(element, "Name"));
        singleton.setType(new FullQualifiedName(attr(element, "Type")));
        singleton.setNavigationPropertyBindings(new ArrayList<NavigationPropertyBinding>());
        readNavigationPropertyBindings(reader, element, singleton.getNavigationPropertyBindings());
        container.getSingletons().add(singleton);
      }

      private void readEntitySet(XMLEventReader reader, StartElement element,
          EntityContainer container) throws XMLStreamException {
        EntitySet entitySet = new EntitySet();
        entitySet.setName(attr(element, "Name"));
        entitySet.setType(new FullQualifiedName(attr(element, "EntityType")));
        entitySet.setIncludeInServiceDocument(Boolean.parseBoolean(attr(element,
            "IncludeInServiceDocument")));
        entitySet.setNavigationPropertyBindings(new ArrayList<NavigationPropertyBinding>());
        readNavigationPropertyBindings(reader, element, entitySet.getNavigationPropertyBindings());
        container.getEntitySets().add(entitySet);
      }

      private void readNavigationPropertyBindings(XMLEventReader reader, StartElement element,
          List<NavigationPropertyBinding> bindings) throws XMLStreamException {
        new ElementReader<List<NavigationPropertyBinding>>() {
          @Override
          void build(XMLEventReader reader, StartElement element,
              List<NavigationPropertyBinding> bindings, String name) throws XMLStreamException {
            NavigationPropertyBinding binding = new NavigationPropertyBinding();
            binding.setPath(attr(element, "Path"));
            binding.setTarget(attr(element, "Target"));
            bindings.add(binding);
          }

        }.read(reader, element, bindings, "NavigationPropertyBinding");
        ;
      }
    }.read(reader, element, schema, "EntitySet", "Singleton", "ActionImport", "FunctionImport");
    schema.setEntityContainer(container);
  }

  private void readComplexType(XMLEventReader reader, StartElement element, Schema schema)
      throws XMLStreamException {
    ComplexType complexType = new ComplexType();
    complexType.setProperties(new ArrayList<Property>());
    complexType.setNavigationProperties(new ArrayList<NavigationProperty>());
    complexType.setName(attr(element, "Name"));
    if (attr(element, "BaseType") != null) {
      complexType.setBaseType(new FullQualifiedName(attr(element, "BaseType")));
    }
    complexType.setAbstract(Boolean.parseBoolean(attr(element, "Abstract")));
    complexType.setOpenType(Boolean.parseBoolean(attr(element, "OpenType")));
    readProperties(reader, complexType);

    schema.getComplexTypes().add(complexType);
  }

  private void readProperties(XMLEventReader reader, ComplexType complexType)
      throws XMLStreamException {
    new ElementReader<ComplexType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, ComplexType complexType, String name)
          throws XMLStreamException {
        if (name.equals("Property")) {
          complexType.getProperties().add(readProperty(element));
        } else if (name.equals("NavigationProperty")) {
          complexType.getNavigationProperties().add(readNavigationProperty(reader, element));
        }
      }
    }.read(reader, null, complexType, "Property", "NavigationProperty");
  }

  abstract class ElementReader<T> {
    void read(XMLEventReader reader, StartElement element, T t, String... names)
        throws XMLStreamException {
      while (reader.hasNext()) {
        XMLEvent event = reader.peek();

        event = skipAnnotations(reader, event);

        if (!event.isStartElement() && !event.isEndElement()) {
          reader.nextEvent();
          continue;
        }

        boolean hit = false;

        for (int i = 0; i < names.length; i++) {
          if (event.isStartElement()) {
            element = event.asStartElement();
            if (element.getName().getLocalPart().equals(names[i])) {
              reader.nextEvent(); // advance cursor
              // System.out.println("reading = "+names[i]);
              build(reader, element, t, names[i]);
              hit = true;
            }
          }
          if (event.isEndElement()) {
            EndElement e = event.asEndElement();
            if (e.getName().getLocalPart().equals(names[i])) {
              reader.nextEvent(); // advance cursor
              // System.out.println("done reading = "+names[i]);
              hit = true;
            }
          }
        }
        if (!hit) {
          break;
        }
      }
    }

    private XMLEvent skipAnnotations(XMLEventReader reader, XMLEvent event)
        throws XMLStreamException {
      boolean skip = false;

      while (reader.hasNext()) {
        if (event.isStartElement()) {
          StartElement element = event.asStartElement();
          if (element.getName().getLocalPart().equals("Annotation")) {
            skip = true;
          }
        }
        if (event.isEndElement()) {
          EndElement element = event.asEndElement();
          if (element.getName().getLocalPart().equals("Annotation")) {
            return reader.peek();
          }
        }
        if (skip) {
          event = reader.nextEvent();
        } else {
          return event;
        }
      }
      return event;
    }

    abstract void build(XMLEventReader reader, StartElement element, T t, String name)
        throws XMLStreamException;
  }
}
