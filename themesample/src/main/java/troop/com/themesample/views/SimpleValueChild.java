package troop.com.themesample.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import troop.com.themesample.R;
import troop.com.themesample.subfragments.Interfaces;

/**
 * Created by troop on 16.06.2015.
 */
public class SimpleValueChild extends FrameLayout implements View.OnClickListener
{

    TextView textView;
    Interfaces.I_CloseNotice closeNotice;
    public SimpleValueChild(Context context)
    {
        super(context);
        init(context);
    }

    public SimpleValueChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.simplevaluechild, this);
        this.textView = (TextView)findViewById(R.id.simplevaluetext);
        this.setOnClickListener(this);
    }

    public void SetString(String text, Interfaces.I_CloseNotice closeNotice)
    {
        textView.setText(text);
        this.closeNotice = closeNotice;
    }

    @Override
    public void onClick(View v)
    {
        closeNotice.onClose((String)textView.getText());
    }
}
