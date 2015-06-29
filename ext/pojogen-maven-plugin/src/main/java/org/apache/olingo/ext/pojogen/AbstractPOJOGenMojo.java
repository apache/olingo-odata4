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
package org.apache.olingo.ext.pojogen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.codehaus.plexus.util.FileUtils;

public abstract class AbstractPOJOGenMojo extends AbstractMojo {

  /**
   * Generated files base root.
   */
  @Parameter(property = "outputDirectory", required = true)
  protected String outputDirectory;

  /**
   * OData service root URL.
   */
  @Parameter(property = "serviceRootURL", required = false)
  protected String serviceRootURL;

  /**
   * Local file from which Edm information can be loaded.
   */
  @Parameter(property = "localEdm", required = false)
  protected String localEdm;

  /**
   * Base package.
   */
  @Parameter(property = "basePackage", required = false)
  protected String basePackage;

  protected final Set<String> namespaces = new HashSet<String>();

  protected static String TOOL_DIR = "ojc-plugin";

  protected AbstractUtility utility;

  protected abstract String getVersion();

  protected File mkdir(final String path) {
    final File dir = new File(outputDirectory + File.separator + TOOL_DIR + File.separator + path);

    if (dir.exists()) {
      if (!dir.isDirectory()) {
        throw new IllegalArgumentException("Invalid path '" + path + "': it is not a directory");
      }
    } else {
      dir.mkdirs();
    }

    return dir;
  }

  protected File mkPkgDir(final String path) {
    return StringUtils.isBlank(basePackage)
        ? mkdir(path)
        : mkdir(basePackage.replace('.', File.separatorChar) + File.separator + path);
  }

  protected void writeFile(final String name, final File path, final VelocityContext ctx, final Template template,
      final boolean append) throws MojoExecutionException {

    if (!path.exists()) {
      throw new IllegalArgumentException("Invalid base path '" + path.getAbsolutePath() + "'");
    }

    FileWriter writer = null;
    try {
      final File toBeWritten = new File(path, name);
      if (!append && toBeWritten.exists()) {
        throw new IllegalStateException("File '" + toBeWritten.getAbsolutePath() + "' already exists");
      }
      writer = new FileWriter(toBeWritten, append);
      template.merge(ctx, writer);
    } catch (IOException e) {
      throw new MojoExecutionException("Error creating file '" + name + "'", e);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

  protected VelocityContext newContext() {
    final VelocityContext ctx = new VelocityContext();

    ctx.put("utility", getUtility());
    ctx.put("basePackage", basePackage);
    ctx.put("schemaName", getUtility().getSchemaName());
    ctx.put("namespace", getUtility().getNamespace());
    ctx.put("namespaces", namespaces);
    ctx.put("odataVersion", getVersion());

    return ctx;
  }

  protected void parseObj(final File base, final String pkg, final String name, final String out)
      throws MojoExecutionException {

    parseObj(base, false, pkg, name, out, Collections.<String, Object> emptyMap());
  }

  protected void parseObj(
      final File base,
      final String pkg,
      final String name,
      final String out,
      final Map<String, Object> objs)
      throws MojoExecutionException {

    parseObj(base, false, pkg, name, out, objs);
  }

  protected void parseObj(
      final File base,
      final boolean append,
      final String pkg,
      final String name,
      final String out,
      final Map<String, Object> objs)
      throws MojoExecutionException {

    final VelocityContext ctx = newContext();
    ctx.put("package", pkg);

    if (objs != null) {
      for (Map.Entry<String, Object> obj : objs.entrySet()) {
        if (StringUtils.isNotBlank(obj.getKey()) && obj.getValue() != null) {
          ctx.put(obj.getKey(), obj.getValue());
        }
      }
    }

    final Template template = Velocity.getTemplate(name + ".vm");
    writeFile(out, base, ctx, template, append);
  }

  protected abstract void createUtility(Edm edm, EdmSchema schema, String basePackage);

  protected abstract AbstractUtility getUtility();

  protected abstract ODataClient getClient();

  private Triple<XMLMetadata, String, Edm> getMetadata() throws FileNotFoundException {
    if (StringUtils.isEmpty(serviceRootURL) && StringUtils.isEmpty(localEdm)) {
      throw new IllegalArgumentException("Must provide either serviceRootURL or localEdm");
    }
    if (StringUtils.isNotEmpty(serviceRootURL) && StringUtils.isNotEmpty(localEdm)) {
      throw new IllegalArgumentException("Must provide either serviceRootURL or localEdm, not both");
    }

    XMLMetadata metadata = null;
    String metadataETag = null;
    Edm edm = null;
    if (StringUtils.isNotEmpty(serviceRootURL)) {
      final EdmMetadataRequest req = getClient().getRetrieveRequestFactory().getMetadataRequest(serviceRootURL);
      metadata = req.getXMLMetadata();
      final ODataRetrieveResponse<Edm> res = req.execute();
      metadataETag = res.getETag();
      edm = res.getBody();
    } else if (StringUtils.isNotEmpty(localEdm)) {
      final FileInputStream fis = new FileInputStream(FileUtils.getFile(localEdm));
      try {
        metadata = getClient().getDeserializer(ContentType.APPLICATION_XML).toMetadata(fis);
        edm = getClient().getReader().readMetadata(metadata.getSchemaByNsOrAlias());
      } finally {
        IOUtils.closeQuietly(fis);
      }
    }

    if (metadata == null || edm == null) {
      throw new IllegalStateException("Metadata not found");
    }
    return new ImmutableTriple<XMLMetadata, String, Edm>(metadata, metadataETag, edm);
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (new File(outputDirectory + File.separator + TOOL_DIR).exists()) {
      getLog().info("Nothing to do because " + TOOL_DIR + " directory already exists. Clean to update.");
      return;
    }

    Velocity.addProperty(Velocity.RESOURCE_LOADER, "class");
    Velocity.addProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());

    try {
      final Triple<XMLMetadata, String, Edm> metadata = getMetadata();

      for (EdmSchema schema : metadata.getRight().getSchemas()) {
        namespaces.add(schema.getNamespace().toLowerCase());
      }

      final Map<String, String> entityTypeNames = new HashMap<String, String>();
      final Map<String, String> complexTypeNames = new HashMap<String, String>();
      final Map<String, String> enumTypeNames = new HashMap<String, String>();
      final Map<String, String> termNames = new HashMap<String, String>();

      final Map<String, Object> objs = new HashMap<String, Object>();

      for (EdmSchema schema : metadata.getRight().getSchemas()) {
        createUtility(metadata.getRight(), schema, basePackage);

        // write package-info for the base package
        final String schemaPath = utility.getNamespace().toLowerCase().replace('.', File.separatorChar);
        final File base = mkPkgDir(schemaPath);
        final String pkg = StringUtils.isBlank(basePackage)
                ? utility.getNamespace().toLowerCase()
                : basePackage + "." + utility.getNamespace().toLowerCase();
        parseObj(base, pkg, "package-info", "package-info.java");

        // write package-info for types package
        final File typesBaseDir = mkPkgDir(schemaPath + "/types");
        final String typesPkg = pkg + ".types";
        parseObj(typesBaseDir, typesPkg, "package-info", "package-info.java");

        for (EdmTerm term : schema.getTerms()) {
          final String className = utility.capitalize(term.getName());
          termNames.put(term.getFullQualifiedName().toString(), typesPkg + "." + className);
          objs.clear();
          objs.put("term", term);
          parseObj(typesBaseDir, typesPkg, "term", className + ".java", objs);
        }

        for (EdmEnumType enumType : schema.getEnumTypes()) {
          final String className = utility.capitalize(enumType.getName());
          enumTypeNames.put(enumType.getFullQualifiedName().toString(), typesPkg + "." + className);
          objs.clear();
          objs.put("enumType", enumType);
          parseObj(typesBaseDir, typesPkg, "enumType", className + ".java", objs);
        }

        final List<EdmComplexType> complexes = new ArrayList<EdmComplexType>();

        for (EdmComplexType complex : schema.getComplexTypes()) {
          complexes.add(complex);
          final String className = utility.capitalize(complex.getName());
          complexTypeNames.put(complex.getFullQualifiedName().toString(), typesPkg + "." + className);
          objs.clear();
          objs.put("complexType", complex);
          
          parseObj(typesBaseDir, typesPkg, 
                  "complexType", className + ".java", objs);
          parseObj(typesBaseDir, typesPkg, 
                  "complexTypeComposableInvoker", className + "ComposableInvoker.java", objs);
          parseObj(typesBaseDir, typesPkg, 
                  "complexCollection", className + "Collection.java", objs);
          parseObj(typesBaseDir, typesPkg, 
                  "complexCollectionComposableInvoker", className + "CollectionComposableInvoker.java", objs);
        }

        for (EdmEntityType entity : schema.getEntityTypes()) {
          final String className = utility.capitalize(entity.getName());
          entityTypeNames.put(entity.getFullQualifiedName().toString(), typesPkg + "." + className);

          objs.clear();
          objs.put("entityType", entity);

          final Map<String, String> keys;

          EdmEntityType baseType = null;
          if (entity.getBaseType() == null) {
            keys = getUtility().getEntityKeyType(entity);
          } else {
            baseType = entity.getBaseType();
            objs.put("baseType", getUtility().getJavaType(baseType.getFullQualifiedName().toString()));
            while (baseType.getBaseType() != null) {
              baseType = baseType.getBaseType();
            }
            keys = getUtility().getEntityKeyType(baseType);
          }

          if (keys.size() > 1) {
            // create compound key class
            final String keyClassName = utility.capitalize(baseType == null
                    ? entity.getName()
                    : baseType.getName()) + "Key";
            objs.put("keyRef", keyClassName);

            if (entity.getBaseType() == null) {
              objs.put("keys", keys);
              parseObj(typesBaseDir, typesPkg, "entityTypeKey", keyClassName + ".java", objs);
            }
          }

          parseObj(typesBaseDir, typesPkg, 
                  "entityType", className + ".java", objs);
          parseObj(typesBaseDir, typesPkg, 
                  "entityComposableInvoker", className + "ComposableInvoker.java", objs);
          parseObj(typesBaseDir, typesPkg, 
                  "entityCollection", className + "Collection.java", objs);
          parseObj(typesBaseDir, typesPkg, 
                  "entityCollectionComposableInvoker", className + "CollectionComposableInvoker.java", objs);
        }

        // write container and top entity sets into the base package
        EdmEntityContainer container = schema.getEntityContainer();
        if(container != null){
          objs.clear();
          objs.put("container", container);
          objs.put("namespace", schema.getNamespace());
          objs.put("complexes", complexes);

          parseObj(base, pkg, "container", utility.capitalize(container.getName()) + ".java", objs);

          for (EdmEntitySet entitySet : container.getEntitySets()) {
            objs.clear();
            objs.put("entitySet", entitySet);
            objs.put("container", container);
            parseObj(base, pkg, "entitySet", utility.capitalize(entitySet.getName()) + ".java", objs);
          }
        }
      }

      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      final GZIPOutputStream gzos = new GZIPOutputStream(baos);
      final ObjectOutputStream oos = new ObjectOutputStream(gzos);
      try {
        oos.writeObject(metadata.getLeft());
      } finally {
        oos.close();
        gzos.close();
        baos.close();
      }

      objs.clear();
      objs.put("metadata", new String(Base64.encodeBase64(baos.toByteArray()), "UTF-8"));
      objs.put("metadataETag", metadata.getMiddle());
      objs.put("entityTypes", entityTypeNames);
      objs.put("complexTypes", complexTypeNames);
      objs.put("enumTypes", enumTypeNames);
      objs.put("terms", termNames);
      final String actualBP = StringUtils.isBlank(basePackage)
              ? StringUtils.EMPTY
              : basePackage;
      parseObj(mkdir(actualBP.replace('.', File.separatorChar)), actualBP, "service", "Service.java", objs);
    } catch (Exception t) {
      getLog().error(t);

      throw (t instanceof MojoExecutionException)
              ? (MojoExecutionException) t
              : new MojoExecutionException("While executin mojo", t);
    }
  }
}
