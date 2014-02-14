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
package com.msopentech.odatajclient.testservice;

import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/V4/Static.svc")
public class V4Services {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(V4Services.class);

    @GET
    @Path("/$metadata")
    @Produces("application/xml")
    public String getMetadata() {
        try {
            final String src = File.separatorChar + "v4" + File.separatorChar + "metadata.xml";
            return IOUtils.toString(getClass().getResourceAsStream(src), "UTF-8");
        } catch (Exception e) {
            LOG.error("Failure retrieving metadata information", e);
            return "";
        }
    }
}
