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
/**
 * Olingo Processors
 * <p>Processors are used to handle OData requests and send back the OData reponse.
 * Before a specific processor is called the Olingo library will parse the URI and validate it.
 * Afterwards the Processor which matches the return type is called.
 * Example: If a primitive property is requested by the URI we will call the
 * PrimitiveProcessor.readPrimitive method.</p>
 * <p>Processors can be registered at the {@link org.apache.olingo.server.api.ODataHttpHandler} object.
 * Per default the {@link org.apache.olingo.server.api.processor.DefaultProcessor} is registered
 * to perform basic functionality like delivering the metadata and service documents, respectively,
 * as well as rendering an OData error.
 * In case an application would like to perform custom tasks for these cases a new
 * {@link org.apache.olingo.server.api.processor.ServiceDocumentProcessor} can be registered
 * in order to overwrite the default behavior.
 */
package org.apache.olingo.server.api.processor;

