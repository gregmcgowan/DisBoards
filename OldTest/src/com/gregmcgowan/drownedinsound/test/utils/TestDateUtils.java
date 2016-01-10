package com.gregmcgowan.drownedinsound.test.utils;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import android.text.format.DateUtils;

public class TestDateUtils extends AndroidTestCase {

    public void testDateUtils(){
	long now = System.currentTimeMillis();
	long tenMinutesAgo = now - (10 * DateUtils.HOUR_IN_MILLIS);
	String expected = "1 week ago";
	String result = DateUtils.getRelativeTimeSpanString(tenMinutesAgo, now, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
	Assert.assertEquals(expected, result);
    }
    
}
