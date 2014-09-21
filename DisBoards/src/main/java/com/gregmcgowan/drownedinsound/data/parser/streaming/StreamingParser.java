package com.gregmcgowan.drownedinsound.data.parser.streaming;

import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;

import android.text.TextUtils;

import java.util.HashMap;

public class StreamingParser {

    public static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX + "StreamingParser";

    //TODO we can get attributes from the tag object
    protected long getTimestampFromParameters(HashMap<String, String> parameters) {
        String timeStampString = parameters.get(HtmlConstants.TITLE);
        long timeStamp = -1;
        try {
            timeStamp = Long.parseLong(timeStampString) * 1000;
        } catch (NumberFormatException nfe) {

        }
        return timeStamp;
    }

    //TODO we can get attributes from the tag 
    protected HashMap<String, String> createAttributeMapFromStartTag(String tag) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (!TextUtils.isEmpty(tag)) {
            String removeStartAndEnd = tag.substring(1, tag.length() - 1);
            if (!TextUtils.isEmpty(removeStartAndEnd)) {
                String[] splitAttributes = removeStartAndEnd.split("\\s");
                if (splitAttributes != null && splitAttributes.length > 0) {
                    for (int i = 0; i < splitAttributes.length; i++) {
                        String splitAttribute = splitAttributes[i];
                        String[] keyValue = splitAttribute.split("=");
                        if (keyValue != null && keyValue.length > 1) {
                            String key = keyValue[0];
                            String value = keyValue[1];
                            if (!TextUtils.isEmpty(key)
                                    && !TextUtils.isEmpty(value)) {
                                // Remove quotes
                                value = value.substring(1, value.length() - 1);
                                //Log.d(TAG, "Key [" + key + "] Value [" + value +"]");
                                hashMap.put(key, value);
                            }
                        }
                    }
                }
            }
        }
        //Log.d(TAG,hashMap.toString());
        return hashMap;
    }
}
