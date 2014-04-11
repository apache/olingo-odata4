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

import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.data.AtomFeedImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.serializer.JsonFeedContainer;
import org.apache.olingo.fit.serializer.JsonEntryContainer;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.DataBinder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.core.data.AtomEntryImpl;
import org.apache.olingo.commons.core.data.AtomPropertyImpl;
import org.apache.olingo.commons.core.data.AtomSerializer;
import org.apache.olingo.commons.core.data.JSONEntryImpl;
import org.apache.olingo.commons.core.data.JSONFeedImpl;
import org.apache.olingo.commons.core.data.NullValueImpl;
import org.apache.olingo.commons.core.data.PrimitiveValueImpl;
import org.apache.olingo.fit.metadata.EntitySet;
import org.apache.olingo.fit.metadata.EntityType;
import org.apache.olingo.fit.metadata.NavigationProperty;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.FSManager;

import org.apache.olingo.fit.utils.Commons;
import org.apache.olingo.fit.methods.MERGE;
import org.apache.olingo.fit.methods.PATCH;
import org.apache.olingo.fit.serializer.FITAtomDeserializer;
import org.apache.olingo.fit.utils.AbstractJSONUtilities;
import org.apache.olingo.fit.utils.AbstractUtilities;
import org.apache.olingo.fit.utils.AbstractXMLUtilities;
import org.apache.olingo.fit.utils.LinkInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServices {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractServices.class);

  protected final ODataServiceVersion version;

  protected final AbstractXMLUtilities xml;

  protected final AbstractJSONUtilities json;

  @Context
  protected UriInfo uriInfo;

  protected Metadata metadata;

  public AbstractServices(final ODataServiceVersion version) throws Exception {
    this.version = version;
    if (version.compareTo(ODataServiceVersion.V30) <= 0) {
      this.xml = new org.apache.olingo.fit.utils.v3.XMLUtilities();
      this.json = new org.apache.olingo.fit.utils.v3.JSONUtilities();
    } else {
      this.xml = new org.apache.olingo.fit.utils.v4.XMLUtilities();
      this.json = new org.apache.olingo.fit.utils.v4.JSONUtilities();
    }

    metadata = Commons.getMetadata(version);
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
  @Produces(MediaType.APPLICATION_XML)
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

      final FITAtomDeserializer atomDeserializer = Commons.getAtomDeserializer(version);

      final ObjectMapper mapper = Commons.getJsonMapper(version);

      final Container<AtomEntryImpl> cres;
      if (acceptType == Accept.ATOM) {
        cres = atomDeserializer.read(res, AtomEntryImpl.class);
      } else {
        final Container<JSONEntryImpl> jcont = mapper.readValue(res, new TypeReference<JSONEntryImpl>() {
        });
        cres = new Container<AtomEntryImpl>(jcont.getContextURL(), jcont.getMetadataETag(),
                (new DataBinder(version)).getAtomEntry(jcont.getObject()));
      }

      normalizeAtomEntry(cres.getObject(), entitySetName, entityId);

      final String path = Commons.getEntityBasePath(entitySetName, entityId);
      FSManager.instance(version).putInMemory(
              cres, path + File.separatorChar + Constants.get(version, ConstantKey.ENTITY));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(
                util.readEntity(entitySetName, entityId, acceptType).getValue(),
                null, acceptType, Response.Status.OK);
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

      final FITAtomDeserializer atomDeserializer = Commons.getAtomDeserializer(version);
      final ObjectMapper mapper = Commons.getJsonMapper(version);

      final Container<AtomEntryImpl> cres;
      if (acceptType == Accept.ATOM) {
        cres = atomDeserializer.read(res, AtomEntryImpl.class);
      } else {
        final Container<JSONEntryImpl> jcont = mapper.readValue(res, new TypeReference<JSONEntryImpl>() {
        });
        cres = new Container<AtomEntryImpl>(jcont.getContextURL(), jcont.getMetadataETag(),
                (new DataBinder(version)).getAtomEntry(jcont.getObject()));
      }

      final String path = Commons.getEntityBasePath(entitySetName, entityId);
      FSManager.instance(version).putInMemory(
              cres, path + File.separatorChar + Constants.get(version, ConstantKey.ENTITY));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(
                getUtilities(acceptType).readEntity(entitySetName, entityId, acceptType).getValue(),
                null, acceptType, Response.Status.OK);
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
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
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

      final FITAtomDeserializer atomDeserializer = Commons.getAtomDeserializer(version);
      final AtomSerializer atomSerializer = Commons.getAtomSerializer(version);
      final ObjectMapper mapper = Commons.getJsonMapper(version);

      final Container<AtomEntryImpl> container;

      final EntitySet entitySet = metadata.getEntitySet(entitySetName);
      final AtomEntryImpl entry;

      final String entityKey;

      if (utils.isMediaContent(entitySetName)) {
        entityKey = xml.getDefaultEntryKey(entitySetName, null);

        utils.addMediaEntityValue(entitySetName, entityKey, IOUtils.toInputStream(entity));

        entry = new AtomEntryImpl();
        entry.setMediaContentType(ContentType.WILDCARD);
        entry.setType(entitySet.getType());

        final String id = Commons.getMediaContent().get(entitySetName);
        if (StringUtils.isNotBlank(id)) {
          final AtomPropertyImpl prop = new AtomPropertyImpl();
          prop.setName(id);
          prop.setType(EdmPrimitiveTypeKind.Int32.toString());
          prop.setValue(new PrimitiveValueImpl(entityKey));
          entry.getProperties().add(prop);
        }

        final Link editLink = new LinkImpl();
        editLink.setHref(Commons.getEntityURI(entitySetName, entityKey));
        editLink.setRel("edit");
        editLink.setTitle(entitySetName);
        entry.setEditLink(editLink);

        entry.setMediaContentSource(editLink.getHref() + "/$value");

        container = new Container<AtomEntryImpl>(null, null, entry);
      } else {
        final Accept contentTypeValue = Accept.parse(contentType, version);
        entityKey = getUtilities(contentTypeValue).getDefaultEntryKey(entitySetName, IOUtils.toInputStream(entity));

        if (Accept.ATOM == contentTypeValue) {
          container = atomDeserializer.read(IOUtils.toInputStream(entity), AtomEntryImpl.class);
          entry = container.getObject();
        } else {
          final Container<JSONEntryImpl> jcontainer =
                  mapper.readValue(IOUtils.toInputStream(entity), new TypeReference<JSONEntryImpl>() {
                  });

          entry = (new DataBinder(version)).
                  getAtomEntry(jcontainer.getObject());

          container = new Container<AtomEntryImpl>(
                  jcontainer.getContextURL(),
                  jcontainer.getMetadataETag(),
                  entry);
        }
      }

      normalizeAtomEntry(entry, entitySetName, entityKey);

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter(content, Constants.encoding);
      atomSerializer.write(writer, container);
      writer.flush();
      writer.close();

      final InputStream serialization =
              xml.addOrReplaceEntity(entitySetName, new ByteArrayInputStream(content.toByteArray()));

      final Container<AtomEntryImpl> cres = atomDeserializer.read(serialization, AtomEntryImpl.class);

      final String path = Commons.getEntityBasePath(entitySetName, entityKey);
      FSManager.instance(version).putInMemory(
              cres, path + File.separatorChar + Constants.get(version, ConstantKey.ENTITY));

      final Response response;
      if ("return-no-content".equalsIgnoreCase(prefer)) {
        response = utils.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
      } else {
        response = utils.createResponse(utils.readEntity(entitySetName, entityKey, acceptType).getValue(),
                null, acceptType, Response.Status.CREATED);
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

      path.append(metadata.getEntitySet(name).isSingleton()
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

      path.append(metadata.getEntitySet(name).isSingleton()
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
   * @param count inlinecount query option.
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
          @QueryParam("$inlinecount") @DefaultValue(StringUtils.EMPTY) String count,
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
          builder.append(metadata.getEntitySet(name).isSingleton()
                  ? Constants.get(version, ConstantKey.ENTITY)
                  : Constants.get(version, ConstantKey.FEED));
        }

        final InputStream feed = FSManager.instance(version).readFile(builder.toString(), Accept.ATOM);

        final FITAtomDeserializer atomDeserializer = Commons.getAtomDeserializer(version);
        final AtomSerializer atomSerializer = Commons.getAtomSerializer(version);
        final Container<AtomFeedImpl> container = atomDeserializer.read(feed, AtomFeedImpl.class);

        setInlineCount(container.getObject(), count);

        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.encoding);

        if (acceptType == Accept.ATOM) {
          atomSerializer.write(writer, container);
          writer.flush();
          writer.close();
        } else {
          final ObjectMapper mapper = Commons.getJsonMapper(version);

          mapper.writeValue(
                  writer, new JsonFeedContainer<JSONFeedImpl>(container.getContextURL(), container.getMetadataETag(),
                          new DataBinder(version).getJsonFeed(container.getObject())));
        }

        return xml.createResponse(new ByteArrayInputStream(content.toByteArray()),
                Commons.getETag(basePath, version), acceptType);
      }
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  protected abstract void setInlineCount(final Feed feed, final String count);

  /**
   * Retrieve entity with key as segment.
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
              utils.getValue().readEntity(entitySetName, entityId, Accept.ATOM);

      InputStream entity = entityInfo.getValue();

      final FITAtomDeserializer atomDeserializer = Commons.getAtomDeserializer(version);
      final AtomSerializer atomSerializer = Commons.getAtomSerializer(version);

      final Container<Entry> container = atomDeserializer.<Entry, AtomEntryImpl>read(entity, AtomEntryImpl.class);
      final Entry entry = container.getObject();

      if (keyAsSegment) {
        final Link editLink = new LinkImpl();
        editLink.setRel("edit");
        editLink.setTitle(entitySetName);
        editLink.setHref(Constants.get(version, ConstantKey.DEFAULT_SERVICE_URL) + entitySetName + "/" + entityId);

        entry.setEditLink(editLink);
      }

      if (StringUtils.isNotBlank(select)) {
        final List<String> properties = Arrays.asList(select.split(","));
        final Set<Property> toBeRemoved = new HashSet<Property>();

        for (Property property : entry.getProperties()) {
          if (!properties.contains(property.getName())) {
            toBeRemoved.add(property);
          }
        }

        entry.getProperties().removeAll(toBeRemoved);

        final Set<Link> linkToBeRemoved = new HashSet<Link>();

        for (Link link : entry.getNavigationLinks()) {
          if (!properties.contains(link.getTitle().replaceAll("@.*$", "")) && !properties.contains(link.getTitle())) {
            linkToBeRemoved.add(link);
          }
        }

        entry.getNavigationLinks().removeAll(linkToBeRemoved);
      }

      if (StringUtils.isNotBlank(expand)) {
        final List<String> links = Arrays.asList(expand.split(","));

        final Map<Link, Link> replace = new HashMap<Link, Link>();

        for (Link link : entry.getNavigationLinks()) {
          if (links.contains(link.getTitle())) {
            // expand link
            final Link rep = new LinkImpl();
            rep.setHref(link.getHref());
            rep.setRel(link.getRel());
            rep.setTitle(link.getTitle());
            rep.setType(link.getType());
            if (link.getType().equals(Constants.get(ConstantKey.ATOM_LINK_ENTRY))) {
              // inline entry
              final Entry inline = atomDeserializer.<Entry, AtomEntryImpl>read(
                      xml.expandEntity(entitySetName, entityId, link.getTitle()),
                      AtomEntryImpl.class).getObject();
              rep.setInlineEntry(inline);
            } else if (link.getType().equals(Constants.get(ConstantKey.ATOM_LINK_FEED))) {
              // inline feed
              final Feed inline = atomDeserializer.<Feed, AtomFeedImpl>read(
                      xml.expandEntity(entitySetName, entityId, link.getTitle()),
                      AtomFeedImpl.class).getObject();
              rep.setInlineFeed(inline);
            }
            replace.put(link, rep);
          }
        }

        for (Map.Entry<Link, Link> link : replace.entrySet()) {
          entry.getNavigationLinks().remove(link.getKey());
          entry.getNavigationLinks().add(link.getValue());
        }
      }

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.encoding);

      if (utils.getKey() == Accept.ATOM) {
        atomSerializer.write(writer, container);
        writer.flush();
        writer.close();
      } else {
        final ObjectMapper mapper = Commons.getJsonMapper(version);
        mapper.writeValue(
                writer, new JsonEntryContainer<JSONEntryImpl>(container.getContextURL(), container.getMetadataETag(),
                        (new DataBinder(version)).getJsonEntry((AtomEntryImpl) container.getObject())));
      }

      return xml.createResponse(new ByteArrayInputStream(content.toByteArray()),
              Commons.getETag(entityInfo.getKey(), version), utils.getKey());

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

  public Map.Entry<Accept, AbstractUtilities> getUtilities(final String accept, final String format) {
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

  private void normalizeAtomEntry(final AtomEntryImpl entry, final String entitySetName, final String entityKey) {
    final EntitySet entitySet = metadata.getEntitySet(entitySetName);
    final EntityType entityType = metadata.getEntityType(entitySet.getType());
    for (Map.Entry<String, org.apache.olingo.fit.metadata.Property> property
            : entityType.getPropertyMap().entrySet()) {
      if (entry.getProperty(property.getKey()) == null && property.getValue().isNullable()) {
        final AtomPropertyImpl prop = new AtomPropertyImpl();
        prop.setName(property.getKey());
        prop.setValue(new NullValueImpl());
        entry.getProperties().add(prop);
      }
    }

    for (Map.Entry<String, NavigationProperty> property : entityType.getNavigationPropertyMap().entrySet()) {
      boolean found = false;
      for (Link link : entry.getNavigationLinks()) {
        if (link.getTitle().equals(property.getKey())) {
          found = true;
        }
      }

      if (!found) {
        final LinkImpl link = new LinkImpl();
        link.setTitle(property.getKey());
        link.setType(property.getValue().isFeed()
                ? Constants.get(ConstantKey.ATOM_LINK_FEED) : Constants.get(ConstantKey.ATOM_LINK_ENTRY));
        link.setRel(Constants.get(ConstantKey.ATOM_LINK_REL) + property.getKey());
        link.setHref(entitySetName + "(" + entityKey + ")/" + property.getKey());
        entry.getNavigationLinks().add(link);
      }
    }
  }
}
