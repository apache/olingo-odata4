/**
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
package com.msopentech.odatajclient.testservice.utils;

import com.msopentech.odatajclient.testservice.UnsupportedMediaTypeException;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

public enum Accept {

    TEXT(ContentType.TEXT_PLAIN.getMimeType(), ".txt"),
    XML(ContentType.APPLICATION_XML.getMimeType(), ".xml"),
    ATOM(ContentType.APPLICATION_ATOM_XML.getMimeType(), ".xml"),
    JSON(ContentType.APPLICATION_JSON.getMimeType() + ";odata=minimalmetadata", ".full.json"),
    JSON_NOMETA(ContentType.APPLICATION_JSON.getMimeType() + ";odata=nometadata", ".full.json"),
    JSON_FULLMETA(ContentType.APPLICATION_JSON.getMimeType() + ";odata=fullmetadata", ".full.json");

    private final String contentType;

    private final String fileExtension;

    private static Pattern allTypesPattern = Pattern.compile("(.*,)?\\*/\\*([,;].*)?");

    Accept(final String contentType, final String fileExtension) {
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    @Override
    public String toString() {
        return contentType;
    }

    public String getExtension() {
        return fileExtension;
    }

    public static Accept parse(final String contentType, final ODataVersion version) {
        return parse(contentType, version, ODataVersion.v3 == version ? ATOM : JSON_NOMETA);
    }

    public static Accept parse(final String contentType, final ODataVersion version, final Accept def) {
        if (StringUtils.isBlank(contentType) || allTypesPattern.matcher(contentType).matches()) {
            return def;
        } else if (JSON_NOMETA.toString().equals(contentType)) {
            return JSON_NOMETA;
        } else if (JSON.toString().equals(contentType) || "application/json".equals(contentType)) {
            return JSON;
        } else if (JSON_FULLMETA.toString().equals(contentType)) {
            return JSON_FULLMETA;
        } else if (XML.toString().equals(contentType)) {
            return XML;
        } else if (ATOM.toString().equals(contentType)) {
            return ATOM;
        } else if (TEXT.toString().equals(contentType)) {
            return TEXT;
        } else {
            throw new UnsupportedMediaTypeException("Unsupported media type");
        }
    }
}