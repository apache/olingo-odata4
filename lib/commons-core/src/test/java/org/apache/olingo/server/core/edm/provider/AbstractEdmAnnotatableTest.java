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
package org.apache.olingo.server.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotatable;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.core.edm.AbstractEdmAnnotatable;
import org.junit.Before;
import org.junit.Test;

public class AbstractEdmAnnotatableTest {

  Edm edm;
  private EdmTerm term;
  
  @Before
  public void setupEdm(){
    edm = mock(Edm.class);
    term = mock(EdmTerm.class);
    FullQualifiedName fullQualifiedName = new FullQualifiedName("namespace", "name");
    when(term.getFullQualifiedName()).thenReturn(fullQualifiedName);
    when(edm.getTerm(fullQualifiedName)).thenReturn(term);
  }
  
  @Test
  public void noAnnotations() {
    EdmAnnotatable anno = new EdmAnnotatableTester(null, new CsdlEntityContainer());

    assertNotNull(anno.getAnnotations());
    assertEquals(0, anno.getAnnotations().size());

    assertNull(anno.getAnnotation(null, null));
    

    assertNull(anno.getAnnotation(term, null));
    assertNull(anno.getAnnotation(term, "qualifier"));
    assertNull(anno.getAnnotation(null, "qualifier"));
  }

  @Test
  public void singleAnnotation() {
    CsdlEntityContainer annotatable = new CsdlEntityContainer();
    CsdlAnnotation annotation = new CsdlAnnotation();
    annotation.setTerm("namespace.name");
    List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();
    annotations.add(annotation);
    annotatable.setAnnotations(annotations);
    EdmAnnotatable anno = new EdmAnnotatableTester(edm, annotatable);

    assertNotNull(anno.getAnnotations());
    assertEquals(1, anno.getAnnotations().size());

    assertNotNull(anno.getAnnotation(term, null));
    assertNull(anno.getAnnotation(term, "qualifier"));
  }
  
  private class EdmAnnotatableTester extends AbstractEdmAnnotatable {
    public EdmAnnotatableTester(final Edm edm, final CsdlAnnotatable annotatable) {
      super(edm, annotatable);
    }
  }

}
