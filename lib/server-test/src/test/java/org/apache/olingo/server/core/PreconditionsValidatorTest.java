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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.server.api.etag.CustomETagSupport;
import org.apache.olingo.server.api.etag.PreconditionException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceValue;
import org.apache.olingo.server.core.etag.PreconditionsValidator;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class PreconditionsValidatorTest {

  private static final Edm edm = new EdmProviderImpl(new EdmTechProvider());

  // -------------- POSITIVE TESTS --------------------------------------------------------------------------------

  @Test
  public void simpleEntity() throws Exception {
    validate("ESAllPrim(1)", null, "*", "*");
  }

  @Test
  public void simpleEntityValue() throws Exception {
    validate("ESMedia(1)/$value", null, "*", "*");
  }

  @Test
  public void EntityAndToOneNavigation() throws Exception {
    validate("ESAllPrim(1)/NavPropertyETTwoPrimOne", "ESTwoPrim", "*", "*");
  }

  @Test
  public void EntityAndToManyNavigationWithKey() throws Exception {
    validate("ESAllPrim(1)/NavPropertyETTwoPrimMany(1)", "ESTwoPrim", "*", "*");
  }

  @Test
  public void EntityAndToOneNavigationValue() throws Exception {
    validate("ESKeyNav(1)/NavPropertyETMediaOne/$value", "ESMedia", "*", "*");
  }

  @Test
  public void boundActionOnEsKeyNav() throws Exception {
    validate("ESKeyNav(1)/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", "ESKeyNav", "*", "*");
  }

  @Test
  public void boundActionOnEsKeyNavWithNavigation() throws Exception {
    validate("ESKeyNav(1)/NavPropertyETKeyNavOne/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav",
        "ESKeyNav", "*", "*");
  }

  @Test
  public void singleton() throws Exception {
    validate("SI", "SI", "*", "*");
  }

  @Test
  public void singletonWithNavigation() throws Exception {
    validate("SINav/NavPropertyETKeyNavOne", "ESKeyNav", "*", "*");
  }

  @Test
  public void singletonWithNavigationValue() throws Exception {
    validate("SINav/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value", "ESMedia", "*", "*");
  }

  @Test
  public void singletonWithAction() throws Exception {
    validate("SINav/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", "SINav", "*", "*");
  }

  @Test
  public void singletonWithActionAndNavigation() throws Exception {
    validate("SINav/NavPropertyETKeyNavOne/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav", "ESKeyNav", "*", "*");
  }

  @Test
  public void simpleEntityValueValidationNotActiveForMedia() throws Exception {
    CustomETagSupport support = mock(CustomETagSupport.class);
    when(support.hasETag(any(EdmBindingTarget.class))).thenReturn(true);
    when(support.hasMediaETag(any(EdmBindingTarget.class))).thenReturn(false);

    final UriInfo uriInfo = new Parser().parseUri("ESMedia(1)/$value", null, null, edm);
    new PreconditionsValidator(support, uriInfo, null, null).validatePreconditions(true);
  }

  // -------------- IGNORE VALIDATION TESTS -----------------------------------------------------------------------

  @Test
  public void entitySetMustNotLeadToException() throws Exception {
    validate("ESAllPrim", null, null, null);
  }

  @Test
  public void propertyMustNotLeadToException() throws Exception {
    validate("ESAllPrim(1)/PropertyInt16", null, null, null);
  }

  @Test
  public void propertyValueMustNotLeadToException() throws Exception {
    validate("ESAllPrim(1)/PropertyInt16/$value", null, null, null);
  }

  @Test
  public void navigationToManyMustNotLeadToException() throws Exception {
    validate("ESAllPrim(1)/NavPropertyETTwoPrimMany", null, null, null);
  }

  @Test
  public void navigationOnPropertyMustNotLeadToException() throws Exception {
    validate("ESAllPrim(1)/NavPropertyETTwoPrimOne/PropertyInt16", null, null, null);
  }

  @Test
  public void navigationToManyOnActionMustNotLeadToException() throws Exception {
    validate("ESTwoPrim(1)/NavPropertyETAllPrimMany/Namespace1_Alias.BAESAllPrimRTETAllPrim", null, null, null);
  }

  @Test
  public void navigationWithoutBindingMustNotLeadToException() throws Exception {
    validate("ESTwoBaseTwoKeyNav(PropertyInt16=1,PropertyString='test')"
        + "/NavPropertyETBaseTwoKeyNavMany(PropertyInt16=1,PropertyString='test')",
        null, null, null);
  }

  // -------------- NEGATIVE TESTS --------------------------------------------------------------------------------

  @Test
  public void positiveTestsMustLeadToAnExceptionIfNoHeaderIsPresent() throws Exception {
    runException("ESAllPrim(1)");
    runException("ESMedia(1)/$value");
    runException("ESAllPrim(1)/NavPropertyETTwoPrimOne");
    runException("ESAllPrim(1)/NavPropertyETTwoPrimMany(1)");
    runException("ESKeyNav(1)/NavPropertyETMediaOne/$value");
    runException("ESKeyNav(1)/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav");
    runException("ESKeyNav(1)/NavPropertyETKeyNavOne/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav");

    runException("SI");
    runException("SINav/NavPropertyETKeyNavOne");
    runException("SINav/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value");
    runException("SINav/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav");
    runException("SINav/NavPropertyETKeyNavOne/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav");
  }

  @Ignore
  @Test(expected = UriParserSemanticException.class)
  public void resourceSegmentAfterActionMustLeadToUriParserException() throws Exception {
    validate("ESKeyNav(1)/Namespace1_Alias.BAETTwoKeyNavRTETTwoKeyNav/PropertyInt16", "ESKeyNav", "*", "*");
  }

  @Test(expected = UriParserSemanticException.class)
  public void valueMustBeLastSegment() throws Exception {
    validate("ESMedia(1)/$value/PropertyInt16", null, null, null);
  }

  private void validate(final String uri, final String entitySetName, final String ifMatch, final String ifNoneMatch)
      throws UriParserException, PreconditionException {
    final UriInfo uriInfo = new Parser().parseUri(uri, null, null, edm);
    final List<UriResource> parts = uriInfo.getUriResourceParts();
    final boolean isMedia = parts.get(parts.size() - 1) instanceof UriResourceValue
        && parts.get(parts.size() - 2) instanceof UriResourceEntitySet;

    CustomETagSupport support = mock(CustomETagSupport.class);
    final Answer<Boolean> answer = new Answer<Boolean>() {
      public Boolean answer(final InvocationOnMock invocation) throws Throwable {
        if (entitySetName != null) {
          assertEquals(entitySetName, ((EdmBindingTarget) invocation.getArguments()[0]).getName());
        }
        return true;
      }};
    when(support.hasETag(any(EdmBindingTarget.class))).thenAnswer(answer);
    when(support.hasMediaETag(any(EdmBindingTarget.class))).thenAnswer(answer);

    new PreconditionsValidator(support, uriInfo, ifMatch, ifNoneMatch).validatePreconditions(isMedia);
  }

  private void runException(final String uri) throws UriParserException {
    try {
      validate(uri, null, null, null);
      fail("Expected a PreconditionRequiredException but was not thrown");
    } catch (final PreconditionException e) {
      assertEquals(PreconditionException.MessageKeys.MISSING_HEADER, e.getMessageKey());
    }
  }
}
