package freed.cam.ui.themesample.settings.opcode;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;

import java.io.File;

import freed.settings.OpCodeUrl;
import freed.utils.StringUtils;

/**
 * Created by KillerInk on 18.12.2017.
 */

public class OpcodeItem extends LinearLayout implements OpCodeDownloadTask.DownloadEvents {

    private TextView cameraid;
    private CheckBox isDownloadAvail;
    private CheckBox isDownloaded;
    private OpCodeUrl opCodeUrl;
    private File op2;
    private File op3;
    private int id;

    public OpcodeItem(Context context, OpCodeUrl opCodeUrl, int id)  {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.settings_opcodeitem, this);
        this.opCodeUrl = opCodeUrl;
        this.id = id;
        cameraid = findViewById(R.id.opcodeitem_cameraid);
        cameraid.setText("CameraID:" +id);
        isDownloadAvail = findViewById(R.id.opcodeitem_downloadable);
        isDownloadAvail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        isDownloaded = findViewById(R.id.opcodeitem_Downloaded);
        isDownloaded.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        update();


    }

    public void update()
    {
        if (this.opCodeUrl != null && (!TextUtils.isEmpty(opCodeUrl.getOpcode2Url()) || !TextUtils.isEmpty(opCodeUrl.getOpcode3Url())))
            isDownloadAvail.setChecked(true);
        else
            isDownloadAvail.setChecked(false);


        op2 = new File(StringUtils.GetFreeDcamConfigFolder+id+"opc2.bin");
        op3 = new File(StringUtils.GetFreeDcamConfigFolder+id+"opc3.bin");
        if (op2.exists() || op3.exists())
            isDownloaded.setChecked(true);
        else
            isDownloaded.setChecked(false);
    }

    public boolean hasOpCode2()
    {
        return op2 != null || op2.exists();
    }

    public boolean hasOpCode3()
    {
        return op3 != null || op3.exists();
    }

    public OpCodeUrl getOpCodeUrl()
    {
        return opCodeUrl;
    }

    public boolean downLoadNeeded()
    {
        return isDownloadAvail.isChecked() && !isDownloaded.isChecked();
    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onComplete() {
        this.post(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }
}
