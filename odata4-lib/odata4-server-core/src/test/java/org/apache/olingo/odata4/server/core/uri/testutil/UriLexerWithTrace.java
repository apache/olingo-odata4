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
package org.apache.olingo.odata4.server.core.uri.testutil;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.apache.olingo.odata4.server.core.uri.antlr.UriLexer;

public class UriLexerWithTrace extends UriLexer {
  int logLevel = 0;

  public UriLexerWithTrace(final ANTLRInputStream antlrInputStream, final int logLevel) {
    super(antlrInputStream);
    this.logLevel = logLevel;
  }

  public UriLexerWithTrace(final ANTLRInputStream antlrInputStream, final int logLevel, final int mode) {
    super(antlrInputStream);
    super.mode(mode);
    this.logLevel = logLevel;
  }

  @Override
  public void emit(final Token token) {
    if (logLevel > 1) {
      String out = String.format("%1$-" + 20 + "s", token.getText());

      int tokenType = token.getType();
      if (tokenType == -1) {
        out += "-1/EOF";
      } else {
        out += UriLexer.tokenNames[tokenType];
      }
      System.out.println("Lexer.emit(...):" + out);
    }

    super.emit(token);
  }

  @Override
  public void pushMode(final int m) {

    String out = UriLexer.modeNames[_mode] + "-->";

    super.pushMode(m);

    out += UriLexer.modeNames[_mode];

    if (logLevel > 1) {
      System.out.println(out + "            ");
    }
  }

  @Override
  public int popMode() {

    String out = UriLexer.modeNames[_mode] + "-->";

    int m = super.popMode();

    out += UriLexer.modeNames[_mode];

    if (logLevel > 1) {
      System.out.println(out + "            ");
    }

    return m;
  }
}
