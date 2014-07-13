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
package org.apache.olingo.fit.proxy.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Car;
import org.junit.Test;

/**
 * This is the unit test class to check media entity retrieve operations.
 */
public class MediaEntityTestITCase extends AbstractTestITCase {

  @Test
  public void read() throws IOException {
    final InputStream is = container.getCar().getByKey(12).load().getStream();
    assertNotNull(is);
    IOUtils.closeQuietly(is);
  }

  @Test
  public void updateReadStreamedProperty() throws IOException {
    final String TO_BE_UPDATED = "buffered stream sample (" + System.currentTimeMillis() + ")";
    final InputStream input = new ByteArrayInputStream(TO_BE_UPDATED.getBytes());

    Car car = container.getCar().getByKey(12).load();
    car.setPhoto(input);

    container.flush();

    car = container.getCar().getByKey(12).load();
    final InputStream is = car.getPhoto();
    assertEquals(TO_BE_UPDATED, IOUtils.toString(is));
    IOUtils.closeQuietly(is);
  }

  @Test
  public void update() throws IOException {
    final Car car = container.getCar().getByKey(14);
    assertNotNull(car);

    final String TO_BE_UPDATED = "buffered stream sample (" + System.currentTimeMillis() + ")";
    InputStream input = IOUtils.toInputStream(TO_BE_UPDATED);

    car.setStream(input);

    container.flush();

    input = container.getCar().getByKey(14).load().getStream();
    assertEquals(TO_BE_UPDATED, IOUtils.toString(input));
    IOUtils.closeQuietly(input);
  }

  @Test
  public void create() throws IOException {
    Car car = container.getCar().newCar();

    final String TO_BE_UPDATED = "buffered stream sample (" + System.currentTimeMillis() + ")";
    InputStream input = IOUtils.toInputStream(TO_BE_UPDATED);

    final String DESC = "DESC - " + System.currentTimeMillis();
    car.setStream(input);
    car.setDescription(DESC);

    container.flush();

    int key = car.getVIN();
    assertTrue(key > 0);

    service.getContext().detachAll();

    car = container.getCar().getByKey(key).load();
    assertEquals(DESC, car.getDescription());
    input = car.getStream();
    assertEquals(TO_BE_UPDATED, IOUtils.toString(input));
    IOUtils.closeQuietly(input);

    container.getCar().delete(key);
    container.flush();

    try {
      container.getCar().getByKey(key).load();
      fail();
    } catch (IllegalArgumentException e) {
    }
  }
}
