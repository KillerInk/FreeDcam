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
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.troop.freedcam.R;
import com.troop.freedcam.R.drawable;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.databinding.FreedviewerGridviewImageviewBinding;

import java.lang.ref.WeakReference;

import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.utils.Log;
import freed.viewer.helper.BitmapHelper;

/**
 * Created by troop on 11.12.2015.
 */
public class GridImageView extends FrameLayout
{
    private final String TAG = GridImageView.class.getSimpleName();
    private FreedviewerGridviewImageviewBinding gridviewImageviewBinding;


    public GridImageView(Context context) {
        super(context);
        init(context);
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
        gridviewImageviewBinding =  DataBindingUtil.inflate(inflater, layout.freedviewer_gridview_imageview, this, false);
        gridviewImageviewBinding.gridimageviewholder.setScaleType(ScaleType.CENTER_CROP);
        addView(gridviewImageviewBinding.getRoot());
    }

    public void bindModel(GridImageViewModel gridImageViewModel)
    {
        gridviewImageviewBinding.setGridimageviewmodel(gridImageViewModel);
    }

    public Drawable getDrawable()
    {
       return gridviewImageviewBinding.gridimageviewholder.getDrawable();
    }



}
