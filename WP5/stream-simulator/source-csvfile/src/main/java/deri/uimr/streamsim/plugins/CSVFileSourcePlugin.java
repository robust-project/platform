/**
 * Copyright 2013 DERI, National University of Ireland Galway.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package deri.uimr.streamsim.plugins;

import deri.uimr.streamsim.Event;
import deri.uimr.streamsim.EventsQueue;

import deri.uimr.utilities.Logger;
import deri.uimr.utilities.SmartProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Basic source plugin which reads events from a CSV file.
 *
 * <p>This plugin uses a properties file to specify the CSV file to read. See {@link #DEFAULT_PROPERTIES_FILENAME}.</p>
 * <p>It uses a simple property name: {@code csvfile} with a default value of {@code events.csv}.</p>
 * <p>The format of the CSV file must be:</p>
 *
 * <pre>
 * TIMESTAMP1;EVENT_STRING_REPRESENTATION
 * TIMESTAMP2;EVENT_STRING_REPRESENTATION
 * TIMESTAMP3;EVENT_STRING_REPRESENTATION
 * </pre>
 *
 * <p>The timestamps are milliseconds since UNIX epoch. It is recommended (but not necessary) that the file is sorted by timestamps in ascending order.</p>
 *
 * <p>This plugin will mark all events to be of type {@code GENERIC}.</p>
 *
 * @author Hugo Hromic
 * @since 3.0
 * @see SourcePlugin
 * @see SmartProperties
 */
public class CSVFileSourcePlugin extends AbstractSourcePlugin {
    /**
     * Default plugin properties file name.
     *
     * <p>Value: {@code csvfilesourceplugin.properties}.</p>
     */
    public static final String DEFAULT_PROPERTIES_FILENAME = "csvfilesourceplugin.properties";

    /**
     * The number of read events from file for this source plugin.
     */
    private volatile long numberReadEvents = 0;

    /**
     * The file path pointing to the CSV file to read.
     */
    private final String filePath;

    /**
     * Creates a new {@code CSVFileSourcePlugin} with specified name and eventsQueue.
     *
     * @param name the runnable name for this source plugin.
     * @param eventsQueue the {@code EventsQueue} this source plugin must use.
     * @see EventsQueue
     */
    public CSVFileSourcePlugin(final String name, final EventsQueue eventsQueue) {
        super(name, eventsQueue);

        // Get the filename to read from properties
        final SmartProperties properties = new SmartProperties();
        properties.loadFromFile(DEFAULT_PROPERTIES_FILENAME);
        filePath = properties.getString("csvfile", "events.csv");
    }

    /**
     * Gets the number of read events for this source plugin.
     *
     * @return the number of read events for this source plugin.
     */
    public long getNumberReadEvents() {
        return numberReadEvents;
    }

    /**
     * Gets the file path of the CSV file for this source plugin.
     *
     * @return the file path of the CSV file for this source plugin.
     */
    public String getFilePath() {
        return filePath;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        Logger.log("Started.");

        // Read events from the CSV file
        Logger.log("CSV file path: " + filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while( !Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null ) {
                final String[] fields = line.trim().split(";", 2);
                if( fields.length < 2 ) continue;

                // Parse event from CSV line
                try {
                    final long timestamp = Long.parseLong(fields[0]);
                    final Event event = new Event(timestamp, "GENERIC", fields[1]);
                    eventsQueue.put(event);
                    numberReadEvents++;
                }
                catch( NumberFormatException e ) {
                    Logger.log("Invalid CSV line: " + e.getMessage());
                }
                catch( InterruptedException e ) {
                    Logger.log("Interrupted.");
                    break;
                }
            }
            Logger.log("Processed " + numberReadEvents + " event(s) from file.");
        }
        catch( FileNotFoundException e ) {
            Logger.log("Error: file not found.");
        }
        catch( IOException e ) {
            Logger.log("Error while reading file: " + e.getMessage());
        }
        finally {
            if( reader != null )
                try {
                    reader.close();
                }
                catch( IOException e ) {
                    Logger.log("Error while closing file: " + e.getMessage());
                }
        }

        // Terminated
        Logger.log("Terminated.");
    }
}
