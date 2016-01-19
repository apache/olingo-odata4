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
package org.apache.olingo.commons.api.data;

/**
 * Represents an instance annotation.
 */
public class Annotation extends Valuable {

  private String term;

  /**
   * Get term for Annotation.
   * @return term for Annotation.
   */
  public String getTerm() {
    return term;
  }

  /**
   * Set term for Annotation.
   * @param term term for Annotation.
   */
  public void setTerm(final String term) {
    this.term = term;
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o)
        && (term == null ? ((Annotation) o).term == null : term.equals(((Annotation) o).term));
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (term == null ? 0 : term.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return term == null ? "null" : term;
  }
}
