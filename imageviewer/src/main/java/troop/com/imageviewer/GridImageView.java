package troop.com.imageviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by troop on 11.12.2015.
 */
public class GridImageView extends AbsoluteLayout
{
    ImageView imageView;
    TextView textView;
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
    }

    public void setImageDrawable(Drawable asyncDrawable)
    {
        imageView.setImageDrawable(asyncDrawable);
    }

    public void setImageBitmap(Bitmap bitmap)
    {
        imageView.setImageBitmap(bitmap);
    }

    public void SetFileEnding(String ending)
    {
        textView.setText(ending);
    }
    public Drawable getDrawable()
    {
       return imageView.getDrawable();
    }
}
