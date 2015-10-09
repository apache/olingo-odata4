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
 * Can be applied to CSDL elements as described in the Conceptual Schema Definition Language.
 */
public interface EdmAnnotatable {

  /**
   * @param term term used for the annotation. MUST NOT be null.
   * @param qualifier for the term. Can be <code>NULL</code>
   * @return annotation according to term
   */
  EdmAnnotation getAnnotation(EdmTerm term, String qualifier);

  /**
   * Get list of all annotations.
   *
   * @return list of all annotations
   */
  List<EdmAnnotation> getAnnotations();
}
