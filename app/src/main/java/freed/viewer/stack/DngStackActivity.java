package freed.viewer.stack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.R;

import freed.ActivityAbstract;
import freed.jni.DngStack;
import freed.utils.LocationManager;
import freed.viewer.dngconvert.DngConvertingFragment;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 25.10.2016.
 */

public class DngStackActivity extends ActivityAbstract
{
    private String[] filesToStack = null;
    private Button stackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dngstackactivity);
        stackButton = (Button)findViewById(R.id.button_dngstack);
        stackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filesToStack != null) {
                    DngStack stack = new DngStack(filesToStack);
                    stack.StartStack(getContext());
                    stackButton.setBackgroundResource(R.drawable.stack_done);
                    stackButton.setClickable(false);
                }
            }
        });
        TouchImageView imageView = (TouchImageView) findViewById(R.id.imageview_dngstack);
        filesToStack = getIntent().getStringArrayExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT);
        if (filesToStack != null)
            ((TextView)findViewById(R.id.rawList)).setText(filesToStack.length+"");


    }

    @Override
    public LocationManager getLocationManager() {
        return null;
    }

    @Override
    public void WorkHasFinished(FileHolder fileHolder) {

    }

    @Override
    public void WorkHasFinished(FileHolder[] fileHolder) {

    }
}
