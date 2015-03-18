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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReferentialConstraint;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.api.edmx.EdmxReferenceIncludeAnnotation;
import org.apache.olingo.server.api.serializer.ODataSerializer;

public class MetadataDocumentXmlSerializer {

  private static final String TRUE = "true";
  private static final String XML_EXTENDS = "Extends";
  private static final String XML_TARGET = "Target";
  private static final String XML_PATH = "Path";
  private static final String XML_NAVIGATION_PROPERTY_BINDING = "NavigationPropertyBinding";
  private static final String XML_VALUE = "Value";
  private static final String XML_MEMBER = "Member";
  private static final String XML_UNDERLYING_TYPE = "UnderlyingType";
  private static final String XML_IS_FLAGS = "IsFlags";
  private static final String XML_ENUM_TYPE = "EnumType";
  private static final String XML_PROPERTY_REF = "PropertyRef";
  private static final String XML_KEY = "Key";
  private static final String XML_SCALE = "Scale";
  private static final String XML_PRECISION = "Precision";
  private static final String XML_MAX_LENGTH = "MaxLength";
  private static final String XML_DEFAULT_VALUE = "DefaultValue";
  private static final String XML_UNICODE = "Unicode";
  private static final String XML_PROPERTY = "Property";
  private static final String XML_PARTNER = "Partner";
  private static final String XML_NULLABLE = "Nullable";
  private static final String XML_NAVIGATION_PROPERTY = "NavigationProperty";
  private static final String XML_HAS_STREAM = "HasStream";
  private static final String XML_BASE_TYPE = "BaseType";
  private static final String XML_COMPLEX_TYPE = "ComplexType";
  private static final String XML_RETURN_TYPE = "ReturnType";
  private static final String XML_TYPE = "Type";
  private static final String XML_PARAMETER = "Parameter";
  private static final String XML_IS_COMPOSABLE = "IsComposable";
  private static final String XML_IS_BOUND = "IsBound";
  private static final String XML_ENTITY_TYPE = "EntityType";
  private static final String XML_SINGLETON = "Singleton";
  private static final String XML_ACTION = "Action";
  private static final String XML_ACTION_IMPORT = "ActionImport";
  private static final String XML_INCLUDE_IN_SERVICE_DOCUMENT = "IncludeInServiceDocument";
  private static final String XML_ENTITY_SET = "EntitySet";
  private static final String XML_FUNCTION = "Function";
  private static final String XML_FUNCTION_IMPORT = "FunctionImport";
  private static final String XML_NAME = "Name";
  private static final String XML_ENTITY_CONTAINER = "EntityContainer";
  private static final String XML_ALIAS = "Alias";
  private static final String XML_NAMESPACE = "Namespace";
  private static final String XML_TYPE_DEFINITION = "TypeDefinition";
  private static final String REFERENCE = "Reference";
  private static final String INCLUDE = "Include";
  private static final String INCLUDE_ANNOTATIONS = "IncludeAnnotations";
  private static final String XML_TERM_NAMESPACE = "TermNamespace";
  private static final String XML_TARGET_NAMESPACE = "TargetNamespace";
  private static final String XML_QUALIFIER = "Qualifier";
  private static final String URI = "Uri";
  private static final String SCHEMA = "Schema";
  private static final String DATA_SERVICES = "DataServices";
  private static final String ABSTRACT = "Abstract";

  private final static String EDMX = "Edmx";
  private final static String PREFIX_EDMX = "edmx";
  private final static String NS_EDMX = "http://docs.oasis-open.org/odata/ns/edmx";

  private final static String NS_EDM = "http://docs.oasis-open.org/odata/ns/edm";
  private static final String XML_ENTITY_SET_PATH = "EntitySetPath";

  private final ServiceMetadata serviceMetadata;
  private final Map<String, String> namespaceToAlias = new HashMap<String, String>();

  public MetadataDocumentXmlSerializer(final ServiceMetadata serviceMetadata) {
    this.serviceMetadata = serviceMetadata;
  }

  // TODO: Annotations in metadata document

  public void writeMetadataDocument(final XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
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
    writer.writeStartElement(NS_EDMX, DATA_SERVICES);
    for (EdmSchema schema : serviceMetadata.getEdm().getSchemas()) {
      appendSchema(writer, schema);
    }
    writer.writeEndElement();
  }

  private void appendSchema(final XMLStreamWriter writer, final EdmSchema schema) throws XMLStreamException {
    writer.writeStartElement(NS_EDM, SCHEMA);
    writer.writeDefaultNamespace(NS_EDM);
    writer.writeAttribute(XML_NAMESPACE, schema.getNamespace());
    if (schema.getAlias() != null) {
      writer.writeAttribute(XML_ALIAS, schema.getAlias());
      namespaceToAlias.put(schema.getNamespace(), schema.getAlias());
    }

    // EnumTypes
    appendEnumTypes(writer, schema.getEnumTypes());

    // EntityTypes
    appendEntityTypes(writer, schema.getEntityTypes());

    // ComplexTypes
    appendComplexTypes(writer, schema.getComplexTypes());

    // TypeDefinitions
    appendTypeDefinitions(writer, schema.getTypeDefinitions());

    // Actions
    appendActions(writer, schema.getActions());

    // Functions
    appendFunctions(writer, schema.getFunctions());

    // EntityContainer
    appendEntityContainer(writer, schema.getEntityContainer());

    writer.writeEndElement();
  }

  private void appendTypeDefinitions(final XMLStreamWriter writer, final List<EdmTypeDefinition> typeDefinitions)
      throws XMLStreamException {
    for (EdmTypeDefinition definition : typeDefinitions) {
      writer.writeEmptyElement(XML_TYPE_DEFINITION);
      writer.writeAttribute(XML_NAME, definition.getName());
      writer.writeAttribute(XML_UNDERLYING_TYPE, getFullQualifiedName(definition.getUnderlyingType(), false));

      // Facets
      if (definition.getMaxLength() != null) {
        writer.writeAttribute(XML_MAX_LENGTH, "" + definition.getMaxLength());
      }

      if (definition.getPrecision() != null) {
        writer.writeAttribute(XML_PRECISION, "" + definition.getPrecision());
      }

      if (definition.getScale() != null) {
        writer.writeAttribute(XML_SCALE, "" + definition.getScale());
      }
    }
  }

  private void appendEntityContainer(final XMLStreamWriter writer, final EdmEntityContainer container)
      throws XMLStreamException {
    if (container != null) {
      writer.writeStartElement(XML_ENTITY_CONTAINER);

      writer.writeAttribute(XML_NAME, container.getName());
      FullQualifiedName parentContainerName = container.getParentContainerName();
      if (parentContainerName != null) {
        String parentContainerNameString;
        if (namespaceToAlias.get(parentContainerName.getNamespace()) != null) {
          parentContainerNameString =
              namespaceToAlias.get(parentContainerName.getNamespace()) + "." + parentContainerName.getName();
        } else {
          parentContainerNameString = parentContainerName.getFullQualifiedNameAsString();
        }
        writer.writeAttribute(XML_EXTENDS, parentContainerNameString);
      }

      // EntitySets
      appendEntitySets(writer, container.getEntitySets());

      // Singletons
      appendSingletons(writer, container.getSingletons());

      // ActionImports
      appendActionImports(writer, container.getActionImports());

      // FunctionImports
      String containerNamespace;
      if (namespaceToAlias.get(container.getNamespace()) != null) {
        containerNamespace = namespaceToAlias.get(container.getNamespace());
      } else {
        containerNamespace = container.getNamespace();
      }
      appendFunctionImports(writer, container.getFunctionImports(), containerNamespace);

      writer.writeEndElement();
    }
  }

  private void appendFunctionImports(final XMLStreamWriter writer, final List<EdmFunctionImport> functionImports,
      final String containerNamespace) throws XMLStreamException {
    for (EdmFunctionImport functionImport : functionImports) {
      writer.writeStartElement(XML_FUNCTION_IMPORT);
      writer.writeAttribute(XML_NAME, functionImport.getName());

      String functionFQNString;
      FullQualifiedName functionFqn = functionImport.getFunctionFqn();
      if (namespaceToAlias.get(functionFqn.getNamespace()) != null) {
        functionFQNString = namespaceToAlias.get(functionFqn.getNamespace()) + "." + functionFqn.getName();
      } else {
        functionFQNString = functionFqn.getFullQualifiedNameAsString();
      }
      writer.writeAttribute(XML_FUNCTION, functionFQNString);

      EdmEntitySet returnedEntitySet = functionImport.getReturnedEntitySet();
      if (returnedEntitySet != null) {
        writer.writeAttribute(XML_ENTITY_SET, containerNamespace + "." + returnedEntitySet.getName());
      }
      writer.writeAttribute(XML_INCLUDE_IN_SERVICE_DOCUMENT, "" + functionImport.isIncludeInServiceDocument());

      writer.writeEndElement();
    }
  }

  private void appendActionImports(final XMLStreamWriter writer, final List<EdmActionImport> actionImports)
      throws XMLStreamException {
    for (EdmActionImport actionImport : actionImports) {
      writer.writeStartElement(XML_ACTION_IMPORT);
      writer.writeAttribute(XML_NAME, actionImport.getName());
      writer.writeAttribute(XML_ACTION, getAliasedFullQualifiedName(actionImport.getUnboundAction(), false));
      writer.writeEndElement();
    }
  }

  private void appendSingletons(final XMLStreamWriter writer, final List<EdmSingleton> singletons)
      throws XMLStreamException {
    for (EdmSingleton singleton : singletons) {
      writer.writeStartElement(XML_SINGLETON);
      writer.writeAttribute(XML_NAME, singleton.getName());
      writer.writeAttribute(XML_ENTITY_TYPE, getAliasedFullQualifiedName(singleton.getEntityType(), false));

      appendNavigationPropertyBindings(writer, singleton);
      writer.writeEndElement();
    }

  }

  private void appendNavigationPropertyBindings(final XMLStreamWriter writer, final EdmBindingTarget bindingTarget)
      throws XMLStreamException {
    if (bindingTarget.getNavigationPropertyBindings() != null) {
      for (EdmNavigationPropertyBinding binding : bindingTarget.getNavigationPropertyBindings()) {
        writer.writeEmptyElement(XML_NAVIGATION_PROPERTY_BINDING);
        writer.writeAttribute(XML_PATH, binding.getPath());
        writer.writeAttribute(XML_TARGET, binding.getTarget());
      }
    }
  }

  private void appendEntitySets(final XMLStreamWriter writer, final List<EdmEntitySet> entitySets)
      throws XMLStreamException {
    for (EdmEntitySet entitySet : entitySets) {
      writer.writeStartElement(XML_ENTITY_SET);
      writer.writeAttribute(XML_NAME, entitySet.getName());
      writer.writeAttribute(XML_ENTITY_TYPE, getAliasedFullQualifiedName(entitySet.getEntityType(), false));

      appendNavigationPropertyBindings(writer, entitySet);
      writer.writeEndElement();
    }
  }

  private void appendFunctions(final XMLStreamWriter writer, final List<EdmFunction> functions)
      throws XMLStreamException {
    for (EdmFunction function : functions) {
      writer.writeStartElement(XML_FUNCTION);
      writer.writeAttribute(XML_NAME, function.getName());
      if (function.getEntitySetPath() != null) {
        writer.writeAttribute(XML_ENTITY_SET_PATH, function.getEntitySetPath());
      }
      writer.writeAttribute(XML_IS_BOUND, "" + function.isBound());
      writer.writeAttribute(XML_IS_COMPOSABLE, "" + function.isComposable());

      appendOperationParameters(writer, function);

      appendOperationReturnType(writer, function);

      writer.writeEndElement();
    }
  }

  private void appendOperationReturnType(final XMLStreamWriter writer, final EdmOperation operation)
      throws XMLStreamException {
    EdmReturnType returnType = operation.getReturnType();
    if (returnType != null) {
      writer.writeEmptyElement(XML_RETURN_TYPE);
      String returnTypeFqnString;
      if (EdmTypeKind.PRIMITIVE.equals(returnType.getType().getKind())) {
        returnTypeFqnString = getFullQualifiedName(returnType.getType(), returnType.isCollection());
      } else {
        returnTypeFqnString = getAliasedFullQualifiedName(returnType.getType(), returnType.isCollection());
      }
      writer.writeAttribute(XML_TYPE, returnTypeFqnString);

      appendReturnTypeFacets(writer, returnType);
    }
  }

  private void appendOperationParameters(final XMLStreamWriter writer, final EdmOperation operation)
      throws XMLStreamException {
    for (String parameterName : operation.getParameterNames()) {
      EdmParameter parameter = operation.getParameter(parameterName);
      writer.writeEmptyElement(XML_PARAMETER);
      writer.writeAttribute(XML_NAME, parameterName);
      String typeFqnString;
      if (EdmTypeKind.PRIMITIVE.equals(parameter.getType().getKind())) {
        typeFqnString = getFullQualifiedName(parameter.getType(), parameter.isCollection());
      } else {
        typeFqnString = getAliasedFullQualifiedName(parameter.getType(), parameter.isCollection());
      }
      writer.writeAttribute(XML_TYPE, typeFqnString);

      appendParameterFacets(writer, parameter);
    }
  }

  private void appendActions(final XMLStreamWriter writer, final List<EdmAction> actions) throws XMLStreamException {
    for (EdmAction action : actions) {
      writer.writeStartElement(XML_ACTION);
      writer.writeAttribute(XML_NAME, action.getName());
      if (action.getEntitySetPath() != null) {
        writer.writeAttribute(XML_ENTITY_SET_PATH, action.getEntitySetPath());
      }
      writer.writeAttribute(XML_IS_BOUND, "" + action.isBound());

      appendOperationParameters(writer, action);

      appendOperationReturnType(writer, action);

      writer.writeEndElement();
    }
  }

  private void appendReturnTypeFacets(final XMLStreamWriter writer, final EdmReturnType returnType)
      throws XMLStreamException {
    if (returnType.isNullable() == false) {
      writer.writeAttribute(XML_NULLABLE, "" + returnType.isNullable());
    }
    if (returnType.getMaxLength() != null) {
      writer.writeAttribute(XML_MAX_LENGTH, "" + returnType.getMaxLength());
    }
    if (returnType.getPrecision() != null) {
      writer.writeAttribute(XML_PRECISION, "" + returnType.getPrecision());
    }
    if (returnType.getScale() != null) {
      writer.writeAttribute(XML_SCALE, "" + returnType.getScale());
    }
  }

  private void appendParameterFacets(final XMLStreamWriter writer, final EdmParameter parameter)
      throws XMLStreamException {
    if (parameter.isNullable() == false) {
      writer.writeAttribute(XML_NULLABLE, "" + parameter.isNullable());
    }
    if (parameter.getMaxLength() != null) {
      writer.writeAttribute(XML_MAX_LENGTH, "" + parameter.getMaxLength());
    }
    if (parameter.getPrecision() != null) {
      writer.writeAttribute(XML_PRECISION, "" + parameter.getPrecision());
    }
    if (parameter.getScale() != null) {
      writer.writeAttribute(XML_SCALE, "" + parameter.getScale());
    }
  }

  private void appendComplexTypes(final XMLStreamWriter writer, final List<EdmComplexType> complexTypes)
      throws XMLStreamException {
    for (EdmComplexType complexType : complexTypes) {
      writer.writeStartElement(XML_COMPLEX_TYPE);
      writer.writeAttribute(XML_NAME, complexType.getName());

      if (complexType.getBaseType() != null) {
        writer.writeAttribute(XML_BASE_TYPE, getAliasedFullQualifiedName(complexType.getBaseType(), false));
      }

      if (complexType.isAbstract()) {
        writer.writeAttribute(ABSTRACT, TRUE);
      }

      appendProperties(writer, complexType);

      appendNavigationProperties(writer, complexType);

      writer.writeEndElement();
    }
  }

  private void appendEntityTypes(final XMLStreamWriter writer, final List<EdmEntityType> entityTypes)
      throws XMLStreamException {
    for (EdmEntityType entityType : entityTypes) {
      writer.writeStartElement(XML_ENTITY_TYPE);
      writer.writeAttribute(XML_NAME, entityType.getName());

      if (entityType.hasStream()) {
        writer.writeAttribute(XML_HAS_STREAM, "" + entityType.hasStream());
      }

      if (entityType.getBaseType() != null) {
        writer.writeAttribute(XML_BASE_TYPE, getAliasedFullQualifiedName(entityType.getBaseType(), false));
      }

      if (entityType.isAbstract()) {
        writer.writeAttribute(ABSTRACT, TRUE);
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

      writer.writeStartElement(XML_NAVIGATION_PROPERTY);
      writer.writeAttribute(XML_NAME, navigationPropertyName);
      writer.writeAttribute(XML_TYPE, getAliasedFullQualifiedName(navigationProperty.getType(), navigationProperty
          .isCollection()));
      if (navigationProperty.isNullable() == false) {
        writer.writeAttribute(XML_NULLABLE, "" + navigationProperty.isNullable());
      }

      if (navigationProperty.getPartner() != null) {
        EdmNavigationProperty partner = navigationProperty.getPartner();
        writer.writeAttribute(XML_PARTNER, partner.getName());
      }

      if (navigationProperty.getReferentialConstraints() != null) {
        for (EdmReferentialConstraint constraint : navigationProperty.getReferentialConstraints()) {
          writer.writeEmptyElement("ReferentialConstraint");
          writer.writeAttribute(XML_PROPERTY, constraint.getPropertyName());
          writer.writeAttribute("ReferencedProperty", constraint.getReferencedPropertyName());
        }
      }

      writer.writeEndElement();
    }
  }

  private void appendProperties(final XMLStreamWriter writer, final EdmStructuredType type) throws XMLStreamException {
    List<String> propertyNames = type.getPropertyNames();
    if (type.getBaseType() != null) {
      propertyNames.removeAll(type.getBaseType().getPropertyNames());
    }
    for (String propertyName : propertyNames) {
      EdmProperty property = type.getStructuralProperty(propertyName);
      writer.writeEmptyElement(XML_PROPERTY);
      writer.writeAttribute(XML_NAME, propertyName);
      String fqnString;
      if (property.isPrimitive()) {
        fqnString = getFullQualifiedName(property.getType(), property.isCollection());
      } else {
        fqnString = getAliasedFullQualifiedName(property.getType(), property.isCollection());
      }
      writer.writeAttribute(XML_TYPE, fqnString);

      // Facets
      if (property.isNullable() == false) {
        writer.writeAttribute(XML_NULLABLE, "" + property.isNullable());
      }

      if (property.isUnicode() == false) {
        writer.writeAttribute(XML_UNICODE, "" + property.isUnicode());
      }

      if (property.getDefaultValue() != null) {
        writer.writeAttribute(XML_DEFAULT_VALUE, property.getDefaultValue());
      }

      if (property.getMaxLength() != null) {
        writer.writeAttribute(XML_MAX_LENGTH, "" + property.getMaxLength());
      }

      if (property.getPrecision() != null) {
        writer.writeAttribute(XML_PRECISION, "" + property.getPrecision());
      }

      if (property.getScale() != null) {
        writer.writeAttribute(XML_SCALE, "" + property.getScale());
      }
    }
  }

  private void appendKey(final XMLStreamWriter writer, final EdmEntityType entityType) throws XMLStreamException {
    List<EdmKeyPropertyRef> keyPropertyRefs = entityType.getKeyPropertyRefs();
    if (keyPropertyRefs != null && !keyPropertyRefs.isEmpty()) {
      // Resolve Base Type key as it is shown in derived type
      EdmEntityType baseType = entityType.getBaseType();
      if (baseType != null && baseType.getKeyPropertyRefs() != null && !(baseType.getKeyPropertyRefs().isEmpty())) {
        return;
      }

      writer.writeStartElement(XML_KEY);
      for (EdmKeyPropertyRef keyRef : keyPropertyRefs) {
        writer.writeEmptyElement(XML_PROPERTY_REF);

        writer.writeAttribute(XML_NAME, keyRef.getName());

        if (keyRef.getAlias() != null) {
          writer.writeAttribute(XML_ALIAS, keyRef.getAlias());
        }
      }
      writer.writeEndElement();
    }
  }

  private void appendEnumTypes(final XMLStreamWriter writer, final List<EdmEnumType> enumTypes)
      throws XMLStreamException {
    for (EdmEnumType enumType : enumTypes) {
      writer.writeStartElement(XML_ENUM_TYPE);
      writer.writeAttribute(XML_NAME, enumType.getName());
      writer.writeAttribute(XML_IS_FLAGS, "" + enumType.isFlags());
      writer.writeAttribute(XML_UNDERLYING_TYPE, getFullQualifiedName(enumType.getUnderlyingType(), false));

      for (String memberName : enumType.getMemberNames()) {
        writer.writeEmptyElement(XML_MEMBER);
        writer.writeAttribute(XML_NAME, memberName);
        writer.writeAttribute(XML_VALUE, enumType.getMember(memberName).getValue());
      }

      writer.writeEndElement();
    }
  }

  private String getFullQualifiedName(final EdmType type, final boolean isCollection) {
    final String name = type.getFullQualifiedName().getFullQualifiedNameAsString();
    return isCollection ? "Collection(" + name + ")" : name;
  }

  private String getAliasedFullQualifiedName(final EdmType type, final boolean isCollection) {
    FullQualifiedName fqn = type.getFullQualifiedName();
    final String name;
    if (namespaceToAlias.get(fqn.getNamespace()) != null) {
      name = namespaceToAlias.get(fqn.getNamespace()) + "." + fqn.getName();
    } else {
      name = fqn.getFullQualifiedNameAsString();
    }

    return isCollection ? "Collection(" + name + ")" : name;
  }

  /**
   * Appends references, e.g., to the OData Core Vocabulary, as defined in the OData specification
   * and mentioned in its Common Schema Definition Language (CSDL) document.
   */
  private void appendReference(final XMLStreamWriter writer) throws XMLStreamException {
    for (final EdmxReference reference : serviceMetadata.getReferences()) {
      writer.writeStartElement(PREFIX_EDMX, REFERENCE, NS_EDMX);
      writer.writeAttribute(URI, reference.getUri().toASCIIString());

      List<EdmxReferenceInclude> includes = reference.getIncludes();
      for (EdmxReferenceInclude include : includes) {
        writer.writeStartElement(PREFIX_EDMX, INCLUDE, NS_EDMX);
        writer.writeAttribute(XML_NAMESPACE, include.getNamespace());
        if (include.getAlias() != null) {
          // Reference Aliases are ignored for now since they are not V2 compatible
          writer.writeAttribute(XML_ALIAS, include.getAlias());
        }
        writer.writeEndElement();
      }

      List<EdmxReferenceIncludeAnnotation> includeAnnotations = reference.getIncludeAnnotations();
      for (EdmxReferenceIncludeAnnotation includeAnnotation : includeAnnotations) {
        writer.writeStartElement(PREFIX_EDMX, INCLUDE_ANNOTATIONS, NS_EDMX);
        writer.writeAttribute(XML_TERM_NAMESPACE, includeAnnotation.getTermNamespace());
        if (includeAnnotation.getQualifier() != null) {
          writer.writeAttribute(XML_QUALIFIER, includeAnnotation.getQualifier());
        }
        if (includeAnnotation.getTargetNamespace() != null) {
          writer.writeAttribute(XML_TARGET_NAMESPACE, includeAnnotation.getTargetNamespace());
        }
        writer.writeEndElement();
      }

      writer.writeEndElement();
    }
  }
}
