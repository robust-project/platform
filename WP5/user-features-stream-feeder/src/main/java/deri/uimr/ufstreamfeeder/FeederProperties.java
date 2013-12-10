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
package deri.uimr.ufstreamfeeder;

import deri.uimr.utilities.Logger;
import deri.uimr.utilities.SmartProperties;

public class FeederProperties extends SmartProperties {
    public static final String PROPERTIES_FILE = "streamfeeder.properties";

    // No public instantiation, this is a singleton
    private FeederProperties() {
        loadFromFile(PROPERTIES_FILE);
    }

    // Singleton holder
    private static class SingletonHolder {
        public static final FeederProperties instance = new FeederProperties();
    }

    // Singleton instance getter
    public static FeederProperties getInstance() {
        return SingletonHolder.instance;
    }

    public String getBrokerURL() {
        return getString("broker_url", "tcp://localhost:61616");
    }

    public String getTopicName() {
        return getString("topic_name", "streams.events");
    }

    public String getMySQLHost() {
        return getString("mysql_host", "127.0.0.1");
    }

    public int getMySQLPort() {
        return parseInteger("mysql_port", 3306);
    }

    public String getMySQLUser() {
        return getString("mysql_user", "guest");
    }

    public String getMySQLPassword() {
        return getString("mysql_password", "guest");
    }

    public String getMySQLDBName() {
        return getString("mysql_dbname", "dbname");
    }

    public String getServletServerHost() {
        return getString("servlet_server_host", "localhost");
    }

    public int getServletServerPort() {
        return parseInteger("servlet_server_port", 9991);
    }
}
