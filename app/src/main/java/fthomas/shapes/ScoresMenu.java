package fthomas.shapes;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.R.layout;
import android.widget.TextView;

import storage.shapes.DatabaseOperations;

/**
 * Created by FThom_000 on 3/20/2016.
 */
public class ScoresMenu extends AppCompatActivity {
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_scores);

        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView ScoreTitle = (TextView)findViewById(R.id.HighScore_Title);
        ScoreTitle.setTypeface(alltextTypeface);

        TextView High_Score_Text = (TextView)findViewById(R.id.High_Score_Text);
        High_Score_Text.setTypeface(alltextTypeface);

        TextView UserScore = (TextView)findViewById(R.id.UserScore);
        UserScore.setTypeface(alltextTypeface);
        UserScore.setText(String.valueOf(DatabaseOperations.getHighScore(DatabaseOperations.getLocalLoggedInUser())));

        TextView Friends_Scores_Text = (TextView)findViewById(R.id.Friends_Scores_Text);
        Friends_Scores_Text.setTypeface(alltextTypeface);
        // TODO: Display friends' highscores.

    }
}
