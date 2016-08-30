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
package org.apache.olingo.fit.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.TargetType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.junit.Test;

public class MetadataTestITCase extends AbstractTestITCase {

  @Test
  public void vocabularies() {
    final Edm edm = client.getRetrieveRequestFactory().
        getMetadataRequest(testVocabulariesServiceRootURL).execute().getBody();
    assertNotNull(edm);

    final EdmTerm isLanguageDependent = edm.getTerm(new FullQualifiedName("Core", "IsLanguageDependent"));
    assertNotNull(isLanguageDependent);
    assertTrue(isLanguageDependent.getAppliesTo().contains(TargetType.Property));
    assertTrue(isLanguageDependent.getAppliesTo().contains(TargetType.Term));
    assertEquals(edm.getTypeDefinition(new FullQualifiedName("Core", "Tag")), isLanguageDependent.getType());
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean),
        ((EdmTypeDefinition) isLanguageDependent.getType()).getUnderlyingType());

    final EdmTerm permissions = edm.getTerm(new FullQualifiedName("Core", "Permissions"));
    assertNotNull(permissions);
    assertTrue(permissions.getType() instanceof EdmEnumType);

    // 2. measures
    final EdmSchema measures = edm.getSchema("UoM");
    assertNotNull(measures);

    final EdmTerm scale = edm.getTerm(new FullQualifiedName("UoM", "Scale"));
    assertNotNull(scale);

    final EdmAnnotation requiresTypeInScale =
        scale.getAnnotation(edm.getTerm(new FullQualifiedName("Core", "RequiresType")), null);
    assertNotNull(requiresTypeInScale);
    assertEquals("Edm.Decimal", requiresTypeInScale.getExpression().asConstant().getValueAsString());

    // 3. capabilities
    final EdmTerm deleteRestrictions = edm.getTerm(new FullQualifiedName("Capabilities", "DeleteRestrictions"));
    assertNotNull(deleteRestrictions);
    assertEquals(deleteRestrictions.getType().getFullQualifiedName(),
        edm.getComplexType(new FullQualifiedName("Capabilities", "DeleteRestrictionsType")).getFullQualifiedName());
  }
}
