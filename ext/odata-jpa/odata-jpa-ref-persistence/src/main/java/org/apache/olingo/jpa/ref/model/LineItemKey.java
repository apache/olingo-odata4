/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.jpa.ref.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineItemKey implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "SO_ID", nullable = false)
  private long soId;

  @Column(name = "LI_ID", unique = true)
  private long liId;

  public LineItemKey() {}

  public LineItemKey(final long liId) {
    super();
    this.liId = liId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (liId ^ (liId >>> 32));
    result = prime * result + (int) (soId ^ (soId >>> 32));
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    LineItemKey other = (LineItemKey) obj;
    if (liId != other.liId) {
      return false;
    }
    if (soId != other.soId) {
      return false;
    }
    return true;
  }

  public long getSoId() {
    return soId;
  }

  public void setSoId(final long soId) {
    this.soId = soId;
  }

  public long getLiId() {
    return liId;
  }

  public void setLiId(final long liId) {
    this.liId = liId;
  }
}
