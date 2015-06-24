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
package org.apache.olingo.ext.config.spring.factory;

import java.util.List;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.Processor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ODataHttpHandlerFactoryBean implements
		FactoryBean<ODataHttpHandler>, InitializingBean {
	private OData odata;
	private ServiceMetadata serviceMetadata;
	private List<Processor> processors;

	@Override
	public ODataHttpHandler getObject() throws Exception {
		ODataHttpHandler handler = odata.createHandler(serviceMetadata);
		if (processors != null) {
			for (Processor processor : processors) {
				handler.register(processor);
			}
		}
		return handler;
	}

	@Override
	public Class<?> getObjectType() {
		return ODataHttpHandler.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (odata == null) {
			throw new IllegalArgumentException(
					"The property odata is required.");
		}

		if (serviceMetadata == null) {
			throw new IllegalArgumentException(
					"The property serviceMetadata is required.");
		}
	}

	public OData getOdata() {
		return odata;
	}

	public void setOdata(OData odata) {
		this.odata = odata;
	}

	public ServiceMetadata getServiceMetadata() {
		return serviceMetadata;
	}

	public void setServiceMetadata(ServiceMetadata serviceMetadata) {
		this.serviceMetadata = serviceMetadata;
	}

	public List<Processor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<Processor> processors) {
		this.processors = processors;
	}

}
