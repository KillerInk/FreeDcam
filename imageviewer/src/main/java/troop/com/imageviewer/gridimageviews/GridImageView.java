package troop.com.imageviewer.gridimageviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import troop.com.imageviewer.R;
import troop.com.imageviewer.gridviewfragments.BaseGridViewFragment;
import troop.com.imageviewer.gridviewfragments.GridViewFragment;
import troop.com.imageviewer.holder.BaseHolder;
import troop.com.imageviewer.holder.FileHolder;

/**
 * Created by troop on 11.12.2015.
 */
public class GridImageView extends AbsoluteLayout implements FileHolder.EventHandler
{
    private ImageView imageView;
    private TextView textView;
    private TextView folderTextView;
    private ImageView checkBox;
    private BaseHolder fileHolder;
    public GridViewFragment.ViewStates viewstate = BaseGridViewFragment.ViewStates.normal;
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
        folderTextView = (TextView)findViewById(R.id.foldertextbox);
        checkBox = (ImageView)findViewById(R.id.checkBox_gridviewimage);
        /*checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileHolder.IsSelected())
                    fileHolder.SetSelected(false);
                else
                    fileHolder.SetSelected(true);
            }
        });*/
    }

    public BaseHolder getFileHolder(){return fileHolder;}

    public void setImageDrawable(Drawable asyncDrawable)
    {
        imageView.setImageDrawable(asyncDrawable);
    }

    public void setImageBitmap(Bitmap bitmap)
    {
        imageView.setImageBitmap(bitmap);
    }

    public void SetEventListner(BaseHolder fileHolder)
    {
        this.fileHolder = fileHolder;
        SetViewState(fileHolder.GetCurrentViewState());
        fileHolder.SetEventListner(this);

    }

    public void SetFileEnding(String ending)
    {
        textView.setText(ending);
    }
    public void SetFolderName(String ending)
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
                checkBox.setVisibility(GONE);
                setChecked(false);
                break;
            case selection: {
                checkBox.setVisibility(VISIBLE);
                if (fileHolder.IsSelected())
                {
                    setChecked(true);
                }
                else
                    setChecked(false);
            }
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

    private void setChecked(boolean checked)
    {
        if (checked)
            checkBox.setImageDrawable(getResources().getDrawable(R.drawable.cust_cb_sel));
        else
            checkBox.setImageDrawable(getResources().getDrawable(R.drawable.cust_cb_unsel));
    }

}
