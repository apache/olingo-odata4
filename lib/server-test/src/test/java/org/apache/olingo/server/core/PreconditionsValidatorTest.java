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
package org.apache.olingo.server.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.server.api.CustomETagSupport;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class PreconditionsValidatorTest {

  @Test
  public void simpleEntity() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void boundActionOnEsKeyNav() throws Exception {
    UriInfo uriInfo =
        new Parser().parseUri("ESKeyNav(1)/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void simpleEntityValue() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESMedia(1)/$value", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, "*", "*").validatePreconditions(true);
  }

  @Test
  public void simpleEntityValueValidationNotActiveForMedia() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESMedia(1)/$value", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(true, false), uriInfo, null, null).validatePreconditions(true);
  }

  @Test
  public void EntityAndToOneNavigation() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)/NavPropertyETTwoPrimOne", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("ESTwoPrim"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void simpleEntityPreconditionsReqException() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)", null, null, getEdm());
    try {
      new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(false);
      fail("Expected a PreconditionRequiredException but was not thrown");
    } catch (PreconditionRequiredException e) {
      assertEquals(PreconditionRequiredException.MessageKeys.MISSING_HEADER, e.getMessageKey());
    }
  }

  @Test
  public void boundActionOnEsKeyNavPreconditionsRequired() throws Exception {
    UriInfo uriInfo =
        new Parser().parseUri("ESKeyNav(1)/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null, null, getEdm());
    try {
      new PreconditionsValidator(new ETagSupport("ESKeyNav"), uriInfo, null, null).validatePreconditions(false);
      fail("Expected a PreconditionRequiredException but was not thrown");
    } catch (PreconditionRequiredException e) {
      assertEquals(PreconditionRequiredException.MessageKeys.MISSING_HEADER, e.getMessageKey());
    }
  }

  @Test
  public void simpleEntityValuePreconditionsRequired() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESMedia(1)/$value", null, null, getEdm());
    try {
      new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(true);
      fail("Expected a PreconditionRequiredException but was not thrown");
    } catch (PreconditionRequiredException e) {
      assertEquals(PreconditionRequiredException.MessageKeys.MISSING_HEADER, e.getMessageKey());
    }
  }

  @Test
  public void EntityAndToOneNavigationPreconditionsRequired() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)/NavPropertyETTwoPrimOne", null, null, getEdm());
    try {
      new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(false);
      fail("Expected a PreconditionRequiredException but was not thrown");
    } catch (PreconditionRequiredException e) {
      assertEquals(PreconditionRequiredException.MessageKeys.MISSING_HEADER, e.getMessageKey());
    }
  }

  private Edm getEdm() {
    return new EdmProviderImpl(new EdmTechProvider());
  }

  public class ETagSupport implements CustomETagSupport {

    private boolean eTag = true;
    private boolean mediaETag = true;
    private String entitySetName;

    public ETagSupport() {
    }

    public ETagSupport(String entitySetName) {
      this.entitySetName = entitySetName;
    }    

    public ETagSupport(boolean eTag, boolean mediaETag) {
      this.eTag = eTag;
      this.mediaETag = mediaETag;
    }
    

    @Override
    public boolean hasETag(String entitySetName) {
      if(this.entitySetName != null){
        assertEquals(this.entitySetName, entitySetName);
      }
      return eTag;
    }

    @Override
    public boolean hasMediaETag(String entitySetName) {
      if(this.entitySetName != null){
        assertEquals(this.entitySetName, entitySetName);
      }
      return mediaETag;
    }
  }
}
