package freed.viewer.screenslide.binding;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.R;

import freed.ActivityAbstract;
import freed.FreedApplication;
import freed.image.ImageManager;
import freed.utils.Log;
import freed.viewer.screenslide.BitmapLoader;
import freed.viewer.screenslide.models.ExifViewModel;
import freed.viewer.screenslide.models.ImageFragmentModel;

public class ScreenSlideCustomBinding {

    @BindingAdapter("setTypeFaceToTextView")
    public static void setTypeFaceToTextView(TextView textView, ExifViewModel gridImageViewModel)
    {
        if (gridImageViewModel != null)
            textView.setTypeface(gridImageViewModel.getTypeface());
    }

    @BindingAdapter("setImageModelModelToImageView")
    public static void setImageModelModelToImageView(TouchImageView gridImageView, ImageFragmentModel gridImageViewModel)
    {
        if (gridImageViewModel == null || gridImageViewModel.getBaseHolder() == null)
            return;
        if (gridImageViewModel.bitmapLoader != null) {
            gridImageViewModel.setProgressBarVisible(false);
            FreedApplication.imageManager().removeImageLoadTask(gridImageViewModel.bitmapLoader);
        }

        gridImageView.setImageBitmap(null);
        if (!gridImageViewModel.getBaseHolder().IsFolder())
        {
            gridImageView.setImageResource(R.drawable.noimage);
            gridImageViewModel.setProgressBarVisible(true);
            try {
                gridImageViewModel.bitmapLoader = new BitmapLoader(gridImageViewModel,gridImageView);
                FreedApplication.imageManager().putImageLoadTask(gridImageViewModel.bitmapLoader);
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
