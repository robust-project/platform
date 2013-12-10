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

package deri.uimr.streamsim;

import deri.uimr.utilities.Logger;
import deri.uimr.utilities.SmartProperties;

/**
 * Singleton class to hold simulator run-time properties.
 *
 * <p>Run-time properties are read from file {@link #DEFAULT_PROPERTIES_FILENAME}.</p>
 * <p>The file format is the same for standard Java {@code Properties} class.</p>
 *
 * <p>Please see below for an example:</p>
 *
 * <pre>
 * # Begin example properties file.
 * events_queue_size = 500000
 * start_delay = 10000.0
 * streaming_mode = NORMAL
 * speed_rate = 1.0
 * constant_delay = 25.0
 * source_plugins = org.example.sources.MySourcePlugin, com.domain.MySecondSourcePlugin
 * delivery_plugins = org.example.deliveries.MyDeliveryPlugin, com.domain.MySecondDeliveryPlugin
 * webadmin_enabled = true
 * webadmin_host = 127.0.0.1
 * webadmin_port = 8082
 * </pre>
 *
 * @see SmartProperties
 * @see java.util.Properties
 *
 * @since 1.0
 */
public class SimulatorProperties extends SmartProperties {
    /**
     * Default simulator properties file name.
     *
     * Value: {@code streamsim.properties}
     */
    public static final String DEFAULT_PROPERTIES_FILENAME = "streamsim.properties";

    /**
     * Creates a new {@code SimulatorProperties} object and loads properties from file.
     *
     * This constructor is private, because this class is a singleton.
     */
    private SimulatorProperties() {
        loadFromFile();
    }

    /**
     * Singleton holder class
     */
    private static class SingletonHolder {
        public static final SimulatorProperties instance = new SimulatorProperties();
    }

    /**
     * Gets the singleton instance of this {@code SimulatorProperties} class.
     *
     * @return the singleton instance of the {@code SimulatorProperties} class.
     */
    public static SimulatorProperties getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Loads simulator properties from the default file.
     *
     * @see #DEFAULT_PROPERTIES_FILENAME
     * @see SmartProperties#loadFromFile
     */
    public void loadFromFile() {
        loadFromFile(DEFAULT_PROPERTIES_FILENAME);
    }

    /**
     * Gets the value of the {@code events_queue_size} property.
     *
     * <p>Number of events to hold in the queue for further stream simulation.</p>
     * <p>The default value is {@code 250000}.</p>
     *
     * @return the value of the {@code events_queue_size} property.
     */
    public int getEventsQueueSize() {
        return parseInteger("events_queue_size", 250000);
    }

    /**
     * Gets the value of the {@code start_delay} property.
     *
     * <p>Delay (in milliseconds, fractionals accepted) before beginning the simulation.</p>
     * <p>The default value is {@code 10000.0}.</p>
     *
     * @return the value of the {@code start_delay} property.
     */
    public double getStartDelay() {
        return parseDouble("start_delay", 10000.0);
    }

    /**
     * Gets the value of the {@code streaming_mode} property.
     *
     * <p>The streaming modes are defined in the {@code StreamerRunnable} class. See description for more information.</p>
     * <p>The default value is taken from {@link StreamerRunnable.StreamingModes#getDefault()}.</p>
     *
     * @return the value of the {@code streaming_mode} property.
     * @see StreamerRunnable.StreamingModes
     */
    public StreamerRunnable.StreamingModes getStreamingMode() {
        return parseEnumeration("streaming_mode", StreamerRunnable.StreamingModes.getDefault(), StreamerRunnable.StreamingModes.class);
    }

    /**
     * Gets the value of the {@code speed_rate} property.
     *
     * <p>In {@code NORMAL} simulation mode, the stream speed rate factor to use ({@code <1.0}: slower, {@code >1.0}: faster, {@code =1.0}: real-time).</p>
     * <p>For example, using a {@code 2.0} value means the stream is simulated twice as fast.</p>
     * <p>The default value is {@code 1.0}.</p>
     *
     * @return the value of the {@code speed_rate} property.
     * @see StreamerRunnable.StreamingModes
     */
    public double getSpeedRate() {
        return parseDouble("speed_rate", 1.0);
    }

    /**
     * Gets the value of the {@code constant_delay} property.
     *
     * <p>In {@code CONSTANT_DELAY} simulation mode, the constant time delay (in milliseconds, fractionals accepted) to use.</p>
     * <p>The default value is {@code 100.0}.</p>
     *
     * @return the value of the {@code constant_delay} property.
     * @see StreamerRunnable.StreamingModes
     */
    public double getConstantDelay() {
        return parseDouble("constant_delay", 100.0);
    }

    /**
     * Gets an array of class names from the {@code source_plugins} property.
     *
     * <p>List (comma separated) of fully qualified class names to use as source plugins.</p>
     * <p>The default value is an empty list.</p>
     *
     * @return an array of class names from the {@code source_plugins} property.
     * @see deri.uimr.streamsim.plugins.SourcePlugin
     */
    public String[] getSourcePlugins() {
        return parseStringArray("source_plugins");
    }

    /**
     * Gets an array of class names from the {@code delivery_plugins} property.
     *
     * <p>List (comma separated) of fully qualified class names to use as delivery plugins.</p>
     * <p>The default value is an empty list.</p>
     *
     * @return an array of class names from the {@code delivery_plugins} property.
     * @see deri.uimr.streamsim.plugins.DeliveryPlugin
     */
    public String[] getDeliveryPlugins() {
        return parseStringArray("delivery_plugins");
    }

    /**
     * Checks boolean value of the {@code webadmin_enabled} property.
     *
     * <p>Enabled state of the WebAdmin tool.</p>
     * <p>The default value is {@code true}.</p>
     *
     * @return the boolean value of the {@code webadmin_enabled} property.
     */
    public boolean isWebAdminEnabled() {
        return parseBoolean("webadmin_enabled", true);
    }

    /**
     * Gets the value of the {@code webadmin_host} property.
     *
     * <p>The listening address to bind the WebAdmin server to.</p>
     * <p>The default value is {@code localhost}.</p>
     *
     * @return the value of the {@code webadmin_host} property.
     */
    public String getWebAdminHost() {
        return getString("webadmin_host", "localhost");
    }

    /**
     * Gets the value of the {@code webadmin_port} property.
     *
     * <p>The listening port to bind the WebAdmin server to.</p>
     * <p>The default value is {@code 12345}.</p>
     *
     * @return the value of the {@code webadmin_port} property.
     */
    public int getWebAdminPort() {
        return parseInteger("webadmin_port", 12345);
    }
}
