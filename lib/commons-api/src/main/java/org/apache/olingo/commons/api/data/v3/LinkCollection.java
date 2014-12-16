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
package org.apache.olingo.commons.api.data.v3;

import java.net.URI;
import java.util.List;

/**
 * REST resource for an <tt>ODataLinkCollection</tt>.
 */
public interface LinkCollection {

  /**
   * Smart management of different JSON format produced by OData services when
   * <tt>$links</tt> is a single or a collection property.
   * 
   * @return list of URIs for <tt>$links</tt>
   */
  List<URI> getLinks();

  /**
   * Sets next link.
   * 
   * @param next next link.
   */
  void setNext(final URI next);

  /**
   * Gets next link if exists.
   * 
   * @return next link if exists; null otherwise.
   */
  URI getNext();
}
