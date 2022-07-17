package freed.cam.ui.themenextgen.view;

import android.animation.Animator;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.troop.freedcam.R;
import com.troop.freedcam.databinding.NextgenCamerauiTextSwitchBinding;
import com.troop.freedcam.databinding.NextgenTextItemBinding;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.ui.themenextgen.view.button.StyledTextView;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.settings.mode.BooleanSettingModeInterface;

public class NextGenCameraUiTextSwitch extends LinearLayout {

    NextgenCamerauiTextSwitchBinding binding;
    private boolean isExpanded = false;
    int vcount = 0;
    private boolean isSwitch = false;
    private boolean showvaluetxt = false;
    private BooleanSettingModeInterface booleanSettingModeInterface;
    private OnClickListener onClickListener;
    public NextGenCameraUiTextSwitch(Context context) {
        super(context);
        bind(context);
    }

    public NextGenCameraUiTextSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bind(context);
    }

    public NextGenCameraUiTextSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bind(context);
    }

    private void bind(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = NextgenCamerauiTextSwitchBinding.inflate(inflater,this,true);
        binding.textViewValueHolder.setOnClickListener(onNormalButtonClickListner);
    }

    public void setParameter(AbstractParameter parameter, float size)
    {
        binding.setParameter(parameter);
        binding.textViewValueHolder.setText(parameter.getStringValue());
        binding.textViewValueHolder.setTextSize((int) size);
        binding.textViewFront.setVisibility(GONE);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setParameter(AbstractParameter parameter, boolean showvaluetxt, String backgroundText, float valuesize, float frontsize)
    {
        this.showvaluetxt = showvaluetxt;

        binding.textViewValueHolder.setText(backgroundText);
        binding.textViewFront.setTextSize((int) frontsize);
        binding.textViewValueHolder.setTextSize((int) valuesize);
        if(parameter != null) {
            binding.setParameter(parameter);
            binding.textViewFront.setText(parameter.getStringValue());
        }
        binding.textViewFront.setSelected(true);
        if (!showvaluetxt)
            binding.textViewFront.setVisibility(GONE);
    }

    public void setBooleanSettingModeInterface(BooleanSettingModeInterface booleanSettingModeInterface,String valueText, float valuesize)
    {
        this.booleanSettingModeInterface = booleanSettingModeInterface;
        binding.textViewValueHolder.setText(valueText);
        binding.textViewValueHolder.setTextSize((int) valuesize);
        binding.textViewFront.setVisibility(GONE);
        if (booleanSettingModeInterface instanceof AbstractParameter)
            binding.setParameter((AbstractParameter)booleanSettingModeInterface);
        isSwitch = true;
    }

    public void setText(String s)
    {
        if (!showvaluetxt)
            binding.textViewValueHolder.setText(s);
        else
            binding.textViewFront.setText(s);
    }

    public AbstractParameter getParameter()
    {
        return binding.getParameter();
    }

    public OnClickListener onNormalButtonClickListner =new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isSwitch) {
                if (onClickListener != null)
                    onClickListener.onClick(NextGenCameraUiTextSwitch.this);
            }
            else
            {
                booleanSettingModeInterface.set(!booleanSettingModeInterface.get());
                setChecked();
            }
        }
    };

    private void setChecked()
    {
        if (booleanSettingModeInterface.get())
            binding.textViewValueHolder.setTextColor(ContextCompat.getColor(getContext(), R.color.manual_button_active));
        else
            binding.textViewValueHolder.setTextColor(ContextCompat.getColor(getContext(), R.color.nextgen_menu_right_text));
    }

    public void setValueTextSize(float size)
    {
        binding.textViewValueHolder.setTextSize((int) size);
    }

    public void setFrontTextSize(float size)
    {
        binding.textViewFront.setTextSize((int) size);
    }

}
