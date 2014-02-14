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
package com.msopentech.odatajclient.engine.data;

/**
 * Exception indicating that no valid object has been found.
 */
public class NoValidEntityFound extends Exception {

    private static final long serialVersionUID = -3078954221364213688L;

    /**
     * Creates a new instance of
     * <code>NoSuchEntityFound</code> without detail message.
     */
    public NoValidEntityFound() {
        super();
    }

    /**
     * Constructs an instance of
     * <code>NoSuchEntityFound</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoValidEntityFound(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of
     * <code>NoSuchEntityFound</code> with the specified cause.
     *
     * @param t cause.
     */
    public NoValidEntityFound(final Throwable t) {
        this.initCause(t);
    }
}
