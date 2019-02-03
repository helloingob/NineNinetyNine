package com.helloingob.nineninetynine.data;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.nineninetynine.Settings;

public class GamestopHandler {

    private static final Logger logger = LogManager.getLogger(Settings.Logger.DEFAULT);

    public GamestopHandler() {
        // TODO Auto-generated constructor stub
    }

    public List<String> getList() {
        String responseText = null;

        HttpResponse response = new HTTPConnection(Settings.Connection.USER_AGENT).doGet(Settings.Url.EXCHANGE_LIST);
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                responseText = EntityUtils.toString(responseEntity);
                return parseGamelist(responseText);
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        return null;
    }

    private static List<String> parseGamelist(String pageSource) {
        final String START_TAG = "<td>";
        final String END_TAG = "</td>";
        List<String> exchangeList = new ArrayList<String>();
        StringBuffer stringBuffer = new StringBuffer(pageSource);
        while (stringBuffer.indexOf(START_TAG) > 0) {
            stringBuffer.delete(0, stringBuffer.indexOf(START_TAG) + START_TAG.length());
            String game = stringBuffer.substring(0, stringBuffer.indexOf(END_TAG));
            exchangeList.add(filterGameString(game));
            stringBuffer.delete(0, stringBuffer.indexOf(END_TAG));
        }
        return exchangeList;
    }

    private static String filterGameString(String beforeFilterString) {
        if (beforeFilterString.contains("<em>")) {
            beforeFilterString = beforeFilterString.replaceAll("<em>", "").replaceAll("</em>", "");
        }
        return beforeFilterString;
    }

}
