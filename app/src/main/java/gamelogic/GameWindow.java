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
    private final int XBlocks = 7; //blocks on x axis
    private final int YBlocks = 10; //blocks on y axis
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

        initBlocks();

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
            int x = (int)(event.getX() / blockWidth);
            int y = (int)(event.getY() / blockWidth);
            System.out.println("press up at " + x + "," + y);
            grid[x][y].rotate();

            return true; // VERY IMPORTANT
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {

            return true; // VERY IMPORTANT
        }

        return super.onTouchEvent(event);
    }

    public void update()
    {
        //update stuff
        //this is where we check for shapes being created, etc
        //TODO: change this so there's a list of empty blocks that it goes through
        for(int x = 1; x < XBlocks - 1; x++) {
            for(int y = 1; y < YBlocks - 1; y++) {
                if(grid[x][y].getType() == Block.BlockType.EMPTY) {
                    if((Math.random() * 100) < 5) {
                        int typeNum = ((int)(Math.random() * 100) % 4) + 1;
                        Bitmap image = blockImages.get(typeNum);
                        Block.BlockType type;
                        switch (typeNum) {
                            case 1:
                                type = Block.BlockType.WEDGE;
                                break;
                            case 2:
                                type = Block.BlockType.DIAGONAL;
                                break;
                            case 3:
                                type = Block.BlockType.CLEFT;
                                break;
                            case 4:
                                type = Block.BlockType.SQUARE;
                                break;
                            default:
                                type = Block.BlockType.WEDGE;
                        }
                        grid[x][y].changeType(type, image, ((int)(Math.random() * 100) % 4));
                        /*if(typeNum < 40) {
                            type = Block.BlockType.WEDGE;
                            image = blockImages.get(typeNum);
                        }
                        else if(typeNum < 70) {
                            type = Block.BlockType.DIAGONAL;
                            image = blockImages.get(typeNum);
                        }
                        else if(typeNum < 90) {
                            type = Block.BlockType.CLEFT;
                            image = blockImages.get(typeNum);
                        }
                        else if(typeNum < 100) {
                            type = Block.BlockType.SQUARE;
                            image = blockImages.get(typeNum);
                        }
                        else {
                            type = Block.BlockType.WEDGE;
                            image = blockImages.get(typeNum);
                        }*/
                    }
                } else {
                    //TODO: check for shapes that have been made here
                }
            }
        }


    }

    @Override
    public void draw(Canvas canvas)
    {
        if(canvas != null) {
            for(int x = 0; x < XBlocks; x++) {
                for(int y = 0; y < YBlocks; y++) {
                    grid[x][y].draw(canvas);
                }
            }
        }
    }

    private void initBlocks()
    {
        // init all blocks to empty
        for(int x = 0; x < XBlocks; x++) {
            for(int y = 0; y < YBlocks; y++) {
                grid[x][y] = new Block(Block.BlockType.EMPTY, blockImages.get(0), x * blockWidth, y * blockWidth);
            }
        }

        //set corners as not changeable
        grid[0][0].setActive(false);                    grid[0][0].setRemovable(false);
        grid[0][YBlocks-1].setActive(false);            grid[0][YBlocks-1].setRemovable(false);
        grid[XBlocks-1][0].setActive(false);            grid[XBlocks-1][0].setRemovable(false);
        grid[XBlocks-1][YBlocks-1].setActive(false);    grid[XBlocks-1][YBlocks-1].setRemovable(false);
        //init walls
        for(int x = 0, y = 1; y < YBlocks - 1; y++) { // left wall
            grid[x][y].changeType(Block.BlockType.WEDGE, blockImages.get(1), 3);
            grid[x][y].setActive(false);
            grid[x][y].setRemovable(false);
        }
        for(int x = XBlocks - 1, y = 1; y < YBlocks - 1; y++) { // right wall
            grid[x][y].changeType(Block.BlockType.WEDGE, blockImages.get(1), 1);
            grid[x][y].setActive(false);
            grid[x][y].setRemovable(false);
        }
        for(int x = 1, y = 0; x < XBlocks - 1; x++) { // top wall
            grid[x][y].changeType(Block.BlockType.WEDGE, blockImages.get(1), 0);
            grid[x][y].setActive(false);
            grid[x][y].setRemovable(false);
        }
        for(int x = 1, y = YBlocks - 1; x < XBlocks - 1; x++) { // bottom wall
            grid[x][y].changeType(Block.BlockType.WEDGE, blockImages.get(1), 2);
            grid[x][y].setActive(false);
            grid[x][y].setRemovable(false);
        }
    }
}
