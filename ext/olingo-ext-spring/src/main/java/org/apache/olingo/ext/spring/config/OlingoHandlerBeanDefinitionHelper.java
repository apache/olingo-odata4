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
package org.apache.olingo.ext.spring.config;

import java.util.List;

import org.apache.olingo.ext.spring.factory.EdmxReferenceFactoryBean;
import org.apache.olingo.ext.spring.factory.ODataFactoryBean;
import org.apache.olingo.ext.spring.factory.ODataHttpHandlerFactoryBean;
import org.apache.olingo.ext.spring.factory.ServiceMetadataFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public abstract class OlingoHandlerBeanDefinitionHelper {

	private static final Class<?> ODATA_FACTORY_BEAN_CLASS = ODataFactoryBean.class;
	private static final Class<?> HTTP_HANDLER_FACTORY_BEAN_CLASS = ODataHttpHandlerFactoryBean.class;
	private static final Class<?> SERVICE_METADATA_FACTORY_BEAN_CLASS = ServiceMetadataFactoryBean.class;
	private static final Class<?> EDMX_REFERENCE_FACTORY_BEAN = EdmxReferenceFactoryBean.class;

	private static final String REFERENCE_ELEMENT = "reference";
	private static final String PROCESSOR_ELEMENT = "processor";
	private static final String INCLUDE_ELEMENT = "include";

	private static final String EDM_PROVIDER_ATTR = "edm-provider";
	private static final String REF_ATTR = "ref";
	private static final String URI_ATTR = "uri";
	private static final String KEY_ATTR = "key";
	private static final String VALUE_ATTR = "value";

	private static final String ODATA_PROPERTY = "odata";
	private static final String SERVICE_METADATA_PROPERTY = "serviceMetadata";
	private static final String REFERENCES_LIST_PROPERTY = "references";
	private static final String PROCESSORS_LIST_PROPERTY = "processors";
	private static final String EDM_PROVIDER_PROPERTY = "edmProvider";
	private static final String URI_PROPERTY = "uri";
	private static final String INCLUDES_PROPERTY = "includes";

	private static String elementAttribute(Element element, String name) {
		String value = element.getAttribute(name);
		return value.length() == 0 ? null : value;
	}

	private static BeanDefinitionBuilder createBeanDefinitionBuilder(
			Class<?> beanClass) {
		return BeanDefinitionBuilder.rootBeanDefinition(beanClass);
	}

	public static BeanDefinition parseHttpHandler(Element element,
			ParserContext parserContext) {
		BeanDefinitionBuilder httpHandler = createBeanDefinitionBuilder(HTTP_HANDLER_FACTORY_BEAN_CLASS);

		// OData
		BeanDefinitionBuilder odataBuilder = createBeanDefinitionBuilder(ODATA_FACTORY_BEAN_CLASS);
		BeanDefinition odata = odataBuilder.getBeanDefinition();

		httpHandler.addPropertyValue(ODATA_PROPERTY, odata);

		// ServiceMetadata
		BeanDefinitionBuilder serviceMetadata = createBeanDefinitionBuilder(SERVICE_METADATA_FACTORY_BEAN_CLASS);
		serviceMetadata.addPropertyValue(ODATA_PROPERTY, odata);

		String edmProviderRef = elementAttribute(element, EDM_PROVIDER_ATTR);
		serviceMetadata.addPropertyValue(EDM_PROVIDER_PROPERTY,
				new RuntimeBeanReference(edmProviderRef));

		// References
		List<Element> referenceElements = DomUtils.getChildElementsByTagName(
				element, REFERENCE_ELEMENT);
		if (referenceElements.size() > 0) {
			ManagedList<BeanDefinition> referenceList = new ManagedList<BeanDefinition>(
					referenceElements.size());
			for (Element referenceElement : referenceElements) {
				BeanDefinition reference = parseReference(referenceElement,
						parserContext);
				referenceList.add(reference);
			}
			serviceMetadata.addPropertyValue(REFERENCES_LIST_PROPERTY,
					referenceList);
		}

		httpHandler.addPropertyValue(SERVICE_METADATA_PROPERTY,
				serviceMetadata.getBeanDefinition());

		// Processors
		List<Element> processorElements = DomUtils.getChildElementsByTagName(
				element, PROCESSOR_ELEMENT);
		if (processorElements.size() > 0) {
			ManagedList<RuntimeBeanReference> processorList = new ManagedList<RuntimeBeanReference>(
					processorElements.size());
			for (Element processorElement : processorElements) {
				RuntimeBeanReference processorRef = parseProcessor(
						processorElement, parserContext);
				processorList.add(processorRef);
			}
			httpHandler.addPropertyValue(PROCESSORS_LIST_PROPERTY,
					processorList);
		}

		AbstractBeanDefinition configurationDef = httpHandler
				.getBeanDefinition();
		return configurationDef;
	}

	private static BeanDefinition parseReference(Element referenceElement,
			ParserContext parserContext) {
		BeanDefinitionBuilder reference = createBeanDefinitionBuilder(EDMX_REFERENCE_FACTORY_BEAN);

		String uri = elementAttribute(referenceElement, URI_ATTR);
		reference.addPropertyValue(URI_PROPERTY, uri);

		// Processors
		List<Element> includeElements = DomUtils.getChildElementsByTagName(
				referenceElement, INCLUDE_ELEMENT);
		if (includeElements.size() > 0) {
			ManagedMap<String, String> includeMap = new ManagedMap<String, String>(
					includeElements.size());
			for (Element includeElement : includeElements) {
				String key = elementAttribute(includeElement, KEY_ATTR);
				String value = elementAttribute(includeElement, VALUE_ATTR);
				includeMap.put(key, value);
			}
			reference.addPropertyValue(INCLUDES_PROPERTY, includeMap);
		}

		return reference.getBeanDefinition();
	}

	private static RuntimeBeanReference parseProcessor(
			Element processorElement, ParserContext parserContext) {
		String ref = elementAttribute(processorElement, REF_ATTR);
		return new RuntimeBeanReference(ref);
	}

}
