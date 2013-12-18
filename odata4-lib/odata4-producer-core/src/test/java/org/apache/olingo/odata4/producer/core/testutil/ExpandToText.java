/*******************************************************************************
 * 
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

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExceptionVisitExpand;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandVisitor;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandOptionImpl;


public class ExpandToText implements ExpandVisitor<String> {

  public static String Serialize(ExpandOptionImpl expand) throws ExceptionVisitExpand {
    return expand.accept(new ExpandToText());
  }

  @Override
  public String visitExpandSegment(EdmElement property, EdmType initialType, EdmType finalType) {

    return "<" + property.getName() + "(" + finalType.getNamespace() + "/" + finalType.getName() + ">";
  }

  @Override
  public String visitExpandItem(List<String> expandSegments, boolean isStar, boolean isRef, EdmType finalType) {
    String tmp = "";
    for (String expandItem : expandSegments) {
      if (tmp.length() != 0) {
        tmp += ",";
      }
      tmp += expandItem;
    }
    return "<(" + tmp + ")>";
  }

  @Override
  public String visitExpand(List<String> expandItems) {
    String tmp = "";

    for (String expandItem : expandItems) {
      if (tmp.length() != 0) {
        tmp += ",";
      }
      tmp += expandItem;
    }
    return "<(" + tmp + ")>";
  }

}
