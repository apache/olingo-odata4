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
package org.apache.olingo.server.core.uri.queryoption.expression;

import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.LiteralList;

import java.util.ArrayList;
import java.util.List;

public class LiteralListImpl implements LiteralList {

  private List<String> text;
  // type of the element.
  private EdmType type;

  public LiteralListImpl(){
    this.text = new ArrayList<String>();
  }

  public LiteralListImpl(final List<String> text, final EdmType type) {
    this.text = text;
    this.type = type;
  }

  public LiteralListImpl setText(List<String> text) {
    this.text = text;
    return this;
  }

  public LiteralListImpl setType(EdmType type) {
    this.type = type;
    return this;
  }

  @Override
  public List<String> getText() {
    return text;
  }

  @Override
  public EdmType getType() {
    return type;
  }

  @Override
  public <T> T accept(final ExpressionVisitor<T> visitor) throws ExpressionVisitException, ODataApplicationException {
    return visitor.visitLiteralList(this);
  }

  @Override
  public String toString() {
    return type == null ? "NULL" :
      type.getFullQualifiedName().getFullQualifiedNameAsString() + text;
  }
}
