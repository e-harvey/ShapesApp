package fthomas.shapes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import storage.shapes.DatabaseOperations;

/**
 * Created by marci_home on 4/16/16.
 */
public class SettingsScreen extends AppCompatActivity {
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        DatabaseOperations.DatabaseOperationsInit(this.getApplicationContext());

        setContentView(R.layout.activity_settings);

        if (DatabaseOperations.getLocalLoggedInUser() == null) {
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
        }

        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView SettingsTitle = (TextView) findViewById(R.id.SettingsTitle);
        SettingsTitle.setTypeface(alltextTypeface);

        TextView LogoutButton = (TextView) findViewById(R.id.LogoutButton);
        LogoutButton.setTypeface(alltextTypeface);

        TextView backToLoginScreenButton = (TextView) findViewById(R.id.backToLoginScreen_button);
        LogoutButton.setTypeface(alltextTypeface);

        TextView backtoMain_button = (TextView) findViewById(R.id.backtoMain_button);
        backtoMain_button.setTypeface(alltextTypeface);

        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user;
                user = DatabaseOperations.getLocalLoggedInUser();
                if (user != null) {

                } else {
                    Toast.makeText(getApplicationContext(), "Oh no we cant get the username.\nPlease try again later.", Toast.LENGTH_SHORT).show();
                }

                DatabaseOperations.logout(user);
                Toast.makeText(getApplicationContext(), "Successful logout", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
            }
        });

        backToLoginScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
            }
        });

    }

    public void MainMenu_Screen(View v) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
