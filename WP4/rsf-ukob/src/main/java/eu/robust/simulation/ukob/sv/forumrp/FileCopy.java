/*
 * Copyright 2012 University of Southampton IT Innovation Centre 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by       	: Mariusz Jacyno
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */ 
package eu.robust.simulation.ukob.sv.forumrp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class FileCopy 
{
  public static void main(String[] args) 
  {
    try 
    {
      copy("fromFile.txt", "toFile.txt");
    } 
    catch (IOException e) 
    {
      System.err.println(e.getMessage());
    }
  }

  public static void copy(String fromFileName, String toFileName)
      throws IOException {
    File fromFile = new File(fromFileName);
    File toFile = new File(toFileName);
    if(fromFile.length()>0){

        if (!fromFile.exists())
            throw new IOException("FileCopy: " + "no such source file: "
                + fromFileName);
          if (!fromFile.isFile())
            throw new IOException("FileCopy: " + "can't copy directory: "
                + fromFileName);
          if (!fromFile.canRead())
            throw new IOException("FileCopy: " + "source file is unreadable: "
                + fromFileName);

          if (toFile.isDirectory())
            toFile = new File(toFile, fromFile.getName());

            {
            String parent = toFile.getParent();
            if (parent == null)
              parent = System.getProperty("user.dir");
            File dir = new File(parent);
            if (!dir.exists())
              throw new IOException("FileCopy: "
                  + "destination directory doesn't exist: " + parent);
            if (dir.isFile())
              throw new IOException("FileCopy: "
                  + "destination is not a directory: " + parent);
            if (!dir.canWrite())
              throw new IOException("FileCopy: "
                  + "destination directory is unwriteable: " + parent);
          }

          FileInputStream from = null;
          FileOutputStream to = null;
          try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1)
              to.write(buffer, 0, bytesRead); // write
          } finally {
            if (from != null)
              try {
                from.close();
              } catch (IOException e) {
                ;
              }
            if (to != null)
              try {
                to.close();
              } catch (IOException e) {
                ;
              }
          }
    }

  }
}
