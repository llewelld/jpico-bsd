/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * Adds simply displaying the stored passwords as a fallback.
 * 
 * The passwords are not sent by the byte or json serialisers.
 * 
 * @author cw471
 * 
 */
public class BrowserPasswordAuthToken extends BrowserAuthToken {

    private final Map<String, String> formFields;

    public BrowserPasswordAuthToken(
            final Collection<Cookie> cookieStrings,
            final URL loginUrl,
            final URL redirectUrl,
            final String responseBody,
            final Map<String, String> formFields) {
        super(cookieStrings, loginUrl, redirectUrl, responseBody);
        this.formFields = formFields;
    }

    /**
     * For a Password Auth token, the fallback is simply the username and password displayed to the
     * user, as session cookies are unlikely to be transcribable.
     * 
     * This sacrifices the benefit of never revealing the long term tokens to the terminal.
     */
    @Override
    public String getFallback() {

        final StringBuilder fallBackString = new StringBuilder();
        for (Map.Entry<String, String> pair : formFields.entrySet()) {
            fallBackString.append(pair.getKey());
            fallBackString.append(": ");
            fallBackString.append(pair.getValue());
            fallBackString.append('\n');
        }
        return fallBackString.toString();
    }
}
