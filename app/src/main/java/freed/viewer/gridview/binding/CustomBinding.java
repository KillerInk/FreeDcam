package freed.viewer.gridview.binding;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;

import com.troop.freedcam.R;

import freed.image.ImageManager;
import freed.utils.Log;
import freed.viewer.gridview.BitmapLoadRunnable;
import freed.viewer.gridview.models.GridImageViewModel;
import freed.viewer.gridview.models.PopupMenuModel;
import freed.viewer.screenslide.models.ImageFragmentModel;
import freed.viewer.screenslide.views.MyHistogram;

public class CustomBinding {

    @BindingAdapter("setVisibility")
    public static void setVisibility(View view, boolean visibile)
    {
        if (visibile)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
    }

    @BindingAdapter("setStringsToPopupMenu")
    public static void setStringsToLayout(LinearLayout view, PopupMenuModel popupMenuModel)
    {
        if (popupMenuModel != null && popupMenuModel.getStrings() != null)
        {
            view.removeAllViews();
            for (String s : popupMenuModel.getStrings())
            {
                Button button = new Button(view.getContext());
                button.setText(s);
                button.setOnClickListener(popupMenuModel.onClickListener);
                view.addView(button);
            }
        }
    }

    @BindingAdapter("setChecked")
    public static void setChecked(ImageView view, boolean checked)
    {
        if (checked)
            view.setImageDrawable(view.getResources().getDrawable(R.drawable.cust_cb_sel));
        else
            view.setImageDrawable(view.getResources().getDrawable(R.drawable.cust_cb_unsel));
    }

    @BindingAdapter("setHistogramData")
    public static void setHistogramData(MyHistogram view, int[] checked)
    {
        if (checked != null)
            view.SetHistogramData(checked);
    }


    @BindingAdapter("setGridModelToView")
    public static void setGridModelToView(ImageView gridImageView, GridImageViewModel gridImageViewModel)
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
