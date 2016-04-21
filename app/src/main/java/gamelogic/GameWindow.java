package gamelogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import fthomas.shapes.R;
import storage.shapes.DatabaseOperations;

/**
 * Created by Ben on 4/14/2016.
 */
public class GameWindow extends SurfaceView implements SurfaceHolder.Callback
{
    private GameThread gameThread;
    private HighScoreThread highScoreThread;
    private Block[][] grid;
    private final int XBlocks = 7; //blocks on x axis
    private final int YBlocks = 10; //blocks on y axis
    private int gridWidth;
    private int gridHeight;
    private int blockWidth;
    static public ArrayList<Bitmap> blockImages = new ArrayList<>();
    private long score = 0;
    private String friendName = null;
    private long friendScore;
    private String localUser;
    private int windowHeight;
    private long blockSeed;
    private long startTime;
    private long remainingTime;
    private String timeString;
    private Typeface textTypeface;
    private DrawMethod drawMethod;

    private class ShapeData {
        public boolean sides[];
        public int x;
        public int y;
        public ShapeData(boolean[] sides, int x, int y) {
            this.sides = new boolean[4];
            System.arraycopy(sides, 0, this.sides, 0, 4);
            this.x = x;
            this.y = y;
        }
    }

    public GameWindow(Context context, boolean playWithFriends)
    {
        super(context);
        grid = new Block[XBlocks][YBlocks];
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        gridWidth = metrics.widthPixels;
        windowHeight = metrics.heightPixels;
        gridHeight = (int)(metrics.widthPixels * ((float)YBlocks / XBlocks));
        blockWidth = gridWidth / XBlocks;
        startTime = System.nanoTime();
        remainingTime = 1000000000L * 90; // put number of seconds here //TODO: get game time from somewhere
        textTypeface = Typeface.createFromAsset(getContext().getAssets(), "ka1.ttf");
        localUser = DatabaseOperations.getLocalLoggedInUser();

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

        //TODO: start the blocks offset by half to allow for more blocks on screen at a time
        initBlocks();
        //TODO: add timer so the game ends

        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);

        // Spawn the correct highscore thread.
        highScoreThread = new HighScoreThread(this, playWithFriends);
        highScoreThread.setRunning(true);
        highScoreThread.start();

        if (playWithFriends) {
            blockSeed = rand_seeded();
            // TODO ensure that everyone playing gets this blockseed...
            DatabaseOperations.setBlockSeed(localUser, blockSeed);
        }

        setFocusable(true);

        setDrawMethod(playWithFriends);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        //needed it for class, ????
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
                highScoreThread.setRunning(false);
                highScoreThread.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
        DatabaseOperations.setHighScore(DatabaseOperations.getLocalLoggedInUser(), score);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        //we can safely start the game loop
        Thread.State state = gameThread.getState();
        if(state == Thread.State.NEW) {
            gameThread.setRunning(true);
            gameThread.start();
        } else if(state == Thread.State.TERMINATED) {
            try {
                gameThread.join();
            }  catch(InterruptedException e) {
                e.printStackTrace();
            }
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
        }
        System.out.println(state.toString());
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //get the block and rotate it
            int x = (int)(event.getX() / blockWidth);
            int y = (int)(event.getY() / blockWidth);
            if(y < YBlocks) { //stop overflow at bottom of screen
                System.out.println("press at " + x + "," + y);
                if(grid[x][y].isActive()) {
                    grid[x][y].rotate();
                    grid[x][y].setChanged(true);
                }

                return true; // VERY IMPORTANT
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //TODO: add tap-and-hold to change one block for point cost
            return true; // VERY IMPORTANT
        }

        return super.onTouchEvent(event);
    }

    public void update()
    {
        //TODO: change so it has a list of changed blocks, and/or empty blocks
        //this is where we check for shapes being created, etc
        for(int x = 1; x < XBlocks - 1; x++) {
            for(int y = 1; y < YBlocks - 1; y++) {
                if(grid[x][y].isChanged()) {
                    handle_shapes(x, y);
                    grid[x][y].setChanged(false);
                }
                grid[x][y].update();
                fill_empty_block(x, y);
            }
        }
        handle_time();
    }

    private void handle_time()
    {
        long curTime = System.nanoTime();
        long timeChange = curTime - startTime;
        startTime = curTime;
        remainingTime -= timeChange;
        if(remainingTime < 0) {
            remainingTime = 0;
        }
        Date date = new Date(remainingTime / 1000000);
        DateFormat formatter;
        if(remainingTime < 60000000000L) { // < minute
            formatter = new SimpleDateFormat("ss:SS");
        } else if(remainingTime < 600000000000L) { // < 10 minutes
            formatter = new SimpleDateFormat("m:ss:SS");
        } else {
            formatter = new SimpleDateFormat("mm:ss:SS");
        }
        timeString = formatter.format(date);
    }

    private void handle_shapes(int x, int y)
    {
        ArrayList<ShapeData> shapeBlocks = check_shape(x, y);
        //change every block in the shape to empty
        if(shapeBlocks != null) {
            //TODO: update score here
            score += shapeBlocks.size() * shapeBlocks.size();

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
    }

    private ArrayList<ShapeData> check_shape(int startX, int startY)
    {
        int[][] adjCoord = {{0, -1},{1, 0},{0, 1},{-1, 0}}; //adjacent grid offsets (top, right, bottom, left)
        ArrayList<ShapeData> activeBlocks = new ArrayList<>();
        HashMap<String,ShapeData> shape = new HashMap<>();
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
        return new ArrayList<>(shape.values());
    }

    public void fill_empty_block(int x, int y)
    {
        if(grid[x][y].getType() == Block.BlockType.EMPTY) {
            long typeNum = rand_seeded() % 100;
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

            grid[x][y].changeType(type, image, ((int) rand_seeded() % 4));
            grid[x][y].setChanged(true);
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
            drawMethod.execute(canvas);
        }
    }

    private long rand_seeded()
    {
        String tmp = String.valueOf(blockSeed);
        blockSeed = Math.abs(tmp.hashCode());
        return blockSeed;
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

    /**
     * A prototype for the database update method.
     */
    public interface DrawMethod {
        public void execute(Canvas canvas);
    }

    /**
     * Set the desired update method.  If we are playing with friends we must update
     * the gameWindows's friend score and friend name attributes; otherwise we just
     * update the user's highScore.
     *
     * @param playWithFriends true if the user is playing with friends; otherwise false.
     */
    private void setDrawMethod(boolean playWithFriends) {

        if (playWithFriends) {
            drawMethod = new DrawMethod() {
                public void execute(Canvas canvas) {
                float vertAlign = 0;

                //draw score
                Paint paint = new Paint();
                paint.setColor(0xFF000000);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(0, gridHeight - (blockWidth / 2), gridWidth, windowHeight, paint);

                //TODO: scale to screen resolution
                float textSize = ((windowHeight - gridHeight) / 4.0F);
                paint = new Paint();
                paint.setTypeface(textTypeface);
                paint.setTextSize(textSize);
                paint.setColor(0xFFFFFFFF);
                paint.setTextAlign(Paint.Align.RIGHT);
                int horizLocation = gridWidth - blockWidth;
                int vertLocation = gridHeight;
                canvas.drawText("SCORE:", horizLocation, vertLocation, paint);
                canvas.drawText("" + score, horizLocation, vertLocation + textSize + 10, paint);

                //draw timer
                //TODO: get remaining time
                textSize = ((windowHeight - gridHeight) / 3.0F);
                paint.setTextSize(textSize);
                horizLocation = blockWidth;
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Time:", horizLocation, vertLocation, paint);
                float timerSize = ((windowHeight - gridHeight) / 3.5F);
                paint.setTextSize(timerSize);
                canvas.drawText(timeString, horizLocation, vertLocation + textSize, paint);

                // Draw friend's score
                vertAlign += textSize + 30;
                textSize = (int) ((windowHeight - gridHeight) / 5.0F);
                vertAlign += textSize;
                paint = new Paint();
                paint.setTypeface(textTypeface);
                horizLocation = blockWidth;
                paint.setTextSize(textSize);
                paint.setColor(0xFFFFFFFF);
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(friendName + "'s SCORE:", horizLocation, vertLocation + vertAlign, paint);
                canvas.drawText(String.valueOf(friendScore), horizLocation, vertLocation + vertAlign + textSize + 10, paint);
                }
            };
        } else {
            drawMethod = new DrawMethod() {
                public void execute(Canvas canvas) {
			//draw score
			Paint paint = new Paint();
			paint.setColor(0xFF000000);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(0, gridHeight - (blockWidth / 2), gridWidth, windowHeight, paint);

			//TODO: scale to screen resolution
			float textSize = ((windowHeight - gridHeight) / 2.5F);
			paint = new Paint();
			paint.setTypeface(textTypeface);
			paint.setTextSize(textSize);
			paint.setColor(0xFFFFFFFF);
			paint.setTextAlign(Paint.Align.RIGHT);
			int horizLocation = gridWidth - blockWidth;
			int vertLocation = gridHeight;
			canvas.drawText("SCORE:", horizLocation, vertLocation, paint);
			canvas.drawText("" + score, horizLocation, vertLocation + textSize, paint);

			//draw timer
			//TODO: get remaining time
			textSize = ((windowHeight - gridHeight) / 3.0F);
			paint.setTextSize(textSize);
			horizLocation = blockWidth;
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText("Time:", horizLocation, vertLocation, paint);
			float timerSize = ((windowHeight - gridHeight) / 3.5F);
			paint.setTextSize(timerSize);
			canvas.drawText(timeString, horizLocation, vertLocation + textSize, paint);
                }
            };
        }
    }

    public long getScore() {
        return score;
    }

    public void setFriendsScore(String name, long score) {
        friendScore = score;
        friendName = name;
    }
}
