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

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class DeltaTokenHandler {
  
  public static URI createDeltaLink(final String rawRequestUri, String deltaToken)
      throws ODataApplicationException {
    // Remove a maybe existing skiptoken, making sure that the query part is not empty.
    String deltalink = rawRequestUri.contains("?") ?
        rawRequestUri.replaceAll("(\\$|%24)deltatoken=.+&?", "").replaceAll("(\\?|&)$", "") :
        rawRequestUri;

    // Add a question mark or an ampersand, depending on the current query part.
        deltalink += deltalink.contains("?") ? '&' : '?';
        deltaToken = deltaToken.substring(0, 4);
    // Append the new skiptoken.
        deltalink += SystemQueryOptionKind.DELTATOKEN.toString().replace("$", "%24")   // poor man's percent encoding
        + '='
        + "%2A" +deltaToken  ;  // "%2A" is a percent-encoded asterisk
    
    try {
      return new URI(deltalink);
    } catch (final URISyntaxException e) {
      throw new ODataApplicationException("Exception while constructing delta link",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
    }
  }

}
