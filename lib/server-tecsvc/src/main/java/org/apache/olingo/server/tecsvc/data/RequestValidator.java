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
package org.apache.olingo.server.tecsvc.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.tecsvc.data.DataProvider.DataProviderException;

public class RequestValidator {
  private final DataProvider provider;
  private final boolean isInsert;
  private final boolean isPatch;
  private final String rawServiceRoot;

  public RequestValidator(final DataProvider provider, final String rawServiceRoot) {
    this(provider, false, false, rawServiceRoot);
  }

  public RequestValidator(final DataProvider provider, final boolean isUpdate, final boolean isPatch,
      final String rawServiceRoot) {
    this.provider = provider;
    this.isInsert = !isUpdate;
    this.isPatch = isPatch;
    this.rawServiceRoot = rawServiceRoot;
  }

  public void validate(final EdmBindingTarget edmBindingTarget, final Entity entity)
      throws DataProviderException {
    final List<String> path = new ArrayList<String>();

    validateEntitySetProperties(entity.getProperties(), edmBindingTarget, edmBindingTarget.getEntityType(), path);
    validateNavigationProperties(entity, edmBindingTarget, edmBindingTarget.getEntityType(), path);
  }

  private void validateNavigationProperties(final Linked entity, final EdmBindingTarget edmBindingTarget,
      final EdmStructuredType edmType, final List<String> path) throws DataProviderException {
    for (final String navPropertyName : edmType.getNavigationPropertyNames()) {
      final EdmNavigationProperty edmProperty = edmType.getNavigationProperty(navPropertyName);
      if (entity == null && !edmProperty.isNullable()) {
        throw new DataProviderException("Navigation property " + navPropertyName + " must not be null",
            HttpStatusCode.BAD_REQUEST);
      } else if (entity != null) {
        final Link navigationBinding = entity.getNavigationBinding(navPropertyName);
        final Link navigationLink = entity.getNavigationLink(navPropertyName);
        final List<String> newPath = new ArrayList<String>(path);
        newPath.add(edmProperty.getName());
        final EdmBindingTarget target = edmBindingTarget.getRelatedBindingTarget(buildPath(newPath));

        final ValidationResult bindingResult = validateBinding(navigationBinding, edmProperty);
        final ValidationResult linkResult = validateNavigationLink(navigationLink,
            edmProperty,
            target);

        if ((isInsert && !edmProperty.isNullable()
            && (bindingResult != ValidationResult.FOUND
            && linkResult != ValidationResult.FOUND))
            || (!(isInsert && isPatch) && !edmProperty.isNullable() && linkResult == ValidationResult.EMPTY)) {
          throw new DataProviderException("Navigation property " + navPropertyName + " must not be null",
              HttpStatusCode.BAD_REQUEST);
        }
      }
    }
  }

  private String buildPath(final List<String> path) {
    final StringBuilder builder = new StringBuilder();

    for (final String segment : path) {
      if (builder.length() > 0) {
        builder.append("/");
      }

      builder.append(segment);
    }

    return builder.toString();
  }

  private ValidationResult validateBinding(final Link navigationBinding, final EdmNavigationProperty edmProperty)
      throws DataProviderException {
    if (navigationBinding == null) {
      return ValidationResult.NOT_FOUND;
    }

    if (edmProperty.isCollection()) {
      if (navigationBinding.getBindingLinks().size() == 0) {
        return ValidationResult.EMPTY;
      }

      for (final String bindingLink : navigationBinding.getBindingLinks()) {
        validateLink(bindingLink);
      }
    } else {
      if (navigationBinding.getBindingLink() == null) {
        return ValidationResult.EMPTY;
      }

      validateLink(navigationBinding.getBindingLink());
    }

    return ValidationResult.FOUND;
  }

  private ValidationResult validateNavigationLink(final Link navigationLink, final EdmNavigationProperty edmProperty,
      final EdmBindingTarget edmBindingTarget) throws DataProviderException {
    if (navigationLink == null) {
      return ValidationResult.NOT_FOUND;
    }

    if (edmProperty.isCollection()) {
      final EntityCollection inlineEntitySet = navigationLink.getInlineEntitySet();
      if (inlineEntitySet != null) {
        if (!isInsert && inlineEntitySet.getEntities().size() > 0) {
          throw new DataProvider.DataProviderException("Deep update is not allowed", HttpStatusCode.BAD_REQUEST);
        } else {
          for (final Entity entity : navigationLink.getInlineEntitySet().getEntities()) {
            validate(edmBindingTarget, entity);
          }
        }
      }
    } else {
      final Entity inlineEntity = navigationLink.getInlineEntity();
      if (!isInsert && inlineEntity != null) {
        throw new DataProvider.DataProviderException("Deep update is not allowed", HttpStatusCode.BAD_REQUEST);
      } else if (inlineEntity != null) {
        validate(edmBindingTarget, navigationLink.getInlineEntity());
      }
    }

    return ValidationResult.FOUND;
  }

  private void validateLink(final String bindingLink) throws DataProviderException {
    provider.getEntityByReference(bindingLink, rawServiceRoot);
  }

  private void validateEntitySetProperties(final List<Property> properties, final EdmBindingTarget edmBindingTarget,
      final EdmEntityType edmType, final List<String> path) throws DataProviderException {
    validateProperties(properties, edmBindingTarget, edmType, edmType.getKeyPredicateNames(), path);
  }

  private void validateProperties(final List<Property> properties, final EdmBindingTarget edmBindingTarget,
      final EdmStructuredType edmType, final List<String> keyPredicateNames, final List<String> path)
      throws DataProviderException {

    for (final String propertyName : edmType.getPropertyNames()) {
      final EdmProperty edmProperty = (EdmProperty) edmType.getProperty(propertyName);

      // Ignore key properties, they are set automatically
      if (!keyPredicateNames.contains(propertyName)) {
        final Property property = getProperty(properties, propertyName);

        // Check if all "not nullable" properties are set
        if (!edmProperty.isNullable()) {
          if ((property != null && property.isNull()) // Update,insert; Property is explicit set to null
              || (isInsert && property == null) // Insert; Property not provided
              || (!isInsert && !isPatch && property == null)) { // Insert(Put); Property not provided
            throw new DataProviderException("Property " + propertyName + " must not be null",
                HttpStatusCode.BAD_REQUEST);
          }
        }

        // Validate property value
        validatePropertyValue(property, edmProperty, edmBindingTarget, path);
      }
    }
  }

  private void validatePropertyValue(final Property property, final EdmProperty edmProperty,
      final EdmBindingTarget edmBindingTarget, final List<String> path) throws DataProviderException {

    final ArrayList<String> newPath = new ArrayList<String>(path);
    newPath.add(edmProperty.getName());

    if (edmProperty.isCollection()) {
      if (edmProperty.getType() instanceof EdmComplexType && property != null) {
        for (final Object value : property.asCollection()) {
          validateComplexValue((ComplexValue) value,
              edmBindingTarget,
              (EdmComplexType) edmProperty.getType(),
              newPath);
        }
      }
    } else if (edmProperty.getType() instanceof EdmComplexType) {
      validateComplexValue((property == null) ? null : property.asComplex(),
          edmBindingTarget,
          (EdmComplexType) edmProperty.getType(),
          newPath);
    }
  }

  private void validateComplexValue(final ComplexValue value, final EdmBindingTarget edmBindingTarget,
      final EdmComplexType edmType, final List<String> path) throws DataProviderException {
    // The whole complex property can be nullable but nested primitive, navigation properties can be not nullable
    final List<Property> properties = (value == null) ? new ArrayList<Property>() : value.getValue();

    validateProperties(properties, edmBindingTarget, edmType, new ArrayList<String>(0), path);
    validateNavigationProperties(value, edmBindingTarget, edmType, path);
  }

  private Property getProperty(final List<Property> properties, final String name) {
    for (final Property property : properties) {
      if (property.getName().equals(name)) {
        return property;
      }
    }

    return null;
  }

  private static enum ValidationResult {
    FOUND,
    NOT_FOUND,
    EMPTY
  }
}
