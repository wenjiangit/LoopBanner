package com.wenjian.loopbanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Description: Tools
 * Date: 2018/12/4
 *
 * @author jian.wen@ubtrobot.com
 */
class Tools {

    private static boolean debug = false;

    private static final String TAG = "LoopBanner";

    static void setDebug(boolean debug) {
        Tools.debug = debug;
        logD(TAG, "version: 1.0.5");
    }

    static int dp2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dpValue * scale + 0.5f);
    }

    static void logD(String tag, String msg) {
        if (debug) {
            Log.d(tag, msg);
        }
    }

    static void logI(String tag, String msg) {
        if (debug) {
            Log.i(tag, msg);
        }
    }

    static void logE(String tag, String msg, Throwable throwable) {
        if (debug) {
            Log.e(tag, msg, throwable);
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    static @NonNull
    <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the check fails; will
     *                     be converted to a string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    static @NonNull
    <T> T checkNotNull(final T reference, final Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }


}
