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

package ie.deri.uimr.crosscomanalysis.hadoop

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/05/2011
 * Time: 18:52
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{BooleanWritable, IntWritable, LongWritable, FloatWritable, Text, UTF8}

object SHadoop {

  implicit def writable2boolean(value: BooleanWritable) = value.get
  implicit def boolean2writable(value: Boolean) = new BooleanWritable(value)

  implicit def writable2int(value: IntWritable) = value.get
  implicit def int2writable(value: Int) = new IntWritable(value)

  implicit def writable2long(value: LongWritable) = value.get
  implicit def long2writable(value: Long) = new LongWritable(value)

  implicit def writable2float(value: FloatWritable) = value.get
  implicit def float2writable(value: Float) = new FloatWritable(value)

  implicit def text2string(value: Text) = value.toString
  implicit def string2text(value: String) = new Text(value)

  implicit def uft82string(value: UTF8) = value.toString
  implicit def string2utf8(value: String) = new UTF8(value)

  implicit def path2string(value: Path) = value.toString
  implicit def string2path(value: String) = new Path(value)
}