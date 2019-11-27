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
package org.apache.olingo.client.core.communication.request.batch;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.LineIterator;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchLineIterator;
import org.junit.Test;

public class ODataBatchControllerTest {
  
  @Test
  public void testController(){
    final InputStream input = getClass().getResourceAsStream("batchResponse.batch");
    Reader reader = new InputStreamReader(input);
    ODataBatchLineIterator iterator = new ODataBatchLineIteratorImpl(new LineIterator(reader ));
    ODataBatchController controller = new ODataBatchController(iterator , "changeset_12ks93js84d");
    assertNotNull(controller.getBatchLineIterator());
    assertNotNull(controller.getBoundary());
    controller.setValidBatch(true);
    assertTrue(controller.isValidBatch());
    assertTrue(iterator.hasNext());
    assertNotNull(iterator.next());
    assertNotNull(iterator.nextLine());
    assertNotNull(iterator.getCurrent());
  }
  
  @Test(expected = UnsupportedOperationException.class)
  public void testControllerNeg(){
    final InputStream input = getClass().getResourceAsStream("batchResponse.batch");
    Reader reader = new InputStreamReader(input);
    ODataBatchLineIterator iterator = new ODataBatchLineIteratorImpl(new LineIterator(reader ));
    iterator.remove();
  }
}
