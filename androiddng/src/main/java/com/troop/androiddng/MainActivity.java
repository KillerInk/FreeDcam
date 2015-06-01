package com.troop.androiddng;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.troop.freedcam.utils.DeviceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import troop.com.androiddng.R;

public class MainActivity extends Activity {

    final int g3W = 4160;
    final int g3H = 3120;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(buttonclick);
	}

	private DngSupportedDevices.SupportedDevices getDevice(String filename)
	{
		if (filename.toLowerCase().contains("yureka"))
			return DngSupportedDevices.SupportedDevices.yureka;
		if (filename.toLowerCase().contains("lg_g3"))
			return DngSupportedDevices.SupportedDevices.LG_G3;
		if (filename.toLowerCase().contains("gione_e7"))
			return DngSupportedDevices.SupportedDevices.Gione_E7;
		if (filename.toLowerCase().contains("one_m8"))
			return DngSupportedDevices.SupportedDevices.HTC_One_m8;
		if (filename.toLowerCase().contains("one_m9"))
			return DngSupportedDevices.SupportedDevices.HTC_One_m9;
		if (filename.toLowerCase().contains("htc_one_sv"))
			return DngSupportedDevices.SupportedDevices.HTC_One_Sv;
		if (filename.toLowerCase().contains("k910"))
			return DngSupportedDevices.SupportedDevices.Lenovo_k910;
		if(filename.toLowerCase().contains("lg_g2"))
			return DngSupportedDevices.SupportedDevices.LG_G2;
		if (filename.toLowerCase().contains("zte"))
			return DngSupportedDevices.SupportedDevices.zteAdv;
		if (filename.toLowerCase().contains("xperial"))
			return DngSupportedDevices.SupportedDevices.Sony_XperiaL;
		if (filename.toLowerCase().contains("htc_one_xl"))
			return DngSupportedDevices.SupportedDevices.HTC_One_XL;
		if (filename.toLowerCase().contains("one_plus_one"))
			return DngSupportedDevices.SupportedDevices.OnePlusOne;
		if (filename.toLowerCase().contains("xiaomi_redmi_note"))
			return DngSupportedDevices.SupportedDevices.Xiaomi_Redmi_Note;
		return null;
	}
	
	Button.OnClickListener buttonclick = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			File rawCollectionFolder = new File(Environment.getExternalStorageDirectory()+"/android_raw_collection");
			File[] rawfiles = rawCollectionFolder.listFiles();
			for (File file : rawfiles)
			{
				if (!file.isDirectory() && file.getAbsolutePath().endsWith(".raw")) {

					DngSupportedDevices.SupportedDevices devices = getDevice(file.getName());
					if (devices == null) {
						//Toast.makeText(null, "Unkown RAWFILE: " + file.getName(), Toast.LENGTH_LONG).show();
						Log.d("rawtodng", "Unkown RAWFILE: " + file.getName());
					} else {
						byte[] data = null;
						try {
							data = RawToDng.readFile(file);
							Log.d("Main", "Filesize: " + data.length + " File:" +file.getAbsolutePath());

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						String out = file.getAbsolutePath().replace(".raw", ".dng");
						RawToDng dng = RawToDng.GetInstance();
						dng.SetBayerData(data, out);
						dng.setExifData(100, 0, 0, 0, 0, "", "0", 0);
						dng.WriteDNG(devices);
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
