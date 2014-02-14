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
package com.msopentech.odatajclient.engine.communication.request;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Streamer utility object.
 */
public abstract class ODataStreamer {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ODataStreamer.class);

    /**
     * CR/LF.
     */
    public static final byte[] CRLF = {13, 10};

    /**
     * OutputStream to be used to write objects to the stream.
     */
    private final PipedOutputStream bodyStreamWriter;

    /**
     * Constructor.
     *
     * @param bodyStreamWriter piped stream to be used to retrieve the payload.
     */
    public ODataStreamer(final PipedOutputStream bodyStreamWriter) {
        this.bodyStreamWriter = bodyStreamWriter;
    }

    /**
     * Writes the gibe byte array onto the output stream provided at instantiation time.
     *
     * @param src byte array to be written.
     */
    protected void stream(final byte[] src) {
        new Writer(src, bodyStreamWriter).run();
    }

    /**
     * Stream CR/LF.
     */
    protected void newLine() {
        stream(CRLF);
    }

    /**
     * Gets the piped stream to be used to stream the payload.
     *
     * @return piped stream.
     */
    public PipedOutputStream getBodyStreamWriter() {
        return bodyStreamWriter;
    }

    /**
     * Writer thread.
     */
    private class Writer implements Runnable {

        final OutputStream os;

        final byte[] src;

        public Writer(final byte[] src, final OutputStream os) {
            this.os = os;
            this.src = src;
        }

        @Override
        public void run() {
            try {
                os.write(src);
            } catch (IOException e) {
                LOG.error("Error streaming object", e);
            }
        }
    }
}
