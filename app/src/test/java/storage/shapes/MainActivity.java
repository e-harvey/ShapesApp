package storage.shapes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import java.util.ArrayList;


// testing only class.  not meant for real use.

public class MainActivity extends AppCompatActivity {
    Context context;
    Button btnAddUser, btnTest;
    TextView txtUser, txtPass;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtUser = (TextView) findViewById(R.id.txtUser);
        txtPass = (TextView) findViewById(R.id.txtPass);
        btnAddUser = (Button) findViewById(R.id.btnAddUser);
        btnTest = (Button) findViewById(R.id.btnTest);

        context = this;


        AddUser();
        TestUser();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void AddUser() {
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = "fritz";
                String password = "password";

                context.deleteDatabase("Master.db");
                LocalDatabaseOperations local = new LocalDatabaseOperations(context);
                local.addUser(context, username, password);
                username = "fritz1";
                password = "password1";
                local.addUser(context, username, password);
                local.addUser(context, "fritz2", "password2");

                boolean b = local.login(context, username, password);

                String loggedIn = local.getLocalLoginStatus();
                Toast.makeText(context, loggedIn, Toast.LENGTH_SHORT).show();


//                if (login == 1) {
//                    Toast.makeText(context, "Success!!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "FAIL", Toast.LENGTH_SHORT).show();
//
//                }

            }
        });

    }

    public void TestUser() {
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = "fritz";
                String password = "password";
                LocalDatabaseOperations local = new LocalDatabaseOperations(context);

                int score = 345;
//                local.addUser(context, username, password);
//                local.setHighScore(context,username, score);

                int returnedScore = local.getHighScore(context, username);
                Toast.makeText(context,Integer.toString(returnedScore),Toast.LENGTH_SHORT);
//                if (b) {
//                    Toast.makeText(context, "Success!!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "FAIL", Toast.LENGTH_SHORT).show();
//
//                }

//                local.logout(context, username);

            }
        });

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
