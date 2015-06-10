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
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.server.api.etag.CustomETagSupport;
import org.apache.olingo.server.api.etag.PreconditionException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.etag.PreconditionsValidator;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.validator.UriValidator;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Ignore;
import org.junit.Test;

public class PreconditionsValidatorTest {

  // -------------- POSITIVE TESTS --------------------------------------------------------------------------------

  @Test
  public void simpleEntity() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void simpleEntityValue() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESMedia(1)/$value", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, "*", "*").validatePreconditions(true);
  }

  @Test
  public void EntityAndToOneNavigation() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)/NavPropertyETTwoPrimOne", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("ESTwoPrim"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void EntityAndToManyNavigationWithKey() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)/NavPropertyETTwoPrimMany(1)", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("ESTwoPrim"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void EntityAndToOneNavigationValue() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESKeyNav(1)/NavPropertyETMediaOne/$value", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("ESMedia"), uriInfo, "*", "*").validatePreconditions(true);
  }

  @Test
  public void boundActionOnEsKeyNav() throws Exception {
    UriInfo uriInfo =
        new Parser().parseUri("ESKeyNav(1)/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("ESKeyNav"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void boundActionOnEsKeyNavWithNavigation() throws Exception {
    UriInfo uriInfo =
        new Parser().parseUri("ESKeyNav(1)/NavPropertyETKeyNavOne/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null,
            null, getEdm());
    new PreconditionsValidator(new ETagSupport("ESKeyNav"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void singleton() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("SI", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("SI"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void singletonWithNavigation() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("SINav/NavPropertyETKeyNavOne", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("ESKeyNav"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void singletonWithNavigationValue() throws Exception {
    UriInfo uriInfo =
        new Parser().parseUri("SINav/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("ESMedia"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void singletonWithAction() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("SINav/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport("SINav"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void singletonWithActionAndNavigation() throws Exception {
    UriInfo uriInfo =
        new Parser().parseUri("SINav/NavPropertyETKeyNavOne/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null, null,
            getEdm());
    new PreconditionsValidator(new ETagSupport("ESKeyNav"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test
  public void simpleEntityValueValidationNotActiveForMedia() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESMedia(1)/$value", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(true, false), uriInfo, null, null).validatePreconditions(true);
  }

  // -------------- IGNORE VALIDATION TESTS -----------------------------------------------------------------------

  @Test
  public void entitySetMustNotLeadToException() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(false);
  }

  @Test
  public void propertyMustNotLeadToException() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)/PropertyInt16", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(false);
  }

  @Test
  public void propertyValueMustNotLeadToException() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)/PropertyInt16/$value", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(true);
  }

  @Test
  public void navigationToManyMustNotLeadToException() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)/NavPropertyETTwoPrimMany", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(false);
  }

  @Test
  public void navigationOnPropertyMustNotLeadToException() throws Exception {
    UriInfo uriInfo = new Parser().parseUri("ESAllPrim(1)/NavPropertyETTwoPrimOne/PropertyInt16", null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(false);
  }

  @Test
  public void navigationToManyOnActionMustNotLeadToException() throws Exception {
    UriInfo uriInfo =
        new Parser().parseUri("ESTwoPrim(1)/NavPropertyETAllPrimMany/Namespace1_Alias.BAESAllPrimRTETAllPrim", null,
            null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(false);
  }

  @Test
  public void navigationWithoutBindingMustNotLeadToAnException() throws Exception {
    UriInfo uriInfo =
        new Parser()
            .parseUri(
                "ESTwoBaseTwoKeyNav(PropertyInt16=1,PropertyString='test')"
                    + "/NavPropertyETBaseTwoKeyNavMany(PropertyInt16=1,PropertyString='test')",
                null, null, getEdm());
    new PreconditionsValidator(new ETagSupport(), uriInfo, null, null).validatePreconditions(false);
  }

  // -------------- NEGATIVE TESTS --------------------------------------------------------------------------------

  @Test
  public void positiveTestsMustLeadToAnExceptionIfNoHeaderIsPresent() throws Exception {
    runException("ESAllPrim(1)", null);
    runException("ESMedia(1)/$value", null);
    runException("ESAllPrim(1)/NavPropertyETTwoPrimOne", null);
    runException("ESAllPrim(1)/NavPropertyETTwoPrimMany(1)", null);
    runException("ESKeyNav(1)/NavPropertyETMediaOne/$value", null);
    runException("ESKeyNav(1)/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null);
    runException("ESKeyNav(1)/NavPropertyETKeyNavOne/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null);

    runException("SI", null);
    runException("SINav/NavPropertyETKeyNavOne", null);
    runException("SINav/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value", null);
    runException("SINav/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null);
    runException("SINav/NavPropertyETKeyNavOne/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", null);
  }

  @Ignore
  @Test
  public void resourceSegmentAfterActionMustLeadToUriParserException() throws Exception {
    // TODO: Check with URI Parser
    UriInfo uriInfo =
        new Parser().parseUri("ESKeyNav(1)/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav/PropertyInt16", null, null,
            getEdm());
    new UriValidator().validate(uriInfo, HttpMethod.GET);
    new PreconditionsValidator(new ETagSupport("ESKeyNav"), uriInfo, "*", "*").validatePreconditions(false);
  }

  @Test(expected = UriParserSemanticException.class)
  public void valueMustBeLastSegment() throws Exception {
    new Parser().parseUri("ESMedia(1)/$value/PropertyInt16", null, null, getEdm());
  }

  private void runException(String uri, String expectedEntitySet) throws UriParserException {
    UriInfo uriInfo = new Parser().parseUri(uri, null, null, getEdm());
    try {
      CustomETagSupport etagSupport =
          expectedEntitySet == null ? new ETagSupport() : new ETagSupport(expectedEntitySet);
      boolean isMedia = uri.endsWith("$value");
      new PreconditionsValidator(etagSupport, uriInfo, null, null).validatePreconditions(isMedia);
      fail("Expected a PreconditionRequiredException but was not thrown");
    } catch (PreconditionException e) {
      assertEquals(PreconditionException.MessageKeys.MISSING_HEADER, e.getMessageKey());
    }
  }

  private Edm getEdm() {
    return new EdmProviderImpl(new EdmTechProvider());
  }

  public class ETagSupport implements CustomETagSupport {

    private boolean eTag = true;
    private boolean mediaETag = true;
    private String entitySetName;

    public ETagSupport() {}

    public ETagSupport(String entitySetName) {
      this.entitySetName = entitySetName;
    }

    public ETagSupport(boolean eTag, boolean mediaETag) {
      this.eTag = eTag;
      this.mediaETag = mediaETag;
    }

    @Override
    public boolean hasETag(EdmBindingTarget entitySetOrSingeton) {
      if (this.entitySetName != null) {
        assertEquals(this.entitySetName, entitySetOrSingeton.getName());
      }
      return eTag;
    }

    @Override
    public boolean hasMediaETag(EdmBindingTarget entitySetOrSingelton) {
      if (this.entitySetName != null) {
        assertEquals(this.entitySetName, entitySetOrSingelton.getName());
      }
      return mediaETag;
    }
  }
}
