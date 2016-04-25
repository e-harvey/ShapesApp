package fthomas.shapes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

        TextView AddFriends = (TextView)findViewById(R.id.Add_Friend);
        AddFriends.setTypeface(alltextTypeface);

        TextView PlayFriends = (TextView)findViewById(R.id.Play_Friends);
        PlayFriends.setTypeface(alltextTypeface);

        if (!createFriendsList()) {
            //@todo display no connection :(
        }

    }

    private Boolean createFriendsList() {
        String username = DatabaseOperations.getLocalLoggedInUser();
        if (username == null)
            return false;
        /*ArrayList<String> friends = DatabaseOperations.getFriendsList(username);
        if (friends == null)
            return false;*/
        RadioGroup buttons = (RadioGroup) findViewById(R.id.friendsList);
        addButton(buttons, "Steve", 1200);
        addButton(buttons, "Tom", 2134);
        addButton(buttons, "Bob", 1337);
        /*
        for (String friend : friends) {
            long high_score = DatabaseOperations.getHighScore(friend);
            addButton(buttons, friend, high_score);
        }*/
        return true;
    }
    public void Acknowledge(View v) {
        Toast t = Toast.makeText(getApplicationContext(), "Still working on this button...", Toast.LENGTH_SHORT);
        t.show();
    }

    private void addButton(RadioGroup group, String name, long score) {
        RadioButton b = new RadioButton(this);
        b.setTextColor(getResources().getColor(R.color.Text));
        b.setText(String.format("%s\t%20d", name, score));
        ViewGroup.LayoutParams params = group.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        b.setLayoutParams(params);
        b.setGravity(Gravity.CENTER);
        b.setBackgroundTintList(getResources().getColorStateList(R.color.Text));
        group.addView(b);
    }
    public void Play(View v) {
        String friendName = getSelectedFriend();
        if (friendName == null) {
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
        return b.getText().toString();
    }
}
