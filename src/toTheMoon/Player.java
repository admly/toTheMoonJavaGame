package toTheMoon;

import javafx.geometry.Rectangle2D;

public class Player extends AbstractSpriteModel {

    public Player(){
        super();
        this.setPosition(450 * Math.random() + 20, 450  * Math.random() + 20);
        this.frames = resourcesManager.getShipImg();
        this.duration = 0.001;
    }

    @Override
    protected Rectangle2D getBoundary() {
        return new Rectangle2D(positionX,positionY,width+25, height+25);
    }
}
