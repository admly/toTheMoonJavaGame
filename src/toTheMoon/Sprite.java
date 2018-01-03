package toTheMoon;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;


import java.io.InputStream;

public class Sprite {

    private static final double MOVEMENT_MULTIPLIER = 0.05;

    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    public Image[] frames;
    public double duration;

    public Sprite()
    {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
    }

    public Image getFrame(double time)
    {
        int index = (int)((time % (frames.length * duration)) / duration);

        return frames[index];
    }

    public void setImage(Image i)
    {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
    }

    public void setImage(InputStream filename)
    {
        Image i = new Image(filename);
        setImage(i);
    }

    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    public void setVelocity(double x, double y)
    {
        velocityX = x;
        velocityY = y;
    }

    public void addVelocity(double x, double y)
    {
        velocityX += x;
        velocityY += y;
    }

    public void update()
    {
        positionX += velocityX * MOVEMENT_MULTIPLIER;
        positionY += velocityY * MOVEMENT_MULTIPLIER;
    }

    //for animated sprites
    public void render(double time, GraphicsContext gc)
    {
        gc.drawImage( this.getFrame(time), positionX, positionY );
    }

    public void render(GraphicsContext gc)
    {
        gc.drawImage(image, positionX, positionY );
    }

    public Rectangle2D getBoundary()
    {
        return new Rectangle2D(positionX,positionY,width,height);
    }

    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects( this.getBoundary() );
    }

    public String toString()
    {
        return " Position: [" + positionX + "," + positionY + "]"
                + " Velocity: [" + velocityX + "," + velocityY + "]";
    }


}