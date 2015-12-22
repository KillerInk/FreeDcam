package troop.com.imageviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.troop.androiddng.DngSupportedDevices;
import com.troop.freedcam.utils.DeviceUtils;

import java.io.File;

/**
 * Created by troop on 22.12.2015.
 */
public class DngConvertingFragment extends Fragment
{
    View view;
    EditText editTextwidth;
    EditText editTextheight;
    Spinner spinnerMatrixProfile;
    Button buttonconvertToDng;
    String[] filesToConvert;
    DngSupportedDevices.DngProfile dngprofile;

    public static final String EXTRA_FILESTOCONVERT = "extra_files_to_convert";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.contex = getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.dngconvertingfragment, container, false);
        this.editTextwidth = (EditText)view.findViewById(R.id.editText_width);
        this.editTextheight = (EditText)view.findViewById(R.id.editText_height);
        this.spinnerMatrixProfile = (Spinner)view.findViewById(R.id.spinner_MatrixProfile);
        this.buttonconvertToDng = (Button)view.findViewById(R.id.button_convertDng);
        buttonconvertToDng.setOnClickListener(convertToDngClick);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.filesToConvert = getActivity().getIntent().getStringArrayExtra(EXTRA_FILESTOCONVERT);
        if (filesToConvert != null && filesToConvert.length > 0)
        {
            DngSupportedDevices.SupportedDevices devices = DngSupportedDevices.getDevice();
            dngprofile = new DngSupportedDevices().getProfile(devices,(int) new File(filesToConvert[0]).length());
            editTextwidth.setText(dngprofile.widht+"");
            editTextheight.setText(dngprofile.height+"");
        }
    }

    private View.OnClickListener convertToDngClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {

        }
    };
}
