package freed.cam.ui.themenextgen.view.button;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.troop.freedcam.R;

public class StyledTextView extends FrameLayout {
    public StyledTextView(@NonNull Context context) {
        super(context);
        load();
    }

    public StyledTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        load();
    }

    public StyledTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        load();
    }

    private void load()
    {
        inflate(getContext(), R.layout.styled_textview, this);
    }

    public void setText(String txt)
    {
        TextView textView = findViewById(R.id.testview);
        TextView textViews = findViewById(R.id.testviewshadow);
        textView.setText(txt);
        textViews.setText(txt);
    }

    public String getText()
    {
        TextView textView = findViewById(R.id.testview);
        return (String) textView.getText();
    }

    public void setTextSize(float sp)
    {
        TextView textView = findViewById(R.id.testview);
        TextView textViews = findViewById(R.id.testviewshadow);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
        textViews.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
        textView.setSelected(true);
        textViews.setSelected(true);
    }

    public void setMargine(int top, int bottom)
    {
        MarginLayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        params.topMargin = top;
        params.bottomMargin = bottom;
        params.leftMargin= 0;
        params.rightMargin = 0;
        setLayoutParams(params);
    }

    public void setColor(int color)
    {
        TextView textView = findViewById(R.id.testview);
        textView.setTextColor(color);
    }
}
