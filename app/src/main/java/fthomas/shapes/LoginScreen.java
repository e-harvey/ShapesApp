package fthomas.shapes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import storage.shapes.DatabaseOperations;

/**
 *  mmcbride 03/21/08
 */
public class LoginScreen extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView Title = (TextView) findViewById(R.id.Title);
        Title.setTypeface(alltextTypeface);

        TextView LoginButton = (TextView) findViewById(R.id.Login_Button);
        LoginButton.setTypeface(alltextTypeface);

        TextView CreateButton = (TextView) findViewById(R.id.Create_Button);
        CreateButton.setTypeface(alltextTypeface);


        // Initialize the databases
        DatabaseOperations.DatabaseOperationsInit(this.getApplicationContext());

        /* Create test users */
        DatabaseOperations.addUser("deadbeef", "wachtwoord");

        DatabaseOperations.addUser("prancingCow", "moooooo");

        DatabaseOperations.addUser("NommingNomer", "nomnom");

        DatabaseOperations.login("deadbeef", "wachtwoord");

        DatabaseOperations.addNewFriend("deadbeef", "prancingCow");
        DatabaseOperations.addNewFriend("deadbeef", "NommingNomer");
        /* End temporary unit tests */


        /**
         * Test to if user is logged in or not.
         */

        if (DatabaseOperations.getLocalLoggedInUser() == null) {
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            button_events();

        } else {
            /** call the main screen */
            Intent intent = new Intent(this, MainMenu.class);
            startActivity(intent);
        }
    }

    public void button_events (){

        Button LoginBtn = (Button)findViewById(R.id.Login_Button);
        Button CreateBtn = (Button)findViewById(R.id.Create_Button);

        EditText username = (EditText) findViewById(R.id.userName);
        EditText password = (EditText) findViewById(R.id.userPassword);

        String user = String.valueOf(username);
        String pass = String.valueOf(password);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseOperations.login(user,pass)

            }
            });
    }

}

