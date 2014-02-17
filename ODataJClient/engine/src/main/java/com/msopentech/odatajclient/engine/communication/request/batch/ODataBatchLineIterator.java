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
package com.msopentech.odatajclient.engine.communication.request.batch;

import java.util.Iterator;
import org.apache.commons.io.LineIterator;

/**
 * Batch line iterator class.
 */
public class ODataBatchLineIterator implements Iterator<String> {

    /**
     * Stream line iterator.
     */
    private final LineIterator batchLineIterator;

    /**
     * Last cached line.
     */
    private String current;

    /**
     * Constructor.
     *
     * @param batchLineIterator stream line iterator.
     */
    public ODataBatchLineIterator(final LineIterator batchLineIterator) {
        this.batchLineIterator = batchLineIterator;
        this.current = null;
    }

    /**
     * Checks if batch has next line.
     *
     * @return 'TRUE' if has next line; 'FALSE' otherwise.
     */
    @Override
    public boolean hasNext() {
        return batchLineIterator.hasNext();
    }

    /**
     * Gets next line.
     *
     * @return next line.
     */
    @Override
    public String next() {
        return nextLine();
    }

    /**
     * Gets next line.
     *
     * @return next line.
     */
    public String nextLine() {
        current = batchLineIterator.nextLine();
        return current;
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    /**
     * Gets last cached line (the current one).
     *
     * @return last cached line; null if <code>next()</code> method never called
     */
    public String getCurrent() {
        return current;
    }
}
