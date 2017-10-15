package com.gooosie.scancode.code;

/**
 * ICodeHandler
 */

public interface ICodeHandler {
    void requestMore(String code, HandleCodeCallback callback);
}
