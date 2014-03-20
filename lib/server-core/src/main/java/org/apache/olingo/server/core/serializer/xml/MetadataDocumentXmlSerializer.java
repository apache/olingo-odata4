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
package org.apache.olingo.server.core.serializer.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;

public class MetadataDocumentXmlSerializer {

  private final Edm edm;

  private final static String EDMX = "Edmx";
  private final static String PREFIX_EDMX = "edmx";
  private final static String NS_EDMX = "http://docs.oasis-open.org/odata/ns/edmx";

  private final static String NS_EDM = "http://docs.oasis-open.org/odata/ns/edm";

  public MetadataDocumentXmlSerializer(final Edm edm) {
    this.edm = edm;
  }

  public void writeMetadataDocument(final XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartDocument();
    writer.setPrefix(PREFIX_EDMX, NS_EDMX);
    writer.setDefaultNamespace(NS_EDMX);
    writer.writeStartElement(PREFIX_EDMX, EDMX, NS_EDMX);
    writer.writeAttribute("Version", "4.0");
    writer.writeNamespace(PREFIX_EDMX, NS_EDMX);

    appendReference(writer);
    appendDataServices(writer);

    writer.writeEndDocument();

  }

  private void appendDataServices(final XMLStreamWriter writer) throws XMLStreamException {
    writer.setDefaultNamespace(NS_EDM);
//    writer.writeStartElement(PREFIX_EDM, "DataServices", NS_EDMX);
    writer.writeStartElement(NS_EDMX, "DataServices");
    for (EdmSchema schema : edm.getSchemas()) {
      appendSchema(writer, schema);
    }
    writer.writeEndElement();
  }

  private void appendSchema(final XMLStreamWriter writer, final EdmSchema schema) throws XMLStreamException {
    writer.writeStartElement(NS_EDM, "Schema");
    writer.writeDefaultNamespace(NS_EDM);
    writer.writeAttribute("Namespace", schema.getNamespace());
    writer.writeAttribute("Alias", schema.getAlias());

    // EnumTypes
    appendEnumTypes(writer, schema.getEnumTypes());

    // EntityTypes
    appendEntityTypes(writer, schema.getEntityTypes());

    // ComplexTypes
    appendComplexTypes(writer, schema.getComplexTypes());

    // TypeDefinitions
    // TODO: TypeDefinitions

    // Actions
    appendActions(writer, schema.getActions());

    // Functions
    appendFunctions(writer, schema.getFunctions());

    // EntityContainer
    appendEntityContainer(writer, schema.getEntityContainer());

    writer.writeEndElement();
  }

  private void appendEntityContainer(final XMLStreamWriter writer, final EdmEntityContainer container)
      throws XMLStreamException {
    if (container != null) {
      writer.writeStartElement("EntityContainer");

      writer.writeAttribute("Name", container.getName());
      // TODO: extends attribute

      // EntitySets
      appendEntitySets(writer, container.getEntitySets());

      // Singletons
      appendSingletons(writer, container.getSingletons());

      // ActionImports
      appendActionImports(writer, container.getActionImports());

      // FunctionImports
      appendFunctionImports(writer, container.getFunctionImports(), container.getNamespace());

      writer.writeEndElement();
    }
  }

  private void appendFunctionImports(final XMLStreamWriter writer, final List<EdmFunctionImport> functionImports,
      final String containerNamespace) throws XMLStreamException {
    for (EdmFunctionImport functionImport : functionImports) {
      writer.writeStartElement("FunctionImport");
      writer.writeAttribute("Name", functionImport.getName());
      writer.writeAttribute("Function", functionImport.getFunctionFqn().getFullQualifiedNameAsString());
      EdmEntitySet returnedEntitySet = functionImport.getReturnedEntitySet();
      if (returnedEntitySet != null) {
        writer.writeAttribute("EntitySet", containerNamespace + "." + returnedEntitySet.getName());
      }
      writer.writeAttribute("IncludeInServiceDocument", "" + functionImport.isIncludeInServiceDocument());

      // TODO: Annotations
      writer.writeEndElement();
    }
  }

  private void appendActionImports(final XMLStreamWriter writer, final List<EdmActionImport> actionImports)
      throws XMLStreamException {
    for (EdmActionImport actionImport : actionImports) {
      writer.writeStartElement("ActionImport");
      writer.writeAttribute("Name", actionImport.getName());
      writer.writeAttribute("Action", getFullQualifiedName(actionImport.getAction(), false));
      // TODO: Annotations
      writer.writeEndElement();
    }
  }

  private void appendSingletons(final XMLStreamWriter writer, final List<EdmSingleton> singletons)
      throws XMLStreamException {
    // TODO: Merge with entity set method
    for (EdmSingleton singleton : singletons) {
      writer.writeStartElement("Singleton");
      writer.writeAttribute("Name", singleton.getName());
      writer.writeAttribute("EntityType", getFullQualifiedName(singleton.getEntityType(), false));

      // TODO: NavigationProperty Bindigs at edm api level

      // TODO: Annotations
      writer.writeEndElement();
    }

  }

  private void appendEntitySets(final XMLStreamWriter writer, final List<EdmEntitySet> entitySets)
      throws XMLStreamException {
    for (EdmEntitySet entitySet : entitySets) {
      writer.writeStartElement("EntitySet");
      writer.writeAttribute("Name", entitySet.getName());
      writer.writeAttribute("EntityType", getFullQualifiedName(entitySet.getEntityType(), false));

      // TODO: NavigationProperty Bindigs at edm api level

      // TODO: Annotations
      writer.writeEndElement();
    }
  }

  private void appendFunctions(final XMLStreamWriter writer, final List<EdmFunction> functions)
      throws XMLStreamException {
    for (EdmFunction function : functions) {
      writer.writeStartElement("Function");
      writer.writeAttribute("Name", function.getName());
      writer.writeAttribute("IsBound", "" + function.isBound());
      writer.writeAttribute("IsComposable", "" + function.isComposable());

      // TODO: move to separate method like for actions
      for (String parameterName : function.getParameterNames()) {
        EdmParameter parameter = function.getParameter(parameterName);
        writer.writeEmptyElement("Parameter");
        writer.writeAttribute("Name", parameterName);
        writer.writeAttribute("Type", getFullQualifiedName(parameter.getType(), parameter.isCollection()));
        // TODO: Parameter facets
      }

      EdmReturnType returnType = function.getReturnType();
      if (returnType != null) {
        writer.writeEmptyElement("ReturnType");
        writer.writeAttribute("Type", getFullQualifiedName(returnType.getType(), returnType.isCollection()));
        // TODO: Return type facets
      }

      writer.writeEndElement();
    }
  }

  private void appendActions(final XMLStreamWriter writer, final List<EdmAction> actions) throws XMLStreamException {
    for (EdmAction action : actions) {
      writer.writeStartElement("Action");
      writer.writeAttribute("Name", action.getName());
      writer.writeAttribute("IsBound", "" + action.isBound());

      for (String parameterName : action.getParameterNames()) {
        EdmParameter parameter = action.getParameter(parameterName);
        writer.writeEmptyElement("Parameter");
        writer.writeAttribute("Name", parameterName);
        writer.writeAttribute("Type", getFullQualifiedName(parameter.getType(), parameter.isCollection()));
        // TODO: Parameter facets
      }

      EdmReturnType returnType = action.getReturnType();
      if (returnType != null) {
        writer.writeEmptyElement("ReturnType");
        writer.writeAttribute("Type", getFullQualifiedName(returnType.getType(), returnType.isCollection()));
        // TODO: Return type facets
      }

      writer.writeEndElement();
    }
  }

  private void appendComplexTypes(final XMLStreamWriter writer, final List<EdmComplexType> complexTypes)
      throws XMLStreamException {
    for (EdmComplexType complexType : complexTypes) {
      writer.writeStartElement("ComplexType");
      writer.writeAttribute("Name", complexType.getName());

      if (complexType.getBaseType() != null) {
        writer.writeAttribute("BaseType", getFullQualifiedName(complexType.getBaseType(), false));
      }

      appendProperties(writer, complexType);

      appendNavigationProperties(writer, complexType);

      writer.writeEndElement();
    }
  }

  private void appendEntityTypes(final XMLStreamWriter writer, final List<EdmEntityType> entityTypes)
      throws XMLStreamException {
    for (EdmEntityType entityType : entityTypes) {
      writer.writeStartElement("EntityType");
      writer.writeAttribute("Name", entityType.getName());

      if (entityType.hasStream()) {
        writer.writeAttribute("HasStream", "" + entityType.hasStream());
      }

      if (entityType.getBaseType() != null) {
        writer.writeAttribute("BaseType", getFullQualifiedName(entityType.getBaseType(), false));
      }

      appendKey(writer, entityType);

      appendProperties(writer, entityType);

      appendNavigationProperties(writer, entityType);

      writer.writeEndElement();
    }
  }

  private void appendNavigationProperties(final XMLStreamWriter writer, final EdmStructuredType type)
      throws XMLStreamException {
    List<String> navigationPropertyNames = type.getNavigationPropertyNames();
    if (type.getBaseType() != null) {
      navigationPropertyNames.removeAll(type.getBaseType().getNavigationPropertyNames());
    }
    for (String navigationPropertyName : navigationPropertyNames) {
      EdmNavigationProperty navigationProperty = type.getNavigationProperty(navigationPropertyName);

      writer.writeEmptyElement("NavigationProperty");
      writer.writeAttribute("Name", navigationPropertyName);
      writer.writeAttribute("Type", getFullQualifiedName(navigationProperty.getType(), navigationProperty
          .isCollection()));
      if (navigationProperty.isNullable() != null) {
        writer.writeAttribute("Nullable", "" + navigationProperty.isNullable());
      }

      if (navigationProperty.getPartner() != null) {
        EdmNavigationProperty partner = navigationProperty.getPartner();
        writer.writeAttribute("Partner", partner.getName());
      }
    }
  }

  private void appendProperties(final XMLStreamWriter writer, final EdmStructuredType type) throws XMLStreamException {
    List<String> propertyNames = type.getPropertyNames();
    if (type.getBaseType() != null) {
      propertyNames.removeAll(type.getBaseType().getPropertyNames());
    }
    for (String propertyName : propertyNames) {
      EdmProperty property = type.getStructuralProperty(propertyName);
      writer.writeEmptyElement("Property");
      writer.writeAttribute("Name", propertyName);
      writer.writeAttribute("Type", getFullQualifiedName(property.getType(), property.isCollection()));

      // Facets
      if (property.isNullable() != null) {
        writer.writeAttribute("Nullable", "" + property.isNullable());
      }

      if (property.isUnicode() != null) {
        writer.writeAttribute("Unicode", "" + property.isUnicode());
      }

      if (property.getDefaultValue() != null) {
        writer.writeAttribute("DefaultValue", property.getDefaultValue());
      }

      if (property.getMaxLength() != null) {
        writer.writeAttribute("MaxLength", "" + property.getMaxLength());
      }

      if (property.getPrecision() != null) {
        writer.writeAttribute("Precision", "" + property.getPrecision());
      }

      if (property.getScale() != null) {
        writer.writeAttribute("Scale", "" + property.getScale());
      }
    }
  }

  private void appendKey(final XMLStreamWriter writer, final EdmEntityType entityType) throws XMLStreamException {
    List<EdmKeyPropertyRef> keyPropertyRefs = entityType.getKeyPropertyRefs();
    if (keyPropertyRefs != null && !keyPropertyRefs.isEmpty()) {
      writer.writeStartElement("Key");
      for (EdmKeyPropertyRef keyRef : keyPropertyRefs) {
        writer.writeEmptyElement("PropertyRef");
        String keyName = null;
        if (keyRef.getPath() != null) {
          keyName = keyRef.getPath() + "/" + keyRef.getKeyPropertyName();
        } else {
          keyName = keyRef.getKeyPropertyName();
        }
        writer.writeAttribute("Name", keyName);

        if (keyRef.getAlias() != null) {
          writer.writeAttribute("Alias", keyRef.getAlias());
        }
      }
      writer.writeEndElement();
    }
  }

  private void appendEnumTypes(final XMLStreamWriter writer, final List<EdmEnumType> enumTypes)
      throws XMLStreamException {
    for (EdmEnumType enumType : enumTypes) {
      writer.writeStartElement("EnumType");
      writer.writeAttribute("Name", enumType.getName());
      writer.writeAttribute("isFlags", "" + enumType.isFlags());
      writer.writeAttribute("UnderlyingType", getFullQualifiedName(enumType.getUnderlyingType(), false));

      for (String memberName : enumType.getMemberNames()) {
        writer.writeEmptyElement("Member");
        writer.writeAttribute("Name", memberName);
        writer.writeAttribute("Value", enumType.getMember(memberName).getValue());
      }

      writer.writeEndElement();
    }
  }

  private String getFullQualifiedName(final EdmType type, final boolean isCollection) {
    if (isCollection) {
      return "Collection(" + type.getNamespace() + "." + type.getName() + ")";
    } else {
      return type.getNamespace() + "." + type.getName();
    }
  }

  private void appendReference(final XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartElement(NS_EDMX, "Reference");
    // TODO: Where is this value comming from?
    writer.writeAttribute("Uri", "http://docs.oasis-open.org/odata/odata/v4.0/cs02/vocabularies/Org.OData.Core.V1.xml");
    writer.writeEmptyElement(NS_EDMX, "Include");
    // TODO: Where is this value comming from?
    writer.writeAttribute("Namespace", "Org.OData.Core.V1");
    // TODO: Where is this value comming from?
    writer.writeAttribute("Alias", "Core");
    writer.writeEndElement();
  }

}
