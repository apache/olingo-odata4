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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class ExpandOptionImpl extends SystemQueryOptionImpl implements ExpandOption {

  private final List<ExpandItem> expandItems = new ArrayList<ExpandItem>();

  public ExpandOptionImpl() {
    setKind(SystemQueryOptionKind.EXPAND);
  }

  public ExpandOptionImpl addExpandItem(final ExpandItem expandItem) {
    expandItems.add(expandItem);
    return this;
  }

  @Override
  public List<ExpandItem> getExpandItems() {
    return Collections.unmodifiableList(expandItems);
  }
}
