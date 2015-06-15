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
package org.apache.olingo.ext.spring.edm;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.ActionImport;
import org.apache.olingo.server.api.edm.provider.AliasInfo;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.api.edm.provider.Function;
import org.apache.olingo.server.api.edm.provider.FunctionImport;
import org.apache.olingo.server.api.edm.provider.Schema;
import org.apache.olingo.server.api.edm.provider.Singleton;
import org.apache.olingo.server.api.edm.provider.Term;
import org.apache.olingo.server.api.edm.provider.TypeDefinition;

public class GenericEdmProvider extends EdmProvider {

	private String containerName = "default";

	private List<Schema> schemas = new ArrayList<>();

	// OData

	@Override
	public List<Schema> getSchemas() throws ODataException {
		return schemas;
	}

	@Override
	public EntityContainer getEntityContainer() throws ODataException {
		EntityContainer container = new EntityContainer();
		container.setName(containerName);

		// EntitySets
		List<EntitySet> entitySets = new ArrayList<EntitySet>();
		container.setEntitySets(entitySets);

		// Load entity sets per index
		for (Schema schema : schemas) {

			if (schema.getEntityContainer() != null
					&& schema.getEntityContainer().getEntitySets() != null) {
				for (EntitySet schemaEntitySet : schema.getEntityContainer()
						.getEntitySets()) {
					EntitySet entitySet = new EntitySet().setName(
							schemaEntitySet.getName()).setType(
							new FullQualifiedName(schemaEntitySet.getType()
									.getNamespace(), schemaEntitySet.getType()
									.getName()));
					entitySets.add(entitySet);
				}
			}
		}

		return container;
	}

	private Schema findSchema(String namespace) {
		for (Schema schema : schemas) {
			if (schema.getNamespace().equals(namespace)) {
				return schema;
			}
		}

		return null;
	}

	private EntityType findEntityType(Schema schema, String entityTypeName) {
		for (EntityType entityType : schema.getEntityTypes()) {
			if (entityType.getName().equals(entityTypeName)) {
				return entityType;
			}
		}

		return null;
	}

	@Override
	public EntityType getEntityType(FullQualifiedName entityTypeName)
			throws ODataException {
		Schema schema = findSchema(entityTypeName.getNamespace());
		return findEntityType(schema, entityTypeName.getName());
	}

	private EnumType findEnumType(Schema schema, String enumTypeName) {
		for (EnumType enumType : schema.getEnumTypes()) {
			if (enumType.getName().equals(enumTypeName)) {
				return enumType;
			}
		}

		return null;
	}

	@Override
	public EnumType getEnumType(FullQualifiedName enumTypeName)
			throws ODataException {
		Schema schema = findSchema(enumTypeName.getNamespace());
		return findEnumType(schema, enumTypeName.getName());
	}

	@Override
	public TypeDefinition getTypeDefinition(FullQualifiedName typeDefinitionName)
			throws ODataException {
		System.out.println(">> getTypeDefinition");
		// TODO Auto-generated method stub
		return super.getTypeDefinition(typeDefinitionName);
	}

	private ComplexType findComplexType(Schema schema, String complexTypeName) {
		for (ComplexType complexType : schema.getComplexTypes()) {
			if (complexType.getName().equals(complexTypeName)) {
				return complexType;
			}
		}

		return null;
	}

	@Override
	public ComplexType getComplexType(FullQualifiedName complexTypeName)
			throws ODataException {
		Schema schema = findSchema(complexTypeName.getNamespace());
		return findComplexType(schema, complexTypeName.getName());
	}

	@Override
	public List<Action> getActions(FullQualifiedName actionName)
			throws ODataException {
		System.out.println(">> getActions");
		// TODO Auto-generated method stub
		return super.getActions(actionName);
	}

	@Override
	public List<Function> getFunctions(FullQualifiedName functionName)
			throws ODataException {
		System.out.println(">> getFunctions");
		// TODO Auto-generated method stub
		return super.getFunctions(functionName);
	}

	@Override
	public Term getTerm(FullQualifiedName termName) throws ODataException {
		System.out.println(">> getTerm");
		// TODO Auto-generated method stub
		return super.getTerm(termName);
	}

	private EntitySet findEntitySetInSchemas(String entitySetName)
			throws ODataException {
		List<Schema> schemas = getSchemas();
		for (Schema schema : schemas) {
			EntityContainer entityContainer = schema.getEntityContainer();
			List<EntitySet> entitySets = entityContainer.getEntitySets();
			for (EntitySet entitySet : entitySets) {
				if (entitySet.getName().equals(entitySetName)) {
					return entitySet;
				}
			}
		}
		return null;
	}

	@Override
	public EntitySet getEntitySet(FullQualifiedName entityContainer,
			String entitySetName) throws ODataException {
		return findEntitySetInSchemas(entitySetName);
	}

	@Override
	public Singleton getSingleton(FullQualifiedName entityContainer,
			String singletonName) throws ODataException {
		System.out.println(">> getSingleton");
		// TODO Auto-generated method stub
		return super.getSingleton(entityContainer, singletonName);
	}

	@Override
	public ActionImport getActionImport(FullQualifiedName entityContainer,
			String actionImportName) throws ODataException {
		System.out.println(">> getActionImport");
		// TODO Auto-generated method stub
		return super.getActionImport(entityContainer, actionImportName);
	}

	@Override
	public FunctionImport getFunctionImport(FullQualifiedName entityContainer,
			String functionImportName) throws ODataException {
		System.out.println(">> getFunctionImport");
		// TODO Auto-generated method stub
		return super.getFunctionImport(entityContainer, functionImportName);
	}

	@Override
	public EntityContainerInfo getEntityContainerInfo(
			FullQualifiedName entityContainerName) throws ODataException {
		EntityContainer container = getEntityContainer();
		FullQualifiedName fqName = new FullQualifiedName(container.getName(),
				container.getName());
		EntityContainerInfo info = new EntityContainerInfo();
		info.setContainerName(fqName);
		return info;
	}

	@Override
	public List<AliasInfo> getAliasInfos() throws ODataException {
		System.out.println(">> getAliasInfos");
		// TODO Auto-generated method stub
		return super.getAliasInfos();
	}

	// DI

	public void setSchemas(List<Schema> schemas) {
		this.schemas = schemas;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}
