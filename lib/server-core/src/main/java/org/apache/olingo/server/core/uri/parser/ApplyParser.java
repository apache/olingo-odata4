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
package org.apache.olingo.server.core.uri.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.AliasQueryOption;
import org.apache.olingo.server.api.uri.queryoption.ApplyItem;
import org.apache.olingo.server.api.uri.queryoption.ApplyOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.api.uri.queryoption.apply.Aggregate;
import org.apache.olingo.server.api.uri.queryoption.apply.AggregateExpression;
import org.apache.olingo.server.api.uri.queryoption.apply.AggregateExpression.StandardMethod;
import org.apache.olingo.server.api.uri.queryoption.apply.BottomTop;
import org.apache.olingo.server.api.uri.queryoption.apply.Compute;
import org.apache.olingo.server.api.uri.queryoption.apply.Concat;
import org.apache.olingo.server.api.uri.queryoption.apply.CustomFunction;
import org.apache.olingo.server.api.uri.queryoption.apply.GroupBy;
import org.apache.olingo.server.api.uri.queryoption.apply.GroupByItem;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceComplexPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceCountImpl;
import org.apache.olingo.server.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourcePrimitivePropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceStartingTypeFilterImpl;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.queryoption.ApplyOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandItemImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SkipOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.TopOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.AggregateExpressionImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.AggregateImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.BottomTopImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.ComputeExpressionImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.ComputeImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.ConcatImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.CustomFunctionImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.DynamicProperty;
import org.apache.olingo.server.core.uri.queryoption.apply.DynamicStructuredType;
import org.apache.olingo.server.core.uri.queryoption.apply.ExpandImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.FilterImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.GroupByImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.GroupByItemImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.IdentityImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.OrderByImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.SearchImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.SkipImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.TopImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class ApplyParser {

  private static final Map<TokenKind, StandardMethod> TOKEN_KIND_TO_STANDARD_METHOD;
  static {
    Map<TokenKind, StandardMethod> temp = new EnumMap<>(TokenKind.class);
    temp.put(TokenKind.SUM, StandardMethod.SUM);
    temp.put(TokenKind.MIN, StandardMethod.MIN);
    temp.put(TokenKind.MAX, StandardMethod.MAX);
    temp.put(TokenKind.AVERAGE, StandardMethod.AVERAGE);
    temp.put(TokenKind.COUNTDISTINCT, StandardMethod.COUNT_DISTINCT);
    TOKEN_KIND_TO_STANDARD_METHOD = Collections.unmodifiableMap(temp);
  }

  private static final Map<TokenKind, BottomTop.Method> TOKEN_KIND_TO_BOTTOM_TOP_METHOD;
  static {
    Map<TokenKind, BottomTop.Method> temp = new EnumMap<>(TokenKind.class);
    temp.put(TokenKind.BottomCountTrafo, BottomTop.Method.BOTTOM_COUNT);
    temp.put(TokenKind.BottomPercentTrafo, BottomTop.Method.BOTTOM_PERCENT);
    temp.put(TokenKind.BottomSumTrafo, BottomTop.Method.BOTTOM_SUM);
    temp.put(TokenKind.TopCountTrafo, BottomTop.Method.TOP_COUNT);
    temp.put(TokenKind.TopPercentTrafo, BottomTop.Method.TOP_PERCENT);
    temp.put(TokenKind.TopSumTrafo, BottomTop.Method.TOP_SUM);
    TOKEN_KIND_TO_BOTTOM_TOP_METHOD = Collections.unmodifiableMap(temp);
  }

  private final Edm edm;
  private final OData odata;

  private UriTokenizer tokenizer;
  private Collection<String> crossjoinEntitySetNames;
  private Map<String, AliasQueryOption> aliases;

  public ApplyParser(final Edm edm, final OData odata) {
    this.edm = edm;
    this.odata = odata;
  }

  public ApplyOption parse(UriTokenizer tokenizer, EdmStructuredType referencedType,
      final Collection<String> crossjoinEntitySetNames, final Map<String, AliasQueryOption> aliases)
      throws UriParserException, UriValidationException {
    this.tokenizer = tokenizer;
    this.crossjoinEntitySetNames = crossjoinEntitySetNames;
    this.aliases = aliases;

    return parseApply(referencedType);
  }

  private ApplyOption parseApply(EdmStructuredType referencedType)
      throws UriParserException, UriValidationException {
    ApplyOptionImpl option = new ApplyOptionImpl();
    option.setEdmStructuredType(referencedType);
    do {
      option.add(parseTrafo(referencedType));
    } while (tokenizer.next(TokenKind.SLASH));
    return option;
  }

  private ApplyItem parseTrafo(EdmStructuredType referencedType) throws UriParserException, UriValidationException {
    if (tokenizer.next(TokenKind.AggregateTrafo)) {
      return parseAggregateTrafo(referencedType);

    } else if (tokenizer.next(TokenKind.IDENTITY)) {
      return new IdentityImpl();

    } else if (tokenizer.next(TokenKind.ComputeTrafo)) {
      return parseComputeTrafo(referencedType);

    } else if (tokenizer.next(TokenKind.ConcatMethod)) {
      return parseConcatTrafo(referencedType);

    } else if (tokenizer.next(TokenKind.ExpandTrafo)) {
      return new ExpandImpl().setExpandOption(parseExpandTrafo(referencedType));

    } else if (tokenizer.next(TokenKind.FilterTrafo)) {
      final FilterOption filterOption = new FilterParser(edm, odata)
          .parse(tokenizer, referencedType, crossjoinEntitySetNames, aliases);
      ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
      return new FilterImpl().setFilterOption(filterOption);

    } else if (tokenizer.next(TokenKind.GroupByTrafo)) {
      return parseGroupByTrafo(referencedType);

    } else if (tokenizer.next(TokenKind.SearchTrafo)) {
      final SearchOption searchOption = new SearchParser().parse(tokenizer);
      ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
      return new SearchImpl().setSearchOption(searchOption);

    } else if (tokenizer.next(TokenKind.OrderByTrafo)) {
    	final OrderByOption orderByOption = new OrderByParser(edm, odata)
    			.parse(tokenizer, referencedType, crossjoinEntitySetNames, aliases);
    	ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    	return new OrderByImpl().setOrderByOption(orderByOption);
    } else if (tokenizer.next(TokenKind.TopTrafo)) {
    	ParserHelper.requireNext(tokenizer, TokenKind.IntegerValue);
    	final int value = ParserHelper.parseNonNegativeInteger(SystemQueryOptionKind.TOP.toString(),
                tokenizer.getText(), true);
        TopOptionImpl topOption = new TopOptionImpl();
        topOption.setText(tokenizer.getText());
        topOption.setValue(value);
        ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
        return new TopImpl().setTopOption(topOption);
    } else if (tokenizer.next(TokenKind.SkipTrafo)) {
    	ParserHelper.requireNext(tokenizer, TokenKind.IntegerValue);
    	final int value = ParserHelper.parseNonNegativeInteger(SystemQueryOptionKind.SKIP.toString(),
                tokenizer.getText(), true);
        SkipOptionImpl skipOption = new SkipOptionImpl();
        skipOption.setText(tokenizer.getText());
        skipOption.setValue(value);
        ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
        return new SkipImpl().setSkipOption(skipOption);
    } else if (tokenizer.next(TokenKind.QualifiedName)) {
      return parseCustomFunction(new FullQualifiedName(tokenizer.getText()), referencedType);

    } else {
      final TokenKind kind = ParserHelper.next(tokenizer,
          TokenKind.BottomCountTrafo, TokenKind.BottomPercentTrafo, TokenKind.BottomSumTrafo,
          TokenKind.TopCountTrafo, TokenKind.TopPercentTrafo, TokenKind.TopSumTrafo);
      if (kind == null) {
        throw new UriParserSyntaxException("Invalid apply expression syntax.",
            UriParserSyntaxException.MessageKeys.SYNTAX);
      } else {
        return parseBottomTop(kind, referencedType);
      }
    }
  }

  private Aggregate parseAggregateTrafo(EdmStructuredType referencedType)
      throws UriParserException, UriValidationException {
    AggregateImpl aggregate = new AggregateImpl();
    Set<String> dynamicProps = new HashSet<>();
    do {
    	AggregateExpression aggregateExpr = parseAggregateExpr(referencedType, dynamicProps, Requirement.REQUIRED);
        aggregate.addExpression(aggregateExpr);
        dynamicProps.addAll(aggregateExpr.getDynamicProperties());
    } while (tokenizer.next(TokenKind.COMMA));
    dynamicProps.forEach(dp -> addPropertyToRefType(referencedType, dp));
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return aggregate;
  }
  
  private void addPropertyToRefType(EdmStructuredType referencedType, String alias) {
      ((DynamicStructuredType) referencedType).addProperty(
              createDynamicProperty(alias,
                  // The OData standard mandates Edm.Decimal (with no decimals), although counts are always integer.
                  odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal)));
  }
  
  public AggregateExpression parseAggregateMethodCallExpr(UriTokenizer tokenizer, EdmStructuredType referringType)
	      throws UriParserException, UriValidationException {
	    this.tokenizer = tokenizer;

	    return parseAggregateExpr(referringType, Collections.emptySet(), Requirement.FORBIDDEN);
	  }

  private AggregateExpression parseAggregateExpr(EdmStructuredType referencedType, Set<String> dynamicProps,
		  Requirement aliasRequired)
      throws UriParserException, UriValidationException {
	    AggregateExpressionImpl aggregateExpression = new AggregateExpressionImpl();
	    tokenizer.saveState();

	    // First try is checking for a (potentially empty) path prefix and the things that could follow it.
	    UriInfoImpl uriInfo = new UriInfoImpl();
	    final String identifierLeft = parsePathPrefix(uriInfo, referencedType);
	    if (identifierLeft != null) {
	      customAggregate(referencedType, aggregateExpression, uriInfo);
	    } else if (tokenizer.next(TokenKind.OPEN)) {
	      final UriResource lastResourcePart = uriInfo.getLastResourcePart();
	      if (lastResourcePart == null) {
	        throw new UriParserSyntaxException("Invalid 'aggregateExpr' syntax.",
	            UriParserSyntaxException.MessageKeys.SYNTAX);
	      }
	      aggregateExpression.setPath(uriInfo);
	      DynamicStructuredType inlineType = new DynamicStructuredType((EdmStructuredType)
	          ParserHelper.getTypeInformation((UriResourcePartTyped) lastResourcePart));
	      aggregateExpression.setInlineAggregateExpression(parseAggregateExpr(inlineType, 
	    		  dynamicProps, aliasRequired));
	      ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
	    } else if (tokenizer.next(TokenKind.COUNT)) {
	      uriInfo.addResourcePart(new UriResourceCountImpl());
	      aggregateExpression.setPath(uriInfo);
	      final String alias = parseAsAlias(referencedType, dynamicProps, aliasRequired);
	      if (alias != null) {
	        aggregateExpression.setAlias(alias);
	        aggregateExpression.addDynamicProperty(alias);
	      }
	    } else {
	      // No legitimate continuation of a path prefix has been found.

	      // Second try is checking for a common expression.
	      tokenizer.returnToSavedState();
	      final Expression expression = new ExpressionParser(edm, odata)
	          .parse(tokenizer, referencedType, crossjoinEntitySetNames, aliases);
	      aggregateExpression.setExpression(expression);
	      parseAggregateWith(aggregateExpression);
	      if (aggregateExpression.getStandardMethod() == null && aggregateExpression.getCustomMethod() == null) {
				if (tokenizer.next(TokenKind.AsOperator)) {
					throw new UriParserSyntaxException("Invalid 'aggregateExpr' syntax.",
							UriParserSyntaxException.MessageKeys.SYNTAX);
				} 
				customAggregateNamedAsProperty(referencedType, aggregateExpression, uriInfo);
				
				return aggregateExpression;
		      }
	      final String alias = parseAsAlias(referencedType, dynamicProps, aliasRequired);
	      if(alias != null) {
	        aggregateExpression.setAlias(alias);
	        aggregateExpression.addDynamicProperty(alias);
	      }
	      parseAggregateFrom(aggregateExpression, referencedType);
	    }

	    return aggregateExpression;
	  }

  private void customAggregate(EdmStructuredType referencedType, AggregateExpressionImpl aggregateExpression,
      UriInfoImpl uriInfo) throws UriParserException {
    final String customAggregate = tokenizer.getText();
    // A custom aggregate (an OData identifier) is defined in the CustomAggregate
    // EDM annotation (in namespace Org.OData.Aggregation.V1) of the structured type
    // or of the entity container.
    // Currently we don't look into annotations, so all custom aggregates are
    // allowed and have no type.
    uriInfo.addResourcePart(new UriResourcePrimitivePropertyImpl(createDynamicProperty(customAggregate, null)));
    aggregateExpression.setPath(uriInfo);
    final String alias = parseAsAlias(referencedType, aggregateExpression.getDynamicProperties(), Requirement.OPTIONAL);
    if (alias != null) {
      aggregateExpression.setAlias(alias);
      aggregateExpression.addDynamicProperty(alias);
    }
    parseAggregateFrom(aggregateExpression, referencedType);
  }

  private void customAggregateNamedAsProperty(EdmStructuredType referencedType, 
		  AggregateExpressionImpl aggregateExpression, UriInfoImpl uriInfo) 
				  throws UriParserException {
    /*
     * The name of the custom aggregate is identical to the name of a declared
     * property of the structured type. This is typically done when the custom
     * aggregate is used as a default aggregate for that property. In this case, the
     * name refers to the custom aggregate within an aggregate expression without a
     * with clause, and to the property in all other cases.
     */
    UriResource lastResourcePart = uriInfo.getLastResourcePart();
    String alias = lastResourcePart.getSegmentValue();
    EdmType edmType = ParserHelper.getTypeInformation((UriResourcePartTyped) lastResourcePart);
    aggregateExpression.setPath(uriInfo);
    aggregateExpression.setAlias(alias);
    aggregateExpression.setExpression(null);
    ((DynamicStructuredType) referencedType).addProperty(createDynamicProperty(alias, edmType));
  }
	  
  private void parseAggregateWith(AggregateExpressionImpl aggregateExpression) throws UriParserException {
    if (tokenizer.next(TokenKind.WithOperator)) {
      final TokenKind kind = ParserHelper.next(tokenizer,
          TokenKind.SUM, TokenKind.MIN, TokenKind.MAX, TokenKind.AVERAGE, TokenKind.COUNTDISTINCT,
          TokenKind.QualifiedName);
      if (kind == null) {
        throw new UriParserSyntaxException("Invalid 'with' syntax.",
            UriParserSyntaxException.MessageKeys.SYNTAX);
      } else if (kind == TokenKind.QualifiedName) {
        // A custom aggregation method is announced in the CustomAggregationMethods
        // EDM annotation (in namespace Org.OData.Aggregation.V1) of the structured type or of the entity container.
        // Currently we don't look into annotations, so all custom aggregation methods are allowed and have no type.
        aggregateExpression.setCustomMethod(new FullQualifiedName(tokenizer.getText()));
      } else {
        aggregateExpression.setStandardMethod(TOKEN_KIND_TO_STANDARD_METHOD.get(kind));
      }
    }
  }

  private EdmType getTypeForAggregateMethod(final StandardMethod method, final EdmType type) {
    if (method == StandardMethod.SUM || method == StandardMethod.AVERAGE || method == StandardMethod.COUNT_DISTINCT) {
      return odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal);
    } else if (method == StandardMethod.MIN || method == StandardMethod.MAX) {
      return type;
    } else {
      return null;
    }
  }
  
  enum Requirement {
	    REQUIRED, OPTIONAL, FORBIDDEN
	  }

  private String parseAsAlias(final EdmStructuredType referencedType, Set<String> dynamicProps,
	      final Requirement requirement) throws UriParserException {
	    if (tokenizer.next(TokenKind.AsOperator)) {
	        if(requirement == Requirement.FORBIDDEN) {
	          throw new UriParserSyntaxException("Unexpected as alias found.", 
	        		  UriParserSyntaxException.MessageKeys.SYNTAX);
	        }
	        ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
	        final String name = tokenizer.getText();
	        if (((DynamicStructuredType)referencedType).hasStaticProperty(name) || dynamicProps.contains(name)) {
	          throw new UriParserSemanticException("Alias '" + name + "' is already a property.",
	              UriParserSemanticException.MessageKeys.IS_PROPERTY, name);
	        }
	        return name;
	      } else if (requirement == Requirement.REQUIRED) {
	        throw new UriParserSyntaxException("Expected as alias not found.", 
	        		UriParserSyntaxException.MessageKeys.SYNTAX);
	      }
	      return null;
	    }

  private void parseAggregateFrom(AggregateExpressionImpl aggregateExpression,
      final EdmStructuredType referencedType) throws UriParserException {
    while (tokenizer.next(TokenKind.FromOperator)) {
      AggregateExpressionImpl from = new AggregateExpressionImpl();
      from.setExpression(new MemberImpl(parseGroupingProperty(referencedType), referencedType));
      parseAggregateWith(from);
      aggregateExpression.addFrom(from);
    }
  }

  private DynamicProperty createDynamicProperty(final String name, final EdmType type) {
    return name == null ? null : new DynamicProperty(name, type);
  }

  private Compute parseComputeTrafo(EdmStructuredType referencedType)
      throws UriParserException, UriValidationException {
    ComputeImpl compute = new ComputeImpl();
    do {
      final Expression expression = new ExpressionParser(edm, odata)
          .parse(tokenizer, referencedType, crossjoinEntitySetNames, aliases);
      final EdmType expressionType = ExpressionParser.getType(expression);
      if (expressionType.getKind() != EdmTypeKind.PRIMITIVE) {
        throw new UriParserSemanticException("Compute expressions must return primitive values.",
            UriParserSemanticException.MessageKeys.ONLY_FOR_PRIMITIVE_TYPES, "compute");
      }
      final String alias = parseAsAlias(referencedType, Collections.emptySet(), Requirement.REQUIRED);
      ((DynamicStructuredType) referencedType).addProperty(createDynamicProperty(alias, expressionType));
      compute.addExpression(new ComputeExpressionImpl()
          .setExpression(expression)
          .setAlias(alias));
    } while (tokenizer.next(TokenKind.COMMA));
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return compute;
  }

  private Concat parseConcatTrafo(EdmStructuredType referencedType)
      throws UriParserException, UriValidationException {
    ConcatImpl concat = new ConcatImpl();
    // A common type is used for all sub-transformations.
    // If one sub-transformation aggregates properties away,
    // this could have unintended consequences for subsequent sub-transformations.
    concat.addApplyOption(parseApply(referencedType));
    ParserHelper.requireNext(tokenizer, TokenKind.COMMA);
    do {
      concat.addApplyOption(parseApply(referencedType));
    } while (tokenizer.next(TokenKind.COMMA));
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return concat;
  }

  private ExpandOption parseExpandTrafo(final EdmStructuredType referencedType)
      throws UriParserException, UriValidationException {
    ExpandItemImpl item = new ExpandItemImpl();
    item.setResourcePath(ExpandParser.parseExpandPath(tokenizer, edm, referencedType, item));
    final EdmType type = ParserHelper.getTypeInformation((UriResourcePartTyped)
        ((UriInfoImpl) item.getResourcePath()).getLastResourcePart());
    if (tokenizer.next(TokenKind.COMMA)) {
      if (tokenizer.next(TokenKind.FilterTrafo)) {
        item.setSystemQueryOption(
            new FilterParser(edm, odata).parse(tokenizer,type, crossjoinEntitySetNames, aliases));
        ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
      } else {
        ParserHelper.requireNext(tokenizer, TokenKind.ExpandTrafo);
        item.setSystemQueryOption(parseExpandTrafo((EdmStructuredType) type));
      }
    }
    while (tokenizer.next(TokenKind.COMMA)) {
      ParserHelper.requireNext(tokenizer, TokenKind.ExpandTrafo);
      final ExpandOption nestedExpand = parseExpandTrafo((EdmStructuredType) type);
      if (item.getExpandOption() == null) {
        item.setSystemQueryOption(nestedExpand);
      } else {
        // Add to the existing items.
        ((ExpandOptionImpl) item.getExpandOption())
            .addExpandItem(nestedExpand.getExpandItems().get(0));
      }
    }
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    ExpandOptionImpl expand = new ExpandOptionImpl();
    expand.addExpandItem(item);
    return expand;
  }

  private GroupBy parseGroupByTrafo(final EdmStructuredType referencedType)
      throws UriParserException, UriValidationException {
    GroupByImpl groupBy = new GroupByImpl();
    parseGroupByList(groupBy, referencedType);
    if (tokenizer.next(TokenKind.COMMA)) {
      groupBy.setApplyOption(parseApply(referencedType));
    }
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return groupBy;
  }

  private void parseGroupByList(GroupByImpl groupBy, final EdmStructuredType referencedType)
      throws UriParserException {
    ParserHelper.requireNext(tokenizer, TokenKind.OPEN);
    do {
      groupBy.addGroupByItem(parseGroupByElement(referencedType));
    } while (tokenizer.next(TokenKind.COMMA));
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
  }

  private GroupByItem parseGroupByElement(final EdmStructuredType referencedType)
      throws UriParserException {
    if (tokenizer.next(TokenKind.RollUpSpec)) {
      return parseRollUpSpec(referencedType);
    } else {
      return new GroupByItemImpl().setPath(parseGroupingProperty(referencedType));
    }
  }

  private GroupByItem parseRollUpSpec(final EdmStructuredType referencedType)
      throws UriParserException {
    GroupByItemImpl item = new GroupByItemImpl();
    if (tokenizer.next(TokenKind.ROLLUP_ALL)) {
      item.setIsRollupAll();
    } else {
      item.addRollupItem(new GroupByItemImpl().setPath(
          parseGroupingProperty(referencedType)));
    }
    ParserHelper.requireNext(tokenizer, TokenKind.COMMA);
    do {
      item.addRollupItem(new GroupByItemImpl().setPath(
          parseGroupingProperty(referencedType)));
    } while (tokenizer.next(TokenKind.COMMA));
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return item;
  }

  private UriInfo parseGroupingProperty(final EdmStructuredType referencedType) throws UriParserException {
    UriInfoImpl uriInfo = new UriInfoImpl();
    final String identifierLeft = parsePathPrefix(uriInfo, referencedType);
    if (identifierLeft != null) {
      throw new UriParserSemanticException("Unknown identifier in grouping property path.",
          UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE,
          identifierLeft,
          uriInfo.getLastResourcePart() != null && uriInfo.getLastResourcePart() instanceof UriResourcePartTyped ?
              ((UriResourcePartTyped) uriInfo.getLastResourcePart())
                  .getType().getFullQualifiedName().getFullQualifiedNameAsString() :
              "");
    }
    return uriInfo;
  }

  /**
   * Parses the path prefix and a following OData identifier as one path, deviating from the ABNF.
   * @param uriInfo object to be filled with path segments
   * @return a parsed but not used OData identifier */
  private String parsePathPrefix(UriInfoImpl uriInfo, final EdmStructuredType referencedType)
      throws UriParserException {
    final EdmStructuredType typeCast = ParserHelper.parseTypeCast(tokenizer, edm, referencedType);
    if (typeCast != null) {
      uriInfo.addResourcePart(new UriResourceStartingTypeFilterImpl(typeCast, true));
      ParserHelper.requireNext(tokenizer, TokenKind.SLASH);
    }
    EdmStructuredType type = typeCast == null ? referencedType : typeCast;
    while (tokenizer.next(TokenKind.ODataIdentifier)) {
      final String name = tokenizer.getText();
      final EdmElement property = type.getProperty(name);
      final UriResource segment = parsePathSegment(property);
      if (segment == null) {
        if (property == null) {
          return name;
        } else {
          uriInfo.addResourcePart(
              property instanceof EdmNavigationProperty ?
                  new UriResourceNavigationPropertyImpl((EdmNavigationProperty) property) :
                  property.getType().getKind() == EdmTypeKind.COMPLEX ?
                      new UriResourceComplexPropertyImpl((EdmProperty) property) :
                      new UriResourcePrimitivePropertyImpl((EdmProperty) property));
          return null;
        }
      } else {
        uriInfo.addResourcePart(segment);
      }
      type = (EdmStructuredType) ParserHelper.getTypeInformation((UriResourcePartTyped) segment);
    }
    return null;
  }

  private UriResource parsePathSegment(final EdmElement property) throws UriParserException {
    if (property == null
        || !(property.getType().getKind() == EdmTypeKind.COMPLEX
        || property instanceof EdmNavigationProperty)) {
      // Could be a customAggregate or $count.
      return null;
    }
    if (tokenizer.next(TokenKind.SLASH)) {
      final EdmStructuredType typeCast = ParserHelper.parseTypeCast(tokenizer, edm,
          (EdmStructuredType) property.getType());
      if (typeCast != null) {
        ParserHelper.requireNext(tokenizer, TokenKind.SLASH);
      }
      return property.getType().getKind() == EdmTypeKind.COMPLEX ?
          new UriResourceComplexPropertyImpl((EdmProperty) property).setTypeFilter(typeCast) :
          new UriResourceNavigationPropertyImpl((EdmNavigationProperty) property).setCollectionTypeFilter(typeCast);
    } else {
      return null;
    }
  }

  private CustomFunction parseCustomFunction(final FullQualifiedName functionName,
      final EdmStructuredType referencedType) throws UriParserException, UriValidationException {
    final List<UriParameter> parameters =
        ParserHelper.parseFunctionParameters(tokenizer, edm, referencedType, true, aliases);
    final List<String> parameterNames = ParserHelper.getParameterNames(parameters);
    final EdmFunction function = edm.getBoundFunction(functionName,
        referencedType.getFullQualifiedName(), true, parameterNames);
    if (function == null) {
      throw new UriParserSemanticException("No function '" + functionName + "' found.",
          UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND,
          functionName.getFullQualifiedNameAsString());
    }
    ParserHelper.validateFunctionParameters(function, parameters, edm, referencedType, aliases);

    // The binding parameter and the return type must be of type complex or entity collection.
    final EdmParameter bindingParameter = function.getParameter(function.getParameterNames().get(0));
    final EdmReturnType returnType = function.getReturnType();
    if (bindingParameter.getType().getKind() != EdmTypeKind.ENTITY
        && bindingParameter.getType().getKind() != EdmTypeKind.COMPLEX
        || !bindingParameter.isCollection()
        || returnType.getType().getKind() != EdmTypeKind.ENTITY
        && returnType.getType().getKind() != EdmTypeKind.COMPLEX
        || !returnType.isCollection()) {
      throw new UriParserSemanticException("Only entity- or complex-collection functions are allowed.",
          UriParserSemanticException.MessageKeys.FUNCTION_MUST_USE_COLLECTIONS,
          functionName.getFullQualifiedNameAsString());
    }

    return new CustomFunctionImpl().setFunction(function).setParameters(parameters);
  }

  private BottomTop parseBottomTop(final TokenKind kind, final EdmStructuredType referencedType)
      throws UriParserException, UriValidationException {
    BottomTopImpl bottomTop = new BottomTopImpl();
    bottomTop.setMethod(TOKEN_KIND_TO_BOTTOM_TOP_METHOD.get(kind));
    final ExpressionParser expressionParser = new ExpressionParser(edm, odata);
    final Expression number = expressionParser.parse(tokenizer, referencedType, crossjoinEntitySetNames, aliases);
    expressionParser.checkIntegerType(number);
    bottomTop.setNumber(number);
    ParserHelper.requireNext(tokenizer, TokenKind.COMMA);
    final Expression value = expressionParser.parse(tokenizer, referencedType, crossjoinEntitySetNames, aliases);
    expressionParser.checkNumericType(value);
    bottomTop.setValue(value);
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return bottomTop;
  }
}
