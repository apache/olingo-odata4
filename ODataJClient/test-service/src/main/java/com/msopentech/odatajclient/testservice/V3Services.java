/**
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
package com.msopentech.odatajclient.testservice;

import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/V3/Static.svc")
public class V3Services {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(V3Services.class);

    @GET
    @Path("/$metadata")
    @Produces("application/xml")
    public String getMetadata() {
        try {
            final String src = File.separatorChar + "v3" + File.separatorChar + "metadata.xml";
            return IOUtils.toString(getClass().getResourceAsStream(src), "UTF-8");
        } catch (Exception e) {
            LOG.error("Failure retrieving metadata information", e);
            return "";
        }
    }
}
