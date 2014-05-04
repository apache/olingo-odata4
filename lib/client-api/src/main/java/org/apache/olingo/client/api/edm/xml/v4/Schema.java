/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.api.edm.xml.v4;

import java.util.List;
import java.util.Map;

public interface Schema extends org.apache.olingo.client.api.edm.xml.Schema, Annotatable {

  @Override
  List<EntityContainer> getEntityContainers();

  EntityContainer getEntityContainer();

  @Override
  ComplexType getComplexType(String name);

  @Override
  List<ComplexType> getComplexTypes();

  @Override
  EntityType getEntityType(String name);

  @Override
  List<EntityType> getEntityTypes();

  List<Action> getActions();

  List<Action> getActions(String name);

  Annotation getAnnotation(String term);

  Map<String, Annotatable> getAnnotatables();

  List<Function> getFunctions();

  List<Function> getFunctions(String name);

  Term getTerm(String name);

  List<Term> getTerms();

  TypeDefinition getTypeDefinition(String name);

  List<TypeDefinition> getTypeDefinitions();

  @Override
  List<Annotations> getAnnotationGroups();

  @Override
  Annotations getAnnotationGroup(String target);

}
