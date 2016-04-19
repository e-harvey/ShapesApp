package fthomas.shapes;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import gamelogic.GameWindow;

/**
 * Created by FThom_000 on 3/19/2016.
 */
public class PlayMenu extends Activity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        //go fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //change to gameWindow, when created...
        setContentView(new GameWindow(this));
    }
}
