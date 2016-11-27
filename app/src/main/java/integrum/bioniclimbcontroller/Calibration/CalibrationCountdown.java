package integrum.bioniclimbcontroller.Calibration;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.widget.TextView;

import org.w3c.dom.Text;

import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-25.
 */
public class CalibrationCountdown extends Activity {

    private TextView countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_recording_countdown);
        countdown = (TextView) findViewById(R.id.countdown);

        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                String timeLeft = "" + millisUntilFinished / 1000;
                countdown.setText(timeLeft);
            }

            public void onFinish() {
                updateText();
                startSecondTimer();
            }

        }.start();

    }




    public void updateText() {
        countdown.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 55);
        countdown.setText("Do movement!");
    }

    public void startSecondTimer(){
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                setResult(Activity.RESULT_OK);
                finish();
            }

        }.start();

    }

    }


