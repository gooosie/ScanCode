package com.gooosie.scancode.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HttpClient
 */

public class HttpClient {

    private static final String TAG = HttpClient.class.getCanonicalName();

    public static final int FAILED_DENIED = -1;
    public static final int FAILED_MALFORMED_URL = -2;
    public static final int FAILED_UNSUPPORTED_ENCODING = -3;
    public static final int FAILED_PROTOCOL = -4;
    public static final int FAILED_IO = -5;

    private static ExecutorService mThreadPool = Executors.newCachedThreadPool();

    /**
     * Use the GET method to access the url.
     * @param urlString url
     * @param callback callback
     */
    public static void getAsync(String urlString, HttpCallback callback) {
        getAsync(urlString, callback, null);
    }

    /**
     * Use the GET method to access the url.
     * @param urlString url
     * @param callback callback
     * @param headers map of headers
     */
    public static void getAsync(final String urlString, final HttpCallback callback, final Map<String, String> headers) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                get(urlString, callback, headers);
            }
        });
    }

    /**
     * Use the GET method to access the url.
     * @param urlString url
     * @param callback callback
     */
    public static void get(String urlString, HttpCallback callback) {
        get(urlString, callback, null);
    }

    /**
     * Use the GET method to access the url.
     * @param urlString url
     * @param callback callback
     * @param headers map of headers
     */
    public static void get(String urlString, HttpCallback callback, Map<String, String> headers) {
        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            conn.connect();
            if (conn.getResponseCode() == 200) {
                bis = new BufferedInputStream(conn.getInputStream());

                bos = new ByteArrayOutputStream();
                byte[] bytesBuffer = new byte[2048];
                int len;
                while ((len = bis.read(bytesBuffer, 0, bytesBuffer.length)) != -1) {
                    bos.write(bytesBuffer, 0, len);
                }
                bos.flush();
                bos.close();

                String result = bos.toString("UTF-8");
                callback.onSuccess(result);
            } else {
                callback.onFailed(FAILED_DENIED, conn.getResponseCode() + "");
            }
        } catch (MalformedURLException e) {
            callback.onFailed(FAILED_MALFORMED_URL, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            callback.onFailed(FAILED_UNSUPPORTED_ENCODING, e.getMessage());
        } catch (ProtocolException e) {
            callback.onFailed(FAILED_PROTOCOL, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailed(FAILED_IO, e.getMessage());
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
