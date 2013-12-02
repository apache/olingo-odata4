/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;










import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriPathInfoKind;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImplPath;
import org.apache.olingo.odata4.producer.core.uri.UriKeyPredicateList;
import org.apache.olingo.odata4.producer.core.uri.UriParserException;
import org.apache.olingo.odata4.producer.core.uri.UriParserImpl;
import org.apache.olingo.odata4.producer.core.uri.UriPathInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriPathInfoEntitySetImpl;


public class UriResourcePathValidator {
  private Edm edm;
  private UriInfoImpl uriInfo = null;
  private UriPathInfoImpl uriPathInfo = null;

  public UriResourcePathValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }


  public UriResourcePathValidator run(String uri) {
    try {
      uriInfo = new UriParserImpl(edm).ParseUri(uri);
    } catch (UriParserException e) {
      fail("Exception occured");
    }

    uriPathInfo = null;
    if (uriInfo instanceof UriInfoImplPath) {
      last();
    }
    return this;
  }

  public UriResourcePathValidator isUriPathInfoKind(UriPathInfoKind infoType) {

    assertNotNull(uriPathInfo);
    assertEquals(infoType, uriPathInfo.getKind());
    return this;
  }

  public UriResourcePathValidator last() {
    if (uriInfo instanceof UriInfoImplPath) {
      uriPathInfo = ((UriInfoImplPath) uriInfo).getLastUriPathInfo();
    } else {
      fail("UriInfo not instanceof UriInfoImplPath");
    }

    return this;
  }


  public UriResourcePathValidator at(int index) {
    if (uriInfo instanceof UriInfoImplPath) {
      try {
        uriPathInfo = ((UriInfoImplPath) uriInfo).getUriPathInfo(index);
      } catch (IndexOutOfBoundsException ex) {
        uriPathInfo = null;
      }
    } else {
      fail("UriInfo not instanceof UriInfoImplPath");

    }
    return this;
  }

  public UriResourcePathValidator first() {
    if (uriInfo instanceof UriInfoImplPath) {
      try {
        uriPathInfo = ((UriInfoImplPath) uriInfo).getUriPathInfo(0);
      } catch (IndexOutOfBoundsException ex) {
        uriPathInfo = null;
      }
    } else {
      fail("UriInfo not instanceof UriInfoImplPath");
    }
    return this;

  }


  /*
   * blic void isKind(UriInfoKind batch) {
   * // TODO assertEquals(batch, uriInfo.getKind());
   * }
   */
  public UriResourcePathValidator isKeyPredicate(int index, String name, String value) {
    if (uriPathInfo instanceof UriPathInfoEntitySetImpl) {
      UriPathInfoEntitySetImpl info = (UriPathInfoEntitySetImpl) uriPathInfo;
      UriKeyPredicateList keyPredicates = info.getKeyPredicates();
      assertEquals(name, keyPredicates.getName(index));
      assertEquals(value, keyPredicates.getValue(index));
    }
    return this;

  }

  public UriResourcePathValidator isType(FullQualifiedName type) {
    EdmType actualType = uriPathInfo.getType();
    if (actualType == null ) {
      fail("type information not set");
    }
    assertEquals(type, new FullQualifiedName(actualType.getNamespace(), actualType.getName()));
    return this;
  }
  
  public UriResourcePathValidator isCollection(boolean isCollection) {
    EdmType actualType = uriPathInfo.getType();
    if (actualType == null ) {
      fail("type information not set");
    }
    assertEquals(isCollection, uriPathInfo.isCollection());
    return this;
  }
  
  public UriResourcePathValidator isInitialType(FullQualifiedName type) {
    EdmType actualType = uriPathInfo.getInitialType();
    if (actualType == null ) {
      fail("type information not set");
    }
    assertEquals(type, new FullQualifiedName(actualType.getNamespace(), actualType.getName()));
    return this;
  }


  public UriResourcePathValidator isProperties(List<String> asList) {
    assertNotNull(uriPathInfo);

    int index = 0;
    while (index < asList.size()) {
      String propertyName = uriPathInfo.getProperty(index).getName();
      assertEquals(asList.get(index), propertyName);
      index++;
    }

    return this;


  }

  public void isKind(UriInfoKind kind) {
    
    assertEquals(kind, uriInfo.getKind());
  }


  public UriResourcePathValidator isProperty(int index, String name, FullQualifiedName type) {
    if ( index >= uriPathInfo.getPropertyCount()) {
      fail("not enougth properties in pathinfo found");
    }
    EdmElement property = uriPathInfo.getProperty(index);
    
    assertEquals(name, property.getName());
    assertEquals(type, new FullQualifiedName(property.getType().getNamespace(), property.getType().getName()));
    return this;
    
  }


  
}
