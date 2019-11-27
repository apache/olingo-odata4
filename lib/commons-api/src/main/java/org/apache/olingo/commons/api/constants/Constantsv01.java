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
package org.apache.olingo.commons.api.constants;

import org.apache.olingo.commons.api.IConstants;

/**
 * Constant values related to the OData protocol.
 */
public final class Constantsv01 implements IConstants{

  // JSON stuff
  public static final String JSON_METADATA = "metadata";
  
  public static final String JSON_TYPE = "@type";

  public static final String JSON_ID = "@id";

  public static final String JSON_READ_LINK = "@readLink";

  public static final String JSON_EDIT_LINK = "@editLink";

  public static final String JSON_CONTEXT = "@context";

  public static final String JSON_ETAG = "@etag";

  public static final String JSON_MEDIA_ETAG = "@mediaEtag";

  public static final String JSON_MEDIA_CONTENT_TYPE = "@mediaContentType";

  public static final String JSON_MEDIA_READ_LINK = "@mediaReadLink";

  public static final String JSON_MEDIA_EDIT_LINK = "@mediaEditLink";

  public static final String JSON_METADATA_ETAG = "@metadataEtag";

  public static final String JSON_BIND_LINK_SUFFIX = "@bind";

  public static final String JSON_ASSOCIATION_LINK = "@associationLink";

  public static final String JSON_NAVIGATION_LINK = "@navigationLink";

  public static final String JSON_COUNT = "@count";

  public static final String JSON_NEXT_LINK = "@nextLink";

  public static final String JSON_DELTA_LINK = "@deltaLink";
  
  @Override
  public String getMetadata() {
    return JSON_METADATA;
  }

  @Override
  public String getType() {
    return JSON_TYPE;
  }

  @Override
  public String getId() {
    return JSON_ID;
  }

  @Override
  public String getReadLink() {
    return JSON_READ_LINK;
  }

  @Override
  public String getEditLink() {
    return JSON_EDIT_LINK;
  }

  @Override
  public String getContext() {
    return JSON_CONTEXT;
  }

  @Override
  public String getEtag() {
    return JSON_ETAG;
  }

  @Override
  public String getMediaEtag() {
    return JSON_MEDIA_ETAG;
  }

  @Override
  public String getMediaContentType() {
    return JSON_MEDIA_CONTENT_TYPE;
  }

  @Override
  public String getMediaReadLink() {
    return JSON_MEDIA_READ_LINK;
  }

  @Override
  public String getMediaEditLink() {
    return JSON_MEDIA_EDIT_LINK;
  }

  @Override
  public String getMetadataEtag() {
    return JSON_METADATA_ETAG;
  }

  @Override
  public String getBind() {
    return JSON_BIND_LINK_SUFFIX;
  }

  @Override
  public String getAssociationLink() {
    return JSON_ASSOCIATION_LINK;
  }

  @Override
  public String getNavigationLink() {
    return JSON_NAVIGATION_LINK;
  }

  @Override
  public String getCount() {
    return JSON_COUNT;
  }

  @Override
  public String getNextLink() {
    return JSON_NEXT_LINK;
  }

  @Override
  public String getDeltaLink() {
    return JSON_DELTA_LINK;
  }
}
