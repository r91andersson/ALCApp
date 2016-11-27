package integrum.bioniclimbcontroller.Calibration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-25.
 */
public class CalibrationRecording extends Activity {
    private static final int countDownRequest=1;
    TextView introText;
    private LinearLayout linlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_calibration_recording);
        linlay= (LinearLayout) findViewById(R.id.linearLayoutRecording);
        linlay.setVisibility(View.INVISIBLE);

        introText = (TextView) findViewById(R.id.introText);
        Button menu = (Button) findViewById(R.id.menu);
        Button redo = (Button) findViewById(R.id.redo);

        Intent recordIntent = new Intent(getApplicationContext(),CalibrationCountdown.class);
        startActivityForResult(recordIntent,countDownRequest);



        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Constants.RESULT_MENU);
                finish();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setResult(Constants.RESULT_REDO);
            finish();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==Activity.RESULT_OK){
            if(requestCode==countDownRequest){
                linlay.setVisibility(View.VISIBLE);
            }

        }

    }
}
