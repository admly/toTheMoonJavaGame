package toTheMoon;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import toTheMoon.res.ResourcesManager;

import java.io.InputStream;

public  abstract class AbstractSpriteModel {
    protected ResourcesManager resourcesManager = new ResourcesManager();
    private static final double MOVEMENT_MULTIPLIER = 0.05;

    protected Image image;
    protected double positionX;
    protected double positionY;
    protected double velocityX;
    protected double velocityY;
    protected double width;
    protected double height;
    protected Image[] frames;
    protected double duration;

    protected AbstractSpriteModel()
    {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
    }
    protected Image getFrame(double time)
    {
        int index = (int)((time % (frames.length * duration)) / duration);

        return frames[index];
    }

    protected void setImage(Image i)
    {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
    }

    protected void setImage(InputStream filename)
    {
        Image i = new Image(filename);
        setImage(i);
    }



    protected void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    protected void setVelocity(double x, double y)
    {
        velocityX = x;
        velocityY = y;
    }

    protected void addVelocity(double x, double y)
    {
        velocityX += x;
        velocityY += y;
    }

    protected void update()
    {
        positionX += velocityX * MOVEMENT_MULTIPLIER;
        positionY += velocityY * MOVEMENT_MULTIPLIER;
    }

    //for animated sprites
    protected void render(double time, GraphicsContext gc)
    {
        gc.drawImage( this.getFrame(time), positionX, positionY );
    }

    protected void render(GraphicsContext gc)
    {
        gc.drawImage(image, positionX, positionY );
    }

    protected abstract Rectangle2D getBoundary();

    protected boolean intersects(AbstractSpriteModel s)
    {
        return s.getBoundary().intersects( this.getBoundary() );
    }

    public String toString()
    {
        return " Position: [" + positionX + "," + positionY + "]"
                + " Velocity: [" + velocityX + "," + velocityY + "]";
    }




}
