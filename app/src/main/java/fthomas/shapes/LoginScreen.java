package fthomas.shapes;

import android.content.Intent;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.Color;
import android.widget.EditText;

import org.w3c.dom.Text;

import storage.shapes.DatabaseOperations;

/**
 *  mmcbride 03/21/08
 */
public class LoginScreen extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseOperations.DatabaseOperationsInit(this.getApplicationContext());
        setContentView(R.layout.activity_login_screen);

        /**
         * setting text typeface
         */
        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView Title = (TextView) findViewById(R.id.Title);
        Title.setTypeface(alltextTypeface);

        TextView userNameLabel = (TextView) findViewById(R.id.userNameLabel);
        userNameLabel.setTypeface(alltextTypeface);

        TextView userPasswordLabel = (TextView) findViewById(R.id.userPasswordLabel);
        userPasswordLabel.setTypeface(alltextTypeface);

        EditText password = (EditText) findViewById(R.id.userPassword);
        password.setTypeface(alltextTypeface);

        EditText username = (EditText) findViewById(R.id.userName);
        username.setTypeface(alltextTypeface);

        Button LoginBtn = (Button)findViewById(R.id.Login_Button);
        LoginBtn.setTypeface(alltextTypeface);

        Button CreateBtn = (Button)findViewById(R.id.Create_Button);
        CreateBtn.setTypeface(alltextTypeface);

        Button DeleteUserButton = (Button) findViewById(R.id.DeleteUserButton);
        DeleteUserButton.setTypeface(alltextTypeface);

        /**
         * the following deals with login buttons and actions
         */
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText password = (EditText) findViewById(R.id.userPassword);
                EditText username = (EditText) findViewById(R.id.userName);

                String pass = password.getText().toString();
                String user = username.getText().toString();

                /**
                 * login will return true if user is sucesffuly logged in
                 */
                if (DatabaseOperations.login(user, pass)) {
                    Toast.makeText(getApplicationContext(), "Successful login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        CreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Add the given user to the given database.
                 *
                 * @param username the desired username
                 * @param password the desired password
                 * @return true if the user is added to the database; otherwise false
                 */
                EditText password = (EditText) findViewById(R.id.userPassword);
                EditText username = (EditText) findViewById(R.id.userName);

                String pass = password.getText().toString();
                String user = username.getText().toString();

                if (DatabaseOperations.addUser(user, pass)) {
                    Toast.makeText(getApplicationContext(), "Account Created... \nLogging you in.", Toast.LENGTH_SHORT).show();
                    DatabaseOperations.login(user, pass);
                    Intent main = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(main);
                } else {
                    Toast.makeText(getApplicationContext(), "Account wasn't created.\nTry a different username.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DeleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Add the given user to the given database.
                 *
                 * @param username the desired username
                 * @param password the desired password
                 * @return true if the user is added to the database; otherwise false
                 */
                EditText password = (EditText) findViewById(R.id.userPassword);
                EditText username = (EditText) findViewById(R.id.userName);

                String pass = password.getText().toString();
                String user = username.getText().toString();

                if (DatabaseOperations.deleteUser(user, pass)) {
                    Toast.makeText(getApplicationContext(), "User '" + user + "' deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, please check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

