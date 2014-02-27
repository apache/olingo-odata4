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
package org.apache.olingo.odata4.client.api.edm.xml;

import org.apache.olingo.odata4.client.api.edm.xml.v3.Annotations;
import java.util.List;

public interface Schema {

  String getNamespace();

  void setNamespace(String namespace);

  String getAlias();

  void setAlias(String alias);

  List<? extends EntityType> getEntityTypes();

  List<? extends EnumType> getEnumTypes();

  EnumType getEnumType(String name);

  List<? extends AbstractAnnotations> getAnnotationsList();

  AbstractAnnotations getAnnotationsList(String target);

  List<? extends ComplexType> getComplexTypes();

  List<? extends EntityContainer> getEntityContainers();

  /**
   * Gets default entity container.
   *
   * @return default entity container.
   */
  EntityContainer getDefaultEntityContainer();

  /**
   * Gets entity container with the given name.
   *
   * @param name name.
   * @return entity container.
   */
  EntityContainer getEntityContainer(String name);

  /**
   * Gets entity type with the given name.
   *
   * @param name name.
   * @return entity type.
   */
  EntityType getEntityType(String name);

  /**
   * Gets complex type with the given name.
   *
   * @param name name.
   * @return complex type.
   */
  ComplexType getComplexType(String name);

}
