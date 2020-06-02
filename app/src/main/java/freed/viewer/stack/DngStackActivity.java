package freed.viewer.stack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.R;

import freed.ActivityAbstract;
import freed.jni.DngStack;
import freed.utils.LocationManager;
import freed.viewer.dngconvert.DngConvertingFragment;


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
        stackButton = findViewById(R.id.button_dngstack);
        stackButton.setOnClickListener(v -> {
            if (filesToStack != null) {
                DngStack stack = new DngStack(filesToStack);
                stack.StartStack(getApplicationContext());
                stackButton.setBackgroundResource(R.drawable.stack_done);
                stackButton.setClickable(false);
            }
        });
        TouchImageView imageView = findViewById(R.id.imageview_dngstack);
        filesToStack = getIntent().getStringArrayExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT);
        if (filesToStack != null)
            ((TextView)findViewById(R.id.rawList)).setText(filesToStack.length+"");


    }

    @Override
    public LocationManager getLocationManager() {
        return null;
    }

    @Override
    protected void setContentToView() {

    }

}
