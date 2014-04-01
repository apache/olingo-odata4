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
package org.apache.olingo.client.api.communication.request.batch;

import java.util.Iterator;

/**
 * Batch line iterator class.
 */
public interface ODataBatchLineIterator extends Iterator<String> {

  /**
   * Checks if batch has next line.
   *
   * @return 'TRUE' if has next line; 'FALSE' otherwise.
   */
  @Override
  boolean hasNext();

  /**
   * Gets next line.
   *
   * @return next line.
   */
  @Override
  String next();

  /**
   * Gets next line.
   *
   * @return next line.
   */
  String nextLine();

  /**
   * Gets last cached line (the current one).
   *
   * @return last cached line; null if <code>next()</code> method never called
   */
  String getCurrent();
}
