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

package ie.deri.uimr.crosscomanalysis.keywords.tokenizer

import java.io.FileInputStream
import opennlp.tools.sentdetect.{SentenceModel, SentenceDetectorME}
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME}
import ie.deri.uimr.crosscomanalysis.keywords.tfidf.StopWords.isStopWord

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 26-Nov-2010
 * Time: 15:50:25
 * ©2010 Digital Enterprise Research Institute, NUI Galway. 
 */

class AbstractTokenizer(sentenceModelFP: String, tokensModelFP: String) {
  
  val sentenceDetector = new SentenceDetectorME(new SentenceModel(new FileInputStream(sentenceModelFP)))
  val tokenizer = new TokenizerME(new TokenizerModel(new FileInputStream(tokensModelFP)))

  def tokenize(ab: String) = {
    for (sentence <- sentenceDetector.sentDetect(ab);
         token <- tokenizer.tokenize(sentence) if !isStopWord(token))
    yield token
  }
}