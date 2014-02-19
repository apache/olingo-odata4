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
package org.apache.olingo.odata4.client.api;


//TODO: Exceptionhandling
public abstract class ODataConsumer {

  private static final String IMPLEMENTATION = "org.apache.olingo.odata4.client.core.ODataConsumerImpl";

  public static ODataConsumer create() {
    ODataConsumer instance;

    try {
      final Class<?> clazz = Class.forName(ODataConsumer.IMPLEMENTATION);

      /*
       * We explicitly do not use the singleton pattern to keep the server state free
       * and avoid class loading issues also during hot deployment.
       */
      final Object object = clazz.newInstance();
      instance = (ODataConsumer) object;

    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    return instance;
  }
}
