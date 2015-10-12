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
package org.apache.olingo.commons.api.edm;

import java.util.List;

/**
 * An EdmEnumType represents a set of related values.
 */
public interface EdmEnumType extends EdmPrimitiveType {

  /**
   * Get member according to given name
   *
   * @param name name of member
   * @return {@link EdmMember} for the given name
   */
  EdmMember getMember(String name);

  /**
   * @return member names as a list
   */
  List<String> getMemberNames();

  /**
   * @return the {@link EdmPrimitiveType} this {@link EdmEnumType} is based upon
   */
  EdmPrimitiveType getUnderlyingType();

  /**
   * @return true if flags is set
   */
  boolean isFlags();
}
