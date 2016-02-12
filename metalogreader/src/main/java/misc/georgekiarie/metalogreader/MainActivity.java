package misc.georgekiarie.metalogreader;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                getLogMi3();
            }
        });
    }

    private void getLogMi3()
    {Process process;
        List<String> metada =  new ArrayList<>();
        try {
            process = new ProcessBuilder()
                    .command("su", "-c", "logcat -d -s AEC_PORT | grep -E iso")
                    .redirectErrorStream(true)
                    .start();


            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                //log.append(line);
                metada.add(line);
            }
            //  02-11 18:47:36.260   396 15501 E AEC_PORT
            // :
            // aec_port_proc_get_aec_data
            // exp
            // 0.043699
            // iso
            // 812
            // , exp-idx: 347
            // , lc: 3440
            // , gain: 8.128906



            TextView tv = (TextView)findViewById(R.id.textView1);
            String[] split0 = metada.get(metada.size()-1).split(",");
            String[] split1 = split0[0].split(":");
            String[] split2 = split1[3].split(" ");

            float exposureTime = Float.parseFloat(split2[3]);
            //int iso = Integer.parseInt(split0[1].split(":")[1]);
            int iso = Integer.parseInt(split2[5]);
           // int flash = Integer.parseInt(split0[2].split(":")[1]);
           // float ActualISO = Float.parseFloat(split0[5].split(":")[1]);
           // int isoActual = Math.round(ActualISO*100);

            //tv.setText(split1[3] + " " + split0[1] + " " + split0[2] + " " + split0[5]);
            tv.setText(exposureTime+" "+ iso+" ");
        }
        catch (IOException e) {}
    }

    private void getLog()
    {Process process;
        List<String> metada =  new ArrayList<>();
        try {
             process = new ProcessBuilder()
                    .command("su", "-c", "logcat -d -s AEC_PORT | grep -E iso")
                    .redirectErrorStream(true)
                    .start();


            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                //log.append(line);
                metada.add(line);
            }
            //  02-11 18:47:36.260   396 15501 E AEC_PORT
            // :
            // aec_port_proc_get_aec_data
            // exp
            // 0.043699
            // iso
            // 812
            // , exp-idx: 347
            // , lc: 3440
            // , gain: 8.128906



            TextView tv = (TextView)findViewById(R.id.textView1);
            String[] split0 = metada.get(metada.size()-1).split(",");
            String[] split1 = split0[0].split(":");

            float exposureTime = Float.parseFloat(split1[3]);
            int iso = Integer.parseInt(split0[1].split(":")[1]);
            int flash = Integer.parseInt(split0[2].split(":")[1]);
            float ActualISO = Float.parseFloat(split0[5].split(":")[1]);
            int isoActual = Math.round(ActualISO*100);

            //tv.setText(split1[3] + " " + split0[1] + " " + split0[2] + " " + split0[5]);
            tv.setText(exposureTime+" "+ iso+" "+ flash+" "+isoActual);
        }
        catch (IOException e) {}
    }

    private void clearLog(){
        try {
            Process process = new ProcessBuilder()
                    .command("su","-c","logcat", "-c")
                    .redirectErrorStream(true)
                    .start();
            process.destroy();
        } catch (IOException e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
