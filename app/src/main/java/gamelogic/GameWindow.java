package gamelogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import fthomas.shapes.R;

/**
 * Created by Ben on 4/14/2016.
 */
public class GameWindow extends SurfaceView implements SurfaceHolder.Callback
{
    private GameThread gameThread;
    private Block[][] grid;
    private final int XBlocks = 6; //blocks on x axis
    private final int YBlocks = 8; //blocks on y axis
    private int gridWidth;
    private int gridHeight;
    private int blockWidth;
    private ArrayList<Bitmap> blockImages = new ArrayList<Bitmap>();

    public GameWindow(Context context)
    {
        super(context);
        grid = new Block[XBlocks][YBlocks];
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        gridWidth = metrics.widthPixels;
        gridHeight = (int)(metrics.widthPixels * ((float)YBlocks / XBlocks));
        blockWidth = gridWidth / XBlocks;

        // 0 empty, 1 wedge, 2 diagonal, 3 cleft, 4 square
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.empty), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wedge), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.diagonal), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cleft), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.square), blockWidth, blockWidth, false));

        //init blocks at the right coordinates
        //TODO: remove random image thing
        for(int x = 0; x < XBlocks; x++) {
            for(int y = 0; y < YBlocks; y++) {
                grid[x][y] = new Block(Block.BlockType.EMPTY, blockImages.get((int)(Math.random() * 100) % 5), x * blockWidth, y * blockWidth);
            }
        }

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

            return true; // VERY IMPORTANT
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //get the block and rotate it
            int x = (int)(event.getX() / blockWidth);
            int y = (int)(event.getY() / blockWidth);
            System.out.println("press up at " + x + "," + y);
            grid[x][y].rotate();
            //grid[x][y].setChanged(true);

            //probably don't need anything here
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
        if(canvas != null) {
            for(int x = 0; x < XBlocks; x++) {
                for(int y = 0; y < YBlocks; y++) {
                    //if(grid[x][y].isChanged()) {
                        grid[x][y].draw(canvas);
                        //grid[x][y].setChanged(false);
                    //}
                }
            }
        }
    }
}
