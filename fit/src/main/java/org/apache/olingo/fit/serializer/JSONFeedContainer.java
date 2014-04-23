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
package org.apache.olingo.fit.serializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.core.data.JSONFeedDeserializer;
import org.apache.olingo.commons.core.data.JSONFeedImpl;
import org.apache.olingo.commons.core.data.JSONFeedSerializer;

@JsonDeserialize(using = JSONFeedDeserializer.class)
@JsonSerialize(using = JSONFeedSerializer.class)
public class JSONFeedContainer extends Container<JSONFeedImpl> {

  public JSONFeedContainer(final URI contextURL, final String metadataETag, final JSONFeedImpl object) {
    super(contextURL, metadataETag, object);
  }
}
