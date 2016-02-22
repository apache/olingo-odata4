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
 * The ODataContentWriteErrorCallback is called when during the {@link ODataContent#write(OutputStream)}
 * or the {@link ODataContent#write(WritableByteChannel)} an error occurs.
 */
public interface ODataContentWriteErrorCallback {
  /**
   * Is called when during <i>write</i> in the ODataContent an error occurs.
   * The <code>context:ODataContentWriteErrorContext</code> contains all relevant information
   * and the <code>channel</code> is the channel (stream) in which before was written.
   * This channel is at this point not closed and can be used to write additional information.
   * <b>ATTENTION:</b> This channel MUST NOT be closed by the callback. It will be closed by the
   * layer responsible for the environment / data transfer (e.g. application server).
   *
   * @param context contains all relevant error information
   * @param channel is the channel (stream) in which before was written
   */
  void handleError(ODataContentWriteErrorContext context, WritableByteChannel channel);
}
