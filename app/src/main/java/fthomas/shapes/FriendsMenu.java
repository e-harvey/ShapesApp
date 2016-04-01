package fthomas.shapes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by FThom_000 on 3/19/2016.
 */
public class FriendsMenu extends AppCompatActivity {
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_friends);
    }

    public void Acknowledge(View v) {
        Toast t = Toast.makeText(getApplicationContext(), "Still working on this button...", Toast.LENGTH_SHORT);
        t.show();
    }

    public void Play(View v) {
        Intent intent = new Intent(this, PlayMenu.class);
        startActivity(intent);
    }
}
