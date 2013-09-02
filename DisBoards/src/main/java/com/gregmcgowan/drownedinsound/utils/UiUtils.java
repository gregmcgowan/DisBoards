package com.gregmcgowan.drownedinsound.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

/**
 * A class to help with some common UI tasks
 *
 * @author Greg
 */
public class UiUtils {

    public static final int MIN_WIDTH_DP_FOR_DUAL_MODE = 600;

    /**
     * This will attempt to hide the softkeyboard if it is displayed at the
     * moment
     *
     * @param context
     * @param windowToken
     */
    public static void hideSoftKeyboard(Context context, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) context
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }


    /**
     * Returns the name that is automatically generated when a framgent is
     * created in the FragmentPagerAdapter
     *
     * @param viewId the id of the fragment
     * @param index  the page index
     * @return
     */
    public static String makeFragmentPagerAdapterTagName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    /**
     * Converts a value in dp to pixels
     *
     * @param resources The application resources
     * @param dp        the value to convert
     * @return the equivalent in pixels
     */
    public static int convertDpToPixels(Resources resources, int dp) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.getDisplayMetrics()
        );

    }

    /**
     * Converts a value from pixels to dp
     *
     * @param resources
     * @param pixels
     * @return
     */
    public static int convertPixelsToDp(Resources resources, int pixels) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float logicalDensity = metrics.density;
        return (int) (pixels / logicalDensity + 0.5);
    }


}
