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
package myservice.mynamespace.service;

import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.processor.ActionVoidProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResourceAction;

import myservice.mynamespace.data.Storage;

public class DemoActionProcessor implements ActionVoidProcessor {

  private OData odata;
  private Storage storage;

  public DemoActionProcessor(final Storage storage) {
    this.storage = storage;
  }

  @Override
  public void init(final OData odata, final ServiceMetadata serviceMetadata) {
    this.odata = odata;
  }

  @Override
  public void processActionVoid(ODataRequest request, ODataResponse response, UriInfo uriInfo,
      ContentType requestFormat) throws ODataApplicationException, ODataLibraryException {

    // 1st Get the action from the resource path
    final EdmAction edmAction = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts()
                                                                                .get(0)).getAction();

    // 2nd Deserialize the parameter
    // In our case there is only one action. So we can be sure that parameter "Amount" has been provided by the client
    if (requestFormat == null) {
      throw new ODataApplicationException("The content type has not been set in the request.",
          HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
    
    final ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
    final Map<String, Parameter> actionParameter = deserializer.actionParameters(request.getBody(), edmAction)
        .getActionParameters();
    final Parameter parameterAmount = actionParameter.get(DemoEdmProvider.PARAMETER_AMOUNT);
    
    // The parameter amount is nullable
    if(parameterAmount.isNull()) {
      storage.resetDataSet();
    } else {
      final Integer amount = (Integer) parameterAmount.asPrimitive();
      storage.resetDataSet(amount);
    }

    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }
}
