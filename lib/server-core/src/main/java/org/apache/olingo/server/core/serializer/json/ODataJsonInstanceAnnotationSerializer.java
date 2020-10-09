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

import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.IConstants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.serializer.utils.ContentTypeHelper;

import com.fasterxml.jackson.core.JsonGenerator;

public class ODataJsonInstanceAnnotationSerializer {

	private final boolean isODataMetadataNone;
	private final boolean isODataMetadataFull;
	private IConstants constants;
	private final boolean isIEEE754Compatible;

	public ODataJsonInstanceAnnotationSerializer(final ContentType contentType, final IConstants constants) {
		isIEEE754Compatible = ContentTypeHelper.isODataIEEE754Compatible(contentType);
		isODataMetadataNone = ContentTypeHelper.isODataMetadataNone(contentType);
		isODataMetadataFull = ContentTypeHelper.isODataMetadataFull(contentType);
		this.constants = constants;
	}

	/**
	 * Write the instance annotation of an entity
	 * @param annotations List of annotations
	 * @param json JsonGenerator
	 * @throws IOException 
	 * @throws SerializerException
	 * @throws DecoderException
	 */
	public void writeInstanceAnnotationsOnEntity(final List<Annotation> annotations, final JsonGenerator json)
			throws IOException, SerializerException, DecoderException {
		for (Annotation annotation : annotations) {
			if (isODataMetadataFull) {
				json.writeStringField(constants.getType(), "#" + annotation.getType());
			}
			json.writeFieldName("@" + annotation.getTerm());
			writeInstanceAnnotation(json, annotation, "");
		}
	}

	/**
	 * Write instance annotation of a property
	 * @param edmProperty EdmProperty
	 * @param property Property
	 * @param json JsonGenerator
	 * @throws IOException
	 * @throws SerializerException
	 * @throws DecoderException
	 */
	public void writeInstanceAnnotationsOnProperties(final EdmProperty edmProperty, final Property property,
			final JsonGenerator json) throws IOException, SerializerException, DecoderException {
		if (property != null) {
			for (Annotation annotation : property.getAnnotations()) {
				json.writeFieldName(edmProperty.getName() + "@" + annotation.getTerm());
				writeInstanceAnnotation(json, annotation, "");
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeInstanceAnnotation(final JsonGenerator json, Valuable annotation, String name)
			throws IOException, SerializerException, DecoderException {
		try {
			switch (annotation.getValueType()) {
			case PRIMITIVE:
				if (isODataMetadataFull && name.length() > 0) {
					json.writeStringField(name + constants.getType(), "#" + annotation.getType());
				}
				if (name.length() > 0) {
					json.writeFieldName(name);
				}
				writeInstanceAnnotOnPrimitiveProperty(json, annotation, annotation.getValue());
				break;
			case COLLECTION_PRIMITIVE:
				if (isODataMetadataFull && name.length() > 0) {
					json.writeStringField(name + constants.getType(), 
							"#Collection(" + annotation.getType() + ")");
				}
				if (name.length() > 0) {
					json.writeFieldName(name);
				}
				json.writeStartArray();
				List list = annotation.asCollection();
				for (Object value : list) {
					writeInstanceAnnotOnPrimitiveProperty(json, annotation, value);
				}
				json.writeEndArray();
				break;
			case COMPLEX:
				if (isODataMetadataFull && name.length() > 0) {
					json.writeStringField(name + constants.getType(), "#" + annotation.getType());
				}
				if (name.length() > 0) {
					json.writeFieldName(name);
				}
				ComplexValue complexValue = annotation.asComplex();
				writeInstanceAnnotOnComplexProperty(json, annotation, complexValue);
				break;
			case COLLECTION_COMPLEX:
				if (isODataMetadataFull && name.length() > 0) {
					json.writeStringField(name + constants.getType(), 
							"#Collection(" + annotation.getType() + ")");
				}
				if (name.length() > 0) {
					json.writeFieldName(name);
				}
				json.writeStartArray();
				List<ComplexValue> complexValues = (List<ComplexValue>) annotation.asCollection();
				for (ComplexValue complxValue : complexValues) {
					writeInstanceAnnotOnComplexProperty(json, annotation, complxValue);
				}
				json.writeEndArray();
				break;
			default:
			}
		} catch (final EdmPrimitiveTypeException e) {
			throw new SerializerException("Wrong value for instance annotation!", e,
					SerializerException.MessageKeys.WRONG_PROPERTY_VALUE, 
					((Annotation) annotation).getTerm(),
					annotation.getValue().toString());
		}
	}

	private void writeInstanceAnnotOnComplexProperty(final JsonGenerator json, Valuable annotation,
			ComplexValue complexValue) throws IOException, SerializerException, DecoderException {
		json.writeStartObject();
		if (isODataMetadataFull) {
			json.writeStringField(constants.getType(), "#" + complexValue.getTypeName());
		}
		List<Property> properties = complexValue.getValue();
		for (Property prop : properties) {
			writeInstanceAnnotation(json, prop, prop.getName());
		}
		json.writeEndObject();
	}

	private void writeInstanceAnnotOnPrimitiveProperty(final JsonGenerator json, Valuable annotation, Object value)
			throws IOException, EdmPrimitiveTypeException {
		writePrimitiveValue("",
				EdmPrimitiveTypeFactory.getInstance(
						EdmPrimitiveTypeKind.getByName(annotation.getType())), value, null,
				null, null, null, true, json);
	}

	protected void writePrimitiveValue(final String name, final EdmPrimitiveType type, final Object primitiveValue,
			final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
			final Boolean isUnicode, final JsonGenerator json) 
					throws EdmPrimitiveTypeException, IOException {
		final String value = type.valueToString(
				primitiveValue, isNullable, maxLength, precision, scale, isUnicode);
		if (value == null) {
			json.writeNull();
		} else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean)) {
			json.writeBoolean(Boolean.parseBoolean(value));
		} else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte)
				|| type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double)
				|| type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16)
				|| type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32)
				|| type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)
				|| type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single)
				|| (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal)
				|| type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64))
						&& !isIEEE754Compatible) {
			json.writeNumber(value);
		} else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Stream)) {
			if (primitiveValue instanceof Link) {
				Link stream = (Link) primitiveValue;
				if (!isODataMetadataNone) {
					if (stream.getMediaETag() != null) {
						json.writeStringField(name + constants.getMediaEtag(), 
								stream.getMediaETag());
					}
					if (stream.getType() != null) {
						json.writeStringField(name + constants.getMediaContentType(), 
								stream.getType());
					}
				}
				if (isODataMetadataFull) {
					if (stream.getRel() != null && 
							stream.getRel().equals(Constants.NS_MEDIA_READ_LINK_REL)) {
						json.writeStringField(name + constants.getMediaReadLink(), 
								stream.getHref());
					}
					if (stream.getRel() == null || 
							stream.getRel().equals(Constants.NS_MEDIA_EDIT_LINK_REL)) {
						json.writeStringField(name + constants.getMediaEditLink(), 
								stream.getHref());
					}
				}
			}
		} else {
			json.writeString(value);
		}
	}
}
