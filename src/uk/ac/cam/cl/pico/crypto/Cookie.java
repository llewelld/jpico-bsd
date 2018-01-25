/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

public class Cookie {

    private final static String RFC1123_DATE_PATTERN =
            "EEE, dd MMM yyyy HH:mm:ss zzz";
    private final static SimpleDateFormat dateFormat =
            new SimpleDateFormat(RFC1123_DATE_PATTERN);

    @SerializedName("url")
    public final String url;
    @SerializedName("value")
    public final String value;
    @SerializedName("date")
    public final String date;

    public Cookie(final String url, final String value, final String date) {
        // Verify the method's preconditions        
        this.url = checkNotNull(url, "Cookie URL cannot be null");;
        this.value = checkNotNull(value, "Cookie value cannot be null");;
        if (Strings.isNullOrEmpty(date)) {
            this.date = "Date: " + dateFormat.format(new Date());
        } else {
            this.date = date;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Cookie))
            return false;
        final Cookie cookie = (Cookie) o;
        return (url.equals(cookie.url) && value.equals(cookie.value) && date.equals(cookie.date));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url, value, date);
    }
    
    public String getNameValue() {
        if (value.indexOf(";") > 0) {
            return value.substring(0, value.indexOf(";"));
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        return String.format("Url:%s Value:%s Date:%s",
                url, value, date);
    }
}