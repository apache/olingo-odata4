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
package org.apache.olingo.server.api.debug;

/**
 * <p>Runtime measurements.</p>
 * <p>All times are in nanoseconds since some fixed but arbitrary time
 * (perhaps in the future, so values may be negative).</p>
 * @see System#nanoTime()
 */
public class RuntimeMeasurement {

  private String className;
  private String methodName;
  private long timeStarted;
  private long timeStopped;

  /**
   * Sets the class name.
   * @param className the name of the class that is measured
   */
  public void setClassName(final String className) {
    this.className = className;
  }

  /**
   * Gets the class name.
   * @return the name of the class that is measured
   */
  public String getClassName() {
    return className;
  };

  /**
   * Sets the method name.
   * @param methodName the name of the method that is measured
   */
  public void setMethodName(final String methodName) {
    this.methodName = methodName;
  }

  /**
   * Gets the method name.
   * @return the name of the method that is measured
   */
  public String getMethodName() {
    return methodName;
  }

  /**
   * Sets the start time.
   * @param timeStarted the start time in nanoseconds
   * @see System#nanoTime()
   */
  public void setTimeStarted(final long timeStarted) {
    this.timeStarted = timeStarted;
  }

  /**
   * Gets the start time.
   * @return the start time in nanoseconds or 0 if not set yet
   * @see System#nanoTime()
   */
  public long getTimeStarted() {
    return timeStarted;
  }

  /**
   * Sets the stop time.
   * @param timeStopped the stop time in nanoseconds
   * @see System#nanoTime()
   */
  public void setTimeStopped(final long timeStopped) {
    this.timeStopped = timeStopped;
  }

  /**
   * Gets the stop time.
   * @return the stop time in nanoseconds or 0 if not set yet
   * @see System#nanoTime()
   */
  public long getTimeStopped() {
    return timeStopped;
  }

  @Override
  public String toString() {
    return className + "." + methodName + ": duration: " + (timeStopped - timeStarted);
  }
}
