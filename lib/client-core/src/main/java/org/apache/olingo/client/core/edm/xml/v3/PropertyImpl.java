/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.edm.xml.v3;

import org.apache.olingo.client.api.edm.xml.v3.Property;
import org.apache.olingo.client.core.edm.xml.AbstractProperty;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.odata4.client.api.edm.ConcurrencyMode;
import org.apache.olingo.odata4.client.api.edm.StoreGeneratedPattern;

public class PropertyImpl extends AbstractProperty implements Property {

  private static final long serialVersionUID = 6224524803474652100L;

  private String collation;

  private boolean fixedLength;

  private ConcurrencyMode concurrencyMode;

  private StoreGeneratedPattern storeGeneratedPattern = StoreGeneratedPattern.None;

  private String fcSourcePath;

  private String fcTargetPath;

  private EdmContentKind fcContentKind = EdmContentKind.text;

  private String fcNSPrefix;

  private String fcNSURI;

  private boolean fcKeepInContent = true;

  @Override
  public boolean isFixedLength() {
    return fixedLength;
  }

  public void setFixedLength(final boolean fixedLength) {
    this.fixedLength = fixedLength;
  }

  @Override
  public String getCollation() {
    return collation;
  }

  public void setCollation(final String collation) {
    this.collation = collation;
  }

  @Override
  public ConcurrencyMode getConcurrencyMode() {
    return concurrencyMode;
  }

  public void setConcurrencyMode(final ConcurrencyMode concurrencyMode) {
    this.concurrencyMode = concurrencyMode;
  }

  @Override
  public StoreGeneratedPattern getStoreGeneratedPattern() {
    return storeGeneratedPattern;
  }

  public void setStoreGeneratedPattern(final StoreGeneratedPattern storeGeneratedPattern) {
    this.storeGeneratedPattern = storeGeneratedPattern;
  }

  @Override
  public String getFcSourcePath() {
    return fcSourcePath;
  }

  public void setFcSourcePath(final String fcSourcePath) {
    this.fcSourcePath = fcSourcePath;
  }

  @Override
  public String getFcTargetPath() {
    return fcTargetPath;
  }

  public void setFcTargetPath(final String fcTargetPath) {
    this.fcTargetPath = fcTargetPath;
  }

  @Override
  public EdmContentKind getFcContentKind() {
    return fcContentKind;
  }

  public void setFcContentKind(final EdmContentKind fcContentKind) {
    this.fcContentKind = fcContentKind;
  }

  @Override
  public String getFcNSPrefix() {
    return fcNSPrefix;
  }

  public void setFcNSPrefix(final String fcNSPrefix) {
    this.fcNSPrefix = fcNSPrefix;
  }

  @Override
  public String getFcNSURI() {
    return fcNSURI;
  }

  public void setFcNSURI(final String fcNSURI) {
    this.fcNSURI = fcNSURI;
  }

  @Override
  public boolean isFcKeepInContent() {
    return fcKeepInContent;
  }

  public void setFcKeepInContent(final boolean fcKeepInContent) {
    this.fcKeepInContent = fcKeepInContent;
  }

}
