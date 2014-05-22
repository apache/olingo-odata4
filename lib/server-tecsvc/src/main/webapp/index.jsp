<%@ page language="java" contentType="text/html; UTF-8"
 pageEncoding="UTF-8"%>

<!--
  Licensed to the Apache Software Foundation (ASF) under one
         or more contributor license agreements.  See the NOTICE file
         distributed with this work for additional information
         regarding copyright ownership.  The ASF licenses this file
         to you under the Apache License, Version 2.0 (the
         "License"); you may not use this file except in compliance
         with the License.  You may obtain a copy of the License at
  
           http://www.apache.org/licenses/LICENSE-2.0
  
         Unless required by applicable law or agreed to in writing,
         software distributed under the License is distributed on an
         "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
         KIND, either express or implied.  See the License for the
         specific language governing permissions and limitations
         under the License.
-->

<!DOCTYPE html>
<html>
<header>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
 <title>Apache Olingo - OData 4.0</title>

 <link type="text/css" rel="stylesheet" href="css/olingo.css">

</header>

<body>
 <div>
  <h1>
   &nbsp;Olingo OData 4.0
   <div class="logo">
    <img height="40" src="img/OlingoOrangeTM.png" />
   </div>
  </h1>
  <hr>
 </div>
 <h2>Technical Service</h2>
 <div>



  <ul>
   <li><a href="odata.svc/">Service Document</a></li>
   <li><a href="odata.svc/$metadata">Metadata</a></li>
  </ul>
 </div>
 <p>
 <hr>
 <p>
 <div class="version">
  <%
    String version = "gen/version.html";

    try {
  %>
  <jsp:include page='<%=version%>' />
  <%
    } catch (Exception e) {
  %>
  <p>IDE Build</p>
  <%
    }
  %>
 </div>


</body>

</html>