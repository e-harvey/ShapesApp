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
    private static boolean playWithFriends;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        GameWindow gameWindow;

        //go fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // TODO check that the local user has a friend with a valid
        // blockseed to play with.
        if (playWithFriends) {
            setContentView(new GameWindow(this, true));
        } else {
            setContentView(new GameWindow(this, false));
        }
        //change to gameWindow, when created...

    }

    public static void setPlayWithFriends(boolean playWithFriends) {
        PlayMenu.playWithFriends = playWithFriends;
    }
}
