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
package org.apache.olingo.server.tecsvc.provider;

import java.util.Arrays;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;

/**
 */
public class TermProvider {

  public static final String CORE_VOCABULARY_NAMESPACE = "Org.OData.Core.V1";
  public static FullQualifiedName TERM_DESCRIPTION =
      new FullQualifiedName(CORE_VOCABULARY_NAMESPACE, "Description");
  public static FullQualifiedName TERM_LONG_DESCRIPTION =
      new FullQualifiedName(CORE_VOCABULARY_NAMESPACE, "LongDescription");
  public static FullQualifiedName TERM_DATA = new FullQualifiedName(SchemaProvider.NAMESPACE, "Data");

  public CsdlTerm getTerm(FullQualifiedName termName) {
    if (TERM_DESCRIPTION.equals(termName)) {
      return new CsdlTerm().setName("Description").setType("Edm.String")
          .setAnnotations(Arrays.asList(new CsdlAnnotation().setTerm("Core.Description").setExpression(
              new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String,
                  "A brief description of a model element")),
              new CsdlAnnotation().setTerm("Core.IsLanguageDependent")));
    } else if (TERM_LONG_DESCRIPTION.equals(termName)) {
      return new CsdlTerm().setName("LongDescription").setType("Edm.String")
          .setAnnotations(Arrays.asList(new CsdlAnnotation().setTerm("Core.Description").setExpression(
              new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String,
                  "A lengthy description of a model element")),
              new CsdlAnnotation().setTerm("Core.IsLanguageDependent")));
    } else if (TERM_DATA.equals(termName)) {
      return new CsdlTerm().setName("Data").setType("Edm.Boolean")
          .setAnnotations(Arrays.asList(new CsdlAnnotation().setTerm("Core.Description").setExpression(
              new CsdlConstantExpression(CsdlConstantExpression.ConstantExpressionType.String,
                  "Indicates if data in the TechSvc is available for the given object"))));
    }

    return null;
  }
}
