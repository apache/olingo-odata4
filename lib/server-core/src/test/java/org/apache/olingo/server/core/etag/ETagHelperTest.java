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
package org.apache.olingo.server.core.etag;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.etag.ETagHelper;
import org.apache.olingo.server.api.etag.PreconditionException;
import org.junit.Test;

public class ETagHelperTest {

  private static final ETagHelper eTagHelper = OData.newInstance().createETagHelper();

  @Test
  public void readPrecondition() throws Exception {
    assertFalse(eTagHelper.checkReadPreconditions(null, null, null));
    assertFalse(eTagHelper.checkReadPreconditions("\"ETag\"", null, null));
    assertFalse(eTagHelper.checkReadPreconditions(null, Collections.singleton("\"ETag\""), null));
    assertFalse(eTagHelper.checkReadPreconditions(null, null, Collections.singleton("\"ETag\"")));
    assertFalse(eTagHelper.checkReadPreconditions("\"ETag\"", Collections.singleton("\"ETag\""), null));
    assertFalse(eTagHelper.checkReadPreconditions("\"ETag\"", Collections.singleton("*"), null));
    assertTrue(eTagHelper.checkReadPreconditions("\"ETag\"", null, Collections.singleton("\"ETag\"")));
    assertTrue(eTagHelper.checkReadPreconditions("\"ETag\"", null, Collections.singleton("*")));
    assertFalse(eTagHelper.checkReadPreconditions("\"ETag\"", null, Collections.singleton("\"ETag2\"")));
  }

  @Test(expected = PreconditionException.class)
  public void readPreconditionFail() throws Exception {
    eTagHelper.checkReadPreconditions("\"ETag\"", Collections.singleton("\"ETag2\""), null);
  }

  @Test
  public void changePrecondition() throws Exception {
    eTagHelper.checkChangePreconditions(null, null, null);
    eTagHelper.checkChangePreconditions("\"ETag\"", null, null);
    eTagHelper.checkChangePreconditions(null, Collections.singleton("\"ETag\""), null);
    eTagHelper.checkChangePreconditions(null, Collections.singleton("*"), null);
    eTagHelper.checkChangePreconditions(null, null, Collections.singleton("*"));
    eTagHelper.checkChangePreconditions("\"ETag\"", Collections.singleton("\"ETag\""), null);
    eTagHelper.checkChangePreconditions("\"ETag\"", Collections.singleton("*"), null);
    eTagHelper.checkChangePreconditions("\"ETag\"", null, Collections.singleton("\"ETag2\""));
  }

  @Test(expected = PreconditionException.class)
  public void changePreconditionFailIfMatch() throws Exception {
    eTagHelper.checkChangePreconditions("\"ETag\"", Collections.singleton("\"ETag2\""), null);
  }

  @Test(expected = PreconditionException.class)
  public void changePreconditionFailIfNoneMatch() throws Exception {
    eTagHelper.checkChangePreconditions("\"ETag\"", null, Collections.singleton("\"ETag\""));
  }

  @Test(expected = PreconditionException.class)
  public void changePreconditionFailIfNoneMatchAll() throws Exception {
    eTagHelper.checkChangePreconditions("\"ETag\"", null, Collections.singleton("*"));
  }
}
