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
package org.apache.olingo.odata4.server.api.uri.queryoption.expression;

public enum MethodCallKind {
  CONTAINS("contains"), STARTSWITH("startswith"), ENDSWITH("endswith"), LENGTH("length"),
  INDEXOF("indexof"), SUBSTRING("substring"), TOLOWER("tolower"), TOUPPER("toupper"), TRIM("trim"),
  CONCAT("concat"),

  YEAR("year"), MONTH("month"), DAY("day"), HOUR("hour"), MINUTE("minute"), SECOND("second"),
  FRACTIONALSECONDS("fractionalseconds"), TOTALSECONDS("totalseconds"), DATE("date"), TIME("time"),
  TOTALOFFSETMINUTES("totaloffsetminutes"), MINDATETIME("mindatetime"), MAXDATETIME("maxdatetime"), NOW("now"),

  ROUND("round"), FLOOR("floor"),

  CEILING("ceiling"), GEODISTANCE("geo.distance"), GEOLENGTH("geo.length"), GEOINTERSECTS("geo.intersects"),
  CAST("cast"),
  ISOF("isof");

  private String syntax;

  private MethodCallKind(final String syntax) {
    this.syntax = syntax;
  }

  @Override
  public String toString() {
    return syntax;
  }


  public static MethodCallKind get(final String method) {
    for (MethodCallKind op : MethodCallKind.values()) {

      if (op.toString().equals(method)) {
        return op;
      }
    }
    return null;
  }

}
