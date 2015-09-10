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
import java.util.Collections;
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
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDelete;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDeleteAction;
import org.apache.olingo.commons.api.edm.provider.CsdlOperation;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlReferentialConstraint;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;

/**
 * This class can convert a CSDL document into EDMProvider object
 */
public class MetadataParser {

  public CsdlEdmProvider buildEdmProvider(Reader csdl) throws XMLStreamException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader = xmlInputFactory.createXMLEventReader(csdl);

    SchemaBasedEdmProvider provider = new SchemaBasedEdmProvider();
    new ElementReader<SchemaBasedEdmProvider>() {
      @Override
      void build(XMLEventReader reader, StartElement element, SchemaBasedEdmProvider provider,
          String name) throws XMLStreamException {
        String version = attr(element, "Version");
        if ("4.0".equals(version)) {
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

    CsdlSchema schema = new CsdlSchema();
    schema.setComplexTypes(new ArrayList<CsdlComplexType>());
    schema.setActions(new ArrayList<CsdlAction>());
    schema.setEntityTypes(new ArrayList<CsdlEntityType>());
    schema.setEnumTypes(new ArrayList<CsdlEnumType>());
    schema.setFunctions(new ArrayList<CsdlFunction>());
    schema.setTerms(new ArrayList<CsdlTerm>());
    schema.setTypeDefinitions(new ArrayList<CsdlTypeDefinition>());

    new ElementReader<CsdlSchema>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlSchema schema, String name)
          throws XMLStreamException {
        schema.setNamespace(attr(element, "Namespace"));
        schema.setAlias(attr(element, "Alias"));
        readSchemaContents(reader, schema);
      }
    }.read(reader, element, schema, "Schema");
    provider.addSchema(schema);
  }

  private void readSchemaContents(XMLEventReader reader, CsdlSchema schema) throws XMLStreamException {
    new ElementReader<CsdlSchema>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlSchema schema, String name)
          throws XMLStreamException {
        if (name.equals("Action")) {
          readAction(reader, element, schema);
//        } else if (name.equals("Annotations")) {
//        } else if (name.equals("Annotation")) {
          // TODO: Add support for annotations
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

  private void readAction(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {

    CsdlAction action = new CsdlAction();
    action.setParameters(new ArrayList<CsdlParameter>());
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
    if (type != null && type.startsWith("Collection(") && type.endsWith(")")) {
      return new FullQualifiedName(type.substring(11, type.length() - 1));
    }
    return new FullQualifiedName(type);
  }

  private boolean isCollectionType(StartElement element) {
    String type = attr(element, "Type");
    if (type != null && type.startsWith("Collection(") && type.endsWith(")")) {
      return true;
    }
    return false;
  }

  private void readReturnType(StartElement element, CsdlOperation operation) {
    CsdlReturnType returnType = new CsdlReturnType();
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
      returnType.setSrid(SRID.valueOf(srid));
    }
    operation.setReturnType(returnType);
  }

  private void readParameter(StartElement element, CsdlOperation operation) {
    CsdlParameter parameter = new CsdlParameter();
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
      parameter.setSrid(SRID.valueOf(srid));
    }
    operation.getParameters().add(parameter);
  }

  private CsdlTypeDefinition readTypeDefinition(StartElement element) {
    CsdlTypeDefinition td = new CsdlTypeDefinition();
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
      td.setSrid(SRID.valueOf(srid));
    }
    return td;
  }

  private CsdlTerm readTerm(StartElement element) {
    CsdlTerm term = new CsdlTerm();
    term.setName(attr(element, "Name"));
    term.setType(attr(element, "Type"));
    if (attr(element, "BaseTerm") != null) {
      term.setBaseTerm(attr(element, "BaseTerm"));
    }
    if (attr(element, "DefaultValue") != null) {
      term.setDefaultValue(attr(element, "DefaultValue"));
    }
    if (attr(element, "AppliesTo") != null) {
      term.setAppliesTo(Collections.singletonList(attr(element, "AppliesTo")));
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
      term.setSrid(SRID.valueOf(srid));
    }
    return term;
  }

  private void readFunction(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    CsdlFunction function = new CsdlFunction();
    function.setParameters(new ArrayList<CsdlParameter>());
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

  private void readOperationParameters(XMLEventReader reader, final CsdlOperation operation)
      throws XMLStreamException {
    new ElementReader<CsdlOperation>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlOperation operation, String name)
          throws XMLStreamException {
        if (name.equals("Parameter")) {
          readParameter(element, operation);
        } else if (name.equals("ReturnType")) {
          readReturnType(element, operation);
        }
      }
    }.read(reader, null, operation, "Parameter", "ReturnType");
  }

  private void readEnumType(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    CsdlEnumType type = new CsdlEnumType();
    type.setMembers(new ArrayList<CsdlEnumMember>());
    type.setName(attr(element, "Name"));
    if (attr(element, "UnderlyingType") != null) {
      type.setUnderlyingType(new FullQualifiedName(attr(element, "UnderlyingType")));
    }
    type.setFlags(Boolean.parseBoolean(attr(element, "IsFlags")));

    readEnumMembers(reader, element, type);
    schema.getEnumTypes().add(type);
  }

  private void readEnumMembers(XMLEventReader reader, StartElement element, CsdlEnumType type)
      throws XMLStreamException {
    new ElementReader<CsdlEnumType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlEnumType type, String name)
          throws XMLStreamException {
        CsdlEnumMember member = new CsdlEnumMember();
        member.setName(attr(element, "Name"));
        member.setValue(attr(element, "Value"));
        type.getMembers().add(member);
      }
    }.read(reader, element, type, "Member");
  }

  private void readEntityType(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    CsdlEntityType entityType = new CsdlEntityType();
    entityType.setProperties(new ArrayList<CsdlProperty>());
    entityType.setNavigationProperties(new ArrayList<CsdlNavigationProperty>());
    entityType.setKey(new ArrayList<CsdlPropertyRef>());
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

  private void readEntityProperties(XMLEventReader reader, CsdlEntityType entityType)
      throws XMLStreamException {
    new ElementReader<CsdlEntityType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlEntityType entityType, String name)
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

  private void readKey(XMLEventReader reader, StartElement element, CsdlEntityType entityType)
      throws XMLStreamException {
    new ElementReader<CsdlEntityType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlEntityType entityType, String name)
          throws XMLStreamException {
        CsdlPropertyRef ref = new CsdlPropertyRef();
        ref.setName(attr(element, "Name"));
        ref.setAlias(attr(element, "Alias"));
        entityType.getKey().add(ref);
      }
    }.read(reader, element, entityType, "PropertyRef");
  }

  private CsdlNavigationProperty readNavigationProperty(XMLEventReader reader, StartElement element)
      throws XMLStreamException {
    CsdlNavigationProperty property = new CsdlNavigationProperty();
    property.setReferentialConstraints(new ArrayList<CsdlReferentialConstraint>());

    property.setName(attr(element, "Name"));
    property.setType(readType(element));
    property.setCollection(isCollectionType(element));
    property.setNullable(Boolean.parseBoolean(attr(element, "Nullable")));
    property.setPartner(attr(element, "Partner"));
    property.setContainsTarget(Boolean.parseBoolean(attr(element, "ContainsTarget")));

    new ElementReader<CsdlNavigationProperty>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlNavigationProperty property,
          String name) throws XMLStreamException {
        if (name.equals("ReferentialConstraint")) {
          CsdlReferentialConstraint constraint = new CsdlReferentialConstraint();
          constraint.setProperty(attr(element, "Property"));
          constraint.setReferencedProperty(attr(element, "ReferencedProperty"));
          property.getReferentialConstraints().add(constraint);
        } else if (name.equals("OnDelete")) {
          property.setOnDelete(new CsdlOnDelete().setAction(CsdlOnDeleteAction.valueOf(attr(element, "Action"))));
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

  private CsdlProperty readProperty(StartElement element) {
    CsdlProperty property = new CsdlProperty();
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
      property.setSrid(SRID.valueOf(srid));
    }
    String defaultValue = attr(element, "DefaultValue");
    if (defaultValue != null) {
      property.setDefaultValue(defaultValue);
    }
    return property;
  }

  private void readEntityContainer(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    final CsdlEntityContainer container = new CsdlEntityContainer();
    container.setName(attr(element, "Name"));
    if (attr(element, "Extends") != null) {
      container.setExtendsContainer(attr(element, "Extends"));
    }
    container.setActionImports(new ArrayList<CsdlActionImport>());
    container.setFunctionImports(new ArrayList<CsdlFunctionImport>());
    container.setEntitySets(new ArrayList<CsdlEntitySet>());
    container.setSingletons(new ArrayList<CsdlSingleton>());

    new ElementReader<CsdlSchema>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlSchema schema, String name)
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

      private void readFunctionImport(StartElement element, CsdlEntityContainer container) {
        CsdlFunctionImport functionImport = new CsdlFunctionImport();
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

      private void readActionImport(StartElement element, CsdlEntityContainer container) {
        CsdlActionImport actionImport = new CsdlActionImport();
        actionImport.setName(attr(element, "Name"));
        actionImport.setAction(new FullQualifiedName(attr(element, "Action")));

        String entitySet = attr(element, "EntitySet");
        if (entitySet != null) {
          actionImport.setEntitySet(entitySet);
        }
        container.getActionImports().add(actionImport);
      }

      private void readSingleton(XMLEventReader reader, StartElement element,
          CsdlEntityContainer container) throws XMLStreamException {
        CsdlSingleton singleton = new CsdlSingleton();
        singleton.setNavigationPropertyBindings(new ArrayList<CsdlNavigationPropertyBinding>());
        singleton.setName(attr(element, "Name"));
        singleton.setType(new FullQualifiedName(attr(element, "Type")));
        singleton.setNavigationPropertyBindings(new ArrayList<CsdlNavigationPropertyBinding>());
        readNavigationPropertyBindings(reader, element, singleton.getNavigationPropertyBindings());
        container.getSingletons().add(singleton);
      }

      private void readEntitySet(XMLEventReader reader, StartElement element,
          CsdlEntityContainer container) throws XMLStreamException {
        CsdlEntitySet entitySet = new CsdlEntitySet();
        entitySet.setName(attr(element, "Name"));
        entitySet.setType(new FullQualifiedName(attr(element, "EntityType")));
        entitySet.setIncludeInServiceDocument(Boolean.parseBoolean(attr(element,
            "IncludeInServiceDocument")));
        entitySet.setNavigationPropertyBindings(new ArrayList<CsdlNavigationPropertyBinding>());
        readNavigationPropertyBindings(reader, element, entitySet.getNavigationPropertyBindings());
        container.getEntitySets().add(entitySet);
      }

      private void readNavigationPropertyBindings(XMLEventReader reader, StartElement element,
          List<CsdlNavigationPropertyBinding> bindings) throws XMLStreamException {
        new ElementReader<List<CsdlNavigationPropertyBinding>>() {
          @Override
          void build(XMLEventReader reader, StartElement element,
              List<CsdlNavigationPropertyBinding> bindings, String name) throws XMLStreamException {
            CsdlNavigationPropertyBinding binding = new CsdlNavigationPropertyBinding();
            binding.setPath(attr(element, "Path"));
            binding.setTarget(attr(element, "Target"));
            bindings.add(binding);
          }

        }.read(reader, element, bindings, "NavigationPropertyBinding");
      }
    }.read(reader, element, schema, "EntitySet", "Singleton", "ActionImport", "FunctionImport");
    schema.setEntityContainer(container);
  }

  private void readComplexType(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    CsdlComplexType complexType = new CsdlComplexType();
    complexType.setProperties(new ArrayList<CsdlProperty>());
    complexType.setNavigationProperties(new ArrayList<CsdlNavigationProperty>());
    complexType.setName(attr(element, "Name"));
    if (attr(element, "BaseType") != null) {
      complexType.setBaseType(new FullQualifiedName(attr(element, "BaseType")));
    }
    complexType.setAbstract(Boolean.parseBoolean(attr(element, "Abstract")));
    complexType.setOpenType(Boolean.parseBoolean(attr(element, "OpenType")));
    readProperties(reader, complexType);

    schema.getComplexTypes().add(complexType);
  }

  private void readProperties(XMLEventReader reader, CsdlComplexType complexType)
      throws XMLStreamException {
    new ElementReader<CsdlComplexType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlComplexType complexType, String name)
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

        for (String name : names) {
          if (event.isStartElement()) {
            element = event.asStartElement();
            if (element.getName().getLocalPart().equals(name)) {
              reader.nextEvent(); // advance cursor
              // System.out.println("reading = "+names[i]);
              build(reader, element, t, name);
              hit = true;
            }
          }
          if (event.isEndElement()) {
            EndElement e = event.asEndElement();
            if (e.getName().getLocalPart().equals(name)) {
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
