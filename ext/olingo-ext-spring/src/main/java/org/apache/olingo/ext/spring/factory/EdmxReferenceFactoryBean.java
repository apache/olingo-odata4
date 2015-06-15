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
package org.apache.olingo.ext.spring.factory;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class EdmxReferenceFactoryBean implements FactoryBean<EdmxReference>,
		InitializingBean {
	private String uri;
	private Map<String, String> includes;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (uri == null) {
			throw new IllegalArgumentException("The property uri is required.");
		}
	}

	@Override
	public EdmxReference getObject() throws Exception {
		EdmxReference reference = new EdmxReference(URI.create(uri));
		if (includes != null) {
			for (Entry<String, String> include : includes.entrySet()) {
				reference.addInclude(new EdmxReferenceInclude(include.getKey(),
						include.getValue()));
			}
		}
		return reference;
	}

	@Override
	public Class<?> getObjectType() {
		return EdmxReference.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Map<String, String> getIncludes() {
		return includes;
	}

	public void setIncludes(Map<String, String> includes) {
		this.includes = includes;
	}

}
