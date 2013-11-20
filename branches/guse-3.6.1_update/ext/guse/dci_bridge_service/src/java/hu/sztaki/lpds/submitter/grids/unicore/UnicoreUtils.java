package hu.sztaki.lpds.submitter.grids.unicore;

import java.io.Closeable;
import java.io.OutputStream;

/**
 *
 * @author Patrick Schäfer
 */
public class UnicoreUtils {

    public static Double parseDoubleSafe(String value, double defaultValue) {
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    public static Long parseLongSafe(String value, long defaultValue) {
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean stringIsNotEmpty(String test) {
        return test != null && !test.trim().equals("");
    }

    public static boolean stringIsEmpty(String test) {
        return test == null || test.trim().equals("");
    }

    
    public static void saveCloseStream(Closeable in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCloseStream(OutputStream in) {
        try {
            if (in != null) {
                in.flush();
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFriendlyUrl(String url) {
        if (url != null &&  url.indexOf("service") > 0) {
            url = url.substring(0, url.indexOf("services")-1);
            if (url.indexOf("https://") >= 0) {
                url = url.substring(url.indexOf("https://"));
                if (url.indexOf("/")>0) {
                  // TODO
                }
            }
        }
        return url;
    }

}
