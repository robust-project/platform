--  Licensed to the Apache Software Foundation (ASF) under one
--  or more contributor license agreements.  See the NOTICE file
--  distributed with this work for additional information
--  regarding copyright ownership.  The ASF licenses this file
--  to you under the Apache License, Version 2.0 (the
--  "License"); you may not use this file except in compliance
--  with the License.  You may obtain a copy of the License at
--
--    http://www.apache.org/licenses/LICENSE-2.0
--
--  Unless required by applicable law or agreed to in writing,
--  software distributed under the License is distributed on an
--  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
--  KIND, either express or implied.  See the License for the
--  specific language governing permissions and limitations
--  under the License.

-- load the edges
-- result: source	dest
edges = LOAD '$edges' USING PigStorage('\t') AS (source, dest);

-- load the vertices
-- result: source
rawVertices = LOAD '$vertices' USING PigStorage('\t') AS (source);

-- transform vertices
-- result: source	dest
vertices = FOREACH rawVertices GENERATE
	source,
	'' AS dest;

-- union edges and vertices
-- result: source	dest
allEdges = UNION edges, vertices;

-- group by the source node
-- result: source	[dest1, dest2, ...]
groupSource = GROUP allEdges BY source;

-- count the length of the destination array
-- result: source	degree
countVertex = FOREACH groupSource GENERATE
	group AS source,
	COUNT(allEdges) AS degree;

-- group by the degree
-- result: degree	[source1, source2, ...]
groupCount = GROUP countVertex BY degree;

-- count the length of the source array
-- result: degree	numVertex
countSources = FOREACH groupCount GENERATE
	group AS degree,	
	COUNT(countVertex) AS numVertex;

-- save the data
STORE countSources INTO '$output' USING PigStorage();
