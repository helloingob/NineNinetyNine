package com.helloingob.nineninetynine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Settings {

    public static class General {
        public static final String FILEPATH_SETTINGS = "settings.properties";
        public static final String DEFAULT_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    }

    public class Logger {
        public static final String DEFAULT = "output";
    }

    public static class Connection {
        public static final int TIMEOUT = 30000;
        public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0";
    }

    public static class Url {
        public static final String EXCHANGE_LIST = "https://www.gamestop.de/eintauschliste";
        public static final String EBAY_CLASSIFIED_BASE = "http://ebay-kleinanzeigen.de";
        //title, plz, radius, maxprice        
        public static final String EBAY_CLASSIFIED_TEMPLATE = EBAY_CLASSIFIED_BASE + "/s-suchanfrage.html?keywords=%s&locationStr=%s&radius=%s&sortingField=SORTING_DATE&pageNum=1&action=find&maxPrice=%s";
    }

    public static class Output {
        public static final String FILENAME = getOutputFilename();
        public static final String FILEPATH_JQUERY = "jquery/jquery-2.1.4.min.js";
        public static final String FILEPATH_TABLESORTER = "jquery/jquery.tablesorter.js";
        public static final String FILEPATH_TABLESORTER_PAGER = "jquery/jquery.tablesorter.pager.js";
        public static final String FILEPATH_CSS = "css/style.css";
        public static final String FILEPATH_CSS_AWESOME = "css/font-awesome.css";

        //table, copyright
        public static final String HTML_TEMPLATE = getHtmlTemplate();

        public static final String YES_SYMBOL = "fa fa-check fa-lg";
        public static final String NO_SYMBOL = "fa fa-close fa-lg";

        public static final String INACTIVE_COLOR = "color:#E6E6E6";
    }

    public static class Formatter {
        public static final String PLUS_LINE = "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
        public static final String DOUBLE_LINE = "==============================================================";
        public static final String SINGLE_LINE = "--------------------------------------------------------------";
    }

    public static class Sleeper {
        public static final int MINIMUM_NORMAL = 500;
        public static final int RANDOM_NORMAL = 500;
    }

    private static String getOutputFilename() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "results_" + simpleDateFormat.format(new Date()) + ".html";
    }

    private static String getHtmlTemplate() {
        StringBuffer stringbuffer = new StringBuffer();

        //@formatter:off
        stringbuffer.append("<!DOCTYPE html>")
                    .append("<html>")
                        .append("<head>")
                            .append("<meta charset=\"UTF-8\"><meta name=\"author\" content=\"Ingo 'the Boss' Bacher\">")
                            .append("<title>Nine.NinetyNine</title>")
                            .append("<script type=\"text/javascript\" src=\"").append(Output.FILEPATH_JQUERY).append("\"></script>")
                            .append("<script type=\"text/javascript\" src=\"").append(Output.FILEPATH_TABLESORTER).append("\"></script>")
                            .append("<script type=\"text/javascript\" id=\"js\">$(document).ready(function() { $(\"table\").tablesorter(); });</script>")
                            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(Output.FILEPATH_CSS).append("\"></link>")
                            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" ).append(Output.FILEPATH_CSS_AWESOME).append("\"></link>")
                        .append("</head>")
                        .append("<body>")
                            .append("<div align=\"center\">%s%s</div>")
                        .append("</body>")
                    .append("</html>");                        
        //@formatter:on

        return stringbuffer.toString();
    }

}
