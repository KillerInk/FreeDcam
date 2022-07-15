package freed.cam.ui.themenextgen.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.troop.freedcam.R;
import com.troop.freedcam.databinding.CamerauiManualbuttonBinding;
import com.troop.freedcam.databinding.NextgenTextItemBinding;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;

public class NextGenTextItem extends LinearLayout implements ManualButtonInterface {

    NextgenTextItemBinding binding;
    private String[] parameterValues;
    protected ParameterInterface parameter;
    private int defaultColor;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NextGenTextItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        bind(context);
        setArrts(context,attrs);
    }

    public static NextGenTextItem getInstance(@NonNull Context context, String header, AbstractParameter parameter)
    {
        NextGenTextItem item = new NextGenTextItem(context);
        item.binding.textViewHeader.setText(header);
        if (parameter != null) {
            item.binding.setParameter(parameter);
            item.binding.notifyChange();
            item.parameter =parameter;
        }
        return item;
    }

    public static NextGenTextItem getInstance(@NonNull Context context, String header, AbstractParameter parameter, int color)
    {
        NextGenTextItem item = NextGenTextItem.getInstance(context,header,parameter);
        item.defaultColor = color;
        item.binding.textViewHeader.setTextColor(color);
        item.binding.textViewValue.setTextColor(color);
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

    @Override
    public void SetActive(boolean active) {
        if (active)
            if (defaultColor == 0) {
                binding.textViewHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.manual_button_active));
                binding.textViewValue.setTextColor(ContextCompat.getColor(getContext(), R.color.manual_button_active));
            }
            else {
                binding.textViewHeader.setTextColor(defaultColor);
                binding.textViewValue.setTextColor(defaultColor);
            }
        else {
            binding.textViewHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.nextgen_menu_right_text));
            binding.textViewValue.setTextColor(ContextCompat.getColor(getContext(), R.color.nextgen_menu_right_text));
        }
    }

    @Override
    public AbstractParameter getParameter() {
        return (AbstractParameter) parameter;
    }

    @Override
    public String[] getStringValues()
    {
        if (parameterValues == null || parameterValues.length ==0)
            parameterValues = parameter.getStringValues();
        return parameterValues;
    }

    @Override
    public int getCurrentItem()
    {
        return parameter.getIntValue();
    }

    @Override
    public void setValueToParameters(final int value)
    {
        parameter.setIntValue(value, true);
    }
}
