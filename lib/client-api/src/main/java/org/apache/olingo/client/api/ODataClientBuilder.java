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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;

/**
 * <p>
 * Builder to create an ODataClient for the API client library.
 * This builder is dependent that an according implementation for the ODataClient and or
 * EdmEnabledODataClient is available in class path.
 * </p>
 * <p>
 * This Builder should only be used in use cases were a direct access to the <code>client-core</code>
 * library is not possible.
 * If direct access is possible it is <b>highly recommended</b> to use the
 * <code>ODataClientFactory</code> provided in the <code>client-core</code> library.
 * </p>
 * <p>
 * By default the ODataClientBuilder use the default Olingo V4 client core implementations
 * (<code>org.apache.olingo.client.core.ODataClientImpl</code> and
 * <code>org.apache.olingo.client.core.EdmEnabledODataClientImpl</code>) which can be
 * overwritten via the System properties <code>ODATA_CLIENT_IMPL_SYS_PROPERTY</code>
 * and <code>ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY</code>.
 * </p>
 */
public final class ODataClientBuilder {

  private static final String ODATA_CLIENT_IMPL_CLASS = "org.apache.olingo.client.core.ODataClientImpl";
  private static final String ODATA_EDM_CLIENT_IMPL_CLASS = "org.apache.olingo.client.core.EdmEnabledODataClientImpl";
  public static final String ODATA_CLIENT_IMPL_SYS_PROPERTY = "ORG_APACHE_OLINGO_CLIENT_IMPL_FQN";
  public static final String ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY = "ORG_APACHE_OLINGO_EDM_CLIENT_IMPL_FQN";

  /**
   * Builder class
   */
  public static class ClientBuilder {
    private final String serviceRoot;
    private Edm edm;
    private String metadataETag;

    /**
     * Create the builder for an EdmEnabledODataClient.
     *
     * @param serviceRoot service root to use
     */
    public ClientBuilder(String serviceRoot) {
      this.serviceRoot = serviceRoot;
    }

    /**
     * Set the edm to use for edm enabled client
     * @param edm edm to use for edm enabled client
     * @return current client builder
     */
    public ClientBuilder edm(final Edm edm) {
      this.edm = edm;
      return this;
    }

    /**
     * Set the metadataETag to use for edm enabled client
     * @param metadataETag edm to use for edm enabled client
     * @return current client builder
     */
    public ClientBuilder metadataETag(final String metadataETag) {
      this.metadataETag = metadataETag;
      return this;
    }

    /**
     * Create an new EdmEnabledODataClient based on via system property ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY
     * class name or if not net the default ODATA_EDM_CLIENT_IMPL_CLASS set class
     * with before set serviceRoot and optional edm and optinal metadataETag.
     * @return new created ODataClient
     */
    public EdmEnabledODataClient createClient() {
      return ODataClientBuilder.createEdmEnabledClient(serviceRoot, edm, metadataETag);
    }
  }

  /** Empty private constructor for static helper class */
  private ODataClientBuilder() {}

  /**
   * Create an new ODataClient based on via system property ODATA_CLIENT_IMPL_SYS_PROPERTY
   * class name or if not net the default ODATA_CLIENT_IMPL_CLASS set class.
   * @return create ODataClient
   */
  public static ODataClient createClient() {
    String clientImplClassName = System.getProperty(ODATA_CLIENT_IMPL_SYS_PROPERTY);
    if(clientImplClassName == null) {
      clientImplClassName = ODATA_CLIENT_IMPL_CLASS;
    }
    return loadClass(ODataClient.class, clientImplClassName);
  }

  /**
   * Initiate the builder for an EdmEnabledODataClient.
   *
   * @param serviceRoot service root to use
   * @return initiated client builder
   */
  public static ClientBuilder with(String serviceRoot) {
    return new ClientBuilder(serviceRoot);
  }


  /**
   * Create an new EdmEnabledODataClient based on via system property ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY
   * class name or if not net the default ODATA_EDM_CLIENT_IMPL_CLASS set class.
   * @param serviceRoot used service root
   * @param edm used Edm
   * @param metadataETag used metadataETag
   * @return create ODataClient
   */
  private static EdmEnabledODataClient createEdmEnabledClient(
          final String serviceRoot, final Edm edm, final String metadataETag) {

    String clientImplClassName = System.getProperty(ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY);
    if(clientImplClassName == null) {
      clientImplClassName = ODATA_EDM_CLIENT_IMPL_CLASS;
    }
    final EdmEnabledODataClient instance =
        loadClass(EdmEnabledODataClient.class, clientImplClassName,
            new Class[] { String.class, Edm.class, String.class },
            new Object[] { serviceRoot, edm, metadataETag });
    instance.getConfiguration().setDefaultPubFormat(ContentType.JSON);
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
      throw wrapException(className, e);
    } catch (InstantiationException e) {
      throw wrapException(className, e);
    } catch (IllegalAccessException e) {
      throw wrapException(className, e);
    } catch (NoSuchMethodException e) {
      throw wrapException(className, e);
    } catch (InvocationTargetException e) {
      throw wrapException(className, e);
    }
  }

  private static ODataRuntimeException wrapException(String className, Exception e) {
    return new ODataRuntimeException("Requested class '" + className + "' could not be loaded.", e);
  }
}
