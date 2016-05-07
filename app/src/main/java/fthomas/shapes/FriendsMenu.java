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

    /**
     * Method called on the creation of the window
     * @param savedInstance - The savedInstance to pass into this method
     */
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

        createFriendsList();

        AddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DatabaseOperations.getRemoteLoginStatus()) {
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

                    builder.setNegativeButton("Search", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] tmp = DatabaseOperations.searchUser(friendName.getText().toString());
                            String names = "";

                            for (int i = 0; i < tmp.length; i++) {
                                names = names + tmp[i] + (i == tmp.length - 1 ? "" : ", ");
                            }

                            if (names.equals("-1")) {
                                Toast.makeText(getApplicationContext(), "Sorry, we couldn't find any users by that name.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Found users:\n" + names, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                    Button okButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    okButton.setBackgroundColor(Color.GREEN);
                    Button cancelButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    cancelButton.setBackgroundColor(Color.BLUE);
                }
            }
        });

    }

    /**
     * creates and populates the friends list to be displayed in the system
     */
    private void createFriendsList() {
        String username = DatabaseOperations.getLocalLoggedInUser();

        if (username == null || !DatabaseOperations.getRemoteLoginStatus()) {
            Toast.makeText(getApplicationContext(), "Check network connection and login again please.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            return;
        }

        ArrayList<String> friends = DatabaseOperations.getFriendsList(username);
        if (friends == null) {
            Toast.makeText(getApplicationContext(), "Sorry, you need to add some friends first!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioGroup buttons = (RadioGroup) findViewById(R.id.friendsList);

        for (String friend : friends) {
            long high_score = DatabaseOperations.getHighScore(friend);
            addButton(buttons, friend, high_score);
        }
    }

    /**
     * Adds a new radio button to the radio group
     * @param group the RadioGroup to add the new button to
     * @param name the name of the player
     * @param score the score of the player
     */
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

    /**
     * Switches the screen to the game menu to start playing
     * @param v the View that calls the methods
     */
    public void Play(View v) {
        if (DatabaseOperations.getRemoteLoginStatus()) {
            String friendName = getSelectedFriend();
            //System.out.println("Friend: " + friendName + ".");

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
        } else {
            Toast.makeText(getApplicationContext(), "Check network connection and login please...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            return;
        }
    }

    /**
     * get's the selected friend from the radiogroup,
     * @return The string name of the friend that is selected, or null if no friend is selected
     */
    public String getSelectedFriend() {
        RadioGroup r = (RadioGroup) findViewById(R.id.friendsList);
        RadioButton b = (RadioButton) findViewById(r.getCheckedRadioButtonId());


        if (b != null)
            return b.getText().toString().split("\t")[0];
        else
            return null;
    }
}
