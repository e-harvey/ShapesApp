package fthomas.shapes;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

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
        GameWindow gameWindow;

        //go fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // TODO check that the local user has a friend with a valid
        // blockseed to play with.
        setContentView(new GameWindow(this, type));
        //change to gameWindow, when created...

    }

    public static void setType(gamePlayType type) {
        PlayMenu.type = type;
    }

    public static void setFriendname(String friendname) {
        PlayMenu.friendname = friendname;
    }
}
