package fthomas.shapes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by FThom_000 on 3/19/2016.
 */
public class FriendsMenu extends AppCompatActivity {
    private String friendName; //TODO: set this

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
    }

    public void Acknowledge(View v) {
        Toast t = Toast.makeText(getApplicationContext(), "Still working on this button...", Toast.LENGTH_SHORT);
        t.show();
    }

    public void Play(View v) {
        PlayMenu.setType(PlayMenu.gamePlayType.PLAY_WITH_FRIENDS);
        PlayMenu.setFriendname(friendName);
        Intent intent = new Intent(this, PlayMenu.class);
        startActivity(intent);
    }
}
