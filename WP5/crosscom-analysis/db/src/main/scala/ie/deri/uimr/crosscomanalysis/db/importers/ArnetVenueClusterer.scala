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

package ie.deri.uimr.crosscomanalysis.db.importers

import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.ArnetSchema._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 20/08/2013
 * Time: 19:49
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
object ArnetVenueClusterer extends DBArgsParser with SessionFactorySetter {
  override val COMMAND_NAME = "arnet-venue-clusterer"

  def cluster() {
    val venueId = transaction {
      from(venue)(v => select(v.name, v.id)).toMap
    }

  }
}
