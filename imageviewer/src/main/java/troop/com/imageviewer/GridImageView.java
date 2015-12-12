package troop.com.imageviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by troop on 11.12.2015.
 */
public class GridImageView extends AbsoluteLayout implements FileHolder.EventHandler
{
    ImageView imageView;
    TextView textView;
    GridViewFragment.ViewStates currentViewstate = GridViewFragment.ViewStates.normal;
    CheckBox checkBox;
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
        inflater.inflate(R.layout.gridimageview, this);
        imageView = (ImageView) findViewById(R.id.gridimageviewholder);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        textView = (TextView)findViewById(R.id.filetypetextbox);
        checkBox = (CheckBox)findViewById(R.id.checkBox_gridviewimage);
    }

    public void setImageDrawable(Drawable asyncDrawable)
    {
        imageView.setImageDrawable(asyncDrawable);
    }

    public void setImageBitmap(Bitmap bitmap)
    {
        imageView.setImageBitmap(bitmap);
    }

    public void SetEventListner(FileHolder fileHolder)
    {
        fileHolder.SetEventListner(this);
    }

    public void SetFileEnding(String ending)
    {
        textView.setText(ending);
    }
    public Drawable getDrawable()
    {
       return imageView.getDrawable();
    }

    public void SetViewState(GridViewFragment.ViewStates state)
    {
        this.currentViewstate = state;
        switch (currentViewstate)
        {
            case normal:
                checkBox.setVisibility(GONE);
                checkBox.setChecked(false);
                break;
            case selection:
                checkBox.setVisibility(VISIBLE);
        }
    }

    public void SetSelected()
    {
        if(checkBox.isChecked())
            checkBox.setChecked(false);
        else
            checkBox.setChecked(true);
    }

    @Override
    public void onViewStateChanged(GridViewFragment.ViewStates state) {
        SetViewState(state);
    }

    @Override
    public void onSelectionChanged(boolean selected) {
        checkBox.setChecked(selected);
    }
}
