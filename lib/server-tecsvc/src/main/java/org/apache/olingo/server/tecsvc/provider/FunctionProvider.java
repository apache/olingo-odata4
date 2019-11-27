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

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.ex.ODataException;

public class FunctionProvider {

  // Bound Functions
  
  
  public static final FullQualifiedName nameBFCColCTAllPrimRTESAllPrim =
    new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCColCTAllPrimRTESAllPrim");

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

  public static final FullQualifiedName nameBFNESAllPrimRTCTAllPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFNESAllPrimRTCTAllPrim");

  public static final FullQualifiedName nameBFCESBaseTwoKeyNavRTESBaseTwoKey =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESBaseTwoKeyNavRTESBaseTwoKey");

  public static final FullQualifiedName nameBFCESKeyNavRTETKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESKeyNavRTETKeyNav");

  public static final FullQualifiedName nameBFCESKeyNavRTETKeyNavParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESKeyNavRTETKeyNavParam");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCollCTTwoPrim");
  
 public static final FullQualifiedName nameBFNESTwoKeyNavRTString =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFNESTwoKeyNavRTString");
 
  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollString =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCollString");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCTTwoPrim");

  public static final FullQualifiedName nameBFC_RTESTwoKeyNav_ =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFC_RTESTwoKeyNav_");

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
  
  public static final FullQualifiedName nameBFESBaseRTESTwoBase =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFESBaseRTESTwoBase");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCTNavFiveProp = new FullQualifiedName(
      SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCTNavFiveProp");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollCTNavFiveProp = new FullQualifiedName(
      SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCollCTNavFiveProp");

  public static final FullQualifiedName nameBFCESKeyNavRTESTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESKeyNavRTESTwoKeyNav");
  
  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollDecimal =
          new FullQualifiedName(SchemaProvider.NAMESPACE, "BFCESTwoKeyNavRTCollDecimal");
  
  // Unknown
   public static final FullQualifiedName name_FC_RTTimeOfDay_ =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "_FC_RTTimeOfDay_");

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

  public static final FullQualifiedName nameUFNRTCollCTNavFiveProp =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFNRTCollCTNavFiveProp");

  public static final FullQualifiedName nameUFNRTCollETMixPrimCollCompTwoParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFNRTCollETMixPrimCollCompTwoParam");

  public static final FullQualifiedName nameUFNRTByteNineParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFNRTByteNineParam");
  
   public static final FullQualifiedName nameUFCRTCollDecimal =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTCollDecimal");
  
   public static final FullQualifiedName nameUFCRTDecimal =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UFCRTDecimal");
  
  public static List<CsdlFunction> getBoundFunctionsForType(FullQualifiedName entityType) throws ODataException {
    FullQualifiedName[] funcNames = {
        nameBFCColCTAllPrimRTESAllPrim,
        nameBFCCollCTPrimCompRTESAllPrim, 
        nameBFCCollStringRTESTwoKeyNav,
        nameBFCCTPrimCompRTESBaseTwoKeyNav,
        nameBFCCTPrimCompRTESTwoKeyNav,
        nameBFCCTPrimCompRTESTwoKeyNavParam,
        nameBFCCTPrimCompRTETTwoKeyNavParam,
        nameBFNESAllPrimRTCTAllPrim,
        nameBFCESBaseTwoKeyNavRTESBaseTwoKey,
        nameBFCESKeyNavRTETKeyNav,
        nameBFCESKeyNavRTETKeyNavParam,
        nameBFCESTwoKeyNavRTCollCTTwoPrim,
        nameBFCESTwoKeyNavRTCollString,
        nameBFCESTwoKeyNavRTCTTwoPrim,
        nameBFC_RTESTwoKeyNav_,
        nameBFCESTwoKeyNavRTString,
        nameBFCESTwoKeyNavRTStringParam,
        nameBFCESTwoKeyNavRTTwoKeyNav,
        nameBFCETBaseTwoKeyNavRTESBaseTwoKey,
        nameBFCETBaseTwoKeyNavRTESTwoKeyNav,
        nameBFCETBaseTwoKeyNavRTETTwoKeyNav,
        nameBFCETKeyNavRTETKeyNav,
        nameBFCETTwoKeyNavRTCTTwoPrim,
        nameBFCETTwoKeyNavRTESTwoKeyNav,
        nameBFCETTwoKeyNavRTETTwoKeyNav,
        nameBFCSINavRTESTwoKeyNav,
        nameBFCStringRTESTwoKeyNav,
        nameBFESTwoKeyNavRTESTwoKeyNav,
        nameBFCESTwoKeyNavRTCTNavFiveProp,
        nameBFCESTwoKeyNavRTCollCTNavFiveProp,
        nameBFCESKeyNavRTESTwoKeyNav,
        nameBFCESTwoKeyNavRTCollDecimal,
        nameBFNESTwoKeyNavRTString,
        name_FC_RTTimeOfDay_
    };
    
    List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
    for (FullQualifiedName fqn:funcNames) {
      List<CsdlFunction> entityFuncs = getFunctions(fqn);
      for (CsdlFunction func:entityFuncs) {
        CsdlParameter parameter = func.getParameters().get(0);
        if (parameter.getTypeFQN().equals(entityType)) {
          functions.add(func);
        }
      }
    }
    return functions;
  }
  
  public static List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException {
      
      if(functionName.equals(name_FC_RTTimeOfDay_)){
        List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
       
            functions.add(new CsdlFunction()
            .setName(name_FC_RTTimeOfDay_.getName())
            .setParameters(Collections.singletonList(
                new CsdlParameter()
                    .setName("ParameterTimeOfDay")
                    .setType(PropertyProvider.nameTimeOfDay)
                    .setNullable(false)))
            .setComposable(true)
            .setBound(true)
            .setReturnType(
                new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)));

           functions.add( new CsdlFunction()
            .setName(name_FC_RTTimeOfDay_.getName())
            .setParameters(Arrays.asList(
                new CsdlParameter()
                    .setName("ParameterTimeOfDay")
                    .setType(PropertyProvider.nameTimeOfDay)
                    .setNullable(false),
                 new CsdlParameter()
                    .setName("ParameterAny")
                    .setType(PropertyProvider.nameString)
                    .setNullable(false)))
            .setComposable(true)
            .setBound(true)
            .setReturnType(
                new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)));

           functions.add( new CsdlFunction()
            .setName(name_FC_RTTimeOfDay_.getName())
            .setParameters(Arrays.asList(
                new CsdlParameter()
                    .setName("ParameterTimeOfDay")
                    .setType(PropertyProvider.nameTimeOfDay)
                    .setNullable(false),
                 new CsdlParameter()
                    .setName("ParameterString")
                    .setType(PropertyProvider.nameString)
                    .setNullable(false),
                  new CsdlParameter()
                    .setName("ParameterAny")
                    .setType(PropertyProvider.nameInt32)
                    .setNullable(false)))
            .setComposable(true)
            .setBound(true)
            .setReturnType(
                new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)));
        
           functions.add(new CsdlFunction()
            .setName(name_FC_RTTimeOfDay_.getName())
            .setParameters(Collections.singletonList(
                new CsdlParameter()
                    .setName("ParameterTimeOfDay")
                    .setType(PropertyProvider.nameTimeOfDay)
                    .setNullable(false)))
            .setComposable(true)
            .setBound(false)
            .setReturnType(
                new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)));

           functions.add( new CsdlFunction()
            .setName(name_FC_RTTimeOfDay_.getName())
            .setParameters(Arrays.asList(
                new CsdlParameter()
                    .setName("ParameterTimeOfDay")
                    .setType(PropertyProvider.nameTimeOfDay)
                    .setNullable(false),
                 new CsdlParameter()
                    .setName("ParameterAny")
                    .setType(PropertyProvider.nameString)
                    .setNullable(false)))
            .setComposable(true)
            .setBound(false)
            .setReturnType(
                new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)));

           functions.add( new CsdlFunction()
            .setName(name_FC_RTTimeOfDay_.getName())
            .setParameters(Arrays.asList(
                new CsdlParameter()
                    .setName("ParameterTimeOfDay")
                    .setType(PropertyProvider.nameTimeOfDay)
                    .setNullable(false),
                 new CsdlParameter()
                    .setName("ParameterString")
                    .setType(PropertyProvider.nameString)
                    .setNullable(false),
                  new CsdlParameter()
                    .setName("ParameterAny")
                    .setType(PropertyProvider.nameInt32)
                    .setNullable(false)))
            .setComposable(true)
            .setBound(false)
            .setReturnType(
                new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)));
           
        return functions;
        
    }else if(functionName.equals(nameUFCRTCollDecimal)){
          return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(new CsdlReturnType()
                      .setType(PropertyProvider.nameDecimal)
                      .setPrecision(12)
                      .setScale(5)
                      .setCollection(true)));
          
    }else if(functionName.equals(nameUFCRTDecimal)){
        return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(new CsdlReturnType()
                      .setType(PropertyProvider.nameDecimal)
                      .setPrecision(12)
                      .setScale(5)));
          
    }else if (functionName.equals(nameUFNRTInt16)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameInt16)));

    } else if (functionName.equals(nameUFCRTETKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTETKeyNav")
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setNullable(false)));

    } else if (functionName.equals(nameUFCRTETTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)));
    } else if (functionName.equals(nameUFCRTETTwoKeyNavParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTETTwoKeyNavParam")
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16).setNullable(false)))
              .setComposable(true)
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false))
          );

    } else if (functionName.equals(nameUFCRTETTwoKeyNavParamCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTETTwoKeyNavParamCTTwoPrim")
              .setParameters(Collections.singletonList(
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
              .setParameters(Collections.singletonList(
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
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(true)));

    } else if (functionName.equals(nameUFCRTCollETTwoKeyNavParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter()
                      .setName("ParameterInt16")
                      .setType(PropertyProvider.nameInt16)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameUFCRTCollETKeyNavContParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTCollETKeyNavContParam")
              .setComposable(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterInt16")
                      .setNullable(false)
                      .setType(PropertyProvider.nameInt16)))
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNavCont)
                  .setCollection(true)
                  .setNullable(false)));

    } else if (functionName.equals(nameUFCRTString)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTString")
              .setComposable(true)
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(false)));

    } else if (functionName.equals(nameUFCRTCollStringTwoParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTCollStringTwoParam")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                      .setNullable(false),
                  new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setCollection(true).setNullable(false)));

    } else if (functionName.equals(nameUFCRTCollString)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTCollString")
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setCollection(true).setNullable(false)));

    } else if (functionName.equals(nameUFCRTCTAllPrimTwoParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTCTAllPrimTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTAllPrim).setNullable(false)));

    } else if (functionName.equals(nameUFNRTCollETMixPrimCollCompTwoParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFNRTCollETMixPrimCollCompTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)
                          .setNullable(false)))
              .setComposable(false)
              .setBound(false)
              .setReturnType(
                    new CsdlReturnType().setType(EntityTypeProvider.nameETMixPrimCollComp)
                       .setNullable(false)
                       .setCollection(true)));

    } else if (functionName.equals(nameUFNRTByteNineParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("ParameterEnum").setType(EnumTypeProvider.nameENString),
                  new CsdlParameter().setName("ParameterDef").setType(TypeDefinitionProvider.nameTDString),
                  new CsdlParameter().setName("ParameterComp").setType(ComplexTypeProvider.nameCTTwoPrim),
                  new CsdlParameter().setName("ParameterETTwoPrim").setType(EntityTypeProvider.nameETTwoPrim),
                  new CsdlParameter().setName("CollParameterByte").setType(PropertyProvider.nameByte)
                      .setCollection(true),
                  new CsdlParameter().setName("CollParameterEnum").setType(EnumTypeProvider.nameENString)
                      .setCollection(true),
                  new CsdlParameter().setName("CollParameterDef").setType(TypeDefinitionProvider.nameTDString)
                      .setCollection(true),
                  new CsdlParameter().setName("CollParameterComp").setType(ComplexTypeProvider.nameCTTwoPrim)
                      .setCollection(true),
                  new CsdlParameter().setName("CollParameterETTwoPrim").setType(EntityTypeProvider.nameETTwoPrim)
                      .setCollection(true)))
              .setComposable(false)
              .setBound(false)
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameByte)));

    } else if (functionName.equals(nameUFCRTCTTwoPrimTwoParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTCTTwoPrimTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)
                          .setNullable(false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false)));

    } else if (functionName.equals(nameUFCRTCollCTTwoPrimTwoParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTCollCTTwoPrimTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)
                          .setNullable(false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(true)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameUFCRTCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTCTTwoPrim")
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false)));

    } else if (functionName.equals(nameUFCRTCollCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTCollCTTwoPrim")
              .setComposable(true)
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameUFCRTETMedia)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTETMedia")
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterInt16").setNullable(false)
                      .setType(PropertyProvider.nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETMedia).setNullable(false)));

    } else if (functionName.equals(nameUFCRTCollETMedia)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(Collections.<CsdlParameter> emptyList())
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETMedia).setCollection(true).setNullable(false)));

    } else if (functionName.equals(nameUFCRTCollETMixPrimCollCompTwoParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName(functionName.getName())
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)
                          .setNullable(false)))
              .setComposable(true)
              .setBound(false)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETMixPrimCollComp).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameUFCRTETAllPrimTwoParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFCRTETAllPrimTwoParam")
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false),
                      new CsdlParameter().setName("ParameterInt16").setType(PropertyProvider.nameInt16)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim).setNullable(false)));

    } else if (functionName.equals(nameUFNRTCollCTNavFiveProp)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("UFNRTCollCTNavFiveProp")
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTNavFiveProp).setCollection(true)));

    } else if (functionName.equals(nameBFC_RTESTwoKeyNav_)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName(nameBFC_RTESTwoKeyNav_.getName())
              .setEntitySetPath("BindingParam/NavPropertyETTwoKeyNavMany")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)),

          new CsdlFunction()
              .setName(nameBFC_RTESTwoKeyNav_.getName())
              .setBound(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false),
                  new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                      .setCollection(false).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)),

          new CsdlFunction()
              .setName(nameBFC_RTESTwoKeyNav_.getName())
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCStringRTESTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction().setName("BFCStringRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(PropertyProvider.nameString).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTETTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCETBaseTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)));

    } else if (functionName.equals(nameBFCESBaseTwoKeyNavRTESBaseTwoKey)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESBaseTwoKeyNavRTESBaseTwoKey")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFNESAllPrimRTCTAllPrim)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFNESAllPrimRTCTAllPrim")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETAllPrim)
                      .setCollection(true).setNullable(false)))
              .setComposable(false)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTAllPrim).setNullable(false)));

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCTTwoPrim")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false)));

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCollCTTwoPrim")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCESTwoKeyNavRTString)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTString")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(false)));
      
    } else if (functionName.equals(nameBFNESTwoKeyNavRTString)){
        return Collections.singletonList(
          new CsdlFunction()
              .setName("BFNESTwoKeyNavRTString")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(false)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(false)));

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollString)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCollString")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameString).setCollection(true).setNullable(false)));

    } else if (functionName.equals(nameBFCETTwoKeyNavRTESTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCETTwoKeyNavRTESTwoKeyNav")
              .setEntitySetPath("BindingParam/NavPropertyETTwoKeyNavOne")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTESTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCETBaseTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCSINavRTESTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCSINavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTESBaseTwoKey)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCETBaseTwoKeyNavRTESBaseTwoKey")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCCollStringRTESTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCCollStringRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(PropertyProvider.nameString).setCollection(true)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCCTPrimCompRTESTwoKeyNav)) {
      return Arrays.asList(
          new CsdlFunction()
              .setName("BFCCTPrimCompRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCCTPrimCompRTESBaseTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCCTPrimCompRTESBaseTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCColCTAllPrimRTESAllPrim)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCColCTAllPrimRTESAllPrim")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTAllPrim)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCCollCTPrimCompRTESAllPrim)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCCollCTPrimCompRTESAllPrim")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCESTwoKeyNavRTTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)));

    } else if (functionName.equals(nameBFCESKeyNavRTETKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESKeyNavRTETKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setNullable(false)));

    } else if (functionName.equals(nameBFCETKeyNavRTETKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCETKeyNavRTETKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setEntitySetPath("BindingParam/NavPropertyETKeyNavOne")
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setNullable(false)));

    } else if (functionName.equals(nameBFESTwoKeyNavRTESTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFESBaseRTESTwoBase)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFESBaseRTESTwoBase")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBase)
                  .setNullable(false)))
              .setComposable(true)
              .setEntitySetPath("BindingParam/olingo.odata.test1.ETTwoBase")
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoBase)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCESKeyNavRTESTwoKeyNav)) {
      return Collections.singletonList(
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
                  .setCollection(true)));

    } else if (functionName.equals(nameBFCETTwoKeyNavRTETTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCETTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)));
      
   } else if (functionName.equals(nameBFCESTwoKeyNavRTCollDecimal)){
        return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCollDecimal")
              .setBound(true)
              .setParameters(
                  Collections.singletonList(
                      new CsdlParameter().setName("BindingParam")
                          .setType(EntityTypeProvider.nameETTwoKeyNav)
                          .setNullable(false)
                          .setCollection(true)))
              .setComposable(true)
              .setBound(true)
              .setReturnType(
                  new CsdlReturnType().setType(PropertyProvider.nameDecimal)
                      .setPrecision(12)
                      .setScale(5)
                      .setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCETTwoKeyNavRTCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCETTwoKeyNavRTCTTwoPrim")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setNullable(false)));

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCTNavFiveProp)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCTNavFiveProp")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTNavFiveProp).setNullable(false)));

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollCTNavFiveProp)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESTwoKeyNavRTCollCTNavFiveProp")
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(ComplexTypeProvider.nameCTNavFiveProp).setCollection(true)
                      .setNullable(false)));

    } else if (functionName.equals(nameBFCESTwoKeyNavRTStringParam)) {
      return Collections.singletonList(
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
                  new CsdlReturnType().setType(PropertyProvider.nameString).setNullable(false)));

    } else if (functionName.equals(nameBFCESKeyNavRTETKeyNavParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCESKeyNavRTETKeyNavParam")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETKeyNav)
                          .setCollection(true).setNullable(false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setNullable(false)));

    } else if (functionName.equals(nameBFCCTPrimCompRTETTwoKeyNavParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCCTPrimCompRTETTwoKeyNavParam")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                          .setNullable(false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(new CsdlReturnType()
                  .setType(EntityTypeProvider.nameETTwoKeyNav).setNullable(false)));

    } else if (functionName.equals(nameBFCCTPrimCompRTESTwoKeyNavParam)) {
      return Collections.singletonList(
          new CsdlFunction()
              .setName("BFCCTPrimCompRTESTwoKeyNavParam")
              .setBound(true)
              .setParameters(
                  Arrays.asList(
                      new CsdlParameter().setName("BindingParam").setType(ComplexTypeProvider.nameCTPrimComp)
                          .setNullable(false),
                      new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                          .setNullable(false)))
              .setComposable(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)
                      .setNullable(false)));
    }

    return null;
  }

}
