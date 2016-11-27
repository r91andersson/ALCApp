package integrum.bioniclimbcontroller.Calibration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Hashtable;

import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-25.
 */
public class CalibrationSetup extends Activity {
    ImageView movementImg;

    private static int imageSelected=R.drawable.open_hand;
    private static int videoSelected=R.raw.open_hand;

    int[] movementImage = new int[]{R.drawable.open_hand,
                                    R.drawable.close_hand,
                                    R.drawable.flex_hand,
                                    R.drawable.extend_hand,
                                    R.drawable.pronation,
                                    R.drawable.supination,
                                    R.drawable.side_grip,
                                    R.drawable.fine_grip,
                                    R.drawable.agree,
                                    R.drawable.pointer,
                                    R.drawable.no_image,
                                    R.drawable.thumb_flex,
                                    R.drawable.thumb_abduc,
                                    R.drawable.thumb_adduc,
                                    R.drawable.flex_elbow,
                                    R.drawable.extend_elbow,
                                    R.drawable.no_image,
                                    R.drawable.no_image,
                                    R.drawable.no_image,
                                    R.drawable.no_image,
                                    R.drawable.no_image,
                                    R.drawable.no_image,
                                    R.drawable.no_image,
                                    R.drawable.no_image};

    int[] movementVideo = new int[]{R.raw.open_hand,
                                    R.raw.close_hand,
                                    R.raw.hand_flex,
                                    R.raw.hand_extend,
                                    R.raw.pronation,
                                    R.raw.supination,
                                    R.raw.side_grip,
                                    R.raw.fine_grip,
                                    R.raw.agree,
                                    R.raw.pointer,
                                    R.raw.thumb_extend,
                                    R.raw.thumb_flex,
                                    R.raw.thumb_adduc_1,
                                    R.raw.thumb_adduc_2,
                                    R.raw.elbow_flex,
                                    R.raw.elbow_extend,
                                    R.raw.index_flex,
                                    R.raw.index_extend,
                                    R.raw.middle_flex,
                                    R.raw.middle_extend,
                                    R.raw.ring_flex,
                                    R.raw.ring_extend,
                                    R.raw.little_flex,
                                    R.raw.little_extend};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calibration_setup);

        Button next = (Button) findViewById(R.id.buttonNext);
        Button back = (Button) findViewById(R.id.buttonBack);
        movementImg = (ImageView) findViewById(R.id.imageMovement);
        Spinner sampleRate= (Spinner) findViewById(R.id.spinnerSampleRate);
        Spinner movement=(Spinner) findViewById(R.id.spinnerMovement);

        movementImg.setImageResource(imageSelected);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recordIntent = new Intent(getApplicationContext(),CalibrationInstructions.class);
                recordIntent.putExtra("videoResource",videoSelected);
                startActivity(recordIntent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

       sampleRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });

        movement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                imageSelected=movementImage[i];
                videoSelected=movementVideo[i];
                movementImg.setImageResource(imageSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
