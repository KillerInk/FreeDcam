package freed.cam.ui.themenextgen.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;
import com.troop.freedcam.databinding.NextgenSettingItemBinding;

import freed.cam.apis.basecamera.parameters.AbstractParameter;

public class NextGenSettingItem extends RelativeLayout {

    private NextgenSettingItemBinding settingItemBinding;

    public NextGenSettingItem(@NonNull Context context) {
        super(context);
        bind(context);
    }

    public NextGenSettingItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bind(context);
        setArrts(context,attrs);
    }

    public NextGenSettingItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bind(context);
        setArrts(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NextGenSettingItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        bind(context);
        setArrts(context,attrs);
    }

    public static NextGenSettingItem getInstance(@NonNull Context context, int headerID, int descriptionID, AbstractParameter parameter)
    {
        NextGenSettingItem item = new NextGenSettingItem(context);
        if (headerID != 0)
            item.settingItemBinding.textViewHeader.setText(headerID);
        if (descriptionID != 0)
            item.settingItemBinding.textViewDescription.setText(descriptionID);
        if (parameter != null) {
            item.settingItemBinding.setParameter(parameter);
            item.settingItemBinding.notifyChange();
        }
        return item;
    }

    public static NextGenSettingItem getInstance(Context context)
    {
        return new NextGenSettingItem(context);
    }

    public void setBinding(int headerID, int descriptionID, AbstractParameter parameter)
    {
        settingItemBinding.textViewHeader.setText(headerID);
        settingItemBinding.textViewDescription.setText(descriptionID);
        if (parameter != null) {
            settingItemBinding.setParameter(parameter);
            settingItemBinding.notifyChange();
        }
    }

    public AbstractParameter getParameter()
    {
        return settingItemBinding.getParameter();
    }

    private void bind(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        settingItemBinding = NextgenSettingItemBinding.inflate(inflater,this,true);
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
        //try to set the attributs
        try
        {
            settingItemBinding.textViewHeader.setText(a.getText(R.styleable.NextGenTextItem_setHeaderToView));
            settingItemBinding.textViewValue.setText(a.getText(R.styleable.NextGenTextItem_setValueToView));
            settingItemBinding.textViewDescription.setText(b.getText(R.styleable.NextGenSettingItem_setDescriptionTextToView));
        }
        finally {
            a.recycle();
            b.recycle();
        }
    }

}
