package gamelogic;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Ben on 4/14/2016.
 */
public class GameThread extends Thread {

    private int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GameWindow gameWindow;
    private boolean running;
    public static Canvas canvas;

    public GameThread(SurfaceHolder surfaceHolder, GameWindow gameWindow)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameWindow = gameWindow;
    }

    /**
     * runs the game at a specified framerate
     * updates the game and draws it each frame
     * @see GameThread#FPS
     */
    @Override
    public void run()
    {
        //Keeps the FPS in check
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / FPS; // in milliseconds

        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            //try locking the canvas for pixel edition
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    //redraws the game each frame
                    this.gameWindow.update();
                    this.gameWindow.draw(canvas);
                }
            } catch(Exception e) {
                ;
            } finally {
                if(canvas != null) {
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                this.sleep(waitTime);
            } catch(Exception e) {
                ;
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;

                System.out.println(averageFPS);
            }
        }
    }

    public void setRunning(boolean b) { running = b; }
}
