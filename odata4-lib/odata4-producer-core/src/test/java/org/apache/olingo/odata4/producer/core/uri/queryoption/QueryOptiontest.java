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
package org.apache.olingo.odata4.producer.core.uri.queryoption;

import static org.junit.Assert.assertEquals;

import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedBinaryOperators;
import org.apache.olingo.odata4.producer.core.testutil.FilterTreeToText;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.AliasImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.ExpressionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.LiteralImpl;
import org.junit.Test;

public class QueryOptiontest {

  @Test
  public void testAliasQueryOption() {
    AliasQueryOptionImpl option = new AliasQueryOptionImpl();
    
    ExpressionImpl expression = new LiteralImpl();
    
    option.setAliasValue(expression);
    assertEquals( expression, option.getValue());
  }
  
  
  
  
  
  
  //
}

