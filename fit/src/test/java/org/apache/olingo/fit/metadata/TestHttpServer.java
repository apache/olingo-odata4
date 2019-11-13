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
package org.apache.olingo.fit.metadata;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Simple HTTP server.
 */
public class TestHttpServer implements HttpHandler, AutoCloseable {

    private final String content;
    private final HttpServer server;

    /**
     * This flag indicates if the server accepted a connection.
     */
    private boolean accepted = false;

    public TestHttpServer(String content) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/test", this);
        server.setExecutor(null);
        server.start();
        this.content = content;
    }

    public String url() {
        return String.format("http://localhost:%d/test", server.getAddress().getPort());
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        System.out.println("server: accepted a request");
        synchronized (this) {
            accepted = true;
        }
        byte[] response = content.getBytes();
        t.sendResponseHeaders(200, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }

    /**
     * @return True if the server accepted a connection, false otherwise.
     */
    public boolean accepted() {
        synchronized (this) {
            return accepted;
        }
    }

    @Override
    public void close() {
        server.stop(0);
    }
}