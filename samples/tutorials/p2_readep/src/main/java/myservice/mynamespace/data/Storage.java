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
package myservice.mynamespace.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import myservice.mynamespace.service.DemoEdmProvider;
import myservice.mynamespace.util.Util;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

public class Storage {

	private List<Entity> productList;

	public Storage() {
		productList = new ArrayList<Entity>();
		initSampleData();
	}

	/* PUBLIC FACADE */

	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet)throws ODataApplicationException{

		// actually, this is only required if we have more than one Entity Sets
		if(edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)){
			return getProducts();
		}

		return null;
	}

	public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) throws ODataApplicationException{

		EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		// actually, this is only required if we have more than one Entity Type
		if(edmEntityType.getName().equals(DemoEdmProvider.ET_PRODUCT_NAME)){
			return getProduct(edmEntityType, keyParams);
		}

		return null;
	}



	/*  INTERNAL */

	private EntityCollection getProducts(){
		EntityCollection retEntitySet = new EntityCollection();

		for(Entity productEntity : this.productList){
			   retEntitySet.getEntities().add(productEntity);
		}

		return retEntitySet;
	}


	private Entity getProduct(EdmEntityType edmEntityType, List<UriParameter> keyParams) throws ODataApplicationException{

		// the list of entities at runtime
		EntityCollection entitySet = getProducts();
		
		/*  generic approach  to find the requested entity */
		Entity requestedEntity = Util.findEntity(edmEntityType, entitySet, keyParams);
		
		if(requestedEntity == null){
			// this variable is null if our data doesn't contain an entity for the requested key
			// Throw suitable exception
			throw new ODataApplicationException("Entity for requested key doesn't exist",
          HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
		}

		return requestedEntity;
	}

	/* HELPER */

	private void initSampleData(){

		// add some sample product entities
		productList.add(new Entity()
			.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 1))
			.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Notebook Basic 15"))
			.addProperty(new Property(null, "Description", ValueType.PRIMITIVE, "Notebook Basic, 1.7GHz - 15 XGA - 1024MB DDR2 SDRAM - 40GB")));

		productList.add(new Entity()
			.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 2))
			.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "1UMTS PDA"))
			.addProperty(new Property(null, "Description", ValueType.PRIMITIVE, "Ultrafast 3G UMTS/HSDPA Pocket PC, supports GSM network")));

		productList.add(new Entity()
			.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 3))
			.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Ergo Screen"))
			.addProperty(new Property(null, "Description", ValueType.PRIMITIVE, "19 Optimum Resolution 1024 x 768 @ 85Hz, resolution 1280 x 960")));

	}
}
