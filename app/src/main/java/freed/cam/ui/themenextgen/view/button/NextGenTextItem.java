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
import com.troop.freedcam.databinding.CamerauiManualbuttonBinding;
import com.troop.freedcam.databinding.NextgenTextItemBinding;

import freed.cam.apis.basecamera.parameters.AbstractParameter;

public class NextGenTextItem extends ConstraintLayout {

    NextgenTextItemBinding binding;

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

    public static NextGenTextItem getInstance(@NonNull Context context, int headerID, AbstractParameter parameter)
    {
        NextGenTextItem item = new NextGenTextItem(context);
        item.binding.textViewHeader.setText(headerID);
        if (parameter != null) {
            item.binding.setParameter(parameter);
            item.binding.notifyChange();
        }
        return item;
    }

    private void bind(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = NextgenTextItemBinding.inflate(inflater,this,true);
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
            binding.textViewHeader.setText(a.getText(R.styleable.NextGenTextItem_setHeaderToView));
            binding.textViewValue.setText(a.getText(R.styleable.NextGenTextItem_setValueToView));
        }
        finally {
            a.recycle();
        }
    }
}
