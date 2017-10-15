package com.gooosie.scancode.http;

/**
 * HttpCallback
 */

public interface HttpCallback {
    void onSuccess(String result);

    void onFailed(int code, String message);
}
