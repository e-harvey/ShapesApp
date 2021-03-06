package gameinterface.shapes;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;


/**
 * Created by marci_home on 4/18/16.
 */

public class HelpScreen extends AppCompatActivity {

    /**
     * Method called on the creation of the window
     * @param savedInstance - The savedInstance to pass into this method
     */
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_help);

        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        /*TextView instruct2 = (TextView)findViewById(R.id.instruct2);
        instruct2.setTypeface(alltextTypeface);*/

        // TODO this doesn't work on some API versions
        Uri uri = Uri.parse("https://shapes.evanharvey.net/demo.mp4"); //Declare your url here.
        VideoView mVideoView  = (VideoView)findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setEnabled(true);

        mVideoView.setMediaController(mediaController);
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.setEnabled(true);
        mVideoView.start();
    }
}