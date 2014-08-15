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
package org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;
import org.apache.olingo.ext.proxy.api.AbstractEntityKey;
//CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.CompoundKey
public class MessageKey extends AbstractEntityKey {

  private static final long serialVersionUID = 3366500795925894331L;

  private java.lang.String _fromUsername;

    @CompoundKeyElement(name = "FromUsername", position = 0)
    public java.lang.String getFromUsername() {
        return _fromUsername;
    }

    public void setFromUsername(java.lang.String _fromUsername) {
        this._fromUsername = _fromUsername;
    }

    private java.lang.Integer _messageId;

    @CompoundKeyElement(name = "MessageId", position = 1)
    public java.lang.Integer getMessageId() {
        return _messageId;
    }

    public void setMessageId(java.lang.Integer _messageId) {
        this._messageId = _messageId;
    }
}
