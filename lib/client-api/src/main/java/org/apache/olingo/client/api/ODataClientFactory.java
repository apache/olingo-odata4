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
package org.apache.olingo.client.api;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ODataFormat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Helper to access an implementation for the API client library.
 */
public final class ODataClientFactory {

  private static final String ODATA_CLIENT_IMPL_CLASS = "org.apache.olingo.client.core.ODataClientImpl";
  private static final String ODATA_EDM_CLIENT_IMPL_CLASS = "org.apache.olingo.client.core.EdmEnabledODataClientImpl";
  public static final String ODATA_CLIENT_IMPL_SYS_PROPERTY = "ORG_APACHE_OLINGO_CLIENT_IMPL_FQN";
  public static final String ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY = "ORG_APACHE_OLINGO_EDM_CLIENT_IMPL_FQN";

  /**
   * Create an new ODataClient based on via system property ODATA_CLIENT_IMPL_SYS_PROPERTY
   * class name or if not net the default ODATA_CLIENT_IMPL_CLASS set class.
   * @return create ODataClient
   */
  public static ODataClient getClient() {
    String clientImplClassName = System.getProperty(ODATA_CLIENT_IMPL_SYS_PROPERTY);
    if(clientImplClassName == null) {
      clientImplClassName = ODATA_CLIENT_IMPL_CLASS;
    }
    return loadClass(ODataClient.class, clientImplClassName);
  }

  /**
   * Create an new EdmEnabledODataClient based on via system property ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY
   * class name or if not net the default ODATA_EDM_CLIENT_IMPL_CLASS set class.
   * @param serviceRoot used service root
   * @return create ODataClient
   */
  public static EdmEnabledODataClient getEdmEnabledClient(final String serviceRoot) {
    return getEdmEnabledClient(serviceRoot, null, null);
  }

  /**
   * Create an new EdmEnabledODataClient based on via system property ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY
   * class name or if not net the default ODATA_EDM_CLIENT_IMPL_CLASS set class.
   * @param serviceRoot used service root
   * @param edm used Edm
   * @param metadataETag used metadataETag
   * @return create ODataClient
   */
  public static EdmEnabledODataClient getEdmEnabledClient(
      final String serviceRoot, final Edm edm, final String metadataETag) {

    String clientImplClassName = System.getProperty(ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY);
    if(clientImplClassName == null) {
      clientImplClassName = ODATA_EDM_CLIENT_IMPL_CLASS;
    }
    final EdmEnabledODataClient instance =
        loadClass(EdmEnabledODataClient.class, clientImplClassName,
            new Class[] { String.class, Edm.class, String.class },
            new Object[] { serviceRoot, edm, metadataETag });
    instance.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return instance;
  }

  private static <T> T loadClass(Class<T> typeOfClass, String className) {
    return loadClass(typeOfClass, className, null, null);
  }

  private static <T> T loadClass(Class<T> typeOfClass, String className,
                                 Class<?>[] ctorParameterClasses,
                                 Object[] ctorParameters) {
    try {
      Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
      if (ctorParameters == null || ctorParameterClasses == null) {
        return typeOfClass.cast(clazz.newInstance());
      }
      Constructor<?> ctor = clazz.getConstructor(ctorParameterClasses);
      return typeOfClass.cast(ctor.newInstance(ctorParameters));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Requested class '" + className + "' could not be loaded.", e);
    } catch (InstantiationException e) {
      throw new RuntimeException("Requested class '" + className + "' could not be loaded.", e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Requested class '" + className + "' could not be loaded.", e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Requested class '" + className + "' could not be loaded.", e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Requested class '" + className + "' could not be loaded.", e);
    }
  }
}
