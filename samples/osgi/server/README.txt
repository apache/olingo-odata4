#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

Apache Olingo OData Cars service on OSGi
=================================================

This demo is an OSGi version of the Olingo Cars demo.

Building
--------
From the base directory of this sample, the pom.xml file
is used to build and run the standalone unit test.

```
  mvn clean install
```

Running the demo in OSGi
------------------------
After building the sample, install the bundle to your Karaf
container.

If you do not have Karaf installed, download apache-karaf-3.0.3.tar.gz from one of the [mirror sites](http://www.apache.org/dyn/closer.cgi/karaf/3.0.3/apache-karaf-3.0.3.tar.gz) and unpack the archive.

```
$ wget -N http://ftp.halifax.rwth-aachen.de/apache/karaf/3.0.3/apache-karaf-3.0.3.tar.gz
$ tar -zxf apache-karaf-3.0.3.tar.gz
$ cd apache-karaf-3.0.3
```

#### Starting Karaf

```
$ bin/karaf
        __ __                  ____      
       / //_/____ __________ _/ __/      
      / ,<  / __ `/ ___/ __ `/ /_        
     / /| |/ /_/ / /  / /_/ / __/        
    /_/ |_|\__,_/_/   \__,_/_/         

  Apache Karaf (3.0.3)

Hit '<tab>' for a list of available commands
and '[cmd] --help' for help on a specific command.
Hit '<ctrl-d>' or type 'system:shutdown' or 'logout' to shutdown Karaf.

karaf@root()>
```

#### Note on using Karaf behind a firewall

If you do not have a direct access to the maven central repositories, you need to set Karaf's property
org.ops4j.pax.url.mvn.repositories to point to your local repository

To use your local repository http://nexus.mycorp.com:8081/nexus/content/groups/build.milestone, run the following commands.

```
config:edit org.ops4j.pax.url.mvn
config:property-set org.ops4j.pax.url.mvn.repositories http://nexus.mycorp.com:8081/nexus/content/groups/build.milestone
config:update
```

Alternatively, you can edit this property in file etc/org.ops4j.pax.url.mvn.cfg.

For further details, please refer to [Karaf User Guide](http://karaf.apache.org/manual/latest/users-guide/index.html).

#### Install Olingo libs and other dependent libs

To install the required bundles, you can simply install the Karaf olingo-server feature.
The following commands will install the olingo-server feature.

```
feature:repo-add mvn:org.apache.olingo/odata-karaf-features/4.2.0-SNAPSHOT/xml/features
feature:install olingo-server
```

#### Install this sample bundle

To install this sample bundle, run the karaf console command.

```
bundle:install -s mvn:org.apache.olingo/odata-server-osgi-sample/4.2.0-SNAPSHOT
```

Shown below is the output from running the above Karaf console commands.

```
karaf@root()> feature:repo-add mvn:org.apache.olingo/odata-karaf-features/4.2.0-SNAPSHOT/xml/features
Adding feature url mvn:org.apache.olingo/odata-karaf-features/4.2.0-SNAPSHOT/xml/features
karaf@root()> feature:install olingo-server
karaf@root()> bundle:install -s mvn:org.apache.olingo/odata-server-osgi-sample/4.2.0-SNAPSHOT
Bundle ID: 109
```

To verify if the sample is correctly installed and running, use list and web:list to see its bundle status and its web context is registered.

```
karaf@root()> list
START LEVEL 100 , List Threshold: 50
 ID | State  | Lvl | Version        | Name                    
--------------------------------------------------------------
 97 | Active |  50 | 1.9.0          | Apache Commons Codec    
 98 | Active |  50 | 3.3.2          | Apache Commons Lang     
 99 | Active |  50 | 3.1.4          | Stax2 API               
100 | Active |  50 | 0.9.10         | aalto-xml               
101 | Active |  50 | 4.1            | antlr4-runtime          
102 | Active |  50 | 2.4.2          | Jackson-core            
103 | Active |  50 | 2.4.2          | Jackson-annotations     
104 | Active |  50 | 2.4.2          | jackson-databind        
105 | Active |  50 | 4.0.0          | odata-commons-api       
106 | Active |  50 | 4.0.0          | odata-commons-core      
107 | Active |  50 | 4.0.0          | odata-server-api        
108 | Active |  50 | 4.0.0          | odata-server-core       
109 | Active |  80 | 4.0.0          | odata-server-osgi-sample
karaf@root()> web:list
ID  | State       | Web-State   | Level | Web-ContextPath | Name                                     
-----------------------------------------------------------------------------------------------------
109 | Active      | Deployed    | 80    | /olingo-cars    | odata-server-osgi-sample (4.0.0)
karaf@root()>
```

#### Test this sample

Using Browser, open [http://localhost:8181/olingo-cars](http://localhost:8181/olingo-cars) to access the default page and see some available
queries. You can invoke these queries by clicking on those links or directly typing
in the queries in Browser's URL field.
