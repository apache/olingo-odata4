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
package org.apache.olingo.commons.core.serialization;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;

public class ContextURLParser {
  public static ContextURL parse(final URI contextURL) {
    if (contextURL == null) {
      return null;
    }

    ContextURL.Builder builder = ContextURL.Builder.create();

    String contextURLasString = contextURL.toASCIIString();

    if (contextURLasString.endsWith("/$entity") || contextURLasString.endsWith("/@Element")) {
      builder.suffix(Suffix.ENTITY);
      contextURLasString = contextURLasString.replace("/$entity", StringUtils.EMPTY)
              .replace("/@Element", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$ref")) {
      builder.suffix(Suffix.REFERENCE);
      contextURLasString = contextURLasString.replace("/$ref", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$delta")) {
      builder.suffix(Suffix.DELTA);
      contextURLasString = contextURLasString.replace("/$delta", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$deletedEntity")) {
      builder.suffix(Suffix.DELTA_DELETED_ENTITY);
      contextURLasString = contextURLasString.replace("/$deletedEntity", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$link")) {
      builder.suffix(Suffix.DELTA_LINK);
      contextURLasString = contextURLasString.replace("/$link", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$deletedLink")) {
      builder.suffix(Suffix.DELTA_DELETED_LINK);
      contextURLasString = contextURLasString.replace("/$deletedLink", StringUtils.EMPTY);
    }

    builder.serviceRoot(URI.create(StringUtils.substringBefore(contextURLasString, Constants.METADATA)));

    final String rest = StringUtils.substringAfter(contextURLasString, Constants.METADATA + "#");

    String firstToken;
    String entitySetOrSingletonOrType = null;
    if (rest.startsWith("Collection(")) {
      firstToken = rest.substring(0, rest.indexOf(')') + 1);
      entitySetOrSingletonOrType = firstToken;
    } else {
      final int openParIdx = rest.indexOf('(');
      if (openParIdx == -1) {
        firstToken = StringUtils.substringBefore(rest, "/");

        entitySetOrSingletonOrType = firstToken;
      } else {
        firstToken = StringUtils.substringBeforeLast(rest, ")") + ")";

        entitySetOrSingletonOrType = firstToken.substring(0, openParIdx);
        final int commaIdx = firstToken.indexOf(',');
        if (commaIdx != -1) {
          builder.selectList(firstToken.substring(openParIdx + 1, firstToken.length() - 1));
        }
      }
    }
    builder.entitySetOrSingletonOrType(entitySetOrSingletonOrType);

    final int slashIdx = entitySetOrSingletonOrType.lastIndexOf('/');
    if (slashIdx != -1 && entitySetOrSingletonOrType.substring(slashIdx + 1).indexOf('.') != -1) {
      final String clone = entitySetOrSingletonOrType;
      builder.entitySetOrSingletonOrType(clone.substring(0, slashIdx));
      builder.derivedEntity(clone.substring(slashIdx + 1));
    }

    if (!firstToken.equals(rest)) {
      final String[] pathElems = StringUtils.substringAfter(rest, "/").split("/");
      if (pathElems.length > 0 && pathElems[0].length() > 0) {
        if (pathElems[0].indexOf('.') == -1) {
          builder.navOrPropertyPath(pathElems[0]);
        } else {
          builder.derivedEntity(pathElems[0]);
        }

        if (pathElems.length > 1) {
          builder.navOrPropertyPath(pathElems[1]);
        }
      }
    }

    return builder.build();
  }
}
