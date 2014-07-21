/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.ext.proxy.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionItems {

  private final List<EntityInvocationHandler> keys = new ArrayList<EntityInvocationHandler>();

  private final List<Integer> values = new ArrayList<Integer>();

  public EntityInvocationHandler get(final Integer value) {
    if (value != null && values.contains(value)) {
      return keys.get(values.indexOf(value));
    } else {
      return null;
    }
  }

  public Integer get(final EntityInvocationHandler key) {
    if (key != null && keys.contains(key)) {
      return values.get(keys.indexOf(key));
    } else {
      return null;
    }
  }

  public void remove(final EntityInvocationHandler key) {
    if (keys.contains(key)) {
      values.remove(keys.indexOf(key));
      keys.remove(key);
    }
  }

  public void normalize() {
    final Set<Integer> toBeRemoved = new HashSet<Integer>();
    for (EntityInvocationHandler key : keys) {
      int i = keys.indexOf(key);
      if (values.get(i) == null) {
        toBeRemoved.add(i);
      }
    }

    for (int i : toBeRemoved) {
      keys.remove(i);
      values.remove(i);
    }
  }

  public void put(final EntityInvocationHandler key, final Integer value) {
    // replace just in case of null current value; otherwise add the new entry
    if (key != null && keys.contains(key) && values.get(keys.indexOf(key)) == null) {
      remove(key);
    }
    keys.add(key);
    values.add(value);
  }

  public List<Integer> sortedValues() {
    final List<Integer> sortedValues = new ArrayList<Integer>(values);
    Collections.<Integer>sort(sortedValues);
    return sortedValues;
  }

  public boolean contains(final EntityInvocationHandler key) {
    return keys.contains(key);
  }

  public int size() {
    return keys.size();
  }

  public boolean isEmpty() {
    return keys.isEmpty();
  }
}
