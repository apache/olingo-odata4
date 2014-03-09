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

import com.msopentech.odatajclient.testservice.utils.Accept;
import com.msopentech.odatajclient.testservice.utils.XMLUtilities;
import com.msopentech.odatajclient.testservice.utils.JSONUtilities;
import com.msopentech.odatajclient.testservice.utils.ODataVersion;
import com.msopentech.odatajclient.testservice.utils.FSManager;

import static com.msopentech.odatajclient.testservice.utils.Constants.*;

import com.msopentech.odatajclient.testservice.methods.PATCH;
import com.msopentech.odatajclient.testservice.utils.AbstractUtilities;
import com.msopentech.odatajclient.testservice.utils.Commons;
import com.msopentech.odatajclient.testservice.utils.LinkInfo;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServices {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractServices.class);

    protected abstract ODataVersion getVersion();

    protected final XMLUtilities xml;

    protected final JSONUtilities json;

    public AbstractServices() throws Exception {
        this.xml = new XMLUtilities(getVersion());
        this.json = new JSONUtilities(getVersion());
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

            return xml.createResponse(
                    FSManager.instance(getVersion()).readFile(SERVICES, acceptType), null, acceptType);
        } catch (Exception e) {
            return xml.createFaultResponse(accept, e);
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
            return xml.
                    createResponse(FSManager.instance(getVersion()).readFile(filename, Accept.XML), null, Accept.XML);
        } catch (Exception e) {
            return xml.createFaultResponse(Accept.XML.toString(), e);
        }
    }

    @PATCH
    @Path("/{entitySetName}({entityId})")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    public Response patchEntity(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
            @PathParam("entitySetName") String entitySetName,
            @PathParam("entityId") String entityId,
            final String changes) {

        try {
            final Accept acceptType = Accept.parse(accept, getVersion());

            if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
                throw new UnsupportedMediaTypeException("Unsupported media type");
            }

            final AbstractUtilities util = acceptType == Accept.ATOM ? xml : json;
            InputStream res = util.patchEntity(entitySetName, entityId, IOUtils.toInputStream(changes), acceptType);
            return xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
        } catch (Exception e) {
            return xml.createFaultResponse(accept, e);
        }
    }

    @PUT
    @Path("/{entitySetName}({entityId})")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    public Response putNewEntity(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
            @PathParam("entitySetName") String entitySetName,
            @PathParam("entityId") String entityId,
            final String entity) {
        try {

            final Accept acceptType = Accept.parse(accept, getVersion());

            if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
                throw new UnsupportedMediaTypeException("Unsupported media type");
            }

            final InputStream res;
            if (acceptType == Accept.ATOM) {
                res = xml.saveSingleEntity(entityId, entitySetName, IOUtils.toInputStream(entity));
            } else {
                res = json.saveSingleEntity(entityId, entitySetName, IOUtils.toInputStream(entity));
            }

            res.close();

            final Response response = xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
            response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
            return response;
        } catch (Exception e) {
            return xml.createFaultResponse(accept, e);
        }
    }

    @POST
    @Path("/{entitySetName}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    public Response postNewEntity(
            @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
            @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
            @PathParam("entitySetName") String entitySetName,
            final String entity) {

        try {

            final Accept acceptType = Accept.parse(accept, getVersion());

            if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
                throw new UnsupportedMediaTypeException("Unsupported media type");
            }

            final InputStream res;
            if (acceptType == Accept.ATOM) {
                res = xml.createEntity(entitySetName, IOUtils.toInputStream(entity));
            } else {
                res = json.createEntity(entitySetName, IOUtils.toInputStream(entity));
            }

            if (prefer.equalsIgnoreCase("return-no-content")) {
                res.close();
                Response response = xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
                response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
                return response;
            } else {
                return xml.createResponse(res, null, acceptType, Response.Status.CREATED);
            }
        } catch (Exception e) {
            return xml.createFaultResponse(accept, e);
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
                final InputStream func = FSManager.instance(getVersion()).readFile(name, acceptType);
                return xml.createResponse(func, null, acceptType);
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

                InputStream feed = FSManager.instance(getVersion()).readFile(builder.toString(), acceptType);
                if ("allpages".equals(inlinecount)) {
                    int count = xml.countAllElements(name);
                    feed.close();
                    if (acceptType == Accept.ATOM) {
                        feed = xml.addAtomInlinecount(
                                FSManager.instance(getVersion()).readFile(builder.toString(), acceptType),
                                count,
                                acceptType);
                    } else {
                        feed = json.addJsonInlinecount(
                                FSManager.instance(getVersion()).readFile(builder.toString(), acceptType),
                                count,
                                acceptType);
                    }
                }

                return xml.createResponse(feed, Commons.getETag(basePath, getVersion()), acceptType);
            }
        } catch (Exception e) {
            return xml.createFaultResponse(accept, e);
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

            final Map.Entry<String, InputStream> entityInfo = xml.readEntity(entitySetName, entityId, acceptType);

            InputStream entity = entityInfo.getValue();

            if (StringUtils.isNotBlank(select)) {
                if (acceptType == Accept.ATOM) {
                    entity = xml.selectEntity(entity, select.split(","));
                } else {
                    entity = json.selectEntity(entity, select.split(","));
                }
            }

            if (StringUtils.isNotBlank(expand)) {
                if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
                    throw new UnsupportedMediaTypeException("Unsupported media type");
                } else if (acceptType == Accept.ATOM) {
                    for (String exp : expand.split(",")) {
                        entity = xml.expandEntity(
                                entitySetName,
                                entityId,
                                entity,
                                exp);
                    }
                } else {
                    for (String exp : expand.split(",")) {
                        entity = json.expandEntity(
                                entitySetName,
                                entityId,
                                entity,
                                exp);
                    }
                }
            }

            return xml.createResponse(entity, Commons.getETag(entityInfo.getKey(), getVersion()), acceptType);
        } catch (Exception e) {
            LOG.error("Error retrieving entity", e);
            return xml.createFaultResponse(accept, e);
        }
    }

    @DELETE
    @Path("/{entitySetName}({entityId})")
    public Response removeEntity(
            @PathParam("entitySetName") String entitySetName,
            @PathParam("entityId") String entityId) {

        try {
            final String basePath =
                    entitySetName + File.separatorChar + Commons.getEntityKey(entityId) + File.separatorChar;

            FSManager.instance(getVersion()).deleteFile(basePath + ENTITY);

            return xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
        } catch (Exception e) {
            return xml.createFaultResponse(Accept.XML.toString(), e);
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
                    entitySetName + File.separatorChar + Commons.getEntityKey(entityId) + File.separatorChar;

            InputStream stream;

            try {
                final LinkInfo linkInfo = xml.readLinks(entitySetName, entityId, path, Accept.XML);
                final Map.Entry<String, List<String>> links = XMLUtilities.extractLinkURIs(linkInfo.getLinks());

                switch (acceptType) {
                    case JSON:
                    case JSON_FULLMETA:
                    case JSON_NOMETA:
                        stream = json.readEntities(links.getValue(), path, links.getKey(), linkInfo.isFeed());
                        stream = json.wrapJsonEntities(stream);
                        break;
                    default:
                        stream = xml.readEntities(links.getValue(), path, links.getKey(), linkInfo.isFeed());
                }
            } catch (NotFoundException e) {
                // if the given path is not about any link then search for property
                LOG.info("Retrieve property {}", path);

                stream = FSManager.instance(getVersion()).readFile(
                        basePath + ENTITY, acceptType == null || acceptType == Accept.TEXT
                        ? Accept.XML : acceptType);

                if (searchForValue) {
                    stream = xml.getAtomPropertyValue(stream, path.split("/"));
                } else {
                    if (acceptType == null || acceptType == Accept.XML || acceptType == Accept.ATOM) {
                        // retrieve xml
                        stream = xml.getAtomProperty(stream, path.split("/"));
                    } else {
                        // retrieve Edm type from xml
                        final String edmType = xml.getEdmTypeFromXML(
                                FSManager.instance(getVersion()).readFile(basePath + ENTITY, Accept.XML),
                                path.split("/"));
                        // retrieve json property
                        stream = json.getJsonProperty(stream, path.split("/"), edmType);
                    }
                }

                if ((searchForValue && acceptType != null && acceptType != Accept.TEXT) || acceptType == Accept.ATOM) {
                    throw new UnsupportedMediaTypeException("Unsupported media type " + acceptType);
                }
            }

            return xml.createResponse(stream, Commons.getETag(basePath, getVersion()), acceptType);
        } catch (Exception e) {
            return xml.createFaultResponse(accept, e);
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

            final LinkInfo links = xml.readLinks(entitySetName, entityId, linkName, acceptType);

            return xml.createResponse(
                    links.getLinks(),
                    links.getEtag(),
                    acceptType);
        } catch (Exception e) {
            return xml.createFaultResponse(accept, e);
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

            int count = xml.countAllElements(entitySetName);

            final Response.ResponseBuilder builder = Response.ok();
            builder.entity(count);

            return builder.build();
        } catch (Exception e) {
            return xml.createFaultResponse(accept, e);
        }
    }
}
