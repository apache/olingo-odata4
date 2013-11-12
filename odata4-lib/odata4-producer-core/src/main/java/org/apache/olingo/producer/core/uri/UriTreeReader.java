package org.apache.olingo.producer.core.uri;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.producer.core.uri.antlr.UriLexer;
import org.apache.olingo.producer.core.uri.antlr.UriParser;
import org.apache.olingo.producer.core.uri.antlr.UriParser.OdataRelativeUriAContext;

public class UriTreeReader {

//  //@Test
//  public void Test() {
//    String uri = "EntityColFunctionImport(ParameterName1=1)(1)/Namespace1.EntityTypeName/EntityFunctionImport()/"
//        + "Namespace1.EntityTypeName?$expand=ComplexColProperty/Namespace1.ComplexTypeName/EntityNavigationProperty"
//        + "&$filter=Namespace1.EntityTypeName/Namespace1.EntityFunction() eq 1 and true";
//    //String uri = "$entity?$id=1";
//    readUri(uri);
//  }

  public UriInfoImpl readUri(final String uri, final Edm edm) {
    UriInfoImpl ret = new UriInfoImpl();
    OdataRelativeUriAContext root = parseUri(uri);

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

  private OdataRelativeUriAContext parseUri(final String uri) {

    ANTLRInputStream input = new ANTLRInputStream(uri);

    UriLexer lexer = new UriLexer(input);

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    UriParser parser = new UriParser(tokens);

    parser.addErrorListener(new ErrorHandler());

    // if (stage == 1) {
    // //see https://github.com/antlr/antlr4/issues/192
    // parser.setErrorHandler(new BailErrorStrategy());
    // parser.getInterpreter().setPredictionMode(PredictionMode.LL);
    // } else {
    parser.setErrorHandler(new DefaultErrorStrategy());
    parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
    // }

    // parser.d
    return parser.odataRelativeUriA();
  }
}
