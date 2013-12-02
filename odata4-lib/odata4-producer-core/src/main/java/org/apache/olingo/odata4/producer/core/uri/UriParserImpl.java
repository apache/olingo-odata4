/*******************************************************************************
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
 ******************************************************************************/

package org.apache.olingo.odata4.producer.core.uri;

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmActionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntitySet;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmSingleton;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltBatchContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltEntityCastContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltEntityContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMetadataContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltResourcePathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValueListContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValueOptListContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValuePairContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriEOFContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PathSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PathSegmentsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.QueryOptionsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ResourcePathContext;

public class UriParserImpl {
  private Edm edm = null;
  private EdmEntityContainer edmEntityContainer = null;

  public UriParserImpl(Edm edm) {
    this.edm = edm;
    this.edmEntityContainer = edm.getEntityContainer(null);
  }

  public UriInfoImpl ParseUri(String uri) throws UriParserException {
    OdataRelativeUriEOFContext root = ParserAdapter.parseInput(uri);
    return readODataRelativeUriEOF(root);
  }

  private UriInfoImpl readODataRelativeUriEOF(OdataRelativeUriEOFContext node) {
    OdataRelativeUriContext first = (OdataRelativeUriContext) node.getChild(0);
    return readODataRelativeUri(first);
  }

  private UriInfoImpl readODataRelativeUri(OdataRelativeUriContext node) {
    if (node instanceof AltBatchContext) {
      return new UriInfoImplBatch();
    } else if (node instanceof AltEntityContext) {
      // TODO read the entity options
      return new UriInfoImplEntity();
    } else if (node instanceof AltEntityCastContext) {
      // TODO read the entity options and the cast ns.odi
      return new UriInfoImplEntity();
    } else if (node instanceof AltMetadataContext) {
      // TODO read the metadata queryparameter and fragment
      return new UriInfoImplMetadata();
    } else if (node instanceof AltResourcePathContext) {
      return readAltResourcePath((AltResourcePathContext) node);
    }
    return null;
  }

  private UriInfoImpl readAltResourcePath(AltResourcePathContext node) {
    ResourcePathContext rpc = (ResourcePathContext) node.getChild(0);
    QueryOptionsContext qoc = (QueryOptionsContext) node.getChild(2); // is null if there are no options

    if (rpc.vPSs != null) {
      return readPathSegments(rpc.vPSs);
    } else if (rpc.vCJ != null) {
      return new UriInfoImplCrossjoin();
    } else if (rpc.vAll != null) {
      return new UriInfoImplAll();
    }

    return null;
  }

  private UriInfoImpl readPathSegments(PathSegmentsContext pathSegments) {
    int iSegment = 0;
    UriInfoImplPath infoImpl = new UriInfoImplPath();
    PathSegmentContext firstChild = (PathSegmentContext) pathSegments.vlPS.get(iSegment);
    UriPathInfoImpl firstPathInfo = readFirstPathSegment(infoImpl, firstChild);

    iSegment++;

    UriPathInfoImpl prevPathInfo = firstPathInfo;

    while (iSegment < pathSegments.vlPS.size()) {
      PathSegmentContext nextChild = (PathSegmentContext) pathSegments.vlPS.get(iSegment);
      prevPathInfo = readNextPathSegment(infoImpl, nextChild, prevPathInfo);
      iSegment++;
    }
    return infoImpl;
  }

  private UriPathInfoImpl readNextPathSegment(UriInfoImplPath infoImpl, PathSegmentContext pathSegment,
      UriPathInfoImpl prevPathInfo) {

    UriPathInfoImpl pathInfo = null;

    String odi = pathSegment.vODI.getText(); // not optional

    // check for properties
    if (pathSegment.vNS == null) {

      EdmType prevType = prevPathInfo.getType();
      if (prevType instanceof EdmStructuralType) {
        EdmStructuralType prevStructType = (EdmStructuralType) prevType;

        EdmElement element = prevStructType.getProperty(odi);
        if (element == null) {
          // TODO exception property not found
        }

        if (element instanceof EdmProperty) {
          prevPathInfo.addProperty((EdmProperty) element);
          return prevPathInfo;
        } else if (element instanceof EdmNavigationProperty) {

          prevPathInfo.addNavigationProperty((EdmNavigationProperty) element);

          UriPathInfoNavEntitySet pathInfoNav = new UriPathInfoNavEntitySet();
          pathInfoNav.addSourceNavigationProperty((EdmNavigationProperty) element);
          infoImpl.addPathInfo(pathInfoNav);
          return pathInfoNav;

        } else {

        }

      }

    } else {
      // check for namespace
      String namespace = pathSegment.vNS.getText();
      namespace = namespace.substring(0, namespace.length() - 1);

      FullQualifiedName fullName = new FullQualifiedName(namespace, odi);

      // check for typecasts
      if (prevPathInfo.getType() instanceof EdmEntityType) {
        EdmEntityType et = edm.getEntityType(fullName);
        if (et != null) {
          prevPathInfo.addTypeFilter(et);

          if (pathSegment.vlVPO.size() != 0) {
            UriKeyPredicateList keyPred = readKeyPredicateList(
                pathSegment.vlVPO.get(0), (EdmEntityType) prevPathInfo.getType());
            prevPathInfo.setKeyPredicates(keyPred);
          }

          return prevPathInfo;
        }
      } else if (prevPathInfo.getType() instanceof EdmComplexType) {
        EdmComplexType ct = edm.getComplexType(fullName);
        if (ct != null) {
          prevPathInfo.addTypeFilter(ct);
          return prevPathInfo;
        }
      }

      // check for bound action
      if (pathSegment.vlVPO == null) {
        EdmAction action = edm.getAction(fullName, prevPathInfo.getFullType(), false);
        pathInfo = new UriPathInfoActionImpl().setAction(action);
        infoImpl.addPathInfo(pathInfo);
        return pathInfo;
      } else {
        // check for bound functions
        UriParameterlist parameterList = readParameterList(pathSegment.vlVPO.get(0));

        EdmFunction function = edm.getFunction(fullName, prevPathInfo.getFullType(), false, parameterList.getNames());
        if (function != null) {

          UriPathInfoFunctionImpl pathInfoFunction = new UriPathInfoFunctionImpl();
          pathInfoFunction.setFunction(function);
          pathInfoFunction.setParameters(parameterList);

          if (pathSegment.vlVPO.size() > 1) {
            UriKeyPredicateList keyPred = readKeyPredicateList(
                pathSegment.vlVPO.get(1), (EdmEntityType) prevPathInfo.getType());
            pathInfoFunction.setKeyPredicates(keyPred);
          }

          infoImpl.addPathInfo(pathInfo);

          return pathInfo;
        }
      }

      // Exception unknown typeFilter/action or function
    }

    return null;
  }

  private UriPathInfoImpl readFirstPathSegment(UriInfoImplPath infoImpl, PathSegmentContext pathSegment) {
    UriPathInfoImpl pathInfo = null;

    // assert pathSegment.vNS = null;
    String odi = pathSegment.vODI.getText(); // not optional

    // EntitySet
    EdmEntitySet edmES = edmEntityContainer.getEntitySet(odi);
    if (edmES != null) {
      pathInfo = readEntitySet(pathSegment, edmES);
    }

    // Singleton
    EdmSingleton edmSI = edmEntityContainer.getSingleton(odi);
    if (edmSI != null) {
      pathInfo = readSingleton(pathSegment, edmSI);
    }

    // FunctionImport
    EdmFunctionImport edmFI = edmEntityContainer.getFunctionImport(odi);
    if (edmFI != null) {
      pathInfo = readFunctionImport(pathSegment, edmFI);
    }

    // ActionImport
    EdmActionImport edmAI = edmEntityContainer.getActionImport(odi);
    if (edmAI != null) {
      pathInfo = readActionImport(pathSegment, edmAI);
    }

    infoImpl.addPathInfo(pathInfo);

    return pathInfo;
  }

  private UriPathInfoImpl readActionImport(PathSegmentContext pathSegment, EdmActionImport edmFI) {
    UriPathInfoActionImpl uriPathInfo = new UriPathInfoActionImpl();

    EdmAction action = edmFI.getAction();
    uriPathInfo.setAction(action);

    int num = pathSegment.vlVPO.size();
    if (num == 2) {
      // TODO exception action parameters not allowed
    } else if (num == 1) {

      if (uriPathInfo.isCollection() == true) {
        if (uriPathInfo.getType() instanceof EdmEntityType) {
          uriPathInfo.setKeyPredicates(
              readKeyPredicateList(pathSegment.vlVPO.get(0), (EdmEntityType) uriPathInfo.getType()));
        } else {
          // TODO exception action keypreticates not allowed
        }
      } else {
        // TODO exception action parameters not allowed
      }
    }

    return uriPathInfo;
  }

  private UriPathInfoImpl readFunctionImport(PathSegmentContext pathSegment, EdmFunctionImport edmFI) {
    UriPathInfoFunctionImpl uriPathInfo = new UriPathInfoFunctionImpl();

    if (pathSegment.vlVPO == null) {
      // TODO exception function parameters missing
    }

    UriParameterlist parameterList = readParameterList(pathSegment.vlVPO.get(0));
    EdmFunction function = edmFI.getFunction(parameterList.getNames());
    uriPathInfo.setFunction(function);
    uriPathInfo.setParameters(parameterList);

    if (pathSegment.vlVPO.size() > 1) {

      if (!(uriPathInfo.getType() instanceof EdmEntityType)) {
        // TODO exception illegally used keypredicates on function impored returning not an entityset
      }
      uriPathInfo.setKeyPredicates(
          readKeyPredicateList(pathSegment.vlVPO.get(1), (EdmEntityType) uriPathInfo.getType()));
    }

    return null;
  }

  private UriPathInfoImpl readSingleton(PathSegmentContext pathSegment, EdmSingleton edmSI) {
    UriPathInfoSingletonImpl uriPathInfo = new UriPathInfoSingletonImpl();

    uriPathInfo.setSingleton(edmSI);
    return uriPathInfo;
  }

  private UriPathInfoImpl readEntitySet(PathSegmentContext pathSegment, EdmEntitySet edmES) {

    UriPathInfoEntitySetImpl uriPathInfo = new UriPathInfoEntitySetImpl();
    uriPathInfo.setEntitSet(edmES);

    // KeyPredicates
    if (pathSegment.vlVPO != null) {
      if (pathSegment.vlVPO.size() == 1) {
        uriPathInfo.setKeyPredicates(readKeyPredicateList(pathSegment.vlVPO.get(0), edmES.getEntityType()));

      } else if (pathSegment.vlVPO.size() > 1) {
        // TODO exception ( to much key predicates)
      }

    }
    return uriPathInfo;
  }

  private UriKeyPredicateList readKeyPredicateList(NameValueOptListContext parameterList, EdmEntityType entityType) {
    if (parameterList.vVO != null) {
      String value = parameterList.vVO.vV.getText();
      List<String> kp = entityType.getKeyPredicateNames();

      if (kp.size() != 1) {
        // TODO exception "for using a value only keyPredicate there must be exact ONE defined keyProperty
      }

      String keyName = kp.get(0); // there yhoul

      return new UriKeyPredicateList().add(keyName, value);
    }

    NameValueListContext vNVL = parameterList.vNVL;
    if (vNVL == null) {
      // TODO throw exception empty keypredicates not allowed
    }
    UriKeyPredicateList uriPrameterList1 = new UriKeyPredicateList();

    for (NameValuePairContext nvl : vNVL.vNVP) {
      String name = nvl.vODI.getText();
      String value = nvl.vVAL.getText();
      uriPrameterList1.add(name, value);
    }
    return uriPrameterList1;

  }

  private UriParameterlist readParameterList(NameValueOptListContext parameterList) {
    if (parameterList.vVO != null) {
      // TODO throw error "Value Only" not allowed for function/action parameters, only in keypredicates
      return null;
    }

    NameValueListContext vNVL = parameterList.vNVL;
    UriParameterlist uriPrameterList1 = new UriParameterlist();

    for (NameValuePairContext nvl : vNVL.vNVP) {
      String name = nvl.vODI.getText();
      if (nvl.vVAL != null) {
        String value = nvl.vVAL.getText();
        uriPrameterList1.add(name, value, null);
      } else {
        String alias = nvl.vALI.getText();
        uriPrameterList1.add(name, null, alias);
      }

    }
    return uriPrameterList1;

  }

}
