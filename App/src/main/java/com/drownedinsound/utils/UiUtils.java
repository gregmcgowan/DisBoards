package com.drownedinsound.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
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
     */
    public static int convertPixelsToDp(Resources resources, int pixels) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float logicalDensity = metrics.density;
        return (int) (pixels / logicalDensity + 0.5);
    }


    public static boolean isDualPaneMode(Context context) {
        Resources resources = context.getResources();
        int screenWidthPixels = resources.getDisplayMetrics().widthPixels;
        int screenWidthDp = UiUtils.convertPixelsToDp(resources,
                screenWidthPixels);
        int currentOrientation = resources.getConfiguration().orientation;
        return currentOrientation == Configuration.ORIENTATION_LANDSCAPE
                && screenWidthDp >= UiUtils.MIN_WIDTH_DP_FOR_DUAL_MODE;
    }


    public static void setBackgroundDrawable(View view, Drawable backgroundDrawble) {
        if (view != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(backgroundDrawble);
            } else {
                view.setBackgroundDrawable(backgroundDrawble);
            }
        }
    }

}
