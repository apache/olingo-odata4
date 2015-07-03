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

package org.apache.olingo.server.core.serializer.json;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class MetadataDocumentJsonTest {


    private static final String CORE_VOCABULARY =
            "http://docs.oasis-open.org/odata/odata/v4.0/cs02/vocabularies/Org.OData.Core.V1.xml";

    @Test
    public void writeMetadataWithTechnicalScenario() throws Exception {
        final OData odata = OData.newInstance();
        final List<EdmxReference> references = getEdmxReferences();
        final ServiceMetadata serviceMetadata = odata.createServiceMetadata(
                new EdmTechProvider(references), references);

        final String metadata = IOUtils.toString(
                odata.createSerializer(ODataFormat.JSON).metadataDocument(serviceMetadata).getContent());
        assertNotNull(metadata);

        String expectedString;
        expectedString="\"$schema\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#\"";
        assertThat(metadata, containsString(expectedString));


        expectedString=
        "\"olingo.odata.test1.ENString\" : {\n" +
                "      \"enum\" : [ \"String1\", \"String2\", \"String3\" ],\n" +
                "      \"String1@odata.value\" : \"1\",\n" +
                "      \"String2@odata.value\" : \"2\",\n" +
                "      \"String3@odata.value\" : \"4\"\n" +
                "    }";
        assertThat(metadata, containsString(expectedString));


        expectedString="\"olingo.odata.test1.TDString\" : {\n" +"      \"$ref\" : \"http://docs.oasis-open.org/odata/" +
                "odata-json-csdl/v4.0/edm.json#/definitions/Edm.String\",\n" +
                "      \"maxLength\" : 15\n" +
                "    }";

        assertThat(metadata, containsString(expectedString));


        expectedString=
        " \"references\" : {\n" +
                "    \"http://docs.oasis-open.org/odata/odata/v4.0/cs02/vocabularies/Org.OData.Core.V1.xml\" : {\n" +
                "      \"includes\" : {\n" +
                "        \"Org.OData.Core.V1\" : {\n" +
                "          \"alias\" : \"Core\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "\"singletons\" : {\n" +
                "          \"SI\" : {\n" +
                "            \"type\" : \"Namespace1_Alias.ETTwoPrim\"\n" +
                "          },\n" +
                "          \"SINav\" : {\n" +
                "            \"type\" : \"Namespace1_Alias.ETTwoKeyNav\",\n" +
                "            \"navigationPropertyBindings\" : {\n" +
                "              \"NavPropertyETTwoKeyNavMany\" : {\n" +
                "                \"target\" : \"ESTwoKeyNav\"\n" +
                "              },\n" +
                "              \"NavPropertyETTwoKeyNavOne\" : {\n" +
                "                \"target\" : \"ESTwoKeyNav\"\n" +
                "              },\n" +
                "              \"NavPropertyETKeyNavOne\" : {\n" +
                "                \"target\" : \"ESKeyNav\"\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          \"SIMedia\" : {\n" +
                "            \"type\" : \"Namespace1_Alias.ETMedia\"\n" +
                "          }\n" +
                "        }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        " \"actionImports\" : {\n" +
                "          \"AIRTString\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTString\"\n" +
                "          },\n" +
                "          \"AIRTCollStringTwoParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTCollStringTwoParam\"\n" +
                "          },\n" +
                "          \"AIRTCTTwoPrimParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTCTTwoPrimParam\"\n" +
                "          },\n" +
                "          \"AIRTCollCTTwoPrimParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTCollCTTwoPrimParam\"\n" +
                "          },\n" +
                "          \"AIRTETTwoKeyTwoPrimParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTETTwoKeyTwoPrimParam\"\n" +
                "          },\n" +
                "          \"AIRTCollETKeyNavParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTCollETKeyNavParam\"\n" +
                "          },\n" +
                "          \"AIRTESAllPrimParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTETAllPrimParam\"\n" +
                "          },\n" +
                "          \"AIRTCollESAllPrimParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTCollETAllPrimParam\"\n" +
                "          },\n" +
                "          \"AIRT\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UART\"\n" +
                "          },\n" +
                "          \"AIRTParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTParam\"\n" +
                "          },\n" +
                "          \"AIRTTwoParam\" : {\n" +
                "            \"action\" : \"Namespace1_Alias.UARTTwoParam\"\n" +
                "          }\n" +
                "        }";
        assertThat(metadata, containsString(expectedString));


        expectedString=
        "\"functionImports\" : {\n" +
                "          \"FINRTInt16\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFNRTInt16\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FINInvisibleRTInt16\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFNRTInt16\"\n" +
                "          },\n" +
                "          \"FINInvisible2RTInt16\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFNRTInt16\"\n" +
                "          },\n" +
                "          \"FICRTETKeyNav\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTETKeyNav\"\n" +
                "          },\n" +
                "          \"FICRTESTwoKeyNav\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTETTwoKeyNav\",\n" +
                "            \"entitySet\" : \"Namespace1_Alias.ESTwoKeyNav\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTETTwoKeyNavParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTETTwoKeyNavParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTStringTwoParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTStringTwoParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCollStringTwoParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCollStringTwoParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCTAllPrimTwoParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCTAllPrimTwoParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTESMixPrimCollCompTwoParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTESMixPrimCollCompTwoParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCollETMixPrimCollCompTwoParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCollETMixPrimCollCompTwoParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FINRTCollETMixPrimCollCompTwoParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFNRTCollETMixPrimCollCompTwoParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCollCTTwoPrim\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCollCTTwoPrim\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTESMedia\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTETMedia\",\n" +
                "            \"entitySet\" : \"Namespace1_Alias.ESMedia\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCollESMedia\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCollETMedia\",\n" +
                "            \"entitySet\" : \"Namespace1_Alias.ESMedia\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCTTwoPrimParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCTTwoPrimParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCTTwoPrim\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCTTwoPrim\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCollString\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCollString\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTString\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTString\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCollESTwoKeyNavParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCollETTwoKeyNavParam\",\n" +
                "            \"entitySet\" : \"Namespace1_Alias.ESTwoKeyNav\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCollCTTwoPrimParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCollCTTwoPrimParam\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FINRTCollCTNavFiveProp\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFNRTCollCTNavFiveProp\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          },\n" +
                "          \"FICRTCollESKeyNavContParam\" : {\n" +
                "            \"function\" : \"Namespace1_Alias.UFCRTCollETKeyNavContParam\",\n" +
                "            \"entitySet\" : \"Namespace1_Alias.ESKeyNavCont\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          }\n" +
                "        }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "\"ESTwoPrim\" : {\n" +
                "            \"entityType\" : \"Namespace1_Alias.ETTwoPrim\",\n" +
                "            \"navigationPropertyBindings\" : {\n" +
                "              \"NavPropertyETAllPrimOne\" : {\n" +
                "                \"target\" : \"ESAllPrim\"\n" +
                "              },\n" +
                "              \"NavPropertyETAllPrimMany\" : {\n" +
                "                \"target\" : \"ESAllPrim\"\n" +
                "              }\n" +
                "            }\n" +
                "          }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        " {\n" +
                "        \"name\" : \"UFCRTCollStringTwoParam\",\n" +
                "        \"isComposable\" : true,\n" +
                "        \"parameters\" : {\n" +
                "          \"ParameterString\" : {\n" +
                "            \"type\" : \"Edm.String\",\n" +
                "            \"nullable\" : false\n" +
                "          },\n" +
                "          \"ParameterInt16\" : {\n" +
                "            \"type\" : \"Edm.Int16\",\n" +
                "            \"nullable\" : false\n" +
                "          }\n" +
                "        },\n" +
                "        \"returnType\" : {\n" +
                "          \"type\" : \"Collection(Edm.String)\",\n" +
                "          \"nullable\" : false\n" +
                "        }\n" +
                "      }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "{\n" +
                "        \"name\" : \"BAESTwoKeyNavRTESKeyNav\",\n" +
                "        \"entitySetPath\" : \"BindingParam/NavPropertyETKeyNavMany\",\n" +
                "        \"isBound\" : true,\n" +
                "        \"parameters\" : {\n" +
                "          \"ParameterETTwoKeyNav\" : {\n" +
                "            \"type\" : \"Collection(Namespace1_Alias.ETTwoKeyNav)\",\n" +
                "            \"nullable\" : false\n" +
                "          }\n" +
                "        },\n" +
                "        \"returnType\" : {\n" +
                "          \"type\" : \"Collection(Namespace1_Alias.ETKeyNav)\"\n" +
                "        }\n" +
                "      }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "\"olingo.odata.test1.CTBasePrimCompNav\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"allOf\" : [ {\n" +
                "        \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/olingo.odata.test1.CTPrimComp\"\n" +
                "      } ],\n" +
                "      \"properties\" : {\n" +
                "        \"NavPropertyETTwoKeyNavMany\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"anyOf\" : [ {\n" +
                "              \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/olingo.odata.test1.ETTwoKeyNav\"\n" +
                "            }, {\n" +
                "              \"type\" : null\n" +
                "            } ]\n" +
                "          },\n" +
                "          \"relationship\" : {\n" +
                "            \"partner\" : \"NavPropertyETKeyNavOne\",\n" +
                "            \"referentialConstraints\" : { }\n" +
                "          }\n" +
                "        },\n" +
                "        \"NavPropertyETTwoKeyNavOne\" : {\n" +
                "          \"anyOf\" : [ {\n" +
                "            \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/olingo.odata.test1.ETTwoKeyNav\"\n" +
                "          }, {\n" +
                "            \"type\" : null\n" +
                "          } ],\n" +
                "          \"relationship\" : {\n" +
                "            \"referentialConstraints\" : { }\n" +
                "          }\n" +
                "        },\n" +
                "        \"NavPropertyETKeyNavOne\" : {\n" +
                "          \"anyOf\" : [ {\n" +
                "            \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/olingo.odata.test1.ETKeyNav\"\n" +
                "          }, {\n" +
                "            \"type\" : null\n" +
                "          } ],\n" +
                "          \"relationship\" : {\n" +
                "            \"referentialConstraints\" : { }\n" +
                "          }\n" +
                "        },\n" +
                "        \"NavPropertyETKeyNavMany\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"anyOf\" : [ {\n" +
                "              \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/olingo.odata.test1.ETKeyNav\"\n" +
                "            }, {\n" +
                "              \"type\" : null\n" +
                "            } ]\n" +
                "          },\n" +
                "          \"relationship\" : {\n" +
                "            \"referentialConstraints\" : { }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "\"olingo.odata.test1.ETMedia\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"hasStream\" : true,\n" +
                "      \"keys\" : [ {\n" +
                "        \"name\" : \"PropertyInt16\"\n" +
                "      } ],\n" +
                "      \"properties\" : {\n" +
                "        \"PropertyInt16\" : {\n" +
                "          \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/Edm.Int16\"\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "\"PropertyDecimal\" : {\n" +
                "          \"anyOf\" : [ {\n" +
                "            \"type\" : \"number\",\n" +
                "            \"multipleOf\" : 1.0E-5,\n" +
                "            \"minimum\" : -999999.99999,\n" +
                "            \"maximum\" : 999999.99999\n" +
                "          }, {\n" +
                "            \"type\" : null\n" +
                "          } ]\n" +
                "        }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "\"CollPropertyDateTimeOffset\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"anyOf\" : [ {\n" +
                "              \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/Edm.DateTimeOffset\"\n" +
                "            }, {\n" +
                "              \"pattern\" : \"^[^.]*$\"\n" +
                "            } ]\n" +
                "          }\n" +
                "        }";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "\"entityContainer\" : {\n" +
                "        \"name\" : \"Container\",";
        assertThat(metadata, containsString(expectedString));

        expectedString=
        "\"olingo.odata.test1.ETTwoBase\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"allOf\" : [ {\n" +
                "        \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/olingo.odata.test1.ETBase\"\n" +
                "      } ],\n" +
                "      \"properties\" : {\n" +
                "        \"AdditionalPropertyString_6\" : {\n" +
                "          \"type\" : [ \"string\", null ]\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        assertThat(metadata, containsString(expectedString));

    }


    private List<EdmxReference> getEdmxReferences() {
        EdmxReference reference = new EdmxReference(URI.create(CORE_VOCABULARY));
        reference.addInclude(new EdmxReferenceInclude("Org.OData.Core.V1", "Core"));
        return Arrays.asList(reference);
    }
}
