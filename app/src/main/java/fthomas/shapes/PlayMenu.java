package fthomas.shapes;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import gamelogic.GameWindow;
import storage.shapes.DatabaseOperations;

/**
 * Created by FThom_000 on 3/19/2016.
 */
public class PlayMenu extends Activity {
    public enum gamePlayType {PLAY_WITH_FRIENDS, SINGLE_PLAYER, DAILY_CHALLENGE};
    public static gamePlayType type;
    public static String friendname;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        //go fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (type == gamePlayType.PLAY_WITH_FRIENDS && DatabaseOperations.getBlockSeed(friendname) != -1) {
            setContentView(new GameWindow(this, type));
            Toast.makeText(getApplicationContext(), "Sorry, '" + friendname + "' needs to play some games first.", Toast.LENGTH_SHORT).show();
        } else {
            setContentView(new GameWindow(this, gamePlayType.SINGLE_PLAYER));
        }
    }

    public static void setType(gamePlayType type) {
        PlayMenu.type = type;
    }

    public static void setFriendname(String friendname) {
        PlayMenu.friendname = friendname;
    }
}
