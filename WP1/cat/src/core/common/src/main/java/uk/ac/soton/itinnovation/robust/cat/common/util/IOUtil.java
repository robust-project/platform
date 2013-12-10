/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2011
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Vegard Engen
//      Created Date :          2011-11-23
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Vegard Engen
 */
public class IOUtil
{
    /**
     * Reads a space/tab separated key-value file and returns a map with the content.
     * Any comments in the file can be prepended with # or %
     * @param filename
     * @return
     * @throws Exception 
     */
    public static Map<String, String> readKeyValueFile(String filename) throws Exception
    {
        File f = new File (filename);
        if (!f.exists())
        {
            //logger.error("The configuration file could not be found: '" + filename + "'");
            throw new FileNotFoundException("The configuration file could not be found: '" + f.getAbsolutePath() + "'");
        }
        
        Map<String, String> paramMap = new HashMap<String, String>();
        
        FileReader fr = null;
        BufferedReader br = null;
        
        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            String line = null;
            while((line = br.readLine()) != null)
            {
                if (!line.trim().isEmpty() && !line.startsWith("#") && !line.startsWith("%"))
                {
                    StringTokenizer st = new StringTokenizer(line, " \t");
                    if (st.countTokens() == 2)
                    {
                        paramMap.put(st.nextToken(), st.nextToken());
                    }
                    else if (st.countTokens() > 2) // might be a value containg spaces
                    {
                        String key = st.nextToken();
                        String value = "";
                        
                        while (st.hasMoreTokens())
                            value += st.nextToken() + " ";
                        
                        paramMap.put(key, value.trim());
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (br != null)
                br.close();
        }
        
        return paramMap;
    }
}
