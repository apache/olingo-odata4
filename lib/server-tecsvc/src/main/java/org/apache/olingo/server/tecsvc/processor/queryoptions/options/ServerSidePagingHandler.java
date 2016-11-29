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

import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.SkipTokenOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class ServerSidePagingHandler {
  private static final int MAX_PAGE_SIZE = 10;
  private static final String ES_SERVER_SIDE_PAGING = "ESServerSidePaging";
  private static final String ES_STREAM_SERVER_SIDE_PAGING = "ESStreamServerSidePaging";

  /**
   * <p>Applies server-side paging to the given entity collection.</p>
   * <p>The next link is constructed and set in the data. It must support client-specified
   * page sizes. Therefore, the format <code>page*pageSize</code> (with a literal asterisk)
   * has been chosen for the skiptoken.</p> 
   * @param skipTokenOption   the current skiptoken option (from a previous response's next link)
   * @param entityCollection  the data
   * @param edmEntitySet      the EDM entity set to decide whether paging must be done
   * @param rawRequestUri     the request URI (used to construct the next link)
   * @param preferredPageSize the client's preference for page size
   * @return the chosen page size (or <code>null</code> if no paging has been done);
   *         could be used in the Preference-Applied HTTP header
   * @throws ODataApplicationException
   */
  public static Integer applyServerSidePaging(final SkipTokenOption skipTokenOption, EntityCollection entityCollection,
      final EdmEntitySet edmEntitySet, final String rawRequestUri, final Integer preferredPageSize)
      throws ODataApplicationException {

    if (edmEntitySet != null && shouldApplyServerSidePaging(edmEntitySet)) {
      final int pageSize = getPageSize(getPageSize(skipTokenOption), preferredPageSize);
      final int page = getPage(skipTokenOption);
      final int itemsToSkip = pageSize * page;

      if (itemsToSkip <= entityCollection.getEntities().size()) {
        SkipHandler.popAtMost(entityCollection, itemsToSkip);
        final int remainingItems = entityCollection.getEntities().size();
        TopHandler.reduceToSize(entityCollection, pageSize);

        // Determine if a new next Link has to be provided.
        if (remainingItems > pageSize) {
          entityCollection.setNext(createNextLink(rawRequestUri, page + 1, pageSize));
        }
      } else {
        throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
      }
      return pageSize;
    }
    return null;
  }

  private static URI createNextLink(final String rawRequestUri, final int page, final int pageSize)
      throws ODataApplicationException {
    // Remove a maybe existing skiptoken, making sure that the query part is not empty.
    String nextlink = rawRequestUri.contains("?") ?
        rawRequestUri.replaceAll("(\\$|%24)skiptoken=.+&?", "").replaceAll("(\\?|&)$", "") :
        rawRequestUri;

    // Add a question mark or an ampersand, depending on the current query part.
    nextlink += nextlink.contains("?") ? '&' : '?';

    // Append the new skiptoken.
    nextlink += SystemQueryOptionKind.SKIPTOKEN.toString().replace("$", "%24")  // poor man's percent encoding
        + '='
        + page + "%2A" + pageSize;  // "%2A" is a percent-encoded asterisk

    try {
      return new URI(nextlink);
    } catch (final URISyntaxException e) {
      throw new ODataApplicationException("Exception while constructing next link",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
    }
  }

  private static boolean shouldApplyServerSidePaging(final EdmEntitySet edmEntitySet) {
    return (ES_SERVER_SIDE_PAGING.equals(edmEntitySet.getName())||
        ES_STREAM_SERVER_SIDE_PAGING.equals(edmEntitySet.getName()));
  }

  private static int getPageSize(final int skipTokenPageSize, final Integer preferredPageSize) {
    return skipTokenPageSize > 0 ? skipTokenPageSize :
        preferredPageSize == null || preferredPageSize >= MAX_PAGE_SIZE ?
            MAX_PAGE_SIZE :
            preferredPageSize;
  }

  private static int getPageSize(final SkipTokenOption skipTokenOption) throws ODataApplicationException {
    if (skipTokenOption != null && skipTokenOption.getValue().length() >= 3
        && skipTokenOption.getValue().contains("*")) {
      final String value = skipTokenOption.getValue();
      try {
        return Integer.parseInt(value.substring(value.indexOf('*') + 1));
      } catch (final NumberFormatException e) {
        throw new ODataApplicationException("Invalid skip token", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT, e);
      }
    } else {
      return 0;
    }
  }

  private static int getPage(final SkipTokenOption skipTokenOption) throws ODataApplicationException {
    if (skipTokenOption != null && skipTokenOption.getValue().length() >= 3
        && skipTokenOption.getValue().contains("*")) {
      final String value = skipTokenOption.getValue();
      try {
        return Integer.parseInt(value.substring(0, value.indexOf('*')));
      } catch (final NumberFormatException e) {
        throw new ODataApplicationException("Invalid skip token", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT, e);
      }
    } else {
      return 0;
    }
  }
}
