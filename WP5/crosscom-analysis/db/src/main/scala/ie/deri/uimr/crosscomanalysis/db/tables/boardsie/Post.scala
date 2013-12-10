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

package ie.deri.uimr.crosscomanalysis.db.tables.boardsie

import java.util.Date

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 26/09/2011
 * Time: 17:28
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class Post(val postid: Long, val threadid: Long, val userid: Long, val title: String, val posteddate: Date,
            val content: String, val isop: Boolean)