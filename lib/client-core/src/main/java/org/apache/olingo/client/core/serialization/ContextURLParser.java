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
package org.apache.olingo.client.core.serialization;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;

public class ContextURLParser {

  public static ContextURL parse(final URI contextURL) {
    if (contextURL == null) {
      return null;
    }

    final ContextURL.Builder contextUrl = ContextURL.with();

    String contextURLasString = contextURL.toASCIIString();

    boolean isEntity = false;
    if (contextURLasString.endsWith("/$entity") || contextURLasString.endsWith("/@Element")) {
      isEntity = true;
      contextUrl.suffix(Suffix.ENTITY);
      contextURLasString = contextURLasString.replace("/$entity", StringUtils.EMPTY).
          replace("/@Element", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$ref")) {
      contextUrl.suffix(Suffix.REFERENCE);
      contextURLasString = contextURLasString.replace("/$ref", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$delta")) {
      contextUrl.suffix(Suffix.DELTA);
      contextURLasString = contextURLasString.replace("/$delta", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$deletedEntity")) {
      contextUrl.suffix(Suffix.DELTA_DELETED_ENTITY);
      contextURLasString = contextURLasString.replace("/$deletedEntity", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$link")) {
      contextUrl.suffix(Suffix.DELTA_LINK);
      contextURLasString = contextURLasString.replace("/$link", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$deletedLink")) {
      contextUrl.suffix(Suffix.DELTA_DELETED_LINK);
      contextURLasString = contextURLasString.replace("/$deletedLink", StringUtils.EMPTY);
    }

    contextUrl.serviceRoot(URI.create(StringUtils.substringBefore(contextURLasString, Constants.METADATA)));

    final String rest = StringUtils.substringAfter(contextURLasString, Constants.METADATA + "#");

    String firstToken;
    String entitySetOrSingletonOrType;
    if (rest.startsWith("Collection(")) {
      firstToken = rest.substring(0, rest.indexOf(')') + 1);
      entitySetOrSingletonOrType = firstToken;
    } else {
      final int openParIdx = rest.indexOf('(');
      if (openParIdx == -1) {
        firstToken = StringUtils.substringBeforeLast(rest, "/");

        entitySetOrSingletonOrType = firstToken;
      } else {
        firstToken = isEntity ? rest : StringUtils.substringBeforeLast(rest, ")") + ")";

        final List<String> parts = new ArrayList<String>();
        for (String split : firstToken.split("\\)/")) {
          parts.add(split.replaceAll("\\(.*", ""));
        }
        entitySetOrSingletonOrType = StringUtils.join(parts, '/');
        final int commaIdx = firstToken.indexOf(',');
        if (commaIdx != -1) {
          contextUrl.selectList(firstToken.substring(openParIdx + 1, firstToken.length() - 1));
        }
      }
    }
    contextUrl.entitySetOrSingletonOrType(entitySetOrSingletonOrType);

    final int slashIdx = entitySetOrSingletonOrType.lastIndexOf('/');
    if (slashIdx != -1 && entitySetOrSingletonOrType.substring(slashIdx + 1).indexOf('.') != -1) {
      contextUrl.entitySetOrSingletonOrType(entitySetOrSingletonOrType.substring(0, slashIdx));
      contextUrl.derivedEntity(entitySetOrSingletonOrType.substring(slashIdx + 1));
    }

    if (!firstToken.equals(rest)) {
      final String[] pathElems = StringUtils.substringAfter(rest, "/").split("/");
      if (pathElems.length > 0 && pathElems[0].length() > 0) {
        if (pathElems[0].indexOf('.') == -1) {
          contextUrl.navOrPropertyPath(pathElems[0]);
        } else {
          contextUrl.derivedEntity(pathElems[0]);
        }

        if (pathElems.length > 1) {
          contextUrl.navOrPropertyPath(pathElems[1]);
        }
      }
    }

    return contextUrl.build();
  }
}
