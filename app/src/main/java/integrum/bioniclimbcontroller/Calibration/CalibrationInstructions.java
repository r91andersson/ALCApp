package integrum.bioniclimbcontroller.Calibration;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-25.
 */
public class CalibrationInstructions extends Activity{
    private VideoView mVideoView;
    private int videoId;
    private static final int calibInstr=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_instructions);

        // Old
        videoId = getIntent().getExtras().getInt("videoResource");

        Button replay = (Button) findViewById(R.id.replayBtn);
        Button record = (Button) findViewById(R.id.recordBtn);

        mVideoView = (VideoView) findViewById(R.id.videoView);

        String uriPath = "android.resource://"+getPackageName()+"/"+ videoId;
        Uri uri=Uri.parse(uriPath);

        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoView mVideoView = (VideoView) findViewById(R.id.videoView);
                String uriPath = "android.resource://" + getPackageName() + "/" + videoId;
                Uri uri = Uri.parse(uriPath);

                mVideoView.setVideoURI(uri);
                mVideoView.requestFocus();
                mVideoView.start();
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recordIntent = new Intent(getApplicationContext(),CalibrationRecording.class);
                startActivityForResult(recordIntent,calibInstr);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==Constants.RESULT_MENU){
            if(requestCode==calibInstr) {
                finish();
            }
        } else if (resultCode== Constants.RESULT_REDO){
            if(requestCode==calibInstr){
                //Restart recording session
            }
        }
    }
}
