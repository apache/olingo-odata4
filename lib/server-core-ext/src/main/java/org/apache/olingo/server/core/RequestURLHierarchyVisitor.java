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

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoAll;
import org.apache.olingo.server.api.uri.UriInfoBatch;
import org.apache.olingo.server.api.uri.UriInfoCrossjoin;
import org.apache.olingo.server.api.uri.UriInfoEntityId;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriInfoMetadata;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriInfoService;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceCount;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceIt;
import org.apache.olingo.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.UriResourceLambdaVariable;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.UriResourceRef;
import org.apache.olingo.server.api.uri.UriResourceRoot;
import org.apache.olingo.server.api.uri.UriResourceSingleton;
import org.apache.olingo.server.api.uri.UriResourceValue;
import org.apache.olingo.server.api.uri.queryoption.ApplyOption;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.DeltaTokenOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.apache.olingo.server.api.uri.queryoption.IdOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.SkipTokenOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;

public class RequestURLHierarchyVisitor implements RequestURLVisitor {

  private UriInfo uriInfo;

  public UriInfo getUriInfo() {
    return this.uriInfo;
  }

  @Override
  public void visit(UriInfo info) {
    this.uriInfo = info;

    UriInfoKind kind = info.getKind();
    switch (kind) {
    case all:
      visit(info.asUriInfoAll());
      break;
    case batch:
      visit(info.asUriInfoBatch());
      break;
    case crossjoin:
      visit(info.asUriInfoCrossjoin());
      break;
    case entityId:
      visit(info.asUriInfoEntityId());
      break;
    case metadata:
      visit(info.asUriInfoMetadata());
      break;
    case resource:
      visit(info.asUriInfoResource());
      break;
    case service:
      visit(info.asUriInfoService());
      break;
    }
  }

  @Override
  public void visit(UriInfoService info) {
  }

  @Override
  public void visit(UriInfoAll info) {
  }

  @Override
  public void visit(UriInfoBatch info) {
  }

  @Override
  public void visit(UriInfoCrossjoin info) {
    if (info.getFilterOption() != null) {
      visit(info.getFilterOption());
    }

    if (info.getCountOption() != null) {
      visit(info.getCountOption());
    }

    if(info.getOrderByOption() != null) {
      visit(info.getOrderByOption());
    }

    if (info.getSkipOption() != null) {
      visit(info.getSkipOption());
    }

    if (info.getTopOption() != null) {
      visit(info.getTopOption());
    }

    if (info.getExpandOption() != null) {
      visit(info.getExpandOption());
    }
    
    if(info.getSelectOption() != null) {
      visit(info.getSelectOption());
    }

    if (info.getFormatOption() != null) {
      visit(info.getFormatOption());
    }

    if (info.getSkipTokenOption() != null) {
      visit(info.getSkipTokenOption());
    }    
  }

  @Override
  public void visit(UriInfoEntityId info) {
    visit(info.getSelectOption());

    if (info.getExpandOption() != null) {
      visit(info.getExpandOption());
    }
    if (info.getFormatOption() != null) {
      visit(info.getFormatOption());
    }
    if (info.getIdOption() != null) {
      visit(info.getIdOption(), info.getEntityTypeCast());
    }
  }

  @Override
  public void visit(UriInfoMetadata info) {
  }

  @Override
  public void visit(UriInfoResource info) {
    List<UriResource> parts = info.getUriResourceParts();
    for (UriResource resource : parts) {
      switch (resource.getKind()) {
      case action:
        visit((UriResourceAction) resource);
        break;
      case complexProperty:
        visit((UriResourceComplexProperty) resource);
        break;
      case count:
        visit((UriResourceCount) resource);
        break;
      case entitySet:
        visit((UriResourceEntitySet) resource);
        break;
      case function:
        visit((UriResourceFunction) resource);
        break;
      case it:
        visit((UriResourceIt) resource);
        break;
      case lambdaAll:
        visit((UriResourceLambdaAll) resource);
        break;
      case lambdaAny:
        visit((UriResourceLambdaAny) resource);
        break;
      case lambdaVariable:
        visit((UriResourceLambdaVariable) resource);
        break;
      case navigationProperty:
        visit((UriResourceNavigation) resource);
        break;
      case ref:
        visit((UriResourceRef) resource);
        break;
      case root:
        visit((UriResourceRoot) resource);
        break;
      case primitiveProperty:
        visit((UriResourcePrimitiveProperty) resource);
        break;
      case singleton:
        visit((UriResourceSingleton) resource);
        break;
      case value:
        visit((UriResourceValue) resource);
        break;
      }
    }
    
    if (info.getApplyOption() != null) {
        //per the docs, apply is first
        visit(info.getApplyOption());
    }

    // http://docs.oasis-open.org/odata/odata/v4.0/os/part1-protocol/odata-v4.0-os-part1-protocol.html#_Toc372793682
    if (info.getSearchOption() != null) {
      visit(info.getSearchOption());
    }

    if (info.getFilterOption() != null) {
      visit(info.getFilterOption());
    }

    if (info.getCountOption() != null) {
      visit(info.getCountOption());
    }

    visit(info.getOrderByOption());

    if (info.getSkipOption() != null) {
      visit(info.getSkipOption());
    }

    if (info.getTopOption() != null) {
      visit(info.getTopOption());
    }

    if (info.getExpandOption() != null) {
      visit(info.getExpandOption());
    }

    visit(info.getSelectOption());

    if (info.getFormatOption() != null) {
      visit(info.getFormatOption());
    }

    if (info.getIdOption() != null) {
      visit(info.getIdOption(), null);
    }

    if (info.getSkipTokenOption() != null) {
      visit(info.getSkipTokenOption());
    }
    
    if (info.getDeltaTokenOption() != null) {
      visit(info.getDeltaTokenOption());
    }
  }
  
  @Override
  public void visit(ApplyOption option) {
  }

  @Override
  public void visit(ExpandOption option) {
  }

  @Override
  public void visit(FilterOption info) {
  }

  @Override
  public void visit(FormatOption info) {
  }

  @Override
  public void visit(IdOption info, EdmEntityType type) {
  }

  @Override
  public void visit(CountOption info) {
  }

  @Override
  public void visit(OrderByOption option) {
  }

  @Override
  public void visit(SearchOption option) {
  }

  @Override
  public void visit(SelectOption option) {
  }

  @Override
  public void visit(SkipOption option) {
  }

  @Override
  public void visit(SkipTokenOption option) {
  }

  @Override
  public void visit(TopOption option) {
  }

  @Override
  public void visit(UriResourceCount option) {
  }

  @Override
  public void visit(UriResourceRef info) {
  }
  
  @Override
  public void visit(DeltaTokenOption option) {
  }
  
  @Override
  public void visit(UriResourceRoot info) {
  }

  @Override
  public void visit(UriResourceValue info) {
  }

  @Override
  public void visit(UriResourceAction info) {
  }

  @Override
  public void visit(UriResourceEntitySet info) {
  }

  @Override
  public void visit(UriResourceFunction info) {
  }

  @Override
  public void visit(UriResourceIt info) {
  }

  @Override
  public void visit(UriResourceLambdaAll info) {
  }

  @Override
  public void visit(UriResourceLambdaAny info) {
  }

  @Override
  public void visit(UriResourceLambdaVariable info) {
  }

  @Override
  public void visit(UriResourceNavigation info) {
  }

  @Override
  public void visit(UriResourceSingleton info) {
  }

  @Override
  public void visit(UriResourceComplexProperty info) {
  }

  @Override
  public void visit(UriResourcePrimitiveProperty info) {
  }
}
