/*
 * Copyright (c) 2010-2013 Digital Enterprise Research Institute, NUI Galway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ie.deri.uimr.crosscomanalysis.hadoop.keywords

import org.apache.hadoop.io.{Text, LongWritable}
import collection.mutable.MutableList
import collection.JavaConversions.iterableAsScalaIterable
import org.apache.hadoop.conf.{Configuration, Configured}
import org.apache.hadoop.util.{ToolRunner, Tool}
import java.lang.{String, Iterable}
import org.apache.hadoop.mapreduce.{Job, Reducer, Mapper}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.tools.ant.util.FileUtils
import java.io.File
import ie.deri.uimr.crosscomanalysis.hadoop.SHadoop._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/05/2011
 * Time: 18:49
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object KeywordFilter extends Configured with Tool {

  val THRESHOLD = "threshold"

  def main(args: Array[String]) {
    FileUtils.delete(new File(args(1)))
    val res = ToolRunner.run(new Configuration(), this, args);
    System.exit(res);
  }

  def run(args: Array[String]) = {
    val conf = getConf
    conf.setInt(THRESHOLD, args(2).toInt)
    val job = new Job(conf, "keyword filter")

    job.setJarByClass(classOf[SwapMapper])
    job.setMapperClass(classOf[SwapMapper])
    job.setReducerClass(classOf[CardinalityReducer])

    job.setOutputKeyClass(classOf[LongWritable])
    job.setOutputValueClass(classOf[LongWritable])

    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    if (job.waitForCompletion(true)) 0 else 1
  }
}

class SwapMapper extends Mapper[Object, Text, LongWritable, LongWritable] {
  override def map(key: Object, value: Text, context: Mapper[Object, Text, LongWritable, LongWritable]#Context) {
    val Array(articleId, keywordId) = value.toString.split("\\t")
    context.write(keywordId.toLong, articleId.toLong)
  }
}

class CardinalityReducer extends Reducer[LongWritable, LongWritable, LongWritable, LongWritable] {
  override def reduce(key: LongWritable, values: Iterable[LongWritable], context: Reducer[LongWritable, LongWritable, LongWritable, LongWritable]#Context) {
    val articleIdList = new MutableList[Long]
    values.foreach(aid => articleIdList += aid.get)
    if (articleIdList.toSet.size >= context.getConfiguration.getInt(KeywordFilter.THRESHOLD, 1))
      articleIdList.foreach(aid => context.write(aid, key))
  }
}