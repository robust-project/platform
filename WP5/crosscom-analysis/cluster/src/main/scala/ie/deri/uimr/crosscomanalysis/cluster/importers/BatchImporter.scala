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

package ie.deri.uimr.crosscomanalysis.cluster.importers

import java.io.{FileFilter, File}
import ie.deri.uimr.crosscomanalysis.util.Logging

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/03/2011
 * Time: 14:18
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

trait BatchImporter extends Logging {

  this: ClusteringImporter =>

  def batchImport(dir: File, modulesFileName: String, flag: File => Option[String]) {
    for (inputDir <- dir.listFiles(new FileFilter {
      def accept(f: File) = f.isDirectory
    }).sortBy(_.getName)) {
      val fileName = inputDir.getAbsolutePath + File.separator + modulesFileName
      log.info("Importing file " + fileName)
      importFile(new File(fileName), flag)
    }
  }

  def batchImport(dir: File, modulesFileName: String) {
    val flag = (f: File) => None
    batchImport(dir, modulesFileName, flag)
  }
}