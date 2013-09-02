package com.gregmcgowan.drownedinsound.utils;

import java.io.File;
import java.io.IOException;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;

import android.content.Context;

/**
 * Some common file tasks
 *
 * @author Greg
 */
public class FileUtils {

    public static File createTempFile(Context context) {
        String prefix = String.valueOf(System.currentTimeMillis());
        File tempFile = null;
        try {
            tempFile = File.createTempFile(prefix, ".html",
                context.getCacheDir());
        } catch (IOException e) {
            if (DisBoardsConstants.DEBUG) {
                e.printStackTrace();
            }
        }
        return tempFile;
    }
}
