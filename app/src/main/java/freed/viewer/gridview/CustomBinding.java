package freed.viewer.gridview;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.troop.freedcam.R;

import freed.file.holder.BaseHolder;
import freed.image.ImageManager;
import freed.utils.Log;

public class CustomBinding {

    @BindingAdapter("setVisibility")
    public static void setVisibility(View view, boolean visibile)
    {
        view.setVisibility(visibile ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("setChecked")
    public static void setChecked(ImageView view, boolean checked)
    {
        if (checked)
            view.setImageDrawable(view.getResources().getDrawable(R.drawable.cust_cb_sel));
        else
            view.setImageDrawable(view.getResources().getDrawable(R.drawable.cust_cb_unsel));
    }


    @BindingAdapter("setGridModelToView")
    public static void setGridModelToView(ImageView gridImageView,GridImageViewModel gridImageViewModel)
    {
        if (gridImageViewModel == null || gridImageViewModel.getImagePath() == null)
            return;
        if (gridImageViewModel.bitmapLoadRunnable != null) {
            gridImageViewModel.bitmapLoadRunnable.stopProgessbar();
            ImageManager.removeImageLoadTask(gridImageViewModel.bitmapLoadRunnable);
        }

        gridImageView.setImageBitmap(null);
        if (!gridImageViewModel.getImagePath().IsFolder())
        {
            gridImageView.setImageResource(R.drawable.noimage);
            gridImageViewModel.setProgressBarVisible(true);
            try {
                gridImageViewModel.bitmapLoadRunnable = new BitmapLoadRunnable(gridImageView,gridImageViewModel);
                ImageManager.putImageLoadTask(gridImageViewModel.bitmapLoadRunnable);
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }

        }
        else {
            gridImageViewModel.setProgressBarVisible(false);
            gridImageView.setImageResource(R.drawable.folder);
        }

        //invalidate();

    }
}
