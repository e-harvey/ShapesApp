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


        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView SettingsTitle = (TextView)findViewById(R.id.SettingsTitle);
        SettingsTitle.setTypeface(alltextTypeface);

        TextView bgVolumeText = (TextView)findViewById(R.id.bgVolumeText);
        bgVolumeText.setTypeface(alltextTypeface);

        TextView effVolumeText = (TextView)findViewById(R.id.effVolumeText);
        effVolumeText.setTypeface(alltextTypeface);

        TextView LogoutButton = (TextView)findViewById(R.id.LogoutButton);
        LogoutButton.setTypeface(alltextTypeface);

        TextView backtoMain_button = (TextView)findViewById(R.id.backtoMain_button);
        backtoMain_button.setTypeface(alltextTypeface);

        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Get the local logged in user.
                 *
                 * @return the username of the logged in user, otherwise null
                 */
                String user;
                user = DatabaseOperations.getLocalLoggedInUser();
                if (user != null) {

                } else {
                    Toast.makeText(getApplicationContext(), "Oh no we cant get the username. \nPlease try again later.", Toast.LENGTH_SHORT).show();
                }
                /**
                 * Log the user out of the given database.
                 *
                 * @param username the user's username
                 */
                DatabaseOperations.logout(user);
                Toast.makeText(getApplicationContext(), "Successful logout", Toast.LENGTH_SHORT).show();
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
