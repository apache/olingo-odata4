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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.serialization.ODataMetadataValidation;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class MetadataValidationTest extends AbstractTest {
  public static final String wrongBindingTarget = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" "
      + "xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\"  m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\"  "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"User\"><Key><PropertyRef Name=\"Id\" />"
      + "</Key><Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\" />"
      + "<NavigationProperty Name=\"Messages\"  "
      + "Type=\"Collection(Microsoft.Exchange.Services.OData.Model.EmailMessage)\" />"
      + "</EntityType>"
      + "<EntityType Name=\"Folder\">"
      + "<Key><PropertyRef Name=\"Id\" /></Key>"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\" />"
      + "<Property Name=\"ParentFolderId\" Type=\"Edm.String\" />"
      + "</EntityType>"
      + "<EntityType Name=\"EmailMessage\">"
      + "<Key><PropertyRef Name=\"Id\" /></Key>"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\" />"
      + "</EntityType>"
      + "<EntityContainer Name=\"EntityContainer\"  m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Users\" EntityType=\"Microsoft.Exchange.Services.OData.Model.User\">"
      + "<NavigationPropertyBinding Path=\"Messages\"  Target=\"Folders\" />"
      + "</EntitySet>"
      + "<EntitySet Name=\"Folders\"  EntityType=\"Microsoft.Exchange.Services.OData.Model.Folder\" />"
      + "</EntityContainer>"
      + "</Schema>"
      + "</edmx:DataServices>"
      + "</edmx:Edmx>";
  
  public static final String xmlWithNonKeyEntity = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\"  "
      + "m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\"  "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"Entity\" Abstract=\"true\">"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\" />"
      + "</EntityType>"
      + "<EntityType Name=\"User\" BaseType=\"Microsoft.Exchange.Services.OData.Model.Entity\">"
      + "</EntityType><EntityContainer Name=\"EntityContainer\"  m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Users\" EntityType=\"Microsoft.Exchange.Services.OData.Model.User\" />"
      + "</EntityContainer>"
      + "</Schema>"
      + "</edmx:DataServices>"
      + "</edmx:Edmx>";
  
  public static final String xmlWithWrongBindingTarget = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"Customer\" Abstract=\"true\">"
      + "<Key><PropertyRef Name=\"CustomerId\" /></Key>"
      + "<Property Name=\"CustomerId\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CustomerName\" Type=\"Edm.String\" />"
      + "<Property Name=\"Pet\" Type=\"Microsoft.Exchange.Services.OData.Model.Animal\" />"
      + "</EntityType><EntityType Name=\"City\">"
      + "<Key><PropertyRef Name=\"Id\" /></Key>"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "</EntityType><ComplexType Name=\"Animal\">"
      + "</ComplexType><ComplexType Name=\"Human\" "
      + "BaseType=\"Microsoft.Exchange.Services.OData.Model.Animal\">"
      + "<Property Name=\"HumanAddress\" Type=\"Microsoft.Exchange.Services.OData.Model.USAddress\" />"
      + "</ComplexType>"
      + "<ComplexType Name=\"Address\">"
      + "<NavigationProperty Name=\"City\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<ComplexType Name=\"USAddress\" "
      + "BaseType=\"Microsoft.Exchange.Services.OData.Model.Address\">"
      + "<NavigationProperty Name=\"SubCity\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<EntityContainer Name=\"EntityContainer\" m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Customers\" EntityType=\"Microsoft.Exchange.Services.OData.Model.Customer\">"
      + "<NavigationPropertyBinding "
      + "Path=\"Pet/Microsoft.Exchange.Services.OData.Model.Human/HumanAddress/SubCity\" "
      + "Target=\"HumanCities\" />"
      + "</EntitySet>"
      + "</EntityContainer>"
      + "</Schema>"
      + "<Schema Namespace=\"ODataWebExperimental.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityContainer Name=\"EntityContainer1\" p4:LazyLoadingEnabled=\"true\" "
      + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
      + "<EntitySet Name=\"HumanCities\" EntityType=\"Microsoft.Exchange.Services.OData.Model.City\"/>"
      + "</EntityContainer>"
      + "</Schema>"
      + "</edmx:DataServices>"
      + "</edmx:Edmx>";
  
  public static final String xmlWithWrongBindingTarget1 = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"Customer\" Abstract=\"true\">"
      + "<Key><PropertyRef Name=\"CustomerId\" /></Key>"
      + "<Property Name=\"CustomerId\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CustomerName\" Type=\"Edm.String\" />"
      + "<Property Name=\"Pet\" Type=\"Microsoft.Exchange.Services.OData.Model.Animal\" />"
      + "</EntityType><EntityType Name=\"City\">"
      + "<Key><PropertyRef Name=\"Id\" /></Key>"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "</EntityType><ComplexType Name=\"Animal\">"
      + "</ComplexType><ComplexType Name=\"Human\" "
      + "BaseType=\"Microsoft.Exchange.Services.OData.Model.Animal\">"
      + "<Property Name=\"HumanAddress\" Type=\"Microsoft.Exchange.Services.OData.Model.USAddress\" />"
      + "</ComplexType>"
      + "<ComplexType Name=\"Address\">"
      + "<NavigationProperty Name=\"City\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<ComplexType Name=\"USAddress\" "
      + "BaseType=\"Microsoft.Exchange.Services.OData.Model.Address\">"
      + "<NavigationProperty Name=\"SubCity\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<EntityContainer Name=\"EntityContainer\" m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Customers\" EntityType=\"Microsoft.Exchange.Services.OData.Model.Customer\">"
      + "<NavigationPropertyBinding "
      + "Path=\"Pet/Microsoft.Exchange.Services.OData.Model.Human/HumanAddress/SubCity\" "
      + "Target=\"ODataWebExperimental.OData.Model.EntityContainer1/HuCities\" />"
      + "</EntitySet>"
      + "</EntityContainer>"
      + "</Schema>"
      + "<Schema Namespace=\"ODataWebExperimental.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityContainer Name=\"EntityContainer1\" p4:LazyLoadingEnabled=\"true\" "
      + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
      + "<EntitySet Name=\"HumanCities\" EntityType=\"Microsoft.Exchange.Services.OData.Model.City\"/>"
      + "</EntityContainer>"
      + "</Schema>"
      + "</edmx:DataServices>"
      + "</edmx:Edmx>";
  
  public static final String xmlWithNonKeyEntity1 = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\"  "
      + "m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\"  "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"User\">"
      + "</EntityType><EntityContainer Name=\"EntityContainer\"  m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Users\" EntityType=\"Microsoft.Exchange.Services.OData.Model.User\" />"
      + "</EntityContainer>"
      + "</Schema>"
      + "</edmx:DataServices>"
      + "</edmx:Edmx>";

  public static final String xmlWithWrongNamespaceInBindingTarget = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"Customer\" Abstract=\"true\">"
      + "<Key><PropertyRef Name=\"CustomerId\" /></Key>"
      + "<Property Name=\"CustomerId\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CustomerName\" Type=\"Edm.String\" />"
      + "<Property Name=\"Pet\" Type=\"Microsoft.Exchange.Services.OData.Model.Animal\" />"
      + "</EntityType><EntityType Name=\"City\">"
      + "<Key><PropertyRef Name=\"Id\" /></Key>"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "</EntityType><ComplexType Name=\"Animal\">"
      + "</ComplexType><ComplexType Name=\"Human\" "
      + "BaseType=\"Microsoft.Exchange.Services.OData.Model.Animal\">"
      + "<Property Name=\"HumanAddress\" Type=\"Microsoft.Exchange.Services.OData.Model.USAddress\" />"
      + "</ComplexType>"
      + "<ComplexType Name=\"Address\">"
      + "<NavigationProperty Name=\"City\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<ComplexType Name=\"USAddress\" "
      + "BaseType=\"Microsoft.Exchange.Services.OData.Model.Address\">"
      + "<NavigationProperty Name=\"SubCity\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<EntityContainer Name=\"EntityContainer\" m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Customers\" EntityType=\"Microsoft.Exchange.Services.OData.Model.Customer\">"
      + "<NavigationPropertyBinding "
      + "Path=\"Pet/Microsoft.Exchange.Services.OData.Model.Human/HumanAddress/SubCity\" "
      + "Target=\"Model.EntityContainer1/HumanCities\" />"
      + "</EntitySet>"
      + "</EntityContainer>"
      + "</Schema>"
      + "<Schema Namespace=\"ODataWebExperimental.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityContainer Name=\"EntityContainer1\" p4:LazyLoadingEnabled=\"true\" "
      + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
      + "<EntitySet Name=\"HumanCities\" EntityType=\"Microsoft.Exchange.Services.OData.Model.City\"/>"
      + "</EntityContainer>"
      + "</Schema>"
      + "</edmx:DataServices>"
      + "</edmx:Edmx>";

  public static final String xmlWithWrongNamespaceInBindingPath = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"Customer\" Abstract=\"true\">"
      + "<Key><PropertyRef Name=\"CustomerId\" /></Key>"
      + "<Property Name=\"CustomerId\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CustomerName\" Type=\"Edm.String\" />"
      + "<Property Name=\"Pet\" Type=\"Microsoft.Exchange.Services.OData.Model.Animal\" />"
      + "</EntityType><EntityType Name=\"City\">"
      + "<Key><PropertyRef Name=\"Id\" /></Key>"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "</EntityType><ComplexType Name=\"Animal\">"
      + "</ComplexType><ComplexType Name=\"Human\" "
      + "BaseType=\"Microsoft.Exchange.Services.OData.Model.Animal\">"
      + "<Property Name=\"HumanAddress\" Type=\"Microsoft.Exchange.Services.OData.Model.USAddress\" />"
      + "</ComplexType>"
      + "<ComplexType Name=\"Address\">"
      + "<NavigationProperty Name=\"City\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<ComplexType Name=\"USAddress\" "
      + "BaseType=\"Microsoft.Exchange.Services.OData.Model.Address\">"
      + "<NavigationProperty Name=\"SubCity\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<EntityContainer Name=\"EntityContainer\" m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Customers\" EntityType=\"Microsoft.Exchange.Services.OData.Model.Customer\">"
      + "<NavigationPropertyBinding "
      + "Path=\"Pet/OData.Model.Human/HumanAddress/SubCity\" "
      + "Target=\"ODataWebExperimental.OData.Model.EntityContainer1/HumanCities\" />"
      + "</EntitySet>"
      + "</EntityContainer>"
      + "</Schema>"
      + "<Schema Namespace=\"ODataWebExperimental.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityContainer Name=\"EntityContainer1\" p4:LazyLoadingEnabled=\"true\" "
      + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
      + "<EntitySet Name=\"HumanCities\" EntityType=\"Microsoft.Exchange.Services.OData.Model.City\"/>"
      + "</EntityContainer>"
      + "</Schema>"
      + "</edmx:DataServices>"
      + "</edmx:Edmx>";
  
  public static final String xmlWithInvalidEntityTypeNamespace = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"Customer\" Abstract=\"true\">"
      + "<Key><PropertyRef Name=\"CustomerId\" /></Key>"
      + "<Property Name=\"CustomerId\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CustomerName\" Type=\"Edm.String\" />"
      + "<Property Name=\"Pet\" Type=\"Microsoft.Exchange.Services.OData.Model.Animal\" />"
      + "</EntityType>"
      + "<EntityType Name=\"City\">"
      + "<Key><PropertyRef Name=\"Id\" /></Key>"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "</EntityType>"
      + "<ComplexType Name=\"Animal\">"
      + "</ComplexType>"
      + "<ComplexType Name=\"Human\" BaseType=\"Microsoft.Exchange.Services.OData.Model.Animal\">"
      + "<Property Name=\"HumanAddress\" Type=\"Microsoft.Exchange.Services.OData.Model.USAddress\"/>"
      + "</ComplexType>"
      + "<ComplexType Name=\"Address\">"
      + "<NavigationProperty Name=\"City\" Type=\"Microsoft.Exchange.Services.OData.Model.City\"/>"
      + "</ComplexType>"
      + "<ComplexType Name=\"USAddress\" BaseType=\"Microsoft.Exchange.Services.OData.Model.Address\">"
      + "<NavigationProperty Name=\"SubCity\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType>"
      + "<EntityContainer Name=\"EntityContainer\" m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Customers\" EntityType=\"OData.Model.Customer\">"
      + "<NavigationPropertyBinding "
      + "Path=\"Pet/Microsoft.Exchange.Services.OData.Model.Human/HumanAddress/SubCity\" "
      + "Target=\"ODataWebExperimental.OData.Model.EntityContainer1/HumanCities\" />"
      + "</EntitySet>"
      + "</EntityContainer>"
      + "</Schema>"
      + "<Schema Namespace=\"ODataWebExperimental.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityContainer Name=\"EntityContainer1\" p4:LazyLoadingEnabled=\"true\" "
      + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
      + "<EntitySet Name=\"HumanCities\" EntityType=\"Microsoft.Exchange.Services.OData.Model.City\"/>"
      + "</EntityContainer>"
      + "</Schema>"
      + "</edmx:DataServices>"
      + "</edmx:Edmx>";
  
  public static final String xmlWithWrongComplexBaseType = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityType Name=\"Customer\" Abstract=\"true\">"
      + "<Key><PropertyRef Name=\"CustomerId\" /></Key>"
      + "<Property Name=\"CustomerId\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CustomerName\" Type=\"Edm.String\" />"
      + "<Property Name=\"Pet\" Type=\"Microsoft.Exchange.Services.OData.Model.Animal\" />"
      + "</EntityType>"
      + "<EntityType Name=\"City\">"
      + "<Key><PropertyRef Name=\"Id\" /></Key>"
      + "<Property Name=\"Id\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "</EntityType>"
      + "<ComplexType Name=\"Animal\">"
      + "</ComplexType><ComplexType Name=\"Human\" BaseType=\"OData.Model.Animal\">"
      + "<Property Name=\"HumanAddress\" Type=\"Microsoft.Exchange.Services.OData.Model.USAddress\"/>"
      + "</ComplexType>"
      + "<ComplexType Name=\"Address\">"
      + "<NavigationProperty Name=\"City\" Type=\"Microsoft.Exchange.Services.OData.Model.City\"/>"
      + "</ComplexType>"
      + "<ComplexType Name=\"USAddress\" BaseType=\"Microsoft.Exchange.Services.OData.Model.Address\">"
      + "<NavigationProperty Name=\"SubCity\" Type=\"Microsoft.Exchange.Services.OData.Model.City\" />"
      + "</ComplexType><EntityContainer Name=\"EntityContainer\" "
      + "m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"Customers\" EntityType=\"Microsoft.Exchange.Services.OData.Model.Customer\">"
      + "<NavigationPropertyBinding "
      + "Path=\"Pet/Microsoft.Exchange.Services.OData.Model.Human/HumanAddress/SubCity\" "
      + "Target=\"ODataWebExperimental.OData.Model.EntityContainer1/HumanCities\" />"
      + "</EntitySet>"
      + "</EntityContainer></Schema><Schema Namespace=\"ODataWebExperimental.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">"
      + "<EntityContainer Name=\"EntityContainer1\" p4:LazyLoadingEnabled=\"true\" "
      + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
      + "<EntitySet Name=\"HumanCities\" EntityType=\"Microsoft.Exchange.Services.OData.Model.City\"/>"
      + "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
  
  public static final String xmlWithBaseEntityAsBindingTarget = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<EntityType Name=\"ETTwoKeyNav\">"
      + "<Key><PropertyRef Name=\"PropertyInt16\"/>"
      + "<PropertyRef Name=\"PropertyString\"/>"
      + "</Key>"
      + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
      + "<Property Name=\"PropertyString\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "</EntityType>"
      + "<EntityType Name=\"ETBaseTwoKeyNav\" BaseType=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<NavigationProperty Name=\"NavPropertyETTwoBaseTwoKeyNavOne\" "
      + "Type=\"Namespace1_Alias.ETTwoBaseTwoKeyNav\"/>"
      + "</EntityType>"
      + "<EntityType Name=\"ETTwoBaseTwoKeyNav\" BaseType=\"Namespace1_Alias.ETBaseTwoKeyNav\">"
      + "</EntityType><EntityContainer Name=\"EntityContainer\" m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"ESTwoKeyNav\" EntityType=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<NavigationPropertyBinding Path=\"Namespace1_Alias.ETBaseTwoKeyNav/NavPropertyETTwoBaseTwoKeyNavOne\""
      + " Target=\"ESBaseTwoKeyNav\"/>"
      + "</EntitySet>"
      + "<EntitySet Name=\"ESBaseTwoKeyNav\" EntityType=\"Namespace1_Alias.ETBaseTwoKeyNav\"/>"
      + "<EntitySet Name=\"ESTwoBaseTwoKeyNav\" EntityType=\"Namespace1_Alias.ETTwoBaseTwoKeyNav\"/>"
      + "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
  
  public static final String basicXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<EntityType Name=\"ETTwoKeyNav\">"
      + "<Key><PropertyRef Name=\"PropertyInt16\"/>"
      + "<PropertyRef Name=\"PropertyString\"/></Key>"
      + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
      + "<Property Name=\"PropertyString\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CollPropertyCompNav\" Type=\"Collection(Namespace1_Alias.CTNavFiveProp)\"/>"
      + "</EntityType><ComplexType Name=\"CTNavFiveProp\">"
      + "<NavigationProperty Name=\"NavPropertyETTwoKeyNavOne\" Type=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<ReferentialConstraint Property=\"PropertyInt16\" ReferencedProperty=\"PropertyInt16\"/>"
      + "</NavigationProperty>"
      + "</ComplexType><EntityContainer Name=\"EntityContainer\" "
      + "m:IsDefaultEntityContainer=\"true\"><EntitySet Name=\"ESTwoKeyNav\" "
      + "EntityType=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<NavigationPropertyBinding Path=\"CollPropertyCompNav/NavPropertyETTwoKeyNavOne\" "
      + "Target=\"ESTwoKeyNav\"/>"
      + "</EntitySet>"
      + "</EntityContainer></Schema>"
      + "</edmx:DataServices></edmx:Edmx>";
  
  public static final String basicXmlWithSingleton = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<EntityType Name=\"ETTwoKeyNav\">"
      + "<Key><PropertyRef Name=\"PropertyInt16\"/>"
      + "<PropertyRef Name=\"PropertyString\"/></Key>"
      + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
      + "<Property Name=\"PropertyString\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CollPropertyCompNav\" Type=\"Collection(Namespace1_Alias.CTNavFiveProp)\"/>"
      + "<NavigationProperty Name=\"NavPropertySINav\" Type=\"Namespace1_Alias.ETTwoKeyNav\"/>"
      + "</EntityType><ComplexType Name=\"CTNavFiveProp\">"
      + "<NavigationProperty Name=\"NavPropertyETTwoKeyNavOne\" Type=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<ReferentialConstraint Property=\"PropertyInt16\" ReferencedProperty=\"PropertyInt16\"/>"
      + "</NavigationProperty>"
      + "</ComplexType><EntityContainer Name=\"EntityContainer\" "
      + "m:IsDefaultEntityContainer=\"true\"><EntitySet Name=\"ESTwoKeyNav\" "
      + "EntityType=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<NavigationPropertyBinding Path=\"CollPropertyCompNav/NavPropertyETTwoKeyNavOne\" "
      + "Target=\"ESTwoKeyNav\"/>"
      + "<NavigationPropertyBinding Path=\"NavPropertySINav\" Target=\"SINav\"/>"
      + "</EntitySet><Singleton Name=\"SINav\" EntityType=\"Namespace1_Alias.ETTwoKeyNav\"/>"
      + "</EntityContainer></Schema>"
      + "</edmx:DataServices></edmx:Edmx>";
  
  public static final String xmlWithIncorrectReferentialConstraint1 = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<EntityType Name=\"ETTwoKeyNav\">"
      + "<Key><PropertyRef Name=\"PropertyInt16\"/>"
      + "<PropertyRef Name=\"PropertyString\"/></Key>"
      + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
      + "<Property Name=\"PropertyString\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CollPropertyCompNav\" Type=\"Collection(Namespace1_Alias.CTNavFiveProp)\"/>"
      + "</EntityType><ComplexType Name=\"CTNavFiveProp\">"
      + "<NavigationProperty Name=\"NavPropertyETTwoKeyNavOne\" Type=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<ReferentialConstraint Property=\"PropertyInt1\" ReferencedProperty=\"PropertyInt16\"/>"
      + "</NavigationProperty>"
      + "</ComplexType><EntityContainer Name=\"EntityContainer\" "
      + "m:IsDefaultEntityContainer=\"true\"><EntitySet Name=\"ESTwoKeyNav\" "
      + "EntityType=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<NavigationPropertyBinding Path=\"CollPropertyCompNav/NavPropertyETTwoKeyNavOne\" "
      + "Target=\"ESTwoKeyNav\"/></EntitySet></EntityContainer></Schema>"
      + "</edmx:DataServices></edmx:Edmx>";
  
  public static final String xmlWithIncorrectReferentialConstraint2 = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<EntityType Name=\"ETTwoKeyNav\">"
      + "<Key><PropertyRef Name=\"PropertyInt16\"/>"
      + "<PropertyRef Name=\"PropertyString\"/></Key>"
      + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
      + "<Property Name=\"PropertyString\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "<Property Name=\"CollPropertyCompNav\" Type=\"Collection(Namespace1_Alias.CTNavFiveProp)\"/>"
      + "</EntityType><ComplexType Name=\"CTNavFiveProp\">"
      + "<NavigationProperty Name=\"NavPropertyETTwoKeyNavOne\" Type=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<ReferentialConstraint Property=\"PropertyInt16\" ReferencedProperty=\"PropertyInt1\"/>"
      + "</NavigationProperty>"
      + "</ComplexType><EntityContainer Name=\"EntityContainer\" "
      + "m:IsDefaultEntityContainer=\"true\"><EntitySet Name=\"ESTwoKeyNav\" "
      + "EntityType=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "<NavigationPropertyBinding Path=\"CollPropertyCompNav/NavPropertyETTwoKeyNavOne\" "
      + "Target=\"ESTwoKeyNav\"/></EntitySet></EntityContainer></Schema>"
      + "</edmx:DataServices></edmx:Edmx>";
  
  public static final String basicActionImportAndFunctionImport = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<EntityType Name=\"ETTwoKeyNav\"><Key><PropertyRef Name=\"PropertyInt16\"/>"
      + "<PropertyRef Name=\"PropertyString\"/></Key><Property Name=\"PropertyInt16\" "
      + "Type=\"Edm.Int16\" Nullable=\"false\"/>"
      + "<Property Name=\"PropertyString\" Type=\"Edm.String\" Nullable=\"false\"/>"
      + "</EntityType><Function Name=\"UFCRTETTwoKeyNav\" IsComposable=\"true\">"
      + "<ReturnType Type=\"Namespace1_Alias.ETTwoKeyNav\" Nullable=\"false\"/>"
      + "</Function><Action Name=\"UARTCollStringTwoParam\" IsBound=\"false\">"
      + "<Parameter Name=\"ParameterInt16\" Type=\"Edm.Int16\"/><Parameter Name=\"ParameterDuration\" "
      + "Type=\"Edm.Duration\"/><ReturnType Type=\"Collection(Edm.String)\"/></Action>"
      + "<EntityContainer Name=\"EntityContainer\" m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"ESTwoKeyNav\" EntityType=\"Namespace1_Alias.ETTwoKeyNav\"/>"
      + "<ActionImport Name=\"AIRTCollStringTwoParam\" "
      + "Action=\"Namespace1_Alias.UARTCollStringTwoParam\"/><FunctionImport "
      + "Name=\"FICRTESTwoKeyNav\" Function=\"Namespace1_Alias.UFCRTETTwoKeyNav\" "
      + "EntitySet=\"Namespace1_Alias.ESTwoKeyNav\" IncludeInServiceDocument=\"true\"/>"
      + "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
  
  public static final String xmlWithNavPropInBaseType= 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" m:MaxDataServiceVersion=\"4.0\" "
      + "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<EntityType Name=\"ETKeyNav\"><Key><PropertyRef Name=\"PropertyInt16\"/>"
      + "</Key><Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
      + "<NavigationProperty Name=\"NavPropertyETKeyNavMany\" Type=\"Collection(Namespace1_Alias.ETKeyNav)\"/>"
      + "</EntityType><EntityType Name=\"ETTwoKeyNav\" BaseType=\"Namespace1_Alias.ETKeyNav\">"
      + "</EntityType><EntityType Name=\"ETBaseTwoKeyNav\" BaseType=\"Namespace1_Alias.ETTwoKeyNav\">"
      + "</EntityType><EntityContainer Name=\"EntityContainer\" m:IsDefaultEntityContainer=\"true\">"
      + "<EntitySet Name=\"ESTwoKeyNav\" EntityType=\"Namespace1_Alias.ETTwoKeyNav\"/>"
      + "<EntitySet Name=\"ESBaseTwoKeyNav\" EntityType=\"Namespace1_Alias.ETBaseTwoKeyNav\">"
      + "<NavigationPropertyBinding Path=\"NavPropertyETKeyNavMany\" Target=\"ESKeyNav\"/>"
      + "</EntitySet>"
      + "<EntitySet Name=\"ESKeyNav\" EntityType=\"Namespace1_Alias.ETKeyNav\"/>"
      + "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
  
  public static final String xmlWithActionsAndFunctionsHavingReturnedEntitiesInDiffNamespace = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" "
      + "m:MaxDataServiceVersion=\"4.0\" xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<Action Name=\"UAETTwoKeyNavRTETTwoKeyNavParam\" IsBound=\"false\">"
          + "<Parameter Name=\"ParameterInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
          + "<ReturnType Type=\"Namespace2_Alias.ODataWebExperimentalETKeyNav\"/>"
          + "</Action><Function Name=\"UFCRTETTwoKeyNav\" IsComposable=\"true\" "
          + "IsBound=\"false\"><ReturnType Type=\"Namespace2_Alias.ODataWebExperimentalETKeyNav\" "
          + "Nullable=\"false\"/></Function><EntityContainer Name=\"EntityContainer\" "
          + "m:IsDefaultEntityContainer=\"true\"><ActionImport "
          + "Name=\"AIRTETTwoKeyNavRTETTwoKeyNavParam\" "
          + "Action=\"Namespace1_Alias.UAETTwoKeyNavRTETTwoKeyNavParam\" "
          + "EntitySet=\"Namespace2_Alias.EntityContainer1/ODataWebExperimentalETKeyNavSet\"/>"
          + "<FunctionImport Name=\"FICRTESTwoKeyNav\" "
          + "Function=\"Namespace1_Alias.UFCRTETTwoKeyNav\" "
          + "EntitySet=\"Namespace2_Alias.EntityContainer1/ODataWebExperimentalETKeyNavSet\" "
          + "IncludeInServiceDocument=\"true\"/></EntityContainer></Schema>"
          + "<Schema Namespace=\"ODataWebExperimental.OData.Model\" "
              + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace2_Alias\">"
              + "<EntityType Name=\"ODataWebExperimentalETKeyNav\"><Key>"
              + "<PropertyRef Name=\"PropertyInt16\"/></Key>"
              + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
              + "</EntityType><EntityContainer Name=\"EntityContainer1\" "
              + "p4:LazyLoadingEnabled=\"true\" "
              + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
              + "<EntitySet Name=\"ODataWebExperimentalETKeyNavSet\" "
              + "EntityType=\"Namespace2_Alias.ODataWebExperimentalETKeyNav\"/>"
              + "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
  
  public static final String invalidFunction =  
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" "
      + "m:MaxDataServiceVersion=\"4.0\" xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
          + "<Function Name=\"UFCRTETTwoKeyNavABC\" IsComposable=\"true\" "
          + "IsBound=\"false\"><ReturnType Type=\"Namespace2_Alias.ODataWebExperimentalETKeyNav\" "
          + "Nullable=\"false\"/></Function><EntityContainer Name=\"EntityContainer\" "
          + "m:IsDefaultEntityContainer=\"true\">"
          + "<FunctionImport Name=\"FICRTESTwoKeyNav\" "
          + "Function=\"Namespace1_Alias.UFCRTETTwoKeyNav\" "
          + "EntitySet=\"Namespace2_Alias.EntityContainer1/ODataWebExperimentalETKeyNavSet\" "
          + "IncludeInServiceDocument=\"true\"/></EntityContainer></Schema>"
          + "<Schema Namespace=\"ODataWebExperimental.OData.Model\" "
              + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace2_Alias\">"
              + "<EntityType Name=\"ODataWebExperimentalETKeyNav\"><Key>"
              + "<PropertyRef Name=\"PropertyInt16\"/></Key>"
              + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
              + "</EntityType><EntityContainer Name=\"EntityContainer1\" "
              + "p4:LazyLoadingEnabled=\"true\" "
              + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
              + "<EntitySet Name=\"ODataWebExperimentalETKeyNavSet\" "
              + "EntityType=\"Namespace2_Alias.ODataWebExperimentalETKeyNav\"/>"
              + "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
  
  public static final String invalidAction = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
      + "<edmx:DataServices m:DataServiceVersion=\"4.0\" "
      + "m:MaxDataServiceVersion=\"4.0\" xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\">"
      + "<Schema Namespace=\"Microsoft.Exchange.Services.OData.Model\" "
      + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace1_Alias\">"
      + "<Action Name=\"UAETTwoKeyNavRTETTwoKeyNavParamABC\" IsBound=\"false\">"
          + "<Parameter Name=\"ParameterInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
          + "<ReturnType Type=\"Namespace2_Alias.ODataWebExperimentalETKeyNav\"/>"
          + "</Action>"
          + "<EntityContainer Name=\"EntityContainer\" "
          + "m:IsDefaultEntityContainer=\"true\"><ActionImport "
          + "Name=\"AIRTETTwoKeyNavRTETTwoKeyNavParam\" "
          + "Action=\"Namespace1_Alias.UAETTwoKeyNavRTETTwoKeyNavParam\" "
          + "EntitySet=\"Namespace2_Alias.EntityContainer1/ODataWebExperimentalETKeyNavSet\"/>"
          + "</EntityContainer></Schema>"
          + "<Schema Namespace=\"ODataWebExperimental.OData.Model\" "
              + "xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Alias=\"Namespace2_Alias\">"
              + "<EntityType Name=\"ODataWebExperimentalETKeyNav\"><Key>"
              + "<PropertyRef Name=\"PropertyInt16\"/></Key>"
              + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"/>"
              + "</EntityType><EntityContainer Name=\"EntityContainer1\" "
              + "p4:LazyLoadingEnabled=\"true\" "
              + "xmlns:p4=\"http://schemas.microsoft.com/ado/2009/02/edm/annotation\">"
              + "<EntitySet Name=\"ODataWebExperimentalETKeyNavSet\" "
              + "EntityType=\"Namespace2_Alias.ODataWebExperimentalETKeyNav\"/>"
              + "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
  
  public static final String V4MetadataWithNoEntityContainer ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+"<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
			+	"<edmx:Reference Uri=\"../VOC_Core/$metadata\">"
			+		"<edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\" />"
			+	"</edmx:Reference>"
			+	"<edmx:DataServices>"
			+"<Schema Namespace=\"EPMSample2\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\"/>"
			+	"</edmx:DataServices>"
			+"</edmx:Edmx>";
  
  public static final String invalidV4MetadataWithNoSchema ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+"<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
			+	"<edmx:Reference Uri=\"../VOC_Core/$metadata\">"
			+		"<edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\" />"
			+	"</edmx:Reference>"
			+	"<edmx:DataServices>"
			+	"</edmx:DataServices>"
			+"</edmx:Edmx>";
  
  public static final String validMetadataWithMultipleSchemaNamespaces ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+"<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"  >"
			+	"<edmx:Reference Uri=\"../VOC_Core/$metadata\">"
			+		"<edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\" />"
			+	"</edmx:Reference>"
			+	"<edmx:DataServices>"
			+"<Schema Namespace=\"EPMSample41\" xmlns:abc=\"http://docs.oasis-open.org/odata/ns/edm\" "
			+ "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"/>"
			+"<Schema Namespace=\"EPMSample42\" xmlns:xyz=\"http://docs.oasis-open.org/odata/ns/edm\""
			+ " xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"/>"
			+"<Schema Namespace=\"EPMSample43\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" "
			+ "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"/>"
			+	"</edmx:DataServices>"
			+"</edmx:Edmx>";
 
  public static final String invalidV4MetadataWithV2Schemas ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+"<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
			+	"<edmx:Reference Uri=\"../VOC_Core/$metadata\">"
			+		"<edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\" />"
			+	"</edmx:Reference>"
			+	"<edmx:DataServices>"
			+"<Schema Namespace=\"EPMSample2\" xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\"/>"
			+"<Schema Namespace=\"EPM2\" xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\"/>"
			+	"</edmx:DataServices>"
			+"</edmx:Edmx>";
  public static final String invalidV4MetadataWithV2AndV4Schemas ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+"<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
			+	"<edmx:Reference Uri=\"../VOC_Core/$metadata\">"
			+		"<edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\" />"
			+	"</edmx:Reference>"
			+	"<edmx:DataServices>"
			+"<Schema Namespace=\"EPMSample4\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\"/>"
			+"<Schema Namespace=\"EPM2\" xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\"/>"
			+	"</edmx:DataServices>"
			+"</edmx:Edmx>";
  
  @Test
  public void testXMLMetadataWithOneSchema() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(metadata);
  }
  
  @Test
  public void testXMLMetadataWithTwoSchemas() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(metadata);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(metadata);
  }
  
  @Test
  public void checkValidV4XMLMetadataWithTwoSchemas() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(metadata);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    try {
		assertEquals(true,metadataValidator.isV4Metadata(metadata));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  @Test
  public void checkInValidV4XMLMetadataWithTwoSchemas() {
    
    boolean checkException = false;
    try {
        InputStream stream = new ByteArrayInputStream(invalidV4MetadataWithV2AndV4Schemas.getBytes("UTF-8"));
        final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
            toMetadata(stream);
        assertNotNull(metadata);
        ODataMetadataValidation metadataValidator = client.metadataValidation();
        assertEquals(false,metadataValidator.isV4Metadata(metadata));
        
      } catch (Exception e) {
      	checkException = true;
      	 
      } 
      assertEquals(false,checkException);
  }
  
  @Test
  public void checkInValidV4XMLMetadataWithNoSchemas() {
	  boolean checkException = false;
    try {
      InputStream stream = new ByteArrayInputStream(invalidV4MetadataWithNoSchema.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.isV4Metadata(metadata);
      
    } catch (Exception e) {
    	checkException = true;
    	 assertEquals(e.getMessage(), "Cannot determine if v4 metadata," 
 				+ "No schemanamespaces found in XMLMetadata");
    } 
    assertEquals(true,checkException);
  }
  
  
  @Test
  public void checkInValidV4XMLMetadataWithNoSchemasample() {
	  boolean checkException = false;
    try {
      InputStream stream = new ByteArrayInputStream(validMetadataWithMultipleSchemaNamespaces.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      assertEquals(true,metadataValidator.isV4Metadata(metadata));
      
    } catch (Exception e) {
    	checkException = true;
    	 
    } 
    assertEquals(false,checkException);
  }
  
  @Test
  public void checkInValidV4XMLMetadataWithV2Schemas() {
	
    try {
      InputStream stream = new ByteArrayInputStream(invalidV4MetadataWithV2Schemas.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      assertEquals(false,metadataValidator.isV4Metadata(metadata));
      
    } catch (Exception e) {
    
    	assertEquals(false, true);
    } 
  }
  
  
  
  @Test
  public void testIfV4Service() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(metadata);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    assertEquals(true,metadataValidator.isServiceDocument(metadata));
  }

  @Test
  public void testIfV4ServiceWithNoEntityContainer() {
	  try{
		  InputStream stream = new ByteArrayInputStream(V4MetadataWithNoEntityContainer.getBytes("UTF-8"));
	      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
	          toMetadata(stream);
	      assertNotNull(metadata);
	    ODataMetadataValidation metadataValidator = client.metadataValidation();
	    boolean isservice = metadataValidator.isServiceDocument(metadata);
	    assertEquals(false,isservice);
	  }catch (Exception e) {
		    
	    	assertEquals(false, true);
	    } 

  }
  @Test
  public void testXMLMetadataWithTripInService() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("metadata_TripInService.xml"));
    assertNotNull(metadata);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(metadata);
  }
  
  @Test
  public void testXMLMetadataWithDiffNavBindingPath() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("metadata_1.xml"));
    assertNotNull(metadata);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(metadata);
  }
  
  @Test
  public void testXMLMetadataWithDiffNavBindingTarget() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("metadata_2.xml"));
    assertNotNull(metadata);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(metadata);
  }
  
  @Test
  public void testXMLMetadataWithAliasNamespaceMapping() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("metadata_3.xml"));
    assertNotNull(metadata);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(metadata);
  }
  
  @Test
  public void testEdmWithOneSchema() {
    final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(edm);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(edm);
  }
  
  @Test
  public void testEdmWithWithTripInService() {
    final Edm edm = client.getReader().readMetadata(getClass().
        getResourceAsStream("metadata_TripInService.xml"));
    assertNotNull(edm);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(edm);
  }
  
  @Test
  public void testEdmWithDiffNavBindingPath() {
    final Edm edm = client.getReader().readMetadata(getClass().
        getResourceAsStream("metadata_1.xml"));
    assertNotNull(edm);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(edm);
  }
  
  @Test
  public void testEdmWithDiffNavBindingTarget() {
    final Edm edm = client.getReader().readMetadata(getClass().
        getResourceAsStream("metadata_2.xml"));
    assertNotNull(edm);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(edm);
  }
  
  @Test
  public void testEdmWithAliasNamespaceMapping() {
    final Edm edm = client.getReader().readMetadata(getClass().
        getResourceAsStream("metadata_3.xml"));
    assertNotNull(edm);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(edm);
  }
  
  @Test
  public void testEdmWithTwoSchema() {
    final Edm edm = client.getReader().readMetadata(getClass().
        getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(edm);
    ODataMetadataValidation metadataValidator = client.metadataValidation();
    metadataValidator.validateMetadata(edm);
  }
  
  @Test 
  public void testWrongEdm1() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(wrongBindingTarget.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Navigation Property Type "
          + "Microsoft.Exchange.Services.OData.Model.EmailMessage "
          + "does not match "
          + "the binding target type Microsoft.Exchange.Services.OData.Model.Folder");
    }
  }
  
  @Test
  public void testInvalidFunction() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(invalidFunction.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Invalid Function Namespace1_Alias.UFCRTETTwoKeyNav");
    }
  }
  
  @Test
  public void testInvalidFunctionInXMLMetadata1() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(invalidFunction.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Invalid Function Namespace1_Alias.UFCRTETTwoKeyNav");
    }
  }
  
  @Test
  public void testInvalidActionInXMLMetadata1() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(invalidAction.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Invalid Action Namespace1_Alias.UAETTwoKeyNavRTETTwoKeyNavParam");
    }
  }
  
  @Test
  public void testWrongXMLMetadata1() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(wrongBindingTarget.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Navigation Property Type "
          + "Microsoft.Exchange.Services.OData.Model.EmailMessage "
          + "does not match "
          + "the binding target type Microsoft.Exchange.Services.OData.Model.Folder");
    }
  }
  
  @Test 
  public void testWrongEdm2() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(xmlWithNonKeyEntity.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Missing key for EntityType Entity");
    }
  }
  
  @Test 
  public void testWrongEdm3() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithWrongNamespaceInBindingPath.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "The fully Qualified type OData.Model.Human mentioned "
          + "in navigation binding path not found ");
    }
  }
  
  @Test
  public void testWrongEdm4() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithWrongComplexBaseType.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (EdmException e) {
      assertEquals(e.getMessage(), "Can't find base type"
          + " with name: OData.Model.Animal for complex type: Human");
    }
  }
  
  @Test
  public void testWrongEdm5() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithNonKeyEntity1.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Missing key for EntityType User");
    }
  }
  
  @Test
  public void testWrongEdm6() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithIncorrectReferentialConstraint1.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Property name PropertyInt1 not part of the source entity.");
    }
  }
  
  @Test
  public void testWrongEdm7() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithIncorrectReferentialConstraint2.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Property name PropertyInt1 not part of the target entity.");
    }
  }
  
  @Test
  public void testEdmWithBaseEntityAsBindingTarget() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithBaseEntityAsBindingTarget.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test
  public void testEdmWithBasicXML() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          basicXml.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test
  public void testEdmWithActionAndFunctionImportXML() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          basicActionImportAndFunctionImport.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test
  public void testEdmWithNavPropInBaseType() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithNavPropInBaseType.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test
  public void testEdmWithtestEdmWithActionAndFunctionImport() 
      throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithActionsAndFunctionsHavingReturnedEntitiesInDiffNamespace.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test
  public void testWrongXMLMetadata2() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(xmlWithNonKeyEntity.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Missing key for EntityType Entity");
    }
  }
  
  @Test 
  public void testWrongXMLMetadata3() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithWrongNamespaceInBindingTarget.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Container with FullyQualifiedName "
          + "Model.EntityContainer1 not found.");
    }
  }
  
  @Test
  public void testWrongXMLMetadata4() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithWrongNamespaceInBindingPath.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "The fully Qualified type OData."
          + "Model.Human mentioned in navigation binding path not found ");
    }
  }
  
  @Test
  public void testWrongXMLMetadata5() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithInvalidEntityTypeNamespace.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Invalid Entity Type OData.Model.Customer");
    }
  }
  
  @Test
  public void testWrongXMLMetadata6() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithWrongComplexBaseType.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Invalid Complex BaseType OData.Model.Animal");
    }
  }
  
  @Test
  public void testWrongXMLMetadata7() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithNonKeyEntity1.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Missing key for EntityType User");
    }
  }
  
  @Test 
  public void testWrongXMLMetadata8() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithWrongBindingTarget.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Navigation Property Target HumanCities is not part "
          + "of the same container Microsoft.Exchange.Services.OData.Model.EntityContainer");
    }
  }
  
  @Test
  public void testXMLMetadataBaseEntityAsBindingTaget() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithBaseEntityAsBindingTarget.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test
  public void testBasicXMLMetadata10() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          basicXml.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test 
  public void testWrongXMLMetadata11() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithWrongBindingTarget1.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Target Entity Set mentioned in navigationBindingProperty "
          + "not found in the container EntityContainer1");
    }
  }
  
  @Test
  public void testWrongXMLMetadata12() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithIncorrectReferentialConstraint1.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Property name PropertyInt1 not part of the source entity.");
    }
  }
  
  @Test
  public void testWrongXMLMetadata13() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithIncorrectReferentialConstraint2.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Property name PropertyInt1 not part of the target entity.");
    }
  }
  
  @Test
  public void XMLMetadataActionImportAndFunctionImport() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          basicActionImportAndFunctionImport.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test
  public void XMLMetadataWithNavigationPropertyInBaseType() throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          xmlWithNavPropInBaseType.getBytes("UTF-8"));
      final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
          toMetadata(stream);
      assertNotNull(metadata);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(metadata);
    } catch (UnsupportedEncodingException e) {
      throw e;
    }
  }
  
  @Test
  public void testEdmWithSingleton() 
      throws UnsupportedEncodingException {
    try {
      InputStream stream = new ByteArrayInputStream(
          basicXmlWithSingleton.getBytes("UTF-8"));
      final Edm edm = client.getReader().readMetadata(stream);
      assertNotNull(edm);
      ODataMetadataValidation metadataValidator = client.metadataValidation();
      metadataValidator.validateMetadata(edm);
    } catch (UnsupportedEncodingException e) {
      throw e;
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Validations of Singletons are not supported: SINav");
    }
  }
    
    @Test
    public void XMLMetadataWithSingleton() throws UnsupportedEncodingException {
      try {
        InputStream stream = new ByteArrayInputStream(
            basicXmlWithSingleton.getBytes("UTF-8"));
        final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
            toMetadata(stream);
        assertNotNull(metadata);
        ODataMetadataValidation metadataValidator = client.metadataValidation();
        metadataValidator.validateMetadata(metadata);
      } catch (UnsupportedEncodingException e) {
        throw e;
      } catch (RuntimeException e) {
        assertEquals(e.getMessage(), "Validations of Singletons are not supported: SINav");
      }
    }
}
