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
package org.apache.olingo.server.core.etag;

import java.util.Collection;
import java.util.Collections;

import org.apache.olingo.server.api.etag.ETagHelper;
import org.apache.olingo.server.api.etag.PreconditionException;

public class ETagHelperImpl implements ETagHelper {

  @Override
  public boolean checkReadPreconditions(final String eTag,
      final Collection<String> ifMatchHeaders, final Collection<String> ifNoneMatchHeaders)
          throws PreconditionException {
    if (eTag != null) {
      final ETagInformation ifMatch = createETagInformation(ifMatchHeaders);
      if (!ifMatch.isMatchedBy(eTag) && !ifMatch.getETags().isEmpty()) {
        throw new PreconditionException("The If-Match precondition is not fulfilled.",
            PreconditionException.MessageKeys.FAILED);
      }
      return createETagInformation(ifNoneMatchHeaders).isMatchedBy(eTag);
    }
    return false;
  }

  @Override
  public void checkChangePreconditions(final String eTag,
      final Collection<String> ifMatchHeaders, final Collection<String> ifNoneMatchHeaders)
          throws PreconditionException {
    if (eTag != null) {
      final ETagInformation ifMatch = createETagInformation(ifMatchHeaders);
      final ETagInformation ifNoneMatch = createETagInformation(ifNoneMatchHeaders);
      if (!ifMatch.isMatchedBy(eTag) && !ifMatch.getETags().isEmpty()
          || ifNoneMatch.isMatchedBy(eTag)) {
        throw new PreconditionException("The preconditions are not fulfilled.",
            PreconditionException.MessageKeys.FAILED);
      }
    }
  }

  /**
   * Creates ETag information from the values of a HTTP header
   * containing a list of entity tags or a single star character, i.e.,
   * <code>If-Match</code> and <code>If-None-Match</code>.
   * @param values the collection of header values
   * @return an {@link ETagInformation} instance
   */
  protected ETagInformation createETagInformation(final Collection<String> values) {
    final Collection<String> eTags = ETagParser.parse(values);
    final boolean isAll = eTags.size() == 1 && "*".equals(eTags.iterator().next());
    return new ETagInformation(isAll,
        isAll ? Collections.<String> emptySet() : Collections.unmodifiableCollection(eTags));
  }
}
