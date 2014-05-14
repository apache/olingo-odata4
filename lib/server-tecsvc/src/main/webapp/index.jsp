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

 <style type="text/css">
body {
	font-family: Arial, sans-serif;
	font-size: 13px;
	line-height: 18px;
	color: blue;
	background-color: #ffffff;
}

a {
	color: blue;
	text-decoration: none;
}

a:focus {
	outline: thin dotted #4076cb;
	outline-offset: -1px;
}

a:hover,a:active {
	outline: 0;
}

a:hover {
	color: #404a7e;
	text-decoration: underline;
}

h1,h2,h3,h4,h5,h6 {
	margin: 9px 0;
	font-family: inherit;
	font-weight: bold;
	line-height: 1;
	color: blue;
}

h1 {
	font-size: 36px;
	line-height: 40px;
}

h2 {
	font-size: 30px;
	line-height: 40px;
}

h3 {
	font-size: 24px;
	line-height: 40px;
}

h4 {
	font-size: 18px;
	line-height: 20px;
}

h5 {
	font-size: 14px;
	line-height: 20px;
}

h6 {
	font-size: 12px;
	line-height: 20px;
}

.logo {
	float: right;
}

ul {
	padding: 0;
	margin: 0 0 9px 25px;
}

ul ul {
	margin-bottom: 0;
}

li {
	line-height: 18px;
}

hr {
	margin: 18px 0;
	border: 0;
	border-top: 1px solid #cccccc;
	border-bottom: 1px solid #ffffff;
}

table {
	border-collapse: collapse;
	border-spacing: 10px;
}

th,td {
	border: 1px solid;
	padding: 20px;
}

.code {
	font-family: "Courier New", monospace;
	font-size: 13px;
	line-height: 18px;
}
</style>

</header>

<body>
 <div>
  <h1>
   Olingo OData 4.0 <img height="100" align="right"
    src="img/OlingoOrangeTM.png" />
  </h1>
 </div>
 <hr>
 <h2>Technical Service</h2>
 <lu>
 <li><a href="odata.svc/">Service Document</a></li>
 <li><a href="odata.svc/$metadata">Metadata</a></li>
 </lu>

<hr>

 <div class="code">
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