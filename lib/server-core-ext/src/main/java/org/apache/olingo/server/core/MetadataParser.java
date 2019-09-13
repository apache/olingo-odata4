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
package org.apache.olingo.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlBindingTarget;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDelete;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDeleteAction;
import org.apache.olingo.commons.api.edm.provider.CsdlOperation;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlReferentialConstraint;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlAnnotationPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlApply;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCast;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCollection;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlIf;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlIsOf;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLabeledElement;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLabeledElementReference;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlNavigationPropertyPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlNull;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlRecord;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlUrlRef;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.commons.api.edmx.EdmxReferenceIncludeAnnotation;
import org.apache.olingo.server.api.ServiceMetadata;

/**
 * This class can convert a CSDL document into EDMProvider object
 */
public class MetadataParser {
  private boolean parseAnnotations = false;
  private static final String XML_LINK_NS = "http://www.w3.org/1999/xlink";
  private ReferenceResolver referenceResolver = new DefaultReferenceResolver();
  private boolean useLocalCoreVocabularies = true;
  private boolean implicitlyLoadCoreVocabularies = false;
  private boolean recursivelyLoadReferences = false;
  private Map<String, SchemaBasedEdmProvider> globalReferenceMap = new HashMap<>();
  
  /**
   * Avoid reading the annotations in the $metadata 
   * @param parse
   * @return
   */
  public MetadataParser parseAnnotations(boolean parse) {
    this.parseAnnotations = parse;
    return this;
  }

  /**
   * Externalize the reference loading, such that they can be loaded from local caches
   * @param resolver
   * @return
   */
  public MetadataParser referenceResolver(ReferenceResolver resolver) {
    this.referenceResolver = resolver;
    return this;
  }
  
  /**
   * Load the core libraries from local classpath
   * @param load true for yes; false otherwise
   * @return
   */
  public MetadataParser useLocalCoreVocabularies(boolean load) {
    this.useLocalCoreVocabularies = load;
    return this;
  }
  
  /**
   * Load the core libraries from local classpath
   * @param load true for yes; false otherwise
   * @return
   */
  public MetadataParser recursivelyLoadReferences(boolean load) {
    this.recursivelyLoadReferences = load;
    return this;
  }  
  
  /**
   * Load the core vocabularies, irrespective of if they are defined in the $metadata
   * @param load
   * @return
   */
  public MetadataParser implicitlyLoadCoreVocabularies(boolean load) {
    this.implicitlyLoadCoreVocabularies = load;
    return this;
  }
  
  public ServiceMetadata buildServiceMetadata(Reader csdl) throws XMLStreamException {
    SchemaBasedEdmProvider provider = buildEdmProvider(csdl, this.referenceResolver,
            this.implicitlyLoadCoreVocabularies, this.useLocalCoreVocabularies, true, null);
    return new ServiceMetadataImpl(provider, provider.getReferences(), null);
  }

  public SchemaBasedEdmProvider buildEdmProvider(Reader csdl) throws XMLStreamException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader = xmlInputFactory.createXMLEventReader(csdl);    
    return buildEdmProvider(reader, this.referenceResolver, this.implicitlyLoadCoreVocabularies,
            this.useLocalCoreVocabularies, true, null);
  }
  
  public SchemaBasedEdmProvider addToEdmProvider(SchemaBasedEdmProvider existing, Reader csdl)
      throws XMLStreamException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader = xmlInputFactory.createXMLEventReader(csdl);
    return addToEdmProvider(existing, reader, this.referenceResolver, this.implicitlyLoadCoreVocabularies,
        this.useLocalCoreVocabularies, true, null);
  }
  
  protected SchemaBasedEdmProvider buildEdmProvider(Reader csdl, ReferenceResolver resolver,
                                                    boolean loadCore, boolean useLocal,
                                                    boolean loadReferenceSchemas, String namespace)
          throws XMLStreamException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader = xmlInputFactory.createXMLEventReader(csdl);
    return buildEdmProvider(reader, resolver, loadCore, useLocal, loadReferenceSchemas, namespace);
  }

  protected SchemaBasedEdmProvider buildEdmProvider(InputStream csdl, ReferenceResolver resolver,
                                                    boolean loadCore, boolean useLocal,
                                                    boolean loadReferenceSchemas, String namespace)
          throws XMLStreamException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader = xmlInputFactory.createXMLEventReader(csdl);
    return buildEdmProvider(reader, resolver, loadCore, useLocal, loadReferenceSchemas, namespace);
  } 

  protected SchemaBasedEdmProvider buildEdmProvider(XMLEventReader reader, ReferenceResolver resolver, boolean loadCore,
      boolean useLocal, boolean loadReferenceSchemas, String namespace) throws XMLStreamException {
    SchemaBasedEdmProvider provider = new SchemaBasedEdmProvider();
    return addToEdmProvider(provider, reader, resolver, loadCore, useLocal, loadReferenceSchemas, namespace);
  }
  
  protected SchemaBasedEdmProvider addToEdmProvider(SchemaBasedEdmProvider provider, XMLEventReader reader,
      ReferenceResolver resolver, boolean loadCore, boolean useLocal, boolean loadReferenceSchemas, String namespace)
      throws XMLStreamException {
    
    final StringBuilder xmlBase = new StringBuilder();
    
    new ElementReader<SchemaBasedEdmProvider>() {
      @Override
      void build(XMLEventReader reader, StartElement element, SchemaBasedEdmProvider provider,
          String name) throws XMLStreamException {
        if (attrNS(element, XML_LINK_NS, "base") != null) {
          xmlBase.append(attrNS(element, XML_LINK_NS, "base"));
        }
        String version = attr(element, "Version");
        if ("4.0".equals(version)) {
          readDataServicesAndReference(reader, element, provider);
        } else {
          throw new XMLStreamException("Currently only V4 is supported.");
        }
      }
    }.read(reader, null, provider, "Edmx");
    
    // make sure there is nothing left to read, due to parser error
    if(reader.hasNext()) {
      XMLEvent event = reader.peek();
      throw new XMLStreamException(
          "Failed to read complete metadata file. Failed at "
              + (event.isStartElement() ? 
                  event.asStartElement().getName().getLocalPart() : 
                  event.asEndElement().getName().getLocalPart()));
    }
    
    //load core vocabularies even though they are not defined in the references
    if (loadCore) {
      loadCoreVocabulary(provider, "Org.OData.Core.V1");
      loadCoreVocabulary(provider, "Org.OData.Capabilities.V1");
      loadCoreVocabulary(provider, "Org.OData.Measures.V1");
    }

    if (namespace != null && !namespace.equals("") && !globalReferenceMap.containsKey(namespace)) {
      globalReferenceMap.put(namespace, provider);
    }

    // load all the reference schemas
    if (resolver != null && loadReferenceSchemas) {
      loadReferencesSchemas(provider, xmlBase.length() == 0 ? null
          : fixXmlBase(xmlBase.toString()), resolver, loadCore, useLocal);
    }
    return provider;
  }  
  
  private void loadReferencesSchemas(SchemaBasedEdmProvider provider,
      String xmlBase, ReferenceResolver resolver, boolean loadCore,
      boolean useLocal) {    

    for (EdmxReference reference:provider.getReferences()) {
      try {
        SchemaBasedEdmProvider refProvider = null;

        for (EdmxReferenceInclude include : reference.getIncludes()) {

          // check if the schema is already loaded before in current provider.
          if (provider.getSchemaDirectly(include.getNamespace()) != null) {
            continue;
          }
          
          if (isCoreVocabulary(include.getNamespace()) && useLocal) {
            loadCoreVocabulary(provider, include.getNamespace());
            continue;
          }

          // check if the schema is already loaded before in parent providers
          refProvider = this.globalReferenceMap.get(include.getNamespace());

          if (refProvider == null) {
            InputStream is = this.referenceResolver.resolveReference(reference.getUri(), xmlBase);
            if (is == null) {
              throw new EdmException("Failed to load Reference "+reference.getUri()+" loading failed");
            } else {
              // do not implicitly load core vocabularies any more. But if the
              // references loading the core vocabularies try to use local if we can
              refProvider = buildEdmProvider(is, resolver, false, useLocal,
                      this.recursivelyLoadReferences, include.getNamespace());
            }
          }
          
          if (refProvider != null) {
            CsdlSchema refSchema = refProvider.getSchema(include.getNamespace(), false);
            provider.addReferenceSchema(include.getNamespace(), refProvider);
            if (include.getAlias() != null) {
              refSchema.setAlias(include.getAlias());
              provider.addReferenceSchema(include.getAlias(), refProvider);
            }
          }
        }
      } catch (XMLStreamException e) {
        throw new EdmException("Failed to load Reference "+reference.getUri()+" parsing failed");
      }
    }
  }
  
  public void loadCoreVocabulary(SchemaBasedEdmProvider provider,
      String namespace) throws XMLStreamException {
    if("Org.OData.Core.V1".equalsIgnoreCase(namespace)) {
      loadLocalVocabularySchema(provider, "Org.OData.Core.V1", "Org.OData.Core.V1.xml");
    } else if ("Org.OData.Capabilities.V1".equalsIgnoreCase(namespace)) {
      loadLocalVocabularySchema(provider, "Org.OData.Capabilities.V1", "Org.OData.Capabilities.V1.xml");
    } else if ("Org.OData.Measures.V1".equalsIgnoreCase(namespace)) {
      loadLocalVocabularySchema(provider, "Org.OData.Measures.V1", "Org.OData.Measures.V1.xml");
    } else {
    	throw new XMLStreamException("Unknown namespace to load vocabulary");
    }
  }

  private boolean isCoreVocabulary(String namespace) {
    if("Org.OData.Core.V1".equalsIgnoreCase(namespace) || 
        "Org.OData.Capabilities.V1".equalsIgnoreCase(namespace) || 
        "Org.OData.Measures.V1".equalsIgnoreCase(namespace)) {
      return true;
    }
    return false;
  }

  private String fixXmlBase(String base) {
    if (base.endsWith("/")) {
      return base;
    } 
    return base+"/";
  }  
  
  private void loadLocalVocabularySchema(SchemaBasedEdmProvider provider, String namespace,
      String resource) throws XMLStreamException {
    CsdlSchema schema = provider.getVocabularySchema(namespace);
    if (schema == null) {
      InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
      if (is != null) {
        SchemaBasedEdmProvider childProvider = buildEdmProvider(is, null, false, false, true, "");
        provider.addVocabularySchema(namespace, childProvider);
      } else {
        throw new XMLStreamException("failed to load "+resource+" core vocabulary");
      }
    }
  }  
  
  private void readDataServicesAndReference(XMLEventReader reader,
      StartElement element, SchemaBasedEdmProvider provider)
      throws XMLStreamException {
    new ElementReader<SchemaBasedEdmProvider>() {
      @Override
      void build(XMLEventReader reader, StartElement element, SchemaBasedEdmProvider provider,
          String name) throws XMLStreamException {
        if ("DataServices".equals(name)) {
          readSchema(reader, element, provider);
        } else if ("Reference".equals(name)) {
          readReference(reader, element, provider, "Reference");
        }
      }
    }.read(reader, element, provider, "DataServices", "Reference");
  }

  private void readReference(XMLEventReader reader, StartElement element,
      final SchemaBasedEdmProvider provider, String name) throws XMLStreamException {
    EdmxReference reference;
    try {
      String uri = attr(element, "Uri");
      reference = new EdmxReference(new URI(uri));
    } catch (URISyntaxException e) {
      throw new XMLStreamException(e);
    }
    new ElementReader<EdmxReference>() {
      @Override
      void build(XMLEventReader reader, StartElement element,
          EdmxReference reference, String name) throws XMLStreamException {
        if ("Include".equals(name)) {
          EdmxReferenceInclude include = new EdmxReferenceInclude(attr(element, "Namespace"),
              attr(element, "Alias"));
          reference.addInclude(include);
        } else if ("IncludeAnnotations".equals(name)) {
          EdmxReferenceIncludeAnnotation annotation = new EdmxReferenceIncludeAnnotation(
              attr(element, "TermNamespace"));
          annotation.setTargetNamespace(attr(element, "TargetNamespace"));
          annotation.setQualifier(attr(element, "Qualifier"));
          reference.addIncludeAnnotation(annotation);
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, reference);
        }
      }
    }.read(reader, element, reference, "Include", "IncludeAnnotations", "Annotation");
    provider.addReference(reference);
  }
  
  private void readSchema(XMLEventReader reader, StartElement element,
      SchemaBasedEdmProvider provider) throws XMLStreamException {

    new ElementReader<SchemaBasedEdmProvider>() {
      @Override
      void build(XMLEventReader reader, StartElement element, SchemaBasedEdmProvider provider, String name)
          throws XMLStreamException {
        CsdlSchema schema = new CsdlSchema();
        schema.setComplexTypes(new ArrayList<CsdlComplexType>());
        schema.setActions(new ArrayList<CsdlAction>());
        schema.setEntityTypes(new ArrayList<CsdlEntityType>());
        schema.setEnumTypes(new ArrayList<CsdlEnumType>());
        schema.setFunctions(new ArrayList<CsdlFunction>());
        schema.setTerms(new ArrayList<CsdlTerm>());
        schema.setTypeDefinitions(new ArrayList<CsdlTypeDefinition>());        
        schema.setNamespace(attr(element, "Namespace"));
        schema.setAlias(attr(element, "Alias"));
        readSchemaContents(reader, schema);
        provider.addSchema(schema);
      }
    }.read(reader, element, provider, "Schema");
  }

  private void readSchemaContents(XMLEventReader reader, CsdlSchema schema) throws XMLStreamException {
    new ElementReader<CsdlSchema>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlSchema schema, String name)
          throws XMLStreamException {
        if ("Action".equals(name)) {
          readAction(reader, element, schema);
        } else if ("Annotations".equals(name)) {
          readAnnotationGroup(reader, element, schema);
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, schema);
        } else if ("ComplexType".equals(name)) {
          readComplexType(reader, element, schema);
        } else if ("EntityContainer".equals(name)) {
          readEntityContainer(reader, element, schema);
        } else if ("EntityType".equals(name)) {
          readEntityType(reader, element, schema);
        } else if ("EnumType".equals(name)) {
          readEnumType(reader, element, schema);
        } else if ("Function".equals(name)) {
          readFunction(reader, element, schema);
        } else if ("Term".equals(name)) {
          schema.getTerms().add(readTerm(reader, element));
        } else if ("TypeDefinition".equals(name)) {
          schema.getTypeDefinitions().add(readTypeDefinition(reader, element));
        }
      }
    }.read(reader, null, schema, "Action", "Annotations", "Annotation", "ComplexType",
        "EntityContainer", "EntityType", "EnumType", "Function", "Term", "TypeDefinition");
  }

  private void readAction(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {

    CsdlAction action = new CsdlAction();
    action.setParameters(new ArrayList<CsdlParameter>());
    action.setName(attr(element, "Name"));
    action.setBound(Boolean.parseBoolean(attr(element, "IsBound")));
    String entitySetPath = attr(element, "EntitySetPath");
    if (entitySetPath != null) {
      // TODO: need to parse into binding and path.
      action.setEntitySetPath(entitySetPath);
    }
    readOperationParameters(reader, action);
    schema.getActions().add(action);
  }

  private FullQualifiedName readType(StartElement element) {
    String type = attr(element, "Type");
    if (type != null && type.startsWith("Collection(") && type.endsWith(")")) {
      return new FullQualifiedName(type.substring(11, type.length() - 1));
    }
    return new FullQualifiedName(type);
  }

  private boolean isCollectionType(StartElement element) {
    String type = attr(element, "Type");
    if (type != null && type.startsWith("Collection(") && type.endsWith(")")) {
      return true;
    }
    return false;
  }

  private void readReturnType(XMLEventReader reader, StartElement element,
      CsdlOperation operation) throws XMLStreamException {
    CsdlReturnType returnType = new CsdlReturnType();
    returnType.setType(readType(element));
    returnType.setCollection(isCollectionType(element));
    returnType.setNullable(Boolean.parseBoolean(attr(element, "Nullable")));

    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      returnType.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      returnType.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      returnType.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      returnType.setSrid(SRID.valueOf(srid));
    }
    peekAnnotations(reader, element.getName().getLocalPart(), returnType);
    operation.setReturnType(returnType);
  }

  private void readParameter(XMLEventReader reader, StartElement element,
      CsdlOperation operation) throws XMLStreamException {
    CsdlParameter parameter = new CsdlParameter();
    parameter.setName(attr(element, "Name"));
    parameter.setType(readType(element));
    parameter.setCollection(isCollectionType(element));
    parameter.setNullable(Boolean.parseBoolean(attr(element, "Nullable")));

    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      parameter.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      parameter.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      parameter.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      parameter.setSrid(SRID.valueOf(srid));
    }
    peekAnnotations(reader, element.getName().getLocalPart(), parameter);
    operation.getParameters().add(parameter);
  }

  private CsdlTypeDefinition readTypeDefinition(XMLEventReader reader,
      StartElement element) throws XMLStreamException {
    CsdlTypeDefinition td = new CsdlTypeDefinition();
    td.setName(attr(element, "Name"));
    td.setUnderlyingType(new FullQualifiedName(attr(element, "UnderlyingType")));
    if (attr(element, "Unicode") != null) {
      td.setUnicode(Boolean.parseBoolean(attr(element, "Unicode")));
    }

    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      td.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      td.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      td.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      td.setSrid(SRID.valueOf(srid));
    }
    peekAnnotations(reader, element.getName().getLocalPart(), td);
    return td;
  }

  private CsdlTerm readTerm(XMLEventReader reader, StartElement element) throws XMLStreamException {
    CsdlTerm term = new CsdlTerm();
    term.setName(attr(element, "Name"));
    term.setType(attr(element, "Type"));
    
    if (attr(element, "BaseTerm") != null) {
      term.setBaseTerm(attr(element, "BaseTerm"));
    }
    if (attr(element, "DefaultValue") != null) {
      term.setDefaultValue(attr(element, "DefaultValue"));
    }
    if (attr(element, "AppliesTo") != null) {
      String[] appliesTo = attr(element, "AppliesTo").split("\\s+");
      term.setAppliesTo(Arrays.asList(appliesTo));
    }
    term.setNullable(Boolean.parseBoolean(attr(element, "Nullable")));
    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      term.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      term.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      term.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      term.setSrid(SRID.valueOf(srid));
    }
    peekAnnotations(reader, "Term", term);
    return term;
  }

  private void readAnnotationGroup(XMLEventReader reader, StartElement element,
      CsdlSchema schema) throws XMLStreamException {
    final CsdlAnnotations annotations = new CsdlAnnotations();
    annotations.setTarget(attr(element, "Target"));
    annotations.setQualifier(attr(element, "Qualifier"));
    peekAnnotations(reader, element.getName().getLocalPart(), annotations);
    schema.getAnnotationGroups().add(annotations);
  }

  private void peekAnnotations(XMLEventReader reader, String endName,
      CsdlAnnotatable edmObject) throws XMLStreamException {
    if(!parseAnnotations) {
      return;
    }
    while (reader.hasNext()) {
      XMLEvent event = reader.peek();

      if (!event.isStartElement() && !event.isEndElement()) {
        reader.nextEvent();
        continue;
      }
      
      if (event.isStartElement()) {
        StartElement element = event.asStartElement();
        if ("Annotation".equals(element.getName().getLocalPart())) {
          reader.nextEvent();
          readAnnotations(reader, element, edmObject);
        }
      }
      
      if (event.isEndElement()) {
        EndElement element = event.asEndElement();
        if ("Annotation".equals(element.getName().getLocalPart())) {
          reader.nextEvent();
        }
        
        if (element.getName().getLocalPart().equals(endName)) {
          return;
        }
      }
    }
  }
  
  private void readAnnotations(XMLEventReader reader, StartElement element,
      CsdlAnnotatable edmObject) throws XMLStreamException {
    if (!parseAnnotations) {
      return;
    }
    final CsdlAnnotation annotation = new CsdlAnnotation();
    annotation.setTerm(attr(element, "Term"));
    for (ConstantExpressionType type:ConstantExpressionType.values()) {
      if (attr(element, type.name()) != null) {
        annotation.setExpression(new CsdlConstantExpression(
            type, attr(element, type.name())));
      }        
    }
    readExpressions(reader, element, annotation);
    edmObject.getAnnotations().add(annotation);
  } 

  private <T> void write(T t, CsdlExpression expr) throws XMLStreamException {
    if(t instanceof CsdlAnnotation) {
      ((CsdlAnnotation)t).setExpression(expr);
    } else if (t instanceof CsdlUrlRef) {
      ((CsdlUrlRef)t).setValue(expr);
    } else if (t instanceof CsdlCast) {
      ((CsdlCast)t).setValue(expr);
    } else if (t instanceof CsdlLabeledElement) {
      ((CsdlLabeledElement)t).setValue(expr);
    } else if (t instanceof CsdlIsOf) {
      ((CsdlIsOf)t).setValue(expr);
    } else if (t instanceof CsdlCollection) {
      ((CsdlCollection)t).getItems().add(((CsdlCollection)t).getItems().size(), expr);
    } else if (t instanceof CsdlApply) {
      ((CsdlApply)t).getParameters().add(expr);
    } else if (t instanceof CsdlIf) {
      if (((CsdlIf)t).getGuard() == null) {
        ((CsdlIf)t).setGuard(expr);
      } else if (((CsdlIf)t).getThen() == null) {
        ((CsdlIf)t).setThen(expr);
      } else {
        ((CsdlIf)t).setElse(expr);
      }
    } else if (t instanceof CsdlPropertyValue) {
      ((CsdlPropertyValue)t).setValue(expr);
    } else {
      throw new XMLStreamException("Unknown expression parent in Annoatation");
    }
  }
  
  private <T> void readExpressions(XMLEventReader reader,
      StartElement element, T target)
      throws XMLStreamException {
    new ElementReader<T>() {
      @Override
      void build(XMLEventReader reader, StartElement element, T target, String name)
          throws XMLStreamException {
        
        // element based expressions
        if (!"Annotation".equals(name)) {
          // attribute based expressions.
          readAttributeExpressions(element, target);        
          
          for (ConstantExpressionType type:ConstantExpressionType.values()) {
            if (name.equals(type.name()) && reader.peek().isCharacters()) {
              CsdlExpression expr = new CsdlConstantExpression(type, elementValue(reader, element));
              write(target, expr);
            }        
          }
        }
        
        if ("Collection".equals(name)) {
          CsdlCollection expr = new CsdlCollection();
          readExpressions(reader, element, expr);
          write(target, expr);
        } else if ("AnnotationPath".equals(name)) {
          write(target, new CsdlAnnotationPath().setValue(elementValue(reader, element)));
        } else if ("NavigationPropertyPath".equals(name)) {
          write(target, new CsdlNavigationPropertyPath()
              .setValue(elementValue(reader, element)));
        } else if ("Path".equals(name)) {
          write(target, new CsdlPath().setValue(elementValue(reader, element)));
        } else if ("PropertyPath".equals(name)) {
          write(target, new CsdlPropertyPath().setValue(elementValue(reader, element)));
        } else if ("UrlRef".equals(name)) {
          CsdlUrlRef expr = new CsdlUrlRef();
          readExpressions(reader, element, expr);
          write(target, expr);
        } else if ("Apply".equals(name)) {
          CsdlApply expr = new CsdlApply();
          expr.setFunction(attr(element, "Function"));
          readExpressions(reader, element, expr);
          write(target, expr);
        } else if ("Cast".equals(name)) {
          CsdlCast expr = new CsdlCast();
          expr.setType(attr(element, "Type"));
          readExpressions(reader, element, expr);
          write(target, expr);
        } else if ("If".equals(name)) {
          CsdlIf expr = new CsdlIf();
          readExpressions(reader, element, expr);
          write(target, expr);
        } else if ("IsOf".equals(name)) {
          CsdlIsOf expr = new CsdlIsOf();
          expr.setType(attr(element, "Type"));
          readExpressions(reader, element, expr);
          write(target, expr);
        } else if ("LabeledElement".equals(name)) {
          CsdlLabeledElement expr = new CsdlLabeledElement();
          expr.setName(attr(element, "Name"));
          readExpressions(reader, element, expr);
          write(target, expr);
        } else if ("LabeledElementReference".equals(name)) {
          CsdlLabeledElementReference expr = new CsdlLabeledElementReference();
          expr.setValue(elementValue(reader, element));
          write(target, expr);
        } else if ("Null".equals(name)) {
          write(target, new CsdlNull());
        } else if ("Record".equals(name)) {
          CsdlRecord expr = new CsdlRecord();
          expr.setType(attr(element, "Type"));          
          readPropertyValues(reader, element, expr);
          write(target, expr);          
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, (CsdlAnnotatable)target);
        }
      }
    }.read(reader, element, target, "Collection", "AnnotationPath",
        "NavigationPropertyPath", "Path", "PropertyPath", "UrlRef",
        "Apply", "Function", "Cast", "If", "IsOf", "LabeledElement",
        "LabeledElementReference", "Null", "Record","Binary", "Bool", "Date",
        "DateTimeOffset", "Decimal", "Duration", "EnumMember", "Float", "Guid",
        "Int", "String", "TimeOfDay", "Annotation");
  }
  
  private <T> void readAttributeExpressions(StartElement element, T target)
      throws XMLStreamException {
    // attribute based expressions
    for (ConstantExpressionType type:ConstantExpressionType.values()) {
      if (attr(element, type.name()) != null) {
        write(target, new CsdlConstantExpression(
            type, attr(element, type.name())));
      }        
    }
    
    if (attr(element,  "AnnotationPath") != null) {
     write(target, new CsdlAnnotationPath().setValue(attr(element,  "AnnotationPath"))); 
    }
    if (attr(element,  "NavigationPropertyPath") != null) {
      write(target, new CsdlNavigationPropertyPath()
          .setValue(attr(element, "NavigationPropertyPath"))); 
    }
    if (attr(element,  "Path") != null) {
      write(target, new CsdlPath().setValue(attr(element, "Path"))); 
    }
    if (attr(element,  "PropertyPath") != null) {
      write(target, new CsdlPropertyPath().setValue(attr(element, "PropertyPath"))); 
    }
    if (attr(element,  "UrlRef") != null) {
      write(target, new CsdlUrlRef().setValue(new CsdlConstantExpression(
          ConstantExpressionType.String, attr(element, "UrlRef"))));
    }
  }  
  
  private String elementValue(XMLEventReader reader, StartElement element) throws XMLStreamException {
    while (reader.hasNext()) {
      XMLEvent event = reader.peek();
      if (event.isStartElement() || event.isEndElement()) {
        return null;
      } else if (event.isCharacters()){
        reader.nextEvent();
        String data = event.asCharacters().getData();
        if (data.trim().length() > 0) {
          return data.trim();
        }
      }
    }    
    return null;
  }
  
  private void readPropertyValues(XMLEventReader reader,
      StartElement element, CsdlRecord record) throws XMLStreamException {
    
    new ElementReader<CsdlRecord>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlRecord record, String name)
          throws XMLStreamException {
        if ("PropertyValue".equals(name)) {
          CsdlPropertyValue value = new CsdlPropertyValue();
          value.setProperty(attr(element, "Property"));
          readAttributeExpressions(element, value);
          readExpressions(reader, element, value);
          record.getPropertyValues().add(value);
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, record);
        }
      }
    }.read(reader, element, record, "PropertyValue", "Annotation");    
  }  
  
  private void readFunction(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    CsdlFunction function = new CsdlFunction();
    function.setParameters(new ArrayList<CsdlParameter>());
    function.setName(attr(element, "Name"));
    function.setBound(Boolean.parseBoolean(attr(element, "IsBound")));
    function.setComposable(Boolean.parseBoolean(attr(element, "IsComposable")));
    String entitySetPath = attr(element, "EntitySetPath");
    if (entitySetPath != null) {
      // TODO: need to parse into binding and path.
      function.setEntitySetPath(entitySetPath);
    }
    readOperationParameters(reader, function);
    schema.getFunctions().add(function);
  }

  private void readOperationParameters(XMLEventReader reader, final CsdlOperation operation)
      throws XMLStreamException {
    new ElementReader<CsdlOperation>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlOperation operation, String name)
          throws XMLStreamException {
        if ("Parameter".equals(name)) {
          readParameter(reader, element, operation);
        } else if ("ReturnType".equals(name)) {
          readReturnType(reader, element, operation);
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, operation);
        }
      }
    }.read(reader, null, operation, "Parameter", "ReturnType", "Annotation");
  }

  private void readEnumType(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    CsdlEnumType type = new CsdlEnumType();
    type.setMembers(new ArrayList<CsdlEnumMember>());
    type.setName(attr(element, "Name"));
    if (attr(element, "UnderlyingType") != null) {
      type.setUnderlyingType(new FullQualifiedName(attr(element, "UnderlyingType")));
    }
    type.setFlags(Boolean.parseBoolean(attr(element, "IsFlags")));

    readEnumMembers(reader, element, type);
    schema.getEnumTypes().add(type);
  }

  private void readEnumMembers(XMLEventReader reader, StartElement element, CsdlEnumType type)
      throws XMLStreamException {
    
    new ElementReader<CsdlEnumType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlEnumType type, String name)
          throws XMLStreamException {
        if ("Member".equals(name)) {
          CsdlEnumMember member = new CsdlEnumMember();
          member.setName(attr(element, "Name"));
          member.setValue(attr(element, "Value"));
          peekAnnotations(reader, name, member);
          type.getMembers().add(member);
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, type);
        }
      }
    }.read(reader, element, type, "Member", "Annotation");
  }

  private void readEntityType(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    CsdlEntityType entityType = new CsdlEntityType();
    entityType.setProperties(new ArrayList<CsdlProperty>());
    entityType.setNavigationProperties(new ArrayList<CsdlNavigationProperty>());
    entityType.setKey(new ArrayList<CsdlPropertyRef>());
    entityType.setName(attr(element, "Name"));
    if (attr(element, "BaseType") != null) {
      entityType.setBaseType(new FullQualifiedName(attr(element, "BaseType")));
    }
    entityType.setAbstract(Boolean.parseBoolean(attr(element, "Abstract")));
    entityType.setOpenType(Boolean.parseBoolean(attr(element, "OpenType")));
    entityType.setHasStream(Boolean.parseBoolean(attr(element, "HasStream")));
    readEntityProperties(reader, entityType);
    schema.getEntityTypes().add(entityType);
  }

  private void readEntityProperties(XMLEventReader reader, CsdlEntityType entityType)
      throws XMLStreamException {
    new ElementReader<CsdlEntityType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlEntityType entityType, String name)
          throws XMLStreamException {
        if ("Property".equals(name)) {
          entityType.getProperties().add(readProperty(reader, element));
        } else if ("NavigationProperty".equals(name)) {
          entityType.getNavigationProperties().add(readNavigationProperty(reader, element));
        } else if ("Key".equals(name)) {
          readKey(reader, element, entityType);
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, entityType);
        }
      }
    }.read(reader, null, entityType, "Property", "NavigationProperty", "Key", "Annotation");
  }

  private void readKey(XMLEventReader reader, StartElement element, CsdlEntityType entityType)
      throws XMLStreamException {
    new ElementReader<CsdlEntityType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlEntityType entityType, String name)
          throws XMLStreamException {
        CsdlPropertyRef ref = new CsdlPropertyRef();
        ref.setName(attr(element, "Name"));
        ref.setAlias(attr(element, "Alias"));
        entityType.getKey().add(ref);
      }
    }.read(reader, element, entityType, "PropertyRef");
  }

  private CsdlNavigationProperty readNavigationProperty(XMLEventReader reader, StartElement element)
      throws XMLStreamException {
    CsdlNavigationProperty property = new CsdlNavigationProperty();
    property.setReferentialConstraints(new ArrayList<CsdlReferentialConstraint>());

    property.setName(attr(element, "Name"));
    property.setType(readType(element));
    property.setCollection(isCollectionType(element));
    property.setNullable(Boolean.parseBoolean(attr(element, "Nullable") == null ? "true" : attr(element, "Nullable")));
    property.setPartner(attr(element, "Partner"));
    property.setContainsTarget(Boolean.parseBoolean(attr(element, "ContainsTarget")));

    new ElementReader<CsdlNavigationProperty>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlNavigationProperty property,
          String name) throws XMLStreamException {
        if ("ReferentialConstraint".equals(name)) {
          CsdlReferentialConstraint constraint = new CsdlReferentialConstraint();
          constraint.setProperty(attr(element, "Property"));
          constraint.setReferencedProperty(attr(element, "ReferencedProperty"));
          peekAnnotations(reader, name, constraint);
          property.getReferentialConstraints().add(constraint);
        } else if ("OnDelete".equals(name)) {
          CsdlOnDelete delete = new CsdlOnDelete();
          delete.setAction(CsdlOnDeleteAction.valueOf(attr(element, "Action")));
          property.setOnDelete(delete);
          peekAnnotations(reader, name, delete);
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, property);
        }
      }
    }.read(reader, element, property, "ReferentialConstraint", "OnDelete", "Annotation");
    return property;
  }

  private static String attr(StartElement element, String name) {
    Attribute attr = element.getAttributeByName(new QName(name));
    if (attr != null) {
      return attr.getValue();
    }
    return null;
  }

  private static String attrNS(StartElement element, String ns, String name) {
    Attribute attr = element.getAttributeByName(new QName(ns, name));
    if (attr != null) {
      return attr.getValue();
    }
    return null;
  }  
  
  private CsdlProperty readProperty(XMLEventReader reader, StartElement element)
      throws XMLStreamException {
    CsdlProperty property = new CsdlProperty();
    property.setName(attr(element, "Name"));
    property.setType(readType(element));
    property.setCollection(isCollectionType(element));
    property.setNullable(Boolean.parseBoolean(attr(element, "Nullable") == null ? "true" : attr(
        element, "Nullable")));
    if (attr(element, "Unicode") != null) {
      property.setUnicode(Boolean.parseBoolean(attr(element, "Unicode")));
    }

    String maxLength = attr(element, "MaxLength");
    if (maxLength != null) {
      property.setMaxLength(Integer.parseInt(maxLength));
    }
    String precision = attr(element, "Precision");
    if (precision != null) {
      property.setPrecision(Integer.parseInt(precision));
    }
    String scale = attr(element, "Scale");
    if (scale != null) {
      property.setScale(Integer.parseInt(scale));
    }
    String srid = attr(element, "SRID");
    if (srid != null) {
      property.setSrid(SRID.valueOf(srid));
    }
    String defaultValue = attr(element, "DefaultValue");
    if (defaultValue != null) {
      property.setDefaultValue(defaultValue);
    }
    peekAnnotations(reader, element.getName().getLocalPart(), property);
    return property;
  }

  private void readEntityContainer(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    final CsdlEntityContainer container = new CsdlEntityContainer();
    container.setName(attr(element, "Name"));
    if (attr(element, "Extends") != null) {
      container.setExtendsContainer(attr(element, "Extends"));
    }
    container.setActionImports(new ArrayList<CsdlActionImport>());
    container.setFunctionImports(new ArrayList<CsdlFunctionImport>());
    container.setEntitySets(new ArrayList<CsdlEntitySet>());
    container.setSingletons(new ArrayList<CsdlSingleton>());

    new ElementReader<CsdlSchema>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlSchema schema, String name)
          throws XMLStreamException {
        if ("EntitySet".equals(name)) {
          readEntitySet(reader, element, container);
        } else if ("Singleton".equals(name)) {
          readSingleton(reader, element, container);
        } else if ("ActionImport".equals(name)) {
          readActionImport(reader, element, container);
        } else if ("FunctionImport".equals(name)) {
          readFunctionImport(reader, element, container);
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, container);
        }
      }

      private void readFunctionImport(XMLEventReader reader,
          StartElement element, CsdlEntityContainer container)
          throws XMLStreamException {
        CsdlFunctionImport functionImport = new CsdlFunctionImport();
        functionImport.setName(attr(element, "Name"));
        functionImport.setFunction(new FullQualifiedName(attr(element, "Function")));
        functionImport.setIncludeInServiceDocument(Boolean.parseBoolean(attr(element,
            "IncludeInServiceDocument")));

        String entitySet = attr(element, "EntitySet");
        if (entitySet != null) {
          functionImport.setEntitySet(entitySet);
        }
        peekAnnotations(reader, "FunctionImport", functionImport);
        container.getFunctionImports().add(functionImport);
      }

      private void readActionImport(XMLEventReader reader,
          StartElement element, CsdlEntityContainer container)
          throws XMLStreamException {
        CsdlActionImport actionImport = new CsdlActionImport();
        actionImport.setName(attr(element, "Name"));
        actionImport.setAction(new FullQualifiedName(attr(element, "Action")));

        String entitySet = attr(element, "EntitySet");
        if (entitySet != null) {
          actionImport.setEntitySet(entitySet);
        }
        peekAnnotations(reader, "ActionImport", actionImport);
        container.getActionImports().add(actionImport);
      }

      private void readSingleton(XMLEventReader reader, StartElement element,
          CsdlEntityContainer container) throws XMLStreamException {
        CsdlSingleton singleton = new CsdlSingleton();
        singleton.setNavigationPropertyBindings(new ArrayList<CsdlNavigationPropertyBinding>());
        singleton.setName(attr(element, "Name"));
        singleton.setType(new FullQualifiedName(attr(element, "Type")));
        singleton.setNavigationPropertyBindings(new ArrayList<CsdlNavigationPropertyBinding>());
        readNavigationPropertyBindings(reader, element, singleton);
        container.getSingletons().add(singleton);
      }

      private void readEntitySet(XMLEventReader reader, StartElement element,
          CsdlEntityContainer container) throws XMLStreamException {
        CsdlEntitySet entitySet = new CsdlEntitySet();
        entitySet.setName(attr(element, "Name"));
        entitySet.setType(new FullQualifiedName(attr(element, "EntityType")));
        entitySet.setIncludeInServiceDocument(Boolean.parseBoolean(attr(element,
            "IncludeInServiceDocument")));
        entitySet.setNavigationPropertyBindings(new ArrayList<CsdlNavigationPropertyBinding>());
        readNavigationPropertyBindings(reader, element, entitySet);
        container.getEntitySets().add(entitySet);
      }

      private void readNavigationPropertyBindings(XMLEventReader reader, StartElement element,
          CsdlBindingTarget entitySet) throws XMLStreamException {
        new ElementReader<CsdlBindingTarget>() {
          @Override
          void build(XMLEventReader reader, StartElement element,
              CsdlBindingTarget entitySet, String name) throws XMLStreamException {
            if ("NavigationPropertyBinding".equals(name)) {
              CsdlNavigationPropertyBinding binding = new CsdlNavigationPropertyBinding();
              binding.setPath(attr(element, "Path"));
              binding.setTarget(attr(element, "Target"));
              entitySet.getNavigationPropertyBindings().add(binding);
            } else if ("Annotation".equals(name)) {
              readAnnotations(reader, element, entitySet);
            }
          }

        }.read(reader, element, entitySet, "NavigationPropertyBinding", "Annotation");
      }
    }.read(reader, element, schema, "EntitySet", "Singleton", "ActionImport", "FunctionImport", "Annotation");
    schema.setEntityContainer(container);
  }

  private void readComplexType(XMLEventReader reader, StartElement element, CsdlSchema schema)
      throws XMLStreamException {
    CsdlComplexType complexType = new CsdlComplexType();
    complexType.setProperties(new ArrayList<CsdlProperty>());
    complexType.setNavigationProperties(new ArrayList<CsdlNavigationProperty>());
    complexType.setName(attr(element, "Name"));
    if (attr(element, "BaseType") != null) {
      complexType.setBaseType(new FullQualifiedName(attr(element, "BaseType")));
    }
    complexType.setAbstract(Boolean.parseBoolean(attr(element, "Abstract")));
    complexType.setOpenType(Boolean.parseBoolean(attr(element, "OpenType")));
    readProperties(reader, complexType);

    schema.getComplexTypes().add(complexType);
  }

  private void readProperties(XMLEventReader reader, CsdlComplexType complexType)
      throws XMLStreamException {
    new ElementReader<CsdlComplexType>() {
      @Override
      void build(XMLEventReader reader, StartElement element, CsdlComplexType complexType, String name)
          throws XMLStreamException {
        if ("Property".equals(name)) {
          complexType.getProperties().add(readProperty(reader, element));
        } else if ("NavigationProperty".equals(name)) {
          complexType.getNavigationProperties().add(readNavigationProperty(reader, element));
        } else if ("Annotation".equals(name)) {
          readAnnotations(reader, element, complexType);
        }
      }
    }.read(reader, null, complexType, "Property", "NavigationProperty", "Annotation");
  }

  abstract class ElementReader<T> {
    void read(XMLEventReader reader, StartElement parentElement, T t, String... names)
        throws XMLStreamException {
      while (reader.hasNext()) {
        XMLEvent event = reader.peek();

        if (!parseAnnotations) {
          XMLEvent eventBefore = event;
          event = skipAnnotations(reader, event);
          // if annotation is stripped start again
          if (eventBefore != event) {            
            continue;
          }
        }

        if (!event.isStartElement() && !event.isEndElement()) {
          reader.nextEvent();
          continue;
        }

        if (parentElement != null && event.isEndElement()
            && ((EndElement) event).getName().equals(parentElement.getName())) {
          // end reached
          break;
        }

        boolean hit = false;

        for (String name : names) {
          if (event.isStartElement()) {
            StartElement element = event.asStartElement();
            if (element.getName().getLocalPart().equals(name)) {              
              reader.nextEvent(); // advance cursor start which is current
              build(reader, element, t, name);
              hit = true;
              break;
            }
          }
          if (event.isEndElement()) {
            EndElement e = event.asEndElement();
            if (e.getName().getLocalPart().equals(name)) {
              reader.nextEvent(); // advance cursor to end which is current
              hit = true;
              break;
            }
          }
        }
        if (!hit) {
          break;
        }
      }
    }

    private XMLEvent skipAnnotations(XMLEventReader reader, XMLEvent event)
        throws XMLStreamException {
      boolean skip = false;

      while (reader.hasNext()) {
        if (event.isStartElement()) {
          StartElement element = event.asStartElement();
          if ("Annotation".equals(element.getName().getLocalPart())) {
            skip = true;
          }
        }
        if (event.isEndElement()) {
          EndElement element = event.asEndElement();
          if ("Annotation".equals(element.getName().getLocalPart())) {
            return reader.peek();
          }
        }
        if (skip) {
          event = reader.nextEvent();
        } else {
          return event;
        }
      }
      return event;
    }

    abstract void build(XMLEventReader reader, StartElement element, T t, String name)
        throws XMLStreamException;
  }
  
  private static class DefaultReferenceResolver implements ReferenceResolver {
    @Override
    public InputStream resolveReference(URI referenceUri, String xmlBase) {
      InputStream in = null;
      try {
        if (referenceUri.isAbsolute()) {
          URL schemaURL = referenceUri.toURL();
          in = schemaURL.openStream();
        } else {
          if (xmlBase != null) {
            URL schemaURL = new URL(xmlBase+referenceUri.toString());
            in = schemaURL.openStream();
          } else {
            in = this.getClass().getClassLoader().getResourceAsStream(referenceUri.getPath());
            if (in == null) {
              throw new EdmException("No xml:base set to read the references from the metadata");
            }
          }        
        }
        return in;
      } catch (MalformedURLException e) {
        throw new EdmException(e);
      } catch (IOException e) {
        throw new EdmException(e);
      }
    }
  }   
}
