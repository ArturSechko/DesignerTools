package com.digitex.designertools.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        String scheme = uri.getScheme();
        Bitmap image = null;
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)
                || ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                image = BitmapFactory.decodeStream(stream);
            } catch (Exception e) {
                Log.w(TAG, "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Unable to close content: " + uri, e);
                    }
                }
            }
        }

        return image;
    }

    public static boolean saveBitmap(Bitmap bmp, String path) throws FileNotFoundException {
        if (bmp == null || path == null) return false;

        FileOutputStream outputStream = new FileOutputStream(path);
        return bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
    }
}
