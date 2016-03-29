package misc.georgekiarie.roothacks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView)findViewById(R.id.output);
        tv.setText(getCameraMode());

    }

    private String getCameraMode()
    {
        List<String> metadata = new ArrayList<>();
        Process process;
        try {
            process = new ProcessBuilder()
                    .command("su", "-c", "getprop ro.product.board")
                    .redirectErrorStream(true)
                    .start();


            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            //StringBuilder log=new StringBuilder();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                //log.append(line);
                metadata.add(line);
            }


            process.destroy();


            //v.setText(exposureTime+" "+ iso+" "+ flash+" "+isoActual);
        } catch (IOException e) {
        }

        return metadata.get(0);

    }
}
