package com.helloingob.nineninetynine.data;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.nineninetynine.Settings;

//stackoverflow.com/questions/3272681/httpurlconnection-thread-safety
public class HTTPConnection {
    private static final Logger logger = LogManager.getLogger(Settings.Logger.DEFAULT);

    private HttpClient httpClient;
    private RequestConfig requestConfig;
    private String userAgent;

    public HTTPConnection(String userAgent) {
        this.userAgent = userAgent;
        httpClient = HttpClientBuilder.create().build();
        requestConfig = RequestConfig.custom().setCircularRedirectsAllowed(true).setSocketTimeout(Settings.Connection.TIMEOUT).setConnectTimeout(Settings.Connection.TIMEOUT).setConnectionRequestTimeout(Settings.Connection.TIMEOUT).build();
    }

    public HttpResponse doGet(String url) {
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        if (userAgent != null) {
            request.setHeader("User-Agent", userAgent);
        }
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en,de;q=0.5");
        request.setHeader("Accept-Encoding", "gzip, deflate");

        try {
            return httpClient.execute(request);
        } catch (Exception e) {
            logger.error(e, e);
        }
        return null;
    }

}
