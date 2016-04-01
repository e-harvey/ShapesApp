package fthomas.shapes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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
}
