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
package org.apache.olingo.odata4.client.api.edm.xml.v3;

import java.util.List;
import org.apache.olingo.odata4.client.api.edm.xml.CommonParameter;

public interface FunctionImport extends org.apache.olingo.odata4.client.api.edm.xml.CommonFunctionImport {

  String getReturnType();

  void setReturnType(String returnType);

  String getEntitySet();

  void setEntitySet(String entitySet);

  String getEntitySetPath();

  void setEntitySetPath(String entitySetPath);

  boolean isComposable();

  void setComposable(boolean composable);

  boolean isSideEffecting();

  void setSideEffecting(boolean sideEffecting);

  boolean isBindable();

  void setBindable(boolean bindable);

  boolean isAlwaysBindable();

  void setAlwaysBindable(boolean alwaysBindable);

  String getHttpMethod();

  void setHttpMethod(String httpMethod);

  List<? extends CommonParameter> getParameters();

}
