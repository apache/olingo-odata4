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
package org.apache.olingo.server.core.testutil.techprovider;

import java.util.Arrays;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.Target;
import org.apache.olingo.server.api.edm.provider.ActionImport;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.FunctionImport;
import org.apache.olingo.server.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.server.api.edm.provider.Singleton;

public class ContainerProvider {

  public static final FullQualifiedName nameContainer = new FullQualifiedName(SchemaProvider.nameSpace, "Container");

  EntityContainerInfo entityContainerInfoTest1 =
      new EntityContainerInfo().setContainerName(nameContainer);

  public EntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {
    if (entityContainerName == null) {
      return entityContainerInfoTest1;
    } else if (entityContainerName.equals(nameContainer)) {
      return entityContainerInfoTest1;
    }

    return null;
  }

  public EntityContainer getEntityContainer() throws ODataException {
    return null;
  }

  public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String name) throws ODataException {
    if (entityContainer == nameContainer) {
      if (name.equals("ESAllPrim")) {
        return new EntitySet()
            .setName("ESAllPrim")
            .setType(EntityTypeProvider.nameETAllPrim);

      } else if (name.equals("ESCollAllPrim")) {
        return new EntitySet()
            .setName("ESCollAllPrim")
            .setType(EntityTypeProvider.nameETCollAllPrim);

      } else if (name.equals("ESTwoPrim")) {
        return new EntitySet()
            .setName("ESTwoPrim")
            .setType(EntityTypeProvider.nameETTwoPrim);

      } else if (name.equals("ESMixPrimCollComp")) {
        return new EntitySet()
            .setName("ESMixPrimCollComp")
            .setType(EntityTypeProvider.nameETMixPrimCollComp);

      } else if (name.equals("ESBase")) {
        return new EntitySet()
            .setName("ESBase")
            .setType(EntityTypeProvider.nameETBase);

      } else if (name.equals("ESTwoBase")) {
        return new EntitySet()
            .setName("ESTwoBase")
            .setType(EntityTypeProvider.nameETTwoBase);

      } else if (name.equals("ESTwoKeyTwoPrim")) {
        return new EntitySet()
            .setName("ESTwoKeyTwoPrim")
            .setType(EntityTypeProvider.nameETTwoKeyTwoPrim);

      } else if (name.equals("ESBaseTwoKeyTwoPrim")) {
        return new EntitySet()
            .setName("ESBaseTwoKeyTwoPrim")
            .setType(EntityTypeProvider.nameETBaseTwoKeyTwoPrim);

      } else if (name.equals("ESTwoBaseTwoKeyTwoPrim")) {
        return new EntitySet()
            .setName("ESTwoBaseTwoKeyTwoPrim")
            .setType(EntityTypeProvider.nameETTwoBaseTwoKeyTwoPrim);

      } else if (name.equals("ESAllKey")) {
        return new EntitySet()
            .setName("ESAllKey")
            .setType(EntityTypeProvider.nameETAllKey);

      } else if (name.equals("ESCompAllPrim")) {
        return new EntitySet()
            .setName("ESCompAllPrim")
            .setType(EntityTypeProvider.nameETCompAllPrim);

      } else if (name.equals("ESCompCollAllPrim")) {
        return new EntitySet()
            .setName("ESCompCollAllPrim")
            .setType(EntityTypeProvider.nameETCompCollAllPrim);

      } else if (name.equals("ESCompComp")) {
        return new EntitySet()
            .setName("ESCompComp")
            .setType(EntityTypeProvider.nameETCompComp);

      } else if (name.equals("ESCompCollComp")) {
        return new EntitySet()
            .setName("ESCompCollComp")
            .setType(EntityTypeProvider.nameETCompCollComp);

      } else if (name.equals("ESMedia")) {
        return new EntitySet()
            .setName("ESMedia")
            .setType(EntityTypeProvider.nameETMedia)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("ESKeyTwoKeyComp")) {
        return new EntitySet()
            .setName("ESKeyTwoKeyComp")
            .setType(EntityTypeProvider.nameETKeyTwoKeyComp);

      } else if (name.equals("ESInvisible")) {
        return new EntitySet()
            .setName("ESInvisible")
            .setType(EntityTypeProvider.nameETAllPrim);

      } else if (name.equals("ESServerSidePaging")) {
        return new EntitySet()
            .setName("ESServerSidePaging")
            .setType(EntityTypeProvider.nameETServerSidePaging);

      } else if (name.equals("ESAllNullable")) {
        return new EntitySet()
            .setName("ESAllNullable")
            .setType(EntityTypeProvider.nameETAllNullable);

      } else if (name.equals("ESKeyNav")) {
        return new EntitySet()
            .setName("ESKeyNav")
            .setType(EntityTypeProvider.nameETKeyNav);

      } else if (name.equals("ESTwoKeyNav")) {
        return new EntitySet()
            .setName("ESTwoKeyNav")
            .setType(EntityTypeProvider.nameETTwoKeyNav);

      } else if (name.equals("ESBaseTwoKeyNav")) {
        return new EntitySet()
            .setName("ESBaseTwoKeyNav")
            .setType(EntityTypeProvider.nameETBaseTwoKeyNav);

      } else if (name.equals("ESCompMixPrimCollComp")) {
        return new EntitySet()
            .setName("ESCompMixPrimCollComp")
            .setType(EntityTypeProvider.nameETCompMixPrimCollComp);

      } else if (name.equals("ESFourKeyAlias")) {
        return new EntitySet()
            .setName("ESFourKeyAlias")
            .setType(EntityTypeProvider.nameETFourKeyAlias);
      }
    }

    return null;
  }

  public ActionImport getActionImport(final FullQualifiedName entityContainer, final String name) throws ODataException
  {
    if (entityContainer.equals(nameContainer)) {
      if (name.equals("AIRTPrimParam")) {
        return new ActionImport()
            .setName("AIRTPrimParam")
            .setAction(ActionProvider.nameUARTPrimParam);

      } else if (name.equals("AIRTPrimCollParam")) {
        return new ActionImport()
            .setName("AIRTPrimCollParam")
            .setAction(ActionProvider.nameUARTPrimCollParam);

      } else if (name.equals("AIRTCompParam")) {
        return new ActionImport()
            .setName("AIRTCompParam")
            .setAction(ActionProvider.nameUARTCompParam);

      } else if (name.equals("AIRTCompCollParam")) {
        return new ActionImport()
            .setName("AIRTCompCollParam")
            .setAction(ActionProvider.nameUARTCompCollParam);

      } else if (name.equals("AIRTETParam")) {
        return new ActionImport()
            .setName("AIRTETParam")
            .setAction(ActionProvider.nameUARTETParam);

      } else if (name.equals("AIRTETCollAllPrimParam")) {
        return new ActionImport()
            .setName("AIRTETCollAllPrimParam")
            .setAction(ActionProvider.nameUARTETCollAllPrimParam);
      }
    }

    return null;
  }

  public FunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String name)
      throws ODataException {

    if (entityContainer.equals(nameContainer)) {
      if (name.equals("FINRTInt16")) {
        return new FunctionImport()
            .setName("FINRTInt16")
            .setFunction(FunctionProvider.nameUFNRTInt16)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FINInvisibleRTInt16")) {
        return new FunctionImport()
            .setName("FINInvisibleRTInt16")
            .setFunction(FunctionProvider.nameUFNRTInt16);

      } else if (name.equals("FINInvisible2RTInt16")) {
        return new FunctionImport()
            .setName("FINInvisible2RTInt16")
            .setFunction(FunctionProvider.nameUFNRTInt16);

      } else if (name.equals("FICRTETKeyNav")) {
        return new FunctionImport()
            .setName("FICRTETKeyNav")
            .setFunction(FunctionProvider.nameUFCRTETKeyNav);

      } else if (name.equals("FICRTETTwoKeyNavParam")) {
        return new FunctionImport()
            .setName("FICRTETTwoKeyNavParam")
            .setFunction(FunctionProvider.nameUFCRTETTwoKeyNavParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTStringTwoParam")) {
        return new FunctionImport()
            .setName("FICRTStringTwoParam")
            .setFunction(FunctionProvider.nameUFCRTStringTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollStringTwoParam")) {
        return new FunctionImport()
            .setName("FICRTCollStringTwoParam")
            .setFunction(FunctionProvider.nameUFCRTCollStringTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTAllPrimTwoParam")) {
        return new FunctionImport()
            .setName("FICRTCTAllPrimTwoParam")
            .setFunction(FunctionProvider.nameUFCRTCTAllPrimTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTESMixPrimCollCompTwoParam")) {
        return new FunctionImport()
            .setName("FICRTESMixPrimCollCompTwoParam")
            .setFunction(FunctionProvider.nameUFCRTESMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FINRTESMixPrimCollCompTwoParam")) {
        return new FunctionImport()
            .setName("FINRTESMixPrimCollCompTwoParam")
            .setFunction(FunctionProvider.nameUFNRTESMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollCTTwoPrim")) {
        return new FunctionImport()
            .setName("FICRTCollCTTwoPrim")
            .setFunction(FunctionProvider.nameUFCRTCollCTTwoPrim)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTETMedia")) {
        return new FunctionImport()
            .setName("FICRTETMedia")
            .setFunction(FunctionProvider.nameUFCRTETMedia)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTTwoPrimParam")) {
        return new FunctionImport()
            .setName("FICRTCTTwoPrimParam")
            .setFunction(FunctionProvider.nameUFCRTCTTwoPrimParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTTwoPrim")) {
        return new FunctionImport()
            .setName("FICRTCTTwoPrim")
            .setFunction(FunctionProvider.nameUFCRTCTTwoPrim)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollString")) {
        return new FunctionImport()
            .setName("FICRTCollString")
            .setFunction(FunctionProvider.nameUFCRTCollString)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTString")) {
        return new FunctionImport()
            .setName("FICRTString")
            .setFunction(FunctionProvider.nameUFCRTString)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTESTwoKeyNavParam")) {
        return new FunctionImport()
            .setName("FICRTESTwoKeyNavParam")
            .setFunction(FunctionProvider.nameUFCRTESTwoKeyNavParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollCTTwoPrimParam")) {
        return new FunctionImport()
            .setName("FICRTCollCTTwoPrimParam")
            .setFunction(FunctionProvider.nameUFCRTCollCTTwoPrimParam)
            .setIncludeInServiceDocument(true);

      }
    }

    return null;
  }

  public Singleton getSingleton(final FullQualifiedName entityContainer, final String name) throws ODataException {
    if (entityContainer.equals(nameContainer)) {

      if (name.equals("SI")) {
        return new Singleton()
            .setName("SI")
            .setType(EntityTypeProvider.nameETTwoPrim);

      } else if (name.equals("SINav")) {
        return new Singleton()
            .setName("SINav")
            .setType(EntityTypeProvider.nameETTwoKeyNav)
            .setNavigationPropertyBindings(Arrays.asList(
                new NavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavMany")
                    .setTarget(new Target().setTargetName("ESTwoKeyNav"))));

      } else if (name.equals("SIMedia")) {
        return new Singleton()
            .setName("SIMedia")
            .setType(EntityTypeProvider.nameETMedia);
      }
    }
    return null;
  }
}
