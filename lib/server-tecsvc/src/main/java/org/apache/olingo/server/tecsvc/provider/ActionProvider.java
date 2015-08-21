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
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;

public class ActionProvider {

  // Bound Actions
  public static final FullQualifiedName nameBAESAllPrimRTETAllPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAESAllPrimRTETAllPrim");

  public static final FullQualifiedName nameBAESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAESTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBAESTwoKeyNavRTESKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAESTwoKeyNavRTESKeyNav");

  public static final FullQualifiedName nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETBaseTwoKeyNavRTETBaseTwoKeyNav");

  public static final FullQualifiedName nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");

  public static final FullQualifiedName nameBAETTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoKeyNavRTETTwoKeyNav");

  public static final FullQualifiedName nameBAESAllPrimRT =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAESAllPrimRT");

  public static final FullQualifiedName nameBAETAllPrimRT =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETAllPrimRT");

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
  public static final FullQualifiedName nameUART = new FullQualifiedName(SchemaProvider.NAMESPACE, "UART");
  public static final FullQualifiedName nameUARTParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTParam");
  public static final FullQualifiedName nameUARTTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTTwoParam");

  public List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException {
    if (actionName.equals(nameUARTString)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameUARTString.getName())
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameString)));

    } else if (actionName.equals(nameUARTCollStringTwoParam)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameUARTCollStringTwoParam.getName())
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16),
                  new CsdlParameter().setName("ParameterDuration").setType(PropertyProvider.nameDuration)))
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameString).setCollection(true)));

    } else if (actionName.equals(nameUARTCTTwoPrimParam)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameUARTCTTwoPrimParam.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)
                      .setNullable(false)))
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false)));

    } else if (actionName.equals(nameUARTCollCTTwoPrimParam)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameUARTCollCTTwoPrimParam.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)));

    } else if (actionName.equals(nameUARTETTwoKeyTwoPrimParam)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameUARTETTwoKeyTwoPrimParam.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyTwoPrim)));

    } else if (actionName.equals(nameUARTCollETKeyNavParam)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameUARTCollETKeyNavParam.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)))
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setCollection(true)));

    } else if (actionName.equals(nameUARTETAllPrimParam)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameUARTETAllPrimParam.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterDate").setType(PropertyProvider.nameDate)))
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim)));

    } else if (actionName.equals(nameUARTCollETAllPrimParam)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameUARTCollETAllPrimParam.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterTimeOfDay")
                      .setType(PropertyProvider.nameTimeOfDay)))
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim).setCollection(true)));

    } else if (actionName.equals(nameUART)) {
      return Collections.singletonList(new CsdlAction().setName(nameUART.getName()));

    } else if (actionName.equals(nameUARTParam)) {
      return Collections.singletonList(
          new CsdlAction()
              .setName(nameUARTParam.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16))));

    } else if (actionName.equals(nameUARTTwoParam)) {
      return Collections.singletonList(
          new CsdlAction()
              .setName(nameUARTTwoParam.getName())
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16),
                  new CsdlParameter().setName("ParameterDuration").setType(PropertyProvider.nameDuration))));

    } else if (actionName.equals(nameBAETTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new CsdlAction().setName("BAETTwoKeyNavRTETTwoKeyNav")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav))
          ,
          new CsdlAction().setName("BAETTwoKeyNavRTETTwoKeyNav")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterETKeyNav").setType(EntityTypeProvider.nameETKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav))
          );

    } else if (actionName.equals(nameBAESAllPrimRTETAllPrim)) {
      return Arrays.asList(
          new CsdlAction().setName("BAESAllPrimRTETAllPrim")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterESAllPrim").setType(EntityTypeProvider.nameETAllPrim)
                      .setCollection(true).setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim)));

    } else if (actionName.equals(nameBAESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlAction().setName("BAESTwoKeyNavRTESTwoKeyNav")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)));

    } else if (actionName.equals(nameBAESTwoKeyNavRTESKeyNav)) {
      return Arrays.asList(
          new CsdlAction().setName("BAESTwoKeyNavRTESKeyNav")
              .setBound(true)
              .setEntitySetPath("BindingParam/NavPropertyETKeyNavMany")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterETTwoKeyNav")
                      .setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true)
                      .setNullable(false)))
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setCollection(true)));

    } else if (actionName.equals(nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav)) {
      return Arrays.asList(
          new CsdlAction().setName("BAETBaseTwoKeyNavRTETBaseTwoKeyNav")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav)));

    } else if (actionName.equals(nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav)) {
      return Arrays.asList(
          new CsdlAction().setName("BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterETTwoBaseTwoKeyNav")
                      .setType(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav)));

    } else if (actionName.equals(nameBAETAllPrimRT)) {
      return Arrays.asList(
          new CsdlAction().setName("BAETAllPrimRT")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterETAllPrim")
                      .setNullable(false)
                      .setType(EntityTypeProvider.nameETAllPrim)
                  )));
    } else if (actionName.equals(nameBAESAllPrimRT)) {
      return Arrays.asList(
          new CsdlAction().setName("BAESAllPrimRT")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterETAllPrim")
                      .setNullable(false)
                      .setCollection(true)
                      .setType(EntityTypeProvider.nameETAllPrim)
                  ))
          );
    }

    return null;
  }
}
