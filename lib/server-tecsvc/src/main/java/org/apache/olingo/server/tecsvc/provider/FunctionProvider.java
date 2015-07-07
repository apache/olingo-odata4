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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;

public class FunctionProvider {

  // Bound Functions
  public static final FullQualifiedName nameBFCCollCTPrimCompRTESAllPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCCollCTPrimCompRTESAllPrim");

  public static final FullQualifiedName nameBFCCollStringRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCCollStringRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCCTPrimCompRTESBaseTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCCTPrimCompRTESBaseTwoKeyNav");

  public static final FullQualifiedName nameBFCCTPrimCompRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCCTPrimCompRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCCTPrimCompRTESTwoKeyNavParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCCTPrimCompRTESTwoKeyNavParam");

  public static final FullQualifiedName nameBFCCTPrimCompRTETTwoKeyNavParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCCTPrimCompRTETTwoKeyNavParam");

  public static final FullQualifiedName nameBFCESAllPrimRTCTAllPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESAllPrimRTCTAllPrim");

  public static final FullQualifiedName nameBFCESBaseTwoKeyNavRTESBaseTwoKey =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESBaseTwoKeyNavRTESBaseTwoKey");

  public static final FullQualifiedName nameBFCESKeyNavRTETKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESKeyNavRTETKeyNav");

  public static final FullQualifiedName nameBFCESKeyNavRTETKeyNavParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESKeyNavRTETKeyNavParam");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCollCTTwoPrim");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollString =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCollString");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCTTwoPrim");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTString =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTString");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTStringParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTStringParam");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTTwoKeyNav");

  public static final FullQualifiedName nameBFCETBaseTwoKeyNavRTESBaseTwoKey =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCETBaseTwoKeyNavRTESBaseTwoKey");

  public static final FullQualifiedName nameBFCETBaseTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCETBaseTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCETBaseTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCETBaseTwoKeyNavRTETTwoKeyNav");

  public static final FullQualifiedName nameBFCETKeyNavRTETKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCETKeyNavRTETKeyNav");

  public static final FullQualifiedName nameBFCETTwoKeyNavRTCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCETTwoKeyNavRTCTTwoPrim");

  public static final FullQualifiedName nameBFCETTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCETTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCETTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCETTwoKeyNavRTETTwoKeyNav");

  public static final FullQualifiedName nameBFCSINavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCSINavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCStringRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCStringRTESTwoKeyNav");

  public static final FullQualifiedName nameBFESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFESTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCTNavFiveProp = new FullQualifiedName(
      SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCTNavFiveProp");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollCTNavFiveProp = new FullQualifiedName(
      SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCollCTNavFiveProp");

  public static final FullQualifiedName nameBFCESKeyNavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESKeyNavRTESTwoKeyNav");

  // Unbound Functions
  public static final FullQualifiedName nameUFCRTCollCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCollCTTwoPrim");
  public static final FullQualifiedName nameUFCRTCollCTTwoPrimTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCollCTTwoPrimTwoParam");
  public static final FullQualifiedName nameUFCRTCollString = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UFCRTCollString");
  public static final FullQualifiedName nameUFCRTCollStringTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCollStringTwoParam");
  public static final FullQualifiedName nameUFCRTCTAllPrimTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCTAllPrimTwoParam");
  public static final FullQualifiedName nameUFCRTCTTwoPrim = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UFCRTCTTwoPrim");
  public static final FullQualifiedName nameUFCRTCTTwoPrimTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCTTwoPrimTwoParam");
  public static final FullQualifiedName nameUFCRTESMixPrimCollCompTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTESMixPrimCollCompTwoParam");
  public static final FullQualifiedName nameUFCRTCollETTwoKeyNavParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCollETTwoKeyNavParam");
  public static final FullQualifiedName nameUFCRTETAllPrimTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTETAllPrimTwoParam");
  public static final FullQualifiedName nameUFCRTETKeyNav = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UFCRTETKeyNav");
  public static final FullQualifiedName nameUFCRTETMedia = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UFCRTETMedia");
  public static final FullQualifiedName nameUFCRTCollETMedia = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UFCRTCollETMedia");

  public static final FullQualifiedName nameUFCRTETTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTETTwoKeyNav");
  public static final FullQualifiedName nameUFCRTETTwoKeyNavParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTETTwoKeyNavParam");

  public static final FullQualifiedName nameUFCRTETTwoKeyNavParamCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTETTwoKeyNavParamCTTwoPrim");

  public static final FullQualifiedName nameUFCRTString =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTString");

  public static final FullQualifiedName nameUFCRTStringTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTStringTwoParam");

  public static final FullQualifiedName nameUFCRTCollETMixPrimCollCompTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCollETMixPrimCollCompTwoParam");

  public static final FullQualifiedName nameUFCRTCollETKeyNavContParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCollETKeyNavContParam");

  public static final FullQualifiedName nameUFNRTInt16 =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFNRTInt16");

  public static final FullQualifiedName nameUFNRTCollCTNavFiveProp = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "UFNRTCollCTNavFiveProp");

  public static final FullQualifiedName nameUFNRTCollETMixPrimCollCompTwoParam = new FullQualifiedName(
      SchemaProvider.NAMESPACE, "UFNRTCollETMixPrimCollCompTwoParam");

  public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException {

    if (functionName.equals(nameUFNRTInt16)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameInt16)));

    } else if (functionName.equals(nameUFCRTETKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTETKeyNav")
              .setParameters(new ArrayList<CsdlParameter>())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTETTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)));
    } else if (functionName.equals(nameUFCRTETTwoKeyNavParam)) {
      return Arrays
          .asList(
          new CsdlFunction()
              .setName("UFCRTETTwoKeyNavParam")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)
              )
          );

    } else if (functionName.equals(nameUFCRTETTwoKeyNavParamCTTwoPrim)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTETTwoKeyNavParamCTTwoPrim")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterCTTwoPrim").setType(ComplexTypeProvider.nameCTTwoPrim)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)
              )
          );

    } else if (functionName.equals(nameUFCRTStringTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTStringTwoParam")
              .setParameters(Arrays.asList(
                  new CsdlParameter()
                      .setName("ParameterInt16")
                      .setType(PropertyProvider.nameInt16)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(true)),
          new CsdlFunction()
              .setName("UFCRTStringTwoParam")
              .setParameters(Arrays.asList(
                  new CsdlParameter()
                      .setName("ParameterString")
                      .setType(PropertyProvider.nameString)
                      .setNullable(false),
                  new CsdlParameter()
                      .setName("ParameterInt16")
                      .setType(PropertyProvider.nameInt16)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(true))

          );

    } else if (functionName.equals(nameUFCRTCollETTwoKeyNavParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter()
                      .setName("ParameterInt16")
                      .setType(PropertyProvider.nameInt16)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameUFCRTCollETKeyNavContParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTCollETKeyNavContParam")
              .setBound(true)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterInt16")
                      .setNullable(false)
                      .setType(PropertyProvider.nameInt16)))
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNavCont)
                  .setCollection(true)
                  .setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTString)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTString")

              .setComposable(true)
              .setParameters(new ArrayList<CsdlParameter>())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(false)
              )
          );

    } else if (functionName.equals(nameUFCRTCollStringTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTCollStringTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString).setNullable(
                          false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(
                          false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setCollection(true).setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTCollString)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTCollString")
              .setParameters(new ArrayList<CsdlParameter>())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setCollection(true).setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTCTAllPrimTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTCTAllPrimTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString).setNullable(
                          false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(
                          false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTAllPrim).setNullable(false))
          );

    } else if (functionName.equals(nameUFNRTCollETMixPrimCollCompTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFNRTCollETMixPrimCollCompTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString).setNullable(
                          false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(
                          false)))
              .setComposable(false)
              .setBound(false)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTMixPrimCollComp)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameUFCRTCTTwoPrimTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTCTTwoPrimTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(
                          false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString).setNullable(
                          false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false))
          );
    } else if (functionName.equals(nameUFCRTCollCTTwoPrimTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTCollCTTwoPrimTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(
                          false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString).setNullable(
                          true)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)
                      .setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTCTTwoPrim)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTCTTwoPrim")
              .setParameters(new ArrayList<CsdlParameter>())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTCollCTTwoPrim)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTCollCTTwoPrim")
              .setComposable(true)
              .setParameters(new ArrayList<CsdlParameter>())
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)
                      .setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTETMedia)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTETMedia")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterInt16").setNullable(false).setType(PropertyProvider.nameInt16)
                  ))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETMedia).setNullable(false))
          );
    } else if (functionName.equals(nameUFCRTCollETMedia)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETMedia).setCollection(true).setNullable(false)));

    } else if (functionName.equals(nameUFCRTCollETMixPrimCollCompTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString).setNullable(
                          false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(
                          false)))
              .setComposable(false)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETMixPrimCollComp).setCollection(true)
                      .setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTETAllPrimTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFCRTETAllPrimTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString).setNullable(
                          false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(
                          false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim).setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTESMixPrimCollCompTwoParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString).setNullable(
                          false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(
                          false)
                      ))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETMixPrimCollComp).setCollection(true)
                      .setNullable(false))
          );

    } else if (functionName.equals(nameUFNRTCollCTNavFiveProp)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("UFNRTCollCTNavFiveProp")
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTNavFiveProp).setCollection(true))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays
          .asList(
              new CsdlFunction()
                  .setName("BFCESTwoKeyNavRTESTwoKeyNav")
                  .setEntitySetPath("BindingParam/NavPropertyETTwoKeyNavMany")
                  .setBound(true)
                  .setParameters(
                      Arrays.asList(
                          new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                              .setCollection(true).setNullable(false)))
                  .setComposable(true)
                  .setReturnType(
                      new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                          .setNullable(false)),

              new CsdlFunction()
                  .setName("BFCESTwoKeyNavRTESTwoKeyNav")
                  .setBound(true)
                  .setParameters(
                      Arrays.asList(
                          new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                              .setCollection(true).setNullable(false),
                          new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                              .setCollection(false).setNullable(false)))
                  .setComposable(true)
                  .setReturnType(
                      new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                          .setNullable(false)),
              new CsdlFunction()
                  .setName("BFCESTwoKeyNavRTESTwoKeyNav")
                  .setBound(true)
                  .setParameters(
                      Arrays.asList(
                          new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav)
                              .setCollection(true).setNullable(false)))
                  .setComposable(true)
                  .setReturnType(
                      new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                          .setNullable(false))
          // new Function()
          // .setName("BFCESTwoKeyNavRTESTwoKeyNav")
          // .setBound(true)
          // .setParameters(
          // Arrays.asList(new Parameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav)
          // .setCollection(true).setNullable(false),
          // new Parameter().setName("ParameterString").setType(PropertyProvider.nameString)
          // .setCollection(false).setNullable(false)))
          // .setComposable(true)
          // .setReturnType(
          // new ReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
          // .setNullable(false))
          );

    } else if (functionName.equals(nameBFCStringRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction().setName("BFCStringRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(PropertyProvider.nameString).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCETBaseTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)
              )
          );

    } else if (functionName.equals(nameBFCESBaseTwoKeyNavRTESBaseTwoKey)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESBaseTwoKeyNavRTESBaseTwoKey")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav).setCollection(true)
                      .setNullable(false))
          );

    } else if (functionName.equals(nameBFCESAllPrimRTCTAllPrim)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESAllPrimRTCTAllPrim")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETAllPrim)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTAllPrim).setNullable(false))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCTTwoPrim)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCTTwoPrim")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollCTTwoPrim)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCollCTTwoPrim")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)
                      .setNullable(false))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTString)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTString")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(false))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollString)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCollString")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setCollection(true).setNullable(false))
          );

    } else if (functionName.equals(nameBFCETTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCETTwoKeyNavRTESTwoKeyNav")
              .setEntitySetPath("BindingParam/NavPropertyETTwoKeyNavOne")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCETBaseTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameBFCSINavRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCSINavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setNullable(
                              false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTESBaseTwoKey)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCETBaseTwoKeyNavRTESBaseTwoKey")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameBFCCollStringRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCCollStringRTESTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(PropertyProvider.nameString).setCollection(
                          true)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameBFCCTPrimCompRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCCTPrimCompRTESTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                          .setNullable(
                              false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameBFCCTPrimCompRTESBaseTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCCTPrimCompRTESBaseTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                          .setNullable(
                              false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav).setCollection(true).setNullable(
                      false))
          );

    } else if (functionName.equals(nameBFCCollCTPrimCompRTESAllPrim)) {
      return Arrays
          .asList(
          new CsdlFunction()
              .setName("BFCCollCTPrimCompRTESAllPrim")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim).setCollection(true).setNullable(false))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false))
          );

    } else if (functionName.equals(nameBFCESKeyNavRTETKeyNav)) {
      return Arrays
          .asList(
          new CsdlFunction()
              .setName("BFCESKeyNavRTETKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav)
                          .setCollection(
                              true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setNullable(false))
          );

    } else if (functionName.equals(nameBFCETKeyNavRTETKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCETKeyNavRTETKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav).setNullable(
                          false)))
              .setComposable(true)
              .setEntitySetPath("BindingParam/NavPropertyETKeyNavOne")
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setNullable(false))
          );
    } else if (functionName.equals(nameBFESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))

          );
    } else if (functionName.equals(nameBFCESKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESKeyNavRTESTwoKeyNav")
              .setEntitySetPath("BindingParam/NavPropertyETTwoKeyNavMany")
              .setBound(true)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam")
                      .setNullable(false)
                      .setType(EntityTypeProvider.nameETKeyNav)
                      .setCollection(true),
                  new CsdlParameter().setName("ParameterString")
                      .setNullable(false)
                      .setType(PropertyProvider.nameString)))
              .setReturnType(new CsdlReturnType()
                  .setNullable(false)
                  .setType(EntityTypeProvider.nameETTwoKeyNav)
                  .setCollection(true))
          );

    } else if (functionName.equals(nameBFCETTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCETTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setNullable(
                              false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false))
          );

    } else if (functionName.equals(nameBFCETTwoKeyNavRTCTTwoPrim)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCETTwoKeyNavRTCTTwoPrim")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setNullable(
                              false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTCTNavFiveProp)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCTNavFiveProp")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTNavFiveProp).setNullable(false))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollCTNavFiveProp)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCollCTNavFiveProp")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTNavFiveProp).setCollection(true)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTStringParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTStringParam")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setCollection(true).setNullable(false),
                      new CsdlParameter().setName("ParameterComp").setType(ComplexTypeProvider.nameCTTwoPrim)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(false))
          );

    } else if (functionName.equals(nameBFCESKeyNavRTETKeyNavParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCESKeyNavRTETKeyNavParam")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav)
                          .setCollection(
                              true).setNullable(false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setNullable(false))
          );
    } else if (functionName.equals(nameBFCCTPrimCompRTETTwoKeyNavParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCCTPrimCompRTETTwoKeyNavParam")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                          .setNullable(
                              false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(new CsdlReturnType()
                  .setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)
              )
          );
    } else if (functionName.equals(nameBFCCTPrimCompRTESTwoKeyNavParam)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCCTPrimCompRTESTwoKeyNavParam")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                          .setNullable(
                              false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true).setNullable(
                      false))
          );
    }

    return null;
  }

}
