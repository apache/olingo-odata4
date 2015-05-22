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

package org.apache.olingo.server.core.serializer.utils;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.commons.core.edm.EdmPropertyImpl;
import org.apache.olingo.server.core.uri.UriHelperImpl;

/**
 * Builder to build the the entity metadata according to the accept header parameter odata.metadata=full
 * (as defined in the <a
 * href="http://docs.oasis-open.org/odata/odata-json-format/v4.0/errata02/os/odata-json-format-v4.0-errata02-os-complete
 * .html#_Toc403940604">protocol specification</a>).
 */

public class JsonEntityMetadataBuilder {

  private static ContextURL contextURL=null;

  public static void setContextURL(final ContextURL argContextURL){
    contextURL=argContextURL;
  }

  public static String getEntityTypeValue( final EdmEntityType entityType ){
    StringBuilder result = new StringBuilder();
    result.append('#');
    return result.append(entityType.getFullQualifiedName().getFullQualifiedNameAsString()).toString();
  }

  public static String getEntityIDValue( final ServiceMetadata metadata, final Entity entity )
                                                  throws SerializerException {
    StringBuilder result = new StringBuilder();
    if (entity.getId()!=null){
      return  result.append(entity.getId().toASCIIString()).toString();
    }
    if (contextURL != null && contextURL.getServiceRoot() != null) {
      result.append(contextURL.getServiceRoot());
    }
    Edm edm = metadata.getEdm();
    EdmEntitySet edmEntitySet = edm.getEntityContainer().getEntitySet(contextURL.getEntitySetOrSingletonOrType());
    result.append(new UriHelperImpl().buildCanonicalURL(edmEntitySet, entity));
    return result.toString();
  }

  public static String getEntityReadLinkValue( final Entity entity ) throws SerializerException {
    StringBuilder result = new StringBuilder();
    if (contextURL != null && contextURL.getServiceRoot() != null) {
      result.append(contextURL.getServiceRoot());
    }
    return result.append(entity.getSelfLink().getHref()).toString();
  }

  public static String getEntityEditLinkValue( final Entity entity ) throws SerializerException {
    StringBuilder result = new StringBuilder();
    if (contextURL != null && contextURL.getServiceRoot() != null) {
      result.append(contextURL.getServiceRoot());
    }
    return result.append(entity.getEditLink().getHref()).toString();
  }

  public static String getMediaReadLinkValue( final ServiceMetadata metadata, final Entity entity )
            throws SerializerException {
    StringBuilder result = new StringBuilder();
    if (entity.getSelfLink()!=null) {
      if (contextURL != null && contextURL.getServiceRoot() != null) {
        result.append(contextURL.getServiceRoot());
      }
      return result.append(entity.getSelfLink().getHref()).toString();
    }
    return result.append(getEntityIDValue(metadata, entity)).append('/').append("$value").toString();
  }

  public static String getMediaEditLinkValue( final ServiceMetadata metadata, final Entity entity )
            throws SerializerException {
    StringBuilder result = new StringBuilder();
    if (entity.getEditLink()!=null) {
      if (contextURL != null && contextURL.getServiceRoot() != null) {
        result.append(contextURL.getServiceRoot());
      }
      return result.append(entity.getEditLink().getHref()).toString();
    }
    return result.append(getEntityIDValue(metadata, entity)).append('/').append("$value").toString();
  }

  public static String getAssociationPropertyKey( final String propertyName )
          throws SerializerException{
    StringBuilder result = new StringBuilder();
    result.append(propertyName).append(Constants.JSON_ASSOCIATION_LINK);
    return result.toString();
  }

  public static String getAssociationPropertyValue( final ServiceMetadata metadata, final String propertyName,
                                        final Linked linked ) throws SerializerException {
    StringBuilder result = new StringBuilder();
    if (contextURL != null && contextURL.getServiceRoot() != null) {
      result.append(contextURL.getServiceRoot());
    }
    Edm edm=metadata.getEdm();
    EdmEntitySet edmEntitySet=edm.getEntityContainer().getEntitySet(contextURL.getEntitySetOrSingletonOrType());
    result.append(new UriHelperImpl().buildCanonicalURL(edmEntitySet,(Entity)linked));
    result.append('/').append(propertyName).append('/').append(ContextURL.Suffix.REFERENCE.getRepresentation());
    return result.toString();
  }

  public static String getNavigationPropertyKey( final String propertyName )
          throws SerializerException {
    StringBuilder result = new StringBuilder();
    result.append(propertyName).append(Constants.JSON_NAVIGATION_LINK);
    return result.toString();
  }

  public static String getNavigationPropertyValue( final ServiceMetadata metadata, final String propertyName,
                                       final Linked linked ) throws SerializerException {
    StringBuilder result = new StringBuilder();
    if (contextURL != null && contextURL.getServiceRoot() != null) {
      result.append(contextURL.getServiceRoot());
    }
    Edm edm=metadata.getEdm();
    EdmEntitySet edmEntitySet=edm.getEntityContainer().getEntitySet(contextURL.getEntitySetOrSingletonOrType());
    result.append(new UriHelperImpl().buildCanonicalURL(edmEntitySet,(Entity)linked));
    result.append('/').append(propertyName);
    return result.toString();
  }

  public static String getPropertyTypeKey( final EdmProperty edmProperty ){
    StringBuilder result = new StringBuilder();
    result.append(edmProperty.getName()).append(Constants.JSON_TYPE);
    return result.toString();
  }

  public static String getPrimitivePropertyTypeValue( final EdmProperty edmProperty ){
    StringBuilder result = new StringBuilder();
    result.append('#').append(((EdmPropertyImpl)edmProperty).getTypeInfo().getPrimitiveTypeKind().toString());
    return result.toString();
  }

  public static String getPrimitivePropertyTypeValue( final EdmPrimitiveType type ){
    StringBuilder result = new StringBuilder();
    result.append('#').append(type.getFullQualifiedName().getName());
    return result.toString();
  }

  public static String getPrimitiveCollectionTypeValue( final EdmProperty edmProperty ){
    StringBuilder result = new StringBuilder();
    result.append('#').append("Collection").append('(')
                   .append(((EdmPropertyImpl) edmProperty).getTypeInfo()
                           .getPrimitiveTypeKind().toString()).append(')');
    return result.toString();
  }

  public static String getPrimitiveCollectionTypeValue( final EdmPrimitiveType type ){
    StringBuilder result = new StringBuilder();
    result.append('#').append("Collection").append('(')
            .append(type.getFullQualifiedName().getName()).append(')');
    return result.toString();
  }

  public static String getComplexPropertyTypeValue( final EdmProperty edmProperty ){
    StringBuilder result = new StringBuilder();
    result.append('#')
               .append(((EdmPropertyImpl)edmProperty).getTypeInfo()
                       .getComplexType().getFullQualifiedName().toString());
    return result.toString();
  }

  public static String getComplexPropertyTypeValue( final EdmComplexType type ){
    StringBuilder result = new StringBuilder();
    result.append('#').append(type.getFullQualifiedName()
            .getFullQualifiedNameAsString().toString());
    return result.toString();
  }

  public static String getComplexCollectionTypeValue( final EdmProperty edmProperty ){
    StringBuilder result = new StringBuilder();
    result.append('#').append("Collection").append('(')
               .append(((EdmPropertyImpl) edmProperty).getTypeInfo()
                       .getComplexType().getFullQualifiedName().toString())
                            .append(')');
    return result.toString();
  }

  public static String getComplexCollectionTypeValue( final EdmComplexType type ){
    StringBuilder result = new StringBuilder();
    result.append('#').append("Collection").append('(')
            .append(type.getFullQualifiedName().getFullQualifiedNameAsString().toString())
                .append(')');
    return result.toString();
  }

}
