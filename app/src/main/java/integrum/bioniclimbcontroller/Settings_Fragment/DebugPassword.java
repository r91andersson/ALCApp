package integrum.bioniclimbcontroller.Settings_Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-24.
 */
public class DebugPassword extends Activity {

    EditText password;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.intent_password);

            password = (EditText) findViewById(R.id.password);
            submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString();
                if(password.getText()!=null && !pass.matches("") ) {
                    int pw = Integer.valueOf(password.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("pw", pw);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                }
            }
        });

    }
}
