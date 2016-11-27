package integrum.bioniclimbcontroller.Calibration;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.widget.TextView;

import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-25.
 */
public class CalibrationCountdownNew extends Activity {

    private TextView countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_recording_countdown);
        countdown = (TextView) findViewById(R.id.countdown);

        countdown.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 55);
        countdown.setText("Prepare to do movement");
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                String timeLeft = "Prepare to do movement in.." + millisUntilFinished / 1000;
                countdown.setText(timeLeft);
            }
            public void onFinish() {
                setResult(Activity.RESULT_OK);
                finish();
            }

        }.start();

    }
    }


