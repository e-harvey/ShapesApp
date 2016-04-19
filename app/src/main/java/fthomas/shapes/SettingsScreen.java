package fthomas.shapes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by marci_home on 4/16/16.
 */
public class SettingsScreen extends AppCompatActivity {
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_settings);
        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView SettingsTitle = (TextView)findViewById(R.id.SettingsTitle);
        SettingsTitle.setTypeface(alltextTypeface);

        TextView bgVolumeText = (TextView)findViewById(R.id.bgVolumeText);
        bgVolumeText.setTypeface(alltextTypeface);

    }
}
