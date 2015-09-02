package au.id.bennettscash.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 31/08/15.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;

    Handler mHandler;
    Handler mResponseHandler;
    Listener<Token> mListener;
    Map<Token, String> requestMap =
            Collections.synchronizedMap(new HashMap<Token, String>());
    LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(1000);

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    Token token = (Token) msg.obj;
//                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                } else if (msg.what == MESSAGE_PRELOAD) {
                    handlePreload((String)msg.obj);
                }
            }
        };
    }

    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got a URL: " + url);
        requestMap.put(token, url);

        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
    }

    public void preloadThumb(String url) {
        mHandler.obtainMessage(MESSAGE_PRELOAD, url).sendToTarget();
    }

    private void handlePreload(final String url) {
        if (url == null)
            return;

        // If the URL isn't in the cache add it
        try {
            if (cache.get(url) == null) {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                cache.put(url, bitmap);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e);
        }
    }

    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            preloadThumb(url);

            final Bitmap bitmap;

            if (url == null)
                return;

            // Is the URL in the cache?
            if (cache.get(url) == null) {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory
                        .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                Log.i(TAG, "WARNING PRELOADING IS PROBABLY BROKEN: Bitmap created");

                cache.put(url, bitmap);
            } else {
                bitmap = cache.get(url);
//                Log.i(TAG, "Using cached bitmap");
            }

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token) != url)
                        return;

                    requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token, bitmap);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image", e);
        }
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
