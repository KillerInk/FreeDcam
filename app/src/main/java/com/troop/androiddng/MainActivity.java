package com.troop.androiddng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Files;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.troop.freedcam.R;

public class MainActivity extends Activity {

	EditText box;
    final int g3W = 4160;
    final int g3H = 3120;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(buttonclick);
		/*Button button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(buttonclick2);*/
		box =(EditText)findViewById(R.id.textView1);
		
		Button buttonPlus = (Button)findViewById(R.id.button2);
		buttonPlus.setOnClickListener(buttonclick2);
		Button buttonMinus = (Button)findViewById(R.id.button3);
		buttonMinus.setOnClickListener(buttonclick3);
		
		box.setText(""+3584);
		
		
	}
	
	Button.OnClickListener buttonclick = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			File filetoread = new File(Environment.getExternalStorageDirectory()+"/test.raw");
			File filetosave = new File(Environment.getExternalStorageDirectory()+"/test.dng");
			//4128x3096
			int width = 3282; //4208;
			int height =  2448; //3120;
			
			String in = filetoread.getAbsolutePath();
			byte[] data = null;
			
			
			try 
			{
				data = readFile(filetoread);
				Log.d("Main", "Filesize: " + data.length);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String out = filetosave.getAbsolutePath();
            int r = data.length / g3H;
            int rowsize = Integer.parseInt(box.getEditableText().toString());

			RawToDng.convertRawBytesToDng(data.clone(), out, g3W,g3H, RawToDng.g3_color1,RawToDng.g3_color2, RawToDng.g3_neutral, RawToDng.g3_blacklevel, RawToDng.GRBG, rowsize);
			data = null;
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	        intent.setData(Uri.fromFile(filetosave));
	        sendBroadcast(intent);
	        
	        Intent i=new Intent(Intent.ACTION_VIEW);
	        Uri uri = Uri.fromFile(filetosave);
            i.setDataAndType(uri, "image/*");
            
            startActivity(i);
		}
	};
	
Button.OnClickListener buttonclick2 = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int rowsize = Integer.parseInt(box.getEditableText().toString());
			rowsize++;
			box.setText(""+rowsize);
		}
	};
	
Button.OnClickListener buttonclick3 = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int rowsize = Integer.parseInt(box.getEditableText().toString());
			rowsize--;
			box.setText(""+rowsize);
		}
	};
	
	public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

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
