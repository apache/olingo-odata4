package myservice.mynamespace.service;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import myservice.mynamespace.data.Storage;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceProperty;

public class DemoPrimitiveProcessor implements PrimitiveProcessor {

	private OData odata;
	private Storage storage;

	public DemoPrimitiveProcessor(Storage storage) {
		this.storage = storage;
	}

	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;

	}

	/*
	 * In our example, the URL would be: http://localhost:8080/DemoService/DemoService.svc/Products(1)/Name
	 * and the response:
	 * {
	 *	  @odata.context: "$metadata#Products/Name",
	 *	  value: "Notebook Basic 15"
	 * }
	 * */
	public void readPrimitive(ODataRequest request, ODataResponse response,
								UriInfo uriInfo, ContentType responseFormat)
								throws ODataApplicationException, SerializerException {

		// 1. Retrieve info from URI
		// 1.1. retrieve the info about the requested entity set
		List<UriResource> resourceParts = uriInfo.getUriResourceParts();
		// Note: only in our example we can rely that the first segment is the EntitySet
		UriResourceEntitySet uriEntityset = (UriResourceEntitySet) resourceParts.get(0);
		EdmEntitySet edmEntitySet = uriEntityset.getEntitySet();
		// the key for the entity
		List<UriParameter> keyPredicates = uriEntityset.getKeyPredicates();

		// 1.2. retrieve the requested (Edm) property
		UriResourceProperty uriProperty = (UriResourceProperty)resourceParts.get(resourceParts.size() -1); // the last segment is the Property
		EdmProperty edmProperty = uriProperty.getProperty();
		String edmPropertyName = edmProperty.getName();
		// in our example, we know we have only primitive types in our model
		EdmPrimitiveType edmPropertyType = (EdmPrimitiveType) edmProperty.getType();


		// 2. retrieve data from backend
		// 2.1. retrieve the entity data, for which the property has to be read
		Entity entity = storage.readEntityData(edmEntitySet, keyPredicates);
		if (entity == null) { // Bad request
			throw new ODataApplicationException("Entity not found", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
		}

		// 2.2. retrieve the property data from the entity
		Property property = entity.getProperty(edmPropertyName);
		if (property == null) {
			throw new ODataApplicationException("Property not found", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
		}

		// 3. serialize
		Object value = property.getValue();
		if (value != null) {
			// 3.1. configure the serializer
			ODataFormat format = ODataFormat.fromContentType(responseFormat);
			ODataSerializer serializer = odata.createSerializer(format);

			ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).navOrPropertyPath(edmPropertyName).build();
			PrimitiveSerializerOptions options = PrimitiveSerializerOptions.with().contextURL(contextUrl).build();
			// 3.2. serialize
			SerializerResult serializerResult = serializer.primitive(edmPropertyType, property, options);
			InputStream propertyStream = serializerResult.getContent();

			//4. configure the response object
			response.setContent(propertyStream);
			response.setStatusCode(HttpStatusCode.OK.getStatusCode());
			response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
		} else {
			// in case there's no value for the property, we can skip the serialization
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
		}
	}



	/*
	 * These processor methods are not handled in this tutorial
	 * */

	public void updatePrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat)
								throws ODataApplicationException, DeserializerException, SerializerException {
		throw new ODataApplicationException("Not supported.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
	}

	public void deletePrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo) throws ODataApplicationException {
		throw new ODataApplicationException("Not supported.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
	}
}
