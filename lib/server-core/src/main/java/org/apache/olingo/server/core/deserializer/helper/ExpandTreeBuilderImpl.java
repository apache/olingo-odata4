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
package org.apache.olingo.server.core.deserializer.helper;

import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.core.uri.queryoption.ExpandItemImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;

public class ExpandTreeBuilderImpl extends ExpandTreeBuilder {

  private ExpandOptionImpl expandOption = null;

  @Override
  public ExpandTreeBuilder expand(final EdmNavigationProperty edmNavigationProperty) {
    ExpandItemImpl expandItem = buildExpandItem(edmNavigationProperty);

    if (expandOption == null) {
      expandOption = new ExpandOptionImpl();
    }
    expandOption.addExpandItem(expandItem);

    return new ExpandTreeBuilderInner(expandItem);
  }

  public ExpandOption build() {
    return expandOption;
  }

  private class ExpandTreeBuilderInner extends ExpandTreeBuilder {
    private ExpandItemImpl parent;

    public ExpandTreeBuilderInner(final ExpandItemImpl expandItem) {
      parent = expandItem;
    }

    @Override
    public ExpandTreeBuilder expand(final EdmNavigationProperty edmNavigationProperty) {
      if (parent.getExpandOption() == null) {
        final ExpandOptionImpl expandOption = new ExpandOptionImpl();
        parent.setSystemQueryOption(expandOption);
      }

      final ExpandItemImpl expandItem = buildExpandItem(edmNavigationProperty);
      ((ExpandOptionImpl) parent.getExpandOption()).addExpandItem(expandItem);

      return new ExpandTreeBuilderInner(expandItem);
    }

  }
}