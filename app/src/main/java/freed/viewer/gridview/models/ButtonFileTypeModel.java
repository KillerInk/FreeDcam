package freed.viewer.gridview.models;

import android.view.View;
import android.widget.Button;

import freed.file.FileListController;
import freed.viewer.gridview.modelview.GridViewFragmentModelView;

public class ButtonFileTypeModel extends ButtonDoAction implements Popup , View.OnClickListener {

    private GridViewFragmentModelView gridViewFragmentModelView;

    public ButtonFileTypeModel(GridViewFragmentModelView gridViewFragmentModelView)
    {
        this.gridViewFragmentModelView = gridViewFragmentModelView;
    }

    private final String values[] = { "ALL", "RAW","BAYER","DNG","JPS","JPG", "MP4"};

    @Override
    public View.OnClickListener getOnPopupChildClickListner() {
        return onPopupClickListener;
    }

    private final View.OnClickListener onPopupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button)v;
            String txt = (String)button.getText();
            gridViewFragmentModelView.setFormatsToShow(getFormat(txt));
            setText(txt);
        }
    };

    private FileListController.FormatTypes getFormat(String txt)
    {
        if (txt.equals("RAW"))
            return FileListController.FormatTypes.raw;
        else if (txt.equals("BAYER"))
            return FileListController.FormatTypes.raw;
        else if (txt.equals("DNG"))
            return FileListController.FormatTypes.dng;
        else if (txt.equals("JPS"))
            return FileListController.FormatTypes.jps;
        else if (txt.equals("JPG"))
            return FileListController.FormatTypes.jpg;
        else if (txt.equals("MP4"))
            return FileListController.FormatTypes.mp4;
        else return FileListController.FormatTypes.all;

    }

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
