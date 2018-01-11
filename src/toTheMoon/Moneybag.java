package toTheMoon;

import javafx.geometry.Rectangle2D;

public class Moneybag extends AbstractSpriteModel {
    public Moneybag(){
        this.setImage(getClass().getResourceAsStream("res/moneybag.png"));
    }



    @Override
    protected Rectangle2D getBoundary() {
        return new Rectangle2D(positionX,positionY,width+15,height+15);
    }
}
