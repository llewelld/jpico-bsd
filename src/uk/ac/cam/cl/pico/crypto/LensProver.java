/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.DestroyFailedException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.pico.crypto.ContinuousProver.ProverStateChangeNotificationInterface;
import uk.ac.cam.cl.pico.crypto.ContinuousProver.SchedulerInterface;
import uk.ac.cam.cl.pico.data.pairing.LensPairing;
import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.data.session.SessionImpFactory;
import uk.ac.cam.cl.pico.util.PicoCookieManager;
import uk.ac.cam.cl.pico.util.WebProverUtils;

import com.google.common.base.Preconditions;

final public class LensProver implements Prover {

    private final static Logger LOGGER =
            LoggerFactory.getLogger(LensProver.class.getSimpleName());
    // @see https://code.google.com/p/android/issues/detail?id=24672
    private boolean isDestroyed;

    private final LensPairing pairing;
    private final URI loginUri;
    private final SessionImpFactory sessionFactory;
    private final PicoCookieManager cookieManager;
    private final Document loginForm;
    private final String cookieString;
    
    public LensProver(
            final LensPairing pairing,
            final URI loginUri,
            final String loginForm,
            final String cookieString,
            final SessionImpFactory sessionFactory) {

        // Verify the method's preconditions
        this.pairing = Preconditions.checkNotNull(pairing,
                "LensProver cannot have a null pairing");
        this.loginUri = Preconditions.checkNotNull(loginUri,
                "LensProver cannot have a null loginUri");
        Preconditions.checkNotNull(loginForm,
                "LensProver cannot have a null loginForm");
        this.loginForm = Jsoup.parseBodyFragment(loginForm, loginUri.toString());
        this.cookieString = Preconditions.checkNotNull(cookieString,
                "LensProver cannot have a null cookieString");
        this.sessionFactory = Preconditions.checkNotNull(sessionFactory,
                "LensProver cannot have a null session factory");
        
        // Set cookie handler, keeping reference to cookieManager so we can get
        // back the cookies to build the auth token.
        cookieManager = new PicoCookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    public ContinuousProver getContinuousProver(
            final Session session,
            final ProverStateChangeNotificationInterface notificationInterface,
            final SchedulerInterface schedulerInterface) {
        throw new UnsupportedOperationException(
                "Lens pairings do not support continuous authentication");
    }

    @Override
    public Session startSession() throws CryptoRuntimeException {
        try {            
            // Get the login form         
            final FormElement loginForm =
                    WebProverUtils.getLoginForm(this.loginForm);
            
            // Build the post data for the form submission
            final String postData = WebProverUtils.buildPostData(
                    loginForm, pairing.getCredentials());

            // Make form submission POST request
            final URL connUrl = loginForm.submit().request().url();
            LOGGER.debug("Making form submission to {}", connUrl);

            // Do not follow any further redirects, we can now
            // pass back the data the Browser needs to complete the job
            final HttpURLConnection loginConnection =
                    WebProverUtils.makeRequest(connUrl, loginUri.toURL(), postData, cookieString);
            URL redirectUrl = WebProverUtils.getRedirectUrl(loginConnection);
            if (redirectUrl == null) {
                redirectUrl = connUrl;
            }

            StringBuffer sb = new StringBuffer();
            // Read the response body
            if (loginConnection.getResponseCode() == 200) {

                if (loginConnection.getInputStream() != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((loginConnection.getInputStream())));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }
                LOGGER.trace("Response body = {}", sb.toString());
            }
            LOGGER.trace("Response body = {}", sb.toString());
            
            // Make browser auth token result
            final AuthToken token = new BrowserPasswordAuthToken(
                    cookieManager.getRawCookies(),
                    loginUri.toURL(),
                    redirectUrl,
                    sb.toString(),
                    pairing.getCredentials());

            // New closed session with null remote ID
            return Session.newInstanceClosed(
                    sessionFactory, null, pairing, token);
        } catch (IOException e) {
            LOGGER.error("IOException occurred!", e);
            // Return Session in error state
            return Session.newInstanceInError(
                    sessionFactory, pairing, Session.Error.IO_EXCEPTION);
        } finally {
            // Remove the stored cookies
            CookieStore store = cookieManager.getCookieStore();
            store.removeAll();
        }
    }

    @Override
    public void destroy() throws DestroyFailedException {
        if (isDestroyed) {
            throw new IllegalStateException("Already destroyed");
        }
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}


