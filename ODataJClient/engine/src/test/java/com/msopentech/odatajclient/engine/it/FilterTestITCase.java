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
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.*;
import org.junit.Test;

import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.uri.URIBuilder;

public class FilterTestITCase extends AbstractTestITCase {
    // filter test

    private void filterQueryTest(final String entity, final String filter, final int expected) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment(entity).filter(filter);
        final ODataEntitySet entitySet = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build()).
                execute().getBody();
        assertNotNull(entitySet);
        assertEquals(expected, entitySet.getEntities().size());
    }

    @Test
    public void withId() {
        filterQueryTest("Customer", "CustomerId eq -10", 1);
    }
    //logical operations

    @Test
    public void logical() {
        filterQueryTest("Customer", "CustomerId gt -10", 2);
        filterQueryTest("Customer", "CustomerId lt -10", 0);
        filterQueryTest("Customer", "not endswith(Name,'Chandan')", 2);
        filterQueryTest("Car", "VIN le 18 and VIN gt 12", 6);
    }
    //arithmetic operations

    @Test
    public void arithmetic() {
        filterQueryTest("Car", "VIN add 5 lt 11", 0);
        filterQueryTest("Car", "VIN div 2 le 8", 7);
        filterQueryTest("Car", "VIN mul 2 le 30", 5);
        filterQueryTest("Person", "PersonId sub 2 lt -10", 2);
    }
    //string operations

    @Test
    public void stringOperations() {
        filterQueryTest("Product", "length(Description) eq 7", 1);
        filterQueryTest("Product", "length(Description) eq 7", 1);
        filterQueryTest("Product", "substringof('kdcuklu', Description) eq true", 1);
        filterQueryTest("Product", "startswith(Description, 'k') eq true", 2);
        filterQueryTest("Product", "startswith(Description, 'k') eq true", 2);
        filterQueryTest("Product", "indexof(Description, 'k') eq 0", 2);
        filterQueryTest("Product", "toupper(Description) eq 'KDCUKLU'", 1);
        filterQueryTest("Product", "concat(Description, ', newname') eq 'kdcuklu, newname' ", 1);
    }
    //math operations

    @Test
    public void math() {
        filterQueryTest("Product", "round(Dimensions/Width) eq 7338", 1);
        filterQueryTest("Product", "round(Dimensions/Width) eq 7338", 1);
        filterQueryTest("Product", "floor(Dimensions/Width) eq 7337", 1);
        filterQueryTest("Product", "ceiling(Dimensions/Width) eq 7338", 1);
    }
    //date operations

    @Test
    public void date() {
        filterQueryTest("ComputerDetail", "day(PurchaseDate) eq 15", 0);
        filterQueryTest("ComputerDetail", "month(PurchaseDate) eq 12", 1);
        filterQueryTest("ComputerDetail", "hour(PurchaseDate) eq 1", 0);
        filterQueryTest("ComputerDetail", "minute(PurchaseDate) eq 33", 0);
        filterQueryTest("ComputerDetail", "second(PurchaseDate) eq 35", 0);
        filterQueryTest("ComputerDetail", "year(PurchaseDate) eq 2020", 0);
    }
    //isOf test

    @Test
    public void isOfTest() {
        filterQueryTest("Customer", "isof(Name,'Edm.String') eq true", 2);
    }
}
