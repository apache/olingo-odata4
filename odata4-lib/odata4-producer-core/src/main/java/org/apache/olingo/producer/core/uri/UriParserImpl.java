/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.producer.core.uri;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmNamed;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.producer.api.uri.KeyPredicate;
import org.apache.olingo.producer.api.uri.UriInfoKind;
import org.apache.olingo.producer.api.uri.UriPathInfoKind;
import org.apache.olingo.producer.core.uri.antlr.UriLexer;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.AllAltContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.BatchAltContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.CrossjoinAltContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.EntityAltContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.MetadataAltContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.OdataRelativeUriContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.PathSegmentContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.PathSegmentsAltContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.PathSegmentsContext;
import org.apache.olingo.producer.core.uri.antlr.UriParserParser.ResourcePathAltContext;

public class UriParserImpl {


  private EdmEntityContainer entityContainer = null;

  public UriInfoImpl readUri(final String uri, final Edm edm) {
    entityContainer = edm.getEntityContainer(null);// "RefScenario","Container1"

    UriInfoImpl ret = new UriInfoImpl();
    OdataRelativeUriContext root = parseUri(uri);

    ret = readODataRelativeUri(root);
    return ret;
  }

  UriInfoImpl readODataRelativeUri(OdataRelativeUriContext root) {
    int i = root.getChildCount();

    if (root instanceof BatchAltContext) {
      return new UriInfoImpl().setKind(UriInfoKind.batch);

    } else if (root instanceof EntityAltContext) {
      // TODO implement
    } else if (root instanceof MetadataAltContext) {
      // TODO implement
    } else if (root instanceof ResourcePathAltContext) {

      return readResourcePath(root);

    }

    return null;
  }

  private UriInfoImpl readResourcePath(OdataRelativeUriContext root) {
    ParseTree firstChild = root.getChild(0);

    if (firstChild instanceof AllAltContext) {
      return new UriInfoImpl().setKind(UriInfoKind.all);
    } else if (firstChild instanceof CrossjoinAltContext) {
      // TODO read ODIs behind crossjoin
      return new UriInfoImpl().setKind(UriInfoKind.crossjoin);
    } else if (firstChild instanceof PathSegmentsAltContext) {
      return readPathSegments((PathSegmentsAltContext) firstChild);
    }
    return null;
  }

  private UriInfoImpl readPathSegments(PathSegmentsAltContext pathSegmentsAlt) {
    PathSegmentsContext firstChild = (PathSegmentsContext) pathSegmentsAlt.getChild(0);

    UriInfoImpl uriInfo = new UriInfoImpl();

    readFirstPathSegment(uriInfo, firstChild.ps.get(0));

    for (int i = 1; i < firstChild.ps.size(); i++) {

    }

    return null;
  }

  private void readFirstPathSegment(UriInfoImpl uriInfo, PathSegmentContext ctx) {
    /*if (ctx.ns != null) {//TODO implement
      // Error: First pathsegment can not be qualified. Allowed is entityset|function...
    }*/

    /*if (ctx.odi == null) {//TODO implement
      // Error: First pathsegment must contain an odata identifier
    }*/

    
    String odataIdentifier = ""; //TODO ctx.odi.getText();

    // get element "odataIdentifier" from EDM
    EdmNamed edmObject = entityContainer.getElement(odataIdentifier);

    if (edmObject instanceof EdmEntitySet) {

      // is EdmEntitySet

      EdmEntitySet entityset = (EdmEntitySet) edmObject;
      UriPathInfoEntitySetImpl pathInfo = new UriPathInfoEntitySetImpl();
      pathInfo.setKind(UriPathInfoKind.entitySet);
      pathInfo.setEntityContainer(entityContainer);

      pathInfo.setTargetEntityset(entityset);
      pathInfo.setTargetType(entityset.getEntityType());
      pathInfo.setCollection(true);

      // TODO check if kp may have been collected into fp
      /*if (ctx.kp != null) {
        //pathInfo.setKeyPredicates(readkeypredicates(ctx.kp, entityset.getEntityType()));
        pathInfo.setCollection(false);
      }*/
      uriInfo.addUriPathInfo(pathInfo);
      return;
    } else if (edmObject instanceof EdmSingleton) {

      // is EdmSingleton

      EdmSingleton singleton = (EdmSingleton) edmObject;
      UriPathInfoSigletonImpl pathInfo = new UriPathInfoSigletonImpl(); // TODO change to UriPathInfoImplEntitySet
      pathInfo.setKind(UriPathInfoKind.singleton);
      pathInfo.setEntityContainer(entityContainer);
      pathInfo.setTargetType(singleton.getEntityType());
      // pathInfo.targetType = singleton.getEntityType();
      pathInfo.setCollection(false);

      uriInfo.addUriPathInfo(pathInfo);
      return;
    } else if (edmObject instanceof EdmActionImport) {

      // is EdmActionImport

      UriPathInfoActionImportImpl pathInfo = new UriPathInfoActionImportImpl();
      pathInfo.setKind(UriPathInfoKind.actionImport);

      uriInfo.addUriPathInfo(pathInfo);
      return;

    } else if (edmObject instanceof EdmFunctionImport) {

      // is EdmFunctionImport

      EdmFunctionImport fi = (EdmFunctionImport) edmObject;
      UriPathInfoImplFunctionImport pathInfo = new UriPathInfoImplFunctionImport();
      pathInfo.setKind(UriPathInfoKind.functioncall);

      /*if (ctx.fp != null) {
        pathInfo.setFunctionParameter(readFunctionParameters(uriInfo, ctx.fp));
      }*/
      /*if (ctx.kp != null) {
        pathInfo.setKeyPredicates(readkeypredicates(ctx.kp, fi.getReturnedEntitySet().getEntityType()));
      }*/

      uriInfo.addUriPathInfo(pathInfo);
      return;
    }

  }

  private Object readFunctionParameters(UriInfoImpl uriInfo/*, FunctionParametersContext fp*/) {
    // TODO Auto-generated method stub
    return null;
  }

  private List<KeyPredicate> readkeypredicates(/*final KeypredicatesContext kp,*/ final EdmType edmType1) {
/*
    EdmEntityType edmType;
    if (edmType1 instanceof EdmEntityType) {
      edmType = (EdmEntityType) edmType1;
    } else {
      return null;// TODO better error
    }
*/
    List<KeyPredicate> ret = new ArrayList<KeyPredicate>();
/*
    ParseTree child = kp.getChild(0);
    if (child instanceof SimpleKeyContext) {
      // it is a simple key without a name

      if (edmType.getKeyPredicateNames().size() != 1) {
        // Error Simple Key only allowed if there is only one keyproperty
      }

      String keyPredicateName = edmType.getKeyPredicateNames().get(0);
      String keyPropertyName = edmType.getKeyPropertyRef(keyPredicateName).getKeyPropertyName();
      EdmElement property = edmType.getProperty(keyPropertyName);
      if (property == null) {
        // error keyproperty not found
      }

      EdmType type = property.getType();
      if (type.getKind() != EdmTypeKind.PRIMITIVE) {
        // error property has wrong type
      }

      String keyLiteral = child.getText();
      // TODO detect type of keyLiteral and compare with "type"

      ret.add(new KeyPredicateImpl(keyLiteral, (EdmProperty) property));
    } else if (child instanceof CompoundKeyContext) {
      CompoundKeyContext compoundKey = (CompoundKeyContext) child;

      for (KeyValuePairContext kvp : compoundKey.kvp) {
        String keyPropertyName = kvp.odi.getText();
        EdmElement property = edmType.getProperty(keyPropertyName);
        if (property == null) {
          // error keyproperty not found
        }

        String keyLiteral = kvp.val.getText();
        // TODO detect type of keyLiteral and compare with "type"

        KeyPredicate keyPredicate = new KeyPredicateImpl(keyLiteral, (EdmProperty) property);
        ret.add(keyPredicate);
      }

    }
*/
    return ret;
  }

  private OdataRelativeUriContext parseUri(final String uri) {

    ANTLRInputStream input = new ANTLRInputStream(uri);

    //UriLexer lexer = new UriLexer(input);
    UriLexer lexer = new UriLexer(input);

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    UriParserParser parser = new UriParserParser(tokens);

    // parser.addErrorListener(new ErrorHandler());
    // if (stage == 1) {
    // //see https://github.com/antlr/antlr4/issues/192
    // parser.setErrorHandler(new BailErrorStrategy());
    // parser.getInterpreter().setPredictionMode(PredictionMode.LL);
    // } else {
    parser.setErrorHandler(new DefaultErrorStrategy());
    parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
    // }

    // parser.d
    return parser.odataRelativeUri();
  }
}
