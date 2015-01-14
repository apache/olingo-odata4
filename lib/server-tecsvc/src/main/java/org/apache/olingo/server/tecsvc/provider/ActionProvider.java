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
package org.apache.olingo.server.tecsvc.provider;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.Parameter;
import org.apache.olingo.server.api.edm.provider.ReturnType;

public class ActionProvider {

  // Bound Actions
  public static final FullQualifiedName nameBAESAllPrimRTETAllPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAESAllPrimRTETAllPrim");

  public static final FullQualifiedName nameBAESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAESTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETBaseTwoKeyNavRTETBaseTwoKeyNav");

  public static final FullQualifiedName nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");

  public static final FullQualifiedName nameBAETTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoKeyNavRTETTwoKeyNav");

  // Unbound Actions
  public static final FullQualifiedName nameUARTString = new FullQualifiedName(SchemaProvider.NAMESPACE,
          "UARTString");
  public static final FullQualifiedName nameUARTCollStringTwoParam = new FullQualifiedName(SchemaProvider.NAMESPACE,
          "UARTCollStringTwoParam");
  public static final FullQualifiedName nameUARTCollCTTwoPrimParam = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UARTCollCTTwoPrimParam");
  public static final FullQualifiedName nameUARTCTTwoPrimParam = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UARTCTTwoPrimParam");
  public static final FullQualifiedName nameUARTETTwoKeyTwoPrimParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTETTwoKeyTwoPrimParam");
  public static final FullQualifiedName nameUARTCollETKeyNavParam =
          new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTCollETKeyNavParam");
  public static final FullQualifiedName nameUARTETAllPrimParam =
          new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTETAllPrimParam");
  public static final FullQualifiedName nameUARTCollETAllPrimParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTCollETAllPrimParam");


  public List<Action> getActions(final FullQualifiedName actionName) throws ODataException {
    if (actionName.equals(nameUARTString)) {
      return Arrays.asList(
              new Action().setName(nameUARTString.getName())
                          .setReturnType(new ReturnType().setType(PropertyProvider.nameString))
      );
    } else if (actionName.equals(nameUARTCollStringTwoParam)) {
        return Arrays.asList(
              new Action().setName(nameUARTCollStringTwoParam.getName())
                          .setParameters(Arrays.asList(
                                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
                          .setReturnType(new ReturnType().setType(PropertyProvider.nameString).setCollection(true))
              );

    } else if (actionName.equals(nameUARTCTTwoPrimParam)) {
      return Arrays.asList(
              new Action().setName(nameUARTCTTwoPrimParam.getName())
                          .setParameters(Arrays.asList(
                                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
                          .setReturnType(
                                  new ReturnType().setType(ComplexTypeProvider.nameCTTwoPrim))
      );

    } else if (actionName.equals(nameUARTCollCTTwoPrimParam)) {
      return Arrays.asList(
              new Action().setName(nameUARTCollCTTwoPrimParam.getName())
                          .setParameters(Arrays.asList(
                                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
                          .setReturnType(
                                  new ReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true))
      );

    } else if (actionName.equals(nameUARTETTwoKeyTwoPrimParam)) {
      return Arrays.asList(
              new Action().setName(nameUARTETTwoKeyTwoPrimParam.getName())
                          .setParameters(Arrays.asList(
                                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
                          .setReturnType(
                                  new ReturnType().setType(EntityTypeProvider.nameETTwoKeyTwoPrim))
      );

    } else if (actionName.equals(nameUARTCollETKeyNavParam)) {
      return Arrays.asList(
              new Action().setName(nameUARTCollETKeyNavParam.getName())
                          .setParameters(Arrays.asList(
                                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
                          .setReturnType(
                                  new ReturnType().setType(EntityTypeProvider.nameETKeyNav).setCollection(true))
          );

    } else if (actionName.equals(nameUARTETAllPrimParam)) {
      return Arrays.asList(
              new Action().setName(nameUARTETAllPrimParam.getName())
                          .setParameters(Arrays.asList(
                              new Parameter().setName("ParameterDate").setType(PropertyProvider.nameDate)))
                          .setReturnType(
                              new ReturnType().setType(EntityTypeProvider.nameETAllPrim))
          );

    } else if (actionName.equals(nameUARTCollETAllPrimParam)) {
      return Arrays.asList(
              new Action().setName(nameUARTCollETAllPrimParam.getName())
                          .setParameters(Arrays.asList(
                                  new Parameter().setName("ParameterTimeOfDay").setType(PropertyProvider.nameInt16)))
                          .setReturnType(
                                  new ReturnType().setType(EntityTypeProvider.nameETAllPrim).setCollection(true))
      );

    } else if (actionName.equals(nameBAETTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new Action().setName("BAETTwoKeyNavRTETTwoKeyNav")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(EntityTypeProvider.nameETTwoKeyNav))
          ,
          new Action().setName("BAETTwoKeyNavRTETTwoKeyNav")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterETKeyNav").setType(EntityTypeProvider.nameETKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(EntityTypeProvider.nameETTwoKeyNav))
          );

    } else if (actionName.equals(nameBAESAllPrimRTETAllPrim)) {
      return Arrays.asList(
          new Action().setName("BAESAllPrimRTETAllPrim")
              .setParameters(
                  Arrays.asList(
                      new Parameter().setName("ParameterESAllPrim").setType(EntityTypeProvider.nameETAllPrim)
                          .setCollection(true).setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(EntityTypeProvider.nameETAllPrim))
          );

    } else if (actionName.equals(nameBAESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Action().setName("BAESTwoKeyNavRTESTwoKeyNav")
              .setParameters(
                  Arrays.asList(
                      new Parameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true))
          );

    } else if (actionName.equals(nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav)) {
      return Arrays.asList(
          new Action().setName("BAETBaseTwoKeyNavRTETBaseTwoKeyNav")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(EntityTypeProvider.nameETTwoKeyNav))
          );

    } else if (actionName.equals(nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav)) {
      return Arrays.asList(
          new Action().setName("BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav")
              .setParameters(
                  Arrays.asList(
                      new Parameter().setName("ParameterETTwoBaseTwoKeyNav").setType(
                          EntityTypeProvider.nameETTwoBaseTwoKeyNav).setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav))
          );
    }

    return null;
  }
}
