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
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ServiceMetadataFactoryBean implements
		FactoryBean<ServiceMetadata>, InitializingBean {
	private OData odata;
	private CsdlEdmProvider edmProvider;
	private List<EdmxReference> references;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (odata == null) {
			throw new IllegalArgumentException(
					"The property odata is required.");
		}

		if (edmProvider == null) {
			throw new IllegalArgumentException(
					"The property edmProvider is required.");
		}

		if (references == null) {
			throw new IllegalArgumentException(
					"The property references is required.");
		}
	}

	@Override
	public ServiceMetadata getObject() throws Exception {
		return odata.createServiceMetadata(edmProvider, references);
	}

	@Override
	public Class<?> getObjectType() {
		return ServiceMetadata.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public OData getOdata() {
		return odata;
	}

	public void setOdata(OData odata) {
		this.odata = odata;
	}

	public CsdlEdmProvider getEdmProvider() {
		return edmProvider;
	}

	public void setEdmProvider(CsdlEdmProvider edmProvider) {
		this.edmProvider = edmProvider;
	}

	public List<EdmxReference> getReferences() {
		return references;
	}

	public void setReferences(List<EdmxReference> references) {
		this.references = references;
	}
}
