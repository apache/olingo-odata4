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
package org.apache.olingo.fit.rproxy;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.DriverConfiguration;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.Extension;
import org.esigate.util.HttpRequestHelper;

public class LinkRewrite implements Extension, IEventListener {

  private DriverConfiguration config;

  @Override
  public void init(final Driver driver, final Properties properties) {
    config = driver.getConfiguration();
    driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);
  }

  @Override
  public boolean event(final EventDefinition eventDef, final Event event) {
    final RenderEvent renderEvent = (RenderEvent) event;
    final String baseUrl = HttpRequestHelper.getBaseUrl(renderEvent.originalRequest).toString();
    final LinkRewriteRenderer fixup = new LinkRewriteRenderer(baseUrl, config.getVisibleBaseURL(baseUrl));

    // Add fixup renderer as first renderer.
    renderEvent.renderers.add(0, fixup);

    // Continue processing
    return true;
  }
}
