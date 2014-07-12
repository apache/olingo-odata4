/*
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
package org.apache.olingo.ext.proxy.api;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.olingo.client.api.Configuration;

public abstract class AsyncCall<V> implements Future<V> {

  private final Future<V> future;

  public AsyncCall(final Configuration configuration) {
    this.future = configuration.getExecutor().submit(new Callable<V>() {

      @Override
      public V call() throws Exception {
        return AsyncCall.this.call();
      }
    });
  }

  public abstract V call();

  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    return this.future.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return this.future.isCancelled();
  }

  @Override
  public boolean isDone() {
    return this.future.isDone();
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    return this.future.get();
  }

  @Override
  public V get(final long timeout, final TimeUnit unit)
          throws InterruptedException, ExecutionException, TimeoutException {

    return this.future.get(timeout, unit);
  }
}
