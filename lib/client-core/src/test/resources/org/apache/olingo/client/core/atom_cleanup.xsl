<?xml version="1.0" encoding="UTF-8"?>
<!--

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/2005/Atom"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:m="http://docs.oasis-open.org/odata/ns/metadata"
                version="1.0">

  <xsl:template match="atom:updated"/>
  <xsl:template match="atom:author"/>
  <xsl:template match="atom:summary"/>
  <xsl:template match="atom:title">
    <xsl:if test="string-length(.) &gt; 0">
      <title type="{@type}">
        <xsl:apply-templates/>
      </title>
    </xsl:if>
  </xsl:template>
  <xsl:template match="atom:link[@rel = 'self' or @rel = 'edit' or @rel = 'edit-media']"/>
  
  <xsl:template match="m:action"/>

  <xsl:template match="@*[name() = 'm:etag' or name() = 'm:context' or name() = 'm:metadata-etag']"/>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
