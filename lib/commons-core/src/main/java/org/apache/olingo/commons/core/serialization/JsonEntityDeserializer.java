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
package org.apache.olingo.commons.core.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.AnnotationImpl;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Reads JSON string into an entity.
 * <br/>
 * If metadata information is available, the corresponding entity fields and content will be populated.
 */
public class JsonEntityDeserializer extends JsonDeserializer {

  public JsonEntityDeserializer(final ODataServiceVersion version, final boolean serverMode) {
    super(version, serverMode);
  }

  protected ResWrap<Entity> doDeserialize(final JsonParser parser) throws IOException {

    final ObjectNode tree = parser.getCodec().readTree(parser);

    if (tree.has(Constants.VALUE) && tree.get(Constants.VALUE).isArray()) {
      throw new JsonParseException("Expected OData Entity, found EntitySet", parser.getCurrentLocation());
    }

    final EntityImpl entity = new EntityImpl();

    final URI contextURL;
    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      contextURL = URI.create(tree.get(Constants.JSON_CONTEXT).textValue());
      tree.remove(Constants.JSON_CONTEXT);
    } else if (tree.hasNonNull(Constants.JSON_METADATA)) {
      contextURL = URI.create(tree.get(Constants.JSON_METADATA).textValue());
      tree.remove(Constants.JSON_METADATA);
    } else {
      contextURL = null;
    }
    if (contextURL != null) {
      entity.setBaseURI(StringUtils.substringBefore(contextURL.toASCIIString(), Constants.METADATA));
    }
    
    final String metadataETag;
    if (tree.hasNonNull(Constants.JSON_METADATA_ETAG)) {
      metadataETag = tree.get(Constants.JSON_METADATA_ETAG).textValue();
      tree.remove(Constants.JSON_METADATA_ETAG);
    } else {
      metadataETag = null;
    }

    if (tree.hasNonNull(jsonETag)) {
      entity.setETag(tree.get(jsonETag).textValue());
      tree.remove(jsonETag);
    }

    if (tree.hasNonNull(jsonType)) {
      entity.setType(new EdmTypeInfo.Builder().setTypeExpression(tree.get(jsonType).textValue()).build().internal());
      tree.remove(jsonType);
    }

    if (tree.hasNonNull(jsonId)) {
      entity.setId(URI.create(tree.get(jsonId).textValue()));
      tree.remove(jsonId);
    }

    if (tree.hasNonNull(jsonReadLink)) {
      final LinkImpl link = new LinkImpl();
      link.setRel(Constants.SELF_LINK_REL);
      link.setHref(tree.get(jsonReadLink).textValue());
      entity.setSelfLink(link);

      tree.remove(jsonReadLink);
    }

    if (tree.hasNonNull(jsonEditLink)) {
      final LinkImpl link = new LinkImpl();
      if (serverMode) {
        link.setRel(Constants.EDIT_LINK_REL);
      }
      link.setHref(tree.get(jsonEditLink).textValue());
      entity.setEditLink(link);

      tree.remove(jsonEditLink);
    }

    if (tree.hasNonNull(jsonMediaReadLink)) {
      entity.setMediaContentSource(URI.create(tree.get(jsonMediaReadLink).textValue()));
      tree.remove(jsonMediaReadLink);
    }
    if (tree.hasNonNull(jsonMediaEditLink)) {
      entity.setMediaContentSource(URI.create(tree.get(jsonMediaEditLink).textValue()));
      tree.remove(jsonMediaEditLink);
    }
    if (tree.hasNonNull(jsonMediaContentType)) {
      entity.setMediaContentType(tree.get(jsonMediaContentType).textValue());
      tree.remove(jsonMediaContentType);
    }
    if (tree.hasNonNull(jsonMediaETag)) {
      entity.setMediaETag(tree.get(jsonMediaETag).textValue());
      tree.remove(jsonMediaETag);
    }

    final Set<String> toRemove = new HashSet<String>();

    final Map<String, List<Annotation>> annotations = new HashMap<String, List<Annotation>>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = tree.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();
      final Matcher customAnnotation = CUSTOM_ANNOTATION.matcher(field.getKey());

      links(field, entity, toRemove, tree, parser.getCodec());
      if (field.getKey().endsWith(getJSONAnnotation(jsonMediaEditLink))) {
        final LinkImpl link = new LinkImpl();
        link.setTitle(getTitle(field));
        link.setRel(version.getNamespace(ODataServiceVersion.NamespaceKey.MEDIA_EDIT_LINK_REL) + getTitle(field));
        link.setHref(field.getValue().textValue());
        link.setType(ODataLinkType.MEDIA_EDIT.toString());
        entity.getMediaEditLinks().add(link);

        if (tree.has(link.getTitle() + getJSONAnnotation(jsonMediaETag))) {
          link.setMediaETag(tree.get(link.getTitle() + getJSONAnnotation(jsonMediaETag)).asText());
          toRemove.add(link.getTitle() + getJSONAnnotation(jsonMediaETag));
        }

        toRemove.add(field.getKey());
        toRemove.add(setInline(field.getKey(), getJSONAnnotation(jsonMediaEditLink), tree, parser.getCodec(), link));
      } else if (field.getKey().endsWith(getJSONAnnotation(jsonMediaContentType))) {
        final String linkTitle = getTitle(field);
        for (Link link : entity.getMediaEditLinks()) {
          if (linkTitle.equals(link.getTitle())) {
            ((LinkImpl) link).setType(field.getValue().asText());
          }
        }
        toRemove.add(field.getKey());
      } else if (field.getKey().charAt(0) == '#') {
        final ODataOperation operation = new ODataOperation();
        operation.setMetadataAnchor(field.getKey());

        final ObjectNode opNode = (ObjectNode) tree.get(field.getKey());
        operation.setTitle(opNode.get(Constants.ATTR_TITLE).asText());
        operation.setTarget(URI.create(opNode.get(Constants.ATTR_TARGET).asText()));

        entity.getOperations().add(operation);

        toRemove.add(field.getKey());
      } else if (customAnnotation.matches() && !"odata".equals(customAnnotation.group(2))) {
        final Annotation annotation = new AnnotationImpl();
        annotation.setTerm(customAnnotation.group(2) + "." + customAnnotation.group(3));
        try {
          value(annotation, field.getValue(), parser.getCodec());
        } catch (final EdmPrimitiveTypeException e) {
          throw new IOException(e);
        }

        if (!annotations.containsKey(customAnnotation.group(1))) {
          annotations.put(customAnnotation.group(1), new ArrayList<Annotation>());
        }
        annotations.get(customAnnotation.group(1)).add(annotation);
      }
    }

    for (Link link : entity.getNavigationLinks()) {
      if (annotations.containsKey(link.getTitle())) {
        link.getAnnotations().addAll(annotations.get(link.getTitle()));
        for (Annotation annotation : annotations.get(link.getTitle())) {
          toRemove.add(link.getTitle() + "@" + annotation.getTerm());
        }
      }
    }
    for (Link link : entity.getMediaEditLinks()) {
      if (annotations.containsKey(link.getTitle())) {
        link.getAnnotations().addAll(annotations.get(link.getTitle()));
        for (Annotation annotation : annotations.get(link.getTitle())) {
          toRemove.add(link.getTitle() + "@" + annotation.getTerm());
        }
      }
    }

    tree.remove(toRemove);

    try {
      populate(entity, entity.getProperties(), tree, parser.getCodec());
    } catch (final EdmPrimitiveTypeException e) {
      throw new IOException(e);
    }

    return new ResWrap<Entity>(contextURL, metadataETag, entity);
  }
}
