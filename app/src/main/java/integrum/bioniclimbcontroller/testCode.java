package integrum.bioniclimbcontroller;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.util.Random;

import integrum.bioniclimbcontroller.Database.SettingsDbHelper;

/**
 * Created by Robin on 2016-11-09.
 */
public class testCode extends Activity{
    public ProgressBar progbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        final SeekBar mSeekbaBar = (SeekBar)findViewById(R.id.seekBar);

        progbar = (ProgressBar)findViewById(R.id.progressBarasdsa);

        startAddingRandomData();

        mSeekbaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void startAddingRandomData() {
        new Thread(
                new Runnable() {
                    public void run() {
                        while (true) {
                            //Set the progress bar
                            progbar.setProgress(generateRandomData(0, 100));

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

    }

    public int generateRandomData(int n1, int n2){
        Random r = new Random();
        return r.nextInt(n2 - n1) + n1;
    }

}
