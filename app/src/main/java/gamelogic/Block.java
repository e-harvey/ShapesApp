package gamelogic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import fthomas.shapes.R;

/**
 * Created by Ben on 4/14/2016.
 */
public class Block
{
    public enum BlockType {EMPTY, WEDGE, DIAGONAL, CLEFT, SQUARE};
    private BlockType type;
    private Bitmap image = null;
    private int rotation;
    private boolean[] activeSides; // in order top, right, down, left
    private boolean removable;
    private boolean active;
    private boolean changed;
    //private int color;
    private int x, y; //coordinates for drawing if needed
    final Matrix rotate90 = new Matrix();


    public Block() {
        this(BlockType.EMPTY, null, 0, 0, true);
    }

    public Block(Bitmap image)
    {
        this(BlockType.EMPTY, image, 0, 0, true);
    }

    public Block(BlockType type, int x, int y)
    {
        this(type, null, x, y, true);
    }

    public Block(BlockType type, Bitmap image, int x, int y)
    {
        this(type, image, x, y, true);
    }

    public Block(BlockType type, Bitmap image, int x, int y, boolean removable)
    {
        this.type = type;
        this.image = image;
        this.rotation = 0;
        this.activeSides = new boolean[4];
        this.removable = removable;
        this.active = true;
        this.x = x;
        this.y = y;

        //assign images, rotation, activeSides here
        switch (type) {
            case EMPTY:
                activeSides = new boolean[]{false, false, false, false};
                break;
            case WEDGE:
                activeSides = new boolean[]{false, false, true, false};
                break;
            case DIAGONAL:
                activeSides = new boolean[]{false, true, true, false};
                break;
            case CLEFT:
                activeSides = new boolean[]{true, true, false, true};
                break;
            case SQUARE:
                activeSides = new boolean[]{true, true, true, true};
                break;
            default:
                System.out.println("Block(): bad type given");
                break;
        }

        rotate90.postRotate(90);
        //this.changed = true;
    }

    public void rotate()
    {
        //TODO:
        //rotate 90 degrees
        //rotate image
        //update activeSides
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), rotate90, true);
    }

    public void update()
    {
        //update stuff
        //rotations, animations, movement, etc
    }

    public void draw(Canvas canvas)
    {
        if(image != null) {
            canvas.drawBitmap(image, x, y, null);
        }
    }

    public void changeType(BlockType type, Bitmap image, int rotation) {
        this.type = type;
        this.image = image;
        this.rotation = rotation;

        //TODO: set correct active sides, rotate bitmap, etc
        switch (type) {
            case EMPTY:
                activeSides = new boolean[]{false, false, false, false};
                break;
            case WEDGE:
                activeSides = new boolean[]{false, false, true, false};
                break;
            case DIAGONAL:
                activeSides = new boolean[]{false, true, true, false};
                break;
            case CLEFT:
                activeSides = new boolean[]{true, true, false, true};
                break;
            case SQUARE:
                activeSides = new boolean[]{true, true, true, true};
                break;
        }
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isRemovable() {
        return removable;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
