package fthomas.shapes;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

        TextView Previous = (TextView)findViewById(R.id.Previous);
        Previous.setTypeface(alltextTypeface);

        TextView Next = (TextView)findViewById(R.id.Next);
        Next.setTypeface(alltextTypeface);
    }
}