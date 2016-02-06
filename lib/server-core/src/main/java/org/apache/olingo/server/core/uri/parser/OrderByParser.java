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
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.queryoption.AliasQueryOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.queryoption.OrderByItemImpl;
import org.apache.olingo.server.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class OrderByParser {

  private final Edm edm;
  private final OData odata;

  public OrderByParser(final Edm edm, final OData odata) {
    this.edm = edm;
    this.odata = odata;
  }

  public OrderByOption parse(UriTokenizer tokenizer, final EdmStructuredType referencedType,
      final Collection<String> crossjoinEntitySetNames, final Map<String, AliasQueryOption> aliases)
      throws UriParserException, UriValidationException {
    OrderByOptionImpl orderByOption = new OrderByOptionImpl();
    do {
      final Expression orderByExpression = new ExpressionParser(edm, odata)
          .parse(tokenizer, referencedType, crossjoinEntitySetNames, aliases);
      OrderByItemImpl item = new OrderByItemImpl();
      item.setExpression(orderByExpression);
      if (tokenizer.next(TokenKind.AscSuffix)) {
        item.setDescending(false);
      } else if (tokenizer.next(TokenKind.DescSuffix)) {
        item.setDescending(true);
      }
      orderByOption.addOrder(item);
    } while (tokenizer.next(TokenKind.COMMA));
    return orderByOption;
  }
}
