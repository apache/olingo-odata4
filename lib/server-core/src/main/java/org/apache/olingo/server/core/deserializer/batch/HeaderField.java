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
package org.apache.olingo.server.core.deserializer.batch;

import java.util.ArrayList;
import java.util.List;

public class HeaderField implements Cloneable {
  private final String fieldName;
  private final int lineNumber;
  private List<String> values;

  public HeaderField(final String fieldName, final int lineNumber) {
    this(fieldName, new ArrayList<String>(), lineNumber);
  }

  public HeaderField(final String fieldName, final List<String> values, final int lineNumber) {
    this.fieldName = fieldName;
    this.values = values;
    this.lineNumber = lineNumber;
  }

  public String getFieldName() {
    return fieldName;
  }

  public List<String> getValues() {
    return values;
  }

  public String getValue() {
    final StringBuilder result = new StringBuilder();

    for (final String value : values) {
      result.append(value);
      result.append(", ");
    }

    if (result.length() > 0) {
      result.delete(result.length() - 2, result.length());
    }

    return result.toString();
  }

  @Override
  public HeaderField clone() throws CloneNotSupportedException{
    HeaderField clone = (HeaderField) super.clone();
    clone.values = new ArrayList<String>(values.size());
    clone.values.addAll(values);
    return clone;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
    result = prime * result + lineNumber;
    result = prime * result + ((values == null) ? 0 : values.hashCode());
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
    HeaderField other = (HeaderField) obj;
    if (fieldName == null) {
      if (other.fieldName != null) {
        return false;
      }
    } else if (!fieldName.equals(other.fieldName)) {
      return false;
    }
    if (lineNumber != other.lineNumber) {
      return false;
    }
    if (values == null) {
      if (other.values != null) {
        return false;
      }
    } else if (!values.equals(other.values)) {
      return false;
    }
    return true;
  }
}
