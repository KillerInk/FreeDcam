package freed.viewer.gridview.models;

import android.view.View;
import android.widget.Button;

import androidx.databinding.BaseObservable;

import freed.viewer.gridview.modelview.GridViewFragmentModelView;

public class ButtonOptionsModel extends VisibilityModel implements  Popup, View.OnClickListener
{
    private final String values[] = { "Delete File","StackJpeg","Raw to Dng","DngStack"};
    private final View.OnClickListener clickListeners[];
    private GridViewFragmentModelView gridViewFragmentModelView;

    public ButtonOptionsModel(View.OnClickListener onDeleteButtonListner,
                              View.OnClickListener onJpgStackButtonListner,
                              View.OnClickListener onRawToDngButtonListner,
                              View.OnClickListener onRawStackButtonListner, GridViewFragmentModelView gridViewFragmentModelView)
    {
        clickListeners = new View.OnClickListener[]{onDeleteButtonListner, onJpgStackButtonListner, onRawToDngButtonListner, onRawStackButtonListner};
        this.gridViewFragmentModelView = gridViewFragmentModelView;
    }

    public String[] getValues() {
        return values;
    }

    public View.OnClickListener[] getClickListeners() {
        return clickListeners;
    }

    @Override
    public View.OnClickListener getOnPopupChildClickListner() {
        return onClickListener;
    }

    private final View.OnClickListener onClickListener = v -> {
        Button button = (Button)v;
        String txt = (String)button.getText();
        for (int i = 0; i < getValues().length; i++)
        {
            if (txt.equals(getValues()[i]))
                getClickListeners()[i].onClick(v);
        }
    };

    @Override
    public void onClick(View v) {
        if (!gridViewFragmentModelView.getPopupMenuModel().getVisibility()) {
            gridViewFragmentModelView.getPopupMenuModel().setButtonOptionsModel(this::getOnPopupChildClickListner);
            gridViewFragmentModelView.getPopupMenuModel().setStrings(values);
        }
        else
            gridViewFragmentModelView.getPopupMenuModel().setVisibility(false);
    }
}
