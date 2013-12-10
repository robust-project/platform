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

package ie.deri.uimr.crosscomanalysis.dblpcsx

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 02/02/2011
 * Time: 13:26
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait SearchTitle {
  def createSearchTitle(title: String) = {
    var t = title.toLowerCase()
    if (t.endsWith(".")) t = t.substring(0, t.length() - 1)
    t = removeHtml(t)
    t = GoogleScholarScraper.removePunctuation(t)
    t = GoogleScholarScraper.removeStopWords(t, GoogleScholarScraper.loadStopWords())
    t = t.replaceAll(" ", "")

    t
  }

  private def removeHtml(st:String) = {
		var s = st.replaceAll("<sup>","")
		s = s.replaceAll("</sup>","")
		s = s.replaceAll("<sub>","")
		s = s.replaceAll("</sub>","")
		s = s.replaceAll("<i>","")
		s = s.replaceAll("</i>","")
		s = s.replaceAll("<tt>","")
		s = s.replaceAll("</tt>","")

		s
	}
}