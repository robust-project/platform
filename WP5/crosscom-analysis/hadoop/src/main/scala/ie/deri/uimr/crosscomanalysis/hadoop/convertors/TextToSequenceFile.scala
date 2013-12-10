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

package ie.deri.uimr.crosscomanalysis.hadoop.convertors

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.util.{ToolRunner, Tool}
import org.apache.hadoop.conf.{Configuration, Configured}
import org.apache.hadoop.mapreduce.lib.output.{FileOutputFormat, SequenceFileOutputFormat}
import ie.deri.uimr.crosscomanalysis.hadoop.SHadoop._
import org.apache.hadoop.mapreduce.{Mapper, Job}
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, TextInputFormat}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 15/05/2011
 * Time: 18:06
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object TextToSequenceFile extends Configured with Tool {
  def main(args: Array[String]) {
    val res = ToolRunner.run(new Configuration(), this, args);
    System.exit(res);
  }

  def run(args: Array[String]) = {
    val conf = getConf
    val job = new Job(conf, "text to sequence file convertor")
    job.setJarByClass(classOf[IdentityMapper])

    job.setMapperClass(classOf[IdentityMapper])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

    job.setOutputFormatClass(classOf[SequenceFileOutputFormat[Text, Text]])
    job.setInputFormatClass(classOf[TextInputFormat])

    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    if (job.waitForCompletion(true)) 0 else 1
  }
}

class IdentityMapper extends Mapper[Object, Text, Text, Text] {
  override def map(key: Object, value: Text, context: Mapper[Object, Text, Text, Text]#Context) {
    val Array(authorClusterId, keywordIds) = value.toString.split("\\x01")
    context.write(authorClusterId, keywordIds)
  }
}