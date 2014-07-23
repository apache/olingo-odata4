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
package org.apache.olingo.client.core.edm.xml;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.client.api.edm.xml.EnumType;
import org.apache.olingo.client.api.edm.xml.Member;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = EnumTypeDeserializer.class)
public abstract class AbstractEnumType extends AbstractEdmItem implements EnumType {

  private static final long serialVersionUID = 3130179997854833072L;

  private String name;

  private String underlyingType;

  private boolean flags;

  private final List<Member> members = new ArrayList<Member>();

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getUnderlyingType() {
    return underlyingType;
  }

  public void setUnderlyingType(final String underlyingType) {
    this.underlyingType = underlyingType;
  }

  @Override
  public boolean isFlags() {
    return flags;
  }

  public void setFlags(final boolean flags) {
    this.flags = flags;
  }

  @Override
  public Member getMember(final String name) {
    Member result = null;
    for (Member member : getMembers()) {
      if (name.equals(member.getName())) {
        result = member;
      }
    }
    return result;
  }

  @Override
  public Member getMember(final Integer value) {
    Member result = null;
    for (Member member : getMembers()) {
      if (value.equals(member.getValue())) {
        result = member;
      }
    }
    return result;
  }

  @Override
  public List<Member> getMembers() {
    return members;
  }

}
