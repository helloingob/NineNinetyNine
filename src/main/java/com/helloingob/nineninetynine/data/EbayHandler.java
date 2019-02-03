package com.helloingob.nineninetynine.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.helloingob.nineninetynine.Settings;

public class EbayHandler {
    private static final Logger logger = LogManager.getLogger(Settings.Logger.DEFAULT);
    private int plz;
    private Integer radius;
    private Integer maxPrice;
    private Integer lessthan;
    private Boolean verbose;

    public EbayHandler(Integer plz, Integer radius, Integer maxPrice, Integer lessthan, Boolean verbose) {
        this.plz = plz;
        this.radius = radius;
        this.maxPrice = maxPrice;
        this.lessthan = lessthan;
        this.verbose = verbose;
    }

    public List<ClassifiedAdvertisementTO> query(String searchString) {
        String formattetSearchString = searchString.replaceAll(" - ", "+").replaceAll("\\s", "+").toLowerCase();
        String radiusStr;

        String maxPriceStr;
        if (radius == null) {
            radiusStr = "";
        } else {
            radiusStr = String.valueOf(radius);
        }
        if (maxPrice == null) {
            maxPriceStr = "";
        } else {
            maxPriceStr = String.valueOf(maxPrice);
        }

        String formattedEbayUrl = String.format(Settings.Url.EBAY_CLASSIFIED_TEMPLATE, formattetSearchString, plz, radiusStr, maxPriceStr);
        if (verbose) {
            logger.info("\t" + formattedEbayUrl);
        }
        String responseText = null;

        HttpResponse response = new HTTPConnection(Settings.Connection.USER_AGENT).doGet(formattedEbayUrl);
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                responseText = EntityUtils.toString(responseEntity);
                return getClassifiedAdvertisementFromPageSource(responseText, searchString.substring(0, searchString.indexOf("-") - 1));
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        return Collections.emptyList();
    }

    public List<ClassifiedAdvertisementTO> getClassifiedAdvertisementFromPageSource(String pageSource, String console) {
        List<ClassifiedAdvertisementTO> classifiedAdvertisements = new LinkedList<ClassifiedAdvertisementTO>();
        Integer notMatching = 0;
        Integer overallCount = 0;
        try {
            Document document = Jsoup.parse(pageSource, Settings.Url.EBAY_CLASSIFIED_BASE);
            Elements items = document.select(".aditem");
            overallCount = items.size();
            for (Element item : items) {
                ClassifiedAdvertisementTO classifiedAdvertisementTO = new ClassifiedAdvertisementTO();

                //link
                classifiedAdvertisementTO.setLink(item.select(".aditem-image").first().select("div").attr("abs:data-href"));

                //image
                classifiedAdvertisementTO.setImage(item.select(".aditem-image").first().select("div").attr("data-imgsrc"));

                //title
                classifiedAdvertisementTO.setTitle(item.select(".aditem-main").first().select("a").text());

                //short description
                classifiedAdvertisementTO.setShortDescription(item.select(".aditem-main").first().select("p").text());

                //price 
                String priceString = item.select(".aditem-details").first().select("strong").text();
                try {
                    if (priceString.toLowerCase().contains("zu verschenken")) {
                        classifiedAdvertisementTO.setPrice(null);
                        classifiedAdvertisementTO.setFree(true);
                    } else {
                        if (priceString.contains("VB")) {
                            classifiedAdvertisementTO.setVb(true);
                        }

                        try {
                            priceString = priceString.replaceAll("\u20ac", "").replaceAll("VB", "").replaceAll("\\.", "").replaceAll("\\s", "");
                            classifiedAdvertisementTO.setPrice(Integer.parseInt(priceString));
                        } catch (Exception e) {
                            classifiedAdvertisementTO.setPrice(null);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e, e);
                    logger.info(priceString);
                }

                //plz, ort, distance
                String section = item.select(".aditem-details").first().toString();
                String[] splittedDetails = section.split("<br>");
                classifiedAdvertisementTO.setPlz(parsePlz(splittedDetails[1]));
                classifiedAdvertisementTO.setLocation(splittedDetails[2].replaceAll("\\s", ""));
                classifiedAdvertisementTO.setDistance(parseDistance(splittedDetails[3]));

                //date
                DateFormat currentDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                DateFormat defaultDateTimeFormat = new SimpleDateFormat(Settings.General.DEFAULT_TIME_FORMAT, Locale.ENGLISH);
                String dateString = item.select(".aditem-addon").first().text();
                Date date = new Date();
                if (dateString.toLowerCase().contains("gestern")) {
                    date = substractDayFromNow(1);
                    String concatedDateString = currentDateFormat.format(date) + " " + dateString.substring(dateString.indexOf(" "), dateString.length());
                    date = defaultDateTimeFormat.parse(concatedDateString);
                } else {
                    if (dateString.toLowerCase().contains("heute")) {
                        date = new Date();
                        String concatedDateString = currentDateFormat.format(date) + " " + dateString.substring(dateString.indexOf(" "), dateString.length());
                        date = defaultDateTimeFormat.parse(concatedDateString);
                    } else {
                        date = defaultDateTimeFormat.parse(dateString + " 00:00");
                    }
                }
                //console
                classifiedAdvertisementTO.setConsole(console);

                classifiedAdvertisementTO.setDate(date);
                if (checkParameter(classifiedAdvertisementTO)) {
                    classifiedAdvertisements.add(classifiedAdvertisementTO);
                } else {
                    notMatching += 1;
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        if (verbose) {
            logger.info("\tfound: " + overallCount);
            logger.info("\tinvalid: " + notMatching);
        }
        return classifiedAdvertisements;

    }

    private boolean checkParameter(ClassifiedAdvertisementTO classifiedAdvertisementTO) {
        //title filter
        if (classifiedAdvertisementTO.getTitle() != null) {
            //@formatter:off
            if (classifiedAdvertisementTO.getTitle().toLowerCase().contains("suche") ||
                classifiedAdvertisementTO.getTitle().toLowerCase().contains("tausche") ||
                classifiedAdvertisementTO.getTitle().toLowerCase().contains("biete") ) {
                return false;
            }
            //@formatter:on
        }

        //too far away
        if (radius != null) {
            if (classifiedAdvertisementTO.getDistance() != null && classifiedAdvertisementTO.getDistance() > radius) {
                return false;
            }
        }
        //too expensive
        if (maxPrice != null) {
            if (classifiedAdvertisementTO.getPrice() != null && classifiedAdvertisementTO.getPrice() > maxPrice) {
                return false;
            }
        }

        //too old
        if (lessthan != null) {
            if (classifiedAdvertisementTO.getDate() != null && getTimeDifferenceInHours(new Date(), classifiedAdvertisementTO.getDate()) > lessthan * 24) {
                return false;
            }
        }

        //just rite!
        return true;
    }

    private long getTimeDifferenceInHours(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        return diff / (60 * 60 * 1000);
    }

    private Date substractDayFromNow(int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    private Integer parsePlz(String input) {
        try {
            input = input.replaceAll("\\s", "");
            if (!input.trim().isEmpty()) {
                return Integer.parseInt(input);
            }
        } catch (Exception e) {
            logger.error(e, e);
            logger.info(input);
        }
        return null;
    }

    private Integer parseDistance(String input) {
        try {
            input = input.replace("</div>", "").replaceAll("\\s", "");
            input = input.replace("ca.", "").replace("km", "");
            if (!input.trim().isEmpty()) {
                return Integer.parseInt(input);
            }
        } catch (Exception e) {
            logger.error(e, e);
            logger.info(input);
        }
        return null;
    }

}
