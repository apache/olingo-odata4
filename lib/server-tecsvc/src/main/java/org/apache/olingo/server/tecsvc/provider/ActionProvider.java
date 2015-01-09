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

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.Parameter;
import org.apache.olingo.server.api.edm.provider.ReturnType;

import java.util.Arrays;
import java.util.List;

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
  public static final FullQualifiedName nameUARTCompCollParam = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UARTCompCollParam");
  public static final FullQualifiedName nameUARTCompParam = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UARTCompParam");
  public static final FullQualifiedName nameUARTESParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTESParam");

  public static final FullQualifiedName nameUARTETParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTETParam");

  public static final FullQualifiedName nameUARTPrimParam = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UARTPrimParam");
  public static final FullQualifiedName nameUARTPrimCollParam = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UARTPrimCollParam");

  public List<Action> getActions(final FullQualifiedName actionName) throws ODataException {
    if (actionName.equals(nameUARTString)) {
      return Arrays.asList(
              new Action().setName(nameUARTString.getName())
                      .setReturnType(new ReturnType().setType(PropertyProvider.nameString))
      );
    } else if (actionName.equals(nameUARTPrimParam)) {
        return Arrays.asList(
          new Action().setName("UARTPrimParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))

              .setReturnType(new ReturnType().setType(PropertyProvider.nameString))
          );

    } else if (actionName.equals(nameUARTPrimCollParam)) {
      return Arrays.asList(
          new Action().setName("UARTPrimCollParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))

              .setReturnType(
                  new ReturnType().setType(PropertyProvider.nameString).setCollection(true))
          );

    } else if (actionName.equals(nameUARTCompParam)) {
      return Arrays.asList(
          new Action().setName("UARTCompParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))

              .setReturnType(
                  new ReturnType().setType(ComplexTypeProvider.nameCTTwoPrim))
          );

    } else if (actionName.equals(nameUARTCompCollParam)) {
      return Arrays.asList(
          new Action().setName("UARTCompCollParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))

              .setReturnType(
                  new ReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true))
          );

    } else if (actionName.equals(nameUARTETParam)) {
      return Arrays.asList(
          new Action().setName("UARTETParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
              .setReturnType(
                  new ReturnType().setType(EntityTypeProvider.nameETTwoKeyTwoPrim))
          );

    } else if (actionName.equals(nameUARTESParam)) {
      return Arrays.asList(
          new Action().setName("UARTESParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
              .setReturnType(
                  new ReturnType().setType(EntityTypeProvider.nameETKeyNav).setCollection(true))
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
