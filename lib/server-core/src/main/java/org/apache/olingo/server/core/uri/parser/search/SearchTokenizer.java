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
package org.apache.olingo.server.core.uri.parser.search;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>
 * searchExpr = ( OPEN BWS searchExpr BWS CLOSE
 *  / searchTerm
 *  ) [ searchOrExpr
 *  / searchAndExpr
 *  ]
 *
 *  searchOrExpr  = RWS 'OR'  RWS searchExpr
 *  searchAndExpr = RWS [ 'AND' RWS ] searchExpr
 *
 *  searchTerm   = [ 'NOT' RWS ] ( searchPhrase / searchWord )
 *  searchPhrase = quotation-mark 1*qchar-no-AMP-DQUOTE quotation-mark
 *  searchWord   = 1*ALPHA ; Actually: any character from the Unicode categories L or Nl,
 *  ; but not the words AND, OR, and NOT
 * </code>
 */
public class SearchTokenizer {
  //RWS = 1*( SP / HTAB / "%20" / "%09" )  ; "required" whitespace
  //BWS =  *( SP / HTAB / "%20" / "%09" )  ; "bad" whitespace


  private static abstract class State implements SearchQueryToken {
    private Token token = null;
    private boolean finished = false;
    private final StringBuilder literal;

    public static final char EOF = 0x03;

    public State(Token t) {
      token = t;
      literal = new StringBuilder();
    }
    public State(Token t, char c) {
      this(t);
      init(c);
    }
    public State(Token t, State consumeState) {
      token = t;
      literal = new StringBuilder(consumeState.getLiteral());
    }

    protected abstract State nextChar(char c);

    public State next(char c) {
      return nextChar(c);
    }

    public State init(char c) {
      if(isFinished()) {
        throw new IllegalStateException(toString() + " is already finished.");
      }
      literal.append(c);
      return this;
    }

    public State allowed(char c) {
      literal.append(c);
      return this;
    }

    public State forbidden(char c) {
      throw new IllegalStateException(this.getClass().getName() + "->" + c);
    }

    public State finish() {
      this.finished = true;
      return this;
    }

    public boolean isFinished() {
      return finished;
    }

    public Token getToken() {
      return token;
    }

    static boolean isAllowedChar(final char character) {
      // TODO mibo: add missing allowed characters
      return 'A' <= character && character <= 'Z' // case A..Z
          || 'a' <= character && character <= 'z' // case a..z
          || '0' <= character && character <= '9'; // case 0..9
    }

    /**
     * qchar-no-AMP-DQUOTE   = qchar-unescaped / escape ( escape / quotation-mark )
     * qchar-unescaped  = unreserved / pct-encoded-unescaped / other-delims / ":" / "@" / "/" / "?" / "$" / "'" / "="
     * unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * @param character which is checked
     * @return true if character is allowed for a phrase
     */
    static boolean isAllowedPhrase(final char character) {
      // FIXME mibo: check missing and '\''
      return isAllowedChar(character)
          || character == '-'
          || character == '.'
          || character == '_'
          || character == '~'
          || character == ':'
          || character == '@'
          || character == '/'
          || character == '$'
          || character == '=';
    }

    static boolean isEof(final char character) {
      return character == EOF;
    }

    static boolean isWhitespace(final char character) {
      //( SP / HTAB / "%20" / "%09" )
      // TODO mibo: add missing whitespaces
      return character == ' ' || character == '\t';
    }

    @Override
    public String getLiteral() {
      return literal.toString();
    }

    @Override
    public String toString() {
      return this.getToken().toString() + "=>{" + literal.toString() + "}";
    }
  }

  private class SearchExpressionState extends State {
    public SearchExpressionState() {
      super(Token.SEARCH_EXPRESSION);
    }
    @Override
    public State nextChar(char c) {
      if (c == '(') {
        return new OpenState(c);
      } else if (isWhitespace(c)) {
        return new RwsState(c);
      } else if(c == ')') {
        return new CloseState(c);
      } else if(isEof(c)) {
        return finish();
      } else {
        return new SearchTermState().init(c);
      }
    }

    @Override
    public State init(char c) {
      return nextChar(c);
    }
  }

  private class SearchTermState extends State {
    public SearchTermState() {
      super(Token.TERM);
    }
    @Override
    public State nextChar(char c) {
      if(c == 'n' || c == 'N') {
        return new NotState(c);
      } else if (c == '\'') {
        return new SearchPhraseState(c);
      } else if (isAllowedChar(c)) {
        return new SearchWordState(c);
      } else if (c == ')') {
        finish();
        return new CloseState(c);
      } else if (isWhitespace(c)) {
        finish();
        return new RwsState(c);
      } else if (isEof(c)) {
        return finish();
      }
      throw new IllegalStateException(this.getClass().getName() + "->" + c);
    }
    @Override
    public State init(char c) {
      return nextChar(c);
    }
  }

  private class SearchWordState extends State {
    public SearchWordState(char c) {
      super(Token.WORD, c);
    }
    public SearchWordState(State toConsume) {
      super(Token.WORD, toConsume);
    }

    @Override
    public State nextChar(char c) {
      //      if(c == 'n' || c == 'N') {
      //        return new NotState(c);
      //      }
      if (isAllowedChar(c)) {
        return allowed(c);
      } else if (c == ')') {
        finish();
        return new CloseState(c);
      } else if (isWhitespace(c)) {
        finish();
        return new RwsState(c);
      } else if (isEof(c)) {
        return finish();
      }
      throw new IllegalStateException(this.getClass().getName() + "->" + c);
    }
  }

  private class SearchPhraseState extends State {
    public SearchPhraseState(char c) {
      super(Token.PHRASE, c);
      if(c != '\'') {
        forbidden(c);
      }
    }

    @Override
    public State nextChar(char c) {
      if(isFinished() && !isEof(c)) {
        return new SearchExpressionState().init(c);
      } else if (isAllowedPhrase(c)) {
        return allowed(c);
      } else if (c == '\'') {
        finish();
        return allowed(c);
      } else if (isWhitespace(c)) {
        if(isFinished()) {
          return new RwsState(c);
        }
        return allowed(c);
      } else if (isEof(c)) {
        return finish();
      }
      throw new IllegalStateException(this.getClass().getName() + "->" + c);
    }
  }

  private class OpenState extends State {
    public OpenState(char c) {
      super(Token.OPEN, c);
      finish();
    }
    @Override
    public State nextChar(char c) {
      finish();
      if (isWhitespace(c)) {
        throw new IllegalStateException(this.getClass().getName() + "->" + c);
      }
      return new SearchExpressionState().init(c);
    }
  }

  private class CloseState extends State {
    public CloseState(char c) {
      super(Token.CLOSE, c);
      finish();
    }

    @Override
    public State nextChar(char c) {
      if (isEof(c)) {
        return finish();
      } else {
        return new SearchExpressionState().init(c);
      }
    }
  }

  private class NotState extends State {
    public NotState(char c) {
      super(Token.NOT, c);
    }
    @Override
    public State nextChar(char c) {
      if (getLiteral().length() == 1 && (c == 'o' || c == 'O')) {
        return allowed(c);
      } else if (getLiteral().length() == 2 && (c == 't' || c == 'T')) {
        return allowed(c);
      } else if(getLiteral().length() == 3 && isWhitespace(c)) {
        finish();
        return new RwsState(c);
      } else {
        return new SearchWordState(this);
      }
    }
  }

  private class AndState extends State {
    public AndState(char c) {
      super(Token.AND, c);
      if(c != 'a' && c != 'A') {
        forbidden(c);
      }
    }
    @Override
    public State nextChar(char c) {
      if (getLiteral().length() == 1 && (c == 'n' || c == 'N')) {
        return allowed(c);
      } else if (getLiteral().length() == 2 && (c == 'd' || c == 'D')) {
        return allowed(c);
      } else if(getLiteral().length() == 3 && isWhitespace(c)) {
        finish();
        return new RwsState(c);
      } else {
        return new SearchWordState(this);
      }
    }
  }

  private class OrState extends State {
    public OrState(char c) {
      super(Token.OR, c);
      if(c != 'o' && c != 'O') {
        forbidden(c);
      }
    }
    @Override
    public State nextChar(char c) {
      if (getLiteral().length() == 1 && (c == 'r' || c == 'R')) {
        return allowed(c);
      } else if(getLiteral().length() == 2 && isWhitespace(c)) {
        finish();
        return new RwsState(c);
      } else {
        return new SearchWordState(this);
      }
    }
  }


  private class RwsState extends State {
    public RwsState(char c) {
      super(Token.RWS, c);
    }
    @Override
    public State nextChar(char c) {
      if (isWhitespace(c)) {
        return allowed(c);
      } else if (c == 'O' || c == 'o') {
        return new OrState(c);
      } else if (c == 'A' || c == 'a') {
        return new AndState(c);
      } else {
        return new SearchExpressionState().init(c);
      }
    }
  }

  // TODO (mibo): add (new) parse exception
  public List<SearchQueryToken> tokenize(String searchQuery) {
    char[] chars = searchQuery.toCharArray();

    State state = new SearchExpressionState();
    List<SearchQueryToken> states = new ArrayList<SearchQueryToken>();
    for (char aChar : chars) {
      State next = state.next(aChar);
      if (state.isFinished() && next != state) {
        states.add(state);
      }
      state = next;
    }

    if(state.next(State.EOF).isFinished()) {
      states.add(state);
    } else {
      throw new IllegalStateException("State: " + state + " not finished and list is: " + states.toString());
    }

    return states;
  }

}
