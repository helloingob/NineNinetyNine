package com.helloingob.nineninetynine;

import java.util.Random;
import org.apache.logging.log4j.Logger;

public class Sleeper {

    public static void normalSleep(Logger logger, Boolean verbose) {
        try {
            Random random = new Random();
            long millis = Settings.Sleeper.MINIMUM_NORMAL + random.nextInt(Settings.Sleeper.RANDOM_NORMAL);
            if (verbose) {
                logger.info("\tsleep (" + millis + " ms)");
            }
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error(e, e);
        }
    }

}
