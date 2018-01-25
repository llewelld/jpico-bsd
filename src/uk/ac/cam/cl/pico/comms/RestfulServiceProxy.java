/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.comms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

/**
 * Interface of a RESTful HTTP Pico service.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @deprecated Class not fully developed.
 */
@Deprecated
public class RestfulServiceProxy extends ProxyService {

    private final URL url;

    /**
     * Construct a <code>RestfulServiceInterface</code> using a complete URL.
     * 
     * @param url URL of the service.
     * @param serializer for serializing and deserializing the messages sent to and received from
     *        this service.
     * @throws MalformedURLException
     */
    public RestfulServiceProxy(String url, MessageSerializer serializer)
            throws MalformedURLException {
        super(serializer);
        this.url = new URL(url);
    }

    /**
     * Construct a <code>RestfulServiceInterface</code> using components of its URL.
     * 
     * @param host host component of the URL, for example <code>www.example.com</code>.
     * @param port port the service is running on.
     * @param path path components of the URL, for example <code>/pico</code>.
     * @param serializer for serializing and deserializing the messages sent to and received from
     *        this service.
     * @throws MalformedURLException
     */
    public RestfulServiceProxy(String host, int port, String path,
            MessageSerializer serializer) throws MalformedURLException {
        super(serializer);
        this.url = new URL("http", host, port, path);
    }

    @Override
    protected byte[] sendRequest(byte[] serializedMessage) throws IOException {
        HttpURLConnection connection = makeRequest2(serializedMessage);
        // Make the request and check the response code, anything other than
        // 200 - OK, throw an IOException.
        if (connection.getResponseCode() != 200) {
            throw new IOException("Error occured whilst making request. HTTP"
                    + connection.getResponseMessage());
        }
        InputStream is = null;
        try {
            is = connection.getInputStream();
            return IOUtils.toByteArray(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private HttpURLConnection makeRequest2(byte[] serializedMessage)
            throws IOException {
        // Construct the HTTP request headers
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-length",
                Integer.toString(serializedMessage.length));
        connection.setRequestProperty("Content-type", "application/json");

        // Write the request body
        connection.setDoOutput(true);
        OutputStream os = null;
        try {
            os = connection.getOutputStream();
            os.write(serializedMessage);
            os.flush();
        } finally {
            if (os != null) {
                os.close();
            }
        }

        return connection;
    }
	
	@Override
	public State getState() {
		throw new UnsupportedOperationException("getState not supported by verifier proxies");
	}
}
