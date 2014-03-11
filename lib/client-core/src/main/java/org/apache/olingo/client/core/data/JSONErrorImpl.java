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
package org.apache.olingo.client.core.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.client.api.data.Error;

/**
 * This class represents an OData error returned as JSON.
 */
public class JSONErrorImpl extends AbstractPayloadObject implements Error {

  private static final long serialVersionUID = -3476499168507242932L;

  /**
   * Error message.
   */
  public static class Message extends AbstractPayloadObject {

    private static final long serialVersionUID = 2577818040815637859L;

    private String lang;

    private String value;

    /**
     * Gets language.
     *
     * @return language.
     */
    public String getLang() {
      return lang;
    }

    /**
     * Sets language.
     *
     * @param lang language.
     */
    public void setLang(final String lang) {
      this.lang = lang;
    }

    /**
     * Gets message.
     *
     * @return message.
     */
    public String getValue() {
      return value;
    }

    /**
     * Sets message.
     *
     * @param value message.
     */
    public void setValue(final String value) {
      this.value = value;
    }
  }

  /**
   * Inner error.
   */
  static class InnerError extends AbstractPayloadObject {

    private static final long serialVersionUID = -3920947476143537640L;

    private String message;

    private String type;

    private String stacktrace;

    private InnerError internalexception;

    /**
     * Gets inner message.
     *
     * @return message.
     */
    public String getMessage() {
      return message;
    }

    /**
     * Sets inner message.
     *
     * @param message message.
     */
    public void setMessage(final String message) {
      this.message = message;
    }

    /**
     * Gets type.
     *
     * @return type.
     */
    public String getType() {
      return type;
    }

    /**
     * Sets type.
     *
     * @param type type.
     */
    public void setType(final String type) {
      this.type = type;
    }

    /**
     * Gets stack-trace.
     *
     * @return stack-trace.
     */
    public String getStacktrace() {
      return stacktrace;
    }

    /**
     * Sets stack-trace.
     *
     * @param stacktrace stack-trace.
     */
    public void setStacktrace(final String stacktrace) {
      this.stacktrace = stacktrace;
    }

    public InnerError getInternalexception() {
      return internalexception;
    }

    public void setInternalexception(final InnerError internalexception) {
      this.internalexception = internalexception;
    }
  }

  private String code;

  @JsonProperty(value = "message")
  private Message message;

  @JsonProperty(value = "innererror", required = false)
  private InnerError innererror;

  /**
   * {@inheritDoc }
   */
  @Override
  public String getCode() {
    return code;
  }

  /**
   * Sets error code.
   *
   * @param code error code.
   */
  public void setCode(final String code) {
    this.code = code;
  }

  /**
   * {@inheritDoc }
   */
  @JsonIgnore
  @Override
  public String getMessageLang() {
    return this.message == null ? null : this.message.getLang();
  }

  /**
   * {@inheritDoc }
   */
  @JsonIgnore
  @Override
  public String getMessageValue() {
    return this.message == null ? null : this.message.getValue();
  }

  /**
   * Sets the value of the message property.
   *
   * @param value allowed object is {@link Error.Message }
   *
   */
  public void setMessage(final Message value) {
    this.message = value;
  }

  /**
   * {@inheritDoc }
   */
  @JsonIgnore
  @Override
  public String getInnerErrorMessage() {
    return this.innererror == null ? null : this.innererror.getMessage();
  }

  /**
   * {@inheritDoc }
   */
  @JsonIgnore
  @Override
  public String getInnerErrorType() {
    return this.innererror == null ? null : this.innererror.getType();
  }

  /**
   * {@inheritDoc }
   */
  @JsonIgnore
  @Override
  public String getInnerErrorStacktrace() {
    return this.innererror == null ? null : this.innererror.getStacktrace();
  }
}
