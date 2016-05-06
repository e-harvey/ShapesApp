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

import org.w3c.dom.Text;

import java.util.ArrayList;

import static org.junit.Assert.*;

import storage.shapes.DatabaseOperations;

public class MainMenu extends AppCompatActivity {

    /**
     * Method called on the creation of the window
     * @param savedInstanceState The savedInstance to pass into this method
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseOperations.DatabaseOperationsInit(this.getApplicationContext());

        setContentView(R.layout.activity_main_menu);

        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        Typeface importedTypeface = Typeface.createFromAsset(getAssets(), "Arcade.ttf");
        TextView titleTypeface = (TextView)findViewById(R.id.Title);
        titleTypeface.setTypeface(importedTypeface);

        /*ImageButton helpButton = (ImageButton) findViewById(R.id.Help);
        helpButton.setPivotX(0);

        ImageButton settingButton = (ImageButton) findViewById(R.id.Settings);
        settingButton.setPivotX(getResources().getDisplayMetrics().widthPixels);*/

        /** http://www.webpagepublicity.com/free-fonts-a4.html#FreeFonts */

        Button playTypeface = (Button)findViewById(R.id.Play);
        playTypeface.setTypeface(alltextTypeface);
        if (!DatabaseOperations.getRemoteLoginStatus()) {
            playTypeface.setText("Play offline as: " + DatabaseOperations.getLocalLoggedInUser());
        }

        Button ChallengeTypeFace = (Button)findViewById(R.id.Challenge);
        ChallengeTypeFace.setTypeface(alltextTypeface);

        Button multiplayerTypeface = (Button)findViewById(R.id.Multiplayer);
        multiplayerTypeface.setTypeface(alltextTypeface);

        Button scoresTypeface = (Button)findViewById(R.id.Scores);
        scoresTypeface.setTypeface(alltextTypeface);
    }

    public void Play_Menu(View v) {
        if (checkStatus()) {
            PlayMenu.setType(PlayMenu.gamePlayType.SINGLE_PLAYER);
            Intent intent = new Intent(this, PlayMenu.class);
            startActivity(intent);
        }
    }

    public void Friends_Menu(View v) {
        if (checkStatus()) {
            Intent intent = new Intent(this, FriendsMenu.class);
            startActivity(intent);
        }
    }

    public void Scores_Menu(View v) {
        if (checkStatus()) {
            Intent intent = new Intent(this, ScoresMenu.class);
            startActivity(intent);
        }
    }

    public void Help_Screen(View v) {
        if (checkStatus()) {
            Intent intent = new Intent(this, HelpScreen.class);
            startActivity(intent);
        }
    }

    public void Settings_Screen(View v) {
        if (checkStatus()) {
            Intent intent = new Intent(this, SettingsScreen.class);
            startActivity(intent);
        }
    }

    public void Challenge_Menu(View view) {
        if (!DatabaseOperations.getRemoteLoginStatus()) {
            Toast.makeText(getApplicationContext(), "Check network connection and login again please.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            return;
        }
        if (checkStatus()) {
            PlayMenu.setType(PlayMenu.gamePlayType.DAILY_CHALLENGE);
            Intent intent = new Intent(this, PlayMenu.class);
            startActivity(intent);
        }
    }

    private boolean checkStatus() {
        if (DatabaseOperations.getLocalLoggedInUser() == null) {
            Toast.makeText(getApplicationContext(), "Hey... you need to login first.\nRerouting to login screen.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            return false;
        }
        return true;
    }
}
