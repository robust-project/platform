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

items=$1
user=$2
database=$3

path=$bin/../temp
similarities=$path/similarities

# create output directory
echo "Create output path $path"
mkdir -p $path

# item similarities
hadoop jar $bin/../hadoop/target/hadoop-1.0-SNAPSHOT.jar org.apache.mahout.cf.taste.hadoop.similarity.item.ItemSimilarityJob -i file://$items -o file://$similarities --similarityClassname SIMILARITY_LOGLIKELIHOOD --maxSimilaritiesPerItem 20 --maxPrefsPerUser 100 --minPrefsPerUser 2 --booleanData true --threshold 0.05 --tempDir file://$path

# copy tsv
mv $similarities/part-r-00000 $path/item_similarity.tsv

# mysql
mysql -u $user $database < $bin/../database/itemsimilarity/schema/item_similarity_create_structure.sql
mysqlimport -u $user --local $database $path/item_similarity.tsv

# cleanup
rm -Rf $path
