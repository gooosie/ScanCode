package com.gooosie.scancode.code;

import com.google.zxing.BarcodeFormat;

/**
 * CodeHandlerFactory
 */

public class CodeHandlerFactory {
    public static ICodeHandler getCodeHandler(BarcodeFormat format) {
        if (format == null) {
            return null;
        }

        switch (format) {
            case EAN_13:
                return new EAN13Handler();
            default:
                break;
        }

        return null;
    }
}
