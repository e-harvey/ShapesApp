package gamelogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
    static public ArrayList<Bitmap> blockImages = new ArrayList<Bitmap>();

    private class ShapeData {
        public boolean active = false;
        public boolean sides[];
        public int x;
        public int y;
        public ShapeData(boolean[] sides, int x, int y) {
            this.sides = new boolean[4];
            System.arraycopy(sides, 0, this.sides, 0, 4);;
            this.x = x;
            this.y = y;
        }
    }

    public GameWindow(Context context)
    {
        super(context);
        grid = new Block[XBlocks][YBlocks];
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        gridWidth = metrics.widthPixels;
        gridHeight = (int)(metrics.widthPixels * ((float)YBlocks / XBlocks));
        blockWidth = gridWidth / XBlocks;

        // initialize bitmaps
        // 0 empty, 1 wedge, 2 diagonal, 3 cleft, 4 square
        // 5 wedge_green, 6 diagonal_green, 7 cleft_green, 8 square_green
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.empty), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wedge), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.diagonal), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cleft), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.square), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wedge_green), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.diagonal_green), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cleft_green), blockWidth, blockWidth, false));
        blockImages.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.square_green), blockWidth, blockWidth, false));



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
            System.out.println("press at " + x + "," + y);
            if(grid[x][y].getType() == Block.BlockType.EMPTY) {
                fill_empty_block(x, y);
            } else {
                grid[x][y].rotate();
            }
            grid[x][y].setChanged(true);

            return true; // VERY IMPORTANT
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {

            return true; // VERY IMPORTANT
        }

        return super.onTouchEvent(event);
    }

    public void update()
    {
        //TODO: change so it has a list of changed blocks
        //this is where we check for shapes being created, etc
        for(int x = 0; x < XBlocks; x++) {
            for(int y = 0; y < YBlocks; y++) {
                if(grid[x][y].isChanged()) {
                    ArrayList<ShapeData> shapeBlocks = check_shape(x, y);
                    //change every block in the shape to empty
                    if(shapeBlocks != null) {
                        //TODO: update score here

                        //Change shape blocks to green to show shape has been made
                        for(ShapeData block : shapeBlocks) {
                            Block tmpBlock = grid[block.x][block.y];
                            switch (tmpBlock.getType()) {
                                case WEDGE:
                                    tmpBlock.changeType(Block.BlockType.WEDGE, blockImages.get(5), tmpBlock.getRotation());
                                    break;
                                case DIAGONAL:
                                    tmpBlock.changeType(Block.BlockType.DIAGONAL, blockImages.get(6), tmpBlock.getRotation());
                                    break;
                                case CLEFT:
                                    tmpBlock.changeType(Block.BlockType.CLEFT, blockImages.get(7), tmpBlock.getRotation());
                                    break;
                                case SQUARE:
                                    tmpBlock.changeType(Block.BlockType.SQUARE, blockImages.get(8), tmpBlock.getRotation());
                                    break;
                            }

                            tmpBlock.setActive(false);
                            tmpBlock.setPartOfShape(true);
                        }
                    }
                    grid[x][y].setChanged(false);
                }
                grid[x][y].update();
                fill_empty_block(x, y);
            }
        }

        //fill_empty_block();
    }

    public ArrayList<ShapeData> check_shape(int startX, int startY)
    {
        int[][] adjCoord = {{0, -1},{1, 0},{0, 1},{-1, 0}}; //adjacent grid offsets (top, right, bottom, left)
        ArrayList<ShapeData> activeBlocks = new ArrayList<ShapeData>();
        HashMap<String,ShapeData> shape = new HashMap<String, ShapeData>();
        // init starting block
        ShapeData start = new ShapeData(grid[startX][startY].getActiveSides(), startX, startY);
        activeBlocks.add(start);
        shape.put(start.x + "," + start.y, start);

        while(activeBlocks.size() > 0) {
            ShapeData block = activeBlocks.remove(0);
            for(int side = 0; side < 4; side++) {
                if(block.sides[side]) { //follow active sides
                    int chX = block.x + adjCoord[side][0];
                    int chY = block.y + adjCoord[side][1];
                    if ((chX >= 0 && chX < XBlocks) && (chY >= 0 && chY < YBlocks)) { //on grid
                        ShapeData newBlock = new ShapeData(grid[chX][chY].getActiveSides(), chX, chY);
                        if (newBlock.sides[(side + 2) % 4]) {
                            // is adjacent side on new block connected?
                            if(!(shape.containsKey(chX + "," + chY))) { //it's new
                                newBlock.sides[(side + 2) % 4] = false;
                                activeBlocks.add(newBlock); // add to active list
                                shape.put(newBlock.x + "," + newBlock.y, newBlock);
                            }
                        } else {
                            // not connected, NO SHAPE
                            return null;
                        }
                    }
                }
            }
        }

        //Got a shape!
        ArrayList<ShapeData> shapeBlocks = new ArrayList<ShapeData>(shape.values());
        return shapeBlocks;
    }

    public void fill_empty_block(int x, int y)
    {
        //TODO: change this so there's a list of empty blocks that it goes through
        //for(int x = 1; x < XBlocks - 1; x++) {
        //    for(int y = 1; y < YBlocks - 1; y++) {
        if(grid[x][y].getType() == Block.BlockType.EMPTY) {
                    int typeNum = (int)(Math.random() * 100);
                    Bitmap image;
                    Block.BlockType type;
                    //TODO: adjust probabilities as needed
                    if(typeNum < 50) {
                        type = Block.BlockType.WEDGE;
                        image = blockImages.get(1);
                    }
                    else if(typeNum < 80) {
                        type = Block.BlockType.DIAGONAL;
                        image = blockImages.get(2);
                    }
                    else if(typeNum < 92) {
                        type = Block.BlockType.CLEFT;
                        image = blockImages.get(3);
                    }
                    else if(typeNum < 100) {
                        type = Block.BlockType.SQUARE;
                        image = blockImages.get(4);
                    }
                    else {
                        type = Block.BlockType.WEDGE;
                        image = blockImages.get(1);
                    }

                    grid[x][y].changeType(type, image, ((int)(Math.random() * 100) % 4));
                    grid[x][y].setChanged(true);
                }
        //    }
        //}
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
            grid[x][y].changeType(Block.BlockType.WEDGE, blockImages.get(5), 3);
            grid[x][y].setActive(false);
            grid[x][y].setRemovable(false);
        }
        for(int x = XBlocks - 1, y = 1; y < YBlocks - 1; y++) { // right wall
            grid[x][y].changeType(Block.BlockType.WEDGE, blockImages.get(5), 1);
            grid[x][y].setActive(false);
            grid[x][y].setRemovable(false);
        }
        for(int x = 1, y = 0; x < XBlocks - 1; x++) { // top wall
            grid[x][y].changeType(Block.BlockType.WEDGE, blockImages.get(5), 0);
            grid[x][y].setActive(false);
            grid[x][y].setRemovable(false);
        }
        for(int x = 1, y = YBlocks - 1; x < XBlocks - 1; x++) { // bottom wall
            grid[x][y].changeType(Block.BlockType.WEDGE, blockImages.get(5), 2);
            grid[x][y].setActive(false);
            grid[x][y].setRemovable(false);
        }
    }
}
