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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.producer.core.uri.antlr.UriLexer;
import org.apache.olingo.producer.core.uri.antlr.UriParser;
import org.apache.olingo.producer.core.uri.antlr.UriParser.OdataRelativeUriContext;

public class UriParserImpl {

  public UriInfoImpl readUri(final String uri, final Edm edm) {
    UriInfoImpl ret = new UriInfoImpl();
    OdataRelativeUriContext root = parseUri(uri);

    root.accept(new UriTreeVisitor(ret, edm));

    /*
     * 
     * 
     * if (root.getChildCount() == 1 ) {
     * System.out.println("is service");
     * return null;
     * }
     * 
     * ParserRuleContext c0 = (ParserRuleContext) root.children.get(0);
     * if (c0 instanceof BatchContext)
     * {
     * System.out.print("is $batch");
     * return null;
     * } else if (c0 instanceof EntityAContext)
     * {
     * readEntity(c0);
     * return null;
     * } else if (c0 instanceof MetadataContext)
     * {
     * readMetadata(c0);
     * return null;
     * } else if (c0 instanceof ResourcePathAContext)
     * {
     * readResourcePath(c0.getChild(0));
     * if (c0.getChildCount() > 2) {
     * readQueryOptions(c0.getChild(0));
     * }
     * return null;
     * }
     * 
     * System.out.println("Error");
     */
    return ret;
  }

  private OdataRelativeUriContext parseUri(final String uri) {

    ANTLRInputStream input = new ANTLRInputStream(uri);

    UriLexer lexer = new UriLexer(input);

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    UriParser parser = new UriParser(tokens);

    //parser.addErrorListener(new ErrorHandler());
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
