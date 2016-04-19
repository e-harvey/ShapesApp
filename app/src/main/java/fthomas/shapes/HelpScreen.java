package fthomas.shapes;

import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.R.layout;
import android.widget.TextView;
import android.widget.VideoView;


/**
 * Created by marci_home on 4/18/16.
 */

public class HelpScreen extends AppCompatActivity {
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_help);
        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView HelpTitle = (TextView)findViewById(R.id.HelpTitle);
        HelpTitle.setTypeface(alltextTypeface);

        TextView instruct1 = (TextView)findViewById(R.id.instruct1);
        instruct1.setTypeface(alltextTypeface);

        TextView instruct2 = (TextView)findViewById(R.id.instruct2);
        instruct2.setTypeface(alltextTypeface);

        String videoPath = "/src/videos/tempDemo.mp4";
        VideoView videoView= (VideoView)findViewById(R.id.videoView);
        videoView.setVideoPath(videoPath);
        videoView.start();
    }
}