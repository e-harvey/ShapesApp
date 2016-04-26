package fthomas.shapes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import storage.shapes.DatabaseOperations;

/**
 * Created by FThom_000 on 3/19/2016.
 */
public class FriendsMenu extends AppCompatActivity {

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_friends);
        Typeface alltextTypeface = Typeface.createFromAsset(getAssets(), "Beeb Mode One.ttf");

        TextView FriendsTitle = (TextView)findViewById(R.id.FriendsTitle);
        FriendsTitle.setTypeface(alltextTypeface);

        Button AddFriends = (Button)findViewById(R.id.Add_Friend);
        AddFriends.setTypeface(alltextTypeface);

        TextView PlayFriends = (TextView)findViewById(R.id.Play_Friends);
        PlayFriends.setTypeface(alltextTypeface);

        if (!createFriendsList()) {

        }

        AddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsMenu.this);
                builder.setTitle("Friend Name");

                final EditText friendName = new EditText(FriendsMenu.this);
                friendName.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(friendName);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DatabaseOperations.addNewFriend(DatabaseOperations.getLocalLoggedInUser(),
                                                            friendName.getText().toString())) {
                            RadioGroup buttons = (RadioGroup) findViewById(R.id.friendsList);
                            addButton(buttons, friendName.getText().toString(),
                                    DatabaseOperations.getHighScore(friendName.getText().toString()));
                        } else {
                            Toast.makeText(getApplicationContext(), "Sorry, we can't find that friend.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                Button okButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                okButton.setBackgroundColor(Color.GREEN);
                Button cancelButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                cancelButton.setBackgroundColor(Color.RED);
            }
        });

    }

    private Boolean createFriendsList() {
        String username = DatabaseOperations.getLocalLoggedInUser();

        if (username == null) {
            Toast.makeText(getApplicationContext(), "Hey... you need to login first.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            return false;
        }

        ArrayList<String> friends = DatabaseOperations.getFriendsList(username);
        if (friends == null) {
            Toast.makeText(getApplicationContext(), "Sorry, you need to add some friends first!", Toast.LENGTH_SHORT).show();
            return false;
        }

        RadioGroup buttons = (RadioGroup) findViewById(R.id.friendsList);

        for (String friend : friends) {
            long high_score = DatabaseOperations.getHighScore(friend);
            addButton(buttons, friend, high_score);
        }

        return true;
    }
    public void Acknowledge(View v) {
        Toast t = Toast.makeText(getApplicationContext(), "Still working on this button...", Toast.LENGTH_SHORT);
        t.show();
    }

    private void addButton(RadioGroup group, String name, long score) {
        RadioButton b = new RadioButton(this);
        b.setTextColor(Color.BLACK);
        //b.setTextSize(getResources().getDisplayMetrics().);
        b.setText(String.format("%s\t%20d", name, score));
        ViewGroup.LayoutParams params = group.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        b.setLayoutParams(params);
        b.setGravity(Gravity.CENTER);
        b.setBackgroundColor(Color.WHITE);
        group.addView(b);
    }
    public void Play(View v) {
        String friendName = getSelectedFriend();
        System.out.println("Friend: " + friendName + ".");

        if (friendName == null || DatabaseOperations.getBlockSeed(friendName) == -1) {
            Toast.makeText(getApplicationContext(), "Sorry, please select a friend.\nPlease make sure that'" + friendName +
                    "'has played some games first.", Toast.LENGTH_LONG).show();
            PlayMenu.setType(PlayMenu.gamePlayType.SINGLE_PLAYER);
        } else {
            PlayMenu.setType(PlayMenu.gamePlayType.PLAY_WITH_FRIENDS);
            PlayMenu.setFriendname(friendName);
        }
        Intent intent = new Intent(this, PlayMenu.class);
        startActivity(intent);
    }

    public String getSelectedFriend() {
        RadioGroup r = (RadioGroup) findViewById(R.id.friendsList);
        RadioButton b = (RadioButton) findViewById(r.getCheckedRadioButtonId());


        if (b != null)
            return b.getText().toString().split("\t")[0];
        else
            return null;
    }
}
