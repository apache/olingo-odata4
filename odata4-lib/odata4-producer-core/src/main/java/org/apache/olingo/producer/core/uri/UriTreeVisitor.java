package org.apache.olingo.producer.core.uri;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmNamed;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmStructuralType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTyped;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.producer.api.uri.KeyPredicate;
import org.apache.olingo.producer.core.uri.UriPathInfoImpl.PathInfoType;
import org.apache.olingo.producer.core.uri.antlr.UriBaseVisitor;
import org.apache.olingo.producer.core.uri.antlr.UriParser;
import org.apache.olingo.producer.core.uri.antlr.UriParser.CompoundKeyContext;
import org.apache.olingo.producer.core.uri.antlr.UriParser.FunctionParameterContext;
import org.apache.olingo.producer.core.uri.antlr.UriParser.FunctionParametersContext;
import org.apache.olingo.producer.core.uri.antlr.UriParser.KeyValuePairContext;
import org.apache.olingo.producer.core.uri.antlr.UriParser.KeypredicatesContext;
import org.apache.olingo.producer.core.uri.antlr.UriParser.PathSegmentContext;
import org.apache.olingo.producer.core.uri.antlr.UriParser.SimpleKeyContext;

public class UriTreeVisitor extends UriBaseVisitor<Object> {
  private Edm edm = null;
  private UriInfoImpl uriInfo = null;
  private EdmEntityContainer entityContainer = null;
  private boolean isFirst;

  /**
   * @param uriInfo
   * @param edm
   */
  UriTreeVisitor(final UriInfoImpl uriInfo, final Edm edm) {
    this.uriInfo = uriInfo;
    this.edm = edm;
    // this.entityContainer = edm.getEntityContainer(null, null);//"RefScenario","Container1"
    entityContainer = edm.getEntityContainer(null);// "RefScenario","Container1"

  }

  @Override
  public Object visitBatch(@NotNull final UriParser.BatchContext ctx) {
    // Set UriType to Batch

    return null;
  }

  @Override
  public Object visitEntityA(@NotNull final UriParser.EntityAContext ctx) {
    // Set UriType to Entity
    // uriInfo.AddResSelector(new OriResourceSelector());

    return visitChildren(ctx);
  }

  @Override
  public Object visitMetadata(@NotNull final UriParser.MetadataContext ctx) {
    // Set UriType to Entity
    // uriInfo.AddResSelector(new OriResourceSelector());

    return visitChildren(ctx);
  }

  @Override
  public Object visitPathSegment(@NotNull final UriParser.PathSegmentContext ctx) {
    // info crossjoin and all have own ruleContexts
    if (isFirst) {
      handleFirstPathSegment(ctx);
      isFirst = false;
    } else {
      handlePathSegments(ctx);
    }
    return null;
  }

  @Override
  public Object visitPathSegments(@NotNull final UriParser.PathSegmentsContext ctx) {
    isFirst = true;
    return visitChildren(ctx);
  }

  private void handleFirstPathSegment(final UriParser.PathSegmentContext ctx) {
    if (ctx.ns != null) {
      // Error: First pathsegment can not be qualified. Allowed is entityset|function...
    }

    if (ctx.odi == null) {
      // Error: First pathsegment must contain an odata identifier
    }

    String odataIdentifier = ctx.odi.getText();

    // get element "odataIdentifier" from EDM
    EdmNamed edmObject = entityContainer.getElement(odataIdentifier);

    // is EdmEntitySet
    if (edmObject instanceof EdmEntitySet) {
      EdmEntitySet entityset = (EdmEntitySet) edmObject;
      UriPathInfoImpl pathInfo = new UriPathInfoImpl(); // TODO change to UriPathInfoImplEntitySet
      pathInfo.type = PathInfoType.entitySet;
      pathInfo.entityContainer = entityContainer;
      pathInfo.targetEntityset = entityset;
      pathInfo.targetType = entityset.getEntityType();
      pathInfo.isCollection = true;
      // TODO check if kp may have been collected into fp
      if (ctx.kp != null) {
        pathInfo.keyPredicates = readkeypredicates(ctx.kp, entityset.getEntityType());
        pathInfo.isCollection = false;
      }

      uriInfo.addUriPathInfo(pathInfo);
      return;
    }

    // is EdmSingleton
    if (edmObject instanceof EdmSingleton) {
      EdmSingleton singleton = (EdmSingleton) edmObject;
      UriPathInfoImpl pathInfo = new UriPathInfoImpl(); // TODO change to UriPathInfoImplEntitySet
      pathInfo.type = PathInfoType.singleton;
      pathInfo.entityContainer = entityContainer;
      pathInfo.targetType = singleton.getEntityType();
      pathInfo.isCollection = false;
      uriInfo.addUriPathInfo(pathInfo);
      return;
    }

    // is EdmActionImport
    if (edmObject instanceof EdmActionImport) {
      UriPathInfoImpl pathInfo = new UriPathInfoImpl();
      pathInfo.type = PathInfoType.actionImport;

      uriInfo.addUriPathInfo(pathInfo);
      return;
    }

    // is EdmFunctionImport
    if (edmObject instanceof EdmFunctionImport) {

      EdmFunctionImport fi = (EdmFunctionImport) edmObject;
      UriPathInfoImpl pathInfo = new UriPathInfoImpl();
      pathInfo.type = PathInfoType.functioncall;

      if (ctx.fp != null) {
        pathInfo.functionParameter = readFunctionParameters(ctx.fp);
      }
      if (ctx.kp != null) {
        pathInfo.keyPredicates = readkeypredicates(ctx.kp, fi.getReturnedEntitySet().getEntityType()/* TODO fix */);
      }

      uriInfo.addUriPathInfo(pathInfo);
      return;
    }

  }

  private void handlePathSegments(final UriParser.PathSegmentContext ctx) {

    UriPathInfoImpl prev = uriInfo.getLastUriPathInfo();

    String namespace = (ctx.ns != null) ? ctx.ns.getText().substring(0, ctx.ns.getText().length() - 1) : null;

    if (namespace != null) {
      // is type filter, action or function

      String odataIdentifier = ctx.odi.getText();
      EdmType type = edm.getTypeDefinition(new FullQualifiedName(namespace, odataIdentifier));

      if (type != null) {
        handleTypeFilter(ctx, type);
        return;
      }

      // check for bound actions
      EdmAction action = null;
      EdmFunction function = null;
      if ((action =
          edm.getAction(new FullQualifiedName(namespace, odataIdentifier), new FullQualifiedName(prev.targetType
              .getNamespace(), prev.targetType.getName()), prev.isCollection)) != null) {
        handleBoundAction(ctx, action);
      } else if ((function =
          edm.getFunction(new FullQualifiedName(namespace, odataIdentifier), new FullQualifiedName(prev.targetType
              .getNamespace(), prev.targetType.getName()), prev.isCollection, null)) != null) {
        handleBoundFunction(ctx, function);
      }

      // Error: namespace.odataIdentifier unknown

    } else {
      // check for property
      EdmType type = prev.targetType;
      String odataIdentifier = ctx.odi.getText();

      if (!(type instanceof EdmStructuralType)) {
        // Error: type is not structured ...
      }

      EdmTyped property = ((EdmStructuralType) type).getProperty(odataIdentifier);
      if (property != null) {
        handleProperty(ctx, prev, property);
      }

      // Error: odataIdentifier is not a property of type ...
    }

  }

  private void handleProperty(final UriParser.PathSegmentContext ctx, final UriPathInfoImpl prev,
      final EdmTyped property) {

    // TODO add check that prev is not a collection
    if (property instanceof EdmNavigationProperty) {

      // add to prev properties so that it can be selected when processing the previous pathInfo
      prev.properties.add(new UriPathInfoImpl.PropertyItem(property));

      UriPathInfoImpl pathInfo = new UriPathInfoImpl();
      pathInfo.type = PathInfoType.navicationProperty;

      if (ctx.kp != null) {
        if (pathInfo.isCollection == false) {
          // Error keyPredicates can only applied to a collection
        }
        pathInfo.keyPredicates = readkeypredicates(ctx.kp, property.getType());
        pathInfo.isCollection = false;
      }

      uriInfo.addUriPathInfo(pathInfo);

    } else if (property instanceof EdmProperty) {

      prev.properties.add(new UriPathInfoImpl.PropertyItem(property));

    }
  }

  private void handleBoundFunction(final PathSegmentContext ctx, final EdmFunction function) {
    UriPathInfoImpl pathInfo = new UriPathInfoImpl();
    pathInfo.type = PathInfoType.boundFunctioncall;

    if (ctx.fp != null) {
      pathInfo.functionParameter = readFunctionParameters(ctx.fp);
    }
    uriInfo.addUriPathInfo(pathInfo);
  }

  private void handleBoundAction(final PathSegmentContext ctx, final EdmAction action) {
    UriPathInfoImpl pathInfo = new UriPathInfoImpl();
    pathInfo.type = PathInfoType.boundActionImport;

    if (ctx.fp != null) {
      pathInfo.functionParameter = readFunctionParameters(ctx.fp);
    }
    uriInfo.addUriPathInfo(pathInfo);
  }

  private void handleTypeFilter(final UriParser.PathSegmentContext ctx, final EdmType type) {
    UriPathInfoImpl prev = uriInfo.getLastUriPathInfo();

    if (type == null) {
      // Error: type not found
    }

    if (prev.keyPredicates == null) {
      prev.typeBeforeKeyPredicates = type;
    } else {
      prev.typeAfterKeyPredicates = type;
    }
    prev.targetType = type;

    if (ctx.kp != null) {
      if (!prev.isCollection) {
        // Error: Keypredicates only allowed on collection
      }
      if (prev.typeAfterKeyPredicates != null) {
        // Internal logic error
      }

      prev.keyPredicates = readkeypredicates(ctx.kp, prev.targetType);
    }
  }

  private List<UriPathInfoImpl.ActualFunctionParameter> readFunctionParameters(final FunctionParametersContext fp) {
    List<UriPathInfoImpl.ActualFunctionParameter> ret = new ArrayList<UriPathInfoImpl.ActualFunctionParameter>();

    for (FunctionParameterContext fps : fp.fps) {
      String parameterName = fps.odi.getText();
      String parameterValue = null;

      if (fps.val != null) {
        parameterValue = fps.val.getText();
      } else if (fps.ali != null) {
        parameterValue = fps.ali.getText();
      }

      ret.add(new UriPathInfoImpl.ActualFunctionParameter(parameterName, parameterValue));
    }
    return ret;
  }

  private List<KeyPredicate> readkeypredicates(final KeypredicatesContext kp, final EdmType edmType1) {

    EdmEntityType edmType;
    if (edmType1 instanceof EdmEntityType) {
      edmType = (EdmEntityType) edmType1;
    } else {
      return null;// TODO better error
    }

    List<KeyPredicate> ret = new ArrayList<KeyPredicate>();

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

    return ret;
  }

}
