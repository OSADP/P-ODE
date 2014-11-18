package com.leidos.ode.collector.datasource.pull;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/10/14
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class WXDEDataSource extends RestPullDataSource {

    private final String DATE_PATTERN = "yyyyMMdd_HHmm";
    private final String TIME_ZONE = "UTC+5";
    private SimpleDateFormat simpleDateFormat;
    private String uuid;
    private String filename;

    public WXDEDataSource() {
        simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
    }

    @Override
    protected String buildWfsFilter() {
        return new StringBuilder()
                .append("?")
                .append("uuid=")
                .append(getUuid())
                .toString();
    }

    @Override
    public byte[] pollDataSource() {
        try {
            setFilename(determineLatestFilename());
            getLogger().debug("Polling data source for filename: '" + getFilename() + "'.");
            URI uri = getHttpGet().getURI();
            String newUriString = new StringBuilder()
                    .append(uri)
                    .append("&")
                    .append("file=")
                    .append(getFilename())
                    .append(".xml")
                    .toString();
            URI newURI = new URI(newUriString);
            getHttpGet().setURI(newURI);
            CloseableHttpResponse closeableHttpResponse = getHttpClient().execute(getHttpGet());
            HttpEntity responseEntity = closeableHttpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
            EntityUtils.consume(responseEntity);
            closeableHttpResponse.close();
            Thread.sleep(getRequestLimit());
            return responseBytes;
        } catch (ClientProtocolException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (URISyntaxException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }

    private String determineLatestFilename() {
        Date now = new Date();
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        Calendar date = new GregorianCalendar();
        date.setTime(now);
        date.set(Calendar.MINUTE, 0);

        if (minute >= 0 && minute <= 15) {
            date.set(Calendar.MINUTE, 0);
        } else if (minute > 15 && minute <= 30) {
            date.set(Calendar.MINUTE, 15);
        } else if (minute > 30 && minute <= 45) {
            date.set(Calendar.MINUTE, 30);
        } else if (minute > 45 && minute <= 60) {
            date.set(Calendar.MINUTE, 45);
        }

        return simpleDateFormat.format(date.getTime());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
