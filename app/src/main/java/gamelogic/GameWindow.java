package gamelogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import fthomas.shapes.R;

/**
 * Created by Ben on 4/14/2016.
 */
public class GameWindow extends SurfaceView implements SurfaceHolder.Callback
{
    private GameThread gameThread;
    private Block[][] grid;
    private final int XBlocks = 8; //blocks on x axis
    private final int YBlocks = 10; //blocks on y axis

    public GameWindow(Context context)
    {
        super(context);
        grid = new Block[XBlocks][YBlocks];
        //TODO: fill array with new Blocks here
        //grid[0][0] = new Block(Block.BlockType.WEDGE, 20, 20);

        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);
        setFocusable(true);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        //needed it for class, ????
        ;
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // surface destroyed, stop game (update scores here)
        boolean retry = true;
        while(retry) {
            try {
                gameThread.setRunning(false);
                gameThread.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // set up background and blocks
        //bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grid_image));

        //we can safely start the game loop
        gameThread.setRunning(true);
        gameThread.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //get the block and rotate it
            System.out.println("press at " + event.getX() + "," + event.getY());
            return true; // VERY IMPORTANT
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //probably don't need anything here
            System.out.println("stopped pressing");
            return true; // VERY IMPORTANT
        }

        return super.onTouchEvent(event);
    }

    public void update()
    {
        //update stuff
        //this is where we check for shapes being created, etc
        //grid[0][0].update();
    }

    @Override
    public void draw(Canvas canvas)
    {
        //super.draw(canvas);
        //need to scale by the number of blocks on the screen
        final Bitmap bg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shapes_background_test),
                canvas.getWidth(), (int) (canvas.getWidth() * ((float)YBlocks / (float)XBlocks)), false);

        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.wedge);
        //b = Bitmap.createScaledBitmap(b, canvas.getWidth() / XBlocks, canvas.getHeight() / YBlocks, false);
        //b = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wedge), bg.getWidth() / 8, bg.getHeight() / 10, false);
        final Block testBlock = new Block(Block.BlockType.WEDGE,
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wedge),
                bg.getWidth() / XBlocks, bg.getHeight() / YBlocks, false), 0, 0);

        //final float scaleFactorX = canvas.getWidth() / (float)bg.getWidth();

        if(canvas != null) {
            final int savedState = canvas.save();
            //canvas.scale(scaleFactorX, scaleFactorX); //scale Y by same as x? keep square shape?

            canvas.drawBitmap(bg, 0, 0, null);
            //grid[0][0].draw(canvas);
            testBlock.draw(canvas);

            //canvas.restoreToCount(savedState); // return to original scaled state (won't keep scaling)
        }
    }
}
