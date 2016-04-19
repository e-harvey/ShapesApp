package fthomas.shapes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.R.layout;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");


        setContentView(R.layout.activity_main_menu);

        Typeface importedTypeface = Typeface.createFromAsset(getAssets(), "Arcade.ttf");
        TextView titleTypeface = (TextView)findViewById(R.id.Title);
        titleTypeface.setTypeface(importedTypeface);

        /** http://www.webpagepublicity.com/free-fonts-a4.html#FreeFonts */

        Button playTypeface = (Button)findViewById(R.id.Play);
        playTypeface.setTypeface(alltextTypeface);
        Button multiplayerTypeface = (Button)findViewById(R.id.Multiplayer);
        multiplayerTypeface.setTypeface(alltextTypeface);
        Button scoresTypeface = (Button)findViewById(R.id.Scores);
        scoresTypeface.setTypeface(alltextTypeface);

    }

    public void acknowledge(View v) {
        Toast myToast = Toast.makeText(getApplicationContext(), "This feature is not implemented yet", Toast.LENGTH_SHORT);
        myToast.show();
    }

    public void Play_Menu(View v) {
        Intent intent = new Intent(this, PlayMenu.class);
        startActivity(intent);
    }

    public void Friends_Menu(View v) {
        Intent intent = new Intent(this, FriendsMenu.class);
        startActivity(intent);
    }

    public void Scores_Menu(View v) {
        Intent intent = new Intent(this, ScoresMenu.class);
        startActivity(intent);
    }

    public void Help_Screen(View v) {
        Intent intent = new Intent(this, HelpScreen.class);
        startActivity(intent);
    }

    public void Settings_Screen(View v) {
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }
}
