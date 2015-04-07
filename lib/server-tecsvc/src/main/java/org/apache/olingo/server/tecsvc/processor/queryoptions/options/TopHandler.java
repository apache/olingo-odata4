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
package org.apache.olingo.server.tecsvc.processor.queryoptions.options;

import java.util.Locale;

import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.TopOption;

public class TopHandler {
  public static void applyTopSystemQueryOption(final TopOption topOption, final EntityCollection entitySet)
      throws ODataApplicationException {

    if (topOption != null) {
      if (topOption.getValue() >= 0) {
        reduceToSize(entitySet, topOption.getValue());
      } else {
        throw new ODataApplicationException("Top value must be positive", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT);
      }
    }
  }

  static void reduceToSize(final EntityCollection entitySet, final int n) {
    while (entitySet.getEntities().size() > n) {
      entitySet.getEntities().remove(entitySet.getEntities().size() - 1);
    }
  }
}
