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

import org.antlr.v4.runtime.tree.ParseTree;
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
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltAddContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltAliasContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltAndContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltBatchContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltComparismContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltEntityCastContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltEntityContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltEqualityContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltLiteralContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMemberContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMetadataContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMethodContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMultContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltOrContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltPharenthesisContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltResourcePathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltRootContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltUnaryContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CustomQueryOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FilterContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FormatContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.IdContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.InlinecountContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MemberExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValueListContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValueOptListContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValuePairContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriEOFContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OrderbyContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PathSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PathSegmentsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.QueryOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.QueryOptionsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ResourcePathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SearchContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SelectContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SkipContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SkiptokenContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SystemQueryOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TopContext;
import org.apache.olingo.odata4.producer.core.uri.expression.Alias;
import org.apache.olingo.odata4.producer.core.uri.expression.Binary;
import org.apache.olingo.odata4.producer.core.uri.expression.Expression;
import org.apache.olingo.odata4.producer.core.uri.expression.Literal;
import org.apache.olingo.odata4.producer.core.uri.expression.Member;
import org.apache.olingo.odata4.producer.core.uri.expression.MethodCall;
import org.apache.olingo.odata4.producer.core.uri.expression.SupportedBinaryOperators;
import org.apache.olingo.odata4.producer.core.uri.expression.SupportedMethodCalls;
import org.apache.olingo.odata4.producer.core.uri.expression.SupportedUnaryOperators;
import org.apache.olingo.odata4.producer.core.uri.expression.UnaryOperator;

public class UriParserImpl {
  private Edm edm = null;
  private EdmEntityContainer edmEntityContainer = null;
  private UriPathInfoImpl lastUriPathInfo;

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

    UriInfoImpl uriInfo = readODataRelativeUri(first);
    return uriInfo;
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
      UriInfoImplPath uriInfo = readPathSegments(rpc.vPSs, null);
      
      if (qoc != null) {
        readQueryParameter(uriInfo, qoc);
      }
      return uriInfo;
    } else if (rpc.vCJ != null) {
      return new UriInfoImplCrossjoin();
    } else if (rpc.vAll != null) {
      return new UriInfoImplAll();
    }

    return null;
  }

  private void readQueryParameter(UriInfoImplPath uriInfoImplPath, QueryOptionsContext qoc) {
    for (QueryOptionContext queryOption : qoc.qo) {
      readQueryOption(uriInfoImplPath, queryOption);
    }
  }

  private void readQueryOption(UriInfoImplPath uriInfoImplPath, QueryOptionContext queryOption) {
    ParseTree firstChild = queryOption.getChild(0);

    if (firstChild instanceof SystemQueryOptionContext) {
      readSystemQueryOption(uriInfoImplPath, firstChild);
    } else if (firstChild instanceof CustomQueryOptionContext) {
      // TODO read custom request option
    } else if (firstChild.getText().equals("@")) {
      // TODO read alias and value
    }

  }

  private void readSystemQueryOption(UriInfoImplPath uriInfoImplPath, ParseTree systemQueryOption) {
    ParseTree firstChild = systemQueryOption.getChild(0);
    if (firstChild instanceof ExpandContext) {
      // TODO implement
    } else if (firstChild instanceof FilterContext) {
      Expression expression = readFilterOption(firstChild);
      uriInfoImplPath.setSystemParameter(SystemQueryParameter.FILTER, expression);
      return;
    } else if (firstChild instanceof FormatContext) {
      // TODO implement
    } else if (firstChild instanceof IdContext) {
      // TODO implement
    } else if (firstChild instanceof InlinecountContext) {
      // TODO implement
    } else if (firstChild instanceof OrderbyContext) {
      // TODO implement
    } else if (firstChild instanceof SearchContext) {
      // TODO implement
    } else if (firstChild instanceof SelectContext) {
      // TODO implement
    } else if (firstChild instanceof SkipContext) {
      // TODO implement
    } else if (firstChild instanceof SkiptokenContext) {
      // TODO implement
    } else if (firstChild instanceof TopContext) {
      // TODO implement
    }
  }

  private Expression readFilterOption(ParseTree filter) {
    return readCommonExpression(filter.getChild(2));
  }

  private Expression readCommonExpression(ParseTree expressionContext) {
    // Expression ret = null;

    if (expressionContext instanceof AltPharenthesisContext) {
      return readCommonExpression(expressionContext.getChild(1));
    } else if (expressionContext instanceof AltMethodContext) {
      return readMethod(expressionContext);
    } else if (expressionContext instanceof AltUnaryContext) {
      UnaryOperator unary = new UnaryOperator();
      unary.setOperator(SupportedUnaryOperators.get(expressionContext.getChild(0).getText()));
      unary.setOperand(readCommonExpression(expressionContext.getChild(1)));
      return unary;
    } else if (expressionContext instanceof AltMemberContext) {
      return readMember(expressionContext);
    } else if (expressionContext instanceof AltMultContext) {
      return readBinary(expressionContext);
    } else if (expressionContext instanceof AltAddContext) {
      return readBinary(expressionContext);
    } else if (expressionContext instanceof AltComparismContext) {
      return readBinary(expressionContext);
    } else if (expressionContext instanceof AltEqualityContext) {
      return readBinary(expressionContext);
    } else if (expressionContext instanceof AltAndContext) {
      return readBinary(expressionContext);
    } else if (expressionContext instanceof AltOrContext) {
      return readBinary(expressionContext);
    } else if (expressionContext instanceof AltRootContext) {
      // TODO
    } else if (expressionContext instanceof AltAliasContext) {
      Alias alias = new Alias();
      alias.setReference(expressionContext.getChild(1).getText());
      // TODO collect all aliases and verify them afterwards
      return alias;
    } else if (expressionContext instanceof AltLiteralContext) {
      Literal literal = new Literal();
      literal.setText(expressionContext.getText());
      return literal;
    }
    return null;
  }

  private Expression readMember(ParseTree expressionContext) {
    MemberExprContext context = (MemberExprContext) expressionContext.getChild(0);

    Member member = new Member();
    
    UriPathInfoIT  pathInfoIT =  new UriPathInfoIT();
    

    if (context.ps!= null) {
      if (context.getChild(0).getText().startsWith("$it/")) {
        member.setIT(true); // TODO check if this is required 
        pathInfoIT.setIsExplicitIT(true);
      }
      UriParserImpl parser = new UriParserImpl(this.edm);
      
      UriInfoImplPath path = parser.readPathSegments(context.ps, 
          new UriPathInfoIT().setType(lastUriPathInfo.getType()));
      member.setPath(path);
    } else    {
      member.setIT(true);
    }
    return member;
      
  }

  private Expression readMethod(ParseTree expressionContext) {
    MethodCall expression = new MethodCall();
    expression.setMethod(SupportedMethodCalls.get(expressionContext.getChild(0).getText()));
    int i = 1;
    while (i < expressionContext.getChildCount()) {
      expression.addParameter(readCommonExpression(expressionContext.getChild(i)));
      i++;
    }
    return expression;
  }

  private Expression readBinary(ParseTree expressionContext) {
    Binary expression = new Binary();
    expression.setLeftOperand(readCommonExpression(expressionContext.getChild(0)));
    expression.setOperator(SupportedBinaryOperators.get(expressionContext.getChild(2).getText()));
    expression.setRightOperand(readCommonExpression(expressionContext.getChild(4)));
    return expression;
  }

  private UriInfoImplPath readPathSegments(PathSegmentsContext pathSegments, UriPathInfoImpl usePrevPathInfo) {
    
    UriPathInfoImpl prevPathInfo = usePrevPathInfo;
    UriInfoImplPath infoImpl = new UriInfoImplPath();
    
    int iSegment = 0;

    if (prevPathInfo == null) {
      PathSegmentContext firstChild = (PathSegmentContext) pathSegments.vlPS.get(iSegment);
      UriPathInfoImpl firstPathInfo = readFirstPathSegment(infoImpl, firstChild);
      iSegment++;
      prevPathInfo = firstPathInfo;
    } else {
      infoImpl.addPathInfo(prevPathInfo);
    }

    while (iSegment < pathSegments.vlPS.size()) {
      PathSegmentContext nextChild = (PathSegmentContext) pathSegments.vlPS.get(iSegment);
      prevPathInfo = readNextPathSegment(infoImpl, nextChild, prevPathInfo);
      iSegment++;
    }
    lastUriPathInfo = prevPathInfo;
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
