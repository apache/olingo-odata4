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
package org.apache.olingo.client.api.domain;

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

import javax.xml.datatype.Duration;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.ODataClient;

/**
 * OData primitive property value.
 */
public class ODataPrimitiveValue extends ODataValue {

  private static final long serialVersionUID = 2841837627899878223L;

  protected abstract static class AbstractBuilder {

    private final ODataClient client;

    /**
     * Constructor.
     */
    public AbstractBuilder(final ODataClient client) {
      this.client = client;
    }

    public AbstractBuilder isSupported(final ODataJClientEdmPrimitiveType type) {
      if (type != null && !ArrayUtils.contains(type.getSupportedVersions(), client.getServiceVersion())) {
        throw new IllegalArgumentException(String.format(
                "Type %s not supported by the current OData working version", type.toString()));
      }

      return this;
    }
  }

  /**
   * Primitive value builder.
   */
  public static class Builder extends AbstractBuilder {

    private final ODataPrimitiveValue opv;

    /**
     * Constructor.
     */
    public Builder(final ODataClient client) {
      super(client);
      this.opv = new ODataPrimitiveValue(client);
    }

    /**
     * Sets the given value provided as a text.
     *
     * @param text value.
     * @return the current builder.
     */
    public Builder setText(final String text) {
      this.opv.text = text;
      return this;
    }

    /**
     * Sets the actual object value.
     *
     * @param value value.
     * @return the current builder.
     */
    public Builder setValue(final Object value) {
      this.opv.value = value;
      return this;
    }

    /**
     * Sets actual value type.
     *
     * @param type type.
     * @return the current builder.
     */
    public Builder setType(final ODataJClientEdmPrimitiveType type) {
      isSupported(type);

      if (type == ODataJClientEdmPrimitiveType.Stream) {
        throw new IllegalArgumentException(String.format(
                "Cannot build a primitive value for %s", ODataJClientEdmPrimitiveType.Stream.toString()));
      }

      this.opv.type = type;
      return this;
    }

    /**
     * Builds the primitive value.
     *
     * @return <code>ODataPrimitiveValue</code> object.
     */
    public ODataPrimitiveValue build() {
      if (this.opv.text == null && this.opv.value == null) {
        throw new IllegalArgumentException("Must provide either text or value");
      }
      if (this.opv.text != null && this.opv.value != null) {
        throw new IllegalArgumentException("Cannot provide both text and value");
      }

      if (this.opv.type == null) {
        this.opv.type = ODataJClientEdmPrimitiveType.String;
      }

      if (this.opv.type.isGeospatial()) {
        throw new IllegalArgumentException(
                "Use " + ODataGeospatialValue.class.getSimpleName() + " for geospatial types");
      }

      if (this.opv.value instanceof Timestamp) {
        this.opv.value = ODataTimestamp.getInstance(this.opv.type, (Timestamp) this.opv.value);
      } else if (this.opv.value instanceof Date) {
        this.opv.value = ODataTimestamp.getInstance(this.opv.type,
                new Timestamp(((Date) this.opv.value).getTime()));
      }
      if (this.opv.value instanceof Duration) {
        this.opv.value = new ODataDuration((Duration) this.opv.value);
      }

      if (this.opv.value != null && !this.opv.type.javaType().isAssignableFrom(this.opv.value.getClass())) {
        throw new IllegalArgumentException("Provided value is not compatible with " + this.opv.type.toString());
      }

      if (this.opv.text != null) {
        this.opv.parseText();
      }
      if (this.opv.value != null) {
        this.opv.formatValue();
      }

      return this.opv;
    }
  }

  protected ODataClient client;

  /**
   * Text value.
   */
  private String text;

  /**
   * Actual value.
   */
  protected Object value;

  /**
   * Value type.
   */
  protected ODataJClientEdmPrimitiveType type;

  /**
   * Protected constructor, need to use the builder to instantiate this class.
   *
   * @see Builder
   */
  protected ODataPrimitiveValue(final ODataClient client) {
    super();
    this.client = client;
  }

  /**
   * Parses given text as object value.
   */
  private void parseText() {
    switch (this.type) {
      case Null:
        this.value = null;
        break;

      case Binary:
        this.value = Base64.decodeBase64(this.toString());
        break;

      case SByte:
        this.value = Byte.parseByte(this.toString());
        break;

      case Boolean:
        this.value = Boolean.parseBoolean(this.toString());
        break;

      case Date:
      case DateTime:
      case DateTimeOffset:
        this.value = ODataTimestamp.parse(this.type, this.toString());
        break;

      case Time:
      case TimeOfDay:
        this.value = new ODataDuration(this.toString());
        break;

      case Decimal:
        this.value = new BigDecimal(this.toString());
        break;

      case Single:
        this.value = Float.parseFloat(this.toString());
        break;

      case Double:
        this.value = Double.parseDouble(this.toString());
        break;

      case Guid:
        this.value = UUID.fromString(this.toString());
        break;

      case Int16:
        this.value = Short.parseShort(this.toString());
        break;

      case Byte:
      case Int32:
        this.value = Integer.parseInt(this.toString());
        break;

      case Int64:
        this.value = Long.parseLong(this.toString());
        break;

      case Stream:
        this.value = URI.create(this.toString());
        break;

      case String:
        this.value = this.toString();
        break;

      default:
    }
  }

  /**
   * Format given value as text.
   */
  private void formatValue() {
    switch (this.type) {
      case Null:
        this.text = StringUtils.EMPTY;
        break;

      case Binary:
        this.text = Base64.encodeBase64String(this.<byte[]>toCastValue());
        break;

      case SByte:
        this.text = this.<Byte>toCastValue().toString();
        break;

      case Boolean:
        this.text = this.<Boolean>toCastValue().toString();
        break;

      case Date:
      case DateTime:
      case DateTimeOffset:
        this.text = this.<ODataTimestamp>toCastValue().toString();
        break;

      case Time:
      case TimeOfDay:
        this.text = this.<ODataDuration>toCastValue().toString();
        break;

      case Decimal:
        this.text = new DecimalFormat(this.type.pattern()).format(this.<BigDecimal>toCastValue());
        break;

      case Single:
        this.text = new DecimalFormat(this.type.pattern()).format(this.<Float>toCastValue());
        break;

      case Double:
        this.text = new DecimalFormat(this.type.pattern()).format(this.<Double>toCastValue());
        break;

      case Guid:
        this.text = this.<UUID>toCastValue().toString();
        break;

      case Int16:
        this.text = this.<Short>toCastValue().toString();
        break;

      case Byte:
      case Int32:
        this.text = this.<Integer>toCastValue().toString();
        break;

      case Int64:
        this.text = this.<Long>toCastValue().toString();
        break;

      case Stream:
        this.text = this.<URI>toCastValue().toASCIIString();
        break;

      case String:
        this.text = this.<String>toCastValue();
        break;

      default:
    }
  }

  /**
   * Gets type name.
   *
   * @return type name.
   */
  public String getTypeName() {
    return type.toString();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public String toString() {
    return this.text;
  }

  /**
   * Gets actual primitive value.
   *
   * @return
   */
  public Object toValue() {
    return this.value;
  }

  /**
   * Casts primitive value.
   *
   * @param <T> cast.
   * @return casted value.
   */
  @SuppressWarnings("unchecked")
  public <T> T toCastValue() {
    return (T) type.javaType().cast(toValue());
  }
}
