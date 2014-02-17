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
package com.msopentech.odatajclient.plugin;

import com.msopentech.odatajclient.engine.client.ODataClientFactory;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataV3MetadataRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.metadata.edm.v3.ComplexType;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntitySet;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityType;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EnumType;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Schema;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * POJOs generator.
 */
@Mojo(name = "pojosV3", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class V3MetadataMojo extends AbstractMetadataMojo {

    @Override
    protected V3Utility getUtility() {
        return (V3Utility) utility;
    }

    @Override
    protected String getVersion() {
        return ODataVersion.V3.name().toLowerCase();
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (new File(outputDirectory + File.separator + TOOL_DIR).exists()) {
            getLog().info("Nothing to do because " + TOOL_DIR + " directory already exists. Clean to update.");
            return;
        }

        try {
            Velocity.addProperty(Velocity.RESOURCE_LOADER, "class");
            Velocity.addProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());

            final ODataV3MetadataRequest req = ODataClientFactory.getV3().
                    getRetrieveRequestFactory().getMetadataRequest(serviceRootURL);

            final ODataRetrieveResponse<EdmV3Metadata> res = req.execute();
            final EdmV3Metadata metadata = res.getBody();

            if (metadata == null) {
                throw new IllegalStateException("Metadata not found");
            }

            for (Schema schema : metadata.getSchemas()) {
                namespaces.add(schema.getNamespace().toLowerCase());
            }

            final Set<String> complexTypeNames = new HashSet<String>();
            final File services = mkdir("META-INF/services");

            for (Schema schema : metadata.getSchemas()) {
                utility = new V3Utility(metadata, schema, basePackage);

                // write package-info for the base package
                final String schemaPath = utility.getNamespace().toLowerCase().replace('.', File.separatorChar);
                final File base = mkPkgDir(schemaPath);
                final String pkg = basePackage + "." + utility.getNamespace().toLowerCase();
                parseObj(base, pkg, "package-info", "package-info.java");

                // write package-info for types package
                final File typesBaseDir = mkPkgDir(schemaPath + "/types");
                final String typesPkg = pkg + ".types";
                parseObj(typesBaseDir, typesPkg, "package-info", "package-info.java");

                final Map<String, Object> objs = new HashMap<String, Object>();

                // write types into types package
                for (EnumType enumType : schema.getEnumTypes()) {
                    final String className = utility.capitalize(enumType.getName());
                    complexTypeNames.add(typesPkg + "." + className);
                    objs.clear();
                    objs.put("enumType", enumType);
                    parseObj(typesBaseDir, typesPkg, "enumType", className + ".java", objs);
                }

                for (ComplexType complex : schema.getComplexTypes()) {
                    final String className = utility.capitalize(complex.getName());
                    complexTypeNames.add(typesPkg + "." + className);
                    objs.clear();
                    objs.put("complexType", complex);
                    parseObj(typesBaseDir, typesPkg, "complexType", className + ".java", objs);
                }

                for (EntityType entity : schema.getEntityTypes()) {
                    objs.clear();
                    objs.put("entityType", entity);

                    final Map<String, String> keys;

                    EntityType baseType = null;
                    if (entity.getBaseType() == null) {
                        keys = getUtility().getEntityKeyType(entity);
                    } else {
                        baseType = schema.getEntityType(utility.getNameFromNS(entity.getBaseType()));
                        objs.put("baseType", getUtility().getJavaType(entity.getBaseType()));
                        while (baseType.getBaseType() != null) {
                            baseType = schema.getEntityType(utility.getNameFromNS(baseType.getBaseType()));
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

                    parseObj(typesBaseDir, typesPkg, "entityType",
                            utility.capitalize(entity.getName()) + ".java", objs);
                    parseObj(typesBaseDir, typesPkg, "entityCollection",
                            utility.capitalize(entity.getName()) + "Collection.java", objs);
                }

                // write container and top entity sets into the base package
                for (EntityContainer container : schema.getEntityContainers()) {
                    objs.clear();
                    objs.put("container", container);
                    parseObj(base, pkg, "container",
                            utility.capitalize(container.getName()) + ".java", objs);

                    for (EntitySet entitySet : container.getEntitySets()) {
                        objs.clear();
                        objs.put("entitySet", entitySet);
                        parseObj(base, pkg, "entitySet",
                                utility.capitalize(entitySet.getName()) + ".java", objs);
                    }
                }

                parseObj(services, true, null, "services", "com.msopentech.odatajclient.proxy.api.AbstractComplexType",
                        Collections.singletonMap("services", (Object) complexTypeNames));
            }
        } catch (Throwable t) {
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            t.printStackTrace(printWriter);
            getLog().error(stringWriter.toString());

            throw (t instanceof MojoExecutionException)
                    ? (MojoExecutionException) t
                    : new MojoExecutionException("While executin mojo", t);
        }
    }
}
