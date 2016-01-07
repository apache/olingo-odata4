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
package org.apache.olingo.server.api.deserializer.batch;

/**
 * Options for the batch deserializer.
 * @see org.apache.olingo.server.api.deserializer.FixedFormatDeserializer #parseBatchRequest(java.io.InputStream,
 * String, BatchOptions)
 */
public final class BatchOptions {
  private boolean isStrict = true;
  private String rawBaseUri = "";
  private String rawServiceResolutionUri = "";

  private BatchOptions() {}

  /**
   * Returns if the batch parsing is strict.
   * Default is true.
   * @return true if parsing is strict
   */
  public boolean isStrict() {
    return isStrict;
  }

  /**
   * Gets raw base URI.
   * @see org.apache.olingo.server.api.ODataRequest#getRawBaseUri()
   */
  public String getRawBaseUri() {
    return rawBaseUri;
  }

  /**
   * Gets raw service resolution URI.
   * @see org.apache.olingo.server.api.ODataRequest#getRawServiceResolutionUri()
   */
  public String getRawServiceResolutionUri() {
    return rawServiceResolutionUri;
  }

  /**
   * Creates a new BatchOptions builder.
   * @return new BatchOptions builder instance
   */
  public static Builder with() {
    return new Builder();
  }

  /**
   * BatchOptions builder
   */
  public static class Builder {
    private BatchOptions options;

    /** Initializes the options builder. */
    public Builder() {
      options = new BatchOptions();
    }

    /**
     * @see BatchOptions#isStrict()
     */
    public Builder isStrict(final boolean isStrict) {
      options.isStrict = isStrict;
      return this;
    }

    /**
     * @see org.apache.olingo.server.api.ODataRequest#getRawBaseUri()
     */
    public Builder rawBaseUri(final String baseUri) {
      options.rawBaseUri = baseUri;
      return this;
    }

    /**
     * @see org.apache.olingo.server.api.ODataRequest#getRawServiceResolutionUri()
     */
    public Builder rawServiceResolutionUri(final String serviceResolutionUri) {
      options.rawServiceResolutionUri = serviceResolutionUri;
      return this;
    }

    /**
     * Creates a new BatchOptions instance.
     * @return new BatchOptions instance
     */
    public BatchOptions build() {
      return options;
    }
  }
}
