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
package org.apache.olingo.jpa.api;

/**
 * <p>
 * The interface provides methods for handling transactions. OData JPA processor defaults to "RESOURCE LOCAL"
 * application managed transaction.
 * </p>
 * <p> Applications can override this behavior by implementing this interface. The implementation can
 * be registered with OData JPA processor using the servelt init parameter <b>odata.jpa.transaction</b></p>
 *
 */
public interface ODataJPATransaction {

}
