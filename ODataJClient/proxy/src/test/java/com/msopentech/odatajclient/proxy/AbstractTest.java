/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.proxy;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.msopentech.odatajclient.proxy.api.EntityContainerFactory;
import com.msopentech.odatajclient.proxy.api.context.EntityContext;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.DefaultContainer;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractTest.class);

    protected static final String TEST_PRODUCT_TYPE = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product";

    protected static final String servicesODataServiceRootURL =
            "http://services.odata.org/V3/(S(csquyjnoaywmz5xcdbfhlc1p))/OData/OData.svc/";

    protected static String testDefaultServiceRootURL;

    protected static String testActionOverloadingServiceRootURL;

    protected static String testKeyAsSegmentServiceRootURL;

    protected static String testODataWriterDefaultServiceRootURL;

    protected static String testOpenTypesServiceRootURL;

    protected static String testPrimitiveKeysServiceRootURL;

    protected static String testOpenTypeServiceRootURL;

    protected static String testLargeModelServiceRootURL;

    protected static String testAuthServiceRootURL;

    protected final EntityContext entityContext = EntityContainerFactory.getContext().entityContext();

    protected static EntityContainerFactory containerFactory;

    protected static DefaultContainer container;

    /**
     * This is needed for correct number handling (Double, for example).
     */
    @BeforeClass
    public static void setEnglishLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @BeforeClass
    public static void setUpODataServiceRoot() throws IOException {
        String testBaseURL = null;

        InputStream propStream = null;
        try {
            propStream = AbstractTest.class.getResourceAsStream("/test.properties");
            final Properties props = new Properties();
            props.load(propStream);

            testBaseURL = props.getProperty("test.base.url");
        } catch (Exception e) {
            LOG.error("Could not load test.properties", e);
        } finally {
            if (propStream != null) {
                propStream.close();
            }
        }
        assertNotNull("Check value for the 'test.base.url' property", testBaseURL);

        testDefaultServiceRootURL = testBaseURL + "/DefaultService.svc";
        testActionOverloadingServiceRootURL = testBaseURL + "/ActionOverloadingService.svc";
        testKeyAsSegmentServiceRootURL = testBaseURL + "/KeyAsSegmentService.svc";
        testODataWriterDefaultServiceRootURL = testBaseURL + "/ODataWriterDefaultService.svc";
        testOpenTypeServiceRootURL = testBaseURL + "/OpenTypeService.svc";
        testPrimitiveKeysServiceRootURL = testBaseURL + "/PrimitiveKeys.svc";
        testLargeModelServiceRootURL = testBaseURL + "/LargeModelService.svc";
        testAuthServiceRootURL = "http://localhost:9080/DefaultService.svc";

        containerFactory = EntityContainerFactory.getV3Instance(testDefaultServiceRootURL);
        container = containerFactory.getEntityContainer(DefaultContainer.class);
        assertNotNull(container);
    }

    protected Customer getSampleCustomerProfile(
            final Integer id,
            final String sampleName,
            final DefaultContainer container) {

        final Customer customer = container.getCustomer().newCustomer();

        // add name attribute
        customer.setName(sampleName);

        // add key attribute
        customer.setCustomerId(id);

        final ContactDetails cd = new ContactDetails();
        cd.setAlternativeNames(Arrays.asList("alternative1", "alternative2"));
        cd.setEmailBag(Collections.<String>singleton("myname@mydomain.org"));
        cd.setMobilePhoneBag(Collections.<Phone>emptySet());

        final Aliases aliases = new Aliases();
        aliases.setAlternativeNames(Collections.<String>singleton("myAlternativeName"));
        cd.setContactAlias(aliases);

        final ContactDetails bcd = new ContactDetails();
        bcd.setAlternativeNames(Arrays.asList("alternative3", "alternative4"));
        bcd.setEmailBag(Collections.<String>emptySet());
        bcd.setMobilePhoneBag(Collections.<Phone>emptySet());

        customer.setPrimaryContactInfo(cd);
        customer.setBackupContactInfo(Collections.<ContactDetails>singletonList(bcd));

        return customer;
    }

    protected void checKSampleCustomerProfile(
            final Customer customer,
            final Integer id,
            final String sampleName) {

        assertEquals(id, customer.getCustomerId());
        assertNotNull(customer.getPrimaryContactInfo());
        assertFalse(customer.getBackupContactInfo().isEmpty());

        final ContactDetails cd = customer.getPrimaryContactInfo();
        final ContactDetails bcd = customer.getBackupContactInfo().iterator().next();

        assertTrue(cd.getAlternativeNames().contains("alternative1"));
        assertTrue(cd.getAlternativeNames().contains("alternative2"));
        assertEquals("myname@mydomain.org", cd.getEmailBag().iterator().next());
        assertEquals("myAlternativeName", cd.getContactAlias().getAlternativeNames().iterator().next());
        assertTrue(cd.getMobilePhoneBag().isEmpty());

        assertTrue(bcd.getAlternativeNames().contains("alternative3"));
        assertTrue(bcd.getAlternativeNames().contains("alternative4"));
        assertTrue(bcd.getEmailBag().isEmpty());
        assertTrue(bcd.getMobilePhoneBag().isEmpty());
    }

    protected Customer readCustomer(final DefaultContainer container, int id) {
        final Customer customer = container.getCustomer().get(id);
        assertNotNull(customer);
        assertEquals(Integer.valueOf(id), customer.getCustomerId());

        return customer;
    }
}
