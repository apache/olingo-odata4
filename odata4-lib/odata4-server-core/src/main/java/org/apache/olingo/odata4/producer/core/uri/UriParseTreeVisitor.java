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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.apache.olingo.odata4.commons.api.edm.EdmEnumType;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmSingleton;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriResource;
import org.apache.olingo.odata4.producer.api.uri.UriResourcePartTyped;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedBinaryOperators;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedConstants;
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
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltHasContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMetadataContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltMultContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltOrContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AltResourcePathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.AnyExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.BooleanNonCaseContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CastExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CeilingMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ConcatMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ConstSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ContainsMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CrossjoinContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.CustomQueryOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.DateMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.DayMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EndsWithMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EntityOptionCastContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EntityOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EntityOptionsCastContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EntityOptionsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.EnumLitContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandCountOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandItemContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandPathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandPathExtensionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ExpandRefOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FilterContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FloorMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FormatContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.FractionalsecondsMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.GeoDistanceMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.GeoIntersectsMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.GeoLengthMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.HourMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.IdContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.IndexOfMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.InlinecountContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.IsofExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.LengthMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.LevelsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MaxDateTimeMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MemberExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MinDateTimeMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MinuteMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.MonthMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValueOptListContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NameValuePairContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NamespaceContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NowMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.NullruleContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataIdentifierContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriEOFContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OrderByContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OrderByItemContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PathSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PathSegmentsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.PrimitiveLiteralContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.QueryOptionContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.QueryOptionsContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ResourcePathContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.RootExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.RoundMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SecondMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SelectContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SelectItemContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SelectSegmentContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SkipContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SkiptokenContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.StartsWithMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.SubstringMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TimeMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ToLowerMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.ToUpperMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TopContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TotalOffsetMinutesMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TotalsecondsMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.TrimMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.UnaryContext;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.YearMethodCallExprContext;
import org.apache.olingo.odata4.producer.core.uri.queryoption.AliasQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandItemImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.InlineCountOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.LevelsOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.OrderByItemImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.QueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SelectItemImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SkipOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SkipTokenOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SystemQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.TopOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.ConstantImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.EnumerationImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.ExpressionImpl;
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
 * <li> It is more tolerable against additional white spaces
 * - Whenever it is possible to move edm validation to the AST classes then
 * this should be done ( see visit {@link #visitSelectSegment} for example)
 * 
 * Not supported
 * <li>Parsing the context of $metadata
 * 
 * TODO
 * <li>clean up
 * <li>Overview testcases
 * <li>search
 * <li>percent decoding
 * 
 */
public class UriParseTreeVisitor extends UriParserBaseVisitor<Object> {
  private Edm edm;

  private EdmEntityContainer edmEntityContainer;

  private class LambdaVariables {
    public boolean isCollection;
    public String name;
    public EdmType type;
  }

  /**
   * Hold all currently allowed lambda variables
   * As lambda functions can be nested there may be more than one allowed lambda variables at a time while parsing a
   * $filter or $orderby expressions.
   */
  private Stack<LambdaVariables> allowedLambdaVariables = new Stack<LambdaVariables>();

  private class TypeInformation {
    private boolean isCollection;
    private EdmType type;

    TypeInformation(final EdmType type, final boolean isCollection) {
      this.type = type;
      this.isCollection = isCollection;
    }

    public TypeInformation() {
      // TODO Auto-generated constructor stub
    }
  }

  /**
   * Used to stack type information for nested $expand, $filter query options and other cases.
   */
  private Stack<TypeInformation> contextTypes = new Stack<TypeInformation>();

  /**
   * Set within method {@link #visitExpandItem(ExpandPathContext ctx)} and {@link #visitExpandPathExtension(final
   * ExpandPathExtensionContext ctx)} to allow nodes
   * deeper in the expand tree at {@link #visitExpandPathExtension(ExpandPathExtensionContext ctx)} appending path
   * segments to the currently processed {@link ExpandItemImpl}.
   */
  private ExpandItemImpl contextExpandItemPath;

  /**
   * Set within method {@link #visitSelectItem(SelectItemContext ctx)} to allow nodes
   * deeper in the expand tree at {@link #visitSelectSegment(SelectSegmentContext ctx)} appending path segments to the
   * currently processed {@link SelectItemImpl}.
   */
  private SelectItemImpl contextSelectItem;

  /**
   * Stores the currently processed UriInfo objects. There is one URI Info object for the resource path
   * and one for each new first member access within $filter and $orderBy options.
   */
  private UriInfoImpl contextUriInfo;

  private boolean contextReadingFunctionParameters = false;

  // --- class ---

  public void init() {
    allowedLambdaVariables.clear();
    contextUriInfo = null;
    contextExpandItemPath = null;
    contextSelectItem = null;
    contextTypes.clear();
    contextUriInfo = null;
  }

  public UriInfoImpl getUriInfo() {
    return contextUriInfo;
  }

  public UriParseTreeVisitor(final Edm edm) {
    this.edm = edm;
    edmEntityContainer = edm.getEntityContainer(null);
  }

  /**
   * Simple percent decoding
   * @param encoded string
   * @return decoded string
   */
  String decode(String encoded) {
    try {
      return URLDecoder.decode(encoded, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw wrap(new UriParserSyntaxException("Error while decoding"));
    }
  }

  private FullQualifiedName getFullNameFromContext(final NamespaceContext vNS, final String odi) {
    String namespace = decode(vNS.getText());
    namespace = namespace.substring(0, namespace.length() - 1); // vNS contains a trailing point that has to be removed
    return new FullQualifiedName(namespace, odi);
  }

  private LambdaVariables getLambdaVar(final String odi) {
    for (LambdaVariables item : allowedLambdaVariables) {
      if (item.name.equals(odi)) {
        return item;
      }
    }
    return null;
  }

  private TypeInformation getTypeInformation(final UriResource lastResourcePart) {

    TypeInformation typeInformation = new TypeInformation();
    if (lastResourcePart instanceof UriResourceWithKeysImpl) {
      UriResourceWithKeysImpl lastPartWithKeys = (UriResourceWithKeysImpl) lastResourcePart;

      if (lastPartWithKeys.getTypeFilterOnEntry() != null) {
        typeInformation.type = lastPartWithKeys.getTypeFilterOnEntry();
      } else if (lastPartWithKeys.getTypeFilterOnCollection() != null) {
        typeInformation.type = lastPartWithKeys.getTypeFilterOnCollection();
      } else {
        typeInformation.type = lastPartWithKeys.getType();
      }
      typeInformation.isCollection = lastPartWithKeys.isCollection();

    } else if (lastResourcePart instanceof UriResourceTypedImpl) {
      UriResourceTypedImpl lastPartTyped = (UriResourceTypedImpl) lastResourcePart;

      if (lastPartTyped.getTypeFilter() != null) {
        typeInformation.type = lastPartTyped.getTypeFilter();
      } else {
        typeInformation.type = lastPartTyped.getType();
      }

      typeInformation.isCollection = lastPartTyped.isCollection();
    }

    return typeInformation;
  }

  public UriResourceTypedImpl readResourcePathSegment(final PathSegmentContext ctx) {

    boolean checkFirst = false;
    if (contextUriInfo.getLastResourcePart() == null ||
        contextUriInfo.getLastResourcePart() instanceof UriResourceRootImpl) {
      checkFirst = true;
    }

    String odi = decode(ctx.vODI.getText());

    if (checkFirst && ctx.vNS == null) {

      // check EntitySet
      EdmEntitySet edmEntitySet = edmEntityContainer.getEntitySet(odi);
      if (edmEntitySet != null) {
        UriResourceEntitySetImpl uriResource = new UriResourceEntitySetImpl()
            .setEntitSet(edmEntitySet);
        contextUriInfo.addResourcePart(uriResource);
        return null;
      }

      // check Singleton
      EdmSingleton edmSingleton = edmEntityContainer.getSingleton(odi);
      if (edmSingleton != null) {
        UriResourceSingletonImpl uriResource = new UriResourceSingletonImpl()
            .setSingleton(edmSingleton);
        contextUriInfo.addResourcePart(uriResource);
        return null;
      }

      // check ActionImport
      EdmActionImport edmActionImport = edmEntityContainer.getActionImport(odi);
      if (edmActionImport != null) {
        UriResourceActionImpl uriResource = new UriResourceActionImpl()
            .setActionImport(edmActionImport);
        contextUriInfo.addResourcePart(uriResource);
        return null;
      }

      // check FunctionImport
      EdmFunctionImport edmFunctionImport = edmEntityContainer.getFunctionImport(odi);
      if (edmFunctionImport != null) {

        // read the URI parameters
        this.contextReadingFunctionParameters = true;
        @SuppressWarnings("unchecked")
        List<UriParameterImpl> parameters = (List<UriParameterImpl>) ctx.vlNVO.get(0).accept(this);
        this.contextReadingFunctionParameters = false;

        // mark parameters as consumed
        ctx.vlNVO.remove(0);

        UriResourceFunctionImpl uriResource = new UriResourceFunctionImpl()
            .setFunctionImport(edmFunctionImport, parameters);

        // collect parameter names
        List<String> names = new ArrayList<String>();
        for (UriParameterImpl item : parameters) {
          names.add(item.getName());
        }

        // get function from function import
        EdmFunction function = edmFunctionImport.getFunction(names);
        if (function == null) {
          String tmp = "";
          for (String name : names) {
            tmp += (tmp.length() != 0 ? "," : "") + name;
          }
          throw wrap(new UriParserSemanticException("Function of functionimport '" + edmFunctionImport.getName()
              + "' with parameters [" + tmp + "] not found"));
        }

        uriResource.setFunction(edmFunctionImport.getFunction(names));
        contextUriInfo.addResourcePart(uriResource);
        return null;
      }
    }

    TypeInformation source = null;
    UriResource lastResourcePart = contextUriInfo.getLastResourcePart();

    if (lastResourcePart == null) {
      if (contextTypes.size() == 0) {
        throw wrap(new UriParserSemanticException("Resource part '" + odi + "' can only applied on typed "
            + "resource parts"));
      }
      source = contextTypes.peek();
    } else {
      source = getTypeInformation(lastResourcePart);

      if (source.type == null) {
        throw wrap(new UriParserSemanticException("Resource part '" + odi + "' can only applied on typed "
            + "resource parts"));
      }
    }

    if (ctx.vNS == null) { // without namespace

      // first check for lambda variable because a newly add property should not shadow a long used lambda variable
      LambdaVariables lVar = getLambdaVar(odi);
      if (lVar != null) {
        UriResourceLambdaVarImpl lambdaResource = new UriResourceLambdaVarImpl();
        lambdaResource.setVariableText(lVar.name);
        lambdaResource.setType(lVar.type);
        lambdaResource.setCollection(lVar.isCollection);
        contextUriInfo.addResourcePart(lambdaResource);
        return null;
      }

      if (!(source.type instanceof EdmStructuralType)) {
        throw wrap(new UriParserSemanticException("Can not parse'" + odi
            + "'Previous path segment not a structural type."));
      }

      EdmStructuralType structType = (EdmStructuralType) source.type;

      EdmElement property = structType.getProperty(odi);
      if (property == null) {
        throw wrap(new UriParserSemanticException("Property '" + odi + "' not found in type '"
            + structType.getNamespace() + "." + structType.getName() + "'"));
      }

      if (property instanceof EdmProperty) {
        if (((EdmProperty) property).isPrimitive() == true) {
          // create simple property
          UriResourcePrimitivePropertyImpl simpleResource = new UriResourcePrimitivePropertyImpl()
              .setProperty((EdmProperty) property);
          contextUriInfo.addResourcePart(simpleResource);
          return null;
        } else {
          // create complex property
          UriResourceComplexPropertyImpl complexResource = new UriResourceComplexPropertyImpl()
              .setProperty((EdmProperty) property);
          contextUriInfo.addResourcePart(complexResource);
          return null;
        }
      } else if (property instanceof EdmNavigationProperty) {
        // create navigation property
        UriResourceNavigationPropertyImpl navigationResource = new UriResourceNavigationPropertyImpl()
            .setNavigationProperty((EdmNavigationProperty) property);
        contextUriInfo.addResourcePart(navigationResource);
        return null;
      } else {
        throw wrap(new UriParserSemanticException("Unkown type for property '" + property + "'"));
      }

    } else { // with namespace

      FullQualifiedName fullFilterName = getFullNameFromContext(ctx.vNS, odi);

      // EdmType lastType = getLastType(lastTyped);

      if (source.type instanceof EdmEntityType) {

        EdmEntityType filterEntityType = edm.getEntityType(fullFilterName);
        if (filterEntityType != null) {
          // is entity type cast
          if (!(filterEntityType.compatibleTo(source.type))) {
            throw wrap(new UriParserSemanticException(
                "Entity typefilter not compatible to previous path segment: " + fullFilterName.toString()));
          }

          if (lastResourcePart == null) {
            // this may be the case if a member expression within a filter starts with a typeCast
            UriResourceStartingTypeFilterImpl uriResource = new UriResourceStartingTypeFilterImpl()
                .setType(source.type)
                .setCollection(source.isCollection);
            if (source.isCollection) {
              uriResource.setCollectionTypeFilter(filterEntityType);
            } else {
              uriResource.setEntryTypeFilter(filterEntityType);
            }
            contextUriInfo.addResourcePart(uriResource);
            return null;
          } else {

            // check if last segment may contain key properties
            if (lastResourcePart instanceof UriResourceWithKeysImpl) {
              UriResourceWithKeysImpl lastPartWithKeys = (UriResourceWithKeysImpl) lastResourcePart;

              if (lastPartWithKeys.isCollection() == false) {
                if (lastPartWithKeys.getTypeFilterOnEntry() != null) {
                  throw wrap(new UriParserSemanticException("Entry typefilters are not chainable, used '" +
                      getName(filterEntityType) + "' behind '" +
                      getName(lastPartWithKeys.getTypeFilterOnEntry()) + "'"));
                }
                lastPartWithKeys.setEntryTypeFilter(filterEntityType);
                return null;
              } else {
                if (lastPartWithKeys.getTypeFilterOnCollection() != null) {
                  throw wrap(new UriParserSemanticException("Collection typefilters are not chainable, used '" +
                      getName(filterEntityType) + "' behind '" +
                      getName(lastPartWithKeys.getTypeFilterOnCollection()) + "'"));
                }
                lastPartWithKeys.setCollectionTypeFilter(filterEntityType);
                return null;
              }
            } else if (lastResourcePart instanceof UriResourceTypedImpl) {
              UriResourceTypedImpl lastPartTyped = (UriResourceTypedImpl) lastResourcePart;
              if (lastPartTyped.getTypeFilter() != null) {
                throw wrap(new UriParserSemanticException("Typefilters are not chainable, used '" +
                    getName(filterEntityType) + "' behind '" +
                    getName(lastPartTyped.getTypeFilter()) + "'"));
              }

              lastPartTyped.setTypeFilter(filterEntityType);
              return null;
            } else {
              throw wrap(new UriParserSemanticException("Path segment before '" + getName(filterEntityType)
                  + "' not typed"));
            }
          }
        }

      } else if (source.type instanceof EdmComplexType) {

        EdmComplexType filterComplexType = edm.getComplexType(fullFilterName);

        if (filterComplexType != null) {

          // is complex type cast
          if (!(filterComplexType.compatibleTo(source.type))) {
            throw wrap(new UriParserSemanticException(
                "Complex typefilter '" + getName(source.type) + "'not compatible type of previous path segment '"
                    + getName(filterComplexType) + "'"));
          }

          // is simple complex type cast
          if (lastResourcePart == null) {
            // this may be the case if a member expression within a filter starts with a typeCast
            UriResourceStartingTypeFilterImpl uriResource = new UriResourceStartingTypeFilterImpl()
                .setType(source.type)
                .setCollection(source.isCollection);

            if (source.isCollection) {
              uriResource.setCollectionTypeFilter(filterComplexType);
            } else {
              uriResource.setEntryTypeFilter(filterComplexType);
            }
            contextUriInfo.addResourcePart(uriResource);
            return null;
          } else {
            if (lastResourcePart instanceof UriResourceWithKeysImpl) {
              // e.g. in case of function returning complex data or a list of complex data
              UriResourceWithKeysImpl lastPartWithKeys = (UriResourceWithKeysImpl) lastResourcePart;

              if (lastPartWithKeys.isCollection() == false) {
                if (lastPartWithKeys.getTypeFilterOnEntry() != null) {
                  throw wrap(new UriParserSemanticException("Entry typefilters are not chainable, used '" +
                      getName(filterComplexType) + "' behind '" +
                      getName(lastPartWithKeys.getTypeFilterOnEntry()) + "'"));
                }
                lastPartWithKeys.setEntryTypeFilter(filterComplexType);
                return null;
              } else {
                if (lastPartWithKeys.getTypeFilterOnCollection() != null) {
                  throw wrap(new UriParserSemanticException("Collection typefilters are not chainable, used '" +
                      getName(filterComplexType) + "' behind '" +
                      getName(lastPartWithKeys.getTypeFilterOnCollection()) + "'"));
                }
                lastPartWithKeys.setCollectionTypeFilter(filterComplexType);
                return null;
              }

            } else if (lastResourcePart instanceof UriResourceTypedImpl) {
              UriResourceTypedImpl lastPartTyped = (UriResourceTypedImpl) lastResourcePart;
              if (lastPartTyped.getTypeFilter() != null) {
                throw wrap(new UriParserSemanticException("Typefilters are not chainable, used '" +
                    getName(filterComplexType) + "' behind '" +
                    getName(lastPartTyped.getTypeFilter()) + "'"));
              }

              lastPartTyped.setTypeFilter(filterComplexType);
              return null;
            } else {
              throw wrap(new UriParserSemanticException("Path segment before '" + getName(filterComplexType)
                  + "' not typed"));
            }
          }
        }
      }

      FullQualifiedName fullBindingTypeName = new FullQualifiedName(source.type.getNamespace(), source.type.getName());

      // check for action
      EdmAction action = edm.getAction(fullFilterName, fullBindingTypeName, source.isCollection);
      if (action != null) {
        UriResourceActionImpl pathInfoAction = new UriResourceActionImpl();
        pathInfoAction.setAction(action);
        contextUriInfo.addResourcePart(pathInfoAction);
        return null;
      }

      // do a check for bound functions (which requires a parameter list)
      if (ctx.vlNVO.size() == 0) {
        throw wrap(new UriParserSemanticException("Expected function parameters for '" + fullBindingTypeName.toString()
            + "'"));
      }

      this.contextReadingFunctionParameters = true;
      @SuppressWarnings("unchecked")
      List<UriParameterImpl> parameters = (List<UriParameterImpl>) ctx.vlNVO.get(0).accept(this);
      this.contextReadingFunctionParameters = false;

      // get names of function parameters
      List<String> names = new ArrayList<String>();
      for (UriParameterImpl item : parameters) {
        names.add(item.getName());
      }

      EdmFunction function = edm.getFunction(fullFilterName, fullBindingTypeName, source.isCollection, names);

      if (function != null) {
        UriResourceFunctionImpl pathInfoFunction = new UriResourceFunctionImpl()
            .setFunction(function)
            .setParameters(parameters);
        contextUriInfo.addResourcePart(pathInfoFunction);

        // mark parameters as consumed
        ctx.vlNVO.remove(0);
        return null;
      }

      // check for unbound function in the $filter case ( where the previous resource segment is a $it)
      function = edm.getFunction(fullFilterName, null, null, names);

      if (function != null) {
        UriResourceFunctionImpl pathInfoFunction = new UriResourceFunctionImpl()
            .setFunction(function)
            .setParameters(parameters);
        contextUriInfo.addResourcePart(pathInfoFunction);

        // mark parameters as consumed
        ctx.vlNVO.remove(0);
        return null;
      }

      throw wrap(new UriParserSemanticException("Unknown resource path segment:" + fullFilterName.toString()));
    }
  }

  private String getName(EdmType type) {
    return type.getNamespace() + "." + type.getName();
  }

  @Override
  public Object visitAliasAndValue(final AliasAndValueContext ctx) {
    AliasQueryOptionImpl alias = new AliasQueryOptionImpl();
    alias.setName(decode(ctx.vODI.getText()));
    alias.setText(decode(ctx.vV.getText()));
    alias.setAliasValue((ExpressionImpl) ctx.vV.accept(this));
    return alias;
  }

  @Override
  public Object visitAllExpr(final AllExprContext ctx) {
    UriResourceLambdaAllImpl all = new UriResourceLambdaAllImpl();

    UriResource obj = contextUriInfo.getLastResourcePart();
    if (!(obj instanceof UriResourcePartTyped)) {
      throw wrap(new UriParserSemanticException("any only allowed on typed path segments"));
    }

    LambdaVariables var = new LambdaVariables();
    var.name = decode(ctx.vLV.getText());
    var.type = getTypeInformation((UriResourceImpl) obj).type;
    var.isCollection = false;

    all.setLamdaVariable(decode(ctx.vLV.getText()));
    allowedLambdaVariables.push(var);
    all.setExpression((ExpressionImpl) ctx.vLE.accept(this));
    allowedLambdaVariables.pop();
    return all;
  }

  @Override
  public ExpressionImpl visitAltAdd(final AltAddContext ctx) {
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
  public Object visitAltAll(final AltAllContext ctx) {
    UriInfoImpl uriInfoImplpath = new UriInfoImpl().setKind(UriInfoKind.resource);

    uriInfoImplpath.addResourcePart((UriResourceImpl) super.visitAltAll(ctx));

    return new MemberImpl()
        .setPath(uriInfoImplpath);
  }

  @Override
  public ExpressionImpl visitAltAnd(final AltAndContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    binary.setOperator(SupportedBinaryOperators.AND);
    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));

    return binary;
  }

  @Override
  public Object visitAltAny(final AltAnyContext ctx) {
    UriInfoImpl uriInfoImplpath = new UriInfoImpl().setKind(UriInfoKind.resource);

    uriInfoImplpath.addResourcePart((UriResourceImpl) super.visitAltAny(ctx));

    return new MemberImpl()
        .setPath(uriInfoImplpath);
  }

  @Override
  public Object visitAltBatch(final AltBatchContext ctx) {
    contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.batch);
    return null;
  }

  @Override
  public ExpressionImpl visitAltComparism(final AltComparismContext ctx) {
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
    }

    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));
    return binary;
  }

  @Override
  public Object visitAltEntity(final AltEntityContext ctx) {
    UriInfoImpl uriInfo = new UriInfoImpl().setKind(UriInfoKind.entityId);

    @SuppressWarnings("unchecked")
    List<QueryOptionImpl> list = (List<QueryOptionImpl>) ctx.vEO.accept(this);
    uriInfo.setQueryOptions(list);

    contextUriInfo = uriInfo;
    return null;
  }

  @Override
  public Object visitAltEntityCast(final AltEntityCastContext ctx) {
    UriInfoImpl uriInfo = new UriInfoImpl().setKind(UriInfoKind.entityId);

    String odi = decode(ctx.vODI.getText());
    FullQualifiedName fullName = getFullNameFromContext(ctx.vNS, odi);

    EdmEntityType type = edm.getEntityType(fullName);
    if (type == null) {
      throw wrap(new UriParserSemanticException("Expected EntityTypeName"));
    }
    uriInfo.setEntityTypeCast(type);

    contextUriInfo = uriInfo;
    contextTypes.push(new TypeInformation(uriInfo.getEntityTypeCast(), true));

    @SuppressWarnings("unchecked")
    List<QueryOptionImpl> list = (List<QueryOptionImpl>) ctx.vEO.accept(this);
    uriInfo.setQueryOptions(list);

    return null;
  }

  @Override
  public ExpressionImpl visitAltEquality(final AltEqualityContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    int tokenIndex = ctx.vO.getType();

    if (tokenIndex == UriLexer.EQ_ALPHA) {
      binary.setOperator(SupportedBinaryOperators.EQ);
    } else {
      binary.setOperator(SupportedBinaryOperators.NE);
    }
    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));

    return binary;
  }

  @Override
  public Object visitAltHas(final AltHasContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    binary.setOperator(SupportedBinaryOperators.HAS);
    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));

    return binary;
  }

  @Override
  public Object visitAltMetadata(final AltMetadataContext ctx) {
    UriInfoImpl uriInfo = new UriInfoImpl().setKind(UriInfoKind.metadata);

    if (ctx.vF != null) {
      FormatOptionImpl format = (FormatOptionImpl) ctx.vF.accept(this);
      uriInfo.setSystemQueryOption(format);
    }

    if (ctx.vCF != null) {
      uriInfo.setFragment(decode(ctx.vCF.getText()));
    }

    contextUriInfo = uriInfo;
    return null;
  }

  @Override
  public ExpressionImpl visitAltMult(final AltMultContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    int tokenIndex = ctx.vO.getType();

    if (tokenIndex == UriLexer.MUL) {
      binary.setOperator(SupportedBinaryOperators.MUL);
    } else if (tokenIndex == UriLexer.DIV) {
      binary.setOperator(SupportedBinaryOperators.DIV);
    } else {
      binary.setOperator(SupportedBinaryOperators.MOD);
    }
    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));

    return binary;
  }

  @Override
  public ExpressionImpl visitAltOr(final AltOrContext ctx) {
    BinaryImpl binary = new BinaryImpl();

    binary.setOperator(SupportedBinaryOperators.OR);
    binary.setLeftOperand((ExpressionImpl) ctx.vE1.accept(this));
    binary.setRightOperand((ExpressionImpl) ctx.vE2.accept(this));

    return binary;
  }

  @Override
  public Object visitAltResourcePath(final AltResourcePathContext ctx) {
    ctx.vRP.accept(this);

    if (ctx.vQO != null) {
      UriResource lastSegment = contextUriInfo.getLastResourcePart();

      if (lastSegment instanceof UriResourcePartTyped) {
        UriResourcePartTyped typed = (UriResourcePartTyped) lastSegment;
        contextTypes.push(new TypeInformation(getTypeInformation((UriResourceImpl) typed).type, typed.isCollection()));
      }
      @SuppressWarnings("unchecked")
      List<QueryOptionImpl> list = (List<QueryOptionImpl>) ctx.vQO.accept(this);
      contextUriInfo.setQueryOptions(list);
    }
    return null;
  }

  @Override
  public Object visitAnyExpr(final AnyExprContext ctx) {
    UriResourceLambdaAnyImpl any = new UriResourceLambdaAnyImpl();
    if (ctx.vLV != null) {
      // TODO
      UriResourceImpl lastResourcePart = (UriResourceImpl) contextUriInfo.getLastResourcePart();
      if (!(lastResourcePart instanceof UriResourcePartTyped)) {
        throw wrap(new UriParserSemanticException("any only allowed on typed path segments"));
      }

      LambdaVariables var = new LambdaVariables();
      var.name = decode(ctx.vLV.getText());
      var.type = getTypeInformation(lastResourcePart).type;
      var.isCollection = false;

      any.setLamdaVariable(decode(ctx.vLV.getText()));
      allowedLambdaVariables.push(var);
      any.setExpression((ExpressionImpl) ctx.vLE.accept(this));
      allowedLambdaVariables.pop();
    }
    return any;
  }

  @Override
  public Object visitBooleanNonCase(BooleanNonCaseContext ctx) {
    String text = decode(ctx.getText()).toLowerCase();

    if (text.equals("false")) {
      return new ConstantImpl().setKind(SupportedConstants.FALSE);
    }
    return new ConstantImpl().setKind(SupportedConstants.TRUE);
  }

  @Override
  public ExpressionImpl visitCastExpr(final CastExprContext ctx) {
    MethodCallImpl method = new MethodCallImpl();
    if (ctx.vE1 != null) {
      // is optional parameter
      ExpressionImpl onExpression = (ExpressionImpl) ctx.vE1.accept(this);
      method.addParameter(onExpression);
    }

    String namespace = decode(ctx.vNS.getText());
    namespace = namespace.substring(0, namespace.length() - 1);

    FullQualifiedName fullName = new FullQualifiedName(namespace, decode(ctx.vODI.getText()));
    EdmType type = getType(fullName);
    method.setMethod(SupportedMethodCalls.CAST);
    method.addParameter(new TypeLiteralImpl().setType(type));
    return method;
  }

  private EdmType getType(final FullQualifiedName fullName) {
    EdmType type = null;

    type = edm.getEntityType(fullName);
    if (type != null) {
      return type;
    }

    type = edm.getComplexType(fullName);
    if (type != null) {
      return type;
    }

    type = edm.getEnumType(fullName);
    if (type != null) {
      return type;
    }

    if (fullName.getNamespace().equals("Edm")) {
      EdmPrimitiveTypeKind typeKind = EdmPrimitiveTypeKind.valueOf(fullName.getName());
      type = typeKind.getEdmPrimitiveTypeInstance();
      if (type != null) {
        return type;
      }
    }

    return null;

  }

  @Override
  public ExpressionImpl visitCeilingMethodCallExpr(final CeilingMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.CEILING)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitConcatMethodCallExpr(final ConcatMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.CONCAT)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public Object visitConstSegment(final ConstSegmentContext ctx) {
    UriInfoImpl uriInfoResource = contextUriInfo;
    UriResource pathInfo = uriInfoResource.getLastResourcePart();

    if (ctx.vV != null) {
      if (pathInfo instanceof UriResourcePartTyped) {
        if (!((UriResourcePartTyped) pathInfo).isCollection()) {
          contextUriInfo.addResourcePart(new UriResourceValueImpl());
        } else {
          throw wrap(new UriParserSemanticException("$value only allowed on typed path segments"));
        }
        return null;
      } else {
        throw wrap(new UriParserSemanticException("$value only allowed on typed path segments"));
      }

    } else if (ctx.vC != null) {
      if (pathInfo instanceof UriResourcePartTyped) {
        if (((UriResourcePartTyped) pathInfo).isCollection()) {
          contextUriInfo.addResourcePart(new UriResourceCountImpl());
        } else {
          throw wrap(new UriParserSemanticException("$count only allowed on collection properties"));
        }
      } else {
        throw wrap(new UriParserSemanticException("$count only allowed on typed properties"));
      }
    } else if (ctx.vR != null) {
      if (pathInfo instanceof UriResourcePartTyped) {
        EdmType type = ((UriResourcePartTyped) pathInfo).getType();
        if (type instanceof EdmEntityType) {
          contextUriInfo.addResourcePart(new UriResourceRefImpl());
        } else {
          throw wrap(new UriParserSemanticException("$ref only allowd on endity types"));
        }
      } else {
        throw wrap(new UriParserSemanticException("$ref only allowed on typed properties"));
      }

    } else if (ctx.vAll != null) {
      contextUriInfo.addResourcePart((UriResourceLambdaAllImpl) ctx.vAll.accept(this));
    } else if (ctx.vAny != null) {
      contextUriInfo.addResourcePart((UriResourceLambdaAnyImpl) ctx.vAny.accept(this));
    }
    return null;
  }

  @Override
  public ExpressionImpl visitContainsMethodCallExpr(final ContainsMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.CONTAINS)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public Object visitCrossjoin(final CrossjoinContext ctx) {
    UriInfoImpl crossJoin = new UriInfoImpl().setKind(UriInfoKind.crossjoin);

    for (OdataIdentifierContext obj : ctx.vlODI) {
      crossJoin.addEntitySetName(decode(obj.getText()));
    }

    contextUriInfo = crossJoin;
    return null;
  }

  @Override
  public Object visitCustomQueryOption(final CustomQueryOptionContext ctx) {
    CustomQueryOptionImpl queryOption = new CustomQueryOptionImpl();
    queryOption.setName(decode(ctx.getChild(0).getText()));

    // set value only if present
    if (ctx.getChildCount() > 1) {
      queryOption.setText(decode(ctx.getChild(2).getText()));
    }

    return queryOption;
  }

  @Override
  public Object visitDateMethodCallExpr(final DateMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.DATE)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitDayMethodCallExpr(final DayMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.DAY)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitGeoDistanceMethodCallExpr(final GeoDistanceMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.GEODISTANCE)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public Object visitEndsWithMethodCallExpr(final EndsWithMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.ENDSWITH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public Object visitEntityOptions(final EntityOptionsContext ctx) {
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
  public Object visitEntityOptionsCast(final EntityOptionsCastContext ctx) {
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
  public Object visitEnumLit(final EnumLitContext ctx) {
    EnumerationImpl enum1 = new EnumerationImpl();

    // get type
    String odi = decode(ctx.vODI.getText());

    FullQualifiedName fullName = getFullNameFromContext(ctx.vNS, odi);
    EdmEnumType edmEnumType = edm.getEnumType(fullName);

    enum1.setType(edmEnumType);

    String valueString = decode(ctx.vValues.getText());
    valueString = valueString.substring(1, valueString.length() - 1);

    String[] values = valueString.split(",");
    for (String item : values) {
      enum1.addValue(item);
    }

    return enum1;
  }

  @Override
  public Object visitExpand(final ExpandContext ctx) {
    ExpandOptionImpl expand = new ExpandOptionImpl();
    expand.setText(decode(ctx.getChild(2).getText()));
    for (ExpandItemContext eI : ctx.vlEI) {
      expand.addExpandItem((ExpandItemImpl) eI.accept(this));
    }

    return expand;
  }

  @Override
  public Object visitExpandItem(final ExpandItemContext ctx) {

    ExpandItemImpl expandItem = null;
    if (ctx.vS != null) {
      expandItem = new ExpandItemImpl().setIsStar(true);
      if (ctx.vR != null) {
        expandItem.setIsRef(true);
      } else if (ctx.vM != null) {
        LevelsOptionImpl levels = new LevelsOptionImpl().setMax();
        levels.setText(decode(ctx.vM.getText()));
        expandItem.setSystemQueryOption(levels);
      } else if (ctx.vL != null) {
        // TODO set value as integer
        LevelsOptionImpl levels = new LevelsOptionImpl().setMax();
        levels.setText(decode(ctx.vL.getText()));
        expandItem.setSystemQueryOption(levels);
      }

    } else if (ctx.vEP != null) {
      expandItem = (ExpandItemImpl) ctx.vEP.accept(this);

      if (ctx.vEPE != null) {
        ExpandItemImpl contextExpandItemPathBU = contextExpandItemPath;
        contextExpandItemPath = expandItem;

        @SuppressWarnings("unchecked")
        List<SystemQueryOptionImpl> list = (List<SystemQueryOptionImpl>) ctx.vEPE.accept(this);
        for (SystemQueryOptionImpl option : list) {
          expandItem.setSystemQueryOption(option);
        }
        contextExpandItemPath = contextExpandItemPathBU;
      }
    }

    return expandItem;

  }

  @Override
  public Object visitExpandPath(final ExpandPathContext ctx) {
    ExpandItemImpl expandItem = new ExpandItemImpl();

    // UriResourceItImpl pathInfoIT = new UriResourceItImpl();

    contextUriInfo.getLastResourcePart();

    // save context
    ExpandItemImpl contextExpandItemPathBU = contextExpandItemPath;
    UriInfoImpl uriInfoResourceBU = contextUriInfo;

    // set tmp context
    contextExpandItemPath = expandItem;
    contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.resource);
    // contextUriInfo.addPathInfo(pathInfoIT);

    super.visitExpandPath(ctx);
    expandItem.setResourceInfo(contextUriInfo);

    // reset context
    contextUriInfo = uriInfoResourceBU;
    contextExpandItemPath = contextExpandItemPathBU;

    return expandItem;
  }

  @Override
  public Object visitExpandPathExtension(final ExpandPathExtensionContext ctx) {
    List<SystemQueryOptionImpl> list = new ArrayList<SystemQueryOptionImpl>();

    EdmType targetType = null;
    boolean isColl = false;
    if (contextExpandItemPath == null) {
      // use the type of the last resource path segement
      UriResourceTypedImpl lastSegment = (UriResourceTypedImpl) contextUriInfo.getLastResourcePart();
      targetType = getTypeInformation(lastSegment).type;
      isColl = lastSegment.isCollection();
    } else {
      if (contextExpandItemPath.getResourceInfo() == null) {
        // use the type of the last resource path segement
        UriResourceTypedImpl lastSegment = (UriResourceTypedImpl) contextUriInfo.getLastResourcePart();
        targetType = getTypeInformation(lastSegment).type;
        isColl = lastSegment.isCollection();
      } else {
        // use the type of the last ''expand'' path segement
        UriInfoImpl info = (UriInfoImpl) contextExpandItemPath.getResourceInfo();
        targetType = getTypeInformation((UriResourceImpl) info.getLastResourcePart()).type;
        isColl = ((UriResourcePartTyped) info.getLastResourcePart()).isCollection();
      }
    }

    contextTypes.push(new TypeInformation(targetType, isColl));

    if (ctx.vC != null) {
      UriInfoImpl resourcePath = (UriInfoImpl) contextExpandItemPath.getResourceInfo();
      resourcePath.addResourcePart(new UriResourceCountImpl());

      for (ExpandCountOptionContext s : ctx.vlEOC) {
        list.add((SystemQueryOptionImpl) s.accept(this));
      }
    } else if (ctx.vR != null) {
      UriInfoImpl resourcePath = (UriInfoImpl) contextExpandItemPath.getResourceInfo();
      resourcePath.addResourcePart(new UriResourceRefImpl());

      for (ExpandRefOptionContext s : ctx.vlEOR) {
        list.add((SystemQueryOptionImpl) s.accept(this));
      }
    } else {
      for (ExpandOptionContext s : ctx.vlEO) {
        list.add((SystemQueryOptionImpl) s.accept(this));
      }
    }

    contextTypes.pop();
    return list;

  }

  @Override
  public Object visitFilter(final FilterContext ctx) {

    FilterOptionImpl filter = new FilterOptionImpl().setExpression((ExpressionImpl) ctx.children.get(2).accept(this));
    return filter;
  }

  @Override
  public ExpressionImpl visitFloorMethodCallExpr(final FloorMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.FLOOR)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitFormat(final FormatContext ctx) {
    FormatOptionImpl format = new FormatOptionImpl();

    TerminalNodeImpl c2 = (TerminalNodeImpl) ctx.children.get(2);
    if (c2.symbol.getType() == UriLexer.ATOM) {
      format.setFormat("atom");
    } else if (c2.symbol.getType() == UriLexer.JSON) {
      format.setFormat("json");
    } else if (c2.symbol.getType() == UriLexer.XML) {
      format.setFormat("xml");
    } else if (c2.symbol.getType() == UriLexer.PCHARS) {
      if (ctx.getChildCount() == 2) {
        format.setFormat(decode(c2.getText()));
      } else {
        format.setFormat(decode(c2.getText()) + "/" + decode(ctx.children.get(4).getText()));
      }
    }
    String text = decode(ctx.children.get(2).getText());
    if (ctx.getChildCount() > 4) {
      text += decode(ctx.children.get(3).getText());
      text += decode(ctx.children.get(4).getText());
    }

    format.setText(text);

    return format;
  }

  @Override
  public ExpressionImpl visitFractionalsecondsMethodCallExpr(final FractionalsecondsMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.FRACTIONALSECONDS)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitGeoLengthMethodCallExpr(final GeoLengthMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.GEOLENGTH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitHourMethodCallExpr(final HourMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.HOUR)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitId(final IdContext ctx) {
    IdOptionImpl id = new IdOptionImpl();

    String text = decode(ctx.children.get(2).getText());

    return id.setValue(text).setText(text);
  }

  @Override
  public ExpressionImpl visitIndexOfMethodCallExpr(final IndexOfMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.INDEXOF)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public Object visitInlinecount(final InlinecountContext ctx) {
    InlineCountOptionImpl inlineCount = new InlineCountOptionImpl();

    String text = decode(ctx.children.get(2).getText());

    return inlineCount.setValue(text.equals("true") ? true : false).setText(text);
  }

  @Override
  public ExpressionImpl visitGeoIntersectsMethodCallExpr(final GeoIntersectsMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.GEOINTERSECTS)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public ExpressionImpl visitIsofExpr(final IsofExprContext ctx) {
    MethodCallImpl method = new MethodCallImpl();
    if (ctx.vE1 != null) {
      ExpressionImpl onExpression = (ExpressionImpl) ctx.vE1.accept(this);
      method.addParameter(onExpression);
    }

    /* TODO improve coding */
    String namespace = decode(ctx.vNS.getText());
    namespace = namespace.substring(0, namespace.length() - 1);

    FullQualifiedName fullName = new FullQualifiedName(namespace, decode(ctx.vODI.getText()));
    EdmType type = getType(fullName);
    method.setMethod(SupportedMethodCalls.ISOF);
    method.addParameter(new TypeLiteralImpl().setType(type));

    return method;
  }

  @Override
  public ExpressionImpl visitLengthMethodCallExpr(final LengthMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.LENGTH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitLevels(LevelsContext ctx) {

    LevelsOptionImpl levels = new LevelsOptionImpl();

    String text = decode(ctx.children.get(2).getText());

    if (text.equals("max")) {
      levels.setMax();
    }
    levels.setText(text);
    // TODO set value as integer

    return levels;

  }

  @Override
  public ExpressionImpl visitMaxDateTimeMethodCallExpr(final MaxDateTimeMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.MAXDATETIME);
  }

  @Override
  public Object visitMemberExpr(final MemberExprContext ctx) {

    UriInfoImpl uriInfoImplpath = new UriInfoImpl().setKind(UriInfoKind.resource);

    TypeInformation lastTypeInfo = contextTypes.peek();

    if (ctx.vIt != null || ctx.vIts != null) {
      UriResourceItImpl pathInfoIT = new UriResourceItImpl();
      pathInfoIT.setType(lastTypeInfo.type);
      pathInfoIT.setCollection(lastTypeInfo.isCollection);
      uriInfoImplpath.addResourcePart(pathInfoIT);
    }

    if (ctx.vPs != null) {
      // save the context
      UriInfoImpl backupUriInfoPath = contextUriInfo;

      // set temporary uriInfoPath
      contextUriInfo = uriInfoImplpath;

      ctx.vPs.accept(this);

      // reset context
      contextUriInfo = backupUriInfoPath;
    }

    if (ctx.vALL != null) {
      uriInfoImplpath.addResourcePart((UriResourceImpl) ctx.vALL.accept(this));
    }
    if (ctx.vANY != null) {
      uriInfoImplpath.addResourcePart((UriResourceImpl) ctx.vANY.accept(this));
    }

    return new MemberImpl()
        .setPath(uriInfoImplpath);
  }

  @Override
  public ExpressionImpl visitMinDateTimeMethodCallExpr(final MinDateTimeMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.MINDATETIME);
  }

  @Override
  public ExpressionImpl visitMinuteMethodCallExpr(final MinuteMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.MINUTE)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitMonthMethodCallExpr(final MonthMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.MONTH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitNameValueOptList(final NameValueOptListContext ctx) {
    if (ctx.vVO != null) {

      // is single key predicate without a name
      String valueText = decode(ctx.vVO.vV.getText());
      ExpressionImpl expression = null;
      try {
        expression = (ExpressionImpl) ctx.vVO.vV.accept(this);
      } catch (Exception ex) {
        throw wrap(new UriParserSemanticException("Invalid key value: " + valueText));
      }

      // get type of last resource part
      UriResource last = contextUriInfo.getLastResourcePart();
      if (!(last instanceof UriResourcePartTyped)) {
        throw wrap(new UriParserSemanticException("Paramterslist on untyped resource path segement not allowed"));
      }
      EdmEntityType lastType = (EdmEntityType) ((UriResourcePartTyped) last).getType();

      // get list of keys for lastType
      List<String> lastKeyPredicates = lastType.getKeyPredicateNames();

      // if there is exactly one key defined in the EDM, then this key the the key written in the URI,
      // so fill the keylist with this key and return
      if (lastKeyPredicates.size() == 1) {
        String keyName = lastKeyPredicates.get(0);
        List<UriParameterImpl> list = new ArrayList<UriParameterImpl>();
        list.add(new UriParameterImpl().setName(keyName).setText(valueText).setExpression(expression));
        return list;
      }

      // There are more keys defined in the EDM, but only one is written in the URI. This is allowed only if
      // referential constrains are defined on this navigation property which can be used to will up all required
      // key.

      // for using referential constrains the last resource part must be a navigation property
      if (!(contextUriInfo.getLastResourcePart() instanceof UriResourceNavigationPropertyImpl)) {
        throw wrap(new UriParserSemanticException("Not enougth keyproperties defined"));
      }
      UriResourceNavigationPropertyImpl lastNav = (UriResourceNavigationPropertyImpl) last;

      // get the partner of the navigation property
      EdmNavigationProperty partner = lastNav.getProperty().getPartner();
      if (partner == null) {
        throw wrap(new UriParserSemanticException("Not enougth keyproperties defined"));
      }

      // create the keylist
      List<UriParameterImpl> list = new ArrayList<UriParameterImpl>();

      // find the key not filled by referential constrains and collect the other keys filled by
      // referential constrains
      String missedKey = null;
      for (String item : lastKeyPredicates) {
        String property = partner.getReferencingPropertyName(item);
        if (property != null) {
          list.add(new UriParameterImpl().setName(item).setRefencedProperty(property));
        } else {
          if (missedKey == null) {
            missedKey = item;
          } else {
            // two of more keys are missing
            throw wrap(new UriParserSemanticException("Not enougth referntial contrains defined"));
          }
        }
      }

      // the missing key is the one which is defined in the URI
      list.add(new UriParameterImpl().setName(missedKey).setText(valueText).setExpression(expression));

      return list;
    } else if (ctx.vNVL != null) {

      List<UriParameterImpl> list = new ArrayList<UriParameterImpl>();

      for (ParseTree c : ctx.vNVL.vlNVP) {
        list.add((UriParameterImpl) c.accept(this));
      }

      if (contextReadingFunctionParameters) {
        return list;
      }

      UriResource last = contextUriInfo.getLastResourcePart();
      // if the last resource part is a function
      /*
       * if (last instanceof UriResourceFunctionImpl) {
       * UriResourceFunctionImpl function = (UriResourceFunctionImpl) last;
       * if (!function.isParameterListFilled()) {
       * return list;
       * }
       * }
       */

      // get type of last resource part
      if (!(last instanceof UriResourcePartTyped)) {
        throw wrap(new UriParserSemanticException("Parameterslist on untyped resource path segement not allowed"));
      }
      EdmEntityType lastType = (EdmEntityType) ((UriResourcePartTyped) last).getType();

      // get list of keys for lastType
      List<String> lastKeyPredicates = lastType.getKeyPredicateNames();

      // check if all key are filled from the URI
      if (list.size() == lastKeyPredicates.size()) {
        return list;
      }

      // if not, check if the missing key predicates can be satisfied with help of the defined referential constrains

      // for using referential constrains the last resource part must be a navigation property
      if (!(contextUriInfo.getLastResourcePart() instanceof UriResourceNavigationPropertyImpl)) {
        throw wrap(new UriParserSemanticException("Not enougth keyproperties defined"));
      }
      UriResourceNavigationPropertyImpl lastNav = (UriResourceNavigationPropertyImpl) last;

      // get the partner of the navigation property
      EdmNavigationProperty partner = lastNav.getProperty().getPartner();
      if (partner == null) {
        throw wrap(new UriParserSemanticException("Not enougth keyproperties defined"));
      }

      // fill missing keys from referential constrains
      for (String key : lastKeyPredicates) {
        boolean found = false;
        for (UriParameterImpl item : list) {
          if (item.getName().equals(key)) {
            found = true;
            break;
          }
        }

        if (!found) {
          String property = partner.getReferencingPropertyName(key);
          if (property != null) {
            // store the key name as referenced property
            list.add(0, new UriParameterImpl().setName(key).setRefencedProperty(property));
          }
        }
      }

      // check again if all keyPredicate are filled from the URI
      if (list.size() == lastKeyPredicates.size()) {
        return list;
      }

      throw wrap(new UriParserSemanticException("Not enougth keyproperties defined"));
    }
    return new ArrayList<String>();
  }

  @Override
  public UriParameterImpl visitNameValuePair(final NameValuePairContext ctx) {
    UriParameterImpl uriParameter = new UriParameterImpl();
    uriParameter.setName(decode(ctx.vODI.getText()));

    if (ctx.vCOM != null) {
      uriParameter.setText(decode(ctx.vCOM.getText()));
      uriParameter.setExpression((ExpressionImpl) ctx.vCOM.accept(this));
    } else {
      uriParameter.setAlias(decode(ctx.vALI.getText()));
    }

    return uriParameter;
  }

  @Override
  public ExpressionImpl visitNowMethodCallExpr(final NowMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.NOW);
  }

  @Override
  public Object visitNullrule(NullruleContext ctx) {
    return new ConstantImpl().setKind(SupportedConstants.NULL);
  }

  @Override
  public Object visitOdataRelativeUriEOF(final OdataRelativeUriEOFContext ctx) {
    contextUriInfo = null;
    super.visitOdataRelativeUriEOF(ctx);
    return contextUriInfo;
  }

  @Override
  public Object visitOrderBy(final OrderByContext ctx) {

    OrderByOptionImpl orderBy = new OrderByOptionImpl();

    for (OrderByItemContext item : ctx.vlOI) {
      OrderByItemImpl oItem = (OrderByItemImpl) item.accept(this);
      orderBy.addOrder(oItem);
    }

    return orderBy;
  }

  @Override
  public Object visitOrderByItem(OrderByItemContext ctx) {
    OrderByItemImpl oItem = new OrderByItemImpl();
    if (ctx.vD != null) {
      oItem.setDescending(true);
    }

    oItem.setExpression((ExpressionImpl) ctx.vC.accept(this));
    return oItem;
  }

  @Override
  public Object visitPathSegment(final PathSegmentContext ctx) {
    readResourcePathSegment(ctx);
    /*
     * if (contextUriInfo.getLastResourcePart() == null ||
     * contextUriInfo.getLastResourcePart() instanceof UriResourceRootImpl) {
     * 
     * } else {
     * readNextPathInfoSegment(ctx);
     * }
     */
    UriResourceImpl pathInfoSegment = (UriResourceImpl) contextUriInfo.getLastResourcePart();

    if (ctx.vlNVO.size() > 0) {
      // check for keyPredicates
      if (pathInfoSegment instanceof UriResourceWithKeysImpl) {
        @SuppressWarnings("unchecked")
        List<UriParameterImpl> list = (List<UriParameterImpl>) ctx.vlNVO.get(0).accept(this);
        ((UriResourceWithKeysImpl) pathInfoSegment)
            .setKeyPredicates(list);
      } else {
        throw wrap(new UriParserSemanticException("Key properties not allowed"));
        // throw UriSemanticError.addKrepredicatesNotAllowed();
      }
    }

    return pathInfoSegment;
  }

  @Override
  public Object visitPathSegments(final PathSegmentsContext ctx) {
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
  public Object visitPrimitiveLiteral(final PrimitiveLiteralContext ctx) {
    ParseTree child1 = ctx.children.get(0);

    if (child1 instanceof EnumLitContext ||
        child1 instanceof BooleanNonCaseContext ||
        child1 instanceof NullruleContext) {
      return child1.accept(this);
    }
    return new LiteralImpl().setText(decode(ctx.getText()));
  }

  @Override
  public Object visitQueryOptions(final QueryOptionsContext ctx) {

    List<QueryOptionImpl> qpList = new ArrayList<QueryOptionImpl>();
    for (QueryOptionContext entityOption : ctx.vlQO) {
      qpList.add((QueryOptionImpl) entityOption.accept(this));
    }

    return qpList;
  }

  @Override
  public Object visitResourcePath(final ResourcePathContext ctx) {
    if (ctx.vAll != null) {
      contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.all);
    } else if (ctx.vCJ != null) {
      ctx.vCJ.accept(this);
    } else if (ctx.vlPS != null) {
      UriInfoImpl uriInfoPath = new UriInfoImpl().setKind(UriInfoKind.resource);
      contextUriInfo = uriInfoPath;
      super.visitResourcePath(ctx); // visit all children of ctx
    }
    return contextUriInfo;
  }

  @Override
  public Object visitRootExpr(final RootExprContext ctx) {

    UriResource lastResource = contextUriInfo.getLastResourcePart();

    if (!(lastResource instanceof UriResourcePartTyped)) {
      throw wrap(new UriParserSemanticException("Resource path not typed"));
    }

    UriResourcePartTyped lastType = (UriResourcePartTyped) lastResource;

    UriResourceRootImpl pathInfoRoot = new UriResourceRootImpl();
    pathInfoRoot.setCollection(lastType.isCollection());
    pathInfoRoot.setType(getTypeInformation((UriResourceImpl) lastType).type);

    UriInfoImpl uriInfoImplpath = new UriInfoImpl().setKind(UriInfoKind.resource);
    uriInfoImplpath.addResourcePart(pathInfoRoot);

    if (ctx.vPs != null) {
      // store the context uriInfoPath
      UriInfoImpl backupUriInfoPath = contextUriInfo;

      // set temporary uriInfoPath to collect the path information of the memberExpression
      contextUriInfo = uriInfoImplpath;

      ctx.vPs.accept(this);

      contextUriInfo = backupUriInfoPath;

    }
    return new MemberImpl()
        .setPath(uriInfoImplpath);

  }

  @Override
  public ExpressionImpl visitRoundMethodCallExpr(final RoundMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.ROUND)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitSecondMethodCallExpr(final SecondMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.SECOND)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitSelect(final SelectContext ctx) {
    List<SelectItemImpl> selectItems = new ArrayList<SelectItemImpl>();

    for (SelectItemContext si : ctx.vlSI) {
      selectItems.add((SelectItemImpl) si.accept(this));
    }

    return new SelectOptionImpl().setSelectItems(selectItems).setText(decode(ctx.children.get(2).getText()));
  }

  @Override
  public Object visitSelectItem(final SelectItemContext ctx) {
    SelectItemImpl selectItem = new SelectItemImpl();

    contextSelectItem = selectItem;
    for (SelectSegmentContext si : ctx.vlSS) {
      si.accept(this);
    }
    contextSelectItem = null;

    return selectItem;
  }

  @Override
  public Object visitSelectSegment(final SelectSegmentContext ctx) {

    if (ctx.vS != null) {
      if (ctx.vNS != null) {
        String namespace = decode(ctx.vNS.getText());
        namespace = namespace.substring(0, namespace.length() - 1);
        FullQualifiedName fullName = new FullQualifiedName(namespace, "*");
        contextSelectItem.addAllOperationsInSchema(fullName);
      } else {
        contextSelectItem.setStar(true);
      }
      return null;
    }

    String odi = decode(decode(ctx.vODI.getText()));
    if (ctx.vNS == null) {

      EdmType prevType = null;
      if (contextSelectItem.getResourceInfo() == null) {
        prevType = contextTypes.peek().type;
      } else {
        UriInfoImpl uriInfo = (UriInfoImpl) contextSelectItem.getResourceInfo();
        UriResource last = uriInfo.getLastResourcePart();
        if (!(last instanceof UriResourceTypedImpl)) {
          throw wrap(new UriParserSemanticException("prev segement typed"));
        }
        prevType = getTypeInformation((UriResourceTypedImpl) last).type;
      }

      if (!(prevType instanceof EdmStructuralType)) {
        throw wrap(new UriParserSemanticException("Previous select item is not a structural type"));
      }

      EdmStructuralType structType = (EdmStructuralType) prevType;
      EdmElement element = structType.getProperty(odi);
      if (element == null) {
        throw wrap(new UriParserSemanticException("Previous select item has not property: " + odi));
      }

      // create new segment
      // SelectSegmentImpl newSegment = new SelectSegmentImpl().setProperty(property);
      // contextSelectItem.addSegment(newSegment);
      if (element instanceof EdmProperty) {
        EdmProperty property = (EdmProperty) element;
        if (property.isPrimitive()) {

          UriResourcePrimitivePropertyImpl simple = new UriResourcePrimitivePropertyImpl();
          simple.setProperty(property);

          UriInfoImpl uriInfo = (UriInfoImpl) contextSelectItem.getResourceInfo();
          if (uriInfo == null) {
            uriInfo = new UriInfoImpl().setKind(UriInfoKind.resource);
            uriInfo.addResourcePart(simple);
            contextSelectItem.setResourceInfo(uriInfo);
          } else {
            uriInfo.addResourcePart(simple);
          }
          return this;
        } else {
          UriInfoImpl uriInfo = (UriInfoImpl) contextSelectItem.getResourceInfo();

          UriResourceComplexPropertyImpl complex = new UriResourceComplexPropertyImpl();
          complex.setProperty(property);

          if (uriInfo == null) {
            uriInfo = new UriInfoImpl().setKind(UriInfoKind.resource);
            uriInfo.addResourcePart(complex);
            contextSelectItem.setResourceInfo(uriInfo);
          } else {
            uriInfo.addResourcePart(complex);
          }
          return this;
        }
      } else {
        throw wrap(new UriParserSemanticException("Only Simple and Complex properties within select allowed"));
      }
    } else {
      String namespace = decode(ctx.vNS.getText());
      namespace = namespace.substring(0, namespace.length() - 1);

      FullQualifiedName fullName = new FullQualifiedName(namespace, odi);
      // contextSelectItem.addQualifiedThing(fullName);

      if (contextSelectItem.getResourceInfo() == null) {
        EdmType prevType = contextTypes.peek().type;

        // check for complex type cast
        if (prevType instanceof EdmComplexType) {
          EdmComplexType ct = edm.getComplexType(fullName);
          if (ct != null) {
            if ((ct.compatibleTo((EdmStructuralType) prevType))) {
              UriResourceStartingTypeFilterImpl resourcePart = new UriResourceStartingTypeFilterImpl();
              resourcePart.setCollectionTypeFilter(ct);

              UriInfoImpl uriInfo = new UriInfoImpl().setKind(UriInfoKind.resource);
              uriInfo.addResourcePart(resourcePart);
              contextSelectItem.setResourceInfo(uriInfo);
              return this;
            }
          }
        } else if (prevType instanceof EdmEntityType) {
          EdmEntityType et = edm.getEntityType(fullName);
          if (et != null) {
            if ((et.compatibleTo((EdmStructuralType) prevType))) {
              UriResourceStartingTypeFilterImpl resourcePart = new UriResourceStartingTypeFilterImpl();
              resourcePart.setCollectionTypeFilter(et);

              UriInfoImpl uriInfo = new UriInfoImpl().setKind(UriInfoKind.resource);
              uriInfo.addResourcePart(resourcePart);
              contextSelectItem.setResourceInfo(uriInfo);
              return this;
            }
          }
        } else {
          throw wrap(new UriParserSemanticException("prev segement must be comlex of entity type"));
        }

      } else {
        UriInfoImpl uriInfo = (UriInfoImpl) contextSelectItem.getResourceInfo();
        UriResource last = uriInfo.getLastResourcePart();
        if (!(last instanceof UriResourceTypedImpl)) {
          throw wrap(new UriParserSemanticException("prev segement typed"));
        }
        EdmType prevType = getTypeInformation((UriResourceTypedImpl) last).type;

        if (prevType instanceof EdmComplexType) {
          EdmComplexType ct = edm.getComplexType(fullName);
          if (ct != null) {
            if ((ct.compatibleTo((EdmStructuralType) prevType))) {
              UriResourceStartingTypeFilterImpl resourcePart = new UriResourceStartingTypeFilterImpl();
              resourcePart.setCollectionTypeFilter(ct);

              uriInfo.addResourcePart(resourcePart);
              return this;
            }
          }
        } else if (prevType instanceof EdmEntityType) {
          throw wrap(new UriParserSemanticException("Error"));
          /*
           * EdmEntityType et = edm.getEntityType(fullName);
           * if (et != null) {
           * if ((et.compatibleTo((EdmStructuralType) prevType))) {
           * UriResourceStartingTypeFilterImpl resourcePart = new UriResourceStartingTypeFilterImpl();
           * resourcePart.setEntryTypeFilter(et);
           * 
           * uriInfo.addResourcePart(resourcePart);
           * return this;
           * }
           * }
           */
        } else {
          throw wrap(new UriParserSemanticException("prev segement must be comlex of entity type"));
        }
      }

      EdmType prevType = null;
      if (contextSelectItem.getResourceInfo() == null) {
        prevType = contextTypes.peek().type;
      } else {
        UriInfoImpl uriInfo = (UriInfoImpl) contextSelectItem.getResourceInfo();
        UriResource last = uriInfo.getLastResourcePart();
        if (!(last instanceof UriResourceTypedImpl)) {
          throw wrap(new UriParserSemanticException("prev segement typed"));
        }
        prevType = getTypeInformation((UriResourceTypedImpl) last).type;
      }

      FullQualifiedName finalTypeName = new FullQualifiedName(prevType.getNamespace(), prevType.getName());

      // check for action
      EdmAction action = edm.getAction(fullName, finalTypeName, null);
      // TODO verify that null ignores if it is a collection

      if (action != null) {
        UriResourceActionImpl uriAction = new UriResourceActionImpl();
        uriAction.setAction(action);

        UriInfoImpl resourcePath = (UriInfoImpl) contextSelectItem.getResourceInfo();
        resourcePath.addResourcePart(uriAction);
      }

      // check for function
      EdmFunction function = edm.getFunction(fullName, finalTypeName, null, null);
      // TODO verify that null ignores if it is a collection

      if (function != null) {
        UriResourceFunctionImpl uriFunction = new UriResourceFunctionImpl();
        uriFunction.setFunction(function);

        UriInfoImpl resourcePath = (UriInfoImpl) contextSelectItem.getResourceInfo();
        resourcePath.addResourcePart(uriFunction);
      }
    }
    return null;
  }

  @Override
  public Object visitSkip(final SkipContext ctx) {
    SkipOptionImpl skiptoken = new SkipOptionImpl();

    String text = decode(ctx.children.get(2).getText());

    return skiptoken.setValue(text).setText(text);
  }

  @Override
  public Object visitSkiptoken(final SkiptokenContext ctx) {
    SkipTokenOptionImpl skiptoken = new SkipTokenOptionImpl();

    String text = decode(ctx.children.get(2).getText());

    return skiptoken.setValue(text).setText(text);
  }

  @Override
  public ExpressionImpl visitStartsWithMethodCallExpr(final StartsWithMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.STARTSWITH)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this))
        .addParameter((ExpressionImpl) ctx.vE2.accept(this));
  }

  @Override
  public ExpressionImpl visitSubstringMethodCallExpr(final SubstringMethodCallExprContext ctx) {
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
  public ExpressionImpl visitTimeMethodCallExpr(final TimeMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TIME)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitTop(final TopContext ctx) {
    TopOptionImpl top = new TopOptionImpl();

    String text = decode(ctx.children.get(2).getText());

    return top.setValue(text).setText(text);
  }

  @Override
  public ExpressionImpl visitToLowerMethodCallExpr(final ToLowerMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TOLOWER)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitTotalOffsetMinutesMethodCallExpr(final TotalOffsetMinutesMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TOTALOFFSETMINUTES)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitTotalsecondsMethodCallExpr(final TotalsecondsMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TOTALSECONDS)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitToUpperMethodCallExpr(final ToUpperMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TOUPPER)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public ExpressionImpl visitTrimMethodCallExpr(final TrimMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.TRIM)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  @Override
  public Object visitUnary(final UnaryContext ctx) {
    // TODO implement
    return super.visitUnary(ctx);
  }

  @Override
  public ExpressionImpl visitYearMethodCallExpr(final YearMethodCallExprContext ctx) {
    return new MethodCallImpl()
        .setMethod(SupportedMethodCalls.YEAR)
        .addParameter((ExpressionImpl) ctx.vE1.accept(this));
  }

  private ParseCancellationException wrap(final UriParserException uriParserException) {
    return new ParseCancellationException(uriParserException);
  }

}
