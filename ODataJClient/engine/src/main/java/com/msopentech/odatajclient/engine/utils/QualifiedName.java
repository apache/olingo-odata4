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
package com.msopentech.odatajclient.engine.utils;

import org.apache.commons.lang3.StringUtils;

public class QualifiedName {

    private final String namespace;

    private final String name;

    public QualifiedName(final String namespace, final String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name " + name);
        }

        this.namespace = namespace;
        this.name = name;
    }

    public QualifiedName(final String qualifiedName) {
        if (StringUtils.isNoneBlank(qualifiedName) && !qualifiedName.trim().endsWith(".")) {
            int lastDotIndex = qualifiedName.lastIndexOf('.');
            this.namespace = qualifiedName.substring(0, lastDotIndex > 0 ? lastDotIndex : 0);
            this.name = qualifiedName.substring(lastDotIndex + 1);
        } else {
            throw new IllegalArgumentException("Invalid qualified name " + qualifiedName);
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        if (StringUtils.isNoneBlank(namespace)) {
            builder.append(namespace).append('.');
        }

        return builder.append(name).toString();
    }
}
