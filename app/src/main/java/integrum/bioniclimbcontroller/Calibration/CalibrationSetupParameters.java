package integrum.bioniclimbcontroller.Calibration;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-11-04.
 */
public class CalibrationSetupParameters extends Activity {

    private Button saveBtn;
    private Button returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_parameter_setup);

        saveBtn = (Button) findViewById(R.id.buttonSave);
        returnBtn = (Button) findViewById(R.id.buttonReturn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }
}
