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

package ie.deri.uimr.crosscomanalysis.dblpcsx;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class GoogleScholarScraper {


    public static String removeStopWords(String title, ArrayList stops) {
        StringTokenizer st = new StringTokenizer(title, " ");
        String newtitle = "";
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (!stops.contains(tok.toLowerCase())) {
                newtitle = newtitle.concat(" " + tok);
            }
        }
        return newtitle.trim();

    }

    public static String removePunctuation(String title) {
        // MKa: added
        title = title.replaceAll("&nbsp;", " "); // ok, we don't need such stuff

        title = title.replaceAll(":", " "); // could be written without or with -
        title = title.replaceAll("!", " "); // may be left out
        title = title.replaceAll("\\?", " "); // may be left out
        title = title.replaceAll(";", " "); // may be left out
        title = title.replaceAll("/", ""); // could be written without or with -
        title = title.replaceAll("_", " "); // could be written without or with -
        title = title.replaceAll("&", " "); // ambiguous in general

        // MKa: changed
//		title = title.replaceAll("-", "");
        title = title.replaceAll("-", " ");

        title = title.replaceAll("'", "");

        // MKa: added
        title = title.replaceAll("\"", "");
        title = title.replaceAll("�", "");

        // MKa: changed
//		title = title.replaceAll("\\(", "\\(");
        title = title.replaceAll("\\(", " ");

        title = title.replaceAll("\u22C6", " ");
        title = title.replaceAll("\\u002B", "");
        title = title.replaceAll("\\u005E", "");
        title = title.replaceAll("\\u0024", "");
        title = title.replaceAll("�", " ");

        // MKa: added
        title = title.replaceAll("\\)", " ");

        // MKa: changed
//		title = title.replaceAll("�", " ");
        title = title.replaceAll("�", "i");
//		title = title.replaceAll("�", " ");
        title = title.replaceAll("�", "e");
//		title = title.replaceAll("�", " ");
        title = title.replaceAll("�", "i");
//		title = title.replaceAll("�", " ");
        title = title.replaceAll("�", "i");
        title = title.replaceAll("#39", " ");
        title = title.replace('.', ' ');
        title = title.replace(',', ' ');

        // MKa: added
//		title = title.replaceFirst("<.*>", "");
        title = title.replaceAll("\\* *[0-9]*", " ");

        // MKa: added loop
        while (title.contains("  ")) title = title.replaceAll("  ", " ");

        title = title.trim();

        return title;
    }


    public static ArrayList<String> loadStopWords() {
        ArrayList<String> out = new ArrayList<String>();

//		String temp = "a,and,are,as,at,be,but,by,can,do,for,has,have,how,i,if,in,into,is,it,no,not,of,on,or,s,so,such,t,that,the,their,then,there,these,they,this,to,was,will,with,what,where,who,why,quot";
        // MKa" removed: "t,s", added "from"
        String temp = "a,and,are,as,at,be,but,by,can,do,for,from,has,have,how,i,if,in,into,is,it,no,not,of,on,or,so,such,that,the,their,then,there,these,they,this,to,was,will,with,what,where,who,why,quot";
        temp += ",poster,session,short,paper,full,best,student,long,version";
        // TODO: this would find one article (4182) and 35 citations for that one more in the no-exact case for all articles up to 9243 - but maybe later articles? maybe other "good" words?
//		temp += ",summary";
        String[] words = temp.split(",");
        for (int x = 0; x < words.length; x++) {
            out.add(words[x].trim());
        }
//		System.err.println(out);
        return out;
    }


}
