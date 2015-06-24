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

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Dedicated Spring namespace handler for Olingo. This namespace is directly
 * usable within a Spring application context.
 * 
 * This namespace can be configured in Spring XML configuration files using
 * standard mechanisms of XML namespace, as following:
 * 
 * &lt;beans xmlns="http://www.springframework.org/schema/beans"
 *     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *     xmlns:olingo="http://olingo.apache.org/schema/olingo/spring-olingo"
 *     xsi:schemaLocation="http://www.springframework.org/schema/beans
 *         http://www.springframework.org/schema/beans/spring-beans.xsd
 *       http://olingo.apache.org/schema/olingo/spring-olingo
 *         http://olingo.apache.org/schema/olingo/spring-olingo.xsd"&gt;
 *   (...)
 * &lt;/beans&gt;
 * 
 * @author Thierry Templier
 */
public class OlingoNamespaceHandler extends NamespaceHandlerSupport {

	public static final String EDM_PROVIDER_ELEMENT = "edm-provider";

	public static final String HTTP_HANDLER_ELEMENT = "http-handler";

	/**
	 * Registers bean definition parsers for the olingo namespace.
	 * 
	 * @see OlingoEdmProviderBeanDefinitionParser
	 * @see OlingoHttpHandlerBeanDefinitionParser
	 */
	public void init() {
		registerBeanDefinitionParser(EDM_PROVIDER_ELEMENT,
				new OlingoEdmProviderBeanDefinitionParser());
		registerBeanDefinitionParser(HTTP_HANDLER_ELEMENT,
				new OlingoHttpHandlerBeanDefinitionParser());
	}

}
