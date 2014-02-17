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

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;

public class ParseTreeToText {

  public static String getTreeAsText(final Tree contextTree, final String[] ruleNames) {
    return toStringTree(contextTree, Arrays.asList(ruleNames));
  }

  private static String toStringTree(final Tree t, @Nullable final List<String> ruleNames) {

    if (t.getChildCount() == 0) {
      return Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
    }

    StringBuilder buf = new StringBuilder();
    String s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
    buf.append(s);
    buf.append("(");

    for (int i = 0; i < t.getChildCount(); i++) {
      if (i > 0) {
        buf.append(' ');
      }
      buf.append(toStringTree(t.getChild(i), ruleNames));
    }
    buf.append(")");
    return buf.toString();
  }

  private static String getNodeText(@NotNull final Tree t, @Nullable final List<String> ruleNames) {
    if (ruleNames != null) {
      if (t instanceof RuleNode) {
        int ruleIndex = ((RuleNode) t).getRuleContext().getRuleIndex();
        return ruleNames.get(ruleIndex);
      } else if (t instanceof ErrorNode) {
        return t.toString();
      } else if (t instanceof TerminalNode) {
        Token symbol = ((TerminalNode) t).getSymbol();
        if (symbol != null) {
          String s = symbol.getText();
          return s;
        }
      }
    }
    // no recog for rule names
    Object payload = t.getPayload();
    if (payload instanceof Token) {
      return ((Token) payload).getText();
    }
    return t.getPayload().toString();
  }
}
