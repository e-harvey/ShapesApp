package fthomas.shapes;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.R.layout;
import android.widget.TextView;

/**
 * Created by FThom_000 on 3/19/2016.
 */
public class PlayMenu extends AppCompatActivity {
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_play_menu);

        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView scorelabel = (TextView)findViewById(R.id.ScoreLabel);
        scorelabel.setTypeface(alltextTypeface);
        TextView score = (TextView)findViewById(R.id.Score);
        score.setTypeface(alltextTypeface);
    }
}
