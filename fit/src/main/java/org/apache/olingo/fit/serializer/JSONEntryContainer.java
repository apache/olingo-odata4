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
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.core.data.JSONEntityDeserializer;
import org.apache.olingo.commons.core.data.JSONEntityImpl;
import org.apache.olingo.commons.core.data.JSONEntitySerializer;

@JsonDeserialize(using = JSONEntityDeserializer.class)
@JsonSerialize(using = JSONEntitySerializer.class)
public class JSONEntryContainer extends ResWrap<JSONEntityImpl> {

  public JSONEntryContainer(final ContextURL contextURL, final String metadataETag, final JSONEntityImpl object) {
    super(contextURL, metadataETag, object);
  }
}
