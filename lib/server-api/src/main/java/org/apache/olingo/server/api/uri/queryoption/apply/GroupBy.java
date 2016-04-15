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
package org.apache.olingo.server.api.uri.queryoption.apply;

import java.util.List;

import org.apache.olingo.server.api.uri.queryoption.ApplyItem;
import org.apache.olingo.server.api.uri.queryoption.ApplyOption;

/**
 * Represents the grouping transformation.
 */
public interface GroupBy extends ApplyItem {

  /**
   * Gets the items to group.
   * @return a non-empty list of {@link GroupByItem}s (but never <code>null</code>)
   */
  List<GroupByItem> getGroupByItems();

  /**
   * Gets the apply option to be applied to the grouped items.
   * @return an {@link ApplyOption} (but never <code>null</code>)
   */
  ApplyOption getApplyOption();
}
