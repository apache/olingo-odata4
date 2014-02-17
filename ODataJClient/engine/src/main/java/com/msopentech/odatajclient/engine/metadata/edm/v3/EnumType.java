/**
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
package com.msopentech.odatajclient.engine.metadata.edm.v3;

import com.msopentech.odatajclient.engine.metadata.edm.AbstractEnumType;
import java.util.ArrayList;
import java.util.List;

public class EnumType extends AbstractEnumType {

    private static final long serialVersionUID = 8967396195669128419L;

    private final List<Member> members = new ArrayList<Member>();

    @Override
    public List<Member> getMembers() {
        return members;
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

}
