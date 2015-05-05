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

import java.util.Iterator;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;

public class SkipHandler {
  public static void applySkipSystemQueryHandler(final SkipOption skipOption, final EntityCollection entitySet)
      throws ODataApplicationException {

    if (skipOption != null) {
      if (skipOption.getValue() >= 0) {
        popAtMost(entitySet, skipOption.getValue());
      } else {
        throw new ODataApplicationException("Skip value must be positive", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT);
      }
    }
  }

  static void popAtMost(final EntityCollection entitySet, final int n) {
    final Iterator<Entity> iter = entitySet.getEntities().iterator();
    int i = 0;

    while (iter.hasNext() && i < n) {
      iter.next();
      iter.remove();
      i++;
    }
  }
}
