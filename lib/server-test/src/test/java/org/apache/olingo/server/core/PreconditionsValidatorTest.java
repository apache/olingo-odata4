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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.etag.CustomETagSupport;
import org.apache.olingo.server.api.etag.PreconditionException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceValue;
import org.apache.olingo.server.core.etag.PreconditionsValidator;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class PreconditionsValidatorTest {

  private static final OData odata = OData.newInstance();
  private static final Edm edm = odata.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  // -------------- POSITIVE TESTS --------------------------------------------------------------------------------

  @Test
  public void simpleEntity() throws Exception {
    assertTrue(mustValidate("ESAllPrim(1)", "ESAllPrim"));
  }

  @Test
  public void simpleEntityValue() throws Exception {
    assertTrue(mustValidate("ESMedia(1)/$value", "ESMedia"));
  }

  @Test
  public void property() throws Exception {
    assertTrue(mustValidate("ESAllPrim(1)/PropertyInt16", "ESAllPrim"));
    assertTrue(mustValidate("ESMixPrimCollComp(0)/PropertyComp", "ESMixPrimCollComp"));
    assertTrue(mustValidate("ESMixPrimCollComp(0)/PropertyComp/PropertyString", "ESMixPrimCollComp"));
  }

  @Test
  public void propertyValue() throws Exception {
    assertTrue(mustValidate("ESAllPrim(1)/PropertyInt16/$value", "ESAllPrim"));
    assertTrue(mustValidate("ESMixPrimCollComp(0)/PropertyComp/PropertyString/$value", "ESMixPrimCollComp"));
  }

  @Test
  public void EntityAndToOneNavigation() throws Exception {
    assertTrue(mustValidate("ESAllPrim(1)/NavPropertyETTwoPrimOne", "ESTwoPrim"));
  }

  @Test
  public void EntityAndToManyNavigationWithKey() throws Exception {
    assertTrue(mustValidate("ESAllPrim(1)/NavPropertyETTwoPrimMany(1)", "ESTwoPrim"));
  }

  @Test
  public void EntityAndToOneNavigationValue() throws Exception {
    assertTrue(mustValidate("ESKeyNav(1)/NavPropertyETMediaOne/$value", "ESMedia"));
  }

  @Test
  public void navigationOnProperty() throws Exception {
    assertTrue(mustValidate("ESAllPrim(1)/NavPropertyETTwoPrimOne/PropertyInt16", "ESTwoPrim"));
  }

  @Test
  public void navigationOnFunction() throws Exception {
    assertTrue(mustValidate("FICRTESTwoKeyNav()/NavPropertySINav", "SINav"));
  }

  @Test
  public void boundActionOnEsKeyNav() throws Exception {
    assertTrue(mustValidate("ESKeyNav(1)/Namespace1_Alias.BA_RTETTwoKeyNav", "ESKeyNav"));
  }

  @Test
  public void boundActionOnEsKeyNavWithNavigation() throws Exception {
    assertTrue(
        mustValidate("ESKeyNav(1)/NavPropertyETKeyNavOne/Namespace1_Alias.BA_RTETTwoKeyNav", "ESKeyNav"));
  }

  @Test
  public void singleton() throws Exception {
    assertTrue(mustValidate("SI", "SI"));
  }

  @Test
  public void singletonWithNavigation() throws Exception {
    assertTrue(mustValidate("SINav/NavPropertyETKeyNavOne", "ESKeyNav"));
  }

  @Test
  public void singletonWithNavigationValue() throws Exception {
    assertTrue(mustValidate("SINav/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value", "ESMedia"));
  }

  @Test
  public void singletonWithAction() throws Exception {
    assertTrue(mustValidate("SINav/Namespace1_Alias.BA_RTETTwoKeyNav", "SINav"));
  }

  @Test
  public void singletonWithActionAndNavigation() throws Exception {
    assertTrue(mustValidate("SINav/NavPropertyETKeyNavOne/Namespace1_Alias.BA_RTETTwoKeyNav", "ESKeyNav"));
  }

  @Test
  public void simpleEntityValueValidationNotActiveForMedia() throws Exception {
    final UriInfo uriInfo = new Parser(edm, odata).parseUri("ESMedia(1)/$value", null, null, null);

    CustomETagSupport support = mock(CustomETagSupport.class);
    when(support.hasETag(any(EdmBindingTarget.class))).thenReturn(true);
    when(support.hasMediaETag(any(EdmBindingTarget.class))).thenReturn(false);

    assertFalse(new PreconditionsValidator(uriInfo).mustValidatePreconditions(support, true));
  }

  // -------------- IGNORE VALIDATION TESTS -----------------------------------------------------------------------

  @Test
  public void entitySetMustBeIgnored() throws Exception {
    assertFalse(mustValidate("ESAllPrim", "ESAllPrim"));
  }

  @Test
  public void navigationToManyMustBeIgnored() throws Exception {
    assertFalse(mustValidate("ESAllPrim(1)/NavPropertyETTwoPrimMany", "ESTwoPrim"));
  }

  @Test
  public void navigationOnFunctionWithoutEntitySetMustBeIgnored() throws Exception {
    assertFalse(mustValidate("FICRTETTwoKeyNavParam(ParameterInt16=1)/NavPropertyETKeyNavOne", null));
  }

  @Test
  public void navigationToManyToActionMustBeIgnored() throws Exception {
    assertFalse(mustValidate("ESTwoPrim(1)/NavPropertyETAllPrimMany/Namespace1_Alias.BAESAllPrimRTETAllPrim", null));
  }

  @Test
  public void navigationWithoutBindingMustBeIgnored() throws Exception {
    assertFalse(mustValidate("ESTwoBaseTwoKeyNav(PropertyInt16=1,PropertyString='test')"
        + "/NavPropertyETBaseTwoKeyNavMany(PropertyInt16=1,PropertyString='test')",
        null));
  }

  @Test
  public void referencesMustBeIgnored() throws Exception {
    assertFalse(mustValidate("ESAllPrim(1)/NavPropertyETTwoPrimOne/$ref", "ESTwoPrim"));
    assertFalse(mustValidate("ESAllPrim(1)/NavPropertyETTwoPrimMany(1)/$ref", "ESTwoPrim"));
    assertFalse(mustValidate("SINav/NavPropertyETKeyNavOne/$ref", "ESKeyNav"));
  }

  @Test
  public void nonResourceMustBeIgnored() throws Exception {
    assertFalse(mustValidate("$all", null));
  }

  private boolean mustValidate(final String uri, final String entitySetName)
      throws UriParserException, UriValidationException, PreconditionException {
    final UriInfo uriInfo = new Parser(edm, odata).parseUri(uri, null, null, null);
    final List<UriResource> parts = uriInfo.getUriResourceParts();
    final boolean isMedia = parts.size() >= 2
        && parts.get(parts.size() - 1) instanceof UriResourceValue
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

    return new PreconditionsValidator(uriInfo).mustValidatePreconditions(support, isMedia);
  }
}
