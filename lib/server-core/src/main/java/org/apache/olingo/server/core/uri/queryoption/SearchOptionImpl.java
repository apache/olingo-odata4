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
package org.apache.olingo.server.core.uri.queryoption;

import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;

public class SearchOptionImpl extends SystemQueryOptionImpl implements SearchOption {

  private SearchExpression searchExpression;

  public SearchOptionImpl() {
    setKind(SystemQueryOptionKind.SEARCH);
  }

  @Override
  public SearchExpression getSearchExpression() {
    return searchExpression;
  }

  public SearchOptionImpl setSearchExpression(final SearchExpression searchExpression) {
    this.searchExpression = searchExpression;
    return this;
  }
}
