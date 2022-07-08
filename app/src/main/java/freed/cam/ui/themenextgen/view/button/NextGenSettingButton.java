package freed.cam.ui.themenextgen.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.troop.freedcam.R;
import com.troop.freedcam.databinding.NextgenSettingItemBinding;

import freed.cam.apis.basecamera.parameters.AbstractParameter;

public class NextGenSettingButton extends RelativeLayout {


    public interface NextGenSettingButtonClick
    {
        void onSettingButtonClick();
    }

    public NextgenSettingItemBinding settingItemBinding;
    private NextGenSettingButtonClick click;


    public NextGenSettingButton(@NonNull Context context) {
        super(context);
        bind(context);
    }

    public NextGenSettingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bind(context);
    }

    public NextGenSettingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bind(context);
    }

    public static NextGenSettingButton getInstance(@NonNull Context context, int headerID, int descriptionID,NextGenSettingButtonClick click)
    {
        NextGenSettingButton item = new NextGenSettingButton(context);
        item.click = click;
        item.settingItemBinding.textViewHeader.setText(headerID);
        item.settingItemBinding.textViewDescription.setText(descriptionID);
        return item;
    }

    public static NextGenSettingButton getInstance(Context context)
    {
        return new NextGenSettingButton(context);
    }


    public void onClick(View v)
    {
        if (click != null)
            click.onSettingButtonClick();
    }

    public void setClick(NextGenSettingButtonClick click)
    {
        this.click = click;
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
