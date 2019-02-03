package com.helloingob.nineninetynine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.nineninetynine.data.ClassifiedAdvertisementTO;
import com.helloingob.nineninetynine.data.EbayHandler;
import com.helloingob.nineninetynine.data.GamestopHandler;
import com.helloingob.nineninetynine.data.PropertiesReader;
import com.hp.gagawa.java.elements.Table;

public class NineNinetyNineMain {

    private static final Logger logger = LogManager.getLogger(Settings.Logger.DEFAULT);

    public static void main(String[] args) {
        logger.info(Settings.Formatter.DOUBLE_LINE);
        logger.info("started ...");

        Properties properties = PropertiesReader.getProperties();

        if (properties != null && properties.containsKey("plz")) {
            Integer radius = null;
            Integer maxprice = null;
            Integer lessthan = null;
            Boolean verbose = false;
            int count = 0;

            if (properties.containsKey("radius")) {
                radius = Integer.parseInt(properties.getProperty("radius"));
                logger.info("radius set to " + radius + " km.");
            }
            if (properties.containsKey("maxprice")) {
                maxprice = Integer.parseInt(properties.getProperty("maxprice"));
                logger.info("maxprice set to " + maxprice + " euro.");
            }
            if (properties.containsKey("lessthan")) {
                lessthan = Integer.parseInt(properties.getProperty("lessthan"));
                logger.info("classified advertisement older " + lessthan + " day(s) are ignored.");
            }
            if (properties.containsKey("verbose")) {
                verbose = Boolean.valueOf(properties.getProperty("verbose"));
                if (verbose) {
                    logger.info("verbose output enabled.");
                }
            }

            logger.info(Settings.Formatter.SINGLE_LINE);

            EbayHandler ebay = new EbayHandler(Integer.parseInt(properties.getProperty("plz")), radius, maxprice, lessthan, verbose);

            File file = new File(Settings.Output.FILENAME);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    logger.error(e, e);
                }
            }

            BufferedWriter bufferWritter = null;
            try {
                bufferWritter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()), "UTF-8"));
                Table table = OutputFormatter.createTableLayout();

                List<String> gamestopGames = new GamestopHandler().getList();
                for (int i = 0; i < gamestopGames.size(); i++) {
                    logger.info("[" + (i + 1) + "/" + gamestopGames.size() + "] '" + gamestopGames.get(i) + "'");

                    Sleeper.normalSleep(logger, verbose);

                    List<ClassifiedAdvertisementTO> classifiedAdvertisements = ebay.query(gamestopGames.get(i));
                    if (classifiedAdvertisements.size() > 0) {
                        logger.info(Settings.Formatter.PLUS_LINE);
                        logger.info(gamestopGames.get(i));
                        logger.info(Settings.Formatter.SINGLE_LINE);
                        for (ClassifiedAdvertisementTO classifiedAdvertisementTO : classifiedAdvertisements) {
                            logger.info(classifiedAdvertisementTO.toString());
                            table.appendChild(OutputFormatter.createRowLayout(classifiedAdvertisementTO));
                            count += 1;
                        }
                        logger.info(Settings.Formatter.PLUS_LINE);
                    } else {
                        if (!verbose) {
                            logger.info("\tnothing found.");
                        }
                    }
                }
                logger.info(Settings.Formatter.DOUBLE_LINE);
                logger.info(count + " possible matches found.");

                bufferWritter.write(String.format(Settings.Output.HTML_TEMPLATE, table.write(), OutputFormatter.createCopyright()));
            } catch (Exception e) {
                logger.error(e, e);
            } finally {
                if (bufferWritter != null) {
                    try {
                        bufferWritter.close();
                    } catch (IOException e) {
                        logger.error(e, e);
                    }
                }
            }
        } else {
            logger.info(Settings.General.FILEPATH_SETTINGS + " is invalid!");
        }
        logger.info(Settings.Formatter.DOUBLE_LINE);
    }

}
