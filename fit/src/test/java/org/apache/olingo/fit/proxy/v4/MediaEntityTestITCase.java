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
package org.apache.olingo.fit.proxy.v4;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.EntityContainerFactory;
import org.apache.olingo.fit.proxy.v4.demo.odatademo.DemoService;
import org.apache.olingo.fit.proxy.v4.demo.odatademo.types.Advertisement;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is the unit test class to check media entity retrieve operations.
 */
public class MediaEntityTestITCase extends AbstractTestITCase {

  private static DemoService dcontainer;

  @BeforeClass
  public static void initContainer() {
    final EntityContainerFactory<EdmEnabledODataClient> otcontainerFactory =
            EntityContainerFactory.getV4(testDemoServiceRootURL);
    otcontainerFactory.getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    dcontainer = otcontainerFactory.getEntityContainer(DemoService.class);
    assertNotNull(dcontainer);
  }

  @Test
  public void read() throws IOException {
    final UUID uuid = UUID.fromString("f89dee73-af9f-4cd4-b330-db93c25ff3c7");

    final Advertisement adv = dcontainer.getAdvertisements().get(uuid);
    assertTrue(adv.getAirDate() instanceof Calendar);

    final InputStream is = adv.getStream();
    assertNotNull(is);
    IOUtils.closeQuietly(is);
  }

  @Test
  public void update() throws IOException {
    final UUID uuid = UUID.fromString("f89dee73-af9f-4cd4-b330-db93c25ff3c7");

    final Advertisement adv = dcontainer.getAdvertisements().get(uuid);
    assertNotNull(adv);

    final String random = RandomStringUtils.random(124, "abcdefghijklmnopqrstuvwxyz");

    adv.setStream(IOUtils.toInputStream(random));

    dcontainer.flush();

    assertEquals(random, IOUtils.toString(dcontainer.getAdvertisements().get(uuid).getStream()));
  }

  @Test
  public void create() throws IOException {
    final String random = RandomStringUtils.random(124, "abcdefghijklmnopqrstuvwxyz");

    final Advertisement adv = dcontainer.getAdvertisements().newAdvertisement();
    adv.setStream(IOUtils.toInputStream(random));
    adv.setAirDate(Calendar.getInstance());

    dcontainer.flush();

    final UUID uuid = adv.getID();
    entityContext.detachAll();
    
    assertEquals(random, IOUtils.toString(dcontainer.getAdvertisements().get(uuid).getStream()));

    entityContext.detachAll();

    dcontainer.getAdvertisements().delete(uuid);
    dcontainer.flush();

    assertNull(dcontainer.getAdvertisements().get(uuid));
  }
}
