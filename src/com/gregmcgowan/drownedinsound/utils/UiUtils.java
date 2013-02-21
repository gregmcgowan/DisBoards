package com.gregmcgowan.drownedinsound.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

/**
 * A class to help with some common UI tasks
 * 
 * @author Greg
 * 
 */
public class UiUtils {

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
     * @param index the page index
     * @return
     */
    public static String makeFragmentPagerAdapterTagName(int viewId, int index) {
	     return "android:switcher:" + viewId + ":" + index;
    }
    
    /**
     * Converts a value in dp to pixels
     * 
     * @param resources The application resources
     * @param dp the value to convert
     * @return the equivalent in pixels
     */
    public static int convertDpToPixels(Resources resources,int dp){
	return  (int) TypedValue.applyDimension(
	        TypedValue.COMPLEX_UNIT_DIP,
	        dp, 
	        resources.getDisplayMetrics()
	);
	
    }
}
