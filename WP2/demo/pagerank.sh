#!/usr/bin/env bash

# Copyright 2013 ROBUST project consortium
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# you may obtain a copy of the License at
#  
#      http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and 
#    limitations under the License.

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

vertices=$1
edges=$2
user=$3
database=$4

path=$bin/../temp
pagerank=$path/pagerank

# create output directory
echo "Create output path $path"
mkdir -p $path

# pagerank
hadoop jar $bin/../hadoop/target/hadoop-1.0-SNAPSHOT.jar org.apache.mahout.graph.linkanalysis.PageRankJob --vertices file://$vertices --edges file://$edges -it 1 -o file://$pagerank --tempDir file://$path

# copy tsv
mv $pagerank/part-m-00000 $path/pagerank.tsv

# mysql
mysql -u $user $database < $bin/../database/pagerank/schema/pagerank_create_schema.sql
mysqlimport -u $user --local $database $path/pagerank.tsv

# cleanup
rm -Rf $path
