package gamelogic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import fthomas.shapes.R;

/**
 * Created by Ben on 4/14/2016.
 */
public class Block
{
    public enum BlockType {EMPTY, WEDGE, DIAGONAL, CLEFT, SQUARE};
    private BlockType type;
    private Bitmap image;
    private int rotation;
    private int[] activeSides;
    private boolean removable;
    private boolean active;
    //private int color;
    private int x, y; //coordinates for drawing if needed

    public Block()
    {
        this(BlockType.EMPTY, 0, 0, true);
    }

    public Block(BlockType type, int x, int y)
    {
        this(type, x, y, true);
    }

    public Block(BlockType type, int x, int y, boolean removable)
    {
        this.type = type;
        //assign images, rotation, activeSides here
        switch (type) {
            case EMPTY:
                break;
            case WEDGE:
                System.out.println("got to wedge");
                image = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.wedge);
                break;
            case DIAGONAL:
                break;
            case CLEFT:
                break;
            case SQUARE:
                break;
            default:
                System.out.println("Block(): bad type given");
                break;
        }

        this.x = x;
        this.y = y;
        this.removable = removable;
    }

    public void rotate()
    {
        //rotate 90 degrees
        //rotate image
        //update activeSides
    }

    public void update()
    {
        //update stuff
        //rotations, animations, movement, etc
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
    }

    public boolean isRemovable() {
        return removable;
    }

    public boolean isActive() {
        return active;
    }
}
