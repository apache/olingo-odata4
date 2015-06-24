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
package org.apache.olingo.ext.config.spring;

import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.ext.config.edm.GenericEdmProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public abstract class OlingoEdmBeanDefinitionHelper {
  private static final Class<?> EDM_PROVIDER_CLASS = GenericEdmProvider.class;
  private static final Class<?> SCHEMA_CLASS = CsdlSchema.class;
  private static final Class<?> ENTITY_CONTAINER_CLASS = CsdlEntityContainer.class;
  private static final Class<?> ENTITY_SET_CLASS = CsdlEntitySet.class;
  private static final Class<?> ENTITY_TYPE_CLASS = CsdlEntityType.class;
  private static final Class<?> PROPERTY_REF_CLASS = CsdlPropertyRef.class;
  private static final Class<?> PROPERTY_CLASS = CsdlProperty.class;
  private static final Class<?> FULL_QUALIFIED_NAME_CLASS = FullQualifiedName.class;

  private static final String SCHEMA_ELEMENT = "schema";
  private static final String ENTITY_CONTAINER_ELEMENT = "entityContainer";
  private static final String ENTITY_TYPE_ELEMENT = "entityType";
  private static final String COMPLEX_TYPE_ELEMENT = "complexType";
  private static final String ENTITY_SET_ELEMENT = "entitySet";
  private static final String KEY_ELEMENT = "key";
  private static final String PROPERTY_ELEMENT = "property";

  private static final String NAME_ATTR = "name";
  private static final String NAMESPACE_ATTR = "namespace";
  private static final String TYPE_ATTR = "type";
  private static final String PROPERTY_NAME_ATTR = "property-name";
  private static final String ALIAS_ATTR = "alias";

  private static final String SCHEMA_LIST_PROPERTY = "schemas";
  private static final String ENTITY_TYPE_LIST_PROPERTY = "entityTypes";
  private static final String COMPLEX_TYPE_LIST_PROPERTY = "complexTypes";
  private static final String ENTITY_SET_LIST_PROPERTY = "entitySets";
  private static final String ENTITY_CONTAINER_PROPERTY = "entityContainer";
  private static final String NAME_PROPERTY = "name";
  private static final String NAMESPACE_PROPERTY = "namespace";
  private static final String ALIAS_PROPERTY = "alias";
  private static final String TYPE_PROPERTY = "type";
  private static final String KEY_LIST_PROPERTY = "key";
  private static final String PROPERTY_LIST_PROPERTY = "properties";
  private static final String PROPERTY_NAME_PROPERTY = "name";

  private static String elementAttribute(Element element, String name) {
    String value = element.getAttribute(name);
    return value.length() == 0 ? null : value;
  }

  private static BeanDefinitionBuilder createBeanDefinitionBuilder(
      Class<?> beanClass) {
    return BeanDefinitionBuilder.rootBeanDefinition(beanClass);
  }

  public static BeanDefinition parseEdmProvider(Element element,
      ParserContext parserContext) {
    BeanDefinitionBuilder configuration = createBeanDefinitionBuilder(EDM_PROVIDER_CLASS);

    // Schemas
    List<Element> schemaElements = DomUtils.getChildElementsByTagName(
        element, SCHEMA_ELEMENT);
    if (schemaElements.size() > 0) {
      ManagedList<BeanDefinition> schemaList = new ManagedList<BeanDefinition>(
          schemaElements.size());
      for (Element schemaElement : schemaElements) {
        BeanDefinition schema = parseSchema(schemaElement,
            parserContext);
        schemaList.add(schema);
      }
      configuration.addPropertyValue(SCHEMA_LIST_PROPERTY, schemaList);
    }

    return configuration.getBeanDefinition();
  }

  private static BeanDefinition parseSchema(Element element,
      ParserContext parserContext) {
    BeanDefinitionBuilder schema = createBeanDefinitionBuilder(SCHEMA_CLASS);

    String namespace = elementAttribute(element, NAMESPACE_ATTR);
    schema.addPropertyValue(NAMESPACE_PROPERTY, namespace);
    String alias = elementAttribute(element, ALIAS_ATTR);
    if (alias != null && !alias.isEmpty()) {
      schema.addPropertyValue(ALIAS_PROPERTY, alias);
    }

    // Entity container
    List<Element> entityContainerElements = DomUtils
        .getChildElementsByTagName(element, ENTITY_CONTAINER_ELEMENT);
    if (entityContainerElements.size() == 1) {
      Element entityContainerElement = entityContainerElements.get(0);
      BeanDefinition entityContainer = parseEntityContainer(
          entityContainerElement, namespace, parserContext);
      schema.addPropertyValue(ENTITY_CONTAINER_PROPERTY, entityContainer);
    }

    // Entity types
    List<Element> entityTypeElements = DomUtils.getChildElementsByTagName(
        element, ENTITY_TYPE_ELEMENT);
    if (entityTypeElements.size() > 0) {
      List<BeanDefinition> entityTypeList = new ManagedList<BeanDefinition>(
          entityTypeElements.size());
      for (Element entityTypeElement : entityTypeElements) {
        BeanDefinition entityType = parseEntityType(entityTypeElement,
            parserContext);
        entityTypeList.add(entityType);
      }
      schema.addPropertyValue(ENTITY_TYPE_LIST_PROPERTY, entityTypeList);
    }

    // Complex types
    List<Element> complexTypeElements = DomUtils.getChildElementsByTagName(
        element, COMPLEX_TYPE_ELEMENT);
    if (complexTypeElements.size() > 0) {
      ManagedList<BeanDefinition> complexTypeList = new ManagedList<BeanDefinition>(
          complexTypeElements.size());
      for (Element complexTypeElement : complexTypeElements) {
        BeanDefinition complexType = parseComplexType(
            complexTypeElement, parserContext);
        complexTypeList.add(complexType);
      }
      schema.addPropertyValue(COMPLEX_TYPE_LIST_PROPERTY, complexTypeList);
    }

    return schema.getBeanDefinition();
  }

  private static BeanDefinition parseEntityContainer(Element element,
      String namespace, ParserContext parserContext) {
    BeanDefinitionBuilder entityContainer = createBeanDefinitionBuilder(ENTITY_CONTAINER_CLASS);

    // Name
    entityContainer.addPropertyValue(NAME_PROPERTY, namespace
        + "EntityContainer");

    // Entity sets
    List<Element> entitySetElements = DomUtils.getChildElementsByTagName(
        element, ENTITY_SET_ELEMENT);
    if (entitySetElements.size() > 0) {
      ManagedList<BeanDefinition> entitySetList = new ManagedList<BeanDefinition>(
          entitySetElements.size());
      for (Element entitySetElement : entitySetElements) {
        BeanDefinition entitySet = parseEntitySet(entitySetElement,
            namespace, parserContext);
        entitySetList.add(entitySet);
      }
      entityContainer.addPropertyValue(ENTITY_SET_LIST_PROPERTY,
          entitySetList);
    }

    return entityContainer.getBeanDefinition();
  }

  private static BeanDefinition parseEntitySet(Element element,
      String namespace, ParserContext parserContext) {
    BeanDefinitionBuilder entitySet = createBeanDefinitionBuilder(ENTITY_SET_CLASS);

    String name = elementAttribute(element, NAME_ATTR);
    String type = elementAttribute(element, TYPE_ATTR);
    entitySet.addPropertyValue(NAME_PROPERTY, name);

    BeanDefinitionBuilder fqn = createBeanDefinitionBuilder(FULL_QUALIFIED_NAME_CLASS);
    fqn.addConstructorArgValue(namespace);
    fqn.addConstructorArgValue(type);

    entitySet.addPropertyValue(TYPE_PROPERTY, fqn.getBeanDefinition());

    return entitySet.getBeanDefinition();
  }

  private static BeanDefinition parseEntityType(Element element,
      ParserContext parserContext) {
    BeanDefinitionBuilder entityType = createBeanDefinitionBuilder(ENTITY_TYPE_CLASS);

    String propertyName = elementAttribute(element, NAME_ATTR);
    entityType.addPropertyValue(NAME_PROPERTY, propertyName);

    // Key
    List<Element> keyElements = DomUtils.getChildElementsByTagName(element,
        KEY_ELEMENT);
    if (keyElements.size() > 0) {
      ManagedList<BeanDefinition> keyList = new ManagedList<BeanDefinition>(
          keyElements.size());
      for (Element keyElement : keyElements) {
        BeanDefinition key = parseKey(keyElement, parserContext);
        keyList.add(key);
      }
      entityType.addPropertyValue(KEY_LIST_PROPERTY, keyList);
    }

    // Properties
    List<Element> propertyElements = DomUtils.getChildElementsByTagName(
        element, PROPERTY_ELEMENT);
    if (propertyElements.size() > 0) {
      ManagedList<BeanDefinition> entitySetList = new ManagedList<BeanDefinition>(
          propertyElements.size());
      for (Element propertyElement : propertyElements) {
        BeanDefinition property = parseProperty(propertyElement,
            parserContext);
        entitySetList.add(property);
      }
      entityType.addPropertyValue(PROPERTY_LIST_PROPERTY, entitySetList);
    }

    return entityType.getBeanDefinition();
  }

  private static BeanDefinition parseKey(Element element,
      ParserContext parserContext) {
    BeanDefinitionBuilder property = createBeanDefinitionBuilder(PROPERTY_REF_CLASS);

    String propertyName = elementAttribute(element, PROPERTY_NAME_ATTR);
    property.addPropertyValue(PROPERTY_NAME_PROPERTY, propertyName);

    return property.getBeanDefinition();
  }

  private static BeanDefinition parseProperty(Element element,
      ParserContext parserContext) {
    BeanDefinitionBuilder property = createBeanDefinitionBuilder(PROPERTY_CLASS);

    String name = elementAttribute(element, NAME_ATTR);
    String type = elementAttribute(element, TYPE_ATTR);
    property.addPropertyValue(NAME_PROPERTY, name);
    property.addPropertyValue(TYPE_PROPERTY, type);

    return property.getBeanDefinition();
  }

  private static BeanDefinition parseComplexType(Element complexTypeElement,
      ParserContext parserContext) {
    // TODO to be implemented
    return null;
  }

}
