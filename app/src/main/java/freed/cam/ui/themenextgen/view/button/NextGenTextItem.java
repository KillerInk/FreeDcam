package freed.cam.ui.themenextgen.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.troop.freedcam.R;

public class NextGenTextItem extends ConstraintLayout {

    private TextView header;
    private TextView value;

    public NextGenTextItem(@NonNull Context context) {
        super(context);
        bind(context);
    }

    public NextGenTextItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bind(context);
        setArrts(context,attrs);
    }

    public NextGenTextItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bind(context);
        setArrts(context,attrs);
    }

    public NextGenTextItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        bind(context);
        setArrts(context,attrs);
    }

    private void bind(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nextgen_text_item, null);
        header = view.findViewById(R.id.textViewHeader);
        value = view.findViewById(R.id.textViewValue);
        addView(view);
    }

    private void setArrts(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NextGenTextItem,
                0, 0
        );
        //try to set the attributs
        try
        {
            header.setText(a.getText(R.styleable.NextGenTextItem_setHeaderToView));
            value.setText(a.getText(R.styleable.NextGenTextItem_setValueToView));
        }
        finally {
            a.recycle();
        }
    }
}
