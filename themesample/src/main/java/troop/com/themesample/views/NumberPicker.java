package troop.com.themesample.views;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

import troop.com.themesample.R;

/**
 * Created by GeorgeKiarie on 9/15/2015.
 */
public class NumberPicker  {
private Object picker;
private Class<?> classPicker;

        public NumberPicker(LinearLayout numberPickerView) {
            picker = numberPickerView;
            classPicker = picker.getClass();

            // ?????? '+', ??? - NumberPickerButton
            View upButton = numberPickerView.getChildAt(0);
            upButton.setBackgroundResource(R.drawable.btn_crc_greenish);

            // ????????? ????, ??? - EditText
            EditText edDate = (EditText) numberPickerView.getChildAt(1);
            edDate.setTextSize(17);
            edDate.setBackgroundResource(R.drawable.iv_bg);

            // ?????? '-', ??? - NumberPickerButton
            View downButton = numberPickerView.getChildAt(2);
            downButton.setBackgroundResource(R.drawable.btn_crc_redish);
        }

        public void setRange(int start, int end) {
            try {
                Method m = classPicker.getMethod("setRange", int.class, int.class);
                m.invoke(picker, start, end);
            } catch (Exception e) {
            }
        }

        public Integer getCurrent() {
            Integer current = -1;
            try {
                Method m = classPicker.getMethod("getCurrent");
                current = (Integer) m.invoke(picker);
            } catch (Exception e) {
            }
            return current;
        }

        public void setCurrent(int current) {
            try {
                Method m = classPicker.getMethod("setCurrent", int.class);
                m.invoke(picker, current);
            } catch (Exception e) {
            }
        }
}
