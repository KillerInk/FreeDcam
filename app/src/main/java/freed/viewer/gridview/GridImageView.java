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

package freed.viewer.gridview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.troop.freedcam.R.drawable;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

import freed.utils.Logger;
import freed.viewer.gridview.BaseGridViewFragment.ViewStates;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.holder.BaseHolder;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 11.12.2015.
 */
public class GridImageView extends AbsoluteLayout implements FileHolder.EventHandler
{
    private ImageView imageView;
    private TextView textView;
    private TextView folderTextView;
    private ImageView checkBox;
    private ImageView sdcard;
    private FileHolder fileHolder;
    private int mImageThumbSize;
    private ProgressBar progressBar;
    private final String TAG = GridImageView.class.getSimpleName();
    private ExecutorService executor;
    private BitmapHelper bitmapHelper;

    private GridViewFragment.ViewStates viewstate = ViewStates.normal;


    public GridImageView(Context context) {
        super(context);
        init(context);
    }

    public GridImageView(Context context, ExecutorService executor, BitmapHelper bitmapHelper) {
        super(context);
        init(context);
        SetThreadPoolAndBitmapHelper(executor,bitmapHelper);
    }

    public GridImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layout.freedviewer_gridview_imageview, this);
        imageView = (ImageView) findViewById(id.gridimageviewholder);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        textView = (TextView) findViewById(id.filetypetextbox);
        folderTextView = (TextView) findViewById(id.foldertextbox);
        checkBox = (ImageView) findViewById(id.checkBox_gridviewimage);
        sdcard = (ImageView) findViewById(id.imageView_sd);
        progressBar = (ProgressBar) findViewById(id.progressBar_gridimageview);
    }

    public void SetThreadPoolAndBitmapHelper(ExecutorService executor, BitmapHelper bitmapHelper)
    {
        this.bitmapHelper =bitmapHelper;
        this.executor = executor;
    }

    public BaseHolder getFileHolder(){return fileHolder;}


    public void SetEventListner(FileHolder fileHolder)
    {
        this.fileHolder = fileHolder;
        SetViewState(fileHolder.GetCurrentViewState());
        fileHolder.SetEventListner(this);

    }



    private void SetFileEnding(String ending)
    {
        textView.setText(ending);
    }
    private void SetFolderName(String ending)
    {
        folderTextView.setText(ending);
    }
    public Drawable getDrawable()
    {
       return imageView.getDrawable();
    }

    public void SetViewState(GridViewFragment.ViewStates state)
    {
        viewstate = state;
        switch (state)
        {
            case normal:
                checkBox.setVisibility(View.GONE);
                setChecked(false);
                break;
            case selection:
                checkBox.setVisibility(View.VISIBLE);
                if (fileHolder.IsSelected())
                {
                    setChecked(true);
                }
                else
                    setChecked(false);
        }
        invalidate();
    }

    @Override
    public void onViewStateChanged(GridViewFragment.ViewStates state) {
        SetViewState(state);
    }

    @Override
    public void onSelectionChanged(boolean selected)
    {
//        checkBox.setChecked(selected);
//        invalidate();
    }

    @Override
    public void updateImage() {
        final Bitmap bitmap = bitmapHelper.getBitmap(fileHolder, true);
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setChecked(boolean checked) {
        if (checked)
            checkBox.setImageDrawable(getResources().getDrawable(drawable.cust_cb_sel));
        else
            checkBox.setImageDrawable(getResources().getDrawable(drawable.cust_cb_unsel));
    }

    public void loadFile(FileHolder fileHolder, int mImageThumbSize)
    {
        this.fileHolder = fileHolder;
        this.mImageThumbSize = mImageThumbSize;
        Logger.d(TAG, "load file:" + fileHolder.getFile().getName());
        imageView.setImageBitmap(null);
        if (!fileHolder.getFile().isDirectory())
        {
            imageView.setImageResource(drawable.noimage);
            progressBar.setVisibility(View.VISIBLE);
            executor.execute(new BitmapLoadRunnable(this,fileHolder));
        }
        else {
            progressBar.setVisibility(View.GONE);
            imageView.setImageResource(drawable.folder);
        }
        String f = fileHolder.getFile().getName();
        if (!fileHolder.getFile().isDirectory()) {
            SetFolderName("");
            SetFileEnding(f.substring(f.length() - 3));
        }
        else {
            SetFileEnding("");
            SetFolderName(f);
        }
        if (fileHolder.isExternalSD())
            sdcard.setVisibility(View.VISIBLE);
        else
            sdcard.setVisibility(View.GONE);
        invalidate();

    }

    class BitmapLoadRunnable implements Runnable
    {
        private final String TAG = BitmapLoadRunnable.class.getSimpleName();
        WeakReference<GridImageView>imageviewRef;
        FileHolder fileHolder;

        public BitmapLoadRunnable(GridImageView imageView, FileHolder fileHolder)
        {
            imageviewRef = new WeakReference<>(imageView);
            this.fileHolder = fileHolder;
        }

        @Override
        public void run()
        {
            Logger.d(TAG, "load file:" + fileHolder.getFile().getName());
            final Bitmap bitmap = bitmapHelper.getBitmap(fileHolder, true);
            if (imageviewRef != null && bitmap != null) {
                final GridImageView imageView = imageviewRef.get();
                if (imageView != null && imageView.getFileHolder() == fileHolder)
                {
                    Logger.d(TAG, "set bitmap to imageview");
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            imageView.imageView.setImageBitmap(bitmap);
                        }
                    });

                }
                else
                    Logger.d(TAG, "Imageview has new file already, skipping it");
            }
            else {
                Logger.d(TAG, "Imageview or bitmap null");
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null)
                            progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }
    }
}
