package uk.ac.cam.cl.pico.util;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.pico.crypto.Cookie;


public class PicoCookieManager extends CookieManager {

    private final static Logger LOGGER =
            LoggerFactory.getLogger(PicoCookieManager.class.getSimpleName());
    private final static String DATE_HEADER = "Date";
    private final static String SET_COOKIE_HEADER = "Set-cookie";
    private final static String SET_COOKIE2_HEADER = "Set-cookie2";

    private final List<Cookie> rawCookies = new ArrayList<Cookie>();

    public List<Cookie> getRawCookies() {
        return rawCookies;
    }

    @Override
    public Map<String, List<String>> get(final URI uri,
            final Map<String, List<String>> responseHeaders)
            throws IOException {

        return super.get(uri, responseHeaders);
    }

    @Override
    public void put(final URI uri,
            final Map<String, List<String>> responseHeaders)
            throws IOException {

        super.put(uri, responseHeaders);

        // Extract the Date from the response headers,
        // a default Date of the current system time is used where the
        // Date header is not present
        String date = null;
        for (final Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {

            // Extract the Date header
            final String key = entry.getKey();
            if (key != null &&
                    key.equalsIgnoreCase(PicoCookieManager.DATE_HEADER)) {
                LOGGER.trace("Date = {}", entry.getValue());
                date = entry.getValue().get(0);
                break;
            }
        }

        for (final Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {

            // Only "Set-cookie" and "Set-cookie2" pair will be parsed
            final String key = entry.getKey();
            if (key != null &&
                    (key.equalsIgnoreCase(PicoCookieManager.SET_COOKIE_HEADER)
                    || key.equalsIgnoreCase(
                            PicoCookieManager.SET_COOKIE2_HEADER))) {

                for (final String cookieString : entry.getValue()) {
                    LOGGER.trace("Adding HTTP cookie ({}) = {}",
                            uri.toString(), cookieString);
                    rawCookies.add(
                            new Cookie(uri.toString(), cookieString, date));
                }
            }
        }
    }
}
