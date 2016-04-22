package gamelogic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import fthomas.shapes.R;

/**
 * Created by Ben on 4/14/2016.
 */
public class Block
{
    final private float SHAPE_SECS = 0.7F;
    public enum BlockType {EMPTY, WEDGE, DIAGONAL, CLEFT, SQUARE};
    private BlockType type;
    private Bitmap image = null;
    private int rotation;
    private boolean[] activeSides; // in order top, right, down, left
    private boolean removable;
    private boolean active;
    private boolean changed;
    private boolean partOfShape;
    private long shapeTime;
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

    /**
     * constructor, initializes the blocks type, image, coordinates, and whether it's removable
     * @param type he block's type
     * @param image the bitmap used to display the block
     * @param x the block's x coordinate
     * @param y the block's y coordinate
     * @param removable is the block changeable
     */
    public Block(BlockType type, Bitmap image, int x, int y, boolean removable)
    {
        this.type = type;
        this.image = image;
        this.rotation = 0;
        this.activeSides = new boolean[4];
        this.removable = removable;
        this.active = true;
        this.changed = false;
        partOfShape = false;
        this.x = x;
        this.y = y;
        rotate90.postRotate(90);

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
    }

    /**
     * rotates the block 90 degrees clockwise
     */
    public void rotate()
    {
        if(active && removable) {
            //rotate bitmap
            image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), rotate90, true);
            rotation = (rotation + 1) % 4;
            //update active sides
            boolean tmp[] = new boolean[4];
            System.arraycopy(activeSides, 0, tmp, 0, 4);
            for(int i = 0; i < 4; i++) {
                activeSides[(i + 1) % 4] = tmp[i];
            }
        }
    }

    /**
     * updates the block with any needed changes
     * handles what happens when the block is part of a shape
     */
    public void update()
    {
        //update stuff
        //rotations, animations, movement, etc

        //Shape updating
        if(partOfShape) {
            long time = System.nanoTime() - shapeTime;
            if((float)time / 1000000000 > SHAPE_SECS) {
                partOfShape = false;
                active = true;
                this.changeType(BlockType.EMPTY, GameWindow.blockImages.get(0), 0);
            }
        }
    }

    /**
     * draws the block on the specified canvas
     * @param canvas the canvas being drawn on
     */
    public void draw(Canvas canvas)
    {
        if(image != null) {
            canvas.drawBitmap(image, x, y, null);
        }
    }

    /**
     * changes the block to a new type, with image and rotation
     * @param type the new block type
     * @param image the new bitmap to represent this block
     * @param rotation the rotation to start this new block with
     */
    public void changeType(BlockType type, Bitmap image, int rotation) {
        if(!removable || !active) {
            return;
        }
        this.type = type;
        this.image = image;
        this.rotation = 0;

        //set correct active sides
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
        //rotate bitmap
        for(; rotation > 0; rotation--) {
            this.rotate();
        }
    }

    public BlockType getType() {
        return type;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getRotation() {
        return rotation;
    }

    public boolean[] getActiveSides() {
        return activeSides;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    public boolean isPartOfShape() {
        return partOfShape;
    }

    public void setPartOfShape(boolean partOfShape) {
        this.partOfShape = partOfShape;
        this.shapeTime = System.nanoTime();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
