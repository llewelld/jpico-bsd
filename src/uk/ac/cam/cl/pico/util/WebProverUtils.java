package uk.ac.cam.cl.pico.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.pico.crypto.Cookie;
import uk.ac.cam.cl.pico.crypto.LensProver;

public class WebProverUtils {
    private final static Logger LOGGER =
            LoggerFactory.getLogger(LensProver.class.getSimpleName());
    private final static String INPUT_TYPE_PASSWORD =
            "input[type=password]";
    private final static String ACCEPT_ENCODING = "";
    private final static String USER_AGENT =
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:27.0) Gecko/20100101 "
                    + "Firefox/27.0";
    private final static String CONTENT_TYPE =
            "application/x-www-form-urlencoded";


    private static class PostStringBuilder {

        private final StringBuilder b = new StringBuilder();

        @Override
        public String toString() {
            return b.toString();
        }

        void add(final String key, final String value) {
            // Verify the method's preconditions
            assert (key != null);
            assert (value != null);

            try {
                if (b.length() > 0) {
                    b.append("&");
                }
                b.append(URLEncoder.encode(key, "UTF-8"));
                b.append("=");
                b.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 not supported", e);
            }
        }
    }

    /**
     * Build the login form POST data string. This method takes the login form {@link FormElement}
     * and the saved pairing credentials and produces the appropriate POST data string.
     * 
     * <p>
     * For each <code>input</code> login form element:
     * <ul>
     * <li>If its <code>name</code> appears in the <code>credentials</code> map, it is included in
     * the POST data with the corresponding value in the map.
     * <li>If its <code>type</code> is <code>hidden</code>, it is included in the POST data with its
     * own HTML <code>value</code>.
     * <li>Otherwise it is ignored.</li>
     * </ul>
     * 
     * <p>
     * The final case is so that names and values of checkboxes, radio buttons, submit buttons and
     * other elements are not submitted, if they were not saved during the initial pairing.
     * 
     * @param loginForm
     * @param credentials
     * @return
     */
    public static String buildPostData(
            final FormElement loginForm,
            final Map<String, String> credentials) {
        // Verify the method's preconditions
        assert (loginForm != null);
        assert (credentials != null);

        final PostStringBuilder builder = new PostStringBuilder();

        final List<Connection.KeyVal> data = loginForm.formData();
        for (Connection.KeyVal kv : data) {
            final String k = kv.key();
            String v = null;
            if (credentials.containsKey(k)) {
                LOGGER.debug("Saved credentials contains key {}", k);
                v = credentials.get(k);
            } else {
                final Element inputElement =
                        loginForm.select("input[name=" + k + "]").first();
                if (inputElement != null &&
                        inputElement.attr("type").equals("hidden")) {
                    LOGGER.debug("Form field with name {} is hidden", k);
                    v = kv.value();
                } else {
                    // Ignore the field -- We don't want things like unchecked
                    // checkbox fields, submit buttons etc. from being added to
                    // the request post data.
                    v = null;
                }
            }

            if (v != null) {
                LOGGER.debug("Setting {}={}", k, v);
                builder.add(k, v);
            } else {
                LOGGER.debug("Ignoring {}", k);
                // Nothing added to post data
            }
        }

        return builder.toString();
    }

    public static URL getRedirectUrl(final HttpURLConnection connection)
            throws IOException {
        // Verify the method's preconditions
        assert (connection != null);

        final int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
				responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
				responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
            final String locationHeader =
                    connection.getHeaderField("Location");
            if (locationHeader != null) {
                try {
                    return new URL(locationHeader);
                } catch (MalformedURLException e) {
                    // Location header may contain a relative URL
                    try {
                        return new URL(connection.getURL(), locationHeader);
                    } catch (MalformedURLException ex) {
                        LOGGER.error(
                                "Malformed redirect location: {}", locationHeader);
                        throw new IOException("Malformed redirect location: {}" +
                                locationHeader);
                    }
                }
            } else {
                LOGGER.warn(
                        "Response had a redirect response code, " +
                                "but no Location header");
                throw new IOException(
                        "Response had a redirect response code," +
                                "but no Location header");
            }
        } else {
            if (responseCode != HttpURLConnection.HTTP_OK) {
                LOGGER.warn("Response was not a redirect or OK");
                throw new IOException("Response was not a redirect or OK");
            }
            return null;
        }
    }

    public static FormElement getLoginForm(final Document loginPage)
            throws IOException {
        // Verify the method's preconditions
        assert (loginPage != null);

        final Elements forms = loginPage.select("form");
        if (!forms.isEmpty()) {
            FormElement loginForm = null;
            int numLoginForms = 0;

            for (final Element form : forms) {
                LOGGER.trace("Checking form for password fields");
                final Elements passwordFields =
                        form.select(INPUT_TYPE_PASSWORD);
                if (!passwordFields.isEmpty()) {
                    ++numLoginForms;
                    if (loginForm == null) {
                        loginForm = (FormElement) form;
                    }
                }
            }

            if (numLoginForms == 0) {
                LOGGER.warn("No forms with password fields found");
                throw new IOException("No forms with password fields found");
            } else if (numLoginForms > 1) {
                LOGGER.warn(
                        "Multiple forms with password fields found, first one "
                                + "returned");
            } else {
                LOGGER.debug("Found single form with password field {}",
                        loginForm);
            }

            return loginForm;
        } else {
            LOGGER.warn("No forms found!");
            LOGGER.trace("Login page content: {}", loginPage.html());
            throw new IOException("No forms found");
        }
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     * 
     * @param tokens an array objects to be joined. Strings will be formed from the objects by
     *        calling object.toString().
     */
    public static String join(CharSequence delimiter, Iterable<?> tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    public static HttpURLConnection makeRequest(
            final URL url, final URL referer, final String postData, final String cookieString)
            throws IOException {

        // Verify the method's preconditions
        assert (url != null);
        assert (referer != null);
        assert (postData != null);

        LOGGER.debug("Making request to {}", url);
        final HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);

        // Set other request headers
        connection.setRequestProperty("Accept-Encoding", ACCEPT_ENCODING);
        connection.setRequestProperty("Cookie", cookieString);
        
        connection.setRequestProperty("User-Agent", USER_AGENT);
        if (referer != null) {
            connection.setRequestProperty("Referer", referer.toString());
        }

        // Write post data
        if (postData != null) {

            LOGGER.trace("Request POST data: {}", postData);
            final byte[] postBytes = postData.getBytes("UTF-8");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", CONTENT_TYPE);
            connection.setRequestProperty(
                    "Content-length", Integer.toString(postBytes.length));

            LOGGER.trace(
                    "Request headers: {}", connection.getRequestProperties());

            connection.setDoOutput(true);
            OutputStream os = null;
            try {
                os = connection.getOutputStream();
                os.write(postBytes);
                os.flush();
            } finally {
                if (os != null) {
                    os.close();
                }
            }
        }
        else {
            LOGGER.trace(
                    "Request headers: {}", connection.getRequestProperties());
        }

        // Make the request
        connection.connect();

        LOGGER.debug("Response code: {}", connection.getResponseCode());
        LOGGER.trace("Response headers: {}", connection.getHeaderFields());

        return connection;
    }
}
