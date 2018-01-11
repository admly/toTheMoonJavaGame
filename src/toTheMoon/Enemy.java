package toTheMoon;

import javafx.geometry.Rectangle2D;

public class Enemy extends AbstractSpriteModel {
    Enemy(){
        super();
        this.setPosition(0+(450 * Math.random() + 20), 0  * Math.random() + 20);
        this.frames = resourcesManager.getEnemyImg();
        this.duration = 0.001;
    }


    @Override
    protected Rectangle2D getBoundary() {
        return new Rectangle2D(positionX,positionY,width+25,height+25);
    }
}
