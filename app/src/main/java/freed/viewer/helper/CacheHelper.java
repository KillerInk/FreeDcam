/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.viewer.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import freed.utils.Log;
import freed.viewer.helper.DiskLruCache.Editor;
import freed.viewer.helper.DiskLruCache.Snapshot;

/**
 * Created by troop on 11.12.2015.
 */
public class CacheHelper
{
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    public static final int DISK_CACHE_SIZE = 1024 * 1024 * 200;
    //private final LruCache<String, Bitmap> mMemoryCache;
    final String TAG = CacheHelper.class.getSimpleName();

    public CacheHelper(Context context)
    {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        /*int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };*/

        File cacheDir = getDiskCacheDir(context);
        new InitDiskCacheTask().execute(cacheDir);

    }

    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    Log.WriteEx(e);
                }
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    public void addBitmapToCache(String data, Bitmap value) {
        //BEGIN_INCLUDE(add_bitmap_to_cache)
        if (data == null || value == null) {
            return;
        }

        /*// Add to memory cache
        if (mMemoryCache != null) {

            mMemoryCache.put(data, value);
        }*/

        synchronized (mDiskCacheLock) {
            // Add to disk cache
            if (mDiskLruCache != null) {

                OutputStream out = null;
                try {
                    Snapshot snapshot = mDiskLruCache.get(data);
                    if (snapshot == null) {
                        Editor editor = mDiskLruCache.edit(data);
                        if (editor != null) {
                            out = editor.newOutputStream(0);
                            value.compress(
                                    CompressFormat.JPEG,70, out);
                            editor.commit();
                            out.close();
                        }
                    } else {
                        snapshot.getInputStream(0).close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "addBitmapToCache - " + e);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) { Log.WriteEx(e);}
                }
            }
        }
        //END_INCLUDE(add_bitmap_to_cache)
    }

    public Bitmap getBitmapFromDiskCache(String data) {
        //BEGIN_INCLUDE(get_bitmap_from_disk_cache)
        //final String key = hashKeyForDisk(data);
        Bitmap bitmap = null;
        if (data.contains(" "))
            data = data.replace(" ", "_");

        synchronized (mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {Log.WriteEx(e);}
            }
            if (mDiskLruCache != null) {
                InputStream inputStream = null;
                try {
                    Snapshot snapshot = mDiskLruCache.get(data);
                    if (snapshot != null) {

                        inputStream = snapshot.getInputStream(0);
                        if (inputStream != null) {
                            FileDescriptor fd = ((FileInputStream) inputStream).getFD();


                            // Decode bitmap, but we don't want to sample so give
                            // MAX_VALUE as the target dimensions
                            bitmap = BitmapFactory.decodeFileDescriptor(fd, null, null);
                            inputStream.close();
                        }
                    }
                } catch (IOException e) {
                    Log.WriteEx(e);
                }
            }
            return bitmap;
        }
        //END_INCLUDE(get_bitmap_from_disk_cache)
    }

    public void deleteFileFromDiskCache(String file)
    {
        if (mDiskLruCache != null)
        {
            if (file.contains(" "))
                file = file.replace(" ", "_");
            try {
                mDiskLruCache.remove(file);
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        }
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    private File getDiskCacheDir(Context context) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        String cachePath = context.getCacheDir().getPath();

        String DISK_CACHE_SUBDIR = "thumbnails";
        return new File(cachePath + File.separator + DISK_CACHE_SUBDIR);
    }


    /*public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }*/


}
