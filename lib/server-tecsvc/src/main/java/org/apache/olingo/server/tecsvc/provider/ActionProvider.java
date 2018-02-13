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

  public static final FullQualifiedName nameBA_RTETTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BA_RTETTwoKeyNav");

  public static final FullQualifiedName nameBAESAllPrimRT =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAESAllPrimRT");

  public static final FullQualifiedName nameBAETAllPrimRT =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETAllPrimRT");
  
  public static final FullQualifiedName nameBAETTwoPrimRTString =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoPrimRTString");
    
  public static final FullQualifiedName nameBAETTwoPrimRTCollString =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoPrimRTCollString");
  
  public static final FullQualifiedName nameBAETTwoPrimRTCTAllPrim =
          new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoPrimRTCTAllPrim");
  
  public static final FullQualifiedName nameBAETTwoPrimRTCollCTAllPrim =
          new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoPrimRTCollCTAllPrim");
  
  public static final FullQualifiedName nameBAETCompAllPrimRTETCompAllPrim =
          new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETCompAllPrimRTETCompAllPrim");
  
  public static final FullQualifiedName nameBAETTwoKeyNavRTETTwoKeyNavParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETTwoKeyNavRTETTwoKeyNavParam");
  
  public static final FullQualifiedName nameBAETBaseETTwoBaseRTETTwoBase =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETBaseETTwoBaseRTETTwoBase");
  
  public static final FullQualifiedName nameBAETMixPrimCollCompRTCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETMixPrimCollCompRTCTTwoPrim");
  
  public static final FullQualifiedName nameBAETMixPrimCollCompCTTwoPrimRTCTTwoPrim = 
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETMixPrimCollCompCTTwoPrimRTCTTwoPrim");
  
  public static final FullQualifiedName nameBAETMixPrimCollCompCTTWOPrimCompRTCTTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETMixPrimCollCompCTTWOPrimCompRTCTTwoPrim");
  
  public static final FullQualifiedName nameBAETMixPrimCollCompCTTWOPrimCompRTCollCTTwoPrim = 
      new FullQualifiedName(SchemaProvider.NAMESPACE, "BAETMixPrimCollCompCTTWOPrimCompRTCollCTTwoPrim");
  
  public static final FullQualifiedName 
  nameBAETTwoKeyNavCTBasePrimCompNavCTTwoBasePrimCompNavRTCTTwoBasePrimCompNav = 
      new FullQualifiedName(SchemaProvider.NAMESPACE, 
          "BAETTwoKeyNavCTBasePrimCompNavCTTwoBasePrimCompNavRTCTTwoBasePrimCompNav");
  // Unknown Actions
  public static final FullQualifiedName name_A_RTTimeOfDay_ =
     new FullQualifiedName(SchemaProvider.NAMESPACE, "_A_RTTimeOfDay_");
  
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
  public static final FullQualifiedName nameUARTByteNineParam =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "UARTByteNineParam");
  
  public static List<CsdlAction> getBoundActionsForEntityType(FullQualifiedName entityType) throws ODataException {
    FullQualifiedName[] actionNames = {nameBAESAllPrimRTETAllPrim, 
        nameBAESTwoKeyNavRTESTwoKeyNav, nameBAESTwoKeyNavRTESKeyNav, nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav,
        nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav, nameBA_RTETTwoKeyNav,nameBAESAllPrimRT, 
        nameBAETAllPrimRT, nameBAETTwoPrimRTString, nameBAETTwoPrimRTCollString, nameBAETTwoPrimRTCTAllPrim,
        nameBAETTwoPrimRTCollCTAllPrim, nameBAETCompAllPrimRTETCompAllPrim, nameBAETTwoKeyNavRTETTwoKeyNavParam, 
        nameBAETBaseETTwoBaseRTETTwoBase, nameBAETMixPrimCollCompRTCTTwoPrim, name_A_RTTimeOfDay_ };
    
    List<CsdlAction> actions = new ArrayList<CsdlAction>();
    for (FullQualifiedName fqn:actionNames) {
      List<CsdlAction> entityActions = getActions(fqn);
      for (CsdlAction action:entityActions) {
        CsdlParameter parameter = action.getParameters().get(0);
        if (parameter.getTypeFQN().equals(entityType)) {
          actions.add(action);
        }
      }
    }
    return actions;
  }

  public static List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException {
      
    if(actionName.equals(name_A_RTTimeOfDay_)){
        return  Arrays.asList(
          new CsdlAction().setName(name_A_RTTimeOfDay_.getName())
              .setParameters(Arrays.asList(
                new CsdlParameter().setName("ParameterTimeOfDay").setType(PropertyProvider.nameTimeOfDay)
                      .setNullable(false),
                new CsdlParameter().setName("ParameterAny").setType(PropertyProvider.nameString)
                      .setNullable(false)))
              .setBound(false)
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)),
                
          new CsdlAction().setName(name_A_RTTimeOfDay_.getName())
              .setParameters(Arrays.asList(
                new CsdlParameter().setName("ParameterTimeOfDay").setType(PropertyProvider.nameTimeOfDay)
                      .setNullable(false),
                new CsdlParameter().setName("ParameterAny").setType(PropertyProvider.nameString)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)),

          new CsdlAction().setName(name_A_RTTimeOfDay_.getName())
              .setParameters(Arrays.asList(
                new CsdlParameter().setName("ParameterString").setType(PropertyProvider.nameString)
                      .setNullable(false),    
                new CsdlParameter().setName("ParameterTimeOfDay").setType(PropertyProvider.nameTimeOfDay)
                      .setNullable(false),
                new CsdlParameter().setName("ParameterAny").setType(PropertyProvider.nameString)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameTimeOfDay)));
        
    }else if (actionName.equals(nameUARTString)) {
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

    } else if (actionName.equals(nameUARTByteNineParam)) {
      return Collections.singletonList(
          new CsdlAction()
              .setName(nameUARTByteNineParam.getName())
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
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameByte)));

    } else if (actionName.equals(nameBA_RTETTwoKeyNav)) {
      return Arrays.asList(
          new CsdlAction().setName("BA_RTETTwoKeyNav")
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav)),

          new CsdlAction().setName("BA_RTETTwoKeyNav")
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETKeyNav").setType(EntityTypeProvider.nameETKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav)));

    } else if (actionName.equals(nameBAESAllPrimRTETAllPrim)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAESAllPrimRTETAllPrim.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterESAllPrim").setType(EntityTypeProvider.nameETAllPrim)
                      .setCollection(true).setNullable(false)))
              .setBound(true)
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETAllPrim)));

    } else if (actionName.equals(nameBAESTwoKeyNavRTESTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAESTwoKeyNavRTESTwoKeyNav.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true).setNullable(false)))
              .setBound(true)
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav).setCollection(true)));

    } else if (actionName.equals(nameBAESTwoKeyNavRTESKeyNav)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAESTwoKeyNavRTESKeyNav.getName())
              .setBound(true)
              .setEntitySetPath("BindingParam/NavPropertyETKeyNavMany")
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoKeyNav")
                      .setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setCollection(true)
                      .setNullable(false)))
              .setReturnType(
                  new CsdlReturnType().setType(EntityTypeProvider.nameETKeyNav).setCollection(true)));

    } else if (actionName.equals(nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoKeyNav").setType(EntityTypeProvider.nameETBaseTwoKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav)));

    } else if (actionName.equals(nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav.getName())
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoBaseTwoKeyNav")
                      .setType(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
                      .setNullable(false)))
              .setBound(true)
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETBaseTwoKeyNav)));

    } else if (actionName.equals(nameBAETAllPrimRT)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAETAllPrimRT.getName())
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETAllPrim")
                      .setNullable(false)
                      .setType(EntityTypeProvider.nameETAllPrim))));

    } else if (actionName.equals(nameBAESAllPrimRT)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAESAllPrimRT.getName())
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETAllPrim")
                      .setNullable(false)
                      .setCollection(true)
                      .setType(EntityTypeProvider.nameETAllPrim))));
    
    }else if(actionName.equals(nameBAETTwoPrimRTString)){
        return Collections.singletonList(
          new CsdlAction().setName(nameBAETTwoPrimRTString.getName())
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoPrim").setType(EntityTypeProvider.nameETTwoPrim)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType().setType(PropertyProvider.nameString)));
        
    }else if(actionName.equals(nameBAETTwoPrimRTCollString)){
        return Collections.singletonList(
          new CsdlAction().setName(nameBAETTwoPrimRTCollString.getName())
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoPrim").setType(EntityTypeProvider.nameETTwoPrim)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType()
                      .setType(PropertyProvider.nameString)
                      .setCollection(true)));
        
    }else if(actionName.equals(nameBAETTwoPrimRTCTAllPrim)){
        return Collections.singletonList(
          new CsdlAction().setName(nameBAETTwoPrimRTCTAllPrim.getName())
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoPrim").setType(EntityTypeProvider.nameETTwoPrim)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType()
                      .setType(ComplexTypeProvider.nameCTAllPrim)));
        
    }else if(actionName.equals(nameBAETTwoPrimRTCollCTAllPrim)){
        return Collections.singletonList(
          new CsdlAction().setName(nameBAETTwoPrimRTCollCTAllPrim.getName())
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETTwoPrim").setType(EntityTypeProvider.nameETTwoPrim)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType()
                      .setType(ComplexTypeProvider.nameCTAllPrim)
                      .setCollection(true)));
        
    }else if(actionName.equals(nameBAETCompAllPrimRTETCompAllPrim)){
        return Collections.singletonList(
          new CsdlAction().setName(nameBAETCompAllPrimRTETCompAllPrim.getName())
              .setBound(true)
              .setParameters(Collections.singletonList(
                  new CsdlParameter().setName("ParameterETCompAllPrim").setType(EntityTypeProvider.nameETCompAllPrim)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType()
                      .setType(EntityTypeProvider.nameETCompAllPrim)));
                  
    }else if(actionName.equals(nameBAETTwoKeyNavRTETTwoKeyNavParam)){
         return Collections.singletonList(
          new CsdlAction().setName(nameBAETTwoKeyNavRTETTwoKeyNavParam.getName())
              .setBound(true)
              .setEntitySetPath("BindingParam/NavPropertyETTwoKeyNavOne")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false),
                  new CsdlParameter().setName("PropertyComp").setType(ComplexTypeProvider.nameCTPrimComp)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETTwoKeyNav)));
    } else if (actionName.equals(nameBAETBaseETTwoBaseRTETTwoBase)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAETBaseETTwoBaseRTETTwoBase.getName())
              .setBound(true)
              .setEntitySetPath("BindingParam/olingo.odata.test1.ETTwoBase")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETBase)
                      .setNullable(false),
                  new CsdlParameter().setName("PropertyString").setType(PropertyProvider.nameString)
                      .setNullable(false),
                  new CsdlParameter().setName("AdditionalPropertyString_5").setType(PropertyProvider.nameString)
                      .setNullable(false),
                  new CsdlParameter().setName("AdditionalPropertyString_6").setType(PropertyProvider.nameString)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType().setType(EntityTypeProvider.nameETTwoBase)));
    } else if (actionName.equals(nameBAETMixPrimCollCompRTCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAETMixPrimCollCompRTCTTwoPrim.getName())
              .setBound(true)
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETMixPrimCollComp)
                      .setNullable(false),
                  new CsdlParameter().setName("CollPropertyComp").setType(ComplexTypeProvider.nameCTTwoPrim)
                      .setNullable(false).setCollection(true)))
              .setReturnType(new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)));
    } else if (actionName.equals(nameBAETMixPrimCollCompCTTWOPrimCompRTCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAETMixPrimCollCompCTTWOPrimCompRTCTTwoPrim.getName())
              .setBound(true)
              .setEntitySetPath("BindingParam")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETMixPrimCollComp)
                      .setNullable(false),
                  new CsdlParameter().setName("PropertyComp").setType(ComplexTypeProvider.nameCTTwoPrim)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim)));
    } else if (actionName.equals(nameBAETMixPrimCollCompCTTWOPrimCompRTCollCTTwoPrim)) {
      return Collections.singletonList(
          new CsdlAction().setName(nameBAETMixPrimCollCompCTTWOPrimCompRTCollCTTwoPrim.getName())
              .setBound(true)
              .setEntitySetPath("BindingParam/CollPropertyComp")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETMixPrimCollComp)
                      .setNullable(false).setCollection(true),
                  new CsdlParameter().setName("PropertyComp").setType(ComplexTypeProvider.nameCTTwoPrim)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoPrim).setCollection(true)));
    } else if (actionName.equals(nameBAETTwoKeyNavCTBasePrimCompNavCTTwoBasePrimCompNavRTCTTwoBasePrimCompNav)) {
      return Collections.singletonList(
          new CsdlAction().setName(
              nameBAETTwoKeyNavCTBasePrimCompNavCTTwoBasePrimCompNavRTCTTwoBasePrimCompNav.getName())
              .setBound(true)
              .setEntitySetPath("BindingParam/PropertyCompNav/olingo.odata.test1.CTTwoBasePrimCompNav")
              .setParameters(Arrays.asList(
                  new CsdlParameter().setName("BindingParam").setType(EntityTypeProvider.nameETTwoKeyNav)
                      .setNullable(false)))
              .setReturnType(new CsdlReturnType().setType(ComplexTypeProvider.nameCTTwoBasePrimCompNav)));
    }
    return null;
  }
}
