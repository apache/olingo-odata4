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
package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class TargetQualifierMapKey {

  private final FullQualifiedName targetName;
  private final String qualifier;

  public TargetQualifierMapKey(FullQualifiedName targetName, String qualifier) {
    if (targetName == null) {
      throw new EdmException("targetName for TargetQualifierMapKey must not be null.");
    }
    this.targetName = targetName;
    this.qualifier = qualifier;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
    result = prime * result + ((targetName == null) ? 0 : targetName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof TargetQualifierMapKey)) {
      return false;
    }
    TargetQualifierMapKey other = (TargetQualifierMapKey) obj;
    if (qualifier == null) {
      if (other.qualifier != null) {
        return false;
      }
    } else if (!qualifier.equals(other.qualifier)) {
      return false;
    }
    if (targetName == null) {
      if (other.targetName != null) {
        return false;
      }
    } else if (!targetName.equals(other.targetName)) {
      return false;
    }
    return true;
  }

}
