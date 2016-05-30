package com.troop.androiddng;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.freedcam.jni.RawToDng;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.troop.freedcam.R;

import java.io.File;
import java.io.IOException;


public class RawToDngTestActivity extends Activity {

    final int g3W = 4160;
    final int g3H = 3120;
	final String TAG = RawToDngTestActivity.class.getSimpleName();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(buttonclick);
		DeviceUtils.CheckAndSetDevice(getApplicationContext());
	}

	private DeviceUtils.Devices getDevice(String filename)
	{
		if (filename.toLowerCase().contains("yureka"))
			return DeviceUtils.Devices.Yu_Yureka;
		if (filename.toLowerCase().contains("lg_g3"))
			return DeviceUtils.Devices.LG_G3;
		if (filename.toLowerCase().contains("gione_e7"))
			return DeviceUtils.Devices.GioneE7;
		if (filename.toLowerCase().contains("one_m8"))
			return DeviceUtils.Devices.Htc_M8;
		if (filename.toLowerCase().contains("one_m9"))
			return DeviceUtils.Devices.Htc_M9;
		if (filename.toLowerCase().contains("htc_one_sv"))
			return DeviceUtils.Devices.Htc_One_Sv;
		if (filename.toLowerCase().contains("k910"))
			return DeviceUtils.Devices.LenovoK910;
		if(filename.toLowerCase().contains("lg_g2"))
			return DeviceUtils.Devices.LG_G2;
		if (filename.toLowerCase().contains("zte"))
			return DeviceUtils.Devices.ZTE_ADV;
		if (filename.toLowerCase().contains("xperial"))
			return DeviceUtils.Devices.Sony_XperiaL;
		if (filename.toLowerCase().contains("htc_one_xl"))
			return DeviceUtils.Devices.Htc_One_Xl;
		if (filename.toLowerCase().contains("one_plus_one"))
			return DeviceUtils.Devices.OnePlusOne;
		if (filename.toLowerCase().contains("xiaomi_redmi_note"))
			return DeviceUtils.Devices.Xiaomi_RedmiNote;
		if (filename.toLowerCase().contains("xiaomi_mi3w"))
			return DeviceUtils.Devices.XiaomiMI3W;
		if (filename.toLowerCase().contains("xiaomi_mi4w"))
			return DeviceUtils.Devices.XiaomiMI4W;
		if (filename.contains("Meizu_Mx4"))
			return DeviceUtils.Devices.MeizuMX4_MTK;
		if (filename.contains("Meizu_MX5"))
			return DeviceUtils.Devices.MeizuMX5_MTK;
		if (filename.contains("MTK_THL5000"))
			return DeviceUtils.Devices.THL5000_MTK;
		if (filename.contains("Xiaomi_MI_NOTE_Pro"))
			return DeviceUtils.Devices.XiaomiMI_Note_Pro;
		if (filename.contains("alcatel idol 3 "))
			return DeviceUtils.Devices.Alcatel_Idol3;
		if (filename.contains("vivo Xplay3S"))
			return DeviceUtils.Devices.Vivo_Xplay3s;
		if (filename.contains("I_Mobile_I_StyleQ6"))
			return DeviceUtils.Devices.I_Mobile_I_StyleQ6;
		if (filename.contains("MotoX_pure"))
			return DeviceUtils.Devices.Moto_MSM8982_8994;
		if(filename.contains("SonyM5"))
			return DeviceUtils.Devices.SonyM5_MTK;
		if(filename.contains("SonyM5"))
			return DeviceUtils.Devices.SonyC5_MTK;
		if(filename.contains("Xiaomi_RedmiNote2"))
			return DeviceUtils.Devices.Xiaomi_RedmiNote2_MTK;
		if(filename.contains("g4_raw10"))
			return DeviceUtils.Devices.LG_G4;
		return null;
	}
	
	private Button.OnClickListener buttonclick = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			File rawCollectionFolder = new File(Environment.getExternalStorageDirectory()+"/android_raw_collection");
			File[] rawfiles = rawCollectionFolder.listFiles();
			for (File file : rawfiles)
			{
				if (!file.isDirectory() && (file.getAbsolutePath().endsWith(StringUtils.FileEnding.RAW) ||file.getAbsolutePath().endsWith(StringUtils.FileEnding.BAYER))) {

					DeviceUtils.Devices devices = getDevice(file.getName());
					if (devices == null) {
						//Toast.makeText(null, "Unkown RAWFILE: " + file.getName(), Toast.LENGTH_LONG).show();
						Logger.d("rawtodng", "Unkown RAWFILE: " + file.getName());
					} else {
						byte[] data = null;
						try {
							data = RawToDng.readFile(file);
							Logger.d("Main", "Filesize: " + data.length + " File:" +file.getAbsolutePath());

						} catch (IOException e) {
							Logger.exception(e);
						}

						String out = null;
						if (file.getName().endsWith(StringUtils.FileEnding.RAW))
						 	out = file.getAbsolutePath().replace(StringUtils.FileEnding.RAW, StringUtils.FileEnding.DNG);
						if (file.getName().endsWith("bayer"))
							out = file.getAbsolutePath().replace(StringUtils.FileEnding.BAYER, StringUtils.FileEnding.DNG);
						RawToDng dng = RawToDng.GetInstance();
						dng.SetBayerData(data, out);
						dng.setExifData(100, 0, 0, 0, 0, "", "0", 0);
						dng.WriteDNG(devices,null);
						data = null;
						Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
						intent.setData(Uri.fromFile(file));
						sendBroadcast(intent);
					}
				}
			}
		}
	};
	

	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}
}
