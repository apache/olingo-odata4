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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.Encoder;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.SkipTokenOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class ServerSidePagingHandler {
  private static final int MAX_PAGE_SIZE = 10;

  public static void
      applyServerSidePaging(final EntitySet entitySet, final String rawRequestUri, final UriInfo uriInfo)
          throws ODataApplicationException {
    if (shouldApplyServerSidePaging(entitySet)) {
      final int maxPageSize = getMaxPageSize();
      final int page = getPage(uriInfo.getSkipTokenOption());
      final int itemsToSkip = maxPageSize * page;

      if (itemsToSkip <= entitySet.getEntities().size()) {
        SkipHandler.popAtMost(entitySet, itemsToSkip);
        final int remainingItems = entitySet.getEntities().size();
        TopHandler.reduceToSize(entitySet, maxPageSize);

        // Determine if a new next Link has to be provided
        if (remainingItems > maxPageSize) {
          entitySet.setNext(createNextLink(uriInfo, rawRequestUri, page + 1));
        }
      } else {
        throw new ODataApplicationException("Invalid skiptoken", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT);
      }
    }
  }

  private static URI createNextLink(final UriInfo uriInfo, final String rawRequestUri, final Integer page)
      throws ODataApplicationException {

    try {
      // Remove skip token
      String nextlink = rawRequestUri;

      // Remove a may existing skiptoken, make sure that the query part is not empty
      if (rawRequestUri.contains("?")) {
        nextlink = rawRequestUri.replaceAll("(\\$|%24)skiptoken=.+&?", "").replaceAll("(\\?|&)$", "");
      }

      // Add a question mark or an ampersand, depending of the current query part
      if (!nextlink.contains("?")) {
        nextlink = nextlink + "?";
      } else {
        nextlink = nextlink + "&";
      }

      // Append the new nextlink
      return new URI(nextlink + Encoder.encode(SystemQueryOptionKind.SKIPTOKEN.toString()) + "="
          + Encoder.encode(page.toString()));

    } catch (URISyntaxException e) {
      throw new ODataApplicationException("Exception while constructing next link",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
  }

  private static int getPage(SkipTokenOption skipTokenOption) throws ODataApplicationException {
    if (skipTokenOption != null) {
      try {
        return Integer.parseInt(skipTokenOption.getValue());
      } catch (NumberFormatException e) {
        throw new ODataApplicationException("Invalid skip token", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT);
      }
    } else {
      return 0;
    }
  }

  private static boolean shouldApplyServerSidePaging(final EntitySet entitySet) {
    return true;
  }

  private static int getMaxPageSize() {
    // TODO Consider odata.maxpagesize preference?
    return MAX_PAGE_SIZE;
  }
}
