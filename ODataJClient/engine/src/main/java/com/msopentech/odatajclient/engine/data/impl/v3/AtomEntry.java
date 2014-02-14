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
package com.msopentech.odatajclient.engine.data.impl.v3;

import com.msopentech.odatajclient.engine.data.impl.AbstractEntry;
import java.net.URI;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class AtomEntry extends AbstractEntry<AtomLink> implements AtomObject {

    private static final long serialVersionUID = 6973729343868293279L;

    public static class Author {

        private String name;

        private String uri;

        private String email;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(final String uri) {
            this.uri = uri;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(final String email) {
            this.email = email;
        }

        public boolean isEmpty() {
            return StringUtils.isBlank(name) && StringUtils.isBlank(uri) && StringUtils.isBlank(email);
        }
    }

    private URI baseURI;

    private String title;

    private String summary;

    private Date updated;

    private Author author;

    @Override
    public void setBaseURI(final String baseURI) {
        this.baseURI = URI.create(baseURI);
    }

    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public void setSummary(final String summary) {
        this.summary = summary;
    }

    @Override
    public Date getUpdated() {
        return new Date(updated.getTime());
    }

    @Override
    public void setUpdated(final Date updated) {
        this.updated = new Date(updated.getTime());
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(final Author author) {
        this.author = author;
    }
}
