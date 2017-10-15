package com.gooosie.scancode.code;

import android.text.TextUtils;
import android.util.Log;

import com.gooosie.scancode.http.HttpCallback;
import com.gooosie.scancode.http.HttpClient;
import com.gooosie.scancode.model.EAN13CNInfo;
import com.gooosie.scancode.model.ISBNInfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * EAN13Handler
 */

public class EAN13Handler implements ICodeHandler {

    private static final String TAG = EAN13Handler.class.getCanonicalName();
    private static final String KEY = "V7N3Xpm4jpRon/WsZ8X/63G8oMeGdUkA8Luxs1CenTY=";

    private boolean isISBN = false;
    private HandleCodeCallback mCodeCallback;

    private HttpCallback mHttpCallback = new HttpCallback() {
        @Override
        public void onSuccess(String result) {
            Log.d(TAG, "onSuccess: length: " + result.length());
            if (isISBN) {
                ISBNInfo info = ISBNInfo.parse(result);
                if (info != null) {
                    mCodeCallback.onHandled(info.toString());
                }
            } else {
                EAN13CNInfo info = EAN13CNInfo.parse(result);
                if (info != null) {
                    mCodeCallback.onHandled(info.toString());
                }
            }
        }

        @Override
        public void onFailed(int code, String message) {
            Log.d(TAG, "onFailed: " + code);
        }
    };

    @Override
    public void requestMore(String code, HandleCodeCallback callback) {
        if (!isEAN13(code)) {
            return;
        }

        mCodeCallback = callback;

        if (isISBN(code)) {
            isISBN = true;
            handleISBN(code);
        } else {
            handleEAN13(code);
        }
    }

    private void handleEAN13(String code) {
        String locale = Locale.getDefault().getLanguage();
        if (isMainlandChinaProduct(code) && locale.equals("zh")) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("User-Agent", "\"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1\"");
            String url = "http://webapi.chinatrace.org/api/getProductData?productCode=" + code;
            url += "&mac=" + getMac(KEY ,url.substring(url.indexOf("/api/", code.length())));
            HttpClient.getAsync(url, mHttpCallback, headers);
        }
    }

    private void handleISBN(String code) {
        HttpClient.getAsync("https://api.douban.com/v2/book/isbn/:" + code, mHttpCallback);
    }

    private boolean isEAN13(String code) {
        return !(TextUtils.isEmpty(code) || code.length() != 13 || !TextUtils.isDigitsOnly(code));
    }

    private boolean isISBN(String code) {
        return isEAN13(code) && (code.startsWith("978") || code.startsWith("979"));
    }

    private boolean isMainlandChinaProduct(String code) {
        try {
            int i = Integer.parseInt(code.substring(0, 3));
            return i >= 690 && i <= 695;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getMac(String key, String code)
    {
        byte[] secretByte = {
                87, -77, 119, 94, -103, -72, -114, -108,
                104, -97, -11, -84, 103, -59, -1, -21,
                113, -68, -96, -57, -122, 117, 73, 0,
                -16, -69, -79, -77, 80, -98, -99, 54
        };
        String resultMac = "";
        try {
            Mac m = Mac.getInstance("HmacSHA256");
            byte[] dataBytes = code.getBytes("ASCII");
            SecretKeySpec macKey = new SecretKeySpec(secretByte, "HMACSHA256");
            m.init(macKey);
            byte[] digest = m.doFinal(dataBytes);
            resultMac = toHex(digest);
            resultMac = resultMac.toUpperCase();
            return resultMac;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMac;
    }

    private static String toHex(byte[] buffer)
    {
        StringBuilder sb = new StringBuilder(buffer.length * 2);
        int i = 0;
        for (;;)
        {
            if (i >= buffer.length) {
                return sb.toString();
            }
            sb.append(Character.forDigit((buffer[i] & 0xF0) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 0xF, 16));
            i += 1;
        }
    }
}
