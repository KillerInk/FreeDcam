package freed.cam.ui.themenextgen.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.troop.freedcam.R;
import com.troop.freedcam.databinding.NextgenSettingBoolitemBinding;

import freed.settings.mode.BooleanSettingModeInterface;

public class NextGenSettingBoolItem extends RelativeLayout implements CompoundButton.OnCheckedChangeListener
{

    NextgenSettingBoolitemBinding binding;
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public NextGenSettingBoolItem(@NonNull Context context) {
        super(context);
        bind(context);
    }

    public NextGenSettingBoolItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bind(context);
        setArrts(context,attrs);
    }

    public NextGenSettingBoolItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bind(context);
        setArrts(context,attrs);
    }

    public static NextGenSettingBoolItem getInstance(@NonNull Context context, int headerID, int descriptionID, BooleanSettingModeInterface booleanSettingMode)
    {
        NextGenSettingBoolItem item = new NextGenSettingBoolItem(context);
        item.setBinding(headerID,descriptionID,booleanSettingMode);
        return item;
    }

    public static NextGenSettingBoolItem getInstance(@NonNull Context context, int headerID, int descriptionID, BooleanSettingModeInterface booleanSettingMode,CompoundButton.OnCheckedChangeListener onCheckedChangeListener)
    {
        NextGenSettingBoolItem item = getInstance(context,headerID,descriptionID,booleanSettingMode);
        item.onCheckedChangeListener = onCheckedChangeListener;
        return item;
    }

    public static View getInstance(Context context) {
        return new NextGenSettingBoolItem(context);
    }

    public void setBinding(int headerID, int descriptionID, BooleanSettingModeInterface booleanSettingMode)
    {
        binding.textViewHeader.setText(headerID);
        binding.textViewDescription.setText(descriptionID);
        binding.switch2.setOnCheckedChangeListener(this);
        if (booleanSettingMode != null)
            binding.setParameter(booleanSettingMode);
    }

    private void bind(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = NextgenSettingBoolitemBinding.inflate(inflater,this,true);
    }

    private void setArrts(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NextGenTextItem,
                0, 0
        );

        TypedArray b = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NextGenSettingItem,
                0, 0
        );
        TypedArray c = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NextGenSettingBoolItem,
                0, 0
        );
        //try to set the attributs
        try
        {
            binding.textViewHeader.setText(a.getText(R.styleable.NextGenTextItem_setHeaderToView));
            binding.switch2.setChecked(c.getBoolean(R.styleable.NextGenSettingBoolItem_setBoolToView,false));
            binding.textViewDescription.setText(b.getText(R.styleable.NextGenSettingItem_setDescriptionTextToView));
        }
        finally {
            a.recycle();
            b.recycle();
            c.recycle();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!buttonView.isPressed()) {
            return;
        }
        binding.getParameter().set(isChecked);
        if (onCheckedChangeListener != null)
            onCheckedChangeListener.onCheckedChanged(buttonView,isChecked);
    }

}
