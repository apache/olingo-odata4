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

import static com.msopentech.odatajclient.testservice.Constants.*;

import java.io.File;
import java.io.InputStream;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServices {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractServices.class);

    protected abstract ODataVersion getVersion();

    protected final Utilities utils;

    public AbstractServices() throws FileSystemException {
        this.utils = new Utilities(getVersion());
    }

    /**
     * Provide sample services.
     *
     * @param accept Accept header.
     * @return OData services.
     */
    @GET
    public Response getSevices(@HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept) {
        try {
            final Accept acceptType = Accept.parse(accept, getVersion());

            if (acceptType == Accept.ATOM) {
                throw new UnsupportedMediaTypeException("Unsupported media type");
            }

            return utils.createResponse(utils.readFile(SERVICES, acceptType), null, acceptType);
        } catch (Exception e) {
            return utils.createFaultResponse(accept, e);
        }
    }

    /**
     * Provide sample metadata.
     *
     * @return metadata.
     */
    @GET
    @Path("/$metadata")
    @Produces("application/xml")
    public Response getMetadata() {
        return getMetadata(METADATA);
    }

    /**
     * Provide sample lartge metadata.
     *
     * @return metadata.
     */
    @GET
    @Path("/large/$metadata")
    @Produces("application/xml")
    public Response getLargeMetadata() {
        return getMetadata("large" + StringUtils.capitalize(METADATA));
    }

    private Response getMetadata(final String filename) {
        try {
            return utils.createResponse(utils.readFile(filename, Accept.XML), null, Accept.XML);
        } catch (Exception e) {
            return utils.createFaultResponse(Accept.XML.toString(), e);
        }
    }

    /**
     * Sample failing entity POST.
     *
     * @param accept Accept header.
     * @param format format query option.
     * @param entitySetName entity set name.
     * @return fault response.
     */
    @POST
    @Path("/{entitySetName}")
    public Response postNewEntity(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
            @PathParam("entitySetName") String entitySetName) {

        try {
            final Accept acceptType = Accept.parse(accept, getVersion());

            if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
                throw new UnsupportedMediaTypeException("Unsupported media type");
            }

            throw new Exception("Bad request ...");
        } catch (Exception e) {
            return utils.createFaultResponse(accept, e);
        }
    }

    /**
     * Retrieve entity set or function execution sample.
     *
     * @param accept Accept header.
     * @param name entity set or function name.
     * @param format format query option.
     * @param inlinecount inlinecount query option.
     * @param filter filter query option.
     * @param orderby orderby query option.
     * @param skiptoken skiptoken query option.
     * @return entity set or function result.
     */
    @GET
    @Path("/{name}")
    public Response getEntitySet(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @PathParam("name") String name,
            @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
            @QueryParam("$inlinecount") @DefaultValue(StringUtils.EMPTY) String inlinecount,
            @QueryParam("$filter") @DefaultValue(StringUtils.EMPTY) String filter,
            @QueryParam("$orderby") @DefaultValue(StringUtils.EMPTY) String orderby,
            @QueryParam("$skiptoken") @DefaultValue(StringUtils.EMPTY) String skiptoken) {

        try {
            final Accept acceptType;
            if (StringUtils.isNotBlank(format)) {
                acceptType = Accept.valueOf(format.toUpperCase());
            } else {
                acceptType = Accept.parse(accept, getVersion());
            }

            if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
                throw new UnsupportedMediaTypeException("Unsupported media type");
            }

            try {
                // search for function ...
                final InputStream func = utils.readFile(name, acceptType);
                return utils.createResponse(func, null, acceptType);
            } catch (NotFoundException e) {
                // search for entitySet ...
                final String basePath = name + File.separatorChar;

                final StringBuilder builder = new StringBuilder();
                builder.append(basePath);

                if (StringUtils.isNotBlank(orderby)) {
                    builder.append(ORDERBY).append(File.separatorChar).append(orderby).append(File.separatorChar);
                }

                if (StringUtils.isNotBlank(filter)) {
                    builder.append(FILTER).append(File.separatorChar).append(filter.replaceAll("/", "."));
                } else if (StringUtils.isNotBlank(skiptoken)) {
                    builder.append(SKIP_TOKEN).append(File.separatorChar).append(skiptoken);
                } else {
                    builder.append(FEED);
                }

                InputStream feed = utils.readFile(builder.toString(), acceptType);
                if ("allpages".equals(inlinecount)) {
                    int count = utils.countAllElements(name);
                    feed.close();
                    if (acceptType == Accept.ATOM) {
                        feed = utils.addAtomInlinecount(
                                utils.readFile(builder.toString(), acceptType),
                                count,
                                acceptType);
                    } else {
                        feed = utils.addJsonInlinecount(
                                utils.readFile(builder.toString(), acceptType),
                                count,
                                acceptType);
                    }
                }

                return utils.createResponse(feed, utils.getETag(basePath), acceptType);
            }
        } catch (Exception e) {
            return utils.createFaultResponse(accept, e);
        }
    }

    /**
     * Retrieve entity sample.
     *
     * @param accept Accept header.
     * @param entitySetName Entity set name.
     * @param entityId entity id.
     * @param format format query option.
     * @param expand expand query option.
     * @param select select query option.
     * @return entity.
     */
    @GET
    @Path("/{entitySetName}({entityId})")
    public Response getEntity(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @PathParam("entitySetName") String entitySetName,
            @PathParam("entityId") String entityId,
            @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
            @QueryParam("$expand") @DefaultValue(StringUtils.EMPTY) String expand,
            @QueryParam("$select") @DefaultValue(StringUtils.EMPTY) String select) {

        try {
            final Accept acceptType;
            if (StringUtils.isNotBlank(format)) {
                acceptType = Accept.valueOf(format.toUpperCase());
            } else {
                acceptType = Accept.parse(accept, getVersion());
            }

            if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
                throw new UnsupportedMediaTypeException("Unsupported media type");
            }

            final String basePath =
                    entitySetName + File.separatorChar + utils.getEntityKey(entityId) + File.separatorChar;

            InputStream entity = utils.readFile(basePath + ENTITY, acceptType);

            if (StringUtils.isNotBlank(select)) {
                if (acceptType == Accept.ATOM) {
                    entity = utils.selectAtomEntity(entity, select.split(","), acceptType);
                } else {
                    entity = utils.selectJsonEntity(entity, select.split(","), acceptType);
                }
            }

            if (StringUtils.isNotBlank(expand)) {
                entity = utils.expandLinks(entity, basePath, expand, acceptType);
            }

            return utils.createResponse(entity, utils.getETag(basePath), acceptType);
        } catch (Exception e) {
            return utils.createFaultResponse(accept, e);
        }
    }

    /**
     * Retrieve property sample.
     *
     * @param accept Accept header.
     * @param entitySetName Entity set name.
     * @param entityId entity id.
     * @param path path.
     * @param format format query option.
     * @return property.
     */
    @GET
    @Path("/{entitySetName}({entityId})/{path:.*}")
    public Response getPath(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @PathParam("entitySetName") String entitySetName,
            @PathParam("entityId") String entityId,
            @PathParam("path") String path,
            @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

        try {
            boolean searchForValue = path.endsWith("$value");
            Accept acceptType = null;
            if (StringUtils.isNotBlank(format)) {
                acceptType = Accept.valueOf(format.toUpperCase());
            } else if (StringUtils.isNotBlank(accept)) {
                acceptType = Accept.parse(accept, getVersion(), null);
            }

            final String basePath =
                    entitySetName + File.separatorChar + utils.getEntityKey(entityId) + File.separatorChar;

            InputStream stream = utils.readFile(basePath + ENTITY, acceptType == null ? Accept.XML : acceptType);

            if (searchForValue) {
                stream = utils.getAtomPropertyValue(stream, path.split("/"));
            } else {
                if (acceptType == null || acceptType == Accept.XML || acceptType == Accept.ATOM) {
                    // retrieve xml
                    stream = utils.getAtomProperty(stream, path.split("/"));
                } else {
                    // retrieve Edm type from xml
                    final String edmType = utils.getEdmTypeFromXML(utils.readFile(basePath + ENTITY, Accept.XML),
                            path.split("/"));
                    // retrieve json property
                    stream = utils.getJsonProperty(stream, path.split("/"), edmType);
                }
            }

            if ((searchForValue && acceptType != null && acceptType != Accept.TEXT) || acceptType == Accept.ATOM) {
                throw new UnsupportedMediaTypeException("Unsupported media type " + acceptType);
            }

            return utils.createResponse(stream, utils.getETag(basePath), acceptType);
        } catch (Exception e) {
            return utils.createFaultResponse(accept, e);
        }
    }

    /**
     * Retrieve links sample.
     *
     * @param accept Accept header.
     * @param entitySetName Entity set name.
     * @param entityId entity id.
     * @param linkName link name.
     * @param format format query option.
     * @return links.
     */
    @GET
    @Path("/{entitySetName}({entityId})/$links/{linkName}")
    public Response getLinks(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @PathParam("entitySetName") String entitySetName,
            @PathParam("entityId") String entityId,
            @PathParam("linkName") String linkName,
            @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
        try {
            final Accept acceptType;
            if (StringUtils.isNotBlank(format)) {
                acceptType = Accept.valueOf(format.toUpperCase());
            } else {
                acceptType = Accept.parse(accept, getVersion());
            }

            if (acceptType == Accept.ATOM) {
                throw new UnsupportedMediaTypeException("Unsupported media type");
            }

            final String basePath =
                    entitySetName + File.separatorChar + utils.getEntityKey(entityId) + File.separatorChar
                    + "links" + File.separatorChar;

            return utils.createResponse(
                    utils.readFile(basePath + linkName, acceptType), utils.getETag(basePath), acceptType);
        } catch (Exception e) {
            return utils.createFaultResponse(accept, e);
        }
    }

    /**
     * Count sample.
     *
     * @param accept Accept header.
     * @param entitySetName entity set name.
     * @return count.
     */
    @GET
    @Path("/{entitySetName}/$count")
    public Response count(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @PathParam("entitySetName") String entitySetName) {
        try {
            final Accept acceptType = Accept.parse(accept, getVersion(), Accept.TEXT);

            if (acceptType != Accept.TEXT) {
                throw new UnsupportedMediaTypeException("Unsupported type " + accept);
            }

            int count = utils.countAllElements(entitySetName);

            final Response.ResponseBuilder builder = Response.ok();
            builder.entity(count);

            return builder.build();
        } catch (Exception e) {
            return utils.createFaultResponse(accept, e);
        }
    }
}
