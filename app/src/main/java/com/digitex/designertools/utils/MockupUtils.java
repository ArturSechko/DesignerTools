package com.digitex.designertools.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.digitex.designertools.utils.PreferenceUtils.MockPreferences;

import java.io.File;
import java.io.IOException;

public class MockupUtils {
    private static final String MOCKUP_DIRECTORY = "mockups";

    public static final String PORTRAIT_MOCKUP_FILENAME = "mockup_portrait";
    public static final String LANDSCAPE_MOCKUP_FILENAME = "mockup_landscape";

    public static void savePortraitMockup(Context context, Bitmap bmp) throws IOException {
        saveMockup(context, bmp, PORTRAIT_MOCKUP_FILENAME);
    }

    public static Bitmap getPortraitMockup(Context context) {
        return loadMockup(context, PORTRAIT_MOCKUP_FILENAME);
    }

    public static void saveLandscapeMockup(Context context, Bitmap bmp) throws IOException {
        saveMockup(context, bmp, LANDSCAPE_MOCKUP_FILENAME);
    }

    public static Bitmap getLandscapeMockup(Context context) {
        return loadMockup(context, LANDSCAPE_MOCKUP_FILENAME);
    }

    private static void saveMockup(Context context, Bitmap bmp, String fileName)
            throws IOException {
        String path = context.getFilesDir().getAbsolutePath() + File.separator + MOCKUP_DIRECTORY;
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Unable to mkdris");
            }
        }
        String filePath = path + File.separator + fileName;
        if (bmp != null) {
            ImageUtils.saveBitmap(bmp, path + File.separator + fileName);
            if (PORTRAIT_MOCKUP_FILENAME.equals(fileName)) {
                MockPreferences.setPortraitMocupkOverlay(context, filePath);
            } else if (LANDSCAPE_MOCKUP_FILENAME.equals(fileName)) {
                MockPreferences.setLandscapeMocupkOverlay(context, filePath);
            }
        } else {
            if (PORTRAIT_MOCKUP_FILENAME.equals(fileName)) {
                if (new File(filePath).delete()) {
                    MockPreferences.setPortraitMocupkOverlay(context, "");
                }
            } else if (LANDSCAPE_MOCKUP_FILENAME.equals(fileName)) {
                if (new File(filePath).delete()) {
                    MockPreferences.setLandscapeMocupkOverlay(context, "");
                }
            }
        }
    }

    private static Bitmap loadMockup(Context context, String fileName) {
        File file = new File(context.getFilesDir().getAbsolutePath() + File.separator +
                MOCKUP_DIRECTORY + File.separator + fileName);
        if (!file.exists()) return null;

        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}
