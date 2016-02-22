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
package org.apache.olingo.server.api;

import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

/**
 * Contains the response content for the OData request.
 * <p/>
 * Because the content is potential streamable an error can occur when the
 * <code>write</code> methods are used.
 * If this happens <b>NO</b> exception will be thrown but if registered the
 * org.apache.olingo.server.api.ODataContentWriteErrorCallback is called.
 */
public interface ODataContent {
  /**
   * Write the available content into the given <code>WritableByteChannel</code>.
   *
   * If during write of the content an exception is thrown this exception will be catched
   * and the org.apache.olingo.server.api.ODataContentWriteErrorCallback is called (if registered).
   *
   * @param channel channel in which the content is written.
   */
  void write(WritableByteChannel channel);

  /**
   * Write the available content into the given <code>OutputStream</code>.
   *
   * If during write of the content an exception is thrown this exception will be catched
   * and the org.apache.olingo.server.api.ODataContentWriteErrorCallback is called (if registered).
   *
   * @param stream stream in which the content is written.
   */
  void write(OutputStream stream);
}
