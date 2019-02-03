package com.helloingob.nineninetynine.data;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.nineninetynine.Settings;

public class PropertiesReader {
    private static final Logger logger = LogManager.getLogger(Settings.Logger.DEFAULT);

    public static Properties getProperties() {
        Properties properties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(Settings.General.FILEPATH_SETTINGS);
            properties.load(input);
            return properties;
        } catch (Exception e) {
            logger.error(e, e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    logger.error(e, e);
                }
            }

        }
        return null;
    }

}
