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

package ie.deri.uimr.crosscomanalysis.util

import java.io.FileInputStream

/**
 * Created by IntelliJ IDEA.
 * User: vaclav.belak@deri.org
 * Date: 12/01/2011
 * Time: 17:16
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object Config {
  val config =
    if (System.getProperty("crosscom.config") == null) {
      Properties.loadFile(Thread.currentThread().getContextClassLoader.getResourceAsStream("crosscom-config.properties"))
    } else {
      Properties.loadFile(new FileInputStream(System.getProperty("crosscom.config")))
    }
}