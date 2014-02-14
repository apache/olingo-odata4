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
package com.msopentech.odatajclient.engine.uri;

/**
 * Query options.
 */
public enum QueryOption {

    /**
     * This option indicates entities associated with the EntityType
     * instance or EntitySet, identified by the resource path section of
     * the URI, and MUST be represented inline in the data service's
     * response.
     */
    EXPAND("expand"),
    /**
     * This option specifies the media type acceptable in a response. If
     * present, this value SHOULD take precedence over value(s)
     * specified in an Accept request header.
     */
    FORMAT("format"),
    /**
     * This option is used to specify that a subset of the properties of
     * the entities identified by the path of the request URI and
     * $expand query option SHOULD be returned in the response
     * from the data service.
     */
    SELECT("select"),
    /**
     * This option specifies the sort properties and sort direction
     * (ascending or descending) that the data service MUST use to
     * order the entities in the EntitySet, identified by the resource
     * path section of the URI.
     */
    ORDERBY("orderby"),
    /**
     * This option specifies a positive integer N that is the maximum
     * number of entities in the EntitySet, identified by the resource
     * path section of the URI, that the data service MUST return.
     */
    TOP("top"),
    /**
     * This option specifies a positive integer N that represents the
     * number of entities, counted from the first entity in the
     * EntitySet and ordered as specified by the $orderby option,
     * that the data service should skip when returning the entities in
     * the EntitySet, which is identified by the resource path section
     * of the URI. The data service SHOULD return all subsequent
     * entities, starting from the one in position N+1.
     */
    SKIP("skip"),
    /**
     * This query option applies only to the OData 2.0 protocol to the AtomPub protocol.
     * The value of a $skiptoken query option is an opaque token
     * which identifies an index into the collection of entities identified
     * by the URI containing the $skiptoken parameter.
     */
    SKIPTOKEN("skiptoken"),
    /**
     * This option specifies a predicate used to filter the elements from
     * the EntitySet identified by the resource path section of the URI.
     */
    FILTER("filter"),
    /**
     * For a value of "allpages", this option indicates that the response
     * to the request MUST include the count of the number of entities
     * in the EntitySet, identified by the resource path section of the
     * URI after all $filter system query options have been applied.
     * For a value of "none", this option indicates that the response to
     * the request MUST NOT include the count value.
     */
    INLINECOUNT("inlinecount");

    final String option;

    QueryOption(final String option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return option;
    }
}
