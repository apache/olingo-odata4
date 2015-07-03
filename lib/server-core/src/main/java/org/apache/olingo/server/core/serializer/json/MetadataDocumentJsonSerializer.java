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

package org.apache.olingo.server.core.serializer.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.api.edmx.EdmxReferenceIncludeAnnotation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataDocumentJsonSerializer {

    private final ServiceMetadata serviceMetadata;
    private final Map<String, String> namespaceToAlias = new HashMap<String, String>();
    private static final String CONSTANT_SCHEMA = "$schema";
    private static final String DEFAULT_SCHEMA="http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#";
    private static final String CONSTANT_DEFINITION_REFERENCE=DEFAULT_SCHEMA+"/definitions/";
    private static final String CONSTANT_REFERENCE_IDENTIFIER="$ref";
    private static final String CONSTANT_REFERENCES = "references";
    private static final String CONSTANT_INCLUDE = "includes";
    private static final String CONSTANT_INCLUDE_ANNOTATIONS = "includeAnnotations";
    private static final String CONSTANT_ALIAS = "alias";
    private static final String CONSTANT_SCHEMAS= "schemas";
    private static final String CONSTANT_ENUM= "enum";
    private static final String CONSTANT_JSON_VALUE = "@odata.value";
    private static final String CONSTANT_DEFINITIONS= "definitions";
    private static final String CONSTANT_TARGET_NAMESPACE = "targetNamespace";
    private static final String CONSTANT_TERM_NAMESPACE = "termNamespace";
    private static final String CONSTANT_QUALIFIER = "qualifier";
    private static final String CONSTANT_SCALE = "scale";
    private static final String CONSTANT_PRECISION = "precision";
    private static final String CONSTANT_NULLABLE = "nullable";
    private static final String CONSTANT_MAX_LENGTH = "maxLength";
    private static final String CONSTANT_ACTIONS = "actions";
    private static final String CONSTANT_FUNCTIONS = "functions";
    private static final String CONSTANT_NAME = "name";
    private static final String CONSTANT_TYPE = "type";
    private static final String CONSTANT_IS_BOUND = "isBound";
    private static final String CONSTANT_IS_COMPOSABLE = "isComposable";
    private static final String CONSTANT_PARAMETERS = "parameters";
    private static final String CONSTANT_RETURN_TYPE = "returnType";
    private static final String CONSTANT_ENTITY_SET_PATH= "entitySetPath";
    private static final String CONSTANT_ENTITY_SET= "entitySet";
    private static final String CONSTANT_FUNCTION_IMPORTS = "functionImports";
    private static final String CONSTANT_ACTION_IMPORTS = "actionImports";
    private static final String CONSTANT_FUNCTION_IDENTIFIER = "function";
    private static final String CONSTANT_ACTION_IDENTIFIER = "action";
    private static final String CONSTANT_IS_INCLUDED_IN_SERVICE_DOCUMENT = "includeInServiceDocument";
    private static final String CONSTANT_NAVIGATION_PROPERTY_BINDINGS = "navigationPropertyBindings";
    private static final String CONSTANT_SINGLETONS = "singletons";
    private static final String CONSTANT_TARGET = "target";
    private static final String CONSTANT_PROPERTIES = "properties";
    private static final String CONSTANT_OBJECT = "object";
    private static final String CONSTANT_ARRAY = "array";
    private static final String CONSTANT_ITEMS = "items";
    private static final String CONSTANT_ABSTRACT = "abstract";
    private static final String CONSTANT_IS_UNICODE = "unicode";
    private static final String CONSTANT_DEFAULT_VALUE = "defaultValue";
    private static final String CONSTANT_PARTNER = "partner";
    private static final String CONSTANT_CONTAINS_TARGET = "containsTarget";
    private static final String CONSTANT_RELATIONSHIP = "relationship";
    private static final String CONSTANT_REFERENTIAL_CONSTRAINTS = "referentialConstraints";
    private static final String CONSTANT_REFERENCED_PROPERTY = "referencedProperty";
    private static final String CONSTANT_HAS_STREAM = "hasStream";
    private static final String CONSTANT_OPEN_TYPE= "openType";
    private static final String CONSTANT_KEYS = "keys";
    private static final String CONSTANT_ENTITY_SETS = "entitySets";
    private static final String CONSTANT_ENTITY_TYPE = "entityType";
    private static final String CONSTANT_ENTITY_CONTAINER = "entityContainer";
    private static final String CONSTANT_EXTEND = "extend";
    private static final String CONSTANT_BASE_TYPE_IDENTIFIER = "allOf";
    private static final String CONSTANT_PROPERTY_BASE_TYPE_IDENTIFIER = "anyOf";
    private static final String CONSTANT_STRING = "string";
    private static final String CONSTANT_NUMBER = "number";
    private static final String CONSTANT_MINIMUM = "minimum";
    private static final String CONSTANT_MAXIMUM = "maximum";
    private static final String CONSTANT_MULTIPLE_OF = "multipleOf";
    private static final String CONSTANT_PATTERN_IDENTIFIER = "pattern";
    private static final String CONSTANT_BOOLEAN = "boolean";
    private static final String CONSTANT_DATE_TIME_PATTERN_1 = "(^[^.]*$|[.][0-9]{1,precision}$)";
    private static final String CONSTANT_DATE_TIME_PATTERN_2 = "^[^.]*$";


    public MetadataDocumentJsonSerializer(final ServiceMetadata serviceMetadata) {
        this.serviceMetadata = serviceMetadata;
    }

    public void writeMetadataDocument(final JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField(CONSTANT_SCHEMA, DEFAULT_SCHEMA);
        appendDefinitions(gen);
        appendSchemas(gen);
        appendReference(gen);
        gen.writeEndObject();
    }

    private void appendSchemas(final JsonGenerator gen) throws IOException {
        if (!serviceMetadata.getEdm().getSchemas().isEmpty()) {
            gen.writeFieldName(CONSTANT_SCHEMAS);
            gen.writeStartObject();
            for (EdmSchema schema : serviceMetadata.getEdm().getSchemas()) {
                appendSchema(gen, schema);
            }
            gen.writeEndObject();
        }
    }

    private void appendDefinitions(final JsonGenerator gen) throws IOException {
        if (!serviceMetadata.getEdm().getSchemas().isEmpty()) {
            gen.writeFieldName(CONSTANT_DEFINITIONS);
            gen.writeStartObject();
            for (EdmSchema schema : serviceMetadata.getEdm().getSchemas()) {
                appendSchemaDefinitions(gen, schema);
            }
            gen.writeEndObject();
        }
    }

    private void appendSchemaDefinitions(final JsonGenerator gen, final EdmSchema schema) throws IOException {
        appendEnumTypes(gen, schema.getEnumTypes());
        appendTypeDefinitions(gen, schema.getTypeDefinitions());
        appendEntityTypes(gen, schema.getEntityTypes());
        appendComplexTypes(gen, schema.getComplexTypes());
    }

    private void appendSchema(final JsonGenerator gen, final EdmSchema schema) throws IOException {
        gen.writeFieldName(schema.getNamespace());
        gen.writeStartObject();
        if (schema.getAlias() != null) {
            gen.writeStringField(CONSTANT_ALIAS,schema.getAlias());
            namespaceToAlias.put(schema.getNamespace(), schema.getAlias());
        }else{
            gen.writeNullField(CONSTANT_ALIAS);
        }
        appendActions(gen, schema.getActions());
        appendFunctions(gen, schema.getFunctions());
        appendEntityContainer(gen, schema.getEntityContainer());
        gen.writeEndObject();
    }

    private void appendTypeDefinitions(final JsonGenerator gen, final List<EdmTypeDefinition> typeDefinitions)
            throws IOException {
        if(!typeDefinitions.isEmpty()) {
            for (EdmTypeDefinition definition : typeDefinitions) {
                gen.writeFieldName(getAliasedFullQualifiedName(definition, false));
                gen.writeStartObject();
                gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER,
                        CONSTANT_DEFINITION_REFERENCE + getFullQualifiedName(definition.getUnderlyingType(), false));

                if (definition.getMaxLength() != null) {
                    gen.writeNumberField(CONSTANT_MAX_LENGTH, definition.getMaxLength());
                }

                if (definition.getPrecision() != null) {
                    gen.writeNumberField(CONSTANT_PRECISION, definition.getPrecision());
                }

                if (definition.getScale() != null) {
                    gen.writeNumberField(CONSTANT_SCALE, definition.getScale());
                }
                gen.writeEndObject();
            }
        }
    }

    private void appendActions(final JsonGenerator gen, final List<EdmAction> actions) throws IOException {
        if(!actions.isEmpty()){
            gen.writeFieldName(CONSTANT_ACTIONS);
            gen.writeStartArray();
            for (EdmAction action : actions) {
                gen.writeStartObject();
                gen.writeStringField(CONSTANT_NAME, action.getName());
                if (action.getEntitySetPath() != null) {
                    gen.writeStringField(CONSTANT_ENTITY_SET_PATH, action.getEntitySetPath());
                }
                gen.writeBooleanField(CONSTANT_IS_BOUND, action.isBound());
                appendOperationParameters(gen, action);
                appendOperationReturnType(gen, action);
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }

    private void appendFunctions(final JsonGenerator gen, final List<EdmFunction> functions)
            throws IOException {
        if(!functions.isEmpty()) {
            gen.writeFieldName(CONSTANT_FUNCTIONS);
            gen.writeStartArray();
            for (EdmFunction function : functions) {
                gen.writeStartObject();
                gen.writeStringField(CONSTANT_NAME, function.getName());
                if (function.getEntitySetPath() != null) {
                    gen.writeStringField(CONSTANT_ENTITY_SET_PATH, function.getEntitySetPath());
                }
                if (function.isBound()) {
                    gen.writeBooleanField(CONSTANT_IS_BOUND, function.isBound());
                }

                if (function.isComposable()) {
                    gen.writeBooleanField(CONSTANT_IS_COMPOSABLE, function.isComposable());
                }
                appendOperationParameters(gen, function);
                appendOperationReturnType(gen, function);
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }

    private void appendOperationReturnType(final JsonGenerator gen, final EdmOperation operation)
            throws IOException {
        EdmReturnType returnType = operation.getReturnType();
        if (returnType != null) {
            gen.writeFieldName(CONSTANT_RETURN_TYPE);
            gen.writeStartObject();
            String returnTypeFqnString;
            if (EdmTypeKind.PRIMITIVE.equals(returnType.getType().getKind())) {
                returnTypeFqnString = getFullQualifiedName(returnType.getType(), returnType.isCollection());
            } else {
                returnTypeFqnString = getAliasedFullQualifiedName(returnType.getType(), returnType.isCollection());
            }
            gen.writeStringField(CONSTANT_TYPE, returnTypeFqnString);
            appendReturnTypeFacets(gen, returnType);
            gen.writeEndObject();
        }
    }

    private void appendOperationParameters(final JsonGenerator gen, final EdmOperation operation)
            throws IOException {
        gen.writeFieldName(CONSTANT_PARAMETERS);
        gen.writeStartObject();
        for (String parameterName : operation.getParameterNames()) {
            gen.writeFieldName(parameterName);
            gen.writeStartObject();
            EdmParameter parameter = operation.getParameter(parameterName);
            String typeFqnString;
            if (EdmTypeKind.PRIMITIVE.equals(parameter.getType().getKind())) {
                typeFqnString = getFullQualifiedName(parameter.getType(), parameter.isCollection());
            } else {
                typeFqnString = getAliasedFullQualifiedName(parameter.getType(), parameter.isCollection());
            }
            gen.writeStringField(CONSTANT_TYPE, typeFqnString);
            appendParameterFacets(gen, parameter);
            gen.writeEndObject();
        }
        gen.writeEndObject();
    }

    private void appendReturnTypeFacets(final JsonGenerator gen, final EdmReturnType returnType)
            throws IOException {
        if (returnType.isNullable() == false) {
            gen.writeBooleanField(CONSTANT_NULLABLE, returnType.isNullable());
        }
        if (returnType.getMaxLength() != null) {
            gen.writeNumberField(CONSTANT_MAX_LENGTH, returnType.getMaxLength());
        }
        if (returnType.getPrecision() != null) {
            gen.writeNumberField(CONSTANT_PRECISION, returnType.getPrecision());
        }
        if (returnType.getScale() != null) {
            gen.writeNumberField(CONSTANT_SCALE, returnType.getScale());
        }
    }

    private void appendParameterFacets(final JsonGenerator gen, final EdmParameter parameter)
            throws IOException {
        if (parameter.isNullable() == false) {
            gen.writeBooleanField(CONSTANT_NULLABLE,parameter.isNullable());
        }
        if (parameter.getMaxLength() != null) {
            gen.writeNumberField(CONSTANT_MAX_LENGTH, parameter.getMaxLength());
        }
        if (parameter.getPrecision() != null) {
            gen.writeNumberField(CONSTANT_PRECISION, parameter.getPrecision());
        }
        if (parameter.getScale() != null) {
            gen.writeNumberField(CONSTANT_SCALE, parameter.getScale());
        }
    }

    private void appendEntityContainer(final JsonGenerator gen, final EdmEntityContainer container)
            throws IOException {
        if (container != null) {
            gen.writeFieldName(CONSTANT_ENTITY_CONTAINER);
            gen.writeStartObject();
            gen.writeStringField(CONSTANT_NAME,container.getName());
            FullQualifiedName parentContainerName = container.getParentContainerName();
            if (parentContainerName != null) {
                String parentContainerNameString;
                if (namespaceToAlias.get(parentContainerName.getNamespace()) != null) {
                    parentContainerNameString =
                         namespaceToAlias.get(parentContainerName.getNamespace()) + "." + parentContainerName.getName();
                } else {
                    parentContainerNameString = parentContainerName.getFullQualifiedNameAsString();
                }
                gen.writeStringField(CONSTANT_EXTEND,parentContainerNameString);
            }
            appendEntitySets(gen, container.getEntitySets());
            appendActionImports(gen, container.getActionImports());
            String containerNamespace;
            if (namespaceToAlias.get(container.getNamespace()) != null) {
                containerNamespace = namespaceToAlias.get(container.getNamespace());
            } else {
                containerNamespace = container.getNamespace();
            }
            appendFunctionImports(gen, container.getFunctionImports(), containerNamespace);
            appendSingletons(gen, container.getSingletons());
            gen.writeEndObject();
        }
    }

    private void appendFunctionImports(final JsonGenerator gen, final List<EdmFunctionImport> functionImports,
                                       final String containerNamespace) throws IOException {
        if(!functionImports.isEmpty()) {
            gen.writeFieldName(CONSTANT_FUNCTION_IMPORTS);
            gen.writeStartObject();
            for (EdmFunctionImport functionImport : functionImports) {
                gen.writeFieldName(functionImport.getName());
                gen.writeStartObject();
                String functionFQNString;
                FullQualifiedName functionFqn = functionImport.getFunctionFqn();
                if (namespaceToAlias.get(functionFqn.getNamespace()) != null) {
                    functionFQNString = namespaceToAlias.get(functionFqn.getNamespace()) + "." + functionFqn.getName();
                } else {
                    functionFQNString = functionFqn.getFullQualifiedNameAsString();
                }
                gen.writeStringField(CONSTANT_FUNCTION_IDENTIFIER, functionFQNString);
                EdmEntitySet returnedEntitySet = functionImport.getReturnedEntitySet();
                if (returnedEntitySet != null) {
                    gen.writeStringField(CONSTANT_ENTITY_SET, containerNamespace + "." + returnedEntitySet.getName());
                }
                if (functionImport.isIncludeInServiceDocument()) {
                    gen.writeBooleanField(CONSTANT_IS_INCLUDED_IN_SERVICE_DOCUMENT,
                            functionImport.isIncludeInServiceDocument());
                }
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }

    private void appendActionImports(final JsonGenerator gen, final List<EdmActionImport> actionImports)
            throws IOException {
        if(!actionImports.isEmpty()) {
            gen.writeFieldName(CONSTANT_ACTION_IMPORTS);
            gen.writeStartObject();
            for (EdmActionImport actionImport : actionImports) {
                gen.writeFieldName(actionImport.getName());
                gen.writeStartObject();
                gen.writeStringField(CONSTANT_ACTION_IDENTIFIER,
                        getAliasedFullQualifiedName(actionImport.getUnboundAction(), false));
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }

    private void appendSingletons(final JsonGenerator gen, final List<EdmSingleton> singletons)
            throws IOException {
        if(!singletons.isEmpty()) {
            gen.writeFieldName(CONSTANT_SINGLETONS);
            gen.writeStartObject();
            for (EdmSingleton singleton : singletons) {
                gen.writeFieldName(singleton.getName());
                gen.writeStartObject();
                gen.writeStringField(CONSTANT_TYPE, getAliasedFullQualifiedName(singleton.getEntityType(), false));
                appendNavigationPropertyBindings(gen, singleton);
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }

    private void appendNavigationPropertyBindings(final JsonGenerator gen, final EdmBindingTarget bindingTarget)
            throws IOException {
        if (!bindingTarget.getNavigationPropertyBindings().isEmpty()) {
            gen.writeFieldName(CONSTANT_NAVIGATION_PROPERTY_BINDINGS);
            gen.writeStartObject();
            for (EdmNavigationPropertyBinding binding : bindingTarget.getNavigationPropertyBindings()) {
                gen.writeFieldName(binding.getPath());
                gen.writeStartObject();
                gen.writeStringField(CONSTANT_TARGET, binding.getTarget());
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }

    private void appendEntitySets(final JsonGenerator gen, final List<EdmEntitySet> entitySets)
            throws IOException {
        gen.writeFieldName(CONSTANT_ENTITY_SETS);
        gen.writeStartObject();
        for (EdmEntitySet entitySet : entitySets) {
            gen.writeFieldName(entitySet.getName());
            gen.writeStartObject();
            gen.writeStringField(CONSTANT_ENTITY_TYPE, getAliasedFullQualifiedName(entitySet.getEntityType(), false));
            if (!entitySet.isIncludeInServiceDocument()) {
                gen.writeBooleanField(CONSTANT_IS_INCLUDED_IN_SERVICE_DOCUMENT, entitySet.isIncludeInServiceDocument());
            }
            appendNavigationPropertyBindings(gen, entitySet);
            gen.writeEndObject();
        }
        gen.writeEndObject();
    }


    private void appendComplexTypes(final JsonGenerator gen, final List<EdmComplexType> complexTypes)
            throws IOException {
        for (EdmComplexType complexType : complexTypes) {
            gen.writeFieldName(getFullQualifiedName(complexType, false));
            gen.writeStartObject();
            gen.writeStringField(CONSTANT_TYPE, CONSTANT_OBJECT);

            if (complexType.getBaseType() != null) {
                gen.writeFieldName(CONSTANT_BASE_TYPE_IDENTIFIER);
                gen.writeStartArray();
                gen.writeStartObject();
                gen.writeStringField( CONSTANT_REFERENCE_IDENTIFIER , CONSTANT_DEFINITION_REFERENCE +
                                getAliasedFullQualifiedName(complexType.getBaseType(), false));
                gen.writeEndObject();
                gen.writeEndArray();
            }
            if (complexType.isAbstract()) {
                gen.writeBooleanField(CONSTANT_ABSTRACT, complexType.isAbstract());
            }
            appendCombinedProperties(gen, complexType);
            gen.writeEndObject();
        }
    }

    private void appendEntityTypes(final JsonGenerator gen, final List<EdmEntityType> entityTypes)
            throws IOException {
        for (EdmEntityType entityType : entityTypes) {
            gen.writeFieldName(getFullQualifiedName(entityType, false));
            gen.writeStartObject();
            gen.writeStringField(CONSTANT_TYPE, CONSTANT_OBJECT);
            if (entityType.hasStream()) {
                gen.writeBooleanField(CONSTANT_HAS_STREAM, entityType.hasStream());
            }
            if (entityType.getBaseType() != null) {
                gen.writeFieldName(CONSTANT_BASE_TYPE_IDENTIFIER);
                gen.writeStartArray();
                gen.writeStartObject();
                gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER , CONSTANT_DEFINITION_REFERENCE +
                        getAliasedFullQualifiedName(entityType.getBaseType(), false));
                gen.writeEndObject();
                gen.writeEndArray();
            }
            if (entityType.isAbstract()) {
                gen.writeBooleanField(CONSTANT_ABSTRACT, entityType.isAbstract());
            }
            if(entityType.isOpenType()){
                gen.writeBooleanField(CONSTANT_OPEN_TYPE,entityType.isOpenType());
            }
            appendKey(gen, entityType);
            appendCombinedProperties(gen, entityType);
            gen.writeEndObject();
        }
    }

    private void appendCombinedProperties(final JsonGenerator gen, final EdmStructuredType type)
            throws IOException{
        gen.writeFieldName(CONSTANT_PROPERTIES);
        gen.writeStartObject();
        appendProperties(gen, type);
        appendNavigationProperties(gen, type);
        gen.writeEndObject();
    }

    private void appendNavigationProperties(final JsonGenerator gen, final EdmStructuredType type)
            throws IOException {
        List<String> navigationPropertyNames = new ArrayList<String>(type.getNavigationPropertyNames());
        if (type.getBaseType() != null) {
            navigationPropertyNames.removeAll(type.getBaseType().getNavigationPropertyNames());
        }
        if(!navigationPropertyNames.isEmpty()) {
            for (String navigationPropertyName : navigationPropertyNames) {
                gen.writeFieldName(navigationPropertyName);
                gen.writeStartObject();
                EdmNavigationProperty navigationProperty = type.getNavigationProperty(navigationPropertyName);
                if(!navigationProperty.isNullable()) {
                    if (!navigationProperty.isCollection()) {
                        gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                getAliasedFullQualifiedName(navigationProperty.getType(), false));
                    } else {
                        gen.writeStringField(CONSTANT_TYPE, CONSTANT_ARRAY);
                        gen.writeFieldName(CONSTANT_ITEMS);
                        gen.writeStartObject();
                        gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                getAliasedFullQualifiedName(navigationProperty.getType(), false));
                        gen.writeEndObject();
                    }
                }else{
                    if (!navigationProperty.isCollection()) {
                        gen.writeFieldName(CONSTANT_PROPERTY_BASE_TYPE_IDENTIFIER);
                        gen.writeStartArray();
                        gen.writeStartObject();
                        gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                getAliasedFullQualifiedName(navigationProperty.getType(), false));
                        gen.writeEndObject();
                        gen.writeStartObject();
                        gen.writeStringField(CONSTANT_TYPE, null);
                        gen.writeEndObject();
                        gen.writeEndArray();
                    }else{
                        gen.writeStringField(CONSTANT_TYPE, CONSTANT_ARRAY);
                        gen.writeFieldName(CONSTANT_ITEMS);
                        gen.writeStartObject();
                        gen.writeFieldName(CONSTANT_PROPERTY_BASE_TYPE_IDENTIFIER);
                        gen.writeStartArray();
                        gen.writeStartObject();
                        gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                getAliasedFullQualifiedName(navigationProperty.getType(), false));
                        gen.writeEndObject();
                        gen.writeStartObject();
                        gen.writeStringField(CONSTANT_TYPE, null);
                        gen.writeEndObject();
                        gen.writeEndArray();
                        gen.writeEndObject();
                    }
                }
                gen.writeFieldName(CONSTANT_RELATIONSHIP);
                gen.writeStartObject();
                if (navigationProperty.getPartner() != null) {
                    EdmNavigationProperty partner = navigationProperty.getPartner();
                    gen.writeStringField(CONSTANT_PARTNER, partner.getName());
                }
                if (navigationProperty.containsTarget()) {
                    gen.writeBooleanField(CONSTANT_CONTAINS_TARGET, navigationProperty.containsTarget());
                }

                if (navigationProperty.getReferentialConstraints() != null) {
                    gen.writeFieldName(CONSTANT_REFERENTIAL_CONSTRAINTS);
                    gen.writeStartObject();
                    for (EdmReferentialConstraint constraint : navigationProperty.getReferentialConstraints()) {
                        gen.writeFieldName(constraint.getPropertyName());
                        gen.writeStartObject();
                        gen.writeStringField(CONSTANT_REFERENCED_PROPERTY, constraint.getReferencedPropertyName());
                        gen.writeEndObject();
                    }
                    gen.writeEndObject();
                }
                gen.writeEndObject();
                gen.writeEndObject();
            }
        }
    }

    private void appendProperties(final JsonGenerator gen, final EdmStructuredType type) throws IOException {
        List<String> propertyNames = new ArrayList<String>(type.getPropertyNames());
        if (type.getBaseType() != null) {
            propertyNames.removeAll(type.getBaseType().getPropertyNames());
        }
        if(!propertyNames.isEmpty()) {
            for (String propertyName : propertyNames) {
                EdmProperty property = type.getStructuralProperty(propertyName);
                gen.writeFieldName(propertyName);
                gen.writeStartObject();
                if(!property.isNullable()) {
                    if (!property.isCollection()) {
                        if(property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.String).getName())){
                            gen.writeStringField(CONSTANT_TYPE,CONSTANT_STRING);
                        }else if(property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.Decimal).getName())){
                            gen.writeStringField(CONSTANT_TYPE,CONSTANT_NUMBER);
                        }else if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.Boolean).getName())){
                            gen.writeStringField(CONSTANT_TYPE,CONSTANT_BOOLEAN);
                        }else if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.DateTimeOffset).getName())||
                        property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.Duration).getName())||
                                property.getType().getName().equals(EdmPrimitiveTypeFactory
                                        .getInstance(EdmPrimitiveTypeKind.TimeOfDay).getName())){
                            gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                    getAliasedFullQualifiedName(property.getType(), false));
                        }else {
                            gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                    getAliasedFullQualifiedName(property.getType(), false));
                        }
                        appendPropertyFacets(gen,property);
                    } else {
                        gen.writeStringField(CONSTANT_TYPE, CONSTANT_ARRAY);
                        gen.writeFieldName(CONSTANT_ITEMS);
                        gen.writeStartObject();
                        if(property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.String).getName())){
                            gen.writeStringField(CONSTANT_TYPE,CONSTANT_STRING);
                        }else if(property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.Decimal).getName())){
                            gen.writeStringField(CONSTANT_TYPE,CONSTANT_NUMBER);
                        }else if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.Boolean).getName())){
                            gen.writeStringField(CONSTANT_TYPE,CONSTANT_BOOLEAN);
                        }else if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.DateTimeOffset).getName())||
                                property.getType().getName().equals(EdmPrimitiveTypeFactory
                                        .getInstance(EdmPrimitiveTypeKind.Duration).getName())||
                                property.getType().getName().equals(EdmPrimitiveTypeFactory
                                        .getInstance(EdmPrimitiveTypeKind.TimeOfDay).getName())){
                            gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                    getAliasedFullQualifiedName(property.getType(), false));
                        }else {
                            gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                    getAliasedFullQualifiedName(property.getType(), false));
                        }
                        appendPropertyFacets(gen,property);
                        gen.writeEndObject();
                    }
                }else{
                    if(!property.isCollection()){
                        if(property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.String).getName())) {
                            gen.writeFieldName(CONSTANT_TYPE);
                            gen.writeStartArray();
                            gen.writeString(CONSTANT_STRING);
                            gen.writeNull();
                            gen.writeEndArray();
                            appendPropertyFacets(gen, property);
                        }else if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.Boolean).getName())){
                            gen.writeFieldName(CONSTANT_TYPE);
                            gen.writeStartArray();
                            gen.writeString(CONSTANT_BOOLEAN);
                            gen.writeNull();
                            gen.writeEndArray();
                            appendPropertyFacets(gen,property);
                        }else {
                            gen.writeFieldName(CONSTANT_PROPERTY_BASE_TYPE_IDENTIFIER);
                            gen.writeStartArray();
                            if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                    .getInstance(EdmPrimitiveTypeKind.Decimal).getName())) {
                                gen.writeStartObject();
                                gen.writeStringField(CONSTANT_TYPE, CONSTANT_NUMBER);
                                appendPropertyFacets(gen, property);
                                gen.writeEndObject();
                                gen.writeStartObject();
                                gen.writeStringField(CONSTANT_TYPE,null);
                                gen.writeEndObject();
                            } else {
                                gen.writeStartObject();
                                gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                        getAliasedFullQualifiedName(property.getType(), false));
                                gen.writeEndObject();
                                gen.writeStartObject();
                                if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                        .getInstance(EdmPrimitiveTypeKind.DateTimeOffset).getName()) ||
                                        property.getType().getName().equals(EdmPrimitiveTypeFactory
                                                .getInstance(EdmPrimitiveTypeKind.Duration).getName()) ||
                                        property.getType().getName().equals(EdmPrimitiveTypeFactory
                                                .getInstance(EdmPrimitiveTypeKind.TimeOfDay).getName())) {
                                    if (property.getPrecision()==null||property.getPrecision() == 0) {
                                        gen.writeStringField(CONSTANT_PATTERN_IDENTIFIER,
                                                    CONSTANT_DATE_TIME_PATTERN_2);
                                    } else {
                                            gen.writeStringField(CONSTANT_PATTERN_IDENTIFIER,
                                                    CONSTANT_DATE_TIME_PATTERN_1.replace("precision",
                                                            String.valueOf(property.getPrecision())));
                                    }
                                } else {
                                    gen.writeStringField(CONSTANT_TYPE, null);
                                }
                                appendPropertyFacets(gen, property);
                                gen.writeEndObject();
                            }
                            gen.writeEndArray();
                        }

                    }else{
                        gen.writeStringField(CONSTANT_TYPE, CONSTANT_ARRAY);
                        gen.writeFieldName(CONSTANT_ITEMS);
                        gen.writeStartObject();
                        if(property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.String).getName())) {
                            gen.writeFieldName(CONSTANT_TYPE);
                            gen.writeStartArray();
                            gen.writeString(CONSTANT_STRING);
                            gen.writeNull();
                            gen.writeEndArray();
                            appendPropertyFacets(gen, property);
                        }else if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.Boolean).getName())){
                            gen.writeFieldName(CONSTANT_TYPE);
                            gen.writeStartArray();
                            gen.writeString(CONSTANT_BOOLEAN);
                            gen.writeNull();
                            gen.writeEndArray();
                            appendPropertyFacets(gen,property);
                        }else {
                            gen.writeFieldName(CONSTANT_PROPERTY_BASE_TYPE_IDENTIFIER);
                            gen.writeStartArray();
                            if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                    .getInstance(EdmPrimitiveTypeKind.Decimal).getName())) {
                                gen.writeStartObject();
                                gen.writeStringField(CONSTANT_TYPE, CONSTANT_NUMBER);
                                appendPropertyFacets(gen, property);
                                gen.writeEndObject();
                                gen.writeStartObject();
                                gen.writeStringField(CONSTANT_TYPE,null);
                                gen.writeEndObject();
                            } else {
                                gen.writeStartObject();
                                gen.writeStringField(CONSTANT_REFERENCE_IDENTIFIER, CONSTANT_DEFINITION_REFERENCE +
                                        getAliasedFullQualifiedName(property.getType(), false));
                                gen.writeEndObject();
                                gen.writeStartObject();
                                if (property.getType().getName().equals(EdmPrimitiveTypeFactory
                                        .getInstance(EdmPrimitiveTypeKind.DateTimeOffset).getName()) ||
                                        property.getType().getName().equals(EdmPrimitiveTypeFactory
                                                .getInstance(EdmPrimitiveTypeKind.Duration).getName()) ||
                                        property.getType().getName().equals(EdmPrimitiveTypeFactory
                                                .getInstance(EdmPrimitiveTypeKind.TimeOfDay).getName())) {
                                    if(property.getPrecision()==null||property.getPrecision() == 0){
                                        gen.writeStringField(CONSTANT_PATTERN_IDENTIFIER, CONSTANT_DATE_TIME_PATTERN_2);
                                    }else{
                                        gen.writeStringField(CONSTANT_PATTERN_IDENTIFIER,
                                                CONSTANT_DATE_TIME_PATTERN_1.replace("precision",
                                                        String.valueOf(property.getPrecision())));
                                    }
                                } else {
                                    gen.writeStringField(CONSTANT_TYPE, null);
                                }
                                appendPropertyFacets(gen, property);
                                gen.writeEndObject();
                            }
                            gen.writeEndArray();
                        }
                        gen.writeEndObject();
                    }
                }
                gen.writeEndObject();
            }
        }
    }

    private void appendPropertyFacets(final JsonGenerator gen,EdmProperty property) throws IOException{
        if (property.isUnicode() == false) {
            gen.writeBooleanField(CONSTANT_IS_UNICODE, property.isUnicode());
        }

        if (property.getDefaultValue() != null) {
            gen.writeStringField(CONSTANT_DEFAULT_VALUE, property.getDefaultValue());
        }

        if (property.getMaxLength() != null) {
            gen.writeNumberField(CONSTANT_MAX_LENGTH, property.getMaxLength());
        }

        if (property.getScale() != null) {
            gen.writeNumberField(CONSTANT_MULTIPLE_OF, getScale(property.getScale()));

        }
        if (property.getPrecision() != null) {
            int scale;
            if (property.getScale()!= null) {
                scale=property.getScale();
            }else{
                scale=0;
            }
            int presicion = property.getPrecision();
            if(presicion>scale) {
                Double min = getMinimum(presicion, scale);
                Double max = getMaximum(presicion, scale);
                gen.writeNumberField(CONSTANT_MINIMUM, min);
                gen.writeNumberField(CONSTANT_MAXIMUM, max);
            }
        }
    }


    private void appendKey(final JsonGenerator gen, final EdmEntityType entityType) throws IOException {
        List<EdmKeyPropertyRef> keyPropertyRefs = entityType.getKeyPropertyRefs();
        if (keyPropertyRefs != null && !keyPropertyRefs.isEmpty()) {
            EdmEntityType baseType = entityType.getBaseType();
            if (baseType != null && baseType.getKeyPropertyRefs() != null
                    && !(baseType.getKeyPropertyRefs().isEmpty())) {
                return;
            }
            gen.writeFieldName(CONSTANT_KEYS);
            gen.writeStartArray();
            for (EdmKeyPropertyRef keyRef : keyPropertyRefs) {
                gen.writeStartObject();
                gen.writeStringField(CONSTANT_NAME,keyRef.getName());
                if (keyRef.getAlias() != null) {
                    gen.writeStringField(CONSTANT_ALIAS, keyRef.getAlias());
                }
                gen.writeEndObject();
            }

            gen.writeEndArray();
        }
    }

    private void appendEnumTypes(final JsonGenerator gen, final List<EdmEnumType> enumTypes)
            throws IOException {
        for (EdmEnumType enumType : enumTypes) {
            gen.writeFieldName(getAliasedFullQualifiedName(enumType, false));
            gen.writeStartObject();
            gen.writeFieldName(CONSTANT_ENUM);
            gen.writeStartArray();
            for (String memberName : enumType.getMemberNames()){
                gen.writeString(memberName);
            }
            gen.writeEndArray();
            for (String memberName : enumType.getMemberNames()) {
                gen.writeStringField(memberName+CONSTANT_JSON_VALUE,enumType.getMember(memberName).getValue());
            }
            gen.writeEndObject();
        }
    }

    private void appendReference(final JsonGenerator gen) throws IOException {
        if (!serviceMetadata.getReferences().isEmpty()) {
            gen.writeFieldName(CONSTANT_REFERENCES);
            gen.writeStartObject();
            for (final EdmxReference reference : serviceMetadata.getReferences()) {
                gen.writeFieldName(reference.getUri().toASCIIString());
                gen.writeStartObject();
                List<EdmxReferenceInclude> includes = reference.getIncludes();
                if(!includes.isEmpty()) {
                    gen.writeFieldName(CONSTANT_INCLUDE);
                    gen.writeStartObject();
                    for (EdmxReferenceInclude include : includes) {
                        gen.writeFieldName(include.getNamespace());
                        gen.writeStartObject();
                        if (include.getAlias() != null) {
                            gen.writeStringField(CONSTANT_ALIAS, include.getAlias());
                        }
                        gen.writeEndObject();
                    }
                    gen.writeEndObject();
                }
                List<EdmxReferenceIncludeAnnotation> includeAnnotations = reference.getIncludeAnnotations();
                if (!includeAnnotations.isEmpty()) {
                    gen.writeFieldName(CONSTANT_INCLUDE_ANNOTATIONS);
                    gen.writeStartArray();
                    for (EdmxReferenceIncludeAnnotation includeAnnotation : includeAnnotations) {
                        gen.writeStartObject();
                        gen.writeStringField(CONSTANT_TERM_NAMESPACE, includeAnnotation.getTermNamespace());
                        if (includeAnnotation.getQualifier() != null) {
                            gen.writeStringField(CONSTANT_QUALIFIER, includeAnnotation.getQualifier());
                        }
                        if (includeAnnotation.getTargetNamespace() != null) {
                            gen.writeStringField(CONSTANT_TARGET_NAMESPACE, includeAnnotation.getTargetNamespace());
                        }
                        gen.writeEndObject();
                    }
                    gen.writeEndArray();
                }
                gen.writeEndObject();
            }
            gen.writeEndObject();
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

    private Double getScale(int scale){
        String temp;
        if(scale!=0){
            temp="1e-"+scale;
        }else{
            temp="1";
        }
        return Double.valueOf(temp);
    }

    private Double getMinimum(int precision , int scale){
       return (-1)*getMaximum(precision,scale);
    }

    private Double getMaximum(int precision , int scale){
        String temp="";
        for(int counter=0;counter<(precision-scale);counter++){
            temp=temp.concat("9");
        }
        if(scale==0){
            return Double.valueOf(temp);
        }else {
            temp = temp.concat(".");
            for (int counter = 0; counter < scale; counter++) {
                temp = temp.concat("9");
            }
            return Double.valueOf(temp);
        }
    }
}
