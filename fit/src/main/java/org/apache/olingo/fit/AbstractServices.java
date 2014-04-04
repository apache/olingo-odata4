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
package org.apache.olingo.fit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.fit.methods.MERGE;
import org.apache.olingo.fit.methods.PATCH;
import org.apache.olingo.fit.utils.AbstractJSONUtilities;
import org.apache.olingo.fit.utils.AbstractUtilities;
import org.apache.olingo.fit.utils.AbstractXMLUtilities;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.Commons;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.FSManager;
import org.apache.olingo.fit.utils.LinkInfo;
import org.apache.olingo.fit.utils.ODataVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServices {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractServices.class);

  private static final Set<ODataVersion> INITIALIZED = EnumSet.noneOf(ODataVersion.class);

  protected final ODataVersion version;

  protected final AbstractXMLUtilities xml;

  protected final AbstractJSONUtilities json;

  @Context
  protected UriInfo uriInfo;

  public AbstractServices(final ODataVersion version) throws Exception {
    this.version = version;
    if (ODataVersion.v3 == version) {
      this.xml = new org.apache.olingo.fit.utils.v3.XMLUtilities();
      this.json = new org.apache.olingo.fit.utils.v3.JSONUtilities();
    } else {
      this.xml = new org.apache.olingo.fit.utils.v4.XMLUtilities();
      this.json = new org.apache.olingo.fit.utils.v4.JSONUtilities();
    }

    if (!INITIALIZED.contains(version)) {
      xml.retrieveLinkInfoFromMetadata();
      INITIALIZED.add(version);
    }
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
      final Accept acceptType = Accept.parse(accept, version);

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      return xml.createResponse(
              FSManager.instance(version).readFile(Constants.get(version, ConstantKey.SERVICES), acceptType),
              null, acceptType);
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
    return getMetadata(Constants.get(version, ConstantKey.METADATA));
  }

  protected Response getMetadata(final String filename) {
    try {
      return xml.createResponse(FSManager.instance(version).readFile(filename, Accept.XML), null, Accept.XML);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(version), e);
    }
  }

  /**
   * Retrieve entity reference sample.
   *
   * @param accept Accept header.
   * @param path path.
   * @param format format query option.
   * @return entity reference or feed of entity reference.
   */
  @GET
  @Path("/{path:.*}/$ref")
  public Response getEntityReference(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("path") String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    try {
      final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

      if (utils.getKey() == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final String filename = Base64.encodeBase64String(path.getBytes("UTF-8"));

      return utils.getValue().createResponse(
              FSManager.instance(version).readFile(Constants.get(version, ConstantKey.REF)
                      + File.separatorChar + filename, utils.getKey()),
              null,
              utils.getKey());
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  @MERGE
  @Path("/{entitySetName}({entityId})")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response mergeEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) String ifMatch,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          final String changes) {

    return patchEntity(accept, prefer, ifMatch, entitySetName, entityId, changes);
  }

  @PATCH
  @Path("/{entitySetName}({entityId})")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response patchEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) String ifMatch,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          final String changes) {

    try {
      final Accept acceptType = Accept.parse(accept, version);

      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final AbstractUtilities util = acceptType == Accept.ATOM ? xml : json;
      InputStream res =
              util.patchEntity(entitySetName, entityId, IOUtils.toInputStream(changes), acceptType, ifMatch);

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(res, null, acceptType, Response.Status.OK);
      } else {
        res.close();
        response = xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @PUT
  @Path("/{entitySetName}({entityId})")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response replaceEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          final String entity) {
    try {
      final Accept acceptType = Accept.parse(accept, version);

      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      InputStream res;
      if (acceptType == Accept.ATOM) {
        res = xml.addOrReplaceEntity(entityId, entitySetName, IOUtils.toInputStream(entity));
      } else {
        res = json.addOrReplaceEntity(entityId, entitySetName, IOUtils.toInputStream(entity));
      }

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(res, null, acceptType, Response.Status.OK);
      } else {
        res.close();
        response = xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/{entitySetName}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM})
  public Response postNewEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          final String entity) {

    // default
    AbstractUtilities utils = xml;
    try {
      final Accept acceptType = Accept.parse(accept, version);

      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      utils = getUtilities(acceptType);

      final InputStream res;

      if (utils.isMediaContent(entitySetName)) {
        res = utils.addMediaEntity(entitySetName, IOUtils.toInputStream(entity));
      } else {
        res = utils.addOrReplaceEntity(entitySetName, IOUtils.toInputStream(entity));
      }

      final Response response;
      if ("return-no-content".equalsIgnoreCase(prefer)) {
        IOUtils.closeQuietly(res);
        response = utils.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
      } else {
        response = utils.createResponse(res, null, acceptType, Response.Status.CREATED);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      return utils.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Person({entityId})/{type:.*}/Sack")
  public Response actionSack(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @PathParam("type") final String type,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

    if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    final Map.Entry<String, InputStream> entityInfo = utils.getValue().readEntity("Person", entityId, utils.getKey());

    InputStream entity = entityInfo.getValue();
    try {
      final ByteArrayOutputStream copy = new ByteArrayOutputStream();
      IOUtils.copy(entity, copy);
      IOUtils.closeQuietly(entity);

      final String newContent = new String(copy.toByteArray(), "UTF-8").
              replaceAll("\"Salary\":[0-9]*,", "\"Salary\":0,").
              replaceAll("\"Title\":\".*\"", "\"Title\":\"[Sacked]\"").
              replaceAll("\\<d:Salary m:type=\"Edm.Int32\"\\>.*\\</d:Salary\\>",
                      "<d:Salary m:type=\"Edm.Int32\">0</d:Salary>").
              replaceAll("\\<d:Title\\>.*\\</d:Title\\>", "<d:Title>[Sacked]</d:Title>");

      final FSManager fsManager = FSManager.instance(version);
      fsManager.putInMemory(IOUtils.toInputStream(newContent, "UTF-8"),
              fsManager.getAbsolutePath(Commons.getEntityBasePath("Person", entityId) + Constants.get(version,
                              ConstantKey.ENTITY), utils.getKey()));

      return utils.getValue().createResponse(null, null, utils.getKey(), Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Person/{type:.*}/IncreaseSalaries")
  public Response actionIncreaseSalaries(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("type") final String type,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String body) {

    final String name = "Person";
    try {
      final Accept acceptType = Accept.parse(accept, version);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final JsonNode tree = new ObjectMapper().readTree(body);
      if (!tree.has("n")) {
        throw new Exception("Missing parameter: n");
      }
      final int n = tree.get("n").asInt();

      final StringBuilder path = new StringBuilder(name).
              append(File.separatorChar).append(type).
              append(File.separatorChar);
      path.append(Commons.getLinkInfo().get(version).isSingleton(name)
              ? Constants.get(version, ConstantKey.ENTITY)
              : Constants.get(version, ConstantKey.FEED));

      final InputStream feed = FSManager.instance(version).readFile(path.toString(), acceptType);

      final ByteArrayOutputStream copy = new ByteArrayOutputStream();
      IOUtils.copy(feed, copy);
      IOUtils.closeQuietly(feed);

      String newContent = new String(copy.toByteArray(), "UTF-8");
      final Pattern salary = Pattern.compile(acceptType == Accept.ATOM
              ? "\\<d:Salary m:type=\"Edm.Int32\"\\>(-?\\d+)\\</d:Salary\\>"
              : "\"Salary\":(-?\\d+),");
      final Matcher salaryMatcher = salary.matcher(newContent);
      while (salaryMatcher.find()) {
        final Long newSalary = Long.valueOf(salaryMatcher.group(1)) + n;
        newContent = newContent.
                replaceAll("\"Salary\":" + salaryMatcher.group(1) + ",",
                        "\"Salary\":" + newSalary + ",").
                replaceAll("\\<d:Salary m:type=\"Edm.Int32\"\\>" + salaryMatcher.group(1) + "</d:Salary\\>",
                        "<d:Salary m:type=\"Edm.Int32\">" + newSalary + "</d:Salary>");
      }

      FSManager.instance(version).putInMemory(IOUtils.toInputStream(newContent, "UTF-8"),
              FSManager.instance(version).getAbsolutePath(path.toString(), acceptType));

      return xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Retrieve entities from the given entity set and the given type.
   *
   * @param accept Accept header.
   * @param name entity set.
   * @param type entity type.
   * @return entity set.
   */
  @GET
  @Path("/{name}/{type:.*}")
  public Response getEntitySet(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("name") final String name,
          @PathParam("type") final String type) {

    try {
      final Accept acceptType = Accept.parse(accept, version);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final String basePath = name + File.separatorChar;
      final StringBuilder path = new StringBuilder(name).
              append(File.separatorChar).append(type).
              append(File.separatorChar);
      path.append(Commons.getLinkInfo().get(version).isSingleton(name)
              ? Constants.get(version, ConstantKey.ENTITY)
              : Constants.get(version, ConstantKey.FEED));

      final InputStream feed = FSManager.instance(version).readFile(path.toString(), acceptType);
      return xml.createResponse(feed, Commons.getETag(basePath, version), acceptType);
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
        acceptType = Accept.parse(accept, version);
      }

      try {
        // search for function ...
        final InputStream func = FSManager.instance(version).readFile(name, acceptType);
        return xml.createResponse(func, null, acceptType);
      } catch (NotFoundException e) {
        if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
          throw new UnsupportedMediaTypeException("Unsupported media type");
        }

        // search for entitySet ...
        final String basePath = name + File.separatorChar;

        final StringBuilder builder = new StringBuilder();
        builder.append(basePath);

        if (StringUtils.isNotBlank(orderby)) {
          builder.append(Constants.get(version, ConstantKey.ORDERBY)).append(File.separatorChar).
                  append(orderby).append(File.separatorChar);
        }

        if (StringUtils.isNotBlank(filter)) {
          builder.append(Constants.get(version, ConstantKey.FILTER)).append(File.separatorChar).
                  append(filter.replaceAll("/", "."));
        } else if (StringUtils.isNotBlank(skiptoken)) {
          builder.append(Constants.get(version, ConstantKey.SKIP_TOKEN)).append(File.separatorChar).
                  append(skiptoken);
        } else {
          builder.append(Commons.getLinkInfo().get(version).isSingleton(name)
                  ? Constants.get(version, ConstantKey.ENTITY)
                  : Constants.get(version, ConstantKey.FEED));
        }

        InputStream feed = FSManager.instance(version).readFile(builder.toString(), acceptType);
        if ("allpages".equals(inlinecount)) {
          int count = xml.countAllElements(name);
          feed.close();
          if (acceptType == Accept.ATOM) {
            feed = xml.addAtomInlinecount(
                    FSManager.instance(version).readFile(builder.toString(), acceptType),
                    count,
                    acceptType);
          } else {
            feed = json.addJsonInlinecount(
                    FSManager.instance(version).readFile(builder.toString(), acceptType),
                    count,
                    acceptType);
          }
        }

        return xml.createResponse(feed, Commons.getETag(basePath, version), acceptType);
      }
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/Person({entityId})")
  public Response getEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

    if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    final Map.Entry<String, InputStream> entityInfo = utils.getValue().readEntity("Person", entityId, utils.getKey());

    InputStream entity = entityInfo.getValue();
    try {
      if (utils.getKey() == Accept.JSON_FULLMETA || utils.getKey() == Accept.ATOM) {
        entity = utils.getValue().addOperation(entity, "Sack", "#DefaultContainer.Sack",
                uriInfo.getAbsolutePath().toASCIIString()
                + "/Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee/Sack");
      }

      return utils.getValue().createResponse(
              entity, Commons.getETag(entityInfo.getKey(), version), utils.getKey());
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
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

    return getEntityInternal(accept, entitySetName, entityId, format, expand, select, false);
  }

  protected Response getEntityInternal(
          final String accept,
          final String entitySetName,
          final String entityId,
          final String format,
          final String expand,
          final String select,
          final boolean keyAsSegment) {

    try {
      final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

      if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Map.Entry<String, InputStream> entityInfo =
              utils.getValue().readEntity(entitySetName, entityId, utils.getKey());

      InputStream entity = entityInfo.getValue();

      if (keyAsSegment) {
        entity = utils.getValue().addEditLink(
                entity, entitySetName,
                Constants.get(version, ConstantKey.DEFAULT_SERVICE_URL) + entitySetName + "/" + entityId);
      }

      if (StringUtils.isNotBlank(select)) {
        entity = utils.getValue().selectEntity(entity, select.split(","));
      }

      if (StringUtils.isNotBlank(expand)) {
        for (String exp : expand.split(",")) {
          entity = utils.getValue().expandEntity(
                  entitySetName,
                  entityId,
                  entity,
                  exp);
        }
      }

      return utils.getValue().createResponse(entity, Commons.getETag(entityInfo.getKey(), version), utils.getKey());
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/{entitySetName}({entityId})/$value")
  public Response getMediaEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId) {

    try {
      if (!accept.contains("*/*") && !accept.contains("application/octet-stream")) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final AbstractUtilities utils = getUtilities(null);
      final Map.Entry<String, InputStream> entityInfo = utils.readMediaEntity(entitySetName, entityId);
      return utils.createResponse(entityInfo.getValue(), Commons.getETag(entityInfo.getKey(), version), null);

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

      FSManager.instance(version).deleteFile(basePath + Constants.get(version, ConstantKey.ENTITY));

      return xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(version), e);
    }
  }

  private Response replaceProperty(
          final String accept,
          final String prefer,
          final String entitySetName,
          final String entityId,
          final String path,
          final String format,
          final String changes,
          final boolean justValue) {
    try {
      Accept acceptType = null;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else if (StringUtils.isNotBlank(accept)) {
        acceptType = Accept.parse(accept, version, null);
      }

      // if the given path is not about any link then search for property
      LOG.info("Retrieve property {}", path);

      final AbstractUtilities utils = getUtilities(acceptType);

      final InputStream changed = utils.replaceProperty(
              entitySetName,
              entityId,
              IOUtils.toInputStream(changes),
              Arrays.asList(path.split("/")),
              acceptType,
              justValue);

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(changed, null, acceptType, Response.Status.OK);
      } else {
        changed.close();
        response = xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;

    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  private Response deletePropertyValue(
          final String accept,
          final String prefer,
          final String entitySetName,
          final String entityId,
          final String path,
          final String format) {
    try {
      Accept acceptType = null;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else if (StringUtils.isNotBlank(accept)) {
        acceptType = Accept.parse(accept, version, null);
      }

      // if the given path is not about any link then search for property
      LOG.info("Retrieve property {}", path);

      final AbstractUtilities utils = getUtilities(acceptType);

      final InputStream changed = utils.deleteProperty(
              entitySetName,
              entityId,
              Arrays.asList(path.split("/")),
              acceptType);

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(changed, null, acceptType, Response.Status.OK);
      } else {
        changed.close();
        response = xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;

    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Replace property value.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return
   */
  @PUT
  @Path("/{entitySetName}({entityId})/{path:.*}/$value")
  public Response replacePropertyValue(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("path") String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
          final String changes) {
    return replaceProperty(accept, prefer, entitySetName, entityId, path, format, changes, true);
  }

  /**
   * Replace property.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return
   */
  @MERGE
  @Path("/{entitySetName}({entityId})/{path:.*}")
  public Response mergeProperty(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("path") String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
          final String changes) {
    return replaceProperty(accept, prefer, entitySetName, entityId, path, format, changes, false);
  }

  /**
   * Replace property.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return
   */
  @PATCH
  @Path("/{entitySetName}({entityId})/{path:.*}")
  public Response patchProperty(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("path") String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
          final String changes) {
    return replaceProperty(accept, prefer, entitySetName, entityId, path, format, changes, false);
  }

  @PUT
  @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.WILDCARD, MediaType.APPLICATION_OCTET_STREAM})
  @Path("/{entitySetName}({entityId})/$value")
  public Response replaceMediaEntity(
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
          String value) {
    try {

      final AbstractUtilities utils = getUtilities(null);

      InputStream res = utils.putMediaInMemory(entitySetName, entityId, IOUtils.toInputStream(value));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(res, null, null, Response.Status.OK);
      } else {
        res.close();
        response = xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;

    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(Accept.JSON.toString(version), e);
    }
  }

  /**
   * Replace property.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return
   */
  @PUT
  @Path("/{entitySetName}({entityId})/{path:.*}")
  public Response replaceProperty(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("path") String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
          final String changes) {
    if (xml.isMediaContent(entitySetName + "/" + path)) {
      return replaceMediaProperty(prefer, entitySetName, entityId, path, changes);
    } else {
      return replaceProperty(accept, prefer, entitySetName, entityId, path, format, changes, false);
    }
  }

  private Response replaceMediaProperty(
          final String prefer,
          final String entitySetName,
          final String entityId,
          final String path,
          final String value) {
    try {
      final AbstractUtilities utils = getUtilities(null);

      InputStream res = utils.putMediaInMemory(entitySetName, entityId, path, IOUtils.toInputStream(value));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(res, null, null, Response.Status.OK);
      } else {
        res.close();
        response = xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;

    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(Accept.JSON.toString(version), e);
    }
  }

  /**
   * Nullify property value.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return
   */
  @DELETE
  @Path("/{entitySetName}({entityId})/{path:.*}/$value")
  public Response deleteProperty(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("path") String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    return deletePropertyValue(accept, prefer, entitySetName, entityId, path, format);
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
  @Path("/{entitySetName}({entityId})/{path:.*}/$value")
  public Response getPathValue(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("path") String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    AbstractUtilities utils = null;
    try {
      Accept acceptType = null;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else if (StringUtils.isNotBlank(accept)) {
        acceptType = Accept.parse(accept, version, null);
      }
      utils = getUtilities(acceptType);

      return navigateProperty(acceptType, entitySetName, entityId, path, true);

    } catch (Exception e) {
      return (utils == null ? xml : utils).createFaultResponse(accept, e);
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

    // default utilities
    final AbstractUtilities utils = xml;

    try {
      if (utils.isMediaContent(entitySetName + "/" + path)) {
        return navigateStreamedEntity(entitySetName, entityId, path);
      } else {
        Accept acceptType = null;
        if (StringUtils.isNotBlank(format)) {
          acceptType = Accept.valueOf(format.toUpperCase());
        } else if (StringUtils.isNotBlank(accept)) {
          acceptType = Accept.parse(accept, version, null);
        }

        try {
          return navigateEntity(acceptType, entitySetName, entityId, path);
        } catch (NotFoundException e) {
          // if the given path is not about any link then search for property
          return navigateProperty(acceptType, entitySetName, entityId, path, false);
        }
      }
    } catch (Exception e) {
      return utils.createFaultResponse(accept, e);
    }
  }

  private Response navigateStreamedEntity(
          String entitySetName,
          String entityId,
          String path) throws Exception {

    final AbstractUtilities utils = getUtilities(null);
    final Map.Entry<String, InputStream> entityInfo = utils.readMediaEntity(entitySetName, entityId, path);
    return utils.createResponse(entityInfo.getValue(), Commons.getETag(entityInfo.getKey(), version), null);
  }

  private Response navigateEntity(
          final Accept acceptType,
          String entitySetName,
          String entityId,
          String path) throws Exception {
    final String basePath = Commons.getEntityBasePath(entitySetName, entityId);

    final LinkInfo linkInfo = xml.readLinks(entitySetName, entityId, path, Accept.XML);
    final Map.Entry<String, List<String>> links = xml.extractLinkURIs(linkInfo.getLinks());

    InputStream stream;

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

    return xml.createResponse(stream, Commons.getETag(basePath, version), acceptType);
  }

  private Response navigateProperty(
          final Accept acceptType,
          final String entitySetName,
          final String entityId,
          final String path,
          final boolean searchForValue) throws Exception {

    if ((searchForValue && acceptType != null && acceptType != Accept.TEXT) || acceptType == Accept.ATOM) {
      throw new UnsupportedMediaTypeException("Unsupported media type " + acceptType);
    }

    final String basePath = Commons.getEntityBasePath(entitySetName, entityId);

    final AbstractUtilities utils = getUtilities(acceptType);

    final List<String> pathElements = Arrays.asList(path.split("\\/"));

    InputStream stream;

    if (searchForValue) {
      stream = FSManager.instance(version).readFile(
              basePath + Constants.get(version, ConstantKey.ENTITY),
              acceptType == null || acceptType == Accept.TEXT ? Accept.XML : acceptType);

      stream = utils.getPropertyValue(stream, pathElements);
    } else {
      String edmType = xml.getEdmTypeFromAtom(entitySetName, entityId, pathElements);
      stream = utils.getProperty(entitySetName, entityId, pathElements, edmType);
    }

    return xml.createResponse(stream, Commons.getETag(basePath, version), acceptType);
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
        acceptType = Accept.parse(accept, version);
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

  @POST
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response postLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          String link,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Accept content;
      if (StringUtils.isNotBlank(contentType)) {
        content = Accept.parse(contentType, version);
      } else {
        content = acceptType;
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      final List<String> links;
      if (content == Accept.XML || content == Accept.TEXT || content == Accept.ATOM) {
        links = xml.extractLinkURIs(IOUtils.toInputStream(link)).getValue();
      } else {
        links = json.extractLinkURIs(IOUtils.toInputStream(link)).getValue();
      }

      utils.putLinksInMemory(
              Commons.getEntityBasePath(entitySetName, entityId),
              entitySetName,
              entityId,
              linkName,
              links);

      return xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @MERGE
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response mergeLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          String link,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    return putLink(accept, contentType, entitySetName, entityId, linkName, link, format);
  }

  @PATCH
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response patchLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          String link,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    return putLink(accept, contentType, entitySetName, entityId, linkName, link, format);
  }

  @PUT
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response putLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          String link,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Accept content;
      if (StringUtils.isNotBlank(contentType)) {
        content = Accept.parse(contentType, version);
      } else {
        content = acceptType;
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      final List<String> links;
      if (content == Accept.XML || content == Accept.TEXT || content == Accept.ATOM) {
        links = xml.extractLinkURIs(IOUtils.toInputStream(link)).getValue();
      } else {
        links = json.extractLinkURIs(IOUtils.toInputStream(link)).getValue();
      }

      utils.putLinksInMemory(
              Commons.getEntityBasePath(entitySetName, entityId),
              entitySetName,
              linkName,
              links);

      return xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @DELETE
  @Path("/{entitySetName}({entityId})/$links/{linkName}({linkId})")
  public Response deleteLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          @PathParam("linkId") String linkId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      final Map.Entry<String, List<String>> currents = json.extractLinkURIs(utils.readLinks(
              entitySetName, entityId, linkName, Accept.JSON_FULLMETA).getLinks());

      final Map.Entry<String, List<String>> toBeRemoved = json.extractLinkURIs(utils.readLinks(
              entitySetName, entityId, linkName + "(" + linkId + ")", Accept.JSON_FULLMETA).getLinks());

      final List<String> remains = currents.getValue();
      remains.removeAll(toBeRemoved.getValue());

      utils.putLinksInMemory(
              Commons.getEntityBasePath(entitySetName, entityId),
              entitySetName,
              linkName,
              remains);

      return xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
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
      final Accept acceptType = Accept.parse(accept, version, Accept.TEXT);

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

  private Map.Entry<Accept, AbstractUtilities> getUtilities(final String accept, final String format) {
    final Accept acceptType;
    if (StringUtils.isNotBlank(format)) {
      acceptType = Accept.valueOf(format.toUpperCase());
    } else {
      acceptType = Accept.parse(accept, version);
    }

    return new AbstractMap.SimpleEntry<Accept, AbstractUtilities>(acceptType, getUtilities(acceptType));
  }

  private AbstractUtilities getUtilities(final Accept accept) {
    final AbstractUtilities utils;
    if (accept == Accept.XML || accept == Accept.TEXT || accept == Accept.ATOM) {
      utils = xml;
    } else {
      utils = json;
    }

    return utils;
  }
}
