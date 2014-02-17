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
package org.apache.olingo.odata4.server.core.testutil;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.apache.olingo.odata4.server.core.uri.Parser;
import org.apache.olingo.odata4.server.core.uri.antlr.UriParserParser;

public class ParserTest extends Parser {
  TestErrorLogger errorCollector1;
  TestErrorLogger errorCollector2;

  public ParserTest() {
    errorCollector1 = new TestErrorLogger("Stage 1", 1);
    errorCollector2 = new TestErrorLogger("Stage 2", 1);
  }

  @Override
  protected void addStage2ErrorStategy(UriParserParser parser) {
    // Don't throw an at first syntax error, so the error listener will be called
    parser.setErrorHandler(new DefaultErrorStrategy());
  }

  @Override
  protected void addStage1ErrorListener(UriParserParser parser) {
    // Log error to console
    parser.removeErrorListeners();
    parser.addErrorListener(errorCollector1);
  }

  @Override
  protected void addStage2ErrorListener(UriParserParser parser) {
    // Log error to console
    parser.removeErrorListeners();
    parser.addErrorListener(errorCollector2);
  }
}
