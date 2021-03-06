<!--
  Licensed to ObjectStyle LLC under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ObjectStyle LLC licenses
  this file to you under the Apache License, Version 2.0 (the
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

# Comparision of Bootique DI and Google Guice

 Feature                          | Bootique DI         |  Guice    |
----------------------------------|:-------------------:|:---------:|
Method injection                  | configurable        | yes
Map injection                     | yes                 | yes       
Set injection                     | yes                 | yes       
Ordered list injection            | yes                 | no        
Per-element binding in collection | no                  | yes       
Optional injection                | yes                 | yes
Binding override                  | always allowed ([#5](https://github.com/bootique/bootique-di/issues/5)) | should be declared per module
Default binding scope             | configurable        | no scope  
Binding decorators                | yes                 | no?
Eager singleton                   | no                  | yes
Just-in-time Bindings             | configurable        | allowed by default
@ImplementedBy/@ProvidedBy        | no                  | yes
Auto resolution of circular dependecies | no            | yes
Multiple failures report          | no                  | yes
