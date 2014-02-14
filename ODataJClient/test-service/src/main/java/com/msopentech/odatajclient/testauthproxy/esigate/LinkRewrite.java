/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.testauthproxy.esigate;

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
        this.config = driver.getConfiguration();
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
