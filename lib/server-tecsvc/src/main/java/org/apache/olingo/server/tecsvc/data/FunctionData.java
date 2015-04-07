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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.tecsvc.data.DataProvider.DataProviderException;

public class FunctionData {

  protected static EntitySet entityCollectionFunction(final String name, final List<UriParameter> parameters,
      final Map<String, EntitySet> data) throws DataProviderException {
    if (name.equals("UFCRTCollETTwoKeyNavParam")) {
      final List<Entity> esTwoKeyNav = data.get("ESTwoKeyNav").getEntities();
      EntitySet result = new EntitySet();
      final int endIndex = parameters.isEmpty() ? 0 : Short.valueOf(parameters.get(0).getText());
      result.getEntities().addAll(
          esTwoKeyNav.subList(0,
              endIndex < 0 ? 0 : endIndex > esTwoKeyNav.size() ? esTwoKeyNav.size() : endIndex));
      return result;
    } else if (name.equals("UFCRTCollETMixPrimCollCompTwoParam")) {
      return data.get("ESMixPrimCollComp");
    } else if (name.equals("UFCRTCollETMedia")) {
      return data.get("ESMedia");
    } else {
      throw new DataProviderException("Function " + name + " is not yet implemented.");
    }
  }

  protected static Entity entityFunction(final String name, final List<UriParameter> parameters,
      final Map<String, EntitySet> data) throws DataProviderException {
    final List<Entity> esTwoKeyNav = data.get("ESTwoKeyNav").getEntities();
    if (name.equals("UFCRTETTwoKeyNav")) {
      return esTwoKeyNav.get(0);
    } else if (name.equals("UFCRTETTwoKeyNavParam")) {
      final int index = parameters.isEmpty() ? 0 : Short.valueOf(parameters.get(0).getText());
      return index < 0 || index >= esTwoKeyNav.size() ? null : esTwoKeyNav.get(index);
    } else if (name.equals("UFCRTETMedia")) {
      final int index = parameters.isEmpty() ? 1 : Short.valueOf(parameters.get(0).getText());
      final List<Entity> esMedia = data.get("ESMedia").getEntities();
      return index < 1 || index > esMedia.size() ? null : esMedia.get(index - 1);
    } else {
      throw new DataProviderException("Function " + name + " is not yet implemented.");
    }
  }

  @SuppressWarnings("unchecked")
  protected static Property primitiveComplexFunction(final String name, final List<UriParameter> parameters,
      final Map<String, EntitySet> data) throws DataProviderException {
    if (name.equals("UFNRTInt16")) {
      return DataCreator.createPrimitive(name, 12345);
    } else if (name.equals("UFCRTString")) {
      return DataCreator.createPrimitive(name, "UFCRTString string value");
    } else if (name.equals("UFCRTCollString")) {
      return data.get("ESCollAllPrim").getEntities().get(0).getProperty("CollPropertyString");
    } else if (name.equals("UFCRTCTTwoPrim")) {
      return DataCreator.createComplex(name,
          DataCreator.createPrimitive("PropertyInt16", 16),
          DataCreator.createPrimitive("PropertyString", "UFCRTCTTwoPrim string value"));
    } else if (name.equals("UFCRTCTTwoPrimParam")) {
      try {

        OData oData = OData.newInstance();
        return DataCreator.createComplex(name,
            DataCreator.createPrimitive("PropertyInt16", oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16)
                .valueOfString(getParameterText("ParameterInt16", parameters),
                    null, null, null, null, null, Short.class)),
            DataCreator.createPrimitive("PropertyString", oData
                .createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String)
                .valueOfString(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String)
                    .fromUriLiteral(getParameterText("ParameterString", parameters)),
                    null, null, null, null, null, String.class)));
      } catch (final EdmPrimitiveTypeException e) {
        throw new DataProviderException("Error in function " + name + ".", e);
      }
    } else if (name.equals("UFCRTCollCTTwoPrim")) {
      return DataCreator.createComplexCollection(name,
          Arrays.asList(DataCreator.createPrimitive("PropertyInt16", 16),
              DataCreator.createPrimitive("PropertyString", "Test123")),
          Arrays.asList(DataCreator.createPrimitive("PropertyInt16", 17),
              DataCreator.createPrimitive("PropertyString", "Test456")),
          Arrays.asList(DataCreator.createPrimitive("PropertyInt16", 18),
              DataCreator.createPrimitive("PropertyString", "Test678")));
    } else {
      throw new DataProviderException("Function " + name + " is not yet implemented.");
    }
  }

  private static String getParameterText(final String name, final List<UriParameter> parameters) {
    for (final UriParameter parameter : parameters) {
      if (parameter.getName().equals(name)) {
        return parameter.getText();
      }
    }
    return null;
  }
}
