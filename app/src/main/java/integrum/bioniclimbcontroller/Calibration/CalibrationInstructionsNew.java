package integrum.bioniclimbcontroller.Calibration;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-25.
 */
public class CalibrationInstructionsNew extends Activity{
    private VideoView mVideoView;
    private int[] videoId;
    private static final int calibInstr=2;
    private static final int countDownRequest=1;
    private static final int countDownRelaxRequest=2;
    private TextView countdown;
    private TextView mInsturctionName;
    private int videoIdx=0;
    private boolean playNextVideo;
    private LinearLayout recordingLay;

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

    String [] movementNames = new String[] {"Open Hand",
            "Close Hand",
            "Flex Hand",
            "Extend hand",
            "Pronation",
            "Supination",
            "Side Grip",
            "Fine Grip",
            "Agree",
            "Pointer",
            "Thumb Extend",
            "Thumb Flex",
            "Tumb Abduc 1",
            "Thumb Abduc 2",
            "Flex Elbow",
            "Extend Elbow",
            "Index Flex",
            "Index Extend",
            "Middle Flex",
            "Middle Extend",
            "Ring Flex",
            "Ring Extend",
            "Little Flex",
            "Little Extend"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_instructions_new);
        mInsturctionName=(TextView) findViewById(R.id.movementInsturctionName);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        recordingLay = (LinearLayout) findViewById(R.id.videoRecordingLayout);
        recordingLay.setVisibility(View.GONE);
        // Get video ID's
        videoId = getIntent().getExtras().getIntArray("videoIndex");
        Button next = (Button) findViewById(R.id.nextMovRecord);
        mInsturctionName.setText(movementNames[videoId[0]]);
        Intent recordIntent = new Intent(getApplicationContext(),CalibrationCountdownNew.class);
        startActivityForResult(recordIntent, countDownRequest);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoIdx < videoId.length) {
                    if (playNextVideo) {
                        playNextVideo = false;
                        recordingLay.setVisibility(View.GONE);
                        Intent recordIntent = new Intent(getApplicationContext(), CalibrationCountdownNew.class);
                        startActivityForResult(recordIntent, countDownRequest);
                    }
                }
            }
        });


    }


    public void playVideo() {
        String uriPath = "android.resource://"+getPackageName()+"/"+ movementVideo[videoId[videoIdx]];
        Uri uri=Uri.parse(uriPath);
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();
        videoIdx++;
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                // Send stop message to Embedded System
                Intent relax = new Intent(getApplicationContext(), CalibrationCountdownRelax.class);
                startActivityForResult(relax, countDownRelaxRequest);
            }
        }.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==countDownRequest) {
                recordingLay.setVisibility(View.VISIBLE);
                mInsturctionName.setText(movementNames[videoIdx]);
                playVideo();
            }

            if(requestCode==countDownRelaxRequest){
                playNextVideo=true;
                if(videoIdx==videoId.length){
                    Intent result = new Intent();
                    result.putExtra("movementIdx",videoId);
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        }
    }
}
