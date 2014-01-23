/*******************************************************************************
 * 
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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
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
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriResourcePart;
import org.apache.olingo.odata4.producer.api.uri.UriResourcePartTyped;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedBinaryOperators;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedMethodCalls;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriLexer;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserBaseVisitor;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AliasAndValueContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AllExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltAddContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltAllContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltAndContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltAnyContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltBatchContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltComparismContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltEntityCastContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltEntityContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltEqualityContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltLiteralContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMetadataContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMultContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltOrContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltResourcePathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AnyExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CastExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CeilingMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ConcatMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ConstSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ContainsMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CrossjoinContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CustomQueryOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.DayMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.DistanceMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EndsWithMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EntityOptionCastContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EntityOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EntityOptionsCastContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EntityOptionsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandCountOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandItemContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandPathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandPathExtensionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandPathSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandRefOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FilterContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FloorMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FormatContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FractionalsecondsMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.GeoLengthMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.HourMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.IdContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.IndexOfMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.InlinecountContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.IntersectsMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.IsofExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.LengthMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MaxDateTimeMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MemberExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MinDateTimeMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MinuteMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MonthMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValueOptListContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValuePairContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NamespaceContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NowMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataIdentifierContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriEOFContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OrderByContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PathSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PathSegmentsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.QueryOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.QueryOptionsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ResourcePathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.RootExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.RoundMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SecondMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SelectContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SelectItemContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SelectSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.StartsWithMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SubstringMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TimeMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ToLowerMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ToUpperMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TotalOffsetMinutesMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TotalsecondsMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TrimMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.UnaryContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.YearMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.queryoption.AliasQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandItemImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandSegment;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.LevelOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.QueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SelectItemOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SystemQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.ExpressionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.LambdaRefImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.MemberImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.MethodCallImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.TypeLiteralImpl;

/**
 * UriVisitor
 * 
 * Converts the URI parse tree from the generate URI parser into an internal representation
 * which maybe (TODO) given to the application.
 * While converting the tree is validated against the EDM.
 * 
 * Attention:
 * <li> This UriVisitor is at some point more lax than the original ABNF:
 * <li> It is more tolarable against additional whitespaces
 * - Whenever it is possible to move edm validation to the AST classes then
 * this sould be done ( see visit {@link #visitSelectSegment} for example)
 * 
 * Not supported (TODO)
 * - Parsing the context of $metadata
 */
public class UriParseTreeVisitor extends UriParserBaseVisitor<Object> {

  private Edm edm;
  private EdmEntityContainer edmEntityContainer;

  // --- context ---
  private SelectItemOptionImpl contextSelectItem;
  private UriInfoImpl contextUriInfo;

  /**
   * Set within method {@link #visitExpandPath(ExpandPathContext ctx)} to allow nodes
   * deeper in the parse tree appending path segments to the currently processed {@link ExpandItemImpl}.
   * <li>The context is required because the following path segments depend on the successor.</li>
   * <li>A stack is used because the $expand system query option can be nested.</li>
   */
  private Stack<ExpandItemImpl> contextExpandItemPath = new Stack<ExpandItemImpl>();

  /**
   * Set to allow nodes deeper in the parse tree appending path segments to the currently
   * processed {@link ExpandItemImpl}.
   * <li>The context is required because the following path segments depend on the successor.</li>
   * <li>A stack is used because some system query options which are applied to the last type can
   * be nested ($expand, $filter)</li>
   */
  private Stack<EdmType> contextType = new Stack<EdmType>();
  private Stack<String> lambdaVariables = new Stack<String>();
  private LambdaRefImpl contextDetectedLambda;

  public UriParseTreeVisitor(Edm edm) {
    this.edm = edm;
    this.edmEntityContainer = edm.getEntityContainer(null);
  }

  public UriResourceImplTyped readFirstPathInfoSegment(PathSegmentContext ctx) {
    UriInfoImpl uriInfoResource = this.contextUriInfo;

    String odi = ctx.vODI.getText();

    // check EntitySet
    EdmEntitySet edmES = edmEntityContainer.getEntitySet(odi);
    if (edmES != null) {
      UriResourceEntitySetImpl uriPathInfo = new UriResourceEntitySetImpl();
      uriPathInfo.setEntitSet(edmES);
      uriInfoResource.addPathInfo(uriPathInfo);
      return null;
    }

    // check Singleton
    EdmSingleton edmSI = edmEntityContainer.getSingleton(odi);
    if (edmSI != null) {
      UriResourceSingletonImpl uriPathInfo = new UriResourceSingletonImpl();
      uriPathInfo.setSingleton(edmSI);
      uriInfoResource.addPathInfo(uriPathInfo);
      return null;
    }

    // check ActionImport
    EdmActionImport edmAI = edmEntityContainer.getActionImport(odi);
    if (edmAI != null) {
      UriResourceActionImpl uriPathInfo = new UriResourceActionImpl();
      uriPathInfo.setActionImport(edmAI);
      
      uriInfoResource.addPathInfo(uriPathInfo);
      return null;
    }

    // check FunctionImport
    EdmFunctionImport edmFI = edmEntityContainer.getFunctionImport(odi);
    if (edmFI != null) {

      // read the URI parameters
      List<UriParameterImpl> parameters = (List<UriParameterImpl>) ctx.vlNVO.get(0).accept(this);
      ctx.vlNVO.remove(0); // parameters are consumed

      UriResourceFunctionImpl uriPathInfo = new UriResourceFunctionImpl();
      uriPathInfo.setFunctionImport(edmFI, parameters);
      uriInfoResource.addPathInfo(uriPathInfo);
      return null;
    }

    throw wrap(new UriParserSemanticException("Unkown path segment found: " + odi));
  }

  public UriResourceImplTyped readNextPathInfoSegment(PathSegmentContext ctx) {
    String odi = ctx.vODI.getText();

    if (!(this.contextUriInfo.getLastUriPathInfo() instanceof UriResourceImplTyped)) {
      throw wrap(new UriParserSemanticException("Previous path segment not typed"));
    }

    UriResourceImplTyped lastSegment = (UriResourceImplTyped) this.contextUriInfo.getLastUriPathInfo();
    // TODO add check for type filters

    if (ctx.vNS == null) {

      //first check for lamda variable because a newly add property should not shaddow a long used lamdavarable
      
      if (this.lambdaVariables.contains(odi)) {
        contextDetectedLambda = new LambdaRefImpl().setVariableText(odi);
        return null;
      }
      
      //
      EdmType targetType = getLastType(lastSegment);
      if (!(targetType instanceof EdmStructuralType)) {
        throw wrap(new UriParserSemanticException("Property " + odi + " not found"));
      }

      EdmStructuralType structType = (EdmStructuralType) targetType;
      EdmElement property = structType.getProperty(odi);
      if (property == null) {
        throw wrap(new UriParserSemanticException("Unkown property: " + odi));
      }

      if (property instanceof EdmProperty) {
        if (((EdmProperty) property).isPrimitive() == true) {
          UriResourceSimplePropertyImpl uriPropertyImpl = new UriResourceSimplePropertyImpl();
          uriPropertyImpl.setProperty((EdmProperty) property);
          this.contextUriInfo.addPathInfo(uriPropertyImpl);
          return null;
        } else {
          UriResourceComplexPropertyImpl uriPropertyImpl = new UriResourceComplexPropertyImpl();
          uriPropertyImpl.setProperty((EdmProperty) property);
          this.contextUriInfo.addPathInfo(uriPropertyImpl);
          return null;
        }
      } else if (property instanceof EdmNavigationProperty) {
        UriResourceNavigationPropertyImpl uriPathInfoNavigation = new UriResourceNavigationPropertyImpl();
        uriPathInfoNavigation.addNavigationProperty((EdmNavigationProperty) property);
        contextUriInfo.addPathInfo(uriPathInfoNavigation);
        return null;
      } else {
        throw wrap(new UriParserSemanticException("Unkown property type"));
      }

    } else {
      FullQualifiedName fullName = getFullName(ctx.vNS, odi);
      EdmType lastType = getLastType(lastSegment);

      if (lastType instanceof EdmEntityType) {

        EdmEntityType et = edm.getEntityType(fullName);
        if (et != null) {
          // is simple entity type cast
          if (!(et.compatibleTo(lastType))) {
            throw wrap(new UriParserSemanticException("Types not kompatible"));
          }

          // check if last segement may contain key properties
          if (lastSegment instanceof UriResourceImplKeyPred) {
            UriResourceImplKeyPred lastKeyPred = (UriResourceImplKeyPred) lastSegment;

            if (lastKeyPred.isCollection() == false) {
              if (lastKeyPred.getTypeFilterOnEntry() != null) {
                throw wrap(new UriParserSemanticException("Single typeFilter are not chainable"));
              }
              lastKeyPred.setSingleTypeFilter(et);
              return null;
            } else {
              if (lastKeyPred.getTypeFilterOnCollection() != null) {
                throw wrap(new UriParserSemanticException("Collection typeFilters are not chainable"));
              }
              lastKeyPred.setCollectionTypeFilter(et);
              return null;
            }

          } else {
            // is
            if (lastSegment.getTypeFilter() != null) {
              throw wrap(new UriParserSemanticException("Chaining typefilters not allowed"));
            }

            lastSegment.setTypeFilter(et);
            return null;
          }

        }
      } else if (lastType instanceof EdmComplexType) {
        EdmComplexType ct = edm.getComplexType(fullName);
        if (ct != null) {
          if (!(ct.compatibleTo(lastType))) {

            throw wrap(new UriParserSemanticException("Types not kompatible"));
          }

          // is simple complex type cast
          if (lastSegment instanceof UriResourceImplKeyPred) {
            UriResourceImplKeyPred lastKeyPred = (UriResourceImplKeyPred) lastSegment;

            if (lastKeyPred.isCollection() == false) {
              if (lastKeyPred.getTypeFilterOnEntry() != null) {
                throw wrap(new UriParserSemanticException("Single TypeFilter are not chainable"));
              }
              lastKeyPred.setSingleTypeFilter(ct);
              return null;
            } else {
              if (lastKeyPred.getTypeFilterOnCollection() != null) {
                throw wrap(new UriParserSemanticException("Collection TypeFilter are not chainable"));
              }
              lastKeyPred.setCollectionTypeFilter(ct);
              return null;
            }

          } else {

            if (lastSegment.getTypeFilter() != null) {
              throw wrap(new UriParserSemanticException("Chaining Typefilters not allowed"));
            }

            lastSegment.setTypeFilter(ct);
            return null;
          }
        }
      }

      FullQualifiedName fullBindingTypeName = new FullQualifiedName(lastType.getNamespace(), lastType.getName());

      // check for action
      EdmAction action = edm.getAction(fullName, fullBindingTypeName, lastSegment.isCollection());
      if (action != null) {
        UriResourceActionImpl pathInfoAction = new UriResourceActionImpl();
        pathInfoAction.setAction(action);
        this.contextUriInfo.addPathInfo(pathInfoAction);
        return null;
      }

      // path segemend is not a complex type and not a entity type
      // do a check for bound functions (which requires the parameter list)

      if (ctx.vlNVO.size() == 0) {
        throw wrap(new UriParserSemanticException("Expected function parameters"));
      }

      List<UriParameterImpl> parameters = (List<UriParameterImpl>) ctx.vlNVO.get(0).accept(this);

      // handle bound function

      // get names of function parameters
      List<String> names = new ArrayList<String>();
      for (UriParameterImpl item : parameters) {
        names.add(item.getName());
      }

      EdmFunction function =
          edm.getFunction(fullName, fullBindingTypeName, lastSegment.isCollection(), names);

      if (function != null) {
        UriResourceFunctionImpl pathInfoFunction = new UriResourceFunctionImpl();
        pathInfoFunction.setFunction(function);
        pathInfoFunction.setParameters(parameters);
        this.contextUriInfo.addPathInfo(pathInfoFunction);
        ctx.vlNVO.remove(0);// as the parameters are consumed
        return null;
      }

      // check for special case
      if (lastSegment instanceof UriResourceItImpl) {
        if (((UriResourceItImpl) lastSegment).isExplicitIt() == false) {
          // check for unbound function
          EdmFunction functionUnbound =
              edm.getFunction(fullName, null, lastSegment.isCollection(), names);
          if (functionUnbound != null) {
            this.contextUriInfo.clearPathInfo(); // replace the $it
            // TODO maybe add $root pathinfo first
            UriResourceFunctionImpl pathInfoFunction = new UriResourceFunctionImpl();
            pathInfoFunction.setFunction(functionUnbound);
            pathInfoFunction.setParameters(parameters);
            this.contextUriInfo.addPathInfo(pathInfoFunction);
            ctx.vlNVO.remove(0);// as the parameters are consumed
            return null;
          }
        }
      }
      throw wrap(new UriParserSemanticException("Unknown resource path segment:" + fullName.toString()));

    }

  }

  private EdmType getLastType(UriResourceImplTyped lastSegment) {
    if (lastSegment instanceof UriResourceImplKeyPred) {
      UriResourceImplKeyPred lastKeyPred = (UriResourceImplKeyPred) lastSegment;
      if (lastKeyPred.getTypeFilterOnEntry() != null) {
        return lastKeyPred.getTypeFilterOnEntry();
      } else if (lastKeyPred.getTypeFilterOnCollection() != null) {
        return lastKeyPred.getTypeFilterOnCollection();
      }
    }
    EdmType type = lastSegment.getTypeFilter();
    if (type != null) {
      return type;
    }

    return lastSegment.getType();
  }

  @Override
  public Object visitAliasAndValue(AliasAndValueContext ctx) {

    AliasQueryOptionImpl alias = new AliasQueryOptionImpl();

    alias.setName(ctx.vODI.getText());
    alias.setText(ctx.vV.getText());
    alias.setAliasValue((ExpressionImpl) ctx.vV.accept(this));

    return alias;
  }

  @Override
  public ExpressionImpl visitAltAdd(AltAddContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    int tokenIndex = ctx.vO.getType();

    if (tokenIndex == UriLexer.ADD) {
      binary.setOperator(SupportedBinaryOperators.ADD);
    } else if (tokenIndex == UriLexer.SUB) {
      binary.setOperator(SupportedBinaryOperators.SUB);
    }

    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));
    return binary;
  }

  @Override
  public ExpressionImpl visitAltAnd(AltAndContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    binary.setOperator(SupportedBinaryOperators.AND);
    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));
    return binary;
  }

  @Override
  public Object visitAltBatch(AltBatchContext ctx) {
    this.contextUriInfo = new UriInfoImpl(edm).setKind(UriInfoKind.batch);
    return null;
  }

  @Override
  public ExpressionImpl visitAltComparism(AltComparismContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    int tokenIndex = ctx.vO.getType();

    if (tokenIndex == UriLexer.GT) {
      binary.setOperator(SupportedBinaryOperators.GT);
    } else if (tokenIndex == UriLexer.GE) {
      binary.setOperator(SupportedBinaryOperators.GE);
    } else if (tokenIndex == UriLexer.LT) {
      binary.setOperator(SupportedBinaryOperators.LT);
    } else if (tokenIndex == UriLexer.LE) {
      binary.setOperator(SupportedBinaryOperators.LE);
    } else if (tokenIndex == UriLexer.ISOF) {
      binary.setOperator(SupportedBinaryOperators.ISOF);
    }

    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));
    return binary;
  }

  @Override
  public Object visitAltEntity(AltEntityContext ctx) {
    UriInfoImpl uriInfo = new UriInfoImpl(edm).setKind(UriInfoKind.entityId);

    List<QueryOptionImpl> list = (List<QueryOptionImpl>) ctx.vEO.accept(this);
    uriInfo.setQueryOptions(list);

    this.contextUriInfo = uriInfo;
    return null;
  }

  @Override
  public Object visitAltEntityCast(AltEntityCastContext ctx) {
    UriInfoImpl uriInfo = new UriInfoImpl(edm).setKind(UriInfoKind.entityId);

    String odi = ctx.vODI.getText();
    FullQualifiedName fullName = getFullName(ctx.vNS, odi);

    EdmEntityType type = edm.getEntityType(fullName);
    if (type == null) {
      throw wrap(new UriParserSemanticException("Expected EntityTypeName"));
    }
    uriInfo.setEntityTypeCast(type);
    
    contextUriInfo = uriInfo;
    contextType.push(uriInfo.getEntityTypeCast());

    List<QueryOptionImpl> list = (List<QueryOptionImpl>) ctx.vEO.accept(this);
    uriInfo.setQueryOptions(list);

    return null;
  }

  private ParseCancellationException wrap(UriParserException uriParserException) {
    return new ParseCancellationException(uriParserException);
  }

  @Override
  public ExpressionImpl visitAltEquality(AltEqualityContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    int tokenIndex = ctx.vO.getType();

    if (tokenIndex == UriLexer.EQ_ALPHA) {
      binary.setOperator(SupportedBinaryOperators.EQ);
    } else if (tokenIndex == UriLexer.NE) {
      binary.setOperator(SupportedBinaryOperators.NE);
    }

    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));
    return binary;
  }

  @Override
  public Object visitAltLiteral(AltLiteralContext ctx) {
    return new LiteralImpl().setText(ctx.getText());
  }

  @Override
  public Object visitAltMetadata(AltMetadataContext ctx) {
    UriInfoImpl uriInfo = new UriInfoImpl(edm).setKind(UriInfoKind.metadata);

    if (ctx.vF != null) {
      FormatOptionImpl format = (FormatOptionImpl) ctx.vF.accept(this);
      uriInfo.setFormat(format);
    }

    if (ctx.vCF != null) {
      throw wrap(new UriParserException("Fragment for $metadata not supported"));
    }

    this.contextUriInfo = uriInfo;
    return null;
  }

  @Override
  public ExpressionImpl visitAltMult(AltMultContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    int tokenIndex = ctx.vO.getType();

    if (tokenIndex == UriLexer.MUL) {
      binary.setOperator(SupportedBinaryOperators.MUL);
    } else if (tokenIndex == UriLexer.DIV) {
      binary.setOperator(SupportedBinaryOperators.DIV);
    } else if (tokenIndex == UriLexer.MOD) {
      binary.setOperator(SupportedBinaryOperators.MOD);
    }

    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));
    return binary;
  }

  @Override
  public ExpressionImpl visitAltOr(AltOrContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    binary.setOperator(SupportedBinaryOperators.OR);

    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));
    return binary;
  }

  @Override
  public Object visitAltResourcePath(AltResourcePathContext ctx) {
    ctx.vRP.accept(this);

    if (ctx.vQO != null) {
      UriResourcePart lastSegment = contextUriInfo.getLastUriPathInfo();

      if (lastSegment instanceof UriResourceImplTyped) {
        contextType.push(getLastType((UriResourceImplTyped) lastSegment));
      }
      contextUriInfo.setQueryOptions((List<QueryOptionImpl>) ctx.vQO.accept(this));
    }
    return null;
  }

  @Override
  public ExpressionImpl visitCastExpr(CastExprContext ctx) {
    MethodCallImpl method = new MethodCallImpl();
    if (ctx.vE1 != null) {
      ExpressionImpl onExpression = (ExpressionImpl) ctx.vE1.accept(this);
      method.addParameter(onExpression);
    }

    FullQualifiedName fullName = new FullQualifiedName(ctx.vNS.getText(), ctx.vODI.getText());
    EdmType type = edm.getTypeDefinition(fullName);
    method.setMethod(SupportedMethodCalls.CAST);
    method.addParameter(new TypeLiteralImpl().setType(type));

    return method;
  }

  @Override
  public ExpressionImpl visitCeilingMethodCallExpr(CeilingMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.CEILING)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitConcatMethodCallExpr(ConcatMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.CONCAT)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public ExpressionImpl visitContainsMethodCallExpr(ContainsMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.CONTAINS)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public Object visitCrossjoin(CrossjoinContext ctx) {
    UriInfoImpl crossJoin = new UriInfoImpl(edm).setKind(UriInfoKind.crossjoin);

    for (OdataIdentifierContext obj : ctx.vlODI) {
      crossJoin.addEntitySetName(obj.getText());
    }

    this.contextUriInfo = crossJoin;
    return null;
  }

  @Override
  public Object visitCustomQueryOption(CustomQueryOptionContext ctx) {
    CustomQueryOptionImpl queryOption = new CustomQueryOptionImpl();
    queryOption.setName(ctx.getChild(0).getText());

    // set value only if present
    if (ctx.getChildCount() > 1) {
      queryOption.setText(ctx.getChild(2).getText());
    }

    return queryOption;
  }

  @Override
  public ExpressionImpl visitDayMethodCallExpr(DayMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.DAY)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitDistanceMethodCallExpr(DistanceMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.DISTANCE)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public Object visitEndsWithMethodCallExpr(EndsWithMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.ENDSWITH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitEntityOptions(EntityOptionsContext ctx) {
    List<QueryOptionImpl> queryOptionList = new ArrayList<QueryOptionImpl>();

    for (EntityOptionContext entityOption : ctx.vlEOb) {
      queryOptionList.add((QueryOptionImpl) entityOption.accept(this));
    }

    queryOptionList.add((QueryOptionImpl) ctx.vlEOm.accept(this));

    for (EntityOptionContext entityOption : ctx.vlEOa) {
      queryOptionList.add((QueryOptionImpl) entityOption.accept(this));
    }
    return queryOptionList;
  }

  @Override
  public Object visitEntityOptionsCast(EntityOptionsCastContext ctx) {
    List<QueryOptionImpl> queryOptionList = new ArrayList<QueryOptionImpl>();
    for (EntityOptionCastContext entityOption : ctx.vlEOb) {
      queryOptionList.add((QueryOptionImpl) entityOption.accept(this));
    }

    queryOptionList.add((QueryOptionImpl) ctx.vlEOm.accept(this));

    for (EntityOptionCastContext entityOption : ctx.vlEOa) {
      queryOptionList.add((QueryOptionImpl) entityOption.accept(this));
    }
    return queryOptionList;
  }

  @Override
  public Object visitExpand(ExpandContext ctx) {
    ExpandOptionImpl expand = new ExpandOptionImpl();
    for (ExpandItemContext eI : ctx.vlEI) {
      expand.addExpandItem((ExpandItemImpl) eI.accept(this));
    }

    return expand;
  }

  @Override
  public Object visitExpandItem(ExpandItemContext ctx) {

    ExpandItemImpl expandItem = null;
    if (ctx.vS != null) {
      expandItem = new ExpandItemImpl().setStar(true);
      if (ctx.vR != null) {
        //expandItem.setRef(true);
        //TODO do creat reference segement
      } else if (ctx.vM != null) {
        expandItem.setExpandQueryOption(new LevelOptionImpl().setMax());
      } else if (ctx.vL != null) {
        expandItem.setExpandQueryOption(new LevelOptionImpl().setValue(ctx.vL.getText()));
      }

    } else if (ctx.vEP != null) {
      expandItem = (ExpandItemImpl) ctx.vEP.accept(this);
    } else {
      // error
    }

    if (ctx.vEPE != null) {
      contextExpandItemPath.push(expandItem);
      List<SystemQueryOptionImpl> list = (List<SystemQueryOptionImpl>) ctx.vEPE.accept(this);
      for (SystemQueryOptionImpl option : list) {
        expandItem.setExpandQueryOption(option);
      }
      contextExpandItemPath.pop();
    }

    return expandItem;

  }

  @Override
  public Object visitExpandPath(ExpandPathContext ctx) {
    ExpandItemImpl expandItem = new ExpandItemImpl();

    contextExpandItemPath.push(expandItem);

    super.visitExpandPath(ctx);

    contextExpandItemPath.pop();

    return expandItem;
  }

  @Override
  public Object visitExpandPathSegment(ExpandPathSegmentContext ctx) {
    ExpandItemImpl expandItemPrev = contextExpandItemPath.peek();

    String odi = ctx.vODI.getText();
    if (ctx.vNS == null) {
      EdmType targetType = null;
      if (expandItemPrev.getLastSegement() == null) {
        // used the global type
        UriResourceImplTyped lastSegment = (UriResourceImplTyped) this.contextUriInfo.getLastUriPathInfo();
        targetType = this.getLastType(lastSegment);
      } else {
        ExpandSegment segment = expandItemPrev.getLastSegement();
        if (segment.getTypeFilter() != null) {
          targetType = segment.getTypeFilter();
        } else {
          targetType = segment.getType();
        }
      }

      if (!(targetType instanceof EdmStructuralType)) {
        throw wrap(new UriParserSemanticException("Prev Expandsegment has no properties"));
      }

      EdmStructuralType structType = (EdmStructuralType) targetType;
      EdmElement property = (EdmElement) structType.getProperty(odi);
      if (property != null) {
        ExpandSegment seg = new ExpandSegment();
        seg.setProperty(property);
        expandItemPrev.addSegment(seg);
        return null;
      }

      throw wrap(new UriParserSemanticException("Prev Expandsegment has no property:" + odi));

    } else {
      EdmType targetType = null;
      if (expandItemPrev.getLastSegement() == null) {
        UriResourceImplTyped lastSegment = (UriResourceImplTyped) this.contextUriInfo.getLastUriPathInfo();
        targetType = this.getLastType(lastSegment);

        ExpandSegment seg = new ExpandSegmentIt();
        expandItemPrev.addSegment(seg);

      } else {
        ExpandSegment segment = expandItemPrev.getLastSegement();
        if (segment.getTypeFilter() != null) {
          throw wrap(new UriParserSemanticException("Prev Expandsegment has already a type filter"));
        } else {
          targetType = segment.getType();
        }
      }

      FullQualifiedName fullName = getFullName(ctx.vNS, odi);

      if (targetType instanceof EdmEntityType) {
        EdmEntityType et = edm.getEntityType(fullName);
        if (et == null) {
          throw wrap(new UriParserSemanticException("entity type not found"));
        }
        if (et.compatibleTo(targetType)) {
          expandItemPrev.getLastSegement().setFilter(et);
          return null;
        }
      }

      if (targetType instanceof EdmComplexType) {
        EdmComplexType ct = edm.getComplexType(fullName);
        if (ct == null) {
          throw wrap(new UriParserSemanticException("Complex type not found"));
        }
        if (ct.compatibleTo(targetType)) {
          expandItemPrev.getLastSegement().setFilter(ct);
          return null;
        }
      }
    }
    return null;
  }

  @Override
  public Object visitExpandPathExtension(ExpandPathExtensionContext ctx) {
    ExpandItemImpl expandItemPrev = contextExpandItemPath.peek();

    if (contextType == null) {
      contextType = new Stack<EdmType>();
    }

    List<SystemQueryOptionImpl> list = new ArrayList<SystemQueryOptionImpl>();

    EdmType targetType = null;
    if (expandItemPrev.getLastSegement() == null) {
      // used the global type
      UriResourceImplTyped lastSegment = (UriResourceImplTyped) this.contextUriInfo.getLastUriPathInfo();
      targetType = this.getLastType(lastSegment);
    } else {
      ExpandSegment segment = expandItemPrev.getLastSegement();
      if (segment.getTypeFilter() != null) {
        targetType = segment.getTypeFilter();
      } else {
        targetType = segment.getType();
      }
    }

    contextType.push(targetType);

    if (ctx.vC != null) {
      ExpandSegment seg = new ExpandSegmentCount();
      expandItemPrev.addSegment(seg);
      for (ExpandCountOptionContext s : ctx.vlEOC) {
        list.add((SystemQueryOptionImpl) s.accept(this));
      }

    } else if (ctx.vR != null) {
      ExpandSegment seg = new ExpandSegmentRef();
      expandItemPrev.addSegment(seg);
      for (ExpandRefOptionContext s : ctx.vlEOR) {
        list.add((SystemQueryOptionImpl) s.accept(this));
      }
    } else {
      for (ExpandOptionContext s : ctx.vlEO) {
        list.add((SystemQueryOptionImpl) s.accept(this));
      }
    }

    contextType.pop();
    return list;

  }

  @Override
  public Object visitFilter(FilterContext ctx) {

    FilterOptionImpl filter = new FilterOptionImpl()
        .setExpression((ExpressionImpl) ctx.children.get(2).accept(this));
    return filter;
  }

  @Override
  public Object visitOrderBy(OrderByContext ctx) {

    OrderByOptionImpl orderBy = new OrderByOptionImpl();
    // TODO collect orders
    // .setExpression((Expression) ctx.children.get(2).accept(this));
    return orderBy;
  }

  @Override
  public ExpressionImpl visitFloorMethodCallExpr(FloorMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.FLOOR)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitFormat(FormatContext ctx) {
    FormatOptionImpl format = new FormatOptionImpl();

    TerminalNodeImpl c2 = (TerminalNodeImpl) ctx.children.get(2);
    if (c2.symbol.getType() == UriLexer.ATOM) {
      format.setValue("atom");
    } else if (c2.symbol.getType() == UriLexer.JSON) {
      format.setValue("json");
    } else if (c2.symbol.getType() == UriLexer.XML) {
      format.setValue("xml");
    } else if (c2.symbol.getType() == UriLexer.PCHARS) {
      if (ctx.getChildCount() == 2) {
        format.setValue(c2.getText());
      } else {
        format.setValue(c2.getText() + "/" + ctx.children.get(4).getText());
      }
    }

    return format;
  }

  @Override
  public ExpressionImpl visitFractionalsecondsMethodCallExpr(FractionalsecondsMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.FRACTIONALSECOND)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitGeoLengthMethodCallExpr(GeoLengthMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.GEOLENGTH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitHourMethodCallExpr(HourMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.HOUR)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitId(IdContext ctx) {
    IdOptionImpl id = new IdOptionImpl();
    /*
     * UriInfoImplResource uriInfoImplpath = new UriInfoImplResource(edm);
     * 
     * // store the context uriInfoPath
     * UriInfoImplResource backupUriInfoPath = this.contextUriInfoPath;
     * 
     * // set temporary uriInfoPath to collect the path information of the memberExpression
     * this.contextUriInfoPath = uriInfoImplpath;
     * 
     * ctx.children.get(2).accept(this);
     * 
     * this.contextUriInfoPath = backupUriInfoPath;
     * 
     * // add the typeFilter which was part of the resource path behind the $entity segment
     * UriPathInfoImpl lastPathInfo = uriInfoImplpath.getLastUriPathInfo();
     * EdmStructuralType typeFilter = ((UriInfoImplEntity) this.contextUriInfo).getTypeFilter();
     * if (typeFilter != null) {
     * lastPathInfo.addTypeFilter(typeFilter);
     * }
     */
    
    String text =   ctx.children.get(2).getText();
        
    return id.setValue(text).setText(text);
  }

  @Override
  public ExpressionImpl visitIndexOfMethodCallExpr(IndexOfMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.INDEXOF)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public ExpressionImpl visitIntersectsMethodCallExpr(IntersectsMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.GEOLENGTH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public ExpressionImpl visitIsofExpr(IsofExprContext ctx) {
    MethodCallImpl method = new MethodCallImpl();
    if (ctx.vE1 != null) {
      ExpressionImpl onExpression = (ExpressionImpl) ctx.vE1.accept(this);
      method.addParameter(onExpression);
    }

    FullQualifiedName fullName = new FullQualifiedName(ctx.vNS.getText(), ctx.vODI.getText());
    EdmType type = edm.getTypeDefinition(fullName);
    method.setMethod(SupportedMethodCalls.ISOF);
    method.addParameter(new TypeLiteralImpl().setType(type));

    return method;
  }

  @Override
  public ExpressionImpl visitLengthMethodCallExpr(LengthMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.LENGTH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitMaxDateTimeMethodCallExpr(MaxDateTimeMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.MINDATETIME);
  }

  @Override
  public Object visitRootExpr(RootExprContext ctx) {
    UriInfoImpl uriInfoImplpath = new UriInfoImpl(edm).setKind(UriInfoKind.resource);

    UriResourceRootImpl pathInfoRoot = new UriResourceRootImpl();

    uriInfoImplpath.addPathInfo(pathInfoRoot);

    if (ctx.vPs != null) {
      // store the context uriInfoPath
      UriInfoImpl backupUriInfoPath = this.contextUriInfo;

      // set temporary uriInfoPath to collect the path information of the memberExpression
      this.contextUriInfo = uriInfoImplpath;

      ctx.vPs.accept(this);

      this.contextUriInfo = backupUriInfoPath;
    }
    return new MemberImpl()
        .setPath(uriInfoImplpath);

  }

  @Override
  public Object visitInlinecount(InlinecountContext ctx) {
    // TODO implement
    return super.visitInlinecount(ctx);
  }

  @Override
  public Object visitAltAny(AltAnyContext ctx) {
    UriInfoImpl uriInfoImplpath = new UriInfoImpl(edm).setKind(UriInfoKind.resource);

    UriResourceItImpl pathInfoIT = new UriResourceItImpl();
    pathInfoIT.setType(contextType.peek());

    uriInfoImplpath.addPathInfo(pathInfoIT);
    uriInfoImplpath.addPathInfo((UriResourcePartImpl) super.visitAltAny(ctx));

    return new MemberImpl()
        .setPath(uriInfoImplpath);
  }

  @Override
  public Object visitAnyExpr(AnyExprContext ctx) {
    UriResourceAnyImpl any = new UriResourceAnyImpl();
    if (ctx.vLV != null) {
      any.setLamdaVariable(ctx.vLV.getText());
      lambdaVariables.push(any.getLamdaVariable());
      any.setExpression((ExpressionImpl) ctx.vLE.accept(this));
      lambdaVariables.pop();
    }
    return any;
  }

  @Override
  public Object visitAltAll(AltAllContext ctx) {
    UriInfoImpl uriInfoImplpath = new UriInfoImpl(edm).setKind(UriInfoKind.resource);

    UriResourceItImpl pathInfoIT = new UriResourceItImpl();
    pathInfoIT.setType(contextType.peek());

    uriInfoImplpath.addPathInfo(pathInfoIT);
    uriInfoImplpath.addPathInfo((UriResourcePartImpl) super.visitAltAll(ctx));

    return new MemberImpl()
        .setPath(uriInfoImplpath);
  }

  @Override
  public Object visitAllExpr(AllExprContext ctx) {
    UriResourceAllImpl all = new UriResourceAllImpl();
    all.setLamdaVariable(ctx.vLV.getText());
    lambdaVariables.push(all.getLamdaVariable());
    all.setExpression((ExpressionImpl) ctx.vLE.accept(this));
    lambdaVariables.pop();
    return all;
  }

  @Override
  public Object visitMemberExpr(MemberExprContext ctx) {

    UriInfoImpl uriInfoImplpath = new UriInfoImpl(edm).setKind(UriInfoKind.resource);

    UriResourceItImpl pathInfoIT = new UriResourceItImpl();
    // the start type for members is the final type of the resource path
    // check wath happens for expand
    pathInfoIT.setType(contextType.peek());
    if (ctx.vIt != null || ctx.vIts != null) {
      pathInfoIT.setIsExplicitIT(true); // a $it prohibits unbound functions as member expression
    }

    uriInfoImplpath.addPathInfo(pathInfoIT);

    if (ctx.vPs != null) {
      // store the context uriInfoPath
      UriInfoImpl backupUriInfoPath = this.contextUriInfo;

      // set temporary uriInfoPath to collect the path information of the memberExpression
      this.contextUriInfo = uriInfoImplpath;
      contextDetectedLambda = null;
      ctx.vPs.accept(this);
      this.contextUriInfo = backupUriInfoPath;
      if (contextDetectedLambda!= null) {
        LambdaRefImpl tmp = contextDetectedLambda;
        contextDetectedLambda = null;
        return tmp;
      }
      
    }


    if (ctx.vALL != null) {
      uriInfoImplpath.addPathInfo((UriResourcePartImpl) ctx.vALL.accept(this));
    }
    if (ctx.vANY != null) {
      uriInfoImplpath.addPathInfo((UriResourcePartImpl) ctx.vANY.accept(this));
    }
    

    return new MemberImpl()
        .setPath(uriInfoImplpath);
  }

  @Override
  public ExpressionImpl visitMinDateTimeMethodCallExpr(MinDateTimeMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.MINDATETIME);
  }

  @Override
  public ExpressionImpl visitMinuteMethodCallExpr(MinuteMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.MINUTE)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitMonthMethodCallExpr(MonthMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.MONTH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitNameValueOptList(NameValueOptListContext ctx) {

    // is key predicate
    if (ctx.vVO != null) {
      String text = ctx.vVO.vV.getText();
      ExpressionImpl expression = (ExpressionImpl) ctx.vVO.vV.accept(this);

      if (!(contextUriInfo.getLastUriPathInfo() instanceof UriResourceImplTyped)) {
        throw wrap(new UriParserSyntaxException("Invalid Paramterslist"));
      }

      EdmEntityType entityType = (EdmEntityType) ((UriResourceImplTyped) contextUriInfo.getLastUriPathInfo()).getType();

      List<String> keyPredicates = entityType.getKeyPredicateNames();
      if (keyPredicates.size() == 1) {
        String keyName = keyPredicates.get(0);
        List<UriParameterImpl> list = new ArrayList<UriParameterImpl>();
        list.add(new UriParameterImpl().setName(keyName).setText(text).setExpression(expression));
        return list;
      }

      // if there is only a single key in the URI but there are more than one keys defined in the EDM, then reduce
      // the keylist with the keys defined as referential constained.

      if (contextUriInfo.getLastUriPathInfo() instanceof UriResourceNavigationPropertyImpl) {
        UriResourceNavigationPropertyImpl nav = (UriResourceNavigationPropertyImpl) contextUriInfo.getLastUriPathInfo();
        EdmNavigationProperty navProp = (EdmNavigationProperty) nav.getNavigationProperty();
        /*
         * if ( navProp.getPartner() != null) {
         * //copy keylist into tmpKeyList
         * 
         * EdmEntityType entityType = nav.getType()// TODO check typecast,
         * EdmNavigationProperty navProp2 = entityType.getProperty(navProp.getPartner());
         * for ( EdmRefConstrain constrain : navProp2 ) {
         * //remove constrain.referenceproperty from tmpKeyList
         * }
         * 
         * if( tmpKeyList.size==1)
         * String keyName = keyPredicates.get(0);
         * List<UriParameterImpl> list = new ArrayList<UriParameterImpl>();
         * list.add(new UriParameterImpl().setName(keyName).setValue(value));
         * return list;
         * }
         * }
         */
      }

      throw wrap(new UriParserSyntaxException(
          "for using a value only keyPredicate there must be exact ONE defined keyProperty"));

    } else {
      List<UriParameterImpl> list = new ArrayList<UriParameterImpl>();
      if (ctx.vNVL != null) {
        for (ParseTree c : ctx.vNVL.vlNVP) {
          list.add((UriParameterImpl) c.accept(this));
        }
      }
      return list;

    }
  }

  @Override
  public UriParameterImpl visitNameValuePair(NameValuePairContext ctx) {
    UriParameterImpl uriParameter = new UriParameterImpl();
    uriParameter.setName(ctx.vODI.getText());

    if (ctx.vCOM != null) {
      uriParameter.setText(ctx.vCOM.getText());
      uriParameter.setExpression((ExpressionImpl) ctx.vCOM.accept(this));
    } else {
      uriParameter.setAlias(ctx.vALI.getText());
    }

    return uriParameter;
  }

  @Override
  public ExpressionImpl visitNowMethodCallExpr(NowMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.NOW);
  }

  @Override
  public Object visitOdataRelativeUriEOF(OdataRelativeUriEOFContext ctx) {
    // setup context
    this.contextUriInfo = null;
    // visit children
    super.visitOdataRelativeUriEOF(ctx);

    return this.contextUriInfo;
  }

  @Override
  public Object visitPathSegment(PathSegmentContext ctx) {

    if (contextUriInfo.getLastUriPathInfo() == null ||
        contextUriInfo.getLastUriPathInfo() instanceof UriResourceRootImpl) {
      readFirstPathInfoSegment(ctx);
    } else {
      readNextPathInfoSegment(ctx);
    }

    UriResourcePartImpl pathInfoSegment = (UriResourcePartImpl) this.contextUriInfo.getLastUriPathInfo();

    if (ctx.vlNVO.size() > 0) {
      // check for keyPredicates
      if (pathInfoSegment instanceof UriResourceImplKeyPred) {

        ((UriResourceImplKeyPred) pathInfoSegment)
            .setKeyPredicates((List<UriParameterImpl>) ctx.vlNVO.get(0).accept(this));
      } else {
        throw wrap(new UriParserSemanticException("Key properties not allowed"));
        // throw UriSemanticError.addKrepredicatesNotAllowed();
      }
    }

    return pathInfoSegment;
  }

  @Override
  public Object visitPathSegments(PathSegmentsContext ctx) {
    // path segment
    for (PathSegmentContext it : ctx.vlPS) {
      it.accept(this);
    }

    // const segment
    if (ctx.vCS != null) {
      ctx.vCS.accept(this);
    }
    return null;
  }

  @Override
  public Object visitConstSegment(ConstSegmentContext ctx) {
    UriInfoImpl uriInfoResource = this.contextUriInfo;
    UriResourcePart pathInfo = uriInfoResource.getLastUriPathInfo();

    if (ctx.vV != null) {
      if (pathInfo instanceof UriResourcePartTyped) {
        if (!((UriResourcePartTyped) pathInfo).isCollection()) {
          this.contextUriInfo.addPathInfo(new UriResourceValueImpl());
        } else {
          throw wrap(new UriParserSemanticException("$value only allowed on typed path segments"));
        }
        return null;
      } else {
        throw wrap(new UriParserSemanticException("$value only allowed on typed path segments"));
      }

    } else if (ctx.vC != null) {
      if (pathInfo instanceof UriResourceImplTyped) {
        if (((UriResourceImplTyped) pathInfo).isCollection()) {
          this.contextUriInfo.addPathInfo(new UriResourceCountImpl());
        } else {
          throw wrap(new UriParserSemanticException("$count only allowed on collection properties"));
        }
      } else {
        throw wrap(new UriParserSemanticException("$count only allowed on typed properties"));
      }
    } else if (ctx.vR != null) {
      if (pathInfo instanceof UriResourceImplTyped) {
        EdmType type = ((UriResourceImplTyped) pathInfo).getType();
        if (type instanceof EdmEntityType) {
          this.contextUriInfo.addPathInfo(new UriResourceRefImpl());
        } else {
          throw wrap(new UriParserSemanticException("$ref only allowd on endity types"));
        }
      } else {
        throw wrap(new UriParserSemanticException("$ref only allowed on typed properties"));
      }

    }
    return null;
  }

  @Override
  public Object visitQueryOptions(QueryOptionsContext ctx) {
    if (contextType == null) {
      contextType = new Stack<EdmType>();

    }
    // contextType.push(this.contextUriInfo.getLastUriPathInfo().getType());

    // QueryOptionsList qpList = new QueryOptionsList();
    List<QueryOptionImpl> qpList = new ArrayList<QueryOptionImpl>();
    for (QueryOptionContext entityOption : ctx.vlQO) {
      qpList.add((QueryOptionImpl) entityOption.accept(this));
    }

    return qpList;
  }

  @Override
  public Object visitResourcePath(ResourcePathContext ctx) {
    if (ctx.vAll != null) {
      this.contextUriInfo = new UriInfoImpl(edm).setKind(UriInfoKind.all);
    } else if (ctx.vCJ != null) {
      ctx.vCJ.accept(this);
    } else if (ctx.vlPS != null) {
      UriInfoImpl uriInfoPath = new UriInfoImpl(edm).setKind(UriInfoKind.resource);
      this.contextUriInfo = uriInfoPath;
      super.visitResourcePath(ctx); // visit all children of ctx
    }
    return this.contextUriInfo;
  }

  @Override
  public ExpressionImpl visitRoundMethodCallExpr(RoundMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.ROUND)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitSecondMethodCallExpr(SecondMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.SECOND)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitSelect(SelectContext ctx) {
    List<SelectItemOptionImpl> selectItems = new ArrayList<SelectItemOptionImpl>();

    for (SelectItemContext si : ctx.vlSI) {
      selectItems.add((SelectItemOptionImpl) si.accept(this));
    }

    return new SelectOptionImpl().setSelectItems(selectItems);
  }

  @Override
  public Object visitSelectItem(SelectItemContext ctx) {
    SelectItemOptionImpl selectItem = new SelectItemOptionImpl();
    selectItem.setEdm(edm);
    selectItem.setStartType(contextType.peek());

    contextSelectItem = selectItem;
    for (SelectSegmentContext si : ctx.vlSS) {
      si.accept(this);
    }
    contextSelectItem = null;

    return selectItem;
  }

  @Override
  public Object visitSelectSegment(SelectSegmentContext ctx) {

    if (ctx.vS != null) {
      if (ctx.vNS != null) {
        String namespace = ctx.vNS.getText();
        namespace = namespace.substring(0, namespace.length() - 1);
        FullQualifiedName fullName = new FullQualifiedName(namespace, "*");
        contextSelectItem.addAllOperationsInSchema(fullName);
      } else {
        contextSelectItem.addStar();
      }
      return null;
    }

    String odi = ctx.vODI.getText();
    if (ctx.vNS == null) {

      contextSelectItem.addProperty(odi);

    } else {
      String namespace = ctx.vNS.getText();
      namespace = namespace.substring(0, namespace.length() - 1);

      FullQualifiedName fullName = new FullQualifiedName(namespace, odi);
      contextSelectItem.addQualifiedThing(fullName);
    }

    return null;
  }

  @Override
  public ExpressionImpl visitStartsWithMethodCallExpr(StartsWithMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.STARTSWITH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitSubstringMethodCallExpr(SubstringMethodCallExprContext ctx) {
    MethodCallImpl ret = new MethodCallImpl();
    ret.setMethod(SupportedMethodCalls.SUBSTRING);
    ret.addParameter((ExpressionImpl) ctx.vE1.accept(this));
    ret.addParameter((ExpressionImpl) ctx.vE2.accept(this));

    if (ctx.vE3 != null) {
      ret.addParameter((ExpressionImpl) ctx.vE3.accept(this));
    }

    return ret;

  }

  @Override
  public ExpressionImpl visitTimeMethodCallExpr(TimeMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TIME)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitToLowerMethodCallExpr(ToLowerMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TOLOWER)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitTotalOffsetMinutesMethodCallExpr(TotalOffsetMinutesMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TOTALOFFSETMINUTES)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitTotalsecondsMethodCallExpr(TotalsecondsMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TOTALSECONDS)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitToUpperMethodCallExpr(ToUpperMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TOUPPER)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitTrimMethodCallExpr(TrimMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TRIM)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitUnary(UnaryContext ctx) {
    // TODO implement
    return super.visitUnary(ctx);
  }

  @Override
  public ExpressionImpl visitYearMethodCallExpr(YearMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.YEAR)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  private FullQualifiedName getFullName(NamespaceContext vNS, String odi) {
    if (vNS != null) {
      String namespace = vNS.getText();
      namespace = namespace.substring(0, namespace.length() - 1);

      return new FullQualifiedName(namespace, odi);
    }
    return null;

  }

}
