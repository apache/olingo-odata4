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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import java.util.Map;
import org.apache.olingo.client.api.data.Error;

/**
 * This class represents an OData error returned as JSON.
 */
public class XMLErrorImpl extends AbstractPayloadObject implements Error {

  private static final long serialVersionUID = -3476499168507242932L;

  @JacksonXmlText(false)
  private String code;

  @JsonProperty
  private Message message;

  @JsonProperty(required = false)
  private InnerError innererror;

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

  @JsonIgnore
  @Override
  public String getMessageLang() {
    return this.message == null ? null : this.message.getLang();
  }

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

  @JsonIgnore
  @Override
  public String getInnerErrorMessage() {
    return this.innererror == null ? null : this.innererror.getMessage().getValue();
  }

  @JsonIgnore
  @Override
  public String getInnerErrorType() {
    return this.innererror == null ? null : this.innererror.getType().getValue();
  }

  @JsonIgnore
  @Override
  public String getInnerErrorStacktrace() {
    return this.innererror == null ? null : this.innererror.getStacktrace().getValue();
  }

  static class TextChildContainer extends AbstractPayloadObject {

    private static final long serialVersionUID = -8908394095210115904L;

    public TextChildContainer() {
      super();
    }

    public TextChildContainer(final String value) {
      super();
      this.value = value;
    }

    @JsonCreator
    public TextChildContainer(final Map<String, Object> props) {
      super();
      this.value = (String) props.get("");
    }

    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(final String value) {
      this.value = value;
    }
  }

  /**
   * Error message.
   */
  public static class Message extends TextChildContainer {

    private static final long serialVersionUID = 2577818040815637859L;

    private String lang;

    public Message() {
      super();
    }

    @JsonCreator
    public Message(final Map<String, Object> props) {
      super(props);
      this.lang = (String) props.get("lang");
    }

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
  }

  /**
   * Inner error.
   */
  static class InnerError extends AbstractPayloadObject {

    private static final long serialVersionUID = -3920947476143537640L;

    private TextChildContainer message;

    private TextChildContainer type;

    private TextChildContainer stacktrace;

    private InnerError internalexception;

    public TextChildContainer getMessage() {
      return message;
    }

    public void setMessage(final TextChildContainer message) {
      this.message = message;
    }

    public TextChildContainer getType() {
      return type;
    }

    public void setType(final TextChildContainer type) {
      this.type = type;
    }

    public TextChildContainer getStacktrace() {
      return stacktrace;
    }

    public void setStacktrace(final TextChildContainer stacktrace) {
      this.stacktrace = stacktrace;
    }

    public InnerError getInternalexception() {
      return internalexception;
    }

    public void setInternalexception(final InnerError internalexception) {
      this.internalexception = internalexception;
    }
  }
}
