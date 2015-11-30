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
 * / searchTerm
 * ) [ searchOrExpr
 * / searchAndExpr
 * ]
 *
 * searchOrExpr = RWS 'OR' RWS searchExpr
 * searchAndExpr = RWS [ 'AND' RWS ] searchExpr
 *
 * searchTerm = [ 'NOT' RWS ] ( searchPhrase / searchWord )
 * searchPhrase = quotation-mark 1*qchar-no-AMP-DQUOTE quotation-mark
 * searchWord = 1*ALPHA ; Actually: any character from the Unicode categories L or Nl,
 * ; but not the words AND, OR, and NOT
 * </code>
 *
 * <b>ATTENTION:</b> For a <code>searchPhrase</code> the percent encoding is not supported by the
 * <code>SearchTokenizer</code>.<br/>
 * This was a decision based on that the <code>org.apache.olingo.server.core.uri.parser.Parser</code>
 * already handles in his <code>parseUri</code> method each query as <code>percent decoded</code> strings (see
 * line <i>177ff</i> (<code>for (RawUri.QueryOption option : uri.queryOptionListDecoded)</code>).
 *
 */
public class SearchTokenizer {

  private static abstract class State implements SearchQueryToken {
    private Token token = null;
    private boolean finished = false;

    protected static final char QUOTATION_MARK = '\"';
    protected static final char PHRASE_ESCAPE_CHAR = '\\';
    protected static final char CHAR_N = 'N';
    protected static final char CHAR_O = 'O';
    protected static final char CHAR_T = 'T';
    protected static final char CHAR_A = 'A';
    protected static final char CHAR_D = 'D';
    protected static final char CHAR_R = 'R';
    protected static final char CHAR_CLOSE = ')';
    protected static final char CHAR_OPEN = '(';

    public State() {
    }
    public State(Token t) {
      token = t;
    }
    public State(Token t, boolean finished) {
      this(t);
      this.finished = finished;
    }

    protected abstract State nextChar(char c) throws SearchTokenizerException;

    public State allowed(char c) {
      return this;
    }

    public State forbidden(char c) throws SearchTokenizerException {
      throw new SearchTokenizerException("Forbidden character in state " + this.getToken() + "->" + c,
          SearchTokenizerException.MessageKeys.FORBIDDEN_CHARACTER, "" + c);
    }

    public State invalid() throws SearchTokenizerException {
      throw new SearchTokenizerException("Token " + this.getToken() + " is in invalid state.",
          SearchTokenizerException.MessageKeys.INVALID_TOKEN_STATE);
    }

    public State finish() {
      this.finished = true;
      return this;
    }

    public State finishAs(Token token) {
      this.finished = true;
      return changeToken(token);
    }

    public boolean isFinished() {
      return finished;
    }

    public Token getToken() {
      return token;
    }

    public String getTokenName() {
      if(token == null) {
        return "NULL";
      }
      return token.name();
    }

    public State close() throws SearchTokenizerException {
      return this;
    }

    protected State changeToken(Token token) {
      this.token = token;
      return this;
    }

    static boolean isAllowedWord(final char character) {
      // TODO mibo: add missing allowed characters
      int type = Character.getType(character);
      return (type == Character.LETTER_NUMBER
          || type == Character.LOWERCASE_LETTER
          || type == Character.MODIFIER_LETTER
          || type == Character.OTHER_LETTER
          || type == Character.TITLECASE_LETTER
          || type == Character.UPPERCASE_LETTER);
    }

    /**
     * <code>
     * <b>searchPhrase</b> = quotation-mark 1*qchar-no-AMP-DQUOTE quotation-mark
     * <br/><br/>
     * <b>qchar-no-AMP-DQUOTE</b> = qchar-unescaped / escape ( escape / quotation-mark )
     * <br/><br/>
     * <b>qchar-unescaped</b> = unreserved / pct-encoded-unescaped / other-delims /
     * ":" / "@" / "/" / "?" / "$" / "'" / "="
     * <br/><br/>
     * <b>unreserved</b> = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * <br/><br/>
     * <b>escape</b> = "\" / "%5C" ; reverse solidus U+005C
     * <br/><br/>
     * <b>pct-encoded-unescaped</b> = "%" ( "0" / "1" / "3" / "4" / "6" / "7" / "8" / "9" / A-to-F ) HEXDIG
     * / "%" "2" ( "0" / "1" / "3" / "4" / "5" / "6" / "7" / "8" / "9" / A-to-F )
     * / "%" "5" ( DIGIT / "A" / "B" / "D" / "E" / "F" )
     * <br/><br/>
     * <b>other-delims</b> = "!" / "(" / ")" / "*" / "+" / "," / ";"
     * <br/><br/>
     * <b>quotation-mark</b> = DQUOTE / "%22"
     * <br/><br/>
     * <b>ALPHA</b> = %x41-5A / %x61-7A
     * <br/>
     * <b>DIGIT</b> = %x30-39
     * <br/>
     * <b>DQUOTE</b> = %x22
     * </code>
     *
     * Checks if given <code>character</code> is allowed for a search phrase.
     * <b>ATTENTION:</b> Escaping and percent encoding is not be validated here (and can not be validated on
     * a single character).<br/>
     * Hence for the {@link #PHRASE_ESCAPE_CHAR} and the {@link #QUOTATION_MARK} characters this method will
     * return <code>FALSE</code>.<br/>
     * <b>Furthermore</b> percent encoded characters are also not validated (and can not be validated on
     * a single character).<br/>
     * Hence for the <code>%</code> character this method will return <code>FALSE</code>.<br/>
     *
     * @param character which is checked
     * @return true if character is allowed for a phrase
     */
    static boolean isAllowedPhrase(final char character) {
      // FIXME mibo: check missing
      return isQCharUnescaped(character);// || isEscaped(character);
    }

//    /**
//     * escape = "\" / "%5C" ; reverse solidus U+005C
//     * @param character which is checked
//     * @return true if character is allowed
//     */
//    private static boolean isEscaped(char character) {
//      // TODO: mibo(151130): is checked in SearchPhraseState
//      return false;
//    }

    /**
     * qchar-unescaped = unreserved / pct-encoded-unescaped / other-delims / ":" / "@" / "/" / "?" / "$" / "'" / "="
     * @param character which is checked
     * @return true if character is allowed
     */
    private static boolean isQCharUnescaped(char character) {
      return isUnreserved(character)
//          || isPctEncodedUnescaped(character)
          || isOtherDelims(character)
          || character == ':'
          || character == '@'
          || character == '/'
          || character == '$'
          || character == '\''
          || character == '=';
    }

    /**
     * other-delims = "!" / "(" / ")" / "*" / "+" / "," / ";"
     * @param character which is checked
     * @return true if character is allowed
     */
    private static boolean isOtherDelims(char character) {
      return character == '!'
          || character == '('
          || character == ')'
          || character == '*'
          || character == '+'
          || character == ','
          || character == ';';
    }

//    /**
//     * pct-encoded-unescaped = "%" ( "0" / "1" / "3" / "4" / "6" / "7" / "8" / "9" / A-to-F ) HEXDIG
//     * / "%" "2" ( "0" / "1" / "3" / "4" / "5" / "6" / "7" / "8" / "9" / A-to-F )
//     * / "%" "5" ( DIGIT / "A" / "B" / "D" / "E" / "F" )
//     *
//     * HEXDIG = DIGIT / A-to-F
//     *
//     * @param character which is checked
//     * @return true if character is allowed
//     */
//    private static boolean isPctEncodedUnescaped(char character) {
//      String hex = Integer.toHexString(character);
//      char aschar[] = hex.toCharArray();
//      if(aschar[0] == '%') {
//        if(aschar[1] == '2') {
//          return aschar[2] != '2' && isHexDigit(aschar[2]);
//        } else if(aschar[1] == '5') {
//          return aschar[2] != 'C' && isHexDigit(aschar[2]);
//        } else if(isHexDigit(aschar[1])) {
//          return isHexDigit(aschar[2]);
//        }
//      }
//      return false;
//    }

//    private static boolean isHexDigit(char character) {
//      return 'A' <= character && character <= 'F' // case A..F
//          || '0' <= character && character <= '9'; // case 0..9
//    }

    /**
     * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * @param character which is checked
     * @return true if character is allowed
     */
    private static boolean isUnreserved(char character) {
      return isAlphaOrDigit(character)
          || character == '-'
          || character == '.'
          || character == '_'
          || character == '~';
    }

    /**
     * ALPHA = %x41-5A / %x61-7A
     * DIGIT = %x30-39
     * @param character which is checked
     * @return true if character is allowed
     */
    private static boolean isAlphaOrDigit(char character) {
      return 'A' <= character && character <= 'Z' // case A..Z
          || 'a' <= character && character <= 'z' // case a..z
          || '0' <= character && character <= '9'; // case 0..9
    }

    // BWS = *( SP / HTAB / "%20" / "%09" ) ; "bad" whitespace
    // RWS = 1*( SP / HTAB / "%20" / "%09" ) ; "required" whitespace
    static boolean isWhitespace(final char character) {
      return character == ' ' || character == '\t';
    }

    @Override
    public String getLiteral() {
      return token.toString();
    }

    @Override
    public String toString() {
      return getToken() + "=>{" + getLiteral() + "}";
    }
  }

  private static abstract class LiteralState extends State {
    protected final StringBuilder literal = new StringBuilder();
    public LiteralState() {
      super();
    }
    public LiteralState(Token t, char c) throws SearchTokenizerException {
      super(t);
      init(c);
    }

    public LiteralState(Token t, String initLiteral) {
      super(t);
      literal.append(initLiteral);
    }

    public State allowed(char c) {
      literal.append(c);
      return this;
    }

    @Override
    public String getLiteral() {
      return literal.toString();
    }

    public State init(char c) throws SearchTokenizerException {
      if (isFinished()) {
        throw new SearchTokenizerException(toString() + " is already finished.",
            SearchTokenizerException.MessageKeys.ALREADY_FINISHED, getTokenName());
      }
      literal.append(c);
      return this;
    }
  }

  private class SearchExpressionState extends LiteralState {
    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (c == CHAR_OPEN) {
        return new OpenState();
      } else if (isWhitespace(c)) {
        return new RwsState();
      } else if (c == CHAR_CLOSE) {
        return new CloseState();
      } else {
        return new SearchTermState().init(c);
      }
    }

    @Override
    public State init(char c) throws SearchTokenizerException {
      return nextChar(c);
    }
  }

  private class SearchTermState extends LiteralState {
    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (c == CHAR_N) {
        return new NotState(c);
      } else if (c == QUOTATION_MARK) {
        return new SearchPhraseState(c);
      } else if (isAllowedWord(c)) {
        return new SearchWordState(c);
      }
      return forbidden(c);
    }

    @Override
    public State init(char c) throws SearchTokenizerException {
      return nextChar(c);
    }
  }

  private class SearchWordState extends LiteralState {
    public SearchWordState(char c) throws SearchTokenizerException {
      super(Token.WORD, c);
      if (!isAllowedWord(c)) {
        forbidden(c);
      }
    }

    public SearchWordState(State toConsume) throws SearchTokenizerException {
      super(Token.WORD, toConsume.getLiteral());
      for (int i = 0; i < literal.length(); i++) {
        if (!isAllowedWord(literal.charAt(i))) {
          forbidden(literal.charAt(i));
        }
      }
    }

    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (isAllowedWord(c)) {
        return allowed(c);
      } else if (c == CHAR_CLOSE) {
        finish();
        return new CloseState();
      } else if (isWhitespace(c)) {
        finish();
        return new RwsState();
      }
      return forbidden(c);
    }

    @Override
    public State finish() {
      String tmpLiteral = literal.toString();
      if(tmpLiteral.length() == 3) {
        if(Token.AND.name().equals(tmpLiteral)) {
          return finishAs(Token.AND);
        } else if(Token.NOT.name().equals(tmpLiteral)) {
          return finishAs(Token.NOT);
        }
      } else if(tmpLiteral.length() == 2 && Token.OR.name().equals(tmpLiteral)) {
        return finishAs(Token.OR);
      }
      return super.finish();
    }

    @Override
    public State close() {
      return finish();
    }
  }

  private class SearchPhraseState extends LiteralState {
    private boolean closed = false;
    private boolean escaped = false;
    public SearchPhraseState(char c) throws SearchTokenizerException {
      super(Token.PHRASE, c);
      if (c != QUOTATION_MARK) {
        forbidden(c);
      }
    }

    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if(closed) {
        finish();
        if (c == CHAR_CLOSE) {
          return new CloseState();
        } else if (isWhitespace(c)) {
          return new RwsState();
        }
      } else if(escaped) {
        escaped = false;
        if(c == QUOTATION_MARK || c == PHRASE_ESCAPE_CHAR) {
          return allowed(c);
        } else {
          return forbidden(c);
        }
      } else if(c == PHRASE_ESCAPE_CHAR) {
        escaped = true;
        return this;
      } else if (isAllowedPhrase(c)) {
        return allowed(c);
      } else if (isWhitespace(c)) {
        return allowed(c);
      } else if (c == QUOTATION_MARK) {
        if(literal.length() == 1) {
          return invalid();
        }
        closed = true;
        return allowed(c);
      }
      return forbidden(c);
    }

    @Override
    public State close() throws SearchTokenizerException {
      if(closed) {
        return finish();
      }
      return invalid();
    }
  }

  private class OpenState extends State {
    public OpenState() {
      super(Token.OPEN, true);
    }

    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      finish();
      if (isWhitespace(c)) {
        return forbidden(c);
      }
      return new SearchExpressionState().init(c);
    }
  }

  private class CloseState extends State {
    public CloseState() {
      super(Token.CLOSE, true);
    }

    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      return new SearchExpressionState().init(c);
    }
  }

  private class NotState extends LiteralState {
    public NotState(char c) throws SearchTokenizerException {
      super(Token.NOT, c);
      if (c != CHAR_N) {
        forbidden(c);
      }
    }

    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (literal.length() == 1 && c == CHAR_O) {
        return allowed(c);
      } else if (literal.length() == 2 && c == CHAR_T) {
        return allowed(c);
      } else if (literal.length() == 3 && isWhitespace(c)) {
        finish();
        return new BeforePhraseOrWordRwsState();
      } else if(isWhitespace(c)) {
        changeToken(Token.WORD).finish();
        return new RwsState();
      }
      literal.append(c);
      return new SearchWordState(this);
    }
    @Override
    public State close() throws SearchTokenizerException {
      if(Token.NOT.name().equals(literal.toString())) {
        return finish();
      }
      return changeToken(Token.WORD).finish();
    }
  }

  private class AndState extends LiteralState {
    public AndState(char c) throws SearchTokenizerException {
      super(Token.AND, c);
      if (c != CHAR_A) {
        forbidden(c);
      }
    }

    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (literal.length() == 1 && c == CHAR_N) {
        return allowed(c);
      } else if (literal.length() == 2 && c == CHAR_D) {
        return allowed(c);
      } else if (literal.length() == 3 && isWhitespace(c)) {
        finish();
        return new BeforeSearchExpressionRwsState();
      } else if(isWhitespace(c)) {
        changeToken(Token.WORD).finish();
        return new RwsState();
      }
      literal.append(c);
      return new SearchWordState(this);
    }
    @Override
    public State close() throws SearchTokenizerException {
      if(Token.AND.name().equals(literal.toString())) {
        return finish();
      }
      return changeToken(Token.WORD).finish();
    }
  }

  private class OrState extends LiteralState {
    public OrState(char c) throws SearchTokenizerException {
      super(Token.OR, c);
      if (c != CHAR_O) {
        forbidden(c);
      }
    }
    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (literal.length() == 1 && (c == CHAR_R)) {
        return allowed(c);
      } else if (literal.length() == 2 && isWhitespace(c)) {
        finish();
        return new BeforeSearchExpressionRwsState();
      } else if(isWhitespace(c)) {
        changeToken(Token.WORD).finish();
        return new RwsState();
      }
      literal.append(c);
      return new SearchWordState(this);
    }
    @Override
    public State close() throws SearchTokenizerException {
      if(Token.OR.name().equals(literal.toString())) {
        return finish();
      }
      return changeToken(Token.WORD).finish();
    }
  }

  // RWS 'OR' RWS searchExpr
  // RWS [ 'AND' RWS ] searchExpr
  private class BeforeSearchExpressionRwsState extends State {
    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (isWhitespace(c)) {
        return allowed(c);
      } else {
        return new SearchExpressionState().init(c);
      }
    }
  }

  private class BeforePhraseOrWordRwsState extends State {
    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (isWhitespace(c)) {
        return allowed(c);
      } else if (c == QUOTATION_MARK) {
        return new SearchPhraseState(c);
      } else {
        return new SearchWordState(c);
      }
    }
  }

  private class RwsState extends State {
    @Override
    public State nextChar(char c) throws SearchTokenizerException {
      if (isWhitespace(c)) {
        return allowed(c);
      } else if (c == CHAR_O) {
        return new OrState(c);
      } else if (c == CHAR_A) {
        return new AndState(c);
      } else {
        return new SearchExpressionState().init(c);
      }
    }
  }

  /**
   * Take the search query and split into according SearchQueryToken.
   * Before split into tokens the given search query is 'trimmed'.
   *
   * @param searchQuery search query to be tokenized
   * @return list of tokens
   * @throws SearchTokenizerException if something in query is not valid
   * (based on OData search query ABNF)
   */
  public List<SearchQueryToken> tokenize(final String searchQuery)
      throws SearchTokenizerException {

    char[] chars = searchQuery.trim().toCharArray();

    State state = new SearchExpressionState();
    List<SearchQueryToken> states = new ArrayList<SearchQueryToken>();
    for (char aChar : chars) {
      State next = state.nextChar(aChar);
      if (state.isFinished()) {
        states.add(state);
      }
      state = next;
    }

    if (state.close().isFinished()) {
      states.add(state);
    } else {
      throw new SearchTokenizerException("Last parsed state '" + state.toString() + "' is not finished.",
          SearchTokenizerException.MessageKeys.NOT_FINISHED_QUERY, state.getTokenName());
    }

    return states;
  }
}
