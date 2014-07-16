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
package org.apache.olingo.commons.core.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.domain.ODataErrorDetail;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JsonODataErrorDeserializer extends JsonDeserializer {

  public JsonODataErrorDeserializer(final ODataServiceVersion version, final boolean serverMode) {
    super(version, serverMode);
  }

  protected ODataError doDeserialize(final JsonParser parser) throws IOException {

    final ODataError error = new ODataError();

    final ObjectNode tree = parser.getCodec().readTree(parser);
    if (tree.has(jsonError)) {
      final JsonNode errorNode = tree.get(jsonError);

      if (errorNode.has(Constants.ERROR_CODE)) {
        error.setCode(errorNode.get(Constants.ERROR_CODE).textValue());
      }
      if (errorNode.has(Constants.ERROR_MESSAGE)) {
        final JsonNode message = errorNode.get(Constants.ERROR_MESSAGE);
        if (message.isValueNode()) {
          error.setMessage(message.textValue());
        } else if (message.isObject()) {
          error.setMessage(message.get(Constants.VALUE).asText());
        }
      }
      if (errorNode.has(Constants.ERROR_TARGET)) {
        error.setTarget(errorNode.get(Constants.ERROR_TARGET).textValue());
      }
      if (errorNode.hasNonNull(Constants.ERROR_DETAILS)) {
        List<ODataErrorDetail> details = new ArrayList<ODataErrorDetail>();
        JsonODataErrorDetailDeserializer detailDeserializer =
            new JsonODataErrorDetailDeserializer(version, serverMode);
        for (JsonNode jsonNode : errorNode.get(Constants.ERROR_DETAILS)) {
          details.add(detailDeserializer.doDeserialize(jsonNode.traverse(parser.getCodec()))
              .getPayload());
        }

        error.setDetails(details);
      }
      if (errorNode.hasNonNull(Constants.ERROR_INNERERROR)) {
        HashMap<String, String> innerErrorMap = new HashMap<String, String>();
        final JsonNode innerError = errorNode.get(Constants.ERROR_INNERERROR);
        for (final Iterator<String> itor = innerError.fieldNames(); itor.hasNext();) {
          final String keyTmp = itor.next();
          final String val = innerError.get(keyTmp).toString();
          innerErrorMap.put(keyTmp, val);
        }
        error.setInnerError(innerErrorMap);
      }
    }

    return error;
  }
}
