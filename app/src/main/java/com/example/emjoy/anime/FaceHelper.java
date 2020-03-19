package com.example.emjoy.anime;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;

public final class FaceHelper {

    private static final String LOG_TAG = "FaceHelper";
    private static final boolean DEBUG_ENABLE = false;

    public static Bitmap genFaceBitmap(Bitmap sourceBitmap) {

        if (!checkBitmap(sourceBitmap, "genFaceBitmap()")) {
            return null;
        }

        // default algorithm of face detecting of Android can only handle
        // RGB_565 bitmap, so copy it by RGB_565 here.
        Bitmap cacheBitmap = sourceBitmap.copy(Bitmap.Config.RGB_565, false);

        // gives up strong reference here. because this method may be
        // time-consuming, so maybe run in work thread usually, we give up the
        // strong reference of the source bitmap after it copied, so that it can
        // be recycled and GC in another thread.
        sourceBitmap = null;

        if (DEBUG_ENABLE) {
            Log.i(LOG_TAG,
                    "genFaceBitmap() : source bitmap width - "
                            + cacheBitmap.getWidth() + " , height - "
                            + cacheBitmap.getHeight());
        }

        int cacheWidth = cacheBitmap.getWidth();
        int cacheHeight = cacheBitmap.getHeight();

        // default algorithm of face detecting of Android can only handle the
        // bitmap that width is even, so we give up the 1 pixel from right if
        // not even.
        if (cacheWidth % 2 != 0) {
            if (0 == cacheWidth - 1) {
                if (DEBUG_ENABLE) {
                    Log.e(LOG_TAG,
                            "genFaceBitmap() : source bitmap width is only 1 , return null.");
                }
                return null;
            }
            final Bitmap localCacheBitmap = Bitmap.createBitmap(cacheBitmap, 0,
                    0, cacheWidth - 1, cacheHeight);
            cacheBitmap.recycle();
            cacheBitmap = localCacheBitmap;
            --cacheWidth;

            if (DEBUG_ENABLE) {
                Log.i(LOG_TAG,
                        "genFaceBitmap() : source bitmap width - "
                                + cacheBitmap.getWidth() + " , height - "
                                + cacheBitmap.getHeight());
            }
        }

        final FaceDetector.Face[] faces = new FaceDetector.Face[1];
        final int facefound = new FaceDetector(cacheWidth, cacheHeight, 1)
                .findFaces(cacheBitmap, faces);
        if (DEBUG_ENABLE) {
            Log.i(LOG_TAG, "genFaceBitmap() : facefound - " + facefound);
        }
        if (0 == facefound) {
            if (DEBUG_ENABLE) {
                Log.e(LOG_TAG, "genFaceBitmap() : no face found , return null.");
            }
            return null;
        }

        final PointF p = new PointF();
        faces[0].getMidPoint(p);
        if (DEBUG_ENABLE) {
            Log.i(LOG_TAG,
                    "getFaceBitmap() : confidence - " + faces[0].confidence());
            Log.i(LOG_TAG, "genFaceBitmap() : face center x - " + p.x
                    + " , y - " + p.y);
        }
        final int faceX = (int) p.x;
        final int faceY = (int) p.y;
        if (DEBUG_ENABLE) {
            Log.i(LOG_TAG, "genFaceBitmap() : int faceX - " + faceX
                    + " , int faceY - " + faceY);
        }

        // compute an area that face in middle of it.
        int startX, startY, width, height;
        if (faceX <= cacheWidth - faceX) {
            startX = 0;
            width = faceX * 2;
        } else {
            startX = faceX - (cacheWidth - faceX);
            width = (cacheWidth - faceX) * 2;
        }
        if (faceY <= cacheHeight - faceY) {
            startY = 0;
            height = faceY * 2;
        } else {
            startY = faceY - (cacheHeight - faceY);
            height = (cacheHeight - faceY) * 2;
        }

        final Bitmap result = Bitmap.createBitmap(cacheBitmap, startX, startY,
                width, height);
        cacheBitmap.recycle();
        return result;
    }

    private static boolean checkBitmap(final Bitmap bitmap,
                                       final String debugInfo) {
        if (null == bitmap || bitmap.isRecycled()) {
            if (DEBUG_ENABLE) {
                Log.e(LOG_TAG, debugInfo
                        + " : check bitmap , is null or is recycled.");
            }
            return false;
        }
        if (0 == bitmap.getWidth() || 0 == bitmap.getHeight()) {
            if (DEBUG_ENABLE) {
                Log.e(LOG_TAG, debugInfo
                        + " : check bitmap , width is 0 or height is 0.");
            }
            return false;
        }
        return true;
    }

}
